package com.mycoach.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import android.widget.TextView;
import java.util.List;

public class DetalhesTreinoActivity extends AppCompatActivity {

    private int treinoId;
    private int alunoId;
    private BancoDeDadosHelper bancoDeDadosHelper;
    private final DataFirebase dbfire = new DataFirebase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_treino);

        bancoDeDadosHelper = new BancoDeDadosHelper(this);

        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

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
        int treinoDiaSemanaIndex = getIntent().getIntExtra("treino_dia_semana_index", -1);

        Log.d("DetalhesTreinoActivity", "Dados recebidos - treinoId: " + treinoId + ", alunoId: " + alunoId + ", nome: " + treinoNome + ", diaIndex: " + treinoDiaSemanaIndex);

        if (treinoId != -1 && alunoId != -1 && treinoNome != null && treinoDiaSemanaIndex != -1) {
            nomeTextView.setText(treinoNome);
            String[] diasSemana = getResources().getStringArray(R.array.dias_semana);
            String treinoDiaSemana = diasSemana[treinoDiaSemanaIndex];
            diaSemanaTextView.setText(String.format(getString(R.string.treino_dia_semana_format), treinoDiaSemana));
            observacaoTextView.setText(treinoObservacao != null ? String.format(getString(R.string.observacao_prefix), treinoObservacao) : getString(R.string.sem_observacao));

            RecyclerView exerciciosRecyclerView = findViewById(R.id.exerciciosRecyclerView);
            exerciciosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            exerciciosRecyclerView.setNestedScrollingEnabled(false);
            ExercicioAdapter exercicioAdapter = new ExercicioAdapter();
            exerciciosRecyclerView.setAdapter(exercicioAdapter);

            List<Treino> treinos = bancoDeDadosHelper.obterTreinosPorAlunoId(alunoId);
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
                nomeTextView.setText(getString(R.string.treino_nao_encontrado));
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
                    .setTitle(getString(R.string.titulo_dialogo_exclusao))
                    .setMessage(String.format(getString(R.string.confirmar_exclusao), nomeTextView.getText()))
                    .setPositiveButton(getString(R.string.botao_confirmar), (dialog, which) -> {
                        List<Treino> treinos = bancoDeDadosHelper.obterTreinosPorAlunoId(alunoId);
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
                                    .setTitle(getString(R.string.titulo_erro))
                                    .setMessage(getString(R.string.erro_exclusao))
                                    .setPositiveButton(getString(R.string.botao_ok), null)
                                    .show();
                        }
                    })
                    .setNegativeButton(getString(R.string.botao_cancelar), null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}