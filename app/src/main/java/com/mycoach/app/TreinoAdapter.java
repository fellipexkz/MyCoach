package com.mycoach.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TreinoAdapter extends RecyclerView.Adapter<TreinoAdapter.TreinoViewHolder> {

    private List<Treino> listaTreinos;

    public TreinoAdapter() {
        this.listaTreinos = new ArrayList<>();
    }

    public void setTreinos(List<Treino> treinos) {
        this.listaTreinos.clear();
        this.listaTreinos.addAll(treinos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TreinoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_treino, parent, false);
        return new TreinoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TreinoViewHolder holder, int position) {
        Treino treino = listaTreinos.get(position);
        holder.nomeTextView.setText(treino.getNome());
        holder.descricaoTextView.setText(treino.getDescricao() != null ? treino.getDescricao() : "Sem descrição");
        holder.dataTextView.setText(treino.getData());
    }

    @Override
    public int getItemCount() {
        return listaTreinos.size();
    }

    static class TreinoViewHolder extends RecyclerView.ViewHolder {
        TextView nomeTextView, descricaoTextView, dataTextView;

        TreinoViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeTextView = itemView.findViewById(R.id.workoutNameTextView);
            descricaoTextView = itemView.findViewById(R.id.workoutDescriptionTextView);
            dataTextView = itemView.findViewById(R.id.workoutDateTextView);
        }
    }
}