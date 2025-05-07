package com.mycoach.app;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdicionarAlunoActivity extends AppCompatActivity {

    private TextInputEditText nameEditText, emailEditText, passwordEditText;
    private MaterialButton addButton;
    private BancoDeDadosHelper bancoDeDadosHelper;
    private DataFirebase dbfire = new DataFirebase();

    DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference("alunos");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_aluno);

        bancoDeDadosHelper = new BancoDeDadosHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(v -> {
            String nome = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String senha = passwordEditText.getText().toString().trim();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(AdicionarAlunoActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean sucesso = bancoDeDadosHelper.adicionarAluno(nome, email, senha);
            if (sucesso) {
                Toast.makeText(AdicionarAlunoActivity.this, "Aluno adicionado com sucesso", Toast.LENGTH_SHORT).show();
                finish();
                // Enviar para o Firebase
                Aluno aluno = new Aluno();
                aluno.setId(generateUniqueId());
                Log.d("Aluno_id", String.valueOf(aluno.getId()));
                aluno.setNome(nome);
                aluno.setEmail(email);
                aluno.setSenha(senha);
            // Conexão com o Firebase
                dbfire.sendFirebaseAluno(aluno, "alunos", bancoDeDadosHelper);
                dbfire.syncWithFirebaseAluno(bancoDeDadosHelper, "alunos");

            } else {
                Toast.makeText(AdicionarAlunoActivity.this, "Erro ao adicionar aluno, email já existe", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private int generateUniqueId()
    {
        List<Aluno> users = bancoDeDadosHelper.obterTodosAlunos();
        int maxId = 0;
        for (Aluno u : users) {
            if (u.getId() > maxId) maxId = u.getId();
        }
        return maxId + 1;
    }
}