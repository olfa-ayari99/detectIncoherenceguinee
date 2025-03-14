package com.keyrus.jade.detectincoherence.dto;

public class Action {


    private String tag;
    private String libelle;
    private String operateur;
    private String sujet;
    private String categorie;
    private String motif;
    private Boolean active;
    private Boolean EstCreateCaseAuto;


    public Action( String libelle,String tag, String operateur,  String sujet,String categorie, String motif, Boolean active, Boolean estCreateCaseAuto) {
        this.tag = tag;
        this.libelle = libelle;
        this.operateur = operateur;
        this.categorie = categorie;
        this.sujet = sujet;
        this.motif = motif;
        this.active = active;
        EstCreateCaseAuto = estCreateCaseAuto;
    }

    public Action() {

    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getEstCreateCaseAuto() {
        return EstCreateCaseAuto;
    }

    public void setEstCreateCaseAuto(Boolean estCreateCaseAuto) {
        EstCreateCaseAuto = estCreateCaseAuto;
    }

    public String getOperateur() {
        return operateur;
    }

    public void setOperateur(String operateur) {
        this.operateur = operateur;
    }
    public boolean isValid() {
        return sujet != null && !sujet.trim().isEmpty() &&
                categorie != null && !categorie.trim().isEmpty() &&
                motif != null && !motif.trim().isEmpty() &&
        operateur != null && !operateur.trim().isEmpty()&&
        libelle != null && !libelle.trim().isEmpty()&&
        tag != null && !tag.trim().isEmpty();







    }
    public String getConcatenatedFields() {
        return sujet + "|" + categorie + "|" + motif;
    }
}
