package com.mycoach.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import java.util.Arrays;
import java.util.List;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

public class AdicionarTreinoActivity extends AppCompatActivity {

    private TextInputEditText treinoNomeInput;
    private TextInputEditText treinoObservacaoInput;
    private MaterialAutoCompleteTextView treinoDiaSemanaInput;
    private RecyclerView exerciciosRecyclerView;
    private ExercicioFormAdapter exercicioAdapter;
    private BancoDeDadosHelper bancoDeDadosHelper;
    private final DataFirebase dbfire = new DataFirebase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_treino);

        bancoDeDadosHelper = new BancoDeDadosHelper(this);

        String[] diasSemana = getResources().getStringArray(R.array.dias_semana);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        treinoNomeInput = findViewById(R.id.treinoNomeInput);
        treinoObservacaoInput = findViewById(R.id.treinoObservacaoInput);
        treinoDiaSemanaInput = findViewById(R.id.treinoDiaSemanaInput);
        exerciciosRecyclerView = findViewById(R.id.exerciciosRecyclerView);
        NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);
        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);

        if (treinoNomeInput == null || treinoObservacaoInput == null || treinoDiaSemanaInput == null ||
                exerciciosRecyclerView == null || nestedScrollView == null || appBarLayout == null) {
            Toast.makeText(this, R.string.toast_layout_error, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        treinoNomeInput.setFocusable(false);
        treinoNomeInput.setFocusableInTouchMode(false);
        treinoObservacaoInput.setFocusable(false);
        treinoObservacaoInput.setFocusableInTouchMode(false);
        treinoDiaSemanaInput.setFocusable(false);
        treinoDiaSemanaInput.setFocusableInTouchMode(false);

        ArrayAdapter<String> diasSemanaAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                diasSemana
        );
        treinoDiaSemanaInput.setAdapter(diasSemanaAdapter);

        exerciciosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exercicioAdapter = new ExercicioFormAdapter(getSupportFragmentManager(), nestedScrollView);
        exercicioAdapter.setRecyclerView(exerciciosRecyclerView);
        exerciciosRecyclerView.setAdapter(exercicioAdapter);

        exerciciosRecyclerView.post(() -> {
            Exercicio exercicioInicial = new Exercicio();
            exercicioInicial.setNome("");
            for (int i = 0; i < 1; i++) {
                Serie serie = new Serie();
                serie.setCarga("");
                serie.setRepeticoes("");
                serie.setTempo("");
                serie.setUnidadeTempo("min");
                serie.setTipoSerie("carga_reps");
                exercicioInicial.getSeries().add(serie);
            }
            exercicioInicial.setTempoDescanso("1min 0s");
            exercicioAdapter.addExercicio(exercicioInicial);
        });

        exerciciosRecyclerView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

        treinoNomeInput.setOnClickListener(v -> {
            treinoNomeInput.setFocusable(true);
            treinoNomeInput.setFocusableInTouchMode(true);
            treinoNomeInput.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(treinoNomeInput, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        treinoObservacaoInput.setOnClickListener(v -> {
            treinoObservacaoInput.setFocusable(true);
            treinoObservacaoInput.setFocusableInTouchMode(true);
            treinoObservacaoInput.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(treinoObservacaoInput, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        treinoDiaSemanaInput.setOnClickListener(v -> {
            treinoDiaSemanaInput.setFocusable(true);
            treinoDiaSemanaInput.setFocusableInTouchMode(true);
            treinoDiaSemanaInput.requestFocus();
            treinoDiaSemanaInput.showDropDown();
        });

        treinoNomeInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        treinoNomeInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                treinoObservacaoInput.setFocusable(true);
                treinoObservacaoInput.setFocusableInTouchMode(true);
                if (treinoObservacaoInput.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(treinoObservacaoInput, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
                return true;
            }
            return false;
        });

        treinoObservacaoInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        treinoObservacaoInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                treinoDiaSemanaInput.setFocusable(true);
                treinoDiaSemanaInput.setFocusableInTouchMode(true);
                if (treinoDiaSemanaInput.requestFocus()) {
                    treinoDiaSemanaInput.showDropDown();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(treinoDiaSemanaInput, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
                return true;
            }
            return false;
        });

        treinoDiaSemanaInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                List<Exercicio> exercicios = exercicioAdapter.getExercicios();
                if (exercicios != null && !exercicios.isEmpty()) {
                    exerciciosRecyclerView.smoothScrollToPosition(0);
                    exerciciosRecyclerView.postDelayed(() -> {
                        RecyclerView.ViewHolder vh = exerciciosRecyclerView.findViewHolderForAdapterPosition(0);
                        if (vh instanceof ExercicioFormAdapter.ExercicioViewHolder) {
                            TextInputEditText exercicioNomeInput = ((ExercicioFormAdapter.ExercicioViewHolder) vh).exercicioNomeInput;
                            exercicioNomeInput.setFocusable(true);
                            exercicioNomeInput.setFocusableInTouchMode(true);
                            if (exercicioNomeInput.requestFocus()) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm != null) {
                                    imm.showSoftInput(exercicioNomeInput, InputMethodManager.SHOW_IMPLICIT);
                                }
                            }
                        } else {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            }
                            v.clearFocus();
                        }
                    }, 150);
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    v.clearFocus();
                }
                return true;
            }
            return false;
        });

        exercicioAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                updateDiaSemanaImeOptions();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                updateDiaSemanaImeOptions();
            }

            @Override
            public void onChanged() {
                updateDiaSemanaImeOptions();
            }
        });

        updateDiaSemanaImeOptions();

        nestedScrollView.post(() -> {
            nestedScrollView.requestFocus();
            treinoNomeInput.clearFocus();
            treinoObservacaoInput.clearFocus();
            treinoDiaSemanaInput.clearFocus();
            exerciciosRecyclerView.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(nestedScrollView.getWindowToken(), 0);
            }
        });

        int alunoId = getIntent().getIntExtra("aluno_id", -1);
        if (alunoId == -1) {
            Toast.makeText(this, R.string.toast_error_client_id, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateDiaSemanaImeOptions() {
        List<Exercicio> exercicios = exercicioAdapter.getExercicios();
        if (exercicios != null && !exercicios.isEmpty()) {
            treinoDiaSemanaInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        } else {
            treinoDiaSemanaInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            salvarTreino();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void salvarTreino() {
        Treino treino = new Treino();
        String nome = treinoNomeInput.getText() != null ? treinoNomeInput.getText().toString().trim() : "";
        String observacao = treinoObservacaoInput.getText() != null ? treinoObservacaoInput.getText().toString().trim() : "";
        String diaSemanaTexto = treinoDiaSemanaInput.getText() != null ? treinoDiaSemanaInput.getText().toString().trim() : "";

        if (nome.isEmpty()) {
            treinoNomeInput.setError(getString(R.string.error_title_required));
            return;
        }

        if (diaSemanaTexto.isEmpty()) {
            Toast.makeText(this, R.string.toast_select_day, Toast.LENGTH_SHORT).show();
            return;
        }

        String[] diasSemana = getResources().getStringArray(R.array.dias_semana);
        int diaSemanaIndex = Arrays.asList(diasSemana).indexOf(diaSemanaTexto);
        if (diaSemanaIndex == -1) {
            Toast.makeText(this, R.string.toast_invalid_day, Toast.LENGTH_SHORT).show();
            return;
        }

        int alunoId = getIntent().getIntExtra("aluno_id", -1);
        if (alunoId == -1) {
            Toast.makeText(this, R.string.toast_error_client_id, Toast.LENGTH_SHORT).show();
            return;
        }

        long treinoId = bancoDeDadosHelper.adicionarTreino(alunoId, nome, observacao.isEmpty() ? null : observacao, diaSemanaIndex);
        if (treinoId == -1) {
            Toast.makeText(this, R.string.toast_error_add_training, Toast.LENGTH_SHORT).show();
            return;
        }

        treino.setId((int)treinoId);
        treino.setDiaSemanaIndex(diaSemanaIndex);

        for (Exercicio exercicio : exercicioAdapter.getExercicios()) {
            String exercicioNome = exercicio.getNome().trim();
            String tempoDescanso = exercicio.getTempoDescanso().trim();

            if (exercicioNome.isEmpty() || tempoDescanso.isEmpty()) {
                Toast.makeText(this, R.string.toast_fill_exercise_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            long exercicioId = bancoDeDadosHelper.adicionarExercicio((int) treinoId, exercicioNome, tempoDescanso);
            if (exercicioId == -1) {
                Toast.makeText(this, R.string.toast_error_add_exercise, Toast.LENGTH_SHORT).show();
                return;
            }

            for (Serie serieParaValidar : exercicio.getSeries()) {
                String tipoSerie = serieParaValidar.getTipoSerie();
                switch (tipoSerie) {
                    case "carga":
                        String cargaVal = serieParaValidar.getCarga().trim();
                        if (cargaVal.isEmpty()) {
                            Toast.makeText(this, R.string.toast_fill_series_fields, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            Float.parseFloat(cargaVal);
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, R.string.toast_invalid_numeric_values, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;

                    case "repeticoes":
                        String repeticoesVal = serieParaValidar.getRepeticoes().trim();
                        if (repeticoesVal.isEmpty()) {
                            Toast.makeText(this, R.string.toast_fill_series_fields, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            int repeticoesIntVal = Integer.parseInt(repeticoesVal);
                            if (repeticoesIntVal <= 0) {
                                Toast.makeText(this, R.string.toast_invalid_repetitions, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, R.string.toast_invalid_numeric_repetitions, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;

                    case "tempo":
                        String tempoVal = serieParaValidar.getTempo().trim();
                        if (tempoVal.isEmpty()) {
                            Toast.makeText(this, R.string.toast_fill_series_fields, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            int tempoIntVal = Integer.parseInt(tempoVal);
                            if (tempoIntVal <= 0) {
                                Toast.makeText(this, R.string.toast_invalid_time, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, R.string.toast_invalid_numeric_time, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;

                    case "carga_reps":
                        String cargaValCargaReps = serieParaValidar.getCarga().trim();
                        String repeticoesValCargaReps = serieParaValidar.getRepeticoes().trim();
                        if (cargaValCargaReps.isEmpty() || repeticoesValCargaReps.isEmpty()) {
                            Toast.makeText(this, R.string.toast_fill_series_fields, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            Float.parseFloat(cargaValCargaReps);
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, R.string.toast_invalid_numeric_values, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            int repeticoesIntVal = Integer.parseInt(repeticoesValCargaReps);
                            if (repeticoesIntVal <= 0) {
                                Toast.makeText(this, R.string.toast_invalid_repetitions, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, R.string.toast_invalid_numeric_repetitions, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;

                    default:
                        Toast.makeText(this, R.string.toast_invalid_series_type, Toast.LENGTH_SHORT).show();
                        return;
                }
            }

            for (Serie serie : exercicio.getSeries()) {
                long serieId = bancoDeDadosHelper.adicionarSerie((int) exercicioId, serie);
                if (serieId == -1) {
                    Toast.makeText(this, R.string.toast_error_add_series, Toast.LENGTH_SHORT).show();
                    return;
                }
                serie.setId((int)serieId);
                serie.setExercicioId((int) exercicioId);
                dbfire.sendFirebaseSerie(serie, "series");
            }

            exercicio.setId((int) exercicioId);
            exercicio.setTreinoId((int) treinoId);
            exercicio.setNome(exercicioNome);
            exercicio.setTempoDescanso(tempoDescanso);
            dbfire.sendFirebaseExercise(exercicio, "exercicios");
        }

        Toast.makeText(this, R.string.toast_training_added, Toast.LENGTH_SHORT).show();

        treino.setAlunoId(alunoId);
        treino.setObservacao(observacao.isEmpty() ? null : observacao);
        treino.setNome(nome);
        dbfire.sendFirebaseTreino(treino, "treinos");

        dbfire.syncWithFirebaseTreino(bancoDeDadosHelper, "treinos");
        dbfire.syncWithFirebaseExercise(bancoDeDadosHelper, "exercicios");
        dbfire.syncWithFirebaseSerie(bancoDeDadosHelper, "series");

        Intent intent = new Intent(this, DetalhesTreinoActivity.class);
        intent.putExtra("treino_id", (int) treinoId);
        intent.putExtra("aluno_id", alunoId);
        intent.putExtra("treino_nome", nome);
        intent.putExtra("treino_observacao", observacao.isEmpty() ? null : observacao);
        intent.putExtra("treino_dia_semana_index", diaSemanaIndex);
        startActivity(intent);
        finish();
    }
}