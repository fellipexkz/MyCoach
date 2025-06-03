package com.mycoach.app;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExercicioAdapter extends RecyclerView.Adapter<ExercicioAdapter.ExercicioViewHolder> {

    private final List<Exercicio> exercicios;

    public ExercicioAdapter() {
        this.exercicios = new ArrayList<>();
    }

    public void setExercicios(List<Exercicio> exercicios) {
        List<Exercicio> novaLista = exercicios != null ? exercicios : new ArrayList<>();
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ExercicioDiffCallback(this.exercicios, novaLista));
        this.exercicios.clear();
        this.exercicios.addAll(novaLista);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ExercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercicio_detalhe, parent, false);
        return new ExercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExercicioViewHolder holder, int position) {
        Exercicio exercicio = exercicios.get(position);
        holder.exercicioNomeText.setText(exercicio.getNome());
        holder.exercicioTempoDescansoText.setText(exercicio.getTempoDescanso());

        if (holder.seriesContainer != null) {
            holder.seriesContainer.removeAllViews();

            List<Serie> series = exercicio.getSeries();

            String tipoSerie = series.isEmpty() ? "carga_reps" : series.get(0).getTipoSerie();

            if (holder.detalhesHeader != null) {
                if ("carga_reps".equals(tipoSerie)) {
                    holder.detalhesHeader.setText(R.string.label_peso_repeticoes);
                } else if ("carga".equals(tipoSerie)) {
                    holder.detalhesHeader.setText(R.string.label_detalhes_peso);
                } else if ("repeticoes".equals(tipoSerie)) {
                    holder.detalhesHeader.setText(R.string.label_detalhes_repeticoes);
                } else if ("tempo".equals(tipoSerie)) {
                    holder.detalhesHeader.setText(R.string.label_tempo);
                }
            }

            int padding_4dp_in_px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 4, holder.itemView.getContext().getResources().getDisplayMetrics());

            for (int i = 0; i < series.size(); i++) {
                Serie serie = series.get(i);

                LinearLayout row = new LinearLayout(holder.itemView.getContext());
                row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                row.setPadding(padding_4dp_in_px, padding_4dp_in_px, padding_4dp_in_px, padding_4dp_in_px);
                row.setOrientation(LinearLayout.HORIZONTAL);

                TextView serieNumberText = new TextView(holder.itemView.getContext());
                serieNumberText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                serieNumberText.setText(String.valueOf(i + 1));
                serieNumberText.setTextSize(14);
                serieNumberText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.on_surface));
                serieNumberText.setGravity(android.view.Gravity.CENTER);
                row.addView(serieNumberText);

                TextView detalhesText = new TextView(holder.itemView.getContext());
                detalhesText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
                detalhesText.setTextSize(14);
                detalhesText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.on_surface));
                detalhesText.setGravity(android.view.Gravity.CENTER);

                if ("carga_reps".equals(tipoSerie)) {
                    String carga = serie.getCarga().isEmpty() ? "-" : serie.getCarga();
                    String repeticoes = serie.getRepeticoes().isEmpty() ? "-" : serie.getRepeticoes();
                    detalhesText.setText(String.format("%s kg x %s reps", carga, repeticoes));
                } else if ("carga".equals(tipoSerie)) {
                    String carga = serie.getCarga().isEmpty() ? "-" : serie.getCarga();
                    detalhesText.setText(String.format("%s kg", carga));
                } else if ("repeticoes".equals(tipoSerie)) {
                    String repeticoes = serie.getRepeticoes().isEmpty() ? "-" : serie.getRepeticoes();
                    detalhesText.setText(String.format("%s reps", repeticoes));
                } else if ("tempo".equals(tipoSerie)) {
                    String tempo = serie.getTempo().isEmpty() ? "-" : serie.getTempo();
                    String unidade = serie.getUnidadeTempo().equals("sec") ? "s" : "min";
                    detalhesText.setText(String.format("%s %s", tempo, unidade));
                }

                row.addView(detalhesText);
                holder.seriesContainer.addView(row);
            }
        }
    }

    @Override
    public int getItemCount() {
        return exercicios.size();
    }

    public static class ExercicioViewHolder extends RecyclerView.ViewHolder {
        final TextView exercicioNomeText;
        final TextView exercicioTempoDescansoText;
        final LinearLayout seriesContainer;
        final TextView detalhesHeader;

        ExercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            exercicioNomeText = itemView.findViewById(R.id.exercicioNomeText);
            exercicioTempoDescansoText = itemView.findViewById(R.id.exercicioTempoDescansoText);
            seriesContainer = itemView.findViewById(R.id.seriesContainer);
            detalhesHeader = itemView.findViewById(R.id.detalhesHeader);
        }
    }

    private static class ExercicioDiffCallback extends DiffUtil.Callback {
        private final List<Exercicio> oldList;
        private final List<Exercicio> newList;

        ExercicioDiffCallback(List<Exercicio> oldList, List<Exercicio> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Exercicio oldExercicio = oldList.get(oldItemPosition);
            Exercicio newExercicio = newList.get(newItemPosition);

            boolean idsSaoIguais = oldExercicio.getId() == newExercicio.getId();
            boolean nomesSaoIguais = Objects.equals(oldExercicio.getNome(), newExercicio.getNome());
            boolean descansosSaoIguais = Objects.equals(oldExercicio.getTempoDescanso(), newExercicio.getTempoDescanso());
            boolean seriesSaoIguais = Objects.equals(oldExercicio.getSeries(), newExercicio.getSeries());

            return idsSaoIguais && nomesSaoIguais && descansosSaoIguais && seriesSaoIguais;
        }
    }
}