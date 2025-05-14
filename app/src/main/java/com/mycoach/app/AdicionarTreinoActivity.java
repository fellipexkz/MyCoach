package com.mycoach.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

public class AdicionarTreinoActivity extends AppCompatActivity {

    private TextInputEditText treinoNomeInput;
    private TextInputEditText treinoObservacaoInput;
    private MaterialAutoCompleteTextView treinoDiaSemanaInput;
    private RecyclerView exerciciosRecyclerView;
    private ExercicioFormAdapter exercicioAdapter;
    private BancoDeDadosHelper bancoDeDadosHelper;
    private DataFirebase dbfire = new DataFirebase();

    private static final String[] DIAS_SEMANA = {
            "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira",
            "Sexta-feira", "Sábado", "Domingo"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_treino);

        bancoDeDadosHelper = new BancoDeDadosHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        treinoNomeInput = findViewById(R.id.treinoNomeInput);
        treinoObservacaoInput = findViewById(R.id.treinoObservacaoInput);
        treinoDiaSemanaInput = findViewById(R.id.treinoDiaSemanaInput);
        exerciciosRecyclerView = findViewById(R.id.exerciciosRecyclerView);

        if (treinoNomeInput == null || treinoObservacaoInput == null || treinoDiaSemanaInput == null ||
                exerciciosRecyclerView == null) {
            Toast.makeText(this, "Erro: Componentes do layout não encontrados", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ArrayAdapter<String> diasSemanaAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                DIAS_SEMANA
        );
        treinoDiaSemanaInput.setAdapter(diasSemanaAdapter);

        exerciciosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exercicioAdapter = new ExercicioFormAdapter();
        exercicioAdapter.setRecyclerView(exerciciosRecyclerView);
        exerciciosRecyclerView.setAdapter(exercicioAdapter);

        Exercicio exercicioInicial = new Exercicio();
        exercicioInicial.setNome("");
        exercicioInicial.setTempoDescanso("");
        for (int i = 0; i < 3; i++) {
            Serie serie = new Serie();
            serie.setCarga("");
            serie.setRepeticoes("");
            exercicioInicial.getSeries().add(serie);
        }
        exercicioAdapter.addExercicio(exercicioInicial);

        exerciciosRecyclerView.post(() -> {
            exerciciosRecyclerView.requestLayout();
            View parent = (View) exerciciosRecyclerView.getParent();
            if (parent != null) {
                parent.requestLayout();
            }
        });

        int alunoId = getIntent().getIntExtra("aluno_id", -1);
        if (alunoId == -1) {
            Toast.makeText(this, "Erro: ID do aluno não fornecido", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            salvarTreino();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void salvarTreino() {
        Treino treino = new Treino();
        String nome = treinoNomeInput.getText().toString().trim();
        String observacao = treinoObservacaoInput.getText().toString().trim();
        String diaSemana = treinoDiaSemanaInput.getText().toString().trim();

        if (nome.isEmpty()) {
            treinoNomeInput.setError("O título é obrigatório");
            return;
        }

        if (diaSemana.isEmpty()) {
            Toast.makeText(this, "Selecione um dia da semana", Toast.LENGTH_SHORT).show();
            return;
        }

        int alunoId = getIntent().getIntExtra("aluno_id", -1);
        if (alunoId == -1) {
            Toast.makeText(this, "Erro: ID do aluno não fornecido", Toast.LENGTH_SHORT).show();
            return;
        }

        long treinoId = bancoDeDadosHelper.adicionarTreino(alunoId, nome, observacao.isEmpty() ? null : observacao, diaSemana);
        if (treinoId == -1) {
            Toast.makeText(this, "Erro ao adicionar treino", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Treino> treinos = bancoDeDadosHelper.obterTreinosPorAlunoId(alunoId);
        if (treinos.isEmpty() || treinos.get(treinos.size() - 1).getId() != treinoId) {
            Toast.makeText(this, "Erro ao recuperar ID do treino", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("AdicionarTreinoActivity", "Treino salvo - ID: " + treinoId + ", Nome: " + nome);
        treino.setId((int)treinoId);

        for (Exercicio exercicio : exercicioAdapter.getExercicios()) {
            String exercicioNome = exercicio.getNome().trim();
            String tempoDescanso = exercicio.getTempoDescanso().trim();

            if (exercicioNome.isEmpty() || tempoDescanso.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos dos exercícios", Toast.LENGTH_SHORT).show();
                return;
            }

            long exercicioId = bancoDeDadosHelper.adicionarExercicio((int) treinoId, exercicioNome, tempoDescanso);
            if (exercicioId == -1) {
                Toast.makeText(this, "Erro ao adicionar exercício", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("AdicionarTreinoActivity", "Exercício salvo - ID: " + exercicioId + ", Nome: " + exercicioNome + ", Tempo Descanso: " + tempoDescanso);
            exercicio.setId((int) exercicioId);
            exercicio.setTreinoId((int) treinoId);
            exercicio.setNome(exercicioNome);
            exercicio.setTempoDescanso(tempoDescanso);
            dbfire.sendFirebaseExercise(exercicio, "exercicios", bancoDeDadosHelper);

            for (Serie serie : exercicio.getSeries()) {
                String carga = serie.getCarga().trim();
                String repeticoes = serie.getRepeticoes().trim();

                if (carga.isEmpty() || repeticoes.isEmpty()) {
                    Toast.makeText(this, "Preencha todos os campos das séries", Toast.LENGTH_SHORT).show();
                    return;
                }

                int repeticoesInt;
                try {
                    repeticoesInt = Integer.parseInt(repeticoes);
                    if (repeticoesInt <= 0) {
                        Toast.makeText(this, "O número de repetições deve ser maior que 0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "O número de repetições deve ser um valor numérico válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                long serieId = bancoDeDadosHelper.adicionarSerie((int) exercicioId, carga, repeticoesInt);
                if (serieId == -1) {
                    Toast.makeText(this, "Erro ao adicionar série", Toast.LENGTH_SHORT).show();
                    return;
                }
                serie.setCarga(carga);
                serie.setExercicioId((int) exercicioId);
                serie.setRepeticoes(repeticoes.trim());

                dbfire.sendFirebaseSerie(serie, "series", bancoDeDadosHelper);
                dbfire.syncWithFirebaseSerie(bancoDeDadosHelper, "series");

                Log.d("AdicionarTreinoActivity", "Série salva - ID: " + serieId + ", Carga: " + carga + ", Repetições: " + repeticoes);
            }
        }

        Toast.makeText(this, "Treino adicionado com sucesso", Toast.LENGTH_SHORT).show();

        // Enviar para o Firebase
        // Treino
        treino.setAlunoId(alunoId);
        treino.setObservacao(observacao.isEmpty() ? null : observacao);
        treino.setDiaSemana(diaSemana);
        treino.setNome(nome);
        dbfire.sendFirebaseTreino(treino,"treinos",bancoDeDadosHelper);
        dbfire.syncWithFirebaseTreino(bancoDeDadosHelper, "treinos");
        dbfire.syncWithFirebaseExercise(bancoDeDadosHelper, "exercicios");




        Intent intent = new Intent(this, DetalhesTreinoActivity.class);
        intent.putExtra("treino_id", (int) treinoId);
        intent.putExtra("aluno_id", alunoId);
        intent.putExtra("treino_nome", nome);
        intent.putExtra("treino_observacao", observacao.isEmpty() ? null : observacao);
        intent.putExtra("treino_dia_semana", diaSemana);
        startActivity(intent);
        finish();
    }
}