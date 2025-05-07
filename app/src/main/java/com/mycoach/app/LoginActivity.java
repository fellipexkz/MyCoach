package com.mycoach.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, senhaEditText;
    private Button loginButton;
    private TextView signUpTextView;
    private BancoDeDadosHelper bancoDeDadosHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        senhaEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpTextView = findViewById(R.id.signUpTextView);

        bancoDeDadosHelper = new BancoDeDadosHelper(this);

        DataFirebase dbfire = new DataFirebase();
        dbfire.syncWithFirebaseAluno(bancoDeDadosHelper, "alunos");
        dbfire.syncWithFirebaseTreino(bancoDeDadosHelper,"treinos");

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String senha = senhaEditText.getText().toString().trim();

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(LoginActivity.this, "Erro ao recuperar ID do aluno", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Email ou senha incorretos", Toast.LENGTH_SHORT).show();
            }
        });

        signUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent);
        });
    }
}