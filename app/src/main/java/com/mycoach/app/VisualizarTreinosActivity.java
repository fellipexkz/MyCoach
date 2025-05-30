package com.mycoach.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.List;

public class VisualizarTreinosActivity extends AppCompatActivity {

    private RecyclerView treinosRecyclerView;
    private TextView semTreinosTextView;
    private TreinoAdapter treinoAdapter;
    private BancoDeDadosHelper bancoDeDadosHelper;
    private ActivityResultLauncher<Intent> detalhesTreinoLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_treinos);

        bancoDeDadosHelper = new BancoDeDadosHelper(this);

        detalhesTreinoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getBooleanExtra("treino_deletado", false)) {
                            int alunoId = getIntent().getIntExtra("aluno_id", -1);
                            if (alunoId != -1) {
                                List<Treino> treinosAtualizados = bancoDeDadosHelper.obterTreinosPorAlunoId(alunoId);
                                atualizarVisibilidade(treinosAtualizados);
                            }
                        }
                    }
                }
        );

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        boolean isAlunoFlow = getIntent().getBooleanExtra("is_aluno_flow", false);
        toolbar.setTitle(isAlunoFlow ? R.string.title_my_exercises : R.string.title_client_workouts);

        int alunoId = getIntent().getIntExtra("aluno_id", -1);
        if (alunoId == -1) {
            finish();
            return;
        }

        treinosRecyclerView = findViewById(R.id.treinosRecyclerView);
        semTreinosTextView = findViewById(R.id.semTreinosTextView);
        treinosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        treinoAdapter = new TreinoAdapter(treino -> {
            Intent intent = new Intent(VisualizarTreinosActivity.this, DetalhesTreinoActivity.class);
            intent.putExtra("treino_id", treino.getId());
            intent.putExtra("aluno_id", alunoId);
            intent.putExtra("treino_nome", treino.getNome());
            intent.putExtra("treino_observacao", treino.getObservacao());
            intent.putExtra("treino_dia_semana_index", treino.getDiaSemanaIndex());
            intent.putExtra("is_aluno_flow", isAlunoFlow);
            detalhesTreinoLauncher.launch(intent);
        });
        treinosRecyclerView.setAdapter(treinoAdapter);

        List<Treino> treinos = bancoDeDadosHelper.obterTreinosPorAlunoId(alunoId);
        atualizarVisibilidade(treinos);
    }

    private void atualizarVisibilidade(List<Treino> treinos) {
        if (treinos != null && !treinos.isEmpty()) {
            treinoAdapter.setTreinos(treinos);
            treinosRecyclerView.setVisibility(View.VISIBLE);
            semTreinosTextView.setVisibility(View.GONE);
        } else {
            treinosRecyclerView.setVisibility(View.GONE);
            semTreinosTextView.setVisibility(View.VISIBLE);
        }
    }
}