package com.keyrus.jade.detectincoherence.dto;

import java.util.Date;

public class Action {

    public Action(String date, String ip, String serverIp, String login, String module, String service, String codeService, String operation, String msisdn, String idContrat, String idCustomer, String sourceInterface, String idCrm, String statut, String resultat, String codification, String idInteraction, String typeInteraction, String caseId) {
        this.date = date;
        this.ip = ip;
        this.serverIp = serverIp;
        this.login = login;
        this.module = module;
        this.service = service;
        this.codeService = codeService;
        this.operation = operation;
        this.msisdn = msisdn;
        this.idContrat = idContrat;
        this.idCustomer = idCustomer;
        this.sourceInterface = sourceInterface;
        this.idCrm = idCrm;
        this.statut = statut;
        this.resultat = resultat;
        this.codification = codification;
        this.idInteraction = idInteraction;
        this.typeInteraction = typeInteraction;
        this.caseId = caseId;
    }

    private String date;
    private String ip;
    private String serverIp;
    private String login;
    private String module;
    private String service;
    private String codeService;
    private String operation;
    private String msisdn;
    private String idContrat;
    private String idCustomer;
    private String sourceInterface;
    private String idCrm;
    private String statut;
    private String resultat;
    private String codification;
    private String idInteraction;
    private String typeInteraction;
    private String caseId;

    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
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

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getIdContrat() {
        return idContrat;
    }

    public void setIdContrat(String idContrat) {
        this.idContrat = idContrat;
    }

    public String getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(String idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getSourceInterface() {
        return sourceInterface;
    }

    public void setSourceInterface(String sourceInterface) {
        this.sourceInterface = sourceInterface;
    }

    public String getIdCrm() {
        return idCrm;
    }

    public void setIdCrm(String idCrm) {
        this.idCrm = idCrm;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getResultat() {
        return resultat;
    }

    public void setResultat(String resultat) {
        this.resultat = resultat;
    }

    public String getCodification() {
        return codification;
    }

    public void setCodification(String codification) {
        this.codification = codification;
    }

    public String getIdInteraction() {
        return idInteraction;
    }

    public void setIdInteraction(String idInteraction) {
        this.idInteraction = idInteraction;
    }

    public String getTypeInteraction() {
        return typeInteraction;
    }

    public void setTypeInteraction(String typeInteraction) {
        this.typeInteraction = typeInteraction;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getKey() {
        return idInteraction + "|" + codification + "|" + login;
    }
}