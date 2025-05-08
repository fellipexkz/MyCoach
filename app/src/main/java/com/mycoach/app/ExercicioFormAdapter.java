package com.mycoach.app;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class ExercicioFormAdapter extends RecyclerView.Adapter<ExercicioFormAdapter.ExercicioViewHolder> {

    private static final String TAG = "ExercicioFormAdapter";
    private List<Exercicio> exercicios;
    private RecyclerView exerciciosRecyclerView;

    public ExercicioFormAdapter() {
        this.exercicios = new ArrayList<>();
    }

    @NonNull
    @Override
    public ExercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercicio_form, parent, false);
        return new ExercicioViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ExercicioViewHolder holder, int position) {
        Exercicio exercicio = exercicios.get(position);

        holder.exercicioNomeInput.removeTextChangedListener(holder.nomeWatcher);
        holder.exercicioTempoDescansoInput.removeTextChangedListener(holder.tempoDescansoWatcher);
        holder.exercicioNomeInput.setText(exercicio.getNome());
        holder.exercicioTempoDescansoInput.setText(exercicio.getTempoDescanso());
        holder.exercicioNomeInput.addTextChangedListener(holder.nomeWatcher);
        holder.exercicioTempoDescansoInput.addTextChangedListener(holder.tempoDescansoWatcher);

        holder.seriesContainer.removeAllViews();

        for (int i = 0; i < exercicio.getSeries().size(); i++) {
            Serie serie = exercicio.getSeries().get(i);
            View serieView = LayoutInflater.from(holder.itemView.getContext())
                    .inflate(R.layout.item_serie_form, holder.seriesContainer, false);

            TextInputEditText serieCargaInput = serieView.findViewById(R.id.serieCargaInput);
            TextInputEditText serieRepeticoesInput = serieView.findViewById(R.id.serieRepeticoesInput);

            serieCargaInput.setText(serie.getCarga());
            serieRepeticoesInput.setText(serie.getRepeticoes());

            final int seriePosition = i;
            serieCargaInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        exercicios.get(adapterPosition).getSeries().get(seriePosition).setCarga(s.toString());
                    }
                }
            });

            serieRepeticoesInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        exercicios.get(adapterPosition).getSeries().get(seriePosition).setRepeticoes(s.toString());
                    }
                }
            });

            holder.seriesContainer.addView(serieView);
        }

        holder.adicionarSerieButton.setOnClickListener(v -> {
            Serie novaSerie = new Serie();
            novaSerie.setCarga("");
            novaSerie.setRepeticoes("");
            exercicio.getSeries().add(novaSerie);

            View serieView = LayoutInflater.from(holder.itemView.getContext())
                    .inflate(R.layout.item_serie_form, holder.seriesContainer, false);

            TextInputEditText serieCargaInput = serieView.findViewById(R.id.serieCargaInput);
            TextInputEditText serieRepeticoesInput = serieView.findViewById(R.id.serieRepeticoesInput);

            serieCargaInput.setText("");
            serieRepeticoesInput.setText("");

            final int seriePosition = exercicio.getSeries().size() - 1;
            serieCargaInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        exercicios.get(adapterPosition).getSeries().get(seriePosition).setCarga(s.toString());
                    }
                }
            });

            serieRepeticoesInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        exercicios.get(adapterPosition).getSeries().get(seriePosition).setRepeticoes(s.toString());
                    }
                }
            });

            holder.seriesContainer.addView(serieView);

            holder.itemView.requestLayout();
            if (exerciciosRecyclerView != null) {
                exerciciosRecyclerView.requestLayout();
            }
        });

        if (position == getItemCount() - 1) {
            holder.adicionarExercicioButton.setVisibility(View.VISIBLE);
        } else {
            holder.adicionarExercicioButton.setVisibility(View.GONE);
        }

        holder.adicionarExercicioButton.setOnClickListener(v -> {
            Exercicio novoExercicio = new Exercicio();
            novoExercicio.setNome("");
            novoExercicio.setTempoDescanso("");
            for (int i = 0; i < 3; i++) {
                Serie serie = new Serie();
                serie.setCarga("");
                serie.setRepeticoes("");
                novoExercicio.getSeries().add(serie);
            }
            addExercicio(novoExercicio);
        });

        Log.d(TAG, "onBindViewHolder - Posição: " + position + ", Total de itens: " + getItemCount() +
                ", Altura do item: " + holder.itemView.getHeight() +
                ", Visíveis: " + getVisibleItemCount() +
                ", Altura total do conteúdo: " + (exerciciosRecyclerView != null ? exerciciosRecyclerView.getLayoutManager().getHeight() : "null"));
    }

    @Override
    public int getItemCount() {
        return exercicios.size();
    }

    public void addExercicio(Exercicio exercicio) {
        exercicios.add(exercicio);
        notifyItemInserted(exercicios.size() - 1);
        if (exerciciosRecyclerView != null) {
            exerciciosRecyclerView.post(() -> {
                exerciciosRecyclerView.scrollToPosition(getItemCount() - 1);
                exerciciosRecyclerView.requestLayout();
                View parent = (View) exerciciosRecyclerView.getParent();
                if (parent != null) {
                    parent.requestLayout();
                }
                Log.d(TAG, "Recálculo forçado - Altura total: " + exerciciosRecyclerView.getHeight() +
                        ", Altura do LayoutManager: " + (exerciciosRecyclerView.getLayoutManager() != null ? exerciciosRecyclerView.getLayoutManager().getHeight() : "null"));
            });
        }
        if (exercicios.size() > 1) {
            notifyItemChanged(exercicios.size() - 2);
        }
        Log.d(TAG, "addExercicio - Total de itens após adição: " + exercicios.size() +
                ", Altura do RecyclerView: " + (exerciciosRecyclerView != null ? exerciciosRecyclerView.getHeight() : "null"));
    }

    public List<Exercicio> getExercicios() {
        List<Exercicio> updatedExercicios = new ArrayList<>();
        for (int i = 0; i < exercicios.size(); i++) {
            Exercicio exercicio = exercicios.get(i);
            Exercicio updatedExercicio = new Exercicio();
            RecyclerView.ViewHolder viewHolder = exerciciosRecyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolder != null) {
                View view = viewHolder.itemView;
                TextInputEditText nomeInput = view.findViewById(R.id.exercicioNomeInput);
                TextInputEditText tempoDescansoInput = view.findViewById(R.id.exercicioTempoDescansoInput);
                updatedExercicio.setNome(nomeInput.getText().toString());
                updatedExercicio.setTempoDescanso(tempoDescansoInput.getText().toString());

                LinearLayout seriesContainer = view.findViewById(R.id.seriesContainer);
                List<Serie> updatedSeries = new ArrayList<>();
                for (int j = 0; j < seriesContainer.getChildCount(); j++) {
                    View serieView = seriesContainer.getChildAt(j);
                    TextInputEditText serieCargaInput = serieView.findViewById(R.id.serieCargaInput);
                    TextInputEditText serieRepeticoesInput = serieView.findViewById(R.id.serieRepeticoesInput);

                    Serie updatedSerie = new Serie();
                    updatedSerie.setCarga(serieCargaInput.getText().toString());
                    updatedSerie.setRepeticoes(serieRepeticoesInput.getText().toString());
                    updatedSeries.add(updatedSerie);
                }
                updatedExercicio.setSeries(updatedSeries);
            } else {
                updatedExercicio.setNome(exercicio.getNome());
                updatedExercicio.setTempoDescanso(exercicio.getTempoDescanso());
                updatedExercicio.setSeries(exercicio.getSeries());
            }
            updatedExercicios.add(updatedExercicio);
        }
        return updatedExercicios;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.exerciciosRecyclerView = recyclerView;
        this.exerciciosRecyclerView.setNestedScrollingEnabled(true);
    }

    private int getVisibleItemCount() {
        if (exerciciosRecyclerView != null && exerciciosRecyclerView.getLayoutManager() != null) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) exerciciosRecyclerView.getLayoutManager();
            int firstVisible = layoutManager.findFirstVisibleItemPosition();
            int lastVisible = layoutManager.findLastVisibleItemPosition();
            if (firstVisible == RecyclerView.NO_POSITION || lastVisible == RecyclerView.NO_POSITION) {
                return 0;
            }
            return lastVisible - firstVisible + 1;
        }
        return 0;
    }

    static class ExercicioViewHolder extends RecyclerView.ViewHolder {
        TextInputEditText exercicioNomeInput;
        TextInputEditText exercicioTempoDescansoInput;
        LinearLayout seriesContainer;
        com.google.android.material.button.MaterialButton adicionarSerieButton;
        com.google.android.material.button.MaterialButton adicionarExercicioButton;
        private final TextWatcher nomeWatcher;
        private final TextWatcher tempoDescansoWatcher;

        ExercicioViewHolder(@NonNull View itemView, ExercicioFormAdapter adapter) {
            super(itemView);
            exercicioNomeInput = itemView.findViewById(R.id.exercicioNomeInput);
            exercicioTempoDescansoInput = itemView.findViewById(R.id.exercicioTempoDescansoInput);
            seriesContainer = itemView.findViewById(R.id.seriesContainer);
            adicionarSerieButton = itemView.findViewById(R.id.adicionarSerieButton);
            adicionarExercicioButton = itemView.findViewById(R.id.adicionarExercicioButton);

            nomeWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        adapter.exercicios.get(position).setNome(s.toString());
                    }
                }
            };

            tempoDescansoWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        adapter.exercicios.get(position).setTempoDescanso(s.toString());
                    }
                }
            };

            exercicioNomeInput.addTextChangedListener(nomeWatcher);
            exercicioTempoDescansoInput.addTextChangedListener(tempoDescansoWatcher);
        }
    }
}