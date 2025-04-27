package com.mycoach.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.List;

public class VisualizarTreinosActivity extends AppCompatActivity {

    private RecyclerView treinosRecyclerView;
    private TreinoAdapter treinoAdapter;
    private BancoDeDadosHelper bancoDeDadosHelper;
    private List<Treino> listaTreinos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_treinos);

        bancoDeDadosHelper = new BancoDeDadosHelper(this);
        listaTreinos = new ArrayList<>();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        treinosRecyclerView = findViewById(R.id.workoutsRecyclerView);
        treinosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        treinoAdapter = new TreinoAdapter();
        treinosRecyclerView.setAdapter(treinoAdapter);

        int alunoId = getIntent().getIntExtra("aluno_id", -1);
        if (alunoId != -1) {
            listaTreinos.clear();
            listaTreinos.addAll(bancoDeDadosHelper.obterTreinosPorAlunoId(alunoId));
            treinoAdapter.setTreinos(listaTreinos);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int alunoId = getIntent().getIntExtra("aluno_id", -1);
        if (alunoId != -1) {
            listaTreinos.clear();
            listaTreinos.addAll(bancoDeDadosHelper.obterTreinosPorAlunoId(alunoId));
            treinoAdapter.setTreinos(listaTreinos);
        }
    }
}