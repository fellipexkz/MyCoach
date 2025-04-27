package com.mycoach.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.List;

public class AlunoMainActivity extends AppCompatActivity {

    private RecyclerView treinosRecyclerView;
    private TreinoAdapter treinoAdapter;
    private BancoDeDadosHelper bancoDeDadosHelper;
    private List<Treino> listaTreinos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aluno_main);

        bancoDeDadosHelper = new BancoDeDadosHelper(this);
        listaTreinos = new ArrayList<>();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_aluno, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            Intent intent = new Intent(AlunoMainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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