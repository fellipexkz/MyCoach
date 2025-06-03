package com.mycoach.app;

import java.util.ArrayList;
import java.util.List;

public class Exercicio {
    private int id;
    private int treinoId;
    private String nome;
    private String tempoDescanso;
    private List<Serie> series;
    private int tipoSerieSelecionadoId = -1;
    private int unidadeTempoSelecionadoId = -1;

    public Exercicio() {
        this.series = new ArrayList<>();
        this.nome = "";
        this.tempoDescanso = "1min 0s";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTreinoId() {
        return treinoId;
    }

    public void setTreinoId(int treinoId) {
        this.treinoId = treinoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTempoDescanso() {
        return tempoDescanso != null ? tempoDescanso : "1min 0s";
    }

    public void setTempoDescanso(String tempoDescanso) {
        this.tempoDescanso = tempoDescanso;
    }

    public List<Serie> getSeries() {
        return series;
    }

    public void setSeries(List<Serie> series) {
        this.series = series;
    }

    public int getTipoSerieSelecionadoId() {
        return tipoSerieSelecionadoId;
    }

    public void setTipoSerieSelecionadoId(int tipoSerieSelecionadoId) {
        this.tipoSerieSelecionadoId = tipoSerieSelecionadoId;
    }

    public int getUnidadeTempoSelecionadoId() {
        return unidadeTempoSelecionadoId;
    }

    public void setUnidadeTempoSelecionadoId(int unidadeTempoSelecionadoId) {
        this.unidadeTempoSelecionadoId = unidadeTempoSelecionadoId;
    }
}