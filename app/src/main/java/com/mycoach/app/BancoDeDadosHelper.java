package com.mycoach.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BancoDeDadosHelper extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "mycoach.db";
    private static final int VERSAO_BANCO = 3;

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

    private final Context context;

    public BancoDeDadosHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
        this.context = context;
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
                COLUNA_TREINO_DIA_SEMANA + " INTEGER NOT NULL, " +
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
        if (versaoAntiga < 3) {
            db.execSQL("ALTER TABLE " + TABELA_TREINOS + " RENAME TO temp_treinos");

            String criarTabelaTreinos = "CREATE TABLE " + TABELA_TREINOS + " (" +
                    COLUNA_TREINO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUNA_TREINO_ALUNO_ID + " INTEGER NOT NULL, " +
                    COLUNA_TREINO_NOME + " TEXT NOT NULL, " +
                    COLUNA_TREINO_OBSERVACAO + " TEXT, " +
                    COLUNA_TREINO_DIA_SEMANA + " INTEGER NOT NULL, " +
                    "FOREIGN KEY (" + COLUNA_TREINO_ALUNO_ID + ") REFERENCES " + TABELA_ALUNOS + "(" + COLUNA_ALUNO_ID + "))";
            db.execSQL(criarTabelaTreinos);

            String[] diasSemana = context.getResources().getStringArray(R.array.dias_semana);

            Cursor cursor = db.rawQuery("SELECT * FROM temp_treinos", null);
            if (cursor.moveToFirst()) {
                do {
                    ContentValues values = new ContentValues();
                    int idIndex = cursor.getColumnIndex(COLUNA_TREINO_ID);
                    int alunoIdIndex = cursor.getColumnIndex(COLUNA_TREINO_ALUNO_ID);
                    int nomeIndex = cursor.getColumnIndex(COLUNA_TREINO_NOME);
                    int observacaoIndex = cursor.getColumnIndex(COLUNA_TREINO_OBSERVACAO);
                    int diaSemanaDbIndex = cursor.getColumnIndex(COLUNA_TREINO_DIA_SEMANA);

                    if (idIndex >= 0) values.put(COLUNA_TREINO_ID, cursor.getInt(idIndex));
                    if (alunoIdIndex >= 0) values.put(COLUNA_TREINO_ALUNO_ID, cursor.getInt(alunoIdIndex));
                    if (nomeIndex >= 0) values.put(COLUNA_TREINO_NOME, cursor.getString(nomeIndex));
                    if (observacaoIndex >= 0) values.put(COLUNA_TREINO_OBSERVACAO, cursor.getString(observacaoIndex));

                    if (diaSemanaDbIndex >= 0) {
                        String diaSemanaTexto = cursor.getString(diaSemanaDbIndex);
                        int diaSemanaIndice = Arrays.asList(diasSemana).indexOf(diaSemanaTexto);
                        if (diaSemanaIndice == -1) {
                            String[] diasSemanaEn = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                            diaSemanaIndice = Arrays.asList(diasSemanaEn).indexOf(diaSemanaTexto);
                            if (diaSemanaIndice == -1) {
                                diaSemanaIndice = 0;
                            }
                        }
                        values.put(COLUNA_TREINO_DIA_SEMANA, diaSemanaIndice);
                    }
                    db.insert(TABELA_TREINOS, null, values);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.execSQL("DROP TABLE temp_treinos");
        }
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

                if (idIndex >= 0) aluno.setId(cursor.getInt(idIndex));
                if (nomeIndex >= 0) aluno.setNome(cursor.getString(nomeIndex));
                if (emailIndex >= 0) aluno.setEmail(cursor.getString(emailIndex));
                if (senhaIndex >= 0) aluno.setSenha(cursor.getString(senhaIndex));

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

            if (idIndex >= 0) aluno.setId(cursor.getInt(idIndex));
            if (nomeIndex >= 0) aluno.setNome(cursor.getString(nomeIndex));
            if (emailIndex >= 0) aluno.setEmail(cursor.getString(emailIndex));
            if (senhaIndex >= 0) aluno.setSenha(cursor.getString(senhaIndex));
        }
        cursor.close();
        db.close();
        return aluno;
    }

    public void deletarAluno(int alunoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABELA_SERIES + " WHERE " + COLUNA_SERIE_EXERCICIO_ID + " IN (" +
                        "SELECT " + COLUNA_EXERCICIO_ID + " FROM " + TABELA_EXERCICIOS + " WHERE " + COLUNA_EXERCICIO_TREINO_ID + " IN (" +
                        "SELECT " + COLUNA_TREINO_ID + " FROM " + TABELA_TREINOS + " WHERE " + COLUNA_TREINO_ALUNO_ID + " = ?))",
                new String[]{String.valueOf(alunoId)});
        db.execSQL("DELETE FROM " + TABELA_EXERCICIOS + " WHERE " + COLUNA_EXERCICIO_TREINO_ID + " IN (" +
                        "SELECT " + COLUNA_TREINO_ID + " FROM " + TABELA_TREINOS + " WHERE " + COLUNA_TREINO_ALUNO_ID + " = ?)",
                new String[]{String.valueOf(alunoId)});
        db.delete(TABELA_TREINOS, COLUNA_TREINO_ALUNO_ID + " = ?", new String[]{String.valueOf(alunoId)});
        db.delete(TABELA_ALUNOS, COLUNA_ALUNO_ID + " = ?", new String[]{String.valueOf(alunoId)});
        db.close();
    }

    public void deletarTreino(int treinoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABELA_SERIES + " WHERE " + COLUNA_SERIE_EXERCICIO_ID + " IN (" +
                        "SELECT " + COLUNA_EXERCICIO_ID + " FROM " + TABELA_EXERCICIOS + " WHERE " + COLUNA_EXERCICIO_TREINO_ID + " = ?)",
                new String[]{String.valueOf(treinoId)});
        db.execSQL("DELETE FROM " + TABELA_EXERCICIOS + " WHERE " + COLUNA_EXERCICIO_TREINO_ID + " = ?",
                new String[]{String.valueOf(treinoId)});
        db.delete(TABELA_TREINOS, COLUNA_TREINO_ID + " = ?", new String[]{String.valueOf(treinoId)});
        db.close();
    }

    public long adicionarTreino(int alunoId, String nome, String observacao, int diaSemanaIndex) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUNA_TREINO_ALUNO_ID, alunoId);
        values.put(COLUNA_TREINO_NOME, nome);
        values.put(COLUNA_TREINO_OBSERVACAO, observacao);
        values.put(COLUNA_TREINO_DIA_SEMANA, diaSemanaIndex);
        long result = db.insert(TABELA_TREINOS, null, values);
        db.close();
        return result;
    }

    public void adicionarTreino(int alunoId, String nome, String observacao, int diaSemanaIndex, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUNA_TREINO_ID, id);
        values.put(COLUNA_TREINO_ALUNO_ID, alunoId);
        values.put(COLUNA_TREINO_NOME, nome);
        values.put(COLUNA_TREINO_OBSERVACAO, observacao);
        values.put(COLUNA_TREINO_DIA_SEMANA, diaSemanaIndex);
        db.insert(TABELA_TREINOS, null, values);
        db.close();
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

    public void adicionarExercicio(int treinoId, String nome, String tempoDescanso, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUNA_EXERCICIO_ID, id);
        values.put(COLUNA_EXERCICIO_TREINO_ID, treinoId);
        values.put(COLUNA_EXERCICIO_NOME, nome);
        values.put(COLUNA_EXERCICIO_TEMPO_DESCANSO, tempoDescanso);
        db.insert(TABELA_EXERCICIOS, null, values);
        db.close();
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

    public boolean emailExiste(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABELA_ALUNOS + " WHERE " + COLUNA_ALUNO_EMAIL + " = ?", new String[]{email});
        boolean existe = cursor.moveToFirst();
        cursor.close();
        db.close();
        return existe;
    }

    public List<Treino> obterTreinosPorAlunoId(int idAluno) {
        List<Treino> listaTreinos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorTreinos = db.rawQuery(
                "SELECT * FROM " + TABELA_TREINOS + " WHERE " + COLUNA_TREINO_ALUNO_ID + " = ?",
                new String[]{String.valueOf(idAluno)}
        );

        if (cursorTreinos.moveToFirst()) {
            do {
                Treino treino = new Treino();
                int idIndex = cursorTreinos.getColumnIndex(COLUNA_TREINO_ID);
                int alunoIdIndex = cursorTreinos.getColumnIndex(COLUNA_TREINO_ALUNO_ID);
                int nomeIndex = cursorTreinos.getColumnIndex(COLUNA_TREINO_NOME);
                int observacaoIndex = cursorTreinos.getColumnIndex(COLUNA_TREINO_OBSERVACAO);
                int diaSemanaIdx = cursorTreinos.getColumnIndex(COLUNA_TREINO_DIA_SEMANA);

                if (idIndex >= 0) treino.setId(cursorTreinos.getInt(idIndex));
                if (alunoIdIndex >= 0) treino.setAlunoId(cursorTreinos.getInt(alunoIdIndex));
                if (nomeIndex >= 0) treino.setNome(cursorTreinos.getString(nomeIndex));
                if (observacaoIndex >= 0) treino.setObservacao(cursorTreinos.getString(observacaoIndex));
                if (diaSemanaIdx >= 0) treino.setDiaSemanaIndex(cursorTreinos.getInt(diaSemanaIdx));

                List<Exercicio> exercicios = new ArrayList<>();
                Cursor cursorExercicios = db.rawQuery(
                        "SELECT * FROM " + TABELA_EXERCICIOS + " WHERE " + COLUNA_EXERCICIO_TREINO_ID + " = ?",
                        new String[]{String.valueOf(treino.getId())}
                );

                if (cursorExercicios.moveToFirst()) {
                    do {
                        Exercicio exercicio = new Exercicio();
                        int exIdIndex = cursorExercicios.getColumnIndex(COLUNA_EXERCICIO_ID);
                        int exTreinoIdIndex = cursorExercicios.getColumnIndex(COLUNA_EXERCICIO_TREINO_ID);
                        int exNomeIndex = cursorExercicios.getColumnIndex(COLUNA_EXERCICIO_NOME);
                        int tempoDescansoIndex = cursorExercicios.getColumnIndex(COLUNA_EXERCICIO_TEMPO_DESCANSO);

                        if (exIdIndex >= 0) exercicio.setId(cursorExercicios.getInt(exIdIndex));
                        if (exTreinoIdIndex >= 0) exercicio.setTreinoId(cursorExercicios.getInt(exTreinoIdIndex));
                        if (exNomeIndex >= 0) exercicio.setNome(cursorExercicios.getString(exNomeIndex));
                        if (tempoDescansoIndex >= 0) exercicio.setTempoDescanso(cursorExercicios.getString(tempoDescansoIndex));

                        List<Serie> series = new ArrayList<>();
                        Cursor cursorSeries = db.rawQuery(
                                "SELECT * FROM " + TABELA_SERIES + " WHERE " + COLUNA_SERIE_EXERCICIO_ID + " = ?",
                                new String[]{String.valueOf(exercicio.getId())}
                        );

                        if (cursorSeries.moveToFirst()) {
                            do {
                                Serie serie = new Serie();
                                int serieIdIndex = cursorSeries.getColumnIndex(COLUNA_SERIE_ID);
                                int serieExIdIndex = cursorSeries.getColumnIndex(COLUNA_SERIE_EXERCICIO_ID);
                                int cargaIndex = cursorSeries.getColumnIndex(COLUNA_SERIE_CARGA);
                                int repeticoesIndex = cursorSeries.getColumnIndex(COLUNA_SERIE_REPETICOES);

                                if (serieIdIndex >= 0) serie.setId(cursorSeries.getInt(serieIdIndex));
                                if (serieExIdIndex >= 0) serie.setExercicioId(cursorSeries.getInt(serieExIdIndex));
                                if (cargaIndex >= 0) serie.setCarga(cursorSeries.getString(cargaIndex));
                                if (repeticoesIndex >= 0) serie.setRepeticoes(String.valueOf(cursorSeries.getInt(repeticoesIndex)));

                                series.add(serie);
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
            } while (cursorTreinos.moveToNext());
        }
        cursorTreinos.close();
        db.close();
        return listaTreinos;
    }

    public void adicionarAluno(int id, String nome, String email, String senha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COLUNA_ALUNO_ID, id);
        valores.put(COLUNA_ALUNO_NOME, nome);
        valores.put(COLUNA_ALUNO_EMAIL, email);
        valores.put(COLUNA_ALUNO_SENHA, senha);
        db.insert(TABELA_ALUNOS, null, valores);
        db.close();
    }

    public void deletarTodosAlunos() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABELA_ALUNOS, null, null);
        db.close();
    }

    public void deletarTodosTreinos() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABELA_TREINOS, null, null);
        db.close();
    }

    public void deletarTodosSeries() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABELA_SERIES, null, null);
        db.close();
    }

    public void deletarTodosExercicios() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABELA_EXERCICIOS, null, null);
        db.close();
    }
}