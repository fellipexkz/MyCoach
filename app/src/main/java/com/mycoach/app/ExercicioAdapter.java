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

                TextView pesoRepText = new TextView(holder.itemView.getContext());
                pesoRepText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
                pesoRepText.setText(String.format(holder.itemView.getContext().getResources().getString(R.string.serie_peso_repeticoes_format), serie.getCarga(), serie.getRepeticoes()));
                pesoRepText.setTextSize(14);
                pesoRepText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.on_surface));
                pesoRepText.setGravity(android.view.Gravity.CENTER);
                row.addView(pesoRepText);

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

        ExercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            exercicioNomeText = itemView.findViewById(R.id.exercicioNomeText);
            exercicioTempoDescansoText = itemView.findViewById(R.id.exercicioTempoDescansoText);
            seriesContainer = itemView.findViewById(R.id.seriesContainer);
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