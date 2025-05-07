package com.mycoach.app;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DataFirebase {
    // Treino
    public void syncWithFirebaseTreino(BancoDeDadosHelper bancoDeDadosHelper, String reference)
    {
        FirebaseDatabase.getInstance().getReference(reference).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Apagar registros locais
                bancoDeDadosHelper.deletarTodosTreinos();

                // Inserir novos
                for (DataSnapshot treinoSnapshot : snapshot.getChildren()) {
                    Treino treino = treinoSnapshot.getValue(Treino.class);
                    if (treino != null) {
                        bancoDeDadosHelper.adicionarTreino(treino.getAlunoId(), treino.getNome(), treino.getDescricao(), treino.getData());
                        Log.d("Aluno_Id", String.valueOf(treino.getAlunoId()));
                        Log.d("Nome", treino.getNome());
                        Log.d("Descrição", treino.getDescricao());
                        Log.d("id", String.valueOf(treino.getId()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Sync", "Erro ao sincronizar com Firebase: " + error.getMessage());
            }
        });
    }
    public void sendFirebaseTreino(Treino treino, String refDatabase, BancoDeDadosHelper bancoDeDadosHelper)
    {
        FirebaseDatabase.getInstance().getReference(refDatabase)
                .push() // ou .child(email.replace(".", "_")) se quiser um ID fixo
                .setValue(treino);
        FirebaseDatabase.getInstance().getReference(refDatabase).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Treino treino = snapshot.getValue(Treino.class);
                if (treino != null) {
                    // Verifique se já existe no SQLite antes de inserir
                    if (!bancoDeDadosHelper.alunoIdExiste(treino.getAlunoId())) {
                        bancoDeDadosHelper.adicionarTreino(treino.getAlunoId(), treino.getNome(), treino.getDescricao(), treino.getData());
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    // Alunos
    public void syncWithFirebaseAluno(BancoDeDadosHelper bancoDeDadosHelper, String reference)
    {
        FirebaseDatabase.getInstance().getReference(reference).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Apagar registros locais
                bancoDeDadosHelper.deletarTodosAlunos();

                // Inserir novos
                for (DataSnapshot alunoSnapshot : snapshot.getChildren()) {
                    Aluno aluno = alunoSnapshot.getValue(Aluno.class);
                    if (aluno != null) {
                        bancoDeDadosHelper.adicio narAluno(aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getSenha());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Sync", "Erro ao sincronizar com Firebase: " + error.getMessage());
            }
        });
    }
    public void sendFirebaseAluno(Aluno aluno, String refDatabase, BancoDeDadosHelper bancoDeDadosHelper)
    {
        FirebaseDatabase.getInstance().getReference(refDatabase)
                .child(String.valueOf(aluno.getId())) // se quiser um ID fixo ou use .push()
                .setValue(aluno);
        FirebaseDatabase.getInstance().getReference(refDatabase).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Aluno aluno = snapshot.getValue(Aluno.class);
                if (aluno != null) {
                    // Verifique se já existe no SQLite antes de inserir
                    if (!bancoDeDadosHelper.emailExiste(aluno.getEmail())) {
                        bancoDeDadosHelper.adicionarAluno(aluno.getNome(), aluno.getEmail(), aluno.getSenha());
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
