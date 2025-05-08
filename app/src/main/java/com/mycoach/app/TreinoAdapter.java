package com.mycoach.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class TreinoAdapter extends RecyclerView.Adapter<TreinoAdapter.TreinoViewHolder> {

    private List<Treino> treinos;
    private OnTreinoClickListener listener;

    public interface OnTreinoClickListener {
        void onTreinoClick(Treino treino);
    }

    public TreinoAdapter(OnTreinoClickListener listener) {
        this.treinos = new ArrayList<>();
        this.listener = listener;
    }

    public void setTreinos(List<Treino> treinos) {
        this.treinos = treinos != null ? treinos : new ArrayList<>();
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
        Treino treino = treinos.get(position);
        holder.treinoNomeText.setText(treino.getNome());
        int exerciciosCount = treino.getExercicios() != null ? treino.getExercicios().size() : 0;
        holder.treinoExerciciosCountText.setText(exerciciosCount + " exercício(s)");
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTreinoClick(treino);
            }
        });
    }

    @Override
    public int getItemCount() {
        return treinos.size();
    }

    static class TreinoViewHolder extends RecyclerView.ViewHolder {
        TextView treinoNomeText;
        TextView treinoExerciciosCountText;

        TreinoViewHolder(@NonNull View itemView) {
            super(itemView);
            treinoNomeText = itemView.findViewById(R.id.treinoNome);
            treinoExerciciosCountText = itemView.findViewById(R.id.treinoExerciciosCount);
        }
    }
}