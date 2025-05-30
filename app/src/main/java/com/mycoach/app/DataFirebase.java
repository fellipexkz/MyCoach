package com.mycoach.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataFirebase {

    private static final String[] DIAS_SEMANA_EN = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    private int convertDiaSemanaToIndex(String diaSemanaTexto) {
        int diaSemanaIndex = -1;
        if (diaSemanaTexto == null) return 0;
        for (int i = 0; i < DIAS_SEMANA_EN.length; i++) {
            if (DIAS_SEMANA_EN[i].equalsIgnoreCase(diaSemanaTexto)) {
                diaSemanaIndex = i;
                break;
            }
        }
        if (diaSemanaIndex == -1) {
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
                    Integer id = treinoSnapshot.child("id").getValue(Integer.class);
                    Integer alunoId = treinoSnapshot.child("alunoId").getValue(Integer.class);
                    String nome = treinoSnapshot.child("nome").getValue(String.class);
                    String observacao = treinoSnapshot.child("observacao").getValue(String.class);
                    String diaSemanaStr = treinoSnapshot.child("diaSemana").getValue(String.class);
                    Integer diaSemanaIndexInt = treinoSnapshot.child("diaSemanaIndex").getValue(Integer.class);

                    if (id == null || alunoId == null) continue;

                    Treino treino = new Treino();
                    treino.setId(id);
                    treino.setAlunoId(alunoId);
                    treino.setNome(nome);
                    treino.setObservacao(observacao);
                    treino.setDiaSemana(diaSemanaStr);

                    int diaSemanaIndex = diaSemanaIndexInt != null ? diaSemanaIndexInt : convertDiaSemanaToIndex(diaSemanaStr);
                    if (diaSemanaIndex < 0 || diaSemanaIndex >= DIAS_SEMANA_EN.length || (treino.getDiaSemana() != null && !DIAS_SEMANA_EN[diaSemanaIndex].equalsIgnoreCase(treino.getDiaSemana()))) {
                        diaSemanaIndex = convertDiaSemanaToIndex(treino.getDiaSemana());
                    }
                    treino.setDiaSemanaIndex(diaSemanaIndex);

                    bancoDeDadosHelper.adicionarTreino(treino.getAlunoId(), treino.getNome(), treino.getObservacao(), treino.getDiaSemanaIndex(), treino.getId());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void sendFirebaseTreino(Treino treino, String refDatabase) {
        Map<String, Object> treinoMap = new HashMap<>();
        treinoMap.put("id", treino.getId());
        treinoMap.put("alunoId", treino.getAlunoId());
        treinoMap.put("nome", treino.getNome());
        treinoMap.put("observacao", treino.getObservacao());
        if (treino.getDiaSemanaIndex() >= 0 && treino.getDiaSemanaIndex() < DIAS_SEMANA_EN.length) {
            treinoMap.put("diaSemana", DIAS_SEMANA_EN[treino.getDiaSemanaIndex()]);
        } else {
            treinoMap.put("diaSemana", DIAS_SEMANA_EN[0]);
        }
        treinoMap.put("diaSemanaIndex", treino.getDiaSemanaIndex());

        List<Map<String, Object>> exerciciosMapList = new ArrayList<>();
        if (treino.getExercicios() != null) {
            for (Exercicio exercicio : treino.getExercicios()) {
                Map<String, Object> exercicioMap = new HashMap<>();
                exercicioMap.put("id", exercicio.getId());
                exercicioMap.put("treinoId", exercicio.getTreinoId());
                exercicioMap.put("nome", exercicio.getNome());
                exercicioMap.put("tempoDescanso", exercicio.getTempoDescanso());
                exercicioMap.put("series", converterListaSeriesParaMap(exercicio.getSeries()));
                exerciciosMapList.add(exercicioMap);
            }
        }
        treinoMap.put("exercicios", exerciciosMapList);

        FirebaseDatabase.getInstance().getReference(refDatabase)
                .push()
                .setValue(treinoMap)
                .addOnSuccessListener(aVoid -> {})
                .addOnFailureListener(e -> {});
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
            }
        });
    }

    public void sendFirebaseAluno(Aluno aluno, String refDatabase, BancoDeDadosHelper bancoDeDadosHelper) {
        FirebaseDatabase.getInstance().getReference(refDatabase)
                .child(String.valueOf(aluno.getId()))
                .setValue(aluno)
                .addOnSuccessListener(aVoid -> {})
                .addOnFailureListener(e -> {});

        FirebaseDatabase.getInstance().getReference(refDatabase).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Aluno alunoFirebase = snapshot.getValue(Aluno.class);
                if (alunoFirebase != null) {
                    if (!bancoDeDadosHelper.emailExiste(alunoFirebase.getEmail())) {
                        bancoDeDadosHelper.adicionarAluno(alunoFirebase.getId(), alunoFirebase.getNome(), alunoFirebase.getEmail(), alunoFirebase.getSenha());
                    }
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Integer alunoId = snapshot.child("id").getValue(Integer.class);
                if (alunoId != null) {
                    bancoDeDadosHelper.deletarAluno(alunoId);
                }
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void removeFirebaseAluno(int alunoId, String refDatabase) {
        DatabaseReference treinosRef = FirebaseDatabase.getInstance().getReference("treinos");
        treinosRef.orderByChild("alunoId").equalTo(alunoId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotTreinos) {
                if (!dataSnapshotTreinos.exists()) {
                    FirebaseDatabase.getInstance().getReference(refDatabase)
                            .child(String.valueOf(alunoId))
                            .removeValue()
                            .addOnSuccessListener(aVoid -> {})
                            .addOnFailureListener(e -> {});
                    return;
                }

                List<String> treinoKeysParaRemover = new ArrayList<>();
                List<Integer> treinoIdsParaRemoverExercicios = new ArrayList<>();

                for (DataSnapshot treinoSnapshot : dataSnapshotTreinos.getChildren()) {
                    String key = treinoSnapshot.getKey();
                    if (key != null) {
                        treinoKeysParaRemover.add(key);
                    }
                    Integer idTreino = treinoSnapshot.child("id").getValue(Integer.class);
                    if (idTreino != null) {
                        treinoIdsParaRemoverExercicios.add(idTreino);
                    }
                }

                final int[] treinosDependentesProcessados = {0};
                int totalTreinosDependentes = treinoKeysParaRemover.size();

                if (totalTreinosDependentes == 0) {
                    FirebaseDatabase.getInstance().getReference(refDatabase)
                            .child(String.valueOf(alunoId))
                            .removeValue()
                            .addOnSuccessListener(aVoid -> {})
                            .addOnFailureListener(e -> {});
                    return;
                }

                for (int i = 0; i < treinoKeysParaRemover.size(); i++) {
                    final String treinoKey = treinoKeysParaRemover.get(i);
                    final int idDoTreino = treinoIdsParaRemoverExercicios.get(i);

                    List<Integer> exercicioIdsParaRemoverSeries = new ArrayList<>();
                    DatabaseReference exerciciosRef = FirebaseDatabase.getInstance().getReference("exercicios");

                    exerciciosRef.orderByChild("treinoId").equalTo(idDoTreino).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot exerciciosSnapshot) {
                            List<String> exercicioKeysFbParaRemover = new ArrayList<>();
                            for (DataSnapshot exercicioSnapshotLoop : exerciciosSnapshot.getChildren()) {
                                exercicioKeysFbParaRemover.add(exercicioSnapshotLoop.getKey());
                                Integer idExercicio = exercicioSnapshotLoop.child("id").getValue(Integer.class);
                                if (idExercicio != null) {
                                    exercicioIdsParaRemoverSeries.add(idExercicio);
                                }
                            }
                            for (String exercicioKeyFb : exercicioKeysFbParaRemover) {
                                exerciciosRef.child(exercicioKeyFb).removeValue();
                            }

                            DatabaseReference seriesRef = FirebaseDatabase.getInstance().getReference("series");
                            if (!exercicioIdsParaRemoverSeries.isEmpty()) {
                                seriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot seriesSnapshot) {
                                        List<String> seriesKeysFbParaRemover = new ArrayList<>();
                                        for (DataSnapshot serieLoopSnapshot : seriesSnapshot.getChildren()) {
                                            Integer exercicioIdDaSerie = serieLoopSnapshot.child("exercicioId").getValue(Integer.class);
                                            if (exercicioIdDaSerie != null && exercicioIdsParaRemoverSeries.contains(exercicioIdDaSerie)) {
                                                seriesKeysFbParaRemover.add(serieLoopSnapshot.getKey());
                                            }
                                        }
                                        for (String serieKeyFb : seriesKeysFbParaRemover) {
                                            seriesRef.child(serieKeyFb).removeValue();
                                        }
                                        if (treinoKey != null) {
                                            treinosRef.child(treinoKey).removeValue();
                                        }

                                        treinosDependentesProcessados[0]++;
                                        if (treinosDependentesProcessados[0] == totalTreinosDependentes) {
                                            FirebaseDatabase.getInstance().getReference(refDatabase)
                                                    .child(String.valueOf(alunoId))
                                                    .removeValue()
                                                    .addOnSuccessListener(aVoid -> {})
                                                    .addOnFailureListener(e -> {});
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            } else {
                                if (treinoKey != null) {
                                    treinosRef.child(treinoKey).removeValue();
                                }
                                treinosDependentesProcessados[0]++;
                                if (treinosDependentesProcessados[0] == totalTreinosDependentes) {
                                    FirebaseDatabase.getInstance().getReference(refDatabase)
                                            .child(String.valueOf(alunoId))
                                            .removeValue()
                                            .addOnSuccessListener(aVoid -> {})
                                            .addOnFailureListener(e -> {});
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private List<Map<String, Object>> converterListaSeriesParaMap(List<Serie> listaDeSeries) {
        List<Map<String, Object>> listaDeMapasDeSeries = new ArrayList<>();
        if (listaDeSeries != null) {
            for (Serie serie : listaDeSeries) {
                Map<String, Object> serieMap = new HashMap<>();
                serieMap.put("carga", serie.getCarga());
                serieMap.put("repeticoes", serie.getRepeticoes());
                listaDeMapasDeSeries.add(serieMap);
            }
        }
        return listaDeMapasDeSeries;
    }

    public void syncWithFirebaseExercise(BancoDeDadosHelper bancoDeDadosHelper, String reference) {
        FirebaseDatabase.getInstance().getReference(reference).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bancoDeDadosHelper.deletarTodosExercicios();
                for (DataSnapshot exercicioSnapshot : snapshot.getChildren()) {
                    Exercicio exercicio = new Exercicio();
                    if (exercicioSnapshot.hasChild("id")) {
                        Integer id = exercicioSnapshot.child("id").getValue(Integer.class);
                        if (id != null) exercicio.setId(id);
                    }
                    if (exercicioSnapshot.hasChild("treinoId")) {
                        Integer treinoId = exercicioSnapshot.child("treinoId").getValue(Integer.class);
                        if (treinoId != null) exercicio.setTreinoId(treinoId);
                    }
                    if (exercicioSnapshot.hasChild("nome")) {
                        exercicio.setNome(exercicioSnapshot.child("nome").getValue(String.class));
                    }
                    if (exercicioSnapshot.hasChild("tempoDescanso")) {
                        exercicio.setTempoDescanso(exercicioSnapshot.child("tempoDescanso").getValue(String.class));
                    }

                    List<Serie> seriesDoExercicio = new ArrayList<>();
                    if (exercicioSnapshot.hasChild("series")) {
                        DataSnapshot seriesArraySnapshot = exercicioSnapshot.child("series");
                        for (DataSnapshot serieMapSnapshot : seriesArraySnapshot.getChildren()) {
                            Serie serie = new Serie();
                            serie.setExercicioId(exercicio.getId());
                            if (serieMapSnapshot.hasChild("carga")) {
                                serie.setCarga(serieMapSnapshot.child("carga").getValue(String.class));
                            }
                            if (serieMapSnapshot.hasChild("repeticoes")) {
                                Object repeticoesValue = serieMapSnapshot.child("repeticoes").getValue();
                                if (repeticoesValue != null) {
                                    serie.setRepeticoes(String.valueOf(repeticoesValue));
                                }
                            }
                            seriesDoExercicio.add(serie);
                        }
                    }
                    exercicio.setSeries(seriesDoExercicio);

                    if (exercicio.getId() != 0 && exercicio.getTreinoId() != 0) {
                        bancoDeDadosHelper.adicionarExercicio(exercicio.getTreinoId(), exercicio.getNome(), exercicio.getTempoDescanso(), exercicio.getId());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void sendFirebaseExercise(Exercicio exercicio, String refDatabase) {
        DatabaseReference exerciciosRef = FirebaseDatabase.getInstance().getReference(refDatabase);
        String firebasePushId = exerciciosRef.push().getKey();

        Map<String, Object> exercicioMap = new HashMap<>();
        exercicioMap.put("id", exercicio.getId());
        exercicioMap.put("treinoId", exercicio.getTreinoId());
        exercicioMap.put("nome", exercicio.getNome());
        exercicioMap.put("tempoDescanso", exercicio.getTempoDescanso());
        exercicioMap.put("series", converterListaSeriesParaMap(exercicio.getSeries()));

        if (firebasePushId != null) {
            exerciciosRef.child(firebasePushId).setValue(exercicioMap)
                    .addOnSuccessListener(aVoid -> {})
                    .addOnFailureListener(e -> {});
        }
    }

    public void syncWithFirebaseSerie(BancoDeDadosHelper bancoDeDadosHelper, String reference) {
        FirebaseDatabase.getInstance().getReference(reference).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bancoDeDadosHelper.deletarTodosSeries();
                for (DataSnapshot serieSnapshot : snapshot.getChildren()) {
                    Serie serie = new Serie();
                    if (serieSnapshot.hasChild("exercicioId")) {
                        Integer exercicioId = serieSnapshot.child("exercicioId").getValue(Integer.class);
                        if (exercicioId != null) serie.setExercicioId(exercicioId);
                    }
                    if (serieSnapshot.hasChild("carga")) {
                        serie.setCarga(serieSnapshot.child("carga").getValue(String.class));
                    }
                    if (serieSnapshot.hasChild("repeticoes")) {
                        Object repeticoesValue = serieSnapshot.child("repeticoes").getValue();
                        if (repeticoesValue != null) {
                            serie.setRepeticoes(String.valueOf(repeticoesValue));
                        }
                    }
                    if (serie.getExercicioId() != 0 && serie.getRepeticoes() != null && !serie.getRepeticoes().isEmpty()) {
                        int repeticoesInt;
                        try {
                            repeticoesInt = Integer.parseInt(serie.getRepeticoes());
                        } catch (NumberFormatException e) {
                            repeticoesInt = 0;
                        }
                        bancoDeDadosHelper.adicionarSerie(serie.getExercicioId(), serie.getCarga(), repeticoesInt);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void sendFirebaseSerie(Serie serie, String refDatabase) {
        DatabaseReference firebaseSeriesRef = FirebaseDatabase.getInstance().getReference(refDatabase);
        String firebasePushId = firebaseSeriesRef.push().getKey();

        Map<String, Object> serieMap = new HashMap<>();
        serieMap.put("exercicioId", serie.getExercicioId());
        serieMap.put("carga", serie.getCarga());
        serieMap.put("repeticoes", serie.getRepeticoes());

        if (firebasePushId != null) {
            firebaseSeriesRef.child(firebasePushId).setValue(serieMap)
                    .addOnSuccessListener(aVoid -> {})
                    .addOnFailureListener(e -> {});
        }
    }

    public void removeTreinoComDependencias(int treinoIdParaRemover, String refTreinos) {
        DatabaseReference treinosRef = FirebaseDatabase.getInstance().getReference(refTreinos);
        treinosRef.orderByChild("id").equalTo(treinoIdParaRemover).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotTreinos) {
                if (!dataSnapshotTreinos.exists()) {
                    return;
                }
                for (DataSnapshot treinoSnapshot : dataSnapshotTreinos.getChildren()) {
                    final String firebaseKeyTreino = treinoSnapshot.getKey();

                    List<Integer> exercicioIdsParaRemoverSeries = new ArrayList<>();
                    DatabaseReference exerciciosRef = FirebaseDatabase.getInstance().getReference("exercicios");

                    exerciciosRef.orderByChild("treinoId").equalTo(treinoIdParaRemover).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot exerciciosSnapshot) {
                            List<String> exercicioKeysFbParaRemover = new ArrayList<>();
                            for (DataSnapshot exercicioLoopSnapshot : exerciciosSnapshot.getChildren()) {
                                exercicioKeysFbParaRemover.add(exercicioLoopSnapshot.getKey());
                                Integer idExercicio = exercicioLoopSnapshot.child("id").getValue(Integer.class);
                                if (idExercicio != null) {
                                    exercicioIdsParaRemoverSeries.add(idExercicio);
                                }
                            }
                            for (String exercicioKeyFb : exercicioKeysFbParaRemover) {
                                exerciciosRef.child(exercicioKeyFb).removeValue();
                            }

                            DatabaseReference seriesRef = FirebaseDatabase.getInstance().getReference("series");
                            if (!exercicioIdsParaRemoverSeries.isEmpty()) {
                                seriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot seriesSnapshot) {
                                        List<String> seriesKeysFbParaRemover = new ArrayList<>();
                                        for (DataSnapshot serieLoopSnapshot : seriesSnapshot.getChildren()) {
                                            Integer exercicioIdDaSerie = serieLoopSnapshot.child("exercicioId").getValue(Integer.class);
                                            if (exercicioIdDaSerie != null && exercicioIdsParaRemoverSeries.contains(exercicioIdDaSerie)) {
                                                seriesKeysFbParaRemover.add(serieLoopSnapshot.getKey());
                                            }
                                        }
                                        for (String serieKeyFb : seriesKeysFbParaRemover) {
                                            seriesRef.child(serieKeyFb).removeValue();
                                        }
                                        if (firebaseKeyTreino != null) {
                                            treinosRef.child(firebaseKeyTreino).removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            } else {
                                if (firebaseKeyTreino != null) {
                                    treinosRef.child(firebaseKeyTreino).removeValue();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}