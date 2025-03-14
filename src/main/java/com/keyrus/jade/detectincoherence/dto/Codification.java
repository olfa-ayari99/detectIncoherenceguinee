package com.keyrus.jade.detectincoherence.dto;

public class Codification {
    private String sujet;
    private String categorie;
    private String motif;

    public Codification(String sujet, String categorie, String motif) {
        this.sujet = sujet;
        this.categorie = categorie;
        this.motif = motif;
    }

    public Codification() {

    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getConcatenatedFields() {
        return sujet + "|" + categorie + "|" + motif;
    }
    public boolean isValid() {
        return sujet != null && !sujet.trim().isEmpty() &&
                categorie != null && !categorie.trim().isEmpty() &&
                motif != null && !motif.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Codification{" +
                "sujet='" + sujet + '\'' +
                ", categorie='" + categorie + '\'' +
                ", motif='" + motif + '\'' +
                '}';
    }
}
