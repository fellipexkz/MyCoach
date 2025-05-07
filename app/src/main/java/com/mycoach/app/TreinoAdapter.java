package com.mycoach.app;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TreinoAdapter extends RecyclerView.Adapter<TreinoAdapter.TreinoViewHolder> {

    private List<Treino> treinos;

    public TreinoAdapter() {
        this.treinos = new java.util.ArrayList<>();
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
        Treino treino = treinos.get(position);
        holder.treinoNome.setText(treino.getNome());
        holder.treinoDescricao.setText(treino.getDescricao());
        holder.treinoData.setText(treino.getData());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetalhesTreinoActivity.class);
            intent.putExtra("treino_id", treino.getId());
            intent.putExtra("treino_nome", treino.getNome());
            intent.putExtra("treino_descricao", treino.getDescricao());
            intent.putExtra("treino_data", treino.getData());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return treinos.size();
    }

    public void setTreinos(List<Treino> treinos) {
        this.treinos.clear();
        this.treinos.addAll(treinos);
        notifyDataSetChanged();
    }

    static class TreinoViewHolder extends RecyclerView.ViewHolder {
        TextView treinoNome;
        TextView treinoDescricao;
        TextView treinoData;

        TreinoViewHolder(@NonNull View itemView) {
            super(itemView);
            treinoNome = itemView.findViewById(R.id.treinoNome);
            treinoDescricao = itemView.findViewById(R.id.treinoDescricao);
            treinoData = itemView.findViewById(R.id.treinoData);
        }
    }
}