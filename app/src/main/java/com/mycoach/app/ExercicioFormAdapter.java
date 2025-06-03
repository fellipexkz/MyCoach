package com.mycoach.app;

import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class ExercicioFormAdapter extends RecyclerView.Adapter<ExercicioFormAdapter.ExercicioViewHolder> {

    private final List<Exercicio> exercicios;
    private RecyclerView exerciciosRecyclerView;
    private final NestedScrollView nestedScrollView;
    private final FragmentManager fragmentManager;
    private final List<List<EditText>> cargaEditTextMatrix = new ArrayList<>();
    private static final int TYPE_CARGA_REPS = 0;
    private static final int TYPE_TEMPO = 1;
    private static final int TYPE_REPETICOES = 2;
    private static final int TYPE_CARGA = 3;

    public ExercicioFormAdapter(FragmentManager fragmentManager, NestedScrollView nestedScrollView) {
        this.exercicios = new ArrayList<>();
        this.fragmentManager = fragmentManager;
        this.nestedScrollView = nestedScrollView;
    }

    @NonNull
    @Override
    public ExercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercicio_form, parent, false);
        return new ExercicioViewHolder(view, this);
    }

    private void scrollToView(final View viewToScrollTo) {
        if (viewToScrollTo == null || nestedScrollView == null) return;
        viewToScrollTo.postDelayed(() -> {
            Rect r = new Rect();
            nestedScrollView.getWindowVisibleDisplayFrame(r);
            int visibleWindowHeight = r.bottom - r.top;
            int[] focusedViewLocation = new int[2];
            viewToScrollTo.getLocationOnScreen(focusedViewLocation);
            int focusedViewBottom = focusedViewLocation[1] + viewToScrollTo.getHeight();
            int desiredMargin = 20;
            int targetBottom = visibleWindowHeight - desiredMargin;
            if (focusedViewBottom > targetBottom) {
                int scrollAmount = focusedViewBottom - targetBottom;
                nestedScrollView.smoothScrollBy(0, scrollAmount);
            }
        }, 200);
    }

    private int getImeOptionsForNavigation(int seriePosition, int totalSeries, int adapterPos, int totalExercises) {
        return seriePosition < totalSeries - 1 ? EditorInfo.IME_ACTION_NEXT : (adapterPos < totalExercises - 1 ? EditorInfo.IME_ACTION_NEXT : EditorInfo.IME_ACTION_DONE);
    }

    private void setupSerieImeNavigation(EditText serieCargaInput, EditText serieRepeticoesInput, EditText serieTempoInput, int seriePosition, ExercicioViewHolder holder) {
        int adapterPos = holder.getBindingAdapterPosition();
        if (adapterPos == RecyclerView.NO_POSITION) return;

        String tipoSerie = getTipoSerieFromChipId(holder.tipoSerieChipGroup.getCheckedChipId());
        int totalSeries = exercicios.get(adapterPos).getSeries().size();
        int totalExercises = exercicios.size();

        int imeOptions = getImeOptionsForNavigation(seriePosition, totalSeries, adapterPos, totalExercises);

        switch (tipoSerie) {
            case "carga":
                serieCargaInput.setImeOptions(imeOptions);
                serieCargaInput.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        if (seriePosition < totalSeries - 1) {
                            View nextSerieView = holder.seriesContainer.getChildAt(seriePosition + 1);
                            EditText nextCargaInput = nextSerieView.findViewById(R.id.serieCargaInput);
                            if (nextCargaInput != null) {
                                nextCargaInput.requestFocus();
                                scrollToView(nextCargaInput);
                            }
                        } else if (adapterPos < totalExercises - 1) {
                            RecyclerView.ViewHolder nextHolder = exerciciosRecyclerView.findViewHolderForAdapterPosition(adapterPos + 1);
                            if (nextHolder instanceof ExercicioViewHolder) {
                                TextInputEditText nextNomeInput = ((ExercicioViewHolder) nextHolder).exercicioNomeInput;
                                if (nextNomeInput != null) {
                                    nextNomeInput.setFocusable(true);
                                    nextNomeInput.setFocusableInTouchMode(true);
                                    nextNomeInput.requestFocus();
                                    scrollToView(nextNomeInput);
                                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    if (imm != null) imm.showSoftInput(nextNomeInput, InputMethodManager.SHOW_IMPLICIT);
                                }
                            }
                        }
                        return true;
                    } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                        hideKeyboard(v);
                        v.clearFocus();
                        return true;
                    }
                    return false;
                });
                break;
            case "repeticoes":
                serieRepeticoesInput.setImeOptions(imeOptions);
                serieRepeticoesInput.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        if (seriePosition < totalSeries - 1) {
                            View nextSerieView = holder.seriesContainer.getChildAt(seriePosition + 1);
                            EditText nextRepeticoesInput = nextSerieView.findViewById(R.id.serieRepeticoesInput);
                            if (nextRepeticoesInput != null) {
                                nextRepeticoesInput.requestFocus();
                                scrollToView(nextRepeticoesInput);
                            }
                        } else if (adapterPos < totalExercises - 1) {
                            RecyclerView.ViewHolder nextHolder = exerciciosRecyclerView.findViewHolderForAdapterPosition(adapterPos + 1);
                            if (nextHolder instanceof ExercicioViewHolder) {
                                TextInputEditText nextNomeInput = ((ExercicioViewHolder) nextHolder).exercicioNomeInput;
                                if (nextNomeInput != null) {
                                    nextNomeInput.setFocusable(true);
                                    nextNomeInput.setFocusableInTouchMode(true);
                                    nextNomeInput.requestFocus();
                                    scrollToView(nextNomeInput);
                                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    if (imm != null) imm.showSoftInput(nextNomeInput, InputMethodManager.SHOW_IMPLICIT);
                                }
                            }
                        }
                        return true;
                    } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                        hideKeyboard(v);
                        v.clearFocus();
                        return true;
                    }
                    return false;
                });
                break;
            case "tempo":
                serieTempoInput.setImeOptions(imeOptions);
                serieTempoInput.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        if (seriePosition < totalSeries - 1) {
                            View nextSerieView = holder.seriesContainer.getChildAt(seriePosition + 1);
                            EditText nextTempoInput = nextSerieView.findViewById(R.id.serieTempoInput);
                            if (nextTempoInput != null) {
                                nextTempoInput.requestFocus();
                                scrollToView(nextTempoInput);
                            }
                        } else if (adapterPos < totalExercises - 1) {
                            RecyclerView.ViewHolder nextHolder = exerciciosRecyclerView.findViewHolderForAdapterPosition(adapterPos + 1);
                            if (nextHolder instanceof ExercicioViewHolder) {
                                TextInputEditText nextNomeInput = ((ExercicioViewHolder) nextHolder).exercicioNomeInput;
                                if (nextNomeInput != null) {
                                    nextNomeInput.setFocusable(true);
                                    nextNomeInput.setFocusableInTouchMode(true);
                                    nextNomeInput.requestFocus();
                                    scrollToView(nextNomeInput);
                                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    if (imm != null) imm.showSoftInput(nextNomeInput, InputMethodManager.SHOW_IMPLICIT);
                                }
                            }
                        }
                        return true;
                    } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                        hideKeyboard(v);
                        v.clearFocus();
                        return true;
                    }
                    return false;
                });
                break;
            case "carga_reps":
                serieCargaInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                serieCargaInput.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        serieRepeticoesInput.requestFocus();
                        scrollToView(serieRepeticoesInput);
                        return true;
                    }
                    return false;
                });
                serieRepeticoesInput.setImeOptions(imeOptions);
                serieRepeticoesInput.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        if (seriePosition < totalSeries - 1) {
                            View nextSerieView = holder.seriesContainer.getChildAt(seriePosition + 1);
                            EditText nextCargaInput = nextSerieView.findViewById(R.id.serieCargaInput);
                            if (nextCargaInput != null) {
                                nextCargaInput.requestFocus();
                                scrollToView(nextCargaInput);
                            }
                        } else if (adapterPos < totalExercises - 1) {
                            RecyclerView.ViewHolder nextHolder = exerciciosRecyclerView.findViewHolderForAdapterPosition(adapterPos + 1);
                            if (nextHolder instanceof ExercicioViewHolder) {
                                TextInputEditText nextNomeInput = ((ExercicioViewHolder) nextHolder).exercicioNomeInput;
                                if (nextNomeInput != null) {
                                    nextNomeInput.setFocusable(true);
                                    nextNomeInput.setFocusableInTouchMode(true);
                                    nextNomeInput.requestFocus();
                                    scrollToView(nextNomeInput);
                                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    if (imm != null) imm.showSoftInput(nextNomeInput, InputMethodManager.SHOW_IMPLICIT);
                                }
                            }
                        }
                        return true;
                    } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                        hideKeyboard(v);
                        v.clearFocus();
                        return true;
                    }
                    return false;
                });
                break;
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBindViewHolder(@NonNull ExercicioViewHolder holder, int position) {
        Exercicio exercicio = exercicios.get(position);

        MaterialButton excluirSerieButton = holder.itemView.findViewById(R.id.excluirSerieButton);

        holder.exercicioNomeInput.removeTextChangedListener(holder.nomeWatcher);
        holder.exercicioNomeInput.setText(exercicio.getNome());
        holder.exercicioNomeInput.addTextChangedListener(holder.nomeWatcher);

        holder.exercicioNomeInput.setOnClickListener(v -> {
            holder.exercicioNomeInput.setFocusable(true);
            holder.exercicioNomeInput.setFocusableInTouchMode(true);
            holder.exercicioNomeInput.requestFocus();
            InputMethodManager imm = (InputMethodManager) holder.itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(holder.exercicioNomeInput, InputMethodManager.SHOW_IMPLICIT);
            scrollToView(holder.exercicioNomeInput);
        });

        holder.exercicioNomeInput.setImeOptions(exercicio.getSeries().isEmpty() ? EditorInfo.IME_ACTION_DONE : EditorInfo.IME_ACTION_NEXT);
        holder.exercicioNomeInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT && !exercicio.getSeries().isEmpty()) {
                View firstSerieView = holder.seriesContainer.getChildAt(0);
                EditText firstCargaInput = firstSerieView.findViewById(R.id.serieCargaInput);
                EditText firstRepeticoesInput = firstSerieView.findViewById(R.id.serieRepeticoesInput);
                EditText firstTempoInput = firstSerieView.findViewById(R.id.serieTempoInput);
                String tipoSerie = getTipoSerieFromChipId(holder.tipoSerieChipGroup.getCheckedChipId());
                EditText firstFocus = null;
                switch (tipoSerie) {
                    case "carga":
                    case "carga_reps":
                        firstFocus = firstCargaInput;
                        break;
                    case "repeticoes":
                        firstFocus = firstRepeticoesInput;
                        break;
                    case "tempo":
                        firstFocus = firstTempoInput;
                        break;
                }
                if (firstFocus != null) {
                    firstFocus.setFocusable(true);
                    firstFocus.setFocusableInTouchMode(true);
                    if (firstFocus.requestFocus()) {
                        InputMethodManager imm = (InputMethodManager) holder.itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) imm.showSoftInput(firstFocus, InputMethodManager.SHOW_IMPLICIT);
                        scrollToView(firstFocus);
                    }
                    return true;
                }
            } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(v);
                v.clearFocus();
                return true;
            }
            return false;
        });

        String tempoDescanso = exercicio.getTempoDescanso();
        holder.tempoDescansoText.setText(String.format(holder.itemView.getContext().getResources().getString(R.string.tempo_descanso_format), tempoDescanso == null || tempoDescanso.isEmpty() ? holder.itemView.getContext().getResources().getString(R.string.tempo_descanso_desativado) : tempoDescanso));

        holder.tempoDescansoContainer.setOnClickListener(view -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION && fragmentManager != null) {
                Exercicio exercicioCurrent = exercicios.get(currentPosition);
                String exerciseName = exercicioCurrent.getNome().isEmpty() ? null : exercicioCurrent.getNome();
                String currentRestTime = exercicioCurrent.getTempoDescanso();
                RestTimeBottomSheetFragment bottomSheet = RestTimeBottomSheetFragment.newInstance(currentPosition, exerciseName, currentRestTime, this);
                bottomSheet.show(fragmentManager, "RestTimeBottomSheet");
            }
        });

        int savedTipoSerieId = exercicio.getTipoSerieSelecionadoId();
        if (savedTipoSerieId != View.NO_ID) {
            holder.tipoSerieChipGroup.check(savedTipoSerieId);
            Log.d("ExercicioFormAdapter", "Position: " + position + ", Restored tipoSerieChipId from Exercicio: " + savedTipoSerieId);
        } else {
            holder.tipoSerieChipGroup.check(R.id.chipCargaReps);
            exercicio.setTipoSerieSelecionadoId(R.id.chipCargaReps);
            Log.d("ExercicioFormAdapter", "Position: " + position + ", Set default tipoSerieChipId: " + R.id.chipCargaReps);
        }

        int savedUnidadeTempoId = exercicio.getUnidadeTempoSelecionadoId();
        HorizontalScrollView scrollUnidadeTempo = holder.itemView.findViewById(R.id.scrollUnidadeTempo);
        TextView unidadeTempoLabel = holder.itemView.findViewById(R.id.unidadeTempoLabel);
        if (holder.tipoSerieChipGroup.getCheckedChipId() == R.id.chipTempo) {
            scrollUnidadeTempo.setVisibility(View.VISIBLE);
            unidadeTempoLabel.setVisibility(View.VISIBLE);
            if (savedUnidadeTempoId != View.NO_ID) {
                holder.unidadeTempoChipGroup.check(savedUnidadeTempoId);
                Log.d("ExercicioFormAdapter", "Position: " + position + ", Restored unidadeTempoChipId from Exercicio: " + savedUnidadeTempoId);
            } else if (!exercicio.getSeries().isEmpty()) {
                String unidade = exercicio.getSeries().get(0).getUnidadeTempo();
                int defaultUnidadeId = "sec".equals(unidade) ? R.id.chipSegundos : R.id.chipMinutos;
                holder.unidadeTempoChipGroup.check(defaultUnidadeId);
                exercicio.setUnidadeTempoSelecionadoId(defaultUnidadeId);
                Log.d("ExercicioFormAdapter", "Position: " + position + ", Set default unidadeTempoChipId: " + defaultUnidadeId);
            }
        } else {
            scrollUnidadeTempo.setVisibility(View.GONE);
            unidadeTempoLabel.setVisibility(View.GONE);
        }

        holder.tipoSerieChipGroup.setOnCheckedStateChangeListener(null);
        holder.tipoSerieChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            int checkedId = group.getCheckedChipId();
            if (checkedId != View.NO_ID) {
                Log.d("ExercicioFormAdapter", "Position: " + position + ", tipoSerieChip Changed to: " + checkedId);
                exercicio.setTipoSerieSelecionadoId(checkedId);
                if (checkedId == R.id.chipTempo) {
                    scrollUnidadeTempo.setVisibility(View.VISIBLE);
                    unidadeTempoLabel.setVisibility(View.VISIBLE);
                    if (savedUnidadeTempoId != View.NO_ID) {
                        holder.unidadeTempoChipGroup.check(savedUnidadeTempoId);
                        Log.d("ExercicioFormAdapter", "Position: " + position + ", Restored unidadeTempoChipId after tipoSerie change: " + savedUnidadeTempoId);
                    } else if (!exercicio.getSeries().isEmpty()) {
                        String unidade = exercicio.getSeries().get(0).getUnidadeTempo();
                        int defaultUnidadeId = "sec".equals(unidade) ? R.id.chipSegundos : R.id.chipMinutos;
                        holder.unidadeTempoChipGroup.check(defaultUnidadeId);
                        exercicio.setUnidadeTempoSelecionadoId(defaultUnidadeId);
                        Log.d("ExercicioFormAdapter", "Position: " + position + ", Set default unidadeTempoChipId after tipoSerie change: " + defaultUnidadeId);
                    }
                } else {
                    scrollUnidadeTempo.setVisibility(View.GONE);
                    unidadeTempoLabel.setVisibility(View.GONE);
                }
                updateSerieLayout(holder, checkedId);
                for (int i = 0; i < holder.seriesContainer.getChildCount(); i++) {
                    View serieView = holder.seriesContainer.getChildAt(i);
                    EditText cargaInput = serieView.findViewById(R.id.serieCargaInput);
                    EditText repeticoesInput = serieView.findViewById(R.id.serieRepeticoesInput);
                    EditText tempoInput = serieView.findViewById(R.id.serieTempoInput);
                    setupSerieImeNavigation(cargaInput, repeticoesInput, tempoInput, i, holder);
                }
            }
        });

        holder.unidadeTempoChipGroup.setOnCheckedStateChangeListener(null);
        holder.unidadeTempoChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            int checkedId = group.getCheckedChipId();
            if (checkedId != View.NO_ID) {
                Log.d("ExercicioFormAdapter", "Position: " + position + ", unidadeTempoChip Changed to: " + checkedId);
                exercicio.setUnidadeTempoSelecionadoId(checkedId);
                String unidade = checkedId == R.id.chipSegundos ? "sec" : "min";
                for (Serie serie : exercicio.getSeries()) {
                    serie.setUnidadeTempo(unidade);
                }
            }
        });

        holder.seriesContainer.removeAllViews();
        LinearLayout seriesHeader = holder.itemView.findViewById(R.id.seriesHeader);
        seriesHeader.setVisibility(exercicio.getSeries().isEmpty() ? View.GONE : View.VISIBLE);

        while (cargaEditTextMatrix.size() <= position) {
            cargaEditTextMatrix.add(new ArrayList<>());
        }
        List<EditText> currentExerciseCargaInputs = new ArrayList<>();
        cargaEditTextMatrix.set(position, currentExerciseCargaInputs);

        for (int i = 0; i < exercicio.getSeries().size(); i++) {
            Serie serie = exercicio.getSeries().get(i);
            inflateSerieLayout(holder.seriesContainer, i, serie, holder);
            currentExerciseCargaInputs.add(holder.seriesContainer.getChildAt(i).findViewById(R.id.serieCargaInput));
        }

        int checkedId = holder.tipoSerieChipGroup.getCheckedChipId();
        if (checkedId != View.NO_ID) {
            updateSerieLayout(holder, checkedId);
        }

        if (exercicio.getSeries().size() > 1) {
            excluirSerieButton.setVisibility(View.VISIBLE);
        } else {
            excluirSerieButton.setVisibility(View.GONE);
        }

        holder.adicionarSerieButton.setOnClickListener(view -> {
            int currentExerciseAdapterPos = holder.getBindingAdapterPosition();
            if (currentExerciseAdapterPos == RecyclerView.NO_POSITION) return;

            Exercicio currentExercicio = exercicios.get(currentExerciseAdapterPos);
            Serie novaSerie = new Serie();
            novaSerie.setCarga("");
            novaSerie.setRepeticoes("");
            novaSerie.setTempo("");
            novaSerie.setUnidadeTempo("min");
            String tipoSerie = getTipoSerieFromChipId(holder.tipoSerieChipGroup.getCheckedChipId());
            novaSerie.setTipoSerie(tipoSerie);
            currentExercicio.getSeries().add(novaSerie);

            int novaSeriePosition = currentExercicio.getSeries().size() - 1;
            inflateSerieLayout(holder.seriesContainer, novaSeriePosition, novaSerie, holder);

            cargaEditTextMatrix.get(currentExerciseAdapterPos).add(holder.seriesContainer.getChildAt(novaSeriePosition).findViewById(R.id.serieCargaInput));

            if (novaSeriePosition > 0) {
                int prevPosition = novaSeriePosition - 1;
                View prevSerieView = holder.seriesContainer.getChildAt(prevPosition);
                EditText prevCargaInput = prevSerieView.findViewById(R.id.serieCargaInput);
                EditText prevRepeticoesInput = prevSerieView.findViewById(R.id.serieRepeticoesInput);
                EditText prevTempoInput = prevSerieView.findViewById(R.id.serieTempoInput);
                setupSerieImeNavigation(prevCargaInput, prevRepeticoesInput, prevTempoInput, prevPosition, holder);
            }

            updateSerieLayout(holder, holder.tipoSerieChipGroup.getCheckedChipId());
            seriesHeader.setVisibility(View.VISIBLE);

            if (currentExercicio.getSeries().size() > 1) {
                excluirSerieButton.setVisibility(View.VISIBLE);
            } else {
                excluirSerieButton.setVisibility(View.GONE);
            }

            scrollToView(holder.seriesContainer.getChildAt(novaSeriePosition));
        });

        excluirSerieButton.setOnClickListener(v -> {
            int currentExerciseAdapterPos = holder.getBindingAdapterPosition();
            if (currentExerciseAdapterPos != RecyclerView.NO_POSITION) {
                Exercicio currentExercicio = exercicios.get(currentExerciseAdapterPos);
                if (currentExercicio.getSeries().size() > 1) {
                    Log.d("ExercicioFormAdapter", "Position: " + currentExerciseAdapterPos + ", Before Exclusion - tipoSerieChipId: " + holder.tipoSerieChipGroup.getCheckedChipId() + ", unidadeTempoChipId: " + holder.unidadeTempoChipGroup.getCheckedChipId());

                    int lastSeriePosition = currentExercicio.getSeries().size() - 1;
                    currentExercicio.getSeries().remove(lastSeriePosition);
                    holder.seriesContainer.removeViewAt(lastSeriePosition);

                    for (int i = 0; i < holder.seriesContainer.getChildCount(); i++) {
                        TextView serieNumero = holder.seriesContainer.getChildAt(i).findViewById(R.id.serieNumero);
                        if (serieNumero != null) {
                            serieNumero.setText(String.valueOf(i + 1));
                        }
                    }

                    if (currentExercicio.getSeries().size() <= 1) {
                        excluirSerieButton.setVisibility(View.GONE);
                    }

                    notifyItemChanged(currentExerciseAdapterPos);
                }
            }
        });

        if (holder.getBindingAdapterPosition() == getItemCount() - 1) {
            holder.adicionarExercicioButton.setVisibility(View.VISIBLE);
        } else {
            holder.adicionarExercicioButton.setVisibility(View.GONE);
        }
        holder.adicionarExercicioButton.setOnClickListener(view -> {
            Exercicio novoExercicio = new Exercicio();
            novoExercicio.setNome("");
            novoExercicio.setTempoDescanso("1min 0s");
            Serie serieInicial = new Serie();
            serieInicial.setCarga("");
            serieInicial.setRepeticoes("");
            serieInicial.setTempo("");
            serieInicial.setUnidadeTempo("min");
            String tipoSerie = getTipoSerieFromChipId(holder.tipoSerieChipGroup.getCheckedChipId());
            serieInicial.setTipoSerie(tipoSerie);
            novoExercicio.getSeries().add(serieInicial);
            addExercicio(novoExercicio);
        });
    }

    private void inflateSerieLayout(ViewGroup seriesContainer, int seriePosition, Serie serie, ExercicioViewHolder holder) {
        LayoutInflater inflater = LayoutInflater.from(seriesContainer.getContext());
        View serieView = inflater.inflate(R.layout.item_serie_form, seriesContainer, false);

        TextView serieNumberText = serieView.findViewById(R.id.serieNumero);
        EditText serieCargaInput = serieView.findViewById(R.id.serieCargaInput);
        EditText serieRepeticoesInput = serieView.findViewById(R.id.serieRepeticoesInput);
        EditText serieTempoInput = serieView.findViewById(R.id.serieTempoInput);

        String tipoSerie = getTipoSerieFromChipId(holder.tipoSerieChipGroup.getCheckedChipId());
        serie.setTipoSerie(tipoSerie);

        serieNumberText.setText(String.valueOf(seriePosition + 1));

        serieCargaInput.setText(serie.getCarga());
        serieCargaInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                int pos = holder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && seriePosition < exercicios.get(pos).getSeries().size()) {
                    exercicios.get(pos).getSeries().get(seriePosition).setCarga(s.toString().trim());
                }
            }
        });

        serieRepeticoesInput.setText(serie.getRepeticoes());
        serieRepeticoesInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                int pos = holder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && seriePosition < exercicios.get(pos).getSeries().size()) {
                    exercicios.get(pos).getSeries().get(seriePosition).setRepeticoes(s.toString().trim());
                }
            }
        });

        serieTempoInput.setText(serie.getTempo());
        serieTempoInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                int pos = holder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && seriePosition < exercicios.get(pos).getSeries().size()) {
                    exercicios.get(pos).getSeries().get(seriePosition).setTempo(s.toString().trim());
                    int checkedId = holder.unidadeTempoChipGroup.getCheckedChipId();
                    String unidade = checkedId == R.id.chipSegundos ? "sec" : "min";
                    exercicios.get(pos).getSeries().get(seriePosition).setUnidadeTempo(unidade);
                }
            }
        });

        serieCargaInput.setOnClickListener(v -> {
            if (serieCargaInput.isFocusable()) {
                if (serieCargaInput.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) holder.itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.showSoftInput(serieCargaInput, InputMethodManager.SHOW_IMPLICIT);
                    scrollToView(serieCargaInput);
                }
            }
        });

        serieRepeticoesInput.setOnClickListener(v -> {
            if (serieRepeticoesInput.isFocusable()) {
                if (serieRepeticoesInput.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) holder.itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.showSoftInput(serieRepeticoesInput, InputMethodManager.SHOW_IMPLICIT);
                    scrollToView(serieRepeticoesInput);
                }
            }
        });

        serieTempoInput.setOnClickListener(v -> {
            if (serieTempoInput.isFocusable()) {
                if (serieTempoInput.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) holder.itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.showSoftInput(serieTempoInput, InputMethodManager.SHOW_IMPLICIT);
                    scrollToView(serieTempoInput);
                }
            }
        });

        setupSerieImeNavigation(serieCargaInput, serieRepeticoesInput, serieTempoInput, seriePosition, holder);

        seriesContainer.addView(serieView);
    }

    private void updateSerieLayout(ExercicioViewHolder holder, int checkedId) {
        int serieType = getSerieTypeFromChipId(checkedId);

        TextView cargaLabel = holder.itemView.findViewById(R.id.cargaLabel);
        TextView repsLabel = holder.itemView.findViewById(R.id.repsLabel);

        if (cargaLabel == null || repsLabel == null) {
            Log.e("ExercicioFormAdapter", "Labels are null - cargaLabel: " + (cargaLabel == null) + ", repsLabel: " + (repsLabel == null));
            return;
        }

        switch (serieType) {
            case TYPE_CARGA_REPS:
                cargaLabel.setText(R.string.label_kg);
                cargaLabel.setVisibility(View.VISIBLE);
                repsLabel.setText(R.string.label_reps);
                repsLabel.setVisibility(View.VISIBLE);
                break;
            case TYPE_TEMPO:
                cargaLabel.setText(R.string.label_tempo);
                cargaLabel.setVisibility(View.VISIBLE);
                repsLabel.setVisibility(View.GONE);
                break;
            case TYPE_REPETICOES:
                cargaLabel.setVisibility(View.GONE);
                repsLabel.setText(R.string.label_reps);
                repsLabel.setVisibility(View.VISIBLE);
                break;
            case TYPE_CARGA:
                cargaLabel.setText(R.string.label_kg);
                cargaLabel.setVisibility(View.VISIBLE);
                repsLabel.setVisibility(View.GONE);
                break;
        }

        int position = holder.getBindingAdapterPosition();
        if (position == RecyclerView.NO_POSITION || position >= exercicios.size()) {
            return;
        }
        Exercicio exercicio = exercicios.get(position);

        int seriesCount = exercicio.getSeries().size();
        int viewCount = holder.seriesContainer.getChildCount();
        if (seriesCount != viewCount) {
            Log.w("ExercicioFormAdapter", "Mismatch detected - seriesCount: " + seriesCount + ", viewCount: " + viewCount + " at position: " + position);
        }

        for (int i = 0; i < Math.min(seriesCount, viewCount); i++) {
            View serieView = holder.seriesContainer.getChildAt(i);
            if (serieView == null) {
                Log.e("ExercicioFormAdapter", "serieView is null at index: " + i);
                continue;
            }

            EditText cargaInput = serieView.findViewById(R.id.serieCargaInput);
            EditText repeticoesInput = serieView.findViewById(R.id.serieRepeticoesInput);
            EditText tempoInput = serieView.findViewById(R.id.serieTempoInput);
            LinearLayout fieldsContainer = serieView.findViewById(R.id.serieFieldsContainer);

            Log.d("ExercicioFormAdapter", "Views - cargaInput: " + (cargaInput != null) + ", repeticoesInput: " + (repeticoesInput != null) + ", tempoInput: " + (tempoInput != null) + ", fieldsContainer: " + (fieldsContainer != null));

            if (fieldsContainer == null) {
                Log.e("ExercicioFormAdapter", "fieldsContainer is null at index: " + i);
                continue;
            }

            Serie serie = exercicio.getSeries().get(i);
            String tipoSerie = getTipoSerieFromChipId(checkedId);
            serie.setTipoSerie(tipoSerie);

            switch (serieType) {
                case TYPE_CARGA_REPS:
                    serie.setTempo("");
                    if (cargaInput != null) {
                        cargaInput.setVisibility(View.VISIBLE);
                        cargaInput.setFocusable(true);
                        cargaInput.setFocusableInTouchMode(true);
                        if (fieldsContainer.indexOfChild(cargaInput) < 0) fieldsContainer.addView(cargaInput);
                    }
                    if (repeticoesInput != null) {
                        repeticoesInput.setVisibility(View.VISIBLE);
                        repeticoesInput.setFocusable(true);
                        repeticoesInput.setFocusableInTouchMode(true);
                        if (fieldsContainer.indexOfChild(repeticoesInput) < 0) fieldsContainer.addView(repeticoesInput);
                    }
                    if (tempoInput != null) {
                        tempoInput.setVisibility(View.GONE);
                        tempoInput.setText("");
                    }
                    break;
                case TYPE_TEMPO:
                    serie.setCarga("");
                    serie.setRepeticoes("");
                    if (tempoInput != null) {
                        tempoInput.setVisibility(View.VISIBLE);
                        tempoInput.setFocusable(true);
                        tempoInput.setFocusableInTouchMode(true);
                        if (fieldsContainer.indexOfChild(tempoInput) < 0) fieldsContainer.addView(tempoInput);
                    }
                    if (cargaInput != null) {
                        cargaInput.setVisibility(View.GONE);
                        cargaInput.setText("");
                    }
                    if (repeticoesInput != null) {
                        repeticoesInput.setVisibility(View.GONE);
                        repeticoesInput.setText("");
                    }
                    break;
                case TYPE_REPETICOES:
                    serie.setCarga("");
                    serie.setTempo("");
                    if (repeticoesInput != null) {
                        repeticoesInput.setVisibility(View.VISIBLE);
                        repeticoesInput.setFocusable(true);
                        repeticoesInput.setFocusableInTouchMode(true);
                        if (fieldsContainer.indexOfChild(repeticoesInput) < 0) fieldsContainer.addView(repeticoesInput);
                    }
                    if (cargaInput != null) {
                        cargaInput.setVisibility(View.GONE);
                        cargaInput.setText("");
                    }
                    if (tempoInput != null) {
                        tempoInput.setVisibility(View.GONE);
                        tempoInput.setText("");
                    }
                    break;
                case TYPE_CARGA:
                    serie.setRepeticoes("");
                    serie.setTempo("");
                    if (cargaInput != null) {
                        cargaInput.setVisibility(View.VISIBLE);
                        cargaInput.setFocusable(true);
                        cargaInput.setFocusableInTouchMode(true);
                        if (fieldsContainer.indexOfChild(cargaInput) < 0) fieldsContainer.addView(cargaInput);
                    }
                    if (repeticoesInput != null) {
                        repeticoesInput.setVisibility(View.GONE);
                        repeticoesInput.setText("");
                    }
                    if (tempoInput != null) {
                        tempoInput.setVisibility(View.GONE);
                        tempoInput.setText("");
                    }
                    break;
            }

            if (cargaInput != null) cargaInput.setText(serie.getCarga());
            if (repeticoesInput != null) repeticoesInput.setText(serie.getRepeticoes());
            if (tempoInput != null) tempoInput.setText(serie.getTempo());
        }
    }

    @Override
    public int getItemCount() {
        return exercicios.size();
    }

    public void addExercicio(Exercicio exercicio) {
        exercicios.add(exercicio);
        final int newPosition = exercicios.size() - 1;
        notifyItemInserted(newPosition);
        if (newPosition > 0) notifyItemChanged(newPosition - 1);
        if (exerciciosRecyclerView != null && nestedScrollView != null) {
            exerciciosRecyclerView.post(() -> {
                exerciciosRecyclerView.smoothScrollToPosition(newPosition);
                exerciciosRecyclerView.postDelayed(() -> {
                    RecyclerView.ViewHolder viewHolder = exerciciosRecyclerView.findViewHolderForAdapterPosition(newPosition);
                    View newItemView = viewHolder != null ? viewHolder.itemView : null;
                    if (newItemView != null) {
                        Rect r = new Rect();
                        nestedScrollView.getWindowVisibleDisplayFrame(r);
                        int[] loc = new int[2];
                        newItemView.getLocationOnScreen(loc);
                        int top = loc[1] - nestedScrollView.getTop() + nestedScrollView.getScrollY();
                        nestedScrollView.smoothScrollTo(0, Math.max(0, top - 60));
                    } else {
                        nestedScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                }, 250);
            });
        }
    }

    public void updateRestTime(int position, String selectedTime) {
        if (position >= 0 && position < exercicios.size()) {
            exercicios.get(position).setTempoDescanso(selectedTime);
            notifyItemChanged(position);
        }
    }

    public List<Exercicio> getExercicios() {
        return new ArrayList<>(exercicios);
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.exerciciosRecyclerView = recyclerView;
        if (recyclerView != null) recyclerView.setNestedScrollingEnabled(false);
    }

    private String getTipoSerieFromChipId(int checkedId) {
        if (checkedId == R.id.chipCargaReps) return "carga_reps";
        else if (checkedId == R.id.chipTempo) return "tempo";
        else if (checkedId == R.id.chipRepeticoes) return "repeticoes";
        else if (checkedId == R.id.chipCarga) return "carga";
        return "carga_reps";
    }

    private int getSerieTypeFromChipId(int checkedId) {
        if (checkedId == R.id.chipCargaReps) return TYPE_CARGA_REPS;
        else if (checkedId == R.id.chipTempo) return TYPE_TEMPO;
        else if (checkedId == R.id.chipRepeticoes) return TYPE_REPETICOES;
        else if (checkedId == R.id.chipCarga) return TYPE_CARGA;
        return TYPE_CARGA_REPS;
    }

    public static class ExercicioViewHolder extends RecyclerView.ViewHolder {
        final TextInputEditText exercicioNomeInput;
        final LinearLayout tempoDescansoContainer;
        final TextView tempoDescansoText;
        final LinearLayout seriesContainer;
        final MaterialButton adicionarSerieButton;
        final MaterialButton adicionarExercicioButton;
        final ChipGroup tipoSerieChipGroup;
        final ChipGroup unidadeTempoChipGroup;
        private final TextWatcher nomeWatcher;

        ExercicioViewHolder(@NonNull View itemView, ExercicioFormAdapter adapter) {
            super(itemView);
            exercicioNomeInput = itemView.findViewById(R.id.exercicioNomeInput);
            tempoDescansoContainer = itemView.findViewById(R.id.tempoDescansoContainer);
            tempoDescansoText = itemView.findViewById(R.id.tempoDescansoText);
            seriesContainer = itemView.findViewById(R.id.seriesContainer);
            adicionarSerieButton = itemView.findViewById(R.id.adicionarSerieButton);
            adicionarExercicioButton = itemView.findViewById(R.id.adicionarExercicioButton);
            tipoSerieChipGroup = itemView.findViewById(R.id.tipoSerieChipGroup);
            unidadeTempoChipGroup = itemView.findViewById(R.id.unidadeTempoChipGroup);

            exercicioNomeInput.setFocusable(false);
            exercicioNomeInput.setFocusableInTouchMode(false);

            nomeWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && adapter != null && pos < adapter.exercicios.size()) {
                        adapter.exercicios.get(pos).setNome(s.toString());
                    }
                }
            };
        }
    }
}