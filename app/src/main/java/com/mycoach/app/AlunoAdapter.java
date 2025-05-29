package com.mycoach.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AlunoAdapter extends RecyclerView.Adapter<AlunoAdapter.AlunoViewHolder> {

    private final List<Aluno> alunos;
    private final Consumer<Aluno> onAlunoClickListener;

    public AlunoAdapter(List<Aluno> alunos, Consumer<Aluno> onAlunoClickListener) {
        this.alunos = new ArrayList<>(alunos);
        this.onAlunoClickListener = onAlunoClickListener;
    }

    @NonNull
    @Override
    public AlunoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_aluno, parent, false);
        return new AlunoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlunoViewHolder holder, int position) {
        Aluno aluno = alunos.get(position);
        holder.nomeTextView.setText(aluno.getNome());
        holder.emailTextView.setText(aluno.getEmail());

        if (aluno.getNome() != null && !aluno.getNome().isEmpty()) {
            holder.thumbnailInitialTextView.setText(aluno.getNome().substring(0, 1).toUpperCase());
        } else {
            holder.thumbnailInitialTextView.setText("?");
        }

        holder.thumbnailLayout.setClipToOutline(true);

        holder.itemView.setOnClickListener(v -> {
            if (onAlunoClickListener != null) {
                onAlunoClickListener.accept(aluno);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alunos.size();
    }

    public void setAlunos(List<Aluno> novosAlunos) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new AlunoDiffCallback(this.alunos, novosAlunos));

        this.alunos.clear();
        this.alunos.addAll(novosAlunos);

        diffResult.dispatchUpdatesTo(this);
    }

    private static class AlunoDiffCallback extends DiffUtil.Callback {
        private final List<Aluno> oldList;
        private final List<Aluno> newList;

        AlunoDiffCallback(List<Aluno> oldList, List<Aluno> newList) {
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
            Aluno oldAluno = oldList.get(oldItemPosition);
            Aluno newAluno = newList.get(newItemPosition);
            return oldAluno.getId() == newAluno.getId() &&
                    oldAluno.getNome().equals(newAluno.getNome()) &&
                    oldAluno.getEmail().equals(newAluno.getEmail());
        }
    }

    public static class AlunoViewHolder extends RecyclerView.ViewHolder {
        final TextView nomeTextView;
        final TextView emailTextView;
        final TextView thumbnailInitialTextView;
        final RelativeLayout thumbnailLayout;

        AlunoViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeTextView = itemView.findViewById(R.id.headline);
            emailTextView = itemView.findViewById(R.id.supportingText);
            thumbnailInitialTextView = itemView.findViewById(R.id.thumbnailInitialTextView);
            thumbnailLayout = itemView.findViewById(R.id.thumbnail);
        }
    }
}