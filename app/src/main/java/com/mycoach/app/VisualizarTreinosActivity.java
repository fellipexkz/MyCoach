package com.mycoach.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
    private static final int REQUEST_CODE_DETALHES_TREINO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_treinos);

        bancoDeDadosHelper = new BancoDeDadosHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        boolean isAlunoFlow = getIntent().getBooleanExtra("is_aluno_flow", false);
        toolbar.setTitle(isAlunoFlow ? "Meus Exercícios" : "Treinos do Aluno");

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
            intent.putExtra("treino_dia_semana", treino.getDiaSemana());
            intent.putExtra("is_aluno_flow", isAlunoFlow);
            startActivityForResult(intent, REQUEST_CODE_DETALHES_TREINO);
        });
        treinosRecyclerView.setAdapter(treinoAdapter);

        List<Treino> treinos = bancoDeDadosHelper.obterTreinosPorAlunoId(alunoId);
        atualizarVisibilidade(treinos);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DETALHES_TREINO && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("treino_deletado", false)) {
                int alunoId = getIntent().getIntExtra("aluno_id", -1);
                if (alunoId != -1) {
                    List<Treino> treinosAtualizados = bancoDeDadosHelper.obterTreinosPorAlunoId(alunoId);
                    atualizarVisibilidade(treinosAtualizados);
                }
            }
        }
    }

    private void atualizarVisibilidade(List<Treino> treinos) {
        if (treinos != null && !treinos.isEmpty()) {
            treinoAdapter.setTreinos(treinos);
            treinosRecyclerView.setVisibility(View.VISIBLE);
            semTreinosTextView.setVisibility(View.GONE);
        } else {
            Log.e("VisualizarTreinosActivity", "Lista de treinos vazia ou nula");
            treinosRecyclerView.setVisibility(View.GONE);
            semTreinosTextView.setVisibility(View.VISIBLE);
        }
    }
}