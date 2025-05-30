package com.mycoach.app;

import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import android.util.Patterns;

public class AdicionarAlunoActivity extends AppCompatActivity {

    private TextInputEditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private BancoDeDadosHelper bancoDeDadosHelper;
    private final DataFirebase dbfire = new DataFirebase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_aluno);

        bancoDeDadosHelper = new BancoDeDadosHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        MaterialButton addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(v -> {
            String nome = nameEditText.getText() != null ? nameEditText.getText().toString().trim() : "";
            String email = emailEditText.getText() != null ? emailEditText.getText().toString().trim() : "";
            String senha = passwordEditText.getText() != null ? passwordEditText.getText().toString().trim() : "";
            String confirmarSenha = confirmPasswordEditText.getText() != null ? confirmPasswordEditText.getText().toString().trim() : "";

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
                Toast.makeText(AdicionarAlunoActivity.this, R.string.toast_fill_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            if (senha.length() < 5) {
                Toast.makeText(AdicionarAlunoActivity.this, R.string.toast_password_too_short, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!senha.equals(confirmarSenha)) {
                Toast.makeText(AdicionarAlunoActivity.this, R.string.toast_passwords_mismatch, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmail(email)) {
                Toast.makeText(AdicionarAlunoActivity.this, R.string.toast_email_invalid, Toast.LENGTH_SHORT).show();
                return;
            }

            if (bancoDeDadosHelper.emailExiste(email)) {
                Toast.makeText(AdicionarAlunoActivity.this, R.string.toast_error_adding_student, Toast.LENGTH_SHORT).show();
                return;
            }

            boolean sucesso = bancoDeDadosHelper.adicionarAluno(nome, email, senha);
            if (sucesso) {
                int novoAlunoId = bancoDeDadosHelper.obterIdAlunoPorEmail(email);
                if (novoAlunoId != -1) {
                    Aluno aluno = new Aluno();
                    aluno.setId(novoAlunoId);
                    aluno.setNome(nome);
                    aluno.setEmail(email);
                    aluno.setSenha(senha);
                    dbfire.sendFirebaseAluno(aluno, "alunos", bancoDeDadosHelper);
                    Toast.makeText(AdicionarAlunoActivity.this, R.string.toast_student_added, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AdicionarAlunoActivity.this, R.string.toast_error_adding_student, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AdicionarAlunoActivity.this, R.string.toast_error_adding_student, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}