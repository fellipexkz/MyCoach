package com.mycoach.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class BancoDeDadosHelper extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "mycoach.db";
    private static final int VERSAO_BANCO = 2;

    private static final String TABELA_PERSONAL = "personal";
    private static final String COLUNA_PERSONAL_ID = "id";
    private static final String COLUNA_PERSONAL_EMAIL = "email";
    private static final String COLUNA_PERSONAL_SENHA = "senha";

    private static final String TABELA_ALUNOS = "alunos";
    private static final String COLUNA_ALUNO_ID = "id";
    private static final String COLUNA_ALUNO_NOME = "nome";
    private static final String COLUNA_ALUNO_EMAIL = "email";
    private static final String COLUNA_ALUNO_SENHA = "senha";

    private static final String TABELA_TREINOS = "treinos";
    private static final String COLUNA_TREINO_ID = "id";
    private static final String COLUNA_TREINO_ALUNO_ID = "aluno_id";
    private static final String COLUNA_TREINO_NOME = "nome";
    private static final String COLUNA_TREINO_OBSERVACAO = "observacao";
    private static final String COLUNA_TREINO_DIA_SEMANA = "dia_semana";

    private static final String TABELA_EXERCICIOS = "exercicios";
    private static final String COLUNA_EXERCICIO_ID = "id";
    private static final String COLUNA_EXERCICIO_TREINO_ID = "treino_id";
    private static final String COLUNA_EXERCICIO_NOME = "nome";
    private static final String COLUNA_EXERCICIO_TEMPO_DESCANSO = "tempo_descanso";

    private static final String TABELA_SERIES = "series";
    private static final String COLUNA_SERIE_ID = "id";
    private static final String COLUNA_SERIE_EXERCICIO_ID = "exercicio_id";
    private static final String COLUNA_SERIE_CARGA = "carga";
    private static final String COLUNA_SERIE_REPETICOES = "repeticoes";

    public BancoDeDadosHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String criarTabelaPersonal = "CREATE TABLE " + TABELA_PERSONAL + " (" +
                COLUNA_PERSONAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUNA_PERSONAL_EMAIL + " TEXT NOT NULL UNIQUE, " +
                COLUNA_PERSONAL_SENHA + " TEXT NOT NULL)";
        db.execSQL(criarTabelaPersonal);

        ContentValues valoresPersonal = new ContentValues();
        valoresPersonal.put(COLUNA_PERSONAL_EMAIL, "personal@mail.com");
        valoresPersonal.put(COLUNA_PERSONAL_SENHA, "admin");
        db.insert(TABELA_PERSONAL, null, valoresPersonal);

        String criarTabelaAlunos = "CREATE TABLE " + TABELA_ALUNOS + " (" +
                COLUNA_ALUNO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUNA_ALUNO_NOME + " TEXT NOT NULL, " +
                COLUNA_ALUNO_EMAIL + " TEXT NOT NULL UNIQUE, " +
                COLUNA_ALUNO_SENHA + " TEXT NOT NULL)";
        db.execSQL(criarTabelaAlunos);

        String criarTabelaTreinos = "CREATE TABLE " + TABELA_TREINOS + " (" +
                COLUNA_TREINO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUNA_TREINO_ALUNO_ID + " INTEGER NOT NULL, " +
                COLUNA_TREINO_NOME + " TEXT NOT NULL, " +
                COLUNA_TREINO_OBSERVACAO + " TEXT, " +
                COLUNA_TREINO_DIA_SEMANA + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + COLUNA_TREINO_ALUNO_ID + ") REFERENCES " + TABELA_ALUNOS + "(" + COLUNA_ALUNO_ID + "))";
        db.execSQL(criarTabelaTreinos);

        String criarTabelaExercicios = "CREATE TABLE " + TABELA_EXERCICIOS + " (" +
                COLUNA_EXERCICIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUNA_EXERCICIO_TREINO_ID + " INTEGER NOT NULL, " +
                COLUNA_EXERCICIO_NOME + " TEXT NOT NULL, " +
                COLUNA_EXERCICIO_TEMPO_DESCANSO + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + COLUNA_EXERCICIO_TREINO_ID + ") REFERENCES " + TABELA_TREINOS + "(" + COLUNA_TREINO_ID + "))";
        db.execSQL(criarTabelaExercicios);

        String criarTabelaSeries = "CREATE TABLE " + TABELA_SERIES + " (" +
                COLUNA_SERIE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUNA_SERIE_EXERCICIO_ID + " INTEGER NOT NULL, " +
                COLUNA_SERIE_CARGA + " TEXT NOT NULL, " +
                COLUNA_SERIE_REPETICOES + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + COLUNA_SERIE_EXERCICIO_ID + ") REFERENCES " + TABELA_EXERCICIOS + "(" + COLUNA_EXERCICIO_ID + "))";
        db.execSQL(criarTabelaSeries);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versaoAntiga, int novaVersao) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_PERSONAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_ALUNOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_TREINOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_EXERCICIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_SERIES);
        onCreate(db);
    }

    public boolean adicionarPersonal(String email, String senha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COLUNA_PERSONAL_EMAIL, email);
        valores.put(COLUNA_PERSONAL_SENHA, senha);

        long resultado = db.insert(TABELA_PERSONAL, null, valores);
        db.close();
        return resultado != -1;
    }

    public boolean verificarPersonal(String email, String senha) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABELA_PERSONAL + " WHERE " + COLUNA_PERSONAL_EMAIL + " = ? AND " + COLUNA_PERSONAL_SENHA + " = ?",
                new String[]{email, senha}
        );

        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return existe;
    }

    public boolean verificarAluno(String email, String senha) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABELA_ALUNOS + " WHERE " + COLUNA_ALUNO_EMAIL + " = ? AND " + COLUNA_ALUNO_SENHA + " = ?",
                new String[]{email, senha}
        );

        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return existe;
    }

    public int obterIdAlunoPorEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUNA_ALUNO_ID + " FROM " + TABELA_ALUNOS + " WHERE " + COLUNA_ALUNO_EMAIL + " = ?",
                new String[]{email}
        );

        int idAluno = -1;
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(COLUNA_ALUNO_ID);
            if (columnIndex >= 0) {
                idAluno = cursor.getInt(columnIndex);
            } else {
                throw new IllegalStateException("Coluna " + COLUNA_ALUNO_ID + " não encontrada no resultado da consulta.");
            }
        }

        cursor.close();
        db.close();
        return idAluno;
    }

    public List<Aluno> obterTodosAlunos() {
        List<Aluno> alunos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABELA_ALUNOS, null);

        if (cursor.moveToFirst()) {
            do {
                Aluno aluno = new Aluno();
                int idIndex = cursor.getColumnIndex(COLUNA_ALUNO_ID);
                int nomeIndex = cursor.getColumnIndex(COLUNA_ALUNO_NOME);
                int emailIndex = cursor.getColumnIndex(COLUNA_ALUNO_EMAIL);
                int senhaIndex = cursor.getColumnIndex(COLUNA_ALUNO_SENHA);

                if (idIndex >= 0) {
                    aluno.setId(cursor.getInt(idIndex));
                } else {
                    throw new IllegalStateException("Coluna " + COLUNA_ALUNO_ID + " não encontrada no resultado da consulta.");
                }
                if (nomeIndex >= 0) {
                    aluno.setNome(cursor.getString(nomeIndex));
                } else {
                    throw new IllegalStateException("Coluna " + COLUNA_ALUNO_NOME + " não encontrada no resultado da consulta.");
                }
                if (emailIndex >= 0) {
                    aluno.setEmail(cursor.getString(emailIndex));
                } else {
                    throw new IllegalStateException("Coluna " + COLUNA_ALUNO_EMAIL + " não encontrada no resultado da consulta.");
                }
                if (senhaIndex >= 0) {
                    aluno.setSenha(cursor.getString(senhaIndex));
                } else {
                    throw new IllegalStateException("Coluna " + COLUNA_ALUNO_SENHA + " não encontrada no resultado da consulta.");
                }

                alunos.add(aluno);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return alunos;
    }

    public boolean adicionarAluno(String nome, String email, String senha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COLUNA_ALUNO_NOME, nome);
        valores.put(COLUNA_ALUNO_EMAIL, email);
        valores.put(COLUNA_ALUNO_SENHA, senha);

        long resultado = db.insert(TABELA_ALUNOS, null, valores);
        db.close();
        return resultado != -1;
    }

    public Aluno obterAlunoPorId(int idAluno) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABELA_ALUNOS + " WHERE " + COLUNA_ALUNO_ID + " = ?", new String[]{String.valueOf(idAluno)});

        Aluno aluno = null;
        if (cursor.moveToFirst()) {
            aluno = new Aluno();
            int idIndex = cursor.getColumnIndex(COLUNA_ALUNO_ID);
            int nomeIndex = cursor.getColumnIndex(COLUNA_ALUNO_NOME);
            int emailIndex = cursor.getColumnIndex(COLUNA_ALUNO_EMAIL);
            int senhaIndex = cursor.getColumnIndex(COLUNA_ALUNO_SENHA);

            if (idIndex >= 0) {
                aluno.setId(cursor.getInt(idIndex));
            } else {
                throw new IllegalStateException("Coluna " + COLUNA_ALUNO_ID + " não encontrada no resultado da consulta.");
            }
            if (nomeIndex >= 0) {
                aluno.setNome(cursor.getString(nomeIndex));
            } else {
                throw new IllegalStateException("Coluna " + COLUNA_ALUNO_NOME + " não encontrada no resultado da consulta.");
            }
            if (emailIndex >= 0) {
                aluno.setEmail(cursor.getString(emailIndex));
            } else {
                throw new IllegalStateException("Coluna " + COLUNA_ALUNO_EMAIL + " não encontrada no resultado da consulta.");
            }
            if (senhaIndex >= 0) {
                aluno.setSenha(cursor.getString(senhaIndex));
            } else {
                throw new IllegalStateException("Coluna " + COLUNA_ALUNO_SENHA + " não encontrada no resultado da consulta.");
            }
        }

        cursor.close();
        db.close();
        return aluno;
    }

    public long adicionarTreino(int alunoId, String nome, String observacao, String diaSemana) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUNA_TREINO_ALUNO_ID, alunoId);
        values.put(COLUNA_TREINO_NOME, nome);
        values.put(COLUNA_TREINO_OBSERVACAO, observacao);
        values.put(COLUNA_TREINO_DIA_SEMANA, diaSemana);

        long result = db.insert(TABELA_TREINOS, null, values);
        db.close();
        return result;
    }

    public long adicionarExercicio(int treinoId, String nome, String tempoDescanso) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUNA_EXERCICIO_TREINO_ID, treinoId);
        values.put(COLUNA_EXERCICIO_NOME, nome);
        values.put(COLUNA_EXERCICIO_TEMPO_DESCANSO, tempoDescanso);

        long result = db.insert(TABELA_EXERCICIOS, null, values);
        db.close();
        return result;
    }

    public long adicionarSerie(int exercicioId, String carga, int repeticoes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUNA_SERIE_EXERCICIO_ID, exercicioId);
        values.put(COLUNA_SERIE_CARGA, carga);
        values.put(COLUNA_SERIE_REPETICOES, repeticoes);

        long result = db.insert(TABELA_SERIES, null, values);
        db.close();
        return result;
    }

    public List<Treino> obterTreinosPorAlunoId(int idAluno) {
        List<Treino> listaTreinos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorTreinos = db.rawQuery(
                "SELECT * FROM " + TABELA_TREINOS + " WHERE " + COLUNA_TREINO_ALUNO_ID + " = ?",
                new String[]{String.valueOf(idAluno)}
        );

        Log.d("BancoDeDadosHelper", "Consultando treinos para aluno ID: " + idAluno);

        if (cursorTreinos.moveToFirst()) {
            do {
                Treino treino = new Treino();
                int idIndex = cursorTreinos.getColumnIndex(COLUNA_TREINO_ID);
                int alunoIdIndex = cursorTreinos.getColumnIndex(COLUNA_TREINO_ALUNO_ID);
                int nomeIndex = cursorTreinos.getColumnIndex(COLUNA_TREINO_NOME);
                int observacaoIndex = cursorTreinos.getColumnIndex(COLUNA_TREINO_OBSERVACAO);
                int diaSemanaIndex = cursorTreinos.getColumnIndex(COLUNA_TREINO_DIA_SEMANA);

                if (idIndex >= 0) {
                    treino.setId(cursorTreinos.getInt(idIndex));
                } else {
                    throw new IllegalStateException("Coluna " + COLUNA_TREINO_ID + " não encontrada no resultado da consulta.");
                }
                if (alunoIdIndex >= 0) {
                    treino.setAlunoId(cursorTreinos.getInt(alunoIdIndex));
                } else {
                    throw new IllegalStateException("Coluna " + COLUNA_TREINO_ALUNO_ID + " não encontrada no resultado da consulta.");
                }
                if (nomeIndex >= 0) {
                    treino.setNome(cursorTreinos.getString(nomeIndex));
                } else {
                    throw new IllegalStateException("Coluna " + COLUNA_TREINO_NOME + " não encontrada no resultado da consulta.");
                }
                if (observacaoIndex >= 0) {
                    treino.setObservacao(cursorTreinos.getString(observacaoIndex));
                } else {
                    throw new IllegalStateException("Coluna " + COLUNA_TREINO_OBSERVACAO + " não encontrada no resultado da consulta.");
                }
                if (diaSemanaIndex >= 0) {
                    treino.setDiaSemana(cursorTreinos.getString(diaSemanaIndex));
                } else {
                    throw new IllegalStateException("Coluna " + COLUNA_TREINO_DIA_SEMANA + " não encontrada no resultado da consulta.");
                }

                Log.d("BancoDeDadosHelper", "Treino encontrado - ID: " + treino.getId() + ", Nome: " + treino.getNome());

                List<Exercicio> exercicios = new ArrayList<>();
                Cursor cursorExercicios = db.rawQuery(
                        "SELECT * FROM " + TABELA_EXERCICIOS + " WHERE " + COLUNA_EXERCICIO_TREINO_ID + " = ?",
                        new String[]{String.valueOf(treino.getId())}
                );

                if (cursorExercicios.moveToFirst()) {
                    do {
                        Exercicio exercicio = new Exercicio();
                        int exercicioIdIndex = cursorExercicios.getColumnIndex(COLUNA_EXERCICIO_ID);
                        int exercicioTreinoIdIndex = cursorExercicios.getColumnIndex(COLUNA_EXERCICIO_TREINO_ID);
                        int exercicioNomeIndex = cursorExercicios.getColumnIndex(COLUNA_EXERCICIO_NOME);
                        int tempoDescansoIndex = cursorExercicios.getColumnIndex(COLUNA_EXERCICIO_TEMPO_DESCANSO);

                        if (exercicioIdIndex >= 0) {
                            exercicio.setId(cursorExercicios.getInt(exercicioIdIndex));
                        } else {
                            throw new IllegalStateException("Coluna " + COLUNA_EXERCICIO_ID + " não encontrada.");
                        }
                        if (exercicioTreinoIdIndex >= 0) {
                            exercicio.setTreinoId(cursorExercicios.getInt(exercicioTreinoIdIndex));
                        } else {
                            throw new IllegalStateException("Coluna " + COLUNA_EXERCICIO_TREINO_ID + " não encontrada.");
                        }
                        if (exercicioNomeIndex >= 0) {
                            exercicio.setNome(cursorExercicios.getString(exercicioNomeIndex));
                        } else {
                            throw new IllegalStateException("Coluna " + COLUNA_EXERCICIO_NOME + " não encontrada.");
                        }
                        if (tempoDescansoIndex >= 0) {
                            exercicio.setTempoDescanso(cursorExercicios.getString(tempoDescansoIndex));
                        } else {
                            throw new IllegalStateException("Coluna " + COLUNA_EXERCICIO_TEMPO_DESCANSO + " não encontrada.");
                        }

                        Log.d("BancoDeDadosHelper", "Exercício encontrado - ID: " + exercicio.getId() + ", Nome: " + exercicio.getNome());

                        List<Serie> series = new ArrayList<>();
                        Cursor cursorSeries = db.rawQuery(
                                "SELECT * FROM " + TABELA_SERIES + " WHERE " + COLUNA_SERIE_EXERCICIO_ID + " = ?",
                                new String[]{String.valueOf(exercicio.getId())}
                        );

                        if (cursorSeries.moveToFirst()) {
                            do {
                                Serie serie = new Serie();
                                int serieIdIndex = cursorSeries.getColumnIndex(COLUNA_SERIE_ID);
                                int serieExercicioIdIndex = cursorSeries.getColumnIndex(COLUNA_SERIE_EXERCICIO_ID);
                                int cargaIndex = cursorSeries.getColumnIndex(COLUNA_SERIE_CARGA);
                                int repeticoesIndex = cursorSeries.getColumnIndex(COLUNA_SERIE_REPETICOES);

                                if (serieIdIndex >= 0) {
                                    serie.setId(cursorSeries.getInt(serieIdIndex));
                                } else {
                                    throw new IllegalStateException("Coluna " + COLUNA_SERIE_ID + " não encontrada.");
                                }
                                if (serieExercicioIdIndex >= 0) {
                                    serie.setExercicioId(cursorSeries.getInt(serieExercicioIdIndex));
                                } else {
                                    throw new IllegalStateException("Coluna " + COLUNA_SERIE_EXERCICIO_ID + " não encontrada.");
                                }
                                if (cargaIndex >= 0) {
                                    serie.setCarga(cursorSeries.getString(cargaIndex));
                                } else {
                                    throw new IllegalStateException("Coluna " + COLUNA_SERIE_CARGA + " não encontrada.");
                                }
                                if (repeticoesIndex >= 0) {
                                    serie.setRepeticoes(String.valueOf(cursorSeries.getInt(repeticoesIndex)));
                                } else {
                                    throw new IllegalStateException("Coluna " + COLUNA_SERIE_REPETICOES + " não encontrada.");
                                }

                                series.add(serie);
                                Log.d("BancoDeDadosHelper", "Série encontrada - ID: " + serie.getId() + ", Carga: " + serie.getCarga() + ", Repetições: " + serie.getRepeticoes());
                            } while (cursorSeries.moveToNext());
                        }
                        cursorSeries.close();
                        exercicio.setSeries(series);
                        exercicios.add(exercicio);
                    } while (cursorExercicios.moveToNext());
                }
                cursorExercicios.close();
                treino.setExercicios(exercicios);
                listaTreinos.add(treino);
                Log.d("BancoDeDadosHelper", "Treino " + treino.getId() + " - Exercícios: " + exercicios.size() + ", Séries totais: " + exercicios.stream().mapToInt(ex -> ex.getSeries().size()).sum());
            } while (cursorTreinos.moveToNext());
        }

        cursorTreinos.close();
        db.close();
        Log.d("BancoDeDadosHelper", "Total de treinos encontrados: " + listaTreinos.size());
        return listaTreinos;
    }
}