package com.mycoach.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, senhaEditText;
    private BancoDeDadosHelper bancoDeDadosHelper;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MyCoachPrefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_LOGGED_IN_USER_EMAIL = "logged_in_user_email";
    private static final String KEY_LOGGED_IN_USER_TYPE = "logged_in_user_type";
    private static final String KEY_LOGGED_IN_ALUNO_ID = "logged_in_aluno_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            String userEmail = sharedPreferences.getString(KEY_LOGGED_IN_USER_EMAIL, null);
            String userType = sharedPreferences.getString(KEY_LOGGED_IN_USER_TYPE, null);
            int alunoId = sharedPreferences.getInt(KEY_LOGGED_IN_ALUNO_ID, -1);

            if (userEmail != null && userType != null) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_USER_TYPE, userType);
                if (MainActivity.USER_TYPE_ALUNO.equals(userType)) {
                    intent.putExtra(MainActivity.EXTRA_ALUNO_ID, alunoId);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return;
            } else {
                clearLoginData();
            }
        }

        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        senhaEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);

        bancoDeDadosHelper = new BancoDeDadosHelper(this);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String senha = senhaEditText.getText().toString().trim();

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(LoginActivity.this, R.string.toast_fill_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            if (bancoDeDadosHelper.verificarPersonal(email, senha)) {
                saveLoginData(email, MainActivity.USER_TYPE_PERSONAL, -1);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_USER_TYPE, MainActivity.USER_TYPE_PERSONAL);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else if (bancoDeDadosHelper.verificarAluno(email, senha)) {
                int alunoId = bancoDeDadosHelper.obterIdAlunoPorEmail(email);
                if (alunoId != -1) {
                    saveLoginData(email, MainActivity.USER_TYPE_ALUNO, alunoId);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(MainActivity.EXTRA_USER_TYPE, MainActivity.USER_TYPE_ALUNO);
                    intent.putExtra(MainActivity.EXTRA_ALUNO_ID, alunoId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

    private void saveLoginData(String email, String userType, int alunoId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_LOGGED_IN_USER_EMAIL, email);
        editor.putString(KEY_LOGGED_IN_USER_TYPE, userType);
        if (MainActivity.USER_TYPE_ALUNO.equals(userType)) {
            editor.putInt(KEY_LOGGED_IN_ALUNO_ID, alunoId);
        }
        editor.apply();
    }

    private void clearLoginData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_LOGGED_IN_USER_EMAIL);
        editor.remove(KEY_LOGGED_IN_USER_TYPE);
        editor.remove(KEY_LOGGED_IN_ALUNO_ID);
        editor.apply();
    }
}