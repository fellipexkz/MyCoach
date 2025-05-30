package com.mycoach.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_USER_TYPE = "USER_TYPE";
    public static final String EXTRA_ALUNO_ID = "ALUNO_ID";
    public static final String USER_TYPE_PERSONAL = "PERSONAL";
    public static final String USER_TYPE_ALUNO = "ALUNO";

    private int currentAlunoId = -1;

    private MaterialCardView mainActionCard;
    private TextView cardHeaderText;
    private ImageView cardMediaImage;

    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "MyCoachPrefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_LOGGED_IN_USER_EMAIL = "logged_in_user_email";
    private static final String KEY_LOGGED_IN_USER_TYPE = "logged_in_user_type";
    private static final String KEY_LOGGED_IN_ALUNO_ID = "logged_in_aluno_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        Intent intent = getIntent();
        String currentUserType = intent.getStringExtra(EXTRA_USER_TYPE);

        if (currentUserType == null && sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            currentUserType = sharedPreferences.getString(KEY_LOGGED_IN_USER_TYPE, null);
            if (MainActivity.USER_TYPE_ALUNO.equals(currentUserType)) {
                currentAlunoId = sharedPreferences.getInt(KEY_LOGGED_IN_ALUNO_ID, -1);
            }
        } else if (USER_TYPE_ALUNO.equals(currentUserType)) {
            currentAlunoId = intent.getIntExtra(EXTRA_ALUNO_ID, -1);
        }


        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainActionCard = findViewById(R.id.main_action_card);
        cardHeaderText = findViewById(R.id.cardHeader);
        cardMediaImage = findViewById(R.id.cardMedia);

        if (currentUserType == null) {
            handleInvalidUserType();
            return;
        }

        switch (currentUserType) {
            case USER_TYPE_PERSONAL:
                setupPersonalUI();
                break;
            case USER_TYPE_ALUNO:
                if (currentAlunoId == -1) {
                    Toast.makeText(this, "Erro: ID do aluno não fornecido.", Toast.LENGTH_LONG).show();
                    handleInvalidUserType();
                    return;
                }
                setupAlunoUI();
                break;
            default:
                handleInvalidUserType();
                break;
        }
    }

    private void setupPersonalUI() {
        cardHeaderText.setText(getString(R.string.text_gerenciar_alunos));
        cardHeaderText.setVisibility(View.VISIBLE);
        cardMediaImage.setImageResource(R.drawable.card_personal);
        cardMediaImage.setContentDescription(getString(R.string.content_description_card_personal));
        cardMediaImage.setVisibility(View.VISIBLE);

        mainActionCard.setOnClickListener(v -> {
            Intent intent_nav = new Intent(MainActivity.this, GerenciarAlunosActivity.class);
            startActivity(intent_nav);
        });
    }

    private void setupAlunoUI() {
        cardHeaderText.setText(getString(R.string.text_ver_exercicios));
        cardHeaderText.setVisibility(View.VISIBLE);
        cardMediaImage.setImageResource(R.drawable.card_aluno);
        cardMediaImage.setContentDescription(getString(R.string.content_description_card_aluno));
        cardMediaImage.setVisibility(View.VISIBLE);

        mainActionCard.setOnClickListener(v -> {
            Intent intent_nav = new Intent(MainActivity.this, VisualizarTreinosActivity.class);
            intent_nav.putExtra("aluno_id", currentAlunoId);
            intent_nav.putExtra("is_aluno_flow", true);
            startActivity(intent_nav);
        });
    }

    private void handleInvalidUserType() {
        Toast.makeText(this, "Sessão expirada ou inválida. Por favor, faça login novamente.", Toast.LENGTH_LONG).show();
        clearLoginData();
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.action_sair))
                    .setMessage(R.string.toast_confirm_logout)
                    .setPositiveButton(R.string.botao_confirmar, (dialog, which) -> {
                        clearLoginData();
                        Toast.makeText(MainActivity.this, R.string.toast_logout_success, Toast.LENGTH_SHORT).show();
                        Intent intent_nav = new Intent(MainActivity.this, LoginActivity.class);
                        intent_nav.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent_nav);
                        finish();
                    })
                    .setNegativeButton(R.string.botao_cancelar, null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
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