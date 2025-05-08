package com.mycoach.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SerieAdapter extends RecyclerView.Adapter<SerieAdapter.SerieViewHolder> {

    private List<Serie> series;

    public SerieAdapter() {
        this.series = new ArrayList<>();
    }

    @NonNull
    @Override
    public SerieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_serie, parent, false);
        return new SerieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SerieViewHolder holder, int position) {
        Serie serie = series.get(position);
        holder.serieCargaText.setText("Carga: " + serie.getCarga());
        holder.serieRepeticoesText.setText("Repetições: " + serie.getRepeticoes());
    }

    @Override
    public int getItemCount() {
        return series.size();
    }

    public void setSeries(List<Serie> series) {
        this.series.clear();
        this.series.addAll(series);
        notifyDataSetChanged();
    }

    static class SerieViewHolder extends RecyclerView.ViewHolder {
        TextView serieCargaText;
        TextView serieRepeticoesText;

        SerieViewHolder(@NonNull View itemView) {
            super(itemView);
            serieCargaText = itemView.findViewById(R.id.serieCargaText);
            serieRepeticoesText = itemView.findViewById(R.id.serieRepeticoesText);
        }
    }
}