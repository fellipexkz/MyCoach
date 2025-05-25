package com.mycoach.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

public class AdicionarTreinoActivity extends AppCompatActivity {

    private TextInputEditText treinoNomeInput;
    private TextInputEditText treinoObservacaoInput;
    private MaterialAutoCompleteTextView treinoDiaSemanaInput;
    private RecyclerView exerciciosRecyclerView;
    private NestedScrollView nestedScrollView;
    private AppBarLayout appBarLayout;
    private MaterialToolbar toolbar;
    private TextView exerciciosLabel;
    private ExercicioFormAdapter exercicioAdapter;
    private BancoDeDadosHelper bancoDeDadosHelper;
    private DataFirebase dbfire = new DataFirebase();

    private boolean paro = false;
    private boolean checkout_val = false;

    private static final String[] DIAS_SEMANA = {
            "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira",
            "Sexta-feira", "Sábado", "Domingo"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_treino);

        bancoDeDadosHelper = new BancoDeDadosHelper(this);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            Log.e("AdicionarTreino", "Toolbar não encontrada!");
        }
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        } else {
            Log.e("AdicionarTreino", "Não foi possível configurar o navigation listener!");
        }

        treinoNomeInput = findViewById(R.id.treinoNomeInput);
        treinoObservacaoInput = findViewById(R.id.treinoObservacaoInput);
        treinoDiaSemanaInput = findViewById(R.id.treinoDiaSemanaInput);
        exerciciosRecyclerView = findViewById(R.id.exerciciosRecyclerView);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        appBarLayout = findViewById(R.id.appBarLayout);
        exerciciosLabel = findViewById(R.id.exerciciosLabel);
        if (exerciciosLabel == null) {
            Log.e("AdicionarTreino", "exerciciosLabel não encontrada!");
        }

        if (treinoNomeInput == null || treinoObservacaoInput == null || treinoDiaSemanaInput == null ||
                exerciciosRecyclerView == null || nestedScrollView == null || appBarLayout == null) {
            Toast.makeText(this, "Erro: Componentes do layout não encontrados", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        treinoNomeInput.setFocusable(false);
        treinoNomeInput.setFocusableInTouchMode(false);
        treinoObservacaoInput.setFocusable(false);
        treinoObservacaoInput.setFocusableInTouchMode(false);
        treinoDiaSemanaInput.setFocusable(false);
        treinoDiaSemanaInput.setFocusableInTouchMode(false);

        ArrayAdapter<String> diasSemanaAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                DIAS_SEMANA
        );
        treinoDiaSemanaInput.setAdapter(diasSemanaAdapter);

        exerciciosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exercicioAdapter = new ExercicioFormAdapter(getSupportFragmentManager(), nestedScrollView);
        exercicioAdapter.setRecyclerView(exerciciosRecyclerView);
        exerciciosRecyclerView.setAdapter(exercicioAdapter);

        exerciciosRecyclerView.post(() -> {
            Exercicio exercicioInicial = new Exercicio();
            exercicioInicial.setNome("");
            for (int i = 0; i < 1; i++) {
                Serie serie = new Serie();
                serie.setCarga("");
                serie.setRepeticoes("");
                exercicioInicial.getSeries().add(serie);
            }
            exercicioAdapter.addExercicio(exercicioInicial);
            Log.d("AdicionarTreino", "Exercício inicial adicionado");
        });

        exerciciosRecyclerView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

        treinoNomeInput.setOnClickListener(v -> {
            treinoNomeInput.setFocusable(true);
            treinoNomeInput.setFocusableInTouchMode(true);
            treinoNomeInput.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(treinoNomeInput, InputMethodManager.SHOW_IMPLICIT);
            }
            Log.d("AdicionarTreino", "treinoNomeInput clicado");
        });

        treinoObservacaoInput.setOnClickListener(v -> {
            treinoObservacaoInput.setFocusable(true);
            treinoObservacaoInput.setFocusableInTouchMode(true);
            treinoObservacaoInput.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(treinoObservacaoInput, InputMethodManager.SHOW_IMPLICIT);
            }
            Log.d("AdicionarTreino", "treinoObservacaoInput clicado");
        });

        treinoDiaSemanaInput.setOnClickListener(v -> {
            treinoDiaSemanaInput.setFocusable(true);
            treinoDiaSemanaInput.setFocusableInTouchMode(true);
            treinoDiaSemanaInput.requestFocus();
            treinoDiaSemanaInput.showDropDown();
            Log.d("AdicionarTreino", "treinoDiaSemanaInput clicado");
        });

        treinoNomeInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        treinoNomeInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                treinoObservacaoInput.setFocusable(true);
                treinoObservacaoInput.setFocusableInTouchMode(true);
                if (treinoObservacaoInput.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(treinoObservacaoInput, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
                return true;
            }
            return false;
        });

        treinoObservacaoInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        treinoObservacaoInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                treinoDiaSemanaInput.setFocusable(true);
                treinoDiaSemanaInput.setFocusableInTouchMode(true);
                if (treinoDiaSemanaInput.requestFocus()) {
                    treinoDiaSemanaInput.showDropDown();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(treinoDiaSemanaInput, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
                return true;
            }
            return false;
        });

        treinoDiaSemanaInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                List<Exercicio> exercicios = exercicioAdapter.getExercicios();
                if (exercicios != null && !exercicios.isEmpty()) {
                    exerciciosRecyclerView.smoothScrollToPosition(0);
                    exerciciosRecyclerView.postDelayed(() -> {
                        RecyclerView.ViewHolder vh = exerciciosRecyclerView.findViewHolderForAdapterPosition(0);
                        if (vh instanceof ExercicioFormAdapter.ExercicioViewHolder) {
                            TextInputEditText exercicioNomeInput = ((ExercicioFormAdapter.ExercicioViewHolder) vh).exercicioNomeInput;
                            exercicioNomeInput.setFocusable(true);
                            exercicioNomeInput.setFocusableInTouchMode(true);
                            if (exercicioNomeInput.requestFocus()) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm != null) {
                                    imm.showSoftInput(exercicioNomeInput, InputMethodManager.SHOW_IMPLICIT);
                                }
                            }
                        } else {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            }
                            v.clearFocus();
                        }
                    }, 150);
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    v.clearFocus();
                }
                return true;
            }
            return false;
        });

        exercicioAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                updateDiaSemanaImeOptions();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                updateDiaSemanaImeOptions();
            }

            @Override
            public void onChanged() {
                updateDiaSemanaImeOptions();
            }
        });

        updateDiaSemanaImeOptions();

        nestedScrollView.post(() -> {
            nestedScrollView.requestFocus();
            treinoNomeInput.clearFocus();
            treinoObservacaoInput.clearFocus();
            treinoDiaSemanaInput.clearFocus();
            exerciciosRecyclerView.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(nestedScrollView.getWindowToken(), 0);
            }
            Log.d("AdicionarTreino", "Foco inicial direcionado e teclado ocultado.");
        });

        int alunoId = getIntent().getIntExtra("aluno_id", -1);
        if (alunoId == -1) {
            Toast.makeText(this, "Erro: ID do aluno não fornecido", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateDiaSemanaImeOptions() {
        List<Exercicio> exercicios = exercicioAdapter.getExercicios();
        if (exercicios != null && !exercicios.isEmpty()) {
            treinoDiaSemanaInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        } else {
            treinoDiaSemanaInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
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

            checkout_val = false;
            paro = false;

            for (Serie serieParaValidar : exercicio.getSeries()) {
                String cargaVal = serieParaValidar.getCarga().trim();
                String repeticoesVal = serieParaValidar.getRepeticoes().trim();
                if (cargaVal.isEmpty() || repeticoesVal.isEmpty()) {
                    Toast.makeText(this, "Preencha todos os campos das séries", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    int repeticoesIntVal = Integer.parseInt(repeticoesVal);
                    if (repeticoesIntVal <= 0) {
                        Toast.makeText(this, "O número de repetições deve ser maior que 0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "O número de repetições deve ser um valor numérico válido", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            for (Serie serie : exercicio.getSeries()) {
                String carga = serie.getCarga().trim();
                String repeticoes = serie.getRepeticoes().trim();
                int repeticoesInt = Integer.parseInt(repeticoes);

                long serieId = bancoDeDadosHelper.adicionarSerie((int) exercicioId, carga, repeticoesInt);
                if (serieId == -1) {
                    Toast.makeText(this, "Erro ao adicionar série", Toast.LENGTH_SHORT).show();
                    return;
                }
                serie.setCarga(carga);
                serie.setExercicioId((int) exercicioId);
                serie.setRepeticoes(repeticoes);

                dbfire.sendFirebaseSerie(serie, "series", bancoDeDadosHelper);
                Log.d("AdicionarTreinoActivity", "Série salva - ID: " + serieId + ", Carga: " + carga + ", Repetições: " + repeticoes);
            }
            exercicio.setId((int) exercicioId);
            exercicio.setTreinoId((int) treinoId);
            exercicio.setNome(exercicioNome);
            exercicio.setTempoDescanso(tempoDescanso);
            dbfire.sendFirebaseExercise(exercicio, "exercicios", bancoDeDadosHelper);
        }

        Toast.makeText(this, "Treino adicionado com sucesso", Toast.LENGTH_SHORT).show();

        treino.setAlunoId(alunoId);
        treino.setObservacao(observacao.isEmpty() ? null : observacao);
        treino.setDiaSemana(diaSemana);
        treino.setNome(nome);
        dbfire.sendFirebaseTreino(treino, "treinos", bancoDeDadosHelper);

        dbfire.syncWithFirebaseTreino(bancoDeDadosHelper, "treinos");
        dbfire.syncWithFirebaseExercise(bancoDeDadosHelper, "exercicios");
        dbfire.syncWithFirebaseSerie(bancoDeDadosHelper, "series");

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