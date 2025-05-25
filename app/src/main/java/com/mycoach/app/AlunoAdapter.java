package com.mycoach.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AlunoAdapter extends RecyclerView.Adapter<AlunoAdapter.AlunoViewHolder> {

    private List<Aluno> alunos;
    private Consumer<Aluno> onAlunoClickListener;

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
        this.alunos.clear();
        this.alunos.addAll(novosAlunos);
        notifyDataSetChanged();
    }

    static class AlunoViewHolder extends RecyclerView.ViewHolder {
        TextView nomeTextView;
        TextView emailTextView;
        TextView thumbnailInitialTextView;
        RelativeLayout thumbnailLayout;

        AlunoViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeTextView = itemView.findViewById(R.id.headline);
            emailTextView = itemView.findViewById(R.id.supportingText);
            thumbnailInitialTextView = itemView.findViewById(R.id.thumbnailInitialTextView);
            thumbnailLayout = itemView.findViewById(R.id.thumbnail);
        }
    }
}