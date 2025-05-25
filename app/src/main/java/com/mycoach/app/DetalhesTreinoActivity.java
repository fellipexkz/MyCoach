package com.mycoach.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import android.widget.TextView;

import java.util.List;

public class DetalhesTreinoActivity extends AppCompatActivity {

    private RecyclerView exerciciosRecyclerView;
    private ExercicioAdapter exercicioAdapter;
    private int treinoId;
    private int alunoId;
    private BancoDeDadosHelper bancoDeDadosHelper;
    private DataFirebase dbfire = new DataFirebase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_treino);

        bancoDeDadosHelper = new BancoDeDadosHelper(this);

        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            if (verticalOffset == 0) {
                Log.d("DetalhesTreinoActivity", "AppBarLayout expandido");
            } else if (Math.abs(verticalOffset) >= appBarLayout1.getTotalScrollRange()) {
                Log.d("DetalhesTreinoActivity", "AppBarLayout colapsado");
            } else {
                Log.d("DetalhesTreinoActivity", "AppBarLayout parcialmente colapsado: " + verticalOffset);
            }
        });

        TextView nomeTextView = findViewById(R.id.detalheTreinoNome);
        TextView diaSemanaTextView = findViewById(R.id.detalheTreinoDiaSemana);
        TextView observacaoTextView = findViewById(R.id.detalheTreinoObservacao);

        treinoId = getIntent().getIntExtra("treino_id", -1);
        alunoId = getIntent().getIntExtra("aluno_id", -1);
        String treinoNome = getIntent().getStringExtra("treino_nome");
        String treinoObservacao = getIntent().getStringExtra("treino_observacao");
        String treinoDiaSemana = getIntent().getStringExtra("treino_dia_semana");

        Log.d("DetalhesTreinoActivity", "Dados recebidos - treinoId: " + treinoId + ", alunoId: " + alunoId + ", nome: " + treinoNome + ", dia: " + treinoDiaSemana);

        if (treinoId != -1 && alunoId != -1 && treinoNome != null && treinoDiaSemana != null) {
            nomeTextView.setText(treinoNome);
            diaSemanaTextView.setText("Dia: " + treinoDiaSemana);
            observacaoTextView.setText(treinoObservacao != null ? "Observação: " + treinoObservacao : "Sem observação");

            exerciciosRecyclerView = findViewById(R.id.exerciciosRecyclerView);
            exerciciosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            exerciciosRecyclerView.setNestedScrollingEnabled(false);
            exercicioAdapter = new ExercicioAdapter();
            exerciciosRecyclerView.setAdapter(exercicioAdapter);

            BancoDeDadosHelper db = new BancoDeDadosHelper(this);
            List<Treino> treinos = db.obterTreinosPorAlunoId(alunoId);
            Treino treinoExibido = null;
            for (Treino treino : treinos) {
                if (treino.getId() == treinoId) {
                    treinoExibido = treino;
                    break;
                }
            }

            if (treinoExibido != null) {
                Log.d("DetalhesTreinoActivity", "Treino encontrado - ID: " + treinoExibido.getId() + ", Exercícios: " + treinoExibido.getExercicios().size());
                for (Exercicio ex : treinoExibido.getExercicios()) {
                    Log.d("DetalhesTreinoActivity", "Exercício - Nome: " + ex.getNome() + ", Séries: " + ex.getSeries().size());
                }
                exercicioAdapter.setExercicios(treinoExibido.getExercicios());
            } else {
                Log.d("DetalhesTreinoActivity", "Treino não encontrado para ID: " + treinoId);
                nomeTextView.setText("Treino não encontrado");
            }
        } else {
            Log.d("DetalhesTreinoActivity", "Dados insuficientes para exibir o treino");
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        boolean isAlunoFlow = getIntent().getBooleanExtra("is_aluno_flow", false);
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        if (isAlunoFlow) {
            deleteItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            TextView nomeTextView = findViewById(R.id.detalheTreinoNome);
            Log.d("Exclusão", "Tentando excluir treino - treinoId: " + treinoId + ", alunoId: " + alunoId);
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                    .setTitle("Confirmar Exclusão")
                    .setMessage("Tem certeza que deseja excluir o treino " + nomeTextView.getText() + "?")
                    .setPositiveButton("Confirmar", (dialog, which) -> {
                        BancoDeDadosHelper db = new BancoDeDadosHelper(this);
                        List<Treino> treinos = db.obterTreinosPorAlunoId(alunoId);
                        boolean treinoPertenceAoAluno = false;
                        for (Treino treino : treinos) {
                            if (treino.getId() == treinoId) {
                                treinoPertenceAoAluno = true;
                                break;
                            }
                        }

                        if (treinoPertenceAoAluno) {
                            Log.d("Exclusão", "Treino pertence ao aluno. Excluindo...");
                            bancoDeDadosHelper.deletarTreino(treinoId);
                            dbfire.removeTreinoComDependencias(treinoId, "treinos");
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("treino_deletado", true);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        } else {
                            Log.e("Exclusão", "Treino ID " + treinoId + " não pertence ao aluno ID " + alunoId);
                            new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                                    .setTitle("Erro")
                                    .setMessage("Este treino não pode ser excluído por você.")
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}