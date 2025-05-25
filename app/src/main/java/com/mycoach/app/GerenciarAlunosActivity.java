package com.mycoach.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class GerenciarAlunosActivity extends AppCompatActivity {

    private RecyclerView alunosRecyclerView;
    private TextView semAlunosTextView;
    private AlunoAdapter alunoAdapter;
    private BancoDeDadosHelper bancoDeDadosHelper;
    private List<Aluno> listaAlunos;
    private static final int REQUEST_CODE_DETALHES_ALUNO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_alunos);

        bancoDeDadosHelper = new BancoDeDadosHelper(this);
        listaAlunos = new ArrayList<>();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        semAlunosTextView = findViewById(R.id.semAlunosTextView);

        alunosRecyclerView = findViewById(R.id.studentsRecyclerView);
        alunosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        alunoAdapter = new AlunoAdapter(listaAlunos, aluno -> {
            Intent intent = new Intent(GerenciarAlunosActivity.this, DetalhesAlunoActivity.class);
            intent.putExtra("aluno_id", aluno.getId());
            startActivityForResult(intent, REQUEST_CODE_DETALHES_ALUNO);
        });
        alunosRecyclerView.setAdapter(alunoAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(alunosRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        alunosRecyclerView.addItemDecoration(dividerItemDecoration);

        alunosRecyclerView.setItemAnimator(new androidx.recyclerview.widget.DefaultItemAnimator());

        carregarAlunos();

        ExtendedFloatingActionButton adicionarAlunoFab = findViewById(R.id.adicionarAlunoFab);
        adicionarAlunoFab.setOnClickListener(v -> {
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
        List<Aluno> novosAlunos = bancoDeDadosHelper.obterTodosAlunos();
        listaAlunos.addAll(novosAlunos);
        alunoAdapter.setAlunos(listaAlunos);

        if (listaAlunos.isEmpty()) {
            alunosRecyclerView.setVisibility(View.GONE);
            semAlunosTextView.setVisibility(View.VISIBLE);
        } else {
            alunosRecyclerView.setVisibility(View.VISIBLE);
            semAlunosTextView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DETALHES_ALUNO && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("aluno_deletado", false)) {
                carregarAlunos();
            }
        }
    }
}