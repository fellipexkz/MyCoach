package com.mycoach.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.List;

public class VisualizarAlunosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AlunoAdapter alunoAdapter;
    private BancoDeDadosHelper bancoDeDadosHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_alunos);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.studentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bancoDeDadosHelper = new BancoDeDadosHelper(this);

        loadStudents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudents();
    }

    private void loadStudents() {
        List<Aluno> alunoList = bancoDeDadosHelper.obterTodosAlunos();
        alunoAdapter = new AlunoAdapter(alunoList, aluno -> {
            Intent intent = new Intent(VisualizarAlunosActivity.this, DetalhesAlunoActivity.class);
            intent.putExtra("aluno_id", aluno.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(alunoAdapter);
    }
}