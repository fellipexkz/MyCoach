package com.mycoach.app;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
        return new ExercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExercicioViewHolder holder, int position) {
        Exercicio exercicio = exercicios.get(position);
        Log.d("ExercicioAdapter", "Exibindo exercício - Nome: " + exercicio.getNome() + ", Séries: " + exercicio.getSeries().size()+", ID associado: "+exercicio.getId()+ ", ID Treino: "+exercicio.getTreinoId());
        holder.exercicioNomeText.setText(exercicio.getNome());
        holder.exercicioTempoDescansoText.setText(exercicio.getTempoDescanso());

        SerieDetalheAdapter serieAdapter = new SerieDetalheAdapter(exercicio.getSeries());
        holder.seriesRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.seriesRecyclerView.setAdapter(serieAdapter);
    }

    @Override
    public int getItemCount() {
        return exercicios.size();
    }

    static class ExercicioViewHolder extends RecyclerView.ViewHolder {
        TextView exercicioNomeText;
        TextView exercicioTempoDescansoText;
        RecyclerView seriesRecyclerView;

        ExercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            exercicioNomeText = itemView.findViewById(R.id.exercicioNomeText);
            exercicioTempoDescansoText = itemView.findViewById(R.id.exercicioTempoDescansoText);
            seriesRecyclerView = itemView.findViewById(R.id.seriesRecyclerView);
        }
    }
}