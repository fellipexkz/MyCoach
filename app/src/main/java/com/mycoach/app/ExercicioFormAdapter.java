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
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class ExercicioFormAdapter extends RecyclerView.Adapter<ExercicioFormAdapter.ExercicioViewHolder> {

    private static final String TAG = "ExercicioFormAdapter";
    private final List<Exercicio> exercicios;
    private RecyclerView exerciciosRecyclerView;
    private final NestedScrollView nestedScrollView;
    private final FragmentManager fragmentManager;
    private final List<List<EditText>> cargaEditTextMatrix = new ArrayList<>();

    public ExercicioFormAdapter(FragmentManager fragmentManager, NestedScrollView nestedScrollView) {
        this.exercicios = new ArrayList<>();
        this.fragmentManager = fragmentManager;
        this.nestedScrollView = nestedScrollView;
    }

    @NonNull
    @Override
    public ExercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercicio_form, parent, false);
        return new ExercicioViewHolder(view, this);
    }

    private void scrollToView(final View viewToScrollTo) {
        if (viewToScrollTo == null || nestedScrollView == null) {
            return;
        }

        viewToScrollTo.postDelayed(() -> {
            Rect r = new Rect();
            nestedScrollView.getWindowVisibleDisplayFrame(r);
            int visibleWindowHeight = r.bottom - r.top;

            int[] focusedViewLocationOnScreen = new int[2];
            viewToScrollTo.getLocationOnScreen(focusedViewLocationOnScreen);
            int focusedViewBottomOnScreen = focusedViewLocationOnScreen[1] + viewToScrollTo.getHeight();

            int desiredBottomMargin = 20;
            int targetVisibleBottomForEditText = visibleWindowHeight - desiredBottomMargin;

            if (focusedViewBottomOnScreen > targetVisibleBottomForEditText) {
                int scrollAmountNeeded = focusedViewBottomOnScreen - targetVisibleBottomForEditText;
                nestedScrollView.smoothScrollBy(0, scrollAmountNeeded);
                Log.d(TAG, "Rolagem programática por: " + scrollAmountNeeded + " para " + viewToScrollTo.getId());
            }
        }, 200);
    }

    private void setupSerieImeNavigation(
            Context context,
            EditText cargaInput,
            EditText repeticoesInput,
            boolean isLastSerieInThisExercise,
            ExercicioViewHolder currentExerciseHolder,
            int currentSeriePositionInExercise
    ) {
        cargaInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        cargaInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                repeticoesInput.setFocusable(true);
                repeticoesInput.setFocusableInTouchMode(true);
                if (repeticoesInput.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.showSoftInput(repeticoesInput, InputMethodManager.SHOW_IMPLICIT);
                    scrollToView(repeticoesInput);
                }
                return true;
            }
            return false;
        });

        if (!isLastSerieInThisExercise) {
            repeticoesInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            repeticoesInput.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    int nextSerieVisualIndex = currentSeriePositionInExercise + 1;
                    if (nextSerieVisualIndex < currentExerciseHolder.seriesContainer.getChildCount()) {
                        View nextSerieView = currentExerciseHolder.seriesContainer.getChildAt(nextSerieVisualIndex);
                        EditText nextCargaInput = nextSerieView.findViewById(R.id.serieCargaInput);
                        if (nextCargaInput != null) {
                            nextCargaInput.setFocusable(true);
                            nextCargaInput.setFocusableInTouchMode(true);
                            if (nextCargaInput.requestFocus()) {
                                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm != null) imm.showSoftInput(nextCargaInput, InputMethodManager.SHOW_IMPLICIT);
                                scrollToView(nextCargaInput);
                            }
                            return true;
                        }
                    }
                }
                return false;
            });
        } else {
            boolean isLastExerciseOverall = (currentExerciseHolder.getBindingAdapterPosition() == exercicios.size() - 1);

            if (!isLastExerciseOverall) {
                repeticoesInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                repeticoesInput.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        int nextExercisePosition = currentExerciseHolder.getBindingAdapterPosition() + 1;
                        if (exerciciosRecyclerView != null) {
                            exerciciosRecyclerView.smoothScrollToPosition(nextExercisePosition);
                            exerciciosRecyclerView.postDelayed(() -> {
                                RecyclerView.ViewHolder nextVH = exerciciosRecyclerView.findViewHolderForAdapterPosition(nextExercisePosition);
                                if (nextVH instanceof ExercicioViewHolder) {
                                    TextInputEditText nextExercicioNomeInput = ((ExercicioViewHolder) nextVH).exercicioNomeInput;
                                    nextExercicioNomeInput.setFocusable(true);
                                    nextExercicioNomeInput.setFocusableInTouchMode(true);
                                    if (nextExercicioNomeInput.requestFocus()) {
                                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                        if (imm != null) imm.showSoftInput(nextExercicioNomeInput, InputMethodManager.SHOW_IMPLICIT);
                                        scrollToView(nextExercicioNomeInput);
                                    }
                                } else {
                                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                    v.clearFocus();
                                }
                            }, 150);
                            return true;
                        }
                    }
                    return false;
                });
            } else {
                repeticoesInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
                repeticoesInput.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        v.clearFocus();
                        return true;
                    }
                    return false;
                });
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ExercicioViewHolder holder, int position) {
        Exercicio exercicio = exercicios.get(position);

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
            Log.d(TAG, "exercicioNomeInput clicado na posição " + holder.getBindingAdapterPosition());
        });

        if (exercicio.getSeries().isEmpty()) {
            holder.exercicioNomeInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
            holder.exercicioNomeInput.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) holder.itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    v.clearFocus();
                    return true;
                }
                return false;
            });
        } else {
            holder.exercicioNomeInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            holder.exercicioNomeInput.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (holder.seriesContainer.getChildCount() > 0) {
                        View firstSerieView = holder.seriesContainer.getChildAt(0);
                        EditText firstSerieCargaInput = firstSerieView.findViewById(R.id.serieCargaInput);
                        if (firstSerieCargaInput != null) {
                            firstSerieCargaInput.setFocusable(true);
                            firstSerieCargaInput.setFocusableInTouchMode(true);
                            if (firstSerieCargaInput.requestFocus()) {
                                InputMethodManager imm = (InputMethodManager) holder.itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm != null) imm.showSoftInput(firstSerieCargaInput, InputMethodManager.SHOW_IMPLICIT);
                                scrollToView(firstSerieCargaInput);
                            }
                            return true;
                        }
                    }
                    InputMethodManager imm = (InputMethodManager) holder.itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    v.clearFocus();
                    return true;
                }
                return false;
            });
        }

        String tempoDescanso = exercicio.getTempoDescanso();
        holder.tempoDescansoText.setText(String.format(
                holder.itemView.getContext().getResources().getString(R.string.tempo_descanso_format),
                tempoDescanso == null || tempoDescanso.isEmpty() ?
                        holder.itemView.getContext().getResources().getString(R.string.tempo_descanso_desativado) :
                        tempoDescanso
        ));

        holder.tempoDescansoContainer.setOnClickListener(view -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION && fragmentManager != null) {
                Exercicio exercicioAtual = exercicios.get(currentPosition);
                String exerciseName = exercicioAtual.getNome().isEmpty() ? null : exercicioAtual.getNome();
                String currentRestTime = exercicioAtual.getTempoDescanso();

                RestTimeBottomSheetFragment bottomSheet = RestTimeBottomSheetFragment.newInstance(
                        currentPosition,
                        exerciseName,
                        currentRestTime,
                        this
                );
                bottomSheet.show(fragmentManager, "RestTimeBottomSheet");
            }
        });

        holder.seriesContainer.removeAllViews();

        while (cargaEditTextMatrix.size() <= position) {
            cargaEditTextMatrix.add(new ArrayList<>());
        }
        List<EditText> currentExerciseCargaInputs = new ArrayList<>();
        cargaEditTextMatrix.set(position, currentExerciseCargaInputs);

        for (int i = 0; i < exercicio.getSeries().size(); i++) {
            Serie serie = exercicio.getSeries().get(i);
            View serieView = LayoutInflater.from(holder.itemView.getContext())
                    .inflate(R.layout.item_serie_form, holder.seriesContainer, false);

            TextView serieNumero = serieView.findViewById(R.id.serieNumero);
            EditText serieCargaInput = serieView.findViewById(R.id.serieCargaInput);
            EditText serieRepeticoesInput = serieView.findViewById(R.id.serieRepeticoesInput);

            serieCargaInput.setFocusable(false);
            serieCargaInput.setFocusableInTouchMode(false);
            serieRepeticoesInput.setFocusable(false);
            serieRepeticoesInput.setFocusableInTouchMode(false);

            serieNumero.setText(String.valueOf(i + 1));
            serieCargaInput.setText(serie.getCarga());
            serieRepeticoesInput.setText(serie.getRepeticoes());

            currentExerciseCargaInputs.add(serieCargaInput);

            final int seriePosition = i;

            serieCargaInput.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) {
                    int adapterPosition = holder.getBindingAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION && seriePosition < exercicios.get(adapterPosition).getSeries().size()) {
                        exercicios.get(adapterPosition).getSeries().get(seriePosition).setCarga(s.toString());
                    }
                }
            });
            serieRepeticoesInput.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) {
                    int adapterPosition = holder.getBindingAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION && seriePosition < exercicios.get(adapterPosition).getSeries().size()) {
                        exercicios.get(adapterPosition).getSeries().get(seriePosition).setRepeticoes(s.toString());
                    }
                }
            });

            serieCargaInput.setOnClickListener(v -> {
                serieCargaInput.setFocusable(true);
                serieCargaInput.setFocusableInTouchMode(true);
                serieCargaInput.requestFocus();
                int currentPosition = holder.getBindingAdapterPosition();
                if (exerciciosRecyclerView != null && currentPosition != RecyclerView.NO_POSITION) {
                    exerciciosRecyclerView.smoothScrollToPosition(currentPosition);
                }
                InputMethodManager imm = (InputMethodManager) holder.itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.showSoftInput(serieCargaInput, InputMethodManager.SHOW_IMPLICIT);
                scrollToView(serieCargaInput);
            });
            serieRepeticoesInput.setOnClickListener(v -> {
                serieRepeticoesInput.setFocusable(true);
                serieRepeticoesInput.setFocusableInTouchMode(true);
                serieRepeticoesInput.requestFocus();
                int currentPosition = holder.getBindingAdapterPosition();
                if (exerciciosRecyclerView != null && currentPosition != RecyclerView.NO_POSITION) {
                    exerciciosRecyclerView.smoothScrollToPosition(currentPosition);
                }
                InputMethodManager imm = (InputMethodManager) holder.itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.showSoftInput(serieRepeticoesInput, InputMethodManager.SHOW_IMPLICIT);
                scrollToView(serieRepeticoesInput);
            });

            serieCargaInput.setNextFocusForwardId(serieRepeticoesInput.getId());
            boolean isLastSerie = (i == exercicio.getSeries().size() - 1);
            setupSerieImeNavigation(holder.itemView.getContext(), serieCargaInput, serieRepeticoesInput, isLastSerie, holder, i);

            holder.seriesContainer.addView(serieView);
        }

        holder.adicionarSerieButton.setOnClickListener(view -> {
            int currentExerciseAdapterPos = holder.getBindingAdapterPosition();
            if (currentExerciseAdapterPos == RecyclerView.NO_POSITION) return;

            Exercicio currentExercicio = exercicios.get(currentExerciseAdapterPos);
            Serie novaSerie = new Serie();
            novaSerie.setCarga("");
            novaSerie.setRepeticoes("");
            currentExercicio.getSeries().add(novaSerie);

            View serieView = LayoutInflater.from(holder.itemView.getContext())
                    .inflate(R.layout.item_serie_form, holder.seriesContainer, false);
            TextView serieNumero = serieView.findViewById(R.id.serieNumero);
            EditText serieCargaInput = serieView.findViewById(R.id.serieCargaInput);
            EditText serieRepeticoesInput = serieView.findViewById(R.id.serieRepeticoesInput);

            serieCargaInput.setFocusable(false);
            serieCargaInput.setFocusableInTouchMode(false);
            serieRepeticoesInput.setFocusable(false);
            serieRepeticoesInput.setFocusableInTouchMode(false);

            serieNumero.setText(String.valueOf(currentExercicio.getSeries().size()));
            serieCargaInput.setText("");
            serieRepeticoesInput.setText("");

            cargaEditTextMatrix.get(currentExerciseAdapterPos).add(serieCargaInput);
            final int novaSeriePositionInExercise = currentExercicio.getSeries().size() - 1;

            serieCargaInput.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) {
                    int adapterPos = holder.getBindingAdapterPosition();
                    if (adapterPos != RecyclerView.NO_POSITION &&
                            novaSeriePositionInExercise < exercicios.get(adapterPos).getSeries().size()) {
                        exercicios.get(adapterPos).getSeries().get(novaSeriePositionInExercise).setCarga(s.toString());
                    }
                }
            });
            serieRepeticoesInput.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) {
                    int adapterPos = holder.getBindingAdapterPosition();
                    if (adapterPos != RecyclerView.NO_POSITION &&
                            novaSeriePositionInExercise < exercicios.get(adapterPos).getSeries().size()) {
                        exercicios.get(adapterPos).getSeries().get(novaSeriePositionInExercise).setRepeticoes(s.toString());
                    }
                }
            });

            serieCargaInput.setOnClickListener(v_clk -> {
                serieCargaInput.setFocusable(true);
                serieCargaInput.setFocusableInTouchMode(true);
                serieCargaInput.requestFocus();
                int currentPosition = holder.getBindingAdapterPosition();
                if (exerciciosRecyclerView != null && currentPosition != RecyclerView.NO_POSITION) {
                    exerciciosRecyclerView.smoothScrollToPosition(currentPosition);
                }
                InputMethodManager imm = (InputMethodManager) holder.itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.showSoftInput(serieCargaInput, InputMethodManager.SHOW_IMPLICIT);
                scrollToView(serieCargaInput);
            });
            serieRepeticoesInput.setOnClickListener(v_clk -> {
                serieRepeticoesInput.setFocusable(true);
                serieRepeticoesInput.setFocusableInTouchMode(true);
                serieRepeticoesInput.requestFocus();
                int currentPosition = holder.getBindingAdapterPosition();
                if (exerciciosRecyclerView != null && currentPosition != RecyclerView.NO_POSITION) {
                    exerciciosRecyclerView.smoothScrollToPosition(currentPosition);
                }
                InputMethodManager imm = (InputMethodManager) holder.itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.showSoftInput(serieRepeticoesInput, InputMethodManager.SHOW_IMPLICIT);
                scrollToView(serieRepeticoesInput);
            });

            serieCargaInput.setNextFocusForwardId(serieRepeticoesInput.getId());
            boolean isNovaSerieUltimaNoExercicio = true;
            setupSerieImeNavigation(holder.itemView.getContext(), serieCargaInput, serieRepeticoesInput, isNovaSerieUltimaNoExercicio, holder, novaSeriePositionInExercise);

            if (novaSeriePositionInExercise > 0) {
                int penultimaSerieIndexNoExercicio = novaSeriePositionInExercise - 1;
                View penultimaSerieView = holder.seriesContainer.getChildAt(penultimaSerieIndexNoExercicio);
                if (penultimaSerieView != null) {
                    EditText penultimoCargaInput = penultimaSerieView.findViewById(R.id.serieCargaInput);
                    EditText penultimoRepeticoesInput = penultimaSerieView.findViewById(R.id.serieRepeticoesInput);
                    if (penultimoCargaInput != null && penultimoRepeticoesInput != null) {
                        setupSerieImeNavigation(holder.itemView.getContext(), penultimoCargaInput, penultimoRepeticoesInput, false, holder, penultimaSerieIndexNoExercicio);
                    }
                }
            }

            holder.seriesContainer.addView(serieView);

            scrollToView(serieView);
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
            novoExercicio.getSeries().add(serieInicial);
            addExercicio(novoExercicio);
        });
    }

    @Override
    public int getItemCount() {
        return exercicios.size();
    }

    public void addExercicio(Exercicio exercicio) {
        exercicios.add(exercicio);
        final int newPosition = exercicios.size() - 1;
        notifyItemInserted(newPosition);

        if (newPosition > 0) {
            notifyItemChanged(newPosition - 1);
        }

        if (exerciciosRecyclerView != null && nestedScrollView != null) {
            exerciciosRecyclerView.post(() -> {
                exerciciosRecyclerView.smoothScrollToPosition(newPosition);

                exerciciosRecyclerView.postDelayed(() -> {
                    View newItemView = null;
                    RecyclerView.LayoutManager layoutManager = exerciciosRecyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        newItemView = layoutManager.findViewByPosition(newPosition);
                    }

                    if (newItemView == null) {
                        RecyclerView.ViewHolder viewHolder = exerciciosRecyclerView.findViewHolderForAdapterPosition(newPosition);
                        if (viewHolder != null) {
                            newItemView = viewHolder.itemView;
                        }
                    }

                    if (newItemView != null) {
                        Log.d(TAG, "addExercicio: View do novo item encontrada na posição " + newPosition);

                        Rect visibleDisplayFrame = new Rect();
                        nestedScrollView.getWindowVisibleDisplayFrame(visibleDisplayFrame);

                        int[] newItemViewLocationOnScreen = new int[2];
                        newItemView.getLocationOnScreen(newItemViewLocationOnScreen);
                        int newItemViewTopOnScreen = newItemViewLocationOnScreen[1];

                        int[] nestedScrollViewLocationOnScreen = new int[2];
                        nestedScrollView.getLocationOnScreen(nestedScrollViewLocationOnScreen);

                        int newItemViewTopRelativeToNestedScrollViewContent =
                                (newItemViewTopOnScreen - nestedScrollViewLocationOnScreen[1]) + nestedScrollView.getScrollY();

                        int desiredTopOffset = 60;
                        int scrollToY = newItemViewTopRelativeToNestedScrollViewContent - desiredTopOffset;
                        scrollToY = Math.max(0, scrollToY);

                        nestedScrollView.smoothScrollTo(0, scrollToY);
                        Log.d(TAG, "addExercicio: Rolando NestedScrollView para Y: " + scrollToY);
                    } else {
                        Log.w(TAG, "addExercicio: View do novo item NÃO encontrada para posição " + newPosition + ". Rolando para o final.");
                        nestedScrollView.post(() -> nestedScrollView.fullScroll(View.FOCUS_DOWN));
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
        if (this.exerciciosRecyclerView != null) {
            this.exerciciosRecyclerView.setNestedScrollingEnabled(false);
        }
    }

    public static class ExercicioViewHolder extends RecyclerView.ViewHolder {
        final TextInputEditText exercicioNomeInput;
        final LinearLayout tempoDescansoContainer;
        final TextView tempoDescansoText;
        final LinearLayout seriesContainer;
        final com.google.android.material.button.MaterialButton adicionarSerieButton;
        final com.google.android.material.button.MaterialButton adicionarExercicioButton;
        private final TextWatcher nomeWatcher;

        ExercicioViewHolder(@NonNull View itemView, ExercicioFormAdapter adapter) {
            super(itemView);
            exercicioNomeInput = itemView.findViewById(R.id.exercicioNomeInput);
            tempoDescansoContainer = itemView.findViewById(R.id.tempoDescansoContainer);
            tempoDescansoText = itemView.findViewById(R.id.tempoDescansoText);
            seriesContainer = itemView.findViewById(R.id.seriesContainer);
            adicionarSerieButton = itemView.findViewById(R.id.adicionarSerieButton);
            adicionarExercicioButton = itemView.findViewById(R.id.adicionarExercicioButton);

            exercicioNomeInput.setFocusable(false);
            exercicioNomeInput.setFocusableInTouchMode(false);

            nomeWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (adapter != null && position < adapter.exercicios.size()) {
                            adapter.exercicios.get(position).setNome(s.toString());
                        }
                    }
                }
            };
        }
    }
}