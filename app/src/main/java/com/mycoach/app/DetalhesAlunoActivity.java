package com.mycoach.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class DetalhesAlunoActivity extends AppCompatActivity {

    private TextView nomeAlunoTextView, emailAlunoTextView, inicialAvatarTextView;
    private MaterialButton visualizarTreinosButton, adicionarTreinoButton;
    private BancoDeDadosHelper bancoDeDadosHelper;
    private int alunoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_aluno);

        bancoDeDadosHelper = new BancoDeDadosHelper(this);

        alunoId = getIntent().getIntExtra("aluno_id", -1);
        if (alunoId == -1) {
            finish();
            return;
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        nomeAlunoTextView = findViewById(R.id.studentNameTextView);
        emailAlunoTextView = findViewById(R.id.studentEmailTextView);
        inicialAvatarTextView = findViewById(R.id.avatarInitialTextView);
        visualizarTreinosButton = findViewById(R.id.viewWorkoutsButton);
        adicionarTreinoButton = findViewById(R.id.addWorkoutButton);

        Aluno aluno = bancoDeDadosHelper.obterAlunoPorId(alunoId);
        if (aluno != null) {
            nomeAlunoTextView.setText(aluno.getNome());
            emailAlunoTextView.setText(aluno.getEmail());
            if (aluno.getNome() != null && !aluno.getNome().isEmpty()) {
                inicialAvatarTextView.setText(aluno.getNome().substring(0, 1).toUpperCase());
            }
        }

        visualizarTreinosButton.setOnClickListener(v -> {
            Intent intent = new Intent(DetalhesAlunoActivity.this, VisualizarTreinosActivity.class);
            intent.putExtra("aluno_id", alunoId);
            startActivity(intent);
        });

        adicionarTreinoButton.setOnClickListener(v -> {
            Intent intent = new Intent(DetalhesAlunoActivity.this, AdicionarTreinoActivity.class);
            intent.putExtra("aluno_id", alunoId);
            startActivity(intent);
        });
    }
}