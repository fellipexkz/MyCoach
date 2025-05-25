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

import java.util.ArrayList;
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
                        bancoDeDadosHelper.adicionarTreino(treino.getAlunoId(), treino.getNome(), treino.getObservacao(), treino.getDiaSemana(), treino.getId());
                        Log.d("Aluno_Id:", String.valueOf(treino.getAlunoId()));
                        Log.d("Nome:", treino.getNome());
                        Log.d("id:", String.valueOf(treino.getId()));
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
                        bancoDeDadosHelper.adicionarTreino(treino.getAlunoId(), treino.getNome(), treino.getObservacao(), treino.getDiaSemana());
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

    public void removeFirebaseTreino(int treinoId, String refDatabase) {
        String[] parts = refDatabase.split("/");
        if (parts.length > 1) {
            String alunoIdStr = parts[parts.length - 2];
            int alunoId = Integer.parseInt(alunoIdStr);
            FirebaseDatabase.getInstance().getReference(refDatabase)
                    .child(String.valueOf(treinoId))
                    .removeValue()
                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Treino removido com sucesso do Firebase: treinoId " + treinoId + ", alunoId " + alunoId))
                    .addOnFailureListener(e -> Log.e("Firebase", "Erro ao remover treino do Firebase: " + e.getMessage()));
        } else {
            Log.e("Firebase", "Referência inválida para remoção: " + refDatabase);
        }
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
                        bancoDeDadosHelper.adicionarAluno(aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getSenha());
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

    public void removeFirebaseAluno(int alunoId, String refDatabase) {
        FirebaseDatabase.getInstance().getReference(refDatabase)
                .child(String.valueOf(alunoId))
                .removeValue()
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Aluno removido com sucesso do Firebase: " + alunoId))
                .addOnFailureListener(e -> Log.e("Firebase", "Erro ao remover aluno do Firebase: " + e.getMessage()));
    }

    // Exercícios
    public void syncWithFirebaseExercise(BancoDeDadosHelper bancoDeDadosHelper, String reference)
    {
        FirebaseDatabase.getInstance().getReference(reference).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Apagar registros locais
                bancoDeDadosHelper.deletarTodosExercicios();

                // Inserir novos
                for (DataSnapshot exercicioSnapshot : snapshot.getChildren()) {
                    Exercicio exercicio = exercicioSnapshot.getValue(Exercicio.class);
                    if (exercicio != null) {
                        Log.d("SYNC","Essa merda ainda tenta sincronizar");
                        Log.d("Exercicio 1", String.valueOf(exercicio.getTempoDescanso()));
                        Log.d("Exercicio 2", String.valueOf(exercicio.getTreinoId()));
                        Log.d("Exercicio 3", String.valueOf(exercicio.getNome()));
                        Log.d("Exercicio 4", String.valueOf(exercicio.getSeries()));
                        Log.d("Exercicio 5", String.valueOf(exercicio.getId()));
                        bancoDeDadosHelper.adicionarExercicio(exercicio.getTreinoId(), exercicio.getNome(), exercicio.getTempoDescanso(), exercicio.getId());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Sync", "Erro ao sincronizar com Firebase: " + error.getMessage());
            }
        });
    }
    public void sendFirebaseExercise(Exercicio exercicio, String refDatabase, BancoDeDadosHelper bancoDeDadosHelper)
    {
        FirebaseDatabase.getInstance().getReference(refDatabase)
                .push() // ou .child(email.replace(".", "_")) se quiser um ID fixo
                .setValue(exercicio);
        FirebaseDatabase.getInstance().getReference(refDatabase).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Exercicio exercicio = snapshot.getValue(Exercicio.class);
                if (exercicio != null) {
                    // Verifique se já existe no SQLite antes de inserir
                    if (!bancoDeDadosHelper.treinoIdExiste(exercicio.getTreinoId())) {
                        bancoDeDadosHelper.adicionarExercicio(exercicio.getTreinoId(), exercicio.getNome(), exercicio.getTempoDescanso(), exercicio.getId());
                        Log.d("Exercicio = Treino_id:", String.valueOf(exercicio.getTreinoId()));
                        Log.d("Exercicio = Id:", String.valueOf(exercicio.getId()));
                        Log.d("Exercicio = Exercicio_id:", exercicio.getNome());
                        Log.d("Exercicio = Carga:", exercicio.getTempoDescanso());
                        Log.d("Exercicio = Series:", String.valueOf(exercicio.getSeries()));
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
    // Series
    public void syncWithFirebaseSerie(BancoDeDadosHelper bancoDeDadosHelper, String reference)
    {
        FirebaseDatabase.getInstance().getReference(reference).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Apagar registros locais
                bancoDeDadosHelper.deletarTodosSeries();

                // Inserir novos
                for (DataSnapshot serieSnapshot : snapshot.getChildren()) {
                    Serie serie = serieSnapshot.getValue(Serie.class);
                    if (serie != null) {
                        bancoDeDadosHelper.adicionarSerie(serie.getExercicioId(), serie.getCarga(), Integer.parseInt(serie.getRepeticoes()));
                        Log.d("Série 1:", String.valueOf(serie.getExercicioId()));
                        Log.d("Série 2:", String.valueOf(serie.getCarga()));
                        Log.d("Série 3:", String.valueOf(serie.getRepeticoes()));
                        Log.d("Série 4:", String.valueOf(serie.getId()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Sync", "Erro ao sincronizar com Firebase: " + error.getMessage());
            }
        });
    }
    public void sendFirebaseSerie(Serie serie, String refDatabase, BancoDeDadosHelper bancoDeDadosHelper)
    {
        FirebaseDatabase.getInstance().getReference(refDatabase)
                .push() // ou .child(email.replace(".", "_")) se quiser um ID fixo
                .setValue(serie);
        FirebaseDatabase.getInstance().getReference(refDatabase).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Serie serie = snapshot.getValue(Serie.class);
                if (serie != null) {
                    // Verifique se já existe no SQLite antes de inserir
                    if (!bancoDeDadosHelper.exercicioIdExiste(serie.getExercicioId())) {
                        bancoDeDadosHelper.adicionarSerie(serie.getExercicioId(), serie.getCarga(), Integer.parseInt(serie.getRepeticoes()));
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

    public void removeTreinoComDependencias(int treinoId, String refTreinos) {
        DatabaseReference treinosRef = FirebaseDatabase.getInstance().getReference(refTreinos);
        treinosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot treinoSnapshot : dataSnapshot.getChildren()) {
                    Treino treino = treinoSnapshot.getValue(Treino.class);
                    if (treino != null && treino.getId() == treinoId) {
                        String firebaseKey = treinoSnapshot.getKey();
                        Log.d("Firebase", "Chave Firebase do treino encontrada: " + firebaseKey + " para treinoId: " + treinoId);

                        DatabaseReference seriesRef = FirebaseDatabase.getInstance().getReference("series");
                        seriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot seriesSnapshot) {
                                List<String> seriesKeysToRemove = new ArrayList<>();
                                for (DataSnapshot serieSnapshot : seriesSnapshot.getChildren()) {
                                    Serie serie = serieSnapshot.getValue(Serie.class);
                                    if (serie != null && treino.getExercicios() != null) {
                                        for (Exercicio exercicio : treino.getExercicios()) {
                                            if (serie.getExercicioId() == exercicio.getId()) {
                                                seriesKeysToRemove.add(serieSnapshot.getKey());
                                                break;
                                            }
                                        }
                                    }
                                }
                                for (String seriesKey : seriesKeysToRemove) {
                                    seriesRef.child(seriesKey).removeValue()
                                            .addOnSuccessListener(aVoid -> Log.d("Firebase", "Série removida: " + seriesKey))
                                            .addOnFailureListener(e -> Log.e("Firebase", "Erro ao remover série: " + e.getMessage()));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("Firebase", "Erro ao buscar séries: " + error.getMessage());
                            }
                        });

                        DatabaseReference exerciciosRef = FirebaseDatabase.getInstance().getReference("exercicios");
                        exerciciosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot exerciciosSnapshot) {
                                List<String> exercicioKeysToRemove = new ArrayList<>();
                                for (DataSnapshot exercicioSnapshot : exerciciosSnapshot.getChildren()) {
                                    Exercicio exercicio = exercicioSnapshot.getValue(Exercicio.class);
                                    if (exercicio != null && exercicio.getTreinoId() == treinoId) {
                                        exercicioKeysToRemove.add(exercicioSnapshot.getKey());
                                    }
                                }
                                for (String exercicioKey : exercicioKeysToRemove) {
                                    exerciciosRef.child(exercicioKey).removeValue()
                                            .addOnSuccessListener(aVoid -> Log.d("Firebase", "Exercício removido: " + exercicioKey))
                                            .addOnFailureListener(e -> Log.e("Firebase", "Erro ao remover exercício: " + e.getMessage()));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("Firebase", "Erro ao buscar exercícios: " + error.getMessage());
                            }
                        });

                        treinosRef.child(firebaseKey).removeValue()
                                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Treino removido com sucesso: " + firebaseKey))
                                .addOnFailureListener(e -> Log.e("Firebase", "Erro ao remover treino: " + e.getMessage()));
                        return;
                    }
                }
                Log.e("Firebase", "Treino com ID " + treinoId + " não encontrado no Firebase");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Erro ao buscar treino: " + error.getMessage());
            }
        });
    }
}
