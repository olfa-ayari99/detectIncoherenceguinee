package com.keyrus.jade.detectincoherence.dto;

public class IdentifiantAction {


    private String libelle;
    private String codeService;
    private String operation;
    private String sourceInterface;

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getCodeService() {
        return codeService;
    }

    public void setCodeService(String codeService) {
        this.codeService = codeService;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getSourceInterface() {
        return sourceInterface;
    }

    public void setSourceInterface(String sourceInterface) {
        this.sourceInterface = sourceInterface;
    }

    public IdentifiantAction(String libelle, String codeService, String operation, String sourceInterface) {
        this.libelle = libelle;
        this.codeService = codeService;
        this.operation = operation;
        this.sourceInterface = sourceInterface;
    }

    public String getIdentifiant() {
        return codeService + "|" + operation + "|" + sourceInterface;
    }
}
