package com.mycoach.app;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class ExercicioAdapter extends RecyclerView.Adapter<ExercicioAdapter.ExercicioViewHolder> {

    private List<Exercicio> exercicios;

    public ExercicioAdapter() {
        this.exercicios = new ArrayList<>();
    }

    public void setExercicios(List<Exercicio> exercicios) {
        this.exercicios = exercicios != null ? exercicios : new ArrayList<>();
        Log.d("ExercicioAdapter", "Exercícios recebidos: " + this.exercicios.size());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercicio_detalhe, parent, false);
        Log.d("ExercicioAdapter", "Layout inflado: item_exercicio_detalhe");
        return new ExercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExercicioViewHolder holder, int position) {
        Exercicio exercicio = exercicios.get(position);
        Log.d("ExercicioAdapter", "Exibindo exercício - Nome: " + exercicio.getNome() + ", Séries: " + exercicio.getSeries().size() + ", ID associado: " + exercicio.getId() + ", ID Treino: " + exercicio.getTreinoId());
        holder.exercicioNomeText.setText(exercicio.getNome());
        holder.exercicioTempoDescansoText.setText(exercicio.getTempoDescanso());

        holder.seriesTable.removeViews(1, holder.seriesTable.getChildCount() - 1);
        Log.d("ExercicioAdapter", "Linhas removidas da tabela, restam: " + (holder.seriesTable.getChildCount() - 1));

        List<Serie> series = exercicio.getSeries();
        Log.d("ExercicioAdapter", "Número de séries para este exercício: " + series.size());
        for (int i = 0; i < series.size(); i++) {
            Serie serie = series.get(i);

            TableRow row = new TableRow(holder.itemView.getContext());
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));
            row.setPadding(0, 4, 0, 4);

            TextView serieNumberText = new TextView(holder.itemView.getContext());
            serieNumberText.setLayoutParams(new TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
            ));
            serieNumberText.setText(String.valueOf(i + 1));
            serieNumberText.setTextSize(14);
            serieNumberText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.on_surface));
            serieNumberText.setGravity(android.view.Gravity.CENTER);
            row.addView(serieNumberText);

            TextView pesoRepText = new TextView(holder.itemView.getContext());
            pesoRepText.setLayoutParams(new TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    2f
            ));
            pesoRepText.setText(serie.getCarga() + "kg x " + serie.getRepeticoes() + " repetições");
            pesoRepText.setTextSize(14);
            pesoRepText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.on_surface));
            pesoRepText.setGravity(android.view.Gravity.CENTER);
            row.addView(pesoRepText);

            holder.seriesTable.addView(row);
            Log.d("ExercicioAdapter", "Adicionada linha para série " + (i + 1) + ": " + serie.getCarga() + "kg x " + serie.getRepeticoes() + " repetições");
        }
    }

    @Override
    public int getItemCount() {
        return exercicios.size();
    }

    static class ExercicioViewHolder extends RecyclerView.ViewHolder {
        TextView exercicioNomeText;
        TextView exercicioTempoDescansoText;
        TableLayout seriesTable;

        ExercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            exercicioNomeText = itemView.findViewById(R.id.exercicioNomeText);
            exercicioTempoDescansoText = itemView.findViewById(R.id.exercicioTempoDescansoText);
            seriesTable = itemView.findViewById(R.id.seriesTable);
            if (seriesTable == null) {
                Log.e("ExercicioViewHolder", "seriesTable não encontrado no layout!");
            }
        }
    }
}