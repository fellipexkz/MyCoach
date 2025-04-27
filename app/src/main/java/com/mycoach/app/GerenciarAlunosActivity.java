package com.mycoach.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class GerenciarAlunosActivity extends AppCompatActivity {

    private RecyclerView alunosRecyclerView;
    private AlunoAdapter alunoAdapter;
    private BancoDeDadosHelper bancoDeDadosHelper;
    private List<Aluno> listaAlunos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_alunos);

        bancoDeDadosHelper = new BancoDeDadosHelper(this);
        listaAlunos = new ArrayList<>();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        alunosRecyclerView = findViewById(R.id.studentsRecyclerView);
        alunosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        alunoAdapter = new AlunoAdapter(listaAlunos, aluno -> {
            Intent intent = new Intent(GerenciarAlunosActivity.this, DetalhesAlunoActivity.class);
            intent.putExtra("aluno_id", aluno.getId());
            startActivity(intent);
        });
        alunosRecyclerView.setAdapter(alunoAdapter);

        carregarAlunos();

        FloatingActionButton addStudentFab = findViewById(R.id.addStudentFab);
        addStudentFab.setOnClickListener(v -> {
            Intent intent = new Intent(GerenciarAlunosActivity.this, AdicionarAlunoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarAlunos();
    }

    private void carregarAlunos() {
        listaAlunos.clear();
        listaAlunos.addAll(bancoDeDadosHelper.obterTodosAlunos());
        alunoAdapter.notifyDataSetChanged();
    }
}