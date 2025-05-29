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

    private static final String[] DIAS_SEMANA_EN = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    private int convertDiaSemanaToIndex(String diaSemanaTexto) {
        int diaSemanaIndex = -1;
        for (int i = 0; i < DIAS_SEMANA_EN.length; i++) {
            if (DIAS_SEMANA_EN[i].equalsIgnoreCase(diaSemanaTexto)) {
                diaSemanaIndex = i;
                break;
            }
        }
        if (diaSemanaIndex == -1) {
            Log.e("Sync", "Dia da semana inválido no Firebase: " + diaSemanaTexto + ". Usando valor padrão (0).");
            diaSemanaIndex = 0;
        }
        return diaSemanaIndex;
    }

    public void syncWithFirebaseTreino(BancoDeDadosHelper bancoDeDadosHelper, String reference) {
        FirebaseDatabase.getInstance().getReference(reference).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bancoDeDadosHelper.deletarTodosTreinos();

                for (DataSnapshot treinoSnapshot : snapshot.getChildren()) {
                    Treino treino = treinoSnapshot.getValue(Treino.class);
                    if (treino != null) {
                        int diaSemanaIndex = treino.getDiaSemanaIndex();
                        if (diaSemanaIndex < 0 || diaSemanaIndex >= DIAS_SEMANA_EN.length || !DIAS_SEMANA_EN[diaSemanaIndex].equalsIgnoreCase(treino.getDiaSemana())) {
                            diaSemanaIndex = convertDiaSemanaToIndex(treino.getDiaSemana());
                            treino.setDiaSemanaIndex(diaSemanaIndex);
                        }

                        bancoDeDadosHelper.adicionarTreino(treino.getAlunoId(), treino.getNome(), treino.getObservacao(), treino.getDiaSemanaIndex(), treino.getId());
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

    public void sendFirebaseTreino(Treino treino, String refDatabase, BancoDeDadosHelper bancoDeDadosHelper) {
        Treino treinoToSend = new Treino();
        treinoToSend.setId(treino.getId());
        treinoToSend.setAlunoId(treino.getAlunoId());
        treinoToSend.setNome(treino.getNome());
        treinoToSend.setObservacao(treino.getObservacao());
        treinoToSend.setDiaSemana(DIAS_SEMANA_EN[treino.getDiaSemanaIndex()]);
        treinoToSend.setDiaSemanaIndex(treino.getDiaSemanaIndex());
        treinoToSend.setExercicios(treino.getExercicios());

        FirebaseDatabase.getInstance().getReference(refDatabase)
                .push()
                .setValue(treinoToSend);

        FirebaseDatabase.getInstance().getReference(refDatabase).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Treino treino = snapshot.getValue(Treino.class);
                if (treino != null) {
                    int diaSemanaIndex = treino.getDiaSemanaIndex();
                    if (diaSemanaIndex < 0 || diaSemanaIndex >= DIAS_SEMANA_EN.length || !DIAS_SEMANA_EN[diaSemanaIndex].equalsIgnoreCase(treino.getDiaSemana())) {
                        diaSemanaIndex = convertDiaSemanaToIndex(treino.getDiaSemana());
                        treino.setDiaSemanaIndex(diaSemanaIndex);
                    }

                    if (!bancoDeDadosHelper.alunoIdExiste(treino.getAlunoId())) {
                        bancoDeDadosHelper.adicionarTreino(treino.getAlunoId(), treino.getNome(), treino.getObservacao(), treino.getDiaSemanaIndex());
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void syncWithFirebaseAluno(BancoDeDadosHelper bancoDeDadosHelper, String reference) {
        FirebaseDatabase.getInstance().getReference(reference).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bancoDeDadosHelper.deletarTodosAlunos();
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

    public void sendFirebaseAluno(Aluno aluno, String refDatabase, BancoDeDadosHelper bancoDeDadosHelper) {
        FirebaseDatabase.getInstance().getReference(refDatabase)
                .child(String.valueOf(aluno.getId()))
                .setValue(aluno);
        FirebaseDatabase.getInstance().getReference(refDatabase).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Aluno aluno = snapshot.getValue(Aluno.class);
                if (aluno != null) {
                    if (!bancoDeDadosHelper.emailExiste(aluno.getEmail())) {
                        bancoDeDadosHelper.adicionarAluno(aluno.getNome(), aluno.getEmail(), aluno.getSenha());
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void removeFirebaseAluno(int alunoId, String refDatabase, BancoDeDadosHelper bancoDeDadosHelper) {
        DatabaseReference treinosRef = FirebaseDatabase.getInstance().getReference("treinos");
        treinosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> treinoKeysToRemove = new ArrayList<>();
                for (DataSnapshot treinoSnapshot : dataSnapshot.getChildren()) {
                    Treino treino = treinoSnapshot.getValue(Treino.class);
                    if (treino != null && treino.getAlunoId() == alunoId) {
                        String treinoKey = treinoSnapshot.getKey();
                        if (treinoKey != null) {
                            treinoKeysToRemove.add(treinoKey);
                        }
                    }
                }

                if (treinoKeysToRemove.isEmpty()) {
                    FirebaseDatabase.getInstance().getReference(refDatabase)
                            .child(String.valueOf(alunoId))
                            .removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firebase", "Aluno removido com sucesso do Firebase: " + alunoId);
                                bancoDeDadosHelper.deletarAluno(alunoId);
                            })
                            .addOnFailureListener(e -> Log.e("Firebase", "Erro ao remover aluno do Firebase: " + e.getMessage()));
                    return;
                }

                for (String treinoKey : treinoKeysToRemove) {
                    List<Integer> exercicioIdsToRemove = new ArrayList<>();
                    DatabaseReference exerciciosRef = FirebaseDatabase.getInstance().getReference("exercicios");
                    exerciciosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot exerciciosSnapshot) {
                            List<String> exercicioKeysToRemove = new ArrayList<>();
                            for (DataSnapshot exercicioSnapshot : exerciciosSnapshot.getChildren()) {
                                Exercicio exercicio = exercicioSnapshot.getValue(Exercicio.class);
                                if (exercicio != null) {
                                    Treino treino = dataSnapshot.child(treinoKey).getValue(Treino.class);
                                    if (treino != null && exercicio.getTreinoId() == treino.getId()) {
                                        exercicioKeysToRemove.add(exercicioSnapshot.getKey());
                                        exercicioIdsToRemove.add(exercicio.getId());
                                    }
                                }
                            }
                            for (String exercicioKey : exercicioKeysToRemove) {
                                exerciciosRef.child(exercicioKey).removeValue()
                                        .addOnSuccessListener(aVoid -> Log.d("Firebase", "Exercício removido: " + exercicioKey))
                                        .addOnFailureListener(e -> Log.e("Firebase", "Erro ao remover exercício: " + e.getMessage()));
                            }

                            DatabaseReference seriesRef = FirebaseDatabase.getInstance().getReference("series");
                            seriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot seriesSnapshot) {
                                    List<String> seriesKeysToRemove = new ArrayList<>();
                                    for (DataSnapshot serieSnapshot : seriesSnapshot.getChildren()) {
                                        Serie serie = serieSnapshot.getValue(Serie.class);
                                        if (serie != null && exercicioIdsToRemove.contains(serie.getExercicioId())) {
                                            seriesKeysToRemove.add(serieSnapshot.getKey());
                                        }
                                    }
                                    for (String seriesKey : seriesKeysToRemove) {
                                        seriesRef.child(seriesKey).removeValue()
                                                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Série removida: " + seriesKey))
                                                .addOnFailureListener(e -> Log.e("Firebase", "Erro ao remover série: " + e.getMessage()));
                                    }

                                    treinosRef.child(treinoKey).removeValue()
                                            .addOnSuccessListener(aVoid -> Log.d("Firebase", "Treino removido com sucesso: " + treinoKey))
                                            .addOnFailureListener(e -> Log.e("Firebase", "Erro ao remover treino: " + e.getMessage()));

                                    if (treinoKey.equals(treinoKeysToRemove.get(treinoKeysToRemove.size() - 1))) {
                                        FirebaseDatabase.getInstance().getReference(refDatabase)
                                                .child(String.valueOf(alunoId))
                                                .removeValue()
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("Firebase", "Aluno removido com sucesso do Firebase: " + alunoId);
                                                    bancoDeDadosHelper.deletarAluno(alunoId);
                                                })
                                                .addOnFailureListener(e -> Log.e("Firebase", "Erro ao remover aluno do Firebase: " + e.getMessage()));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("Firebase", "Erro ao buscar séries: " + error.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase", "Erro ao buscar exercícios: " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Erro ao buscar treinos: " + error.getMessage());
            }
        });
    }

    public void syncWithFirebaseExercise(BancoDeDadosHelper bancoDeDadosHelper, String reference) {
        FirebaseDatabase.getInstance().getReference(reference).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bancoDeDadosHelper.deletarTodosExercicios();
                for (DataSnapshot exercicioSnapshot : snapshot.getChildren()) {
                    Exercicio exercicio = exercicioSnapshot.getValue(Exercicio.class);
                    if (exercicio != null) {
                        Log.d("SYNC", "Essa merda ainda tenta sincronizar");
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

    public void sendFirebaseExercise(Exercicio exercicio, String refDatabase, BancoDeDadosHelper bancoDeDadosHelper) {
        FirebaseDatabase.getInstance().getReference(refDatabase)
                .push()
                .setValue(exercicio);
        FirebaseDatabase.getInstance().getReference(refDatabase).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Exercicio exercicio = snapshot.getValue(Exercicio.class);
                if (exercicio != null) {
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
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void syncWithFirebaseSerie(BancoDeDadosHelper bancoDeDadosHelper, String reference) {
        FirebaseDatabase.getInstance().getReference(reference).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bancoDeDadosHelper.deletarTodosSeries();
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

    public void sendFirebaseSerie(Serie serie, String refDatabase, BancoDeDadosHelper bancoDeDadosHelper) {
        FirebaseDatabase.getInstance().getReference(refDatabase)
                .push()
                .setValue(serie);
        FirebaseDatabase.getInstance().getReference(refDatabase).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Serie serie = snapshot.getValue(Serie.class);
                if (serie != null) {
                    if (!bancoDeDadosHelper.exercicioIdExiste(serie.getExercicioId())) {
                        bancoDeDadosHelper.adicionarSerie(serie.getExercicioId(), serie.getCarga(), Integer.parseInt(serie.getRepeticoes()));
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void removeTreinoComDependencias(int treinoId, String refTreinos) {
        DatabaseReference treinosRef = FirebaseDatabase.getInstance().getReference(refTreinos);
        treinosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot treinoSnapshot : dataSnapshot.getChildren()) {
                    Treino treino = treinoSnapshot.getValue(Treino.class);
                    if (treino != null) {
                        int diaSemanaIndex = treino.getDiaSemanaIndex();
                        if (diaSemanaIndex < 0 || diaSemanaIndex >= DIAS_SEMANA_EN.length || !DIAS_SEMANA_EN[diaSemanaIndex].equalsIgnoreCase(treino.getDiaSemana())) {
                            diaSemanaIndex = convertDiaSemanaToIndex(treino.getDiaSemana());
                            treino.setDiaSemanaIndex(diaSemanaIndex);
                        }

                        if (treino.getId() == treinoId) {
                            String firebaseKey = treinoSnapshot.getKey();
                            Log.d("Firebase", "Chave Firebase do treino encontrada: " + firebaseKey + " para treinoId: " + treinoId);

                            if (firebaseKey == null) {
                                Log.e("Firebase", "Chave Firebase inválida (null) para treinoId: " + treinoId);
                                return;
                            }

                            List<Integer> exercicioIdsToRemove = new ArrayList<>();
                            DatabaseReference exerciciosRef = FirebaseDatabase.getInstance().getReference("exercicios");
                            exerciciosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot exerciciosSnapshot) {
                                    List<String> exercicioKeysToRemove = new ArrayList<>();
                                    for (DataSnapshot exercicioSnapshot : exerciciosSnapshot.getChildren()) {
                                        Exercicio exercicio = exercicioSnapshot.getValue(Exercicio.class);
                                        if (exercicio != null && exercicio.getTreinoId() == treinoId) {
                                            exercicioKeysToRemove.add(exercicioSnapshot.getKey());
                                            exercicioIdsToRemove.add(exercicio.getId());
                                        }
                                    }
                                    for (String exercicioKey : exercicioKeysToRemove) {
                                        exerciciosRef.child(exercicioKey).removeValue()
                                                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Exercício removido: " + exercicioKey))
                                                .addOnFailureListener(e -> Log.e("Firebase", "Erro ao remover exercício: " + e.getMessage()));
                                    }

                                    DatabaseReference seriesRef = FirebaseDatabase.getInstance().getReference("series");
                                    seriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot seriesSnapshot) {
                                            List<String> seriesKeysToRemove = new ArrayList<>();
                                            for (DataSnapshot serieSnapshot : seriesSnapshot.getChildren()) {
                                                Serie serie = serieSnapshot.getValue(Serie.class);
                                                if (serie != null && exercicioIdsToRemove.contains(serie.getExercicioId())) {
                                                    seriesKeysToRemove.add(serieSnapshot.getKey());
                                                }
                                            }
                                            for (String seriesKey : seriesKeysToRemove) {
                                                seriesRef.child(seriesKey).removeValue()
                                                        .addOnSuccessListener(aVoid -> Log.d("Firebase", "Série removida: " + seriesKey))
                                                        .addOnFailureListener(e -> Log.e("Firebase", "Erro ao remover série: " + e.getMessage()));
                                            }

                                            treinosRef.child(firebaseKey).removeValue()
                                                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Treino removido com sucesso: " + firebaseKey))
                                                    .addOnFailureListener(e -> Log.e("Firebase", "Erro ao remover treino: " + e.getMessage()));
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("Firebase", "Erro ao buscar séries: " + error.getMessage());
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("Firebase", "Erro ao buscar exercícios: " + error.getMessage());
                                }
                            });
                            return;
                        }
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