package com.mycoach.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class TreinoAdapter extends RecyclerView.Adapter<TreinoAdapter.TreinoViewHolder> {

    private final List<Treino> treinos;
    private final OnTreinoClickListener listener;

    public interface OnTreinoClickListener {
        void onTreinoClick(Treino treino);
    }

    public TreinoAdapter(OnTreinoClickListener listener) {
        this.treinos = new ArrayList<>();
        this.listener = listener;
    }

    public void setTreinos(List<Treino> treinos) {
        List<Treino> novaLista = treinos != null ? treinos : new ArrayList<>();
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new TreinoDiffCallback(this.treinos, novaLista));
        this.treinos.clear();
        this.treinos.addAll(novaLista);
        diffResult.dispatchUpdatesTo(this);
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
        holder.treinoExerciciosCountText.setText(
                holder.itemView.getContext().getResources().getQuantityString(
                        R.plurals.treino_exercicios_count,
                        exerciciosCount,
                        exerciciosCount
                )
        );
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

    private static class TreinoDiffCallback extends DiffUtil.Callback {
        private final List<Treino> oldList;
        private final List<Treino> newList;

        TreinoDiffCallback(List<Treino> oldList, List<Treino> newList) {
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
            Treino oldTreino = oldList.get(oldItemPosition);
            Treino newTreino = newList.get(newItemPosition);
            return oldTreino.getId() == newTreino.getId() &&
                    oldTreino.getNome().equals(newTreino.getNome()) &&
                    oldTreino.getExercicios().equals(newTreino.getExercicios());
        }
    }

    public static class TreinoViewHolder extends RecyclerView.ViewHolder {
        final TextView treinoNomeText;
        final TextView treinoExerciciosCountText;

        TreinoViewHolder(@NonNull View itemView) {
            super(itemView);
            treinoNomeText = itemView.findViewById(R.id.treinoNome);
            treinoExerciciosCountText = itemView.findViewById(R.id.treinoExerciciosCount);
        }
    }
}