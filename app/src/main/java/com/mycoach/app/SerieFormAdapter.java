package com.mycoach.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SerieFormAdapter extends RecyclerView.Adapter<SerieFormAdapter.SerieViewHolder> {

    private List<Serie> series;
    private RecyclerView seriesRecyclerView;

    public SerieFormAdapter(List<Serie> series) {
        this.series = new ArrayList<>(series != null ? series : new ArrayList<>());
    }

    @NonNull
    @Override
    public SerieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_serie_form, parent, false);
        return new SerieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SerieViewHolder holder, int position) {
        Serie serie = series.get(position);
        holder.serieNumero.setText(String.valueOf(position + 1));
        holder.serieCargaInput.setText(serie.getCarga());
        holder.serieRepeticoesInput.setText(serie.getRepeticoes());
    }

    @Override
    public int getItemCount() {
        return series.size();
    }

    public void addSerie(Serie serie) {
        series.add(serie);
        notifyItemInserted(series.size() - 1);
    }

    public void setSeries(List<Serie> series) {
        this.series.clear();
        this.series.addAll(series != null ? series : new ArrayList<>());
        notifyDataSetChanged();
    }

    public List<Serie> getSeries() {
        List<Serie> updatedSeries = new ArrayList<>();
        for (int i = 0; i < series.size(); i++) {
            Serie serie = series.get(i);
            Serie updatedSerie = new Serie();
            RecyclerView.ViewHolder viewHolder = seriesRecyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolder != null) {
                View view = viewHolder.itemView;
                EditText cargaInput = view.findViewById(R.id.serieCargaInput);
                EditText repeticoesInput = view.findViewById(R.id.serieRepeticoesInput);
                updatedSerie.setCarga(cargaInput.getText().toString());
                try {
                    updatedSerie.setRepeticoes(repeticoesInput.getText().toString());
                } catch (Exception e) {
                    updatedSerie.setRepeticoes("0");
                }
            } else {
                updatedSerie.setCarga(serie.getCarga());
                updatedSerie.setRepeticoes(serie.getRepeticoes());
            }
            updatedSeries.add(updatedSerie);
        }
        return updatedSeries;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.seriesRecyclerView = recyclerView;
    }

    static class SerieViewHolder extends RecyclerView.ViewHolder {
        TextView serieNumero;
        EditText serieCargaInput;
        EditText serieRepeticoesInput;

        SerieViewHolder(@NonNull View itemView) {
            super(itemView);
            serieNumero = itemView.findViewById(R.id.serieNumero);
            serieCargaInput = itemView.findViewById(R.id.serieCargaInput);
            serieRepeticoesInput = itemView.findViewById(R.id.serieRepeticoesInput);
        }
    }
}