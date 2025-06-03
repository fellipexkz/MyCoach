package com.mycoach.app;

public class Serie {
    private int id;
    private int exercicioId;
    private String carga;
    private String repeticoes;
    private String tempo;
    private String unidadeTempo;
    private String tipoSerie;

    @SuppressWarnings("unused")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExercicioId() {
        return exercicioId;
    }

    public void setExercicioId(int exercicioId) {
        this.exercicioId = exercicioId;
    }

    public String getCarga() {
        return carga != null ? carga : "";
    }

    public void setCarga(String carga) {
        this.carga = carga;
    }

    public String getRepeticoes() {
        return repeticoes != null ? repeticoes : "";
    }

    public void setRepeticoes(String repeticoes) {
        this.repeticoes = repeticoes;
    }

    public String getTempo() {
        return tempo != null ? tempo : "";
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public String getUnidadeTempo() {
        return unidadeTempo != null ? unidadeTempo : "min";
    }

    public void setUnidadeTempo(String unidadeTempo) {
        this.unidadeTempo = unidadeTempo;
    }

    public String getTipoSerie() {
        return tipoSerie != null ? tipoSerie : "carga";
    }

    public void setTipoSerie(String tipoSerie) {
        this.tipoSerie = tipoSerie;
    }
}