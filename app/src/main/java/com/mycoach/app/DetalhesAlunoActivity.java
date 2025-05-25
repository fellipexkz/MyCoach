package com.mycoach.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.widget.RelativeLayout;

public class DetalhesAlunoActivity extends AppCompatActivity {

    private TextView nomeAlunoTextView, emailAlunoTextView, inicialAvatarTextView;
    private MaterialButton visualizarTreinosButton, adicionarTreinoButton, deleteStudentButton;
    private BancoDeDadosHelper bancoDeDadosHelper;
    private DataFirebase dbfire = new DataFirebase();
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
        deleteStudentButton = findViewById(R.id.deleteStudentButton);

        RelativeLayout avatarLayout = findViewById(R.id.avatar);
        avatarLayout.setClipToOutline(true);

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

        deleteStudentButton.setOnClickListener(v ->
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Confirmar Exclusão")
                        .setMessage("Tem certeza que deseja excluir o aluno " + nomeAlunoTextView.getText() + "?")
                        .setPositiveButton("Confirmar", (dialog, which) -> {
                            bancoDeDadosHelper.deletarAluno(alunoId);
                            dbfire.removeFirebaseAluno(alunoId, "alunos");
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("aluno_deletado", true);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        })
                        .setNegativeButton("Cancelar", null)
                        .show()
        );
    }
}