package com.mycoach.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class BancoDeDadosHelper extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "mycoach.db";
    private static final int VERSAO_BANCO = 1;

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
    private static final String COLUNA_TREINO_DESCRICAO = "descricao";
    private static final String COLUNA_TREINO_DATA = "data";

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
                COLUNA_TREINO_DESCRICAO + " TEXT, " +
                COLUNA_TREINO_DATA + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + COLUNA_TREINO_ALUNO_ID + ") REFERENCES " + TABELA_ALUNOS + "(" + COLUNA_ALUNO_ID + "))";
        db.execSQL(criarTabelaTreinos);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versaoAntiga, int novaVersao) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_PERSONAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_ALUNOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_TREINOS);
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

    public boolean adicionarTreino(int alunoId, String nome, String descricao, String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUNA_TREINO_ALUNO_ID, alunoId);
        values.put(COLUNA_TREINO_NOME, nome);
        values.put(COLUNA_TREINO_DESCRICAO, descricao);
        values.put(COLUNA_TREINO_DATA, data);

        long result = db.insert(TABELA_TREINOS, null, values);
        db.close();
        return result != -1;
    }

    public List<Treino> obterTreinosPorAlunoId(int idAluno) {
        List<Treino> listaTreinos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABELA_TREINOS + " WHERE " + COLUNA_TREINO_ALUNO_ID + " = ?",
                new String[]{String.valueOf(idAluno)}
        );

        if (cursor.moveToFirst()) {
            do {
                Treino treino = new Treino();
                int idIndex = cursor.getColumnIndex(COLUNA_TREINO_ID);
                int alunoIdIndex = cursor.getColumnIndex(COLUNA_TREINO_ALUNO_ID);
                int nomeIndex = cursor.getColumnIndex(COLUNA_TREINO_NOME);
                int descricaoIndex = cursor.getColumnIndex(COLUNA_TREINO_DESCRICAO);
                int dataIndex = cursor.getColumnIndex(COLUNA_TREINO_DATA);

                if (idIndex >= 0) {
                    treino.setId(cursor.getInt(idIndex));
                } else {
                    throw new IllegalStateException("Coluna " + COLUNA_TREINO_ID + " não encontrada no resultado da consulta.");
                }
                if (alunoIdIndex >= 0) {
                    treino.setAlunoId(cursor.getInt(alunoIdIndex));
                } else {
                    throw new IllegalStateException("Coluna " + COLUNA_TREINO_ALUNO_ID + " não encontrada no resultado da consulta.");
                }
                if (nomeIndex >= 0) {
                    treino.setNome(cursor.getString(nomeIndex));
                } else {
                    throw new IllegalStateException("Coluna " + COLUNA_TREINO_NOME + " não encontrada no resultado da consulta.");
                }
                if (descricaoIndex >= 0) {
                    treino.setDescricao(cursor.getString(descricaoIndex));
                } else {
                    throw new IllegalStateException("Coluna " + COLUNA_TREINO_DESCRICAO + " não encontrada no resultado da consulta.");
                }
                if (dataIndex >= 0) {
                    treino.setData(cursor.getString(dataIndex));
                } else {
                    throw new IllegalStateException("Coluna " + COLUNA_TREINO_DATA + " não encontrada no resultado da consulta.");
                }

                listaTreinos.add(treino);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaTreinos;
    }
}