package com.mycoach.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private DataFirebase dbfire;
    private List<Aluno> listaAlunos;
    private ActivityResultLauncher<Intent> detalhesAlunoLauncher;
    private ActivityResultLauncher<Intent> adicionarAlunoLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_alunos);

        bancoDeDadosHelper = new BancoDeDadosHelper(this);
        dbfire = new DataFirebase();
        listaAlunos = new ArrayList<>();

        detalhesAlunoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getBooleanExtra("aluno_deletado", false)) {
                            carregarAlunos();
                        }
                    }
                }
        );

        adicionarAlunoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        carregarAlunos();
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

        semAlunosTextView = findViewById(R.id.semAlunosTextView);

        alunosRecyclerView = findViewById(R.id.studentsRecyclerView);
        alunosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        alunoAdapter = new AlunoAdapter(listaAlunos, aluno -> {
            Intent intent = new Intent(GerenciarAlunosActivity.this, DetalhesAlunoActivity.class);
            intent.putExtra("aluno_id", aluno.getId());
            detalhesAlunoLauncher.launch(intent);
        });
        alunosRecyclerView.setAdapter(alunoAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(alunosRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        alunosRecyclerView.addItemDecoration(dividerItemDecoration);

        alunosRecyclerView.setItemAnimator(new androidx.recyclerview.widget.DefaultItemAnimator());

        ExtendedFloatingActionButton adicionarAlunoFab = findViewById(R.id.adicionarAlunoFab);
        if (adicionarAlunoFab != null) {
            adicionarAlunoFab.setOnClickListener(v -> {
                Intent intent = new Intent(GerenciarAlunosActivity.this, AdicionarAlunoActivity.class);
                adicionarAlunoLauncher.launch(intent);
            });
        } else {
            Log.e("GerenciarAlunosActivity", "adicionarAlunoFab não encontrado no layout");
        }

        dbfire.syncWithFirebaseAluno(bancoDeDadosHelper, "alunos");
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
    protected void onResume() {
        super.onResume();
        dbfire.syncWithFirebaseAluno(bancoDeDadosHelper, "alunos");
        carregarAlunos();
    }
}