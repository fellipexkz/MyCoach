package com.mycoach.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, senhaEditText;
    private BancoDeDadosHelper bancoDeDadosHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        senhaEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);

        bancoDeDadosHelper = new BancoDeDadosHelper(this);

        DataFirebase dbfire = new DataFirebase();
        dbfire.syncWithFirebaseAluno(bancoDeDadosHelper, "alunos");
        dbfire.syncWithFirebaseTreino(bancoDeDadosHelper, "treinos");
        dbfire.syncWithFirebaseExercise(bancoDeDadosHelper, "exercicios");
        dbfire.syncWithFirebaseSerie(bancoDeDadosHelper, "series");

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String senha = senhaEditText.getText().toString().trim();

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(LoginActivity.this, R.string.toast_fill_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            if (bancoDeDadosHelper.verificarPersonal(email, senha)) {
                Intent intent = new Intent(LoginActivity.this, PersonalMainActivity.class);
                startActivity(intent);
                finish();
            } else if (bancoDeDadosHelper.verificarAluno(email, senha)) {
                int alunoId = bancoDeDadosHelper.obterIdAlunoPorEmail(email);
                if (alunoId != -1) {
                    Intent intent = new Intent(LoginActivity.this, AlunoMainActivity.class);
                    intent.putExtra("aluno_id", alunoId);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.toast_error_recover_client_id, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, R.string.toast_invalid_credentials, Toast.LENGTH_SHORT).show();
            }
        });
    }
}