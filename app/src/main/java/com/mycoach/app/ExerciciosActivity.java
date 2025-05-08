package com.mycoach.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.List;

public class ExerciciosActivity extends AppCompatActivity {

    private RecyclerView treinosRecyclerView;
    private TreinoAdapter treinoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_treinos);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        int alunoId = getIntent().getIntExtra("aluno_id", -1);
        if (alunoId == -1) {
            finish();
            return;
        }

        treinosRecyclerView = findViewById(R.id.treinosRecyclerView);
        treinosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        treinoAdapter = new TreinoAdapter(treino -> {
            Intent intent = new Intent(ExerciciosActivity.this, DetalhesTreinoActivity.class);
            intent.putExtra("treino_id", treino.getId());
            intent.putExtra("aluno_id", alunoId);
            intent.putExtra("treino_nome", treino.getNome());
            intent.putExtra("treino_observacao", treino.getObservacao());
            intent.putExtra("treino_dia_semana", treino.getDiaSemana());
            startActivity(intent);
        });
        treinosRecyclerView.setAdapter(treinoAdapter);

        BancoDeDadosHelper db = new BancoDeDadosHelper(this);
        List<Treino> treinos = db.obterTreinosPorAlunoId(alunoId);
        treinoAdapter.setTreinos(treinos);
    }
}