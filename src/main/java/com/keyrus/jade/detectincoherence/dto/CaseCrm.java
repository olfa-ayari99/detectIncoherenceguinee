package com.keyrus.jade.detectincoherence.dto;

import java.util.ArrayList;
import java.util.List;

public class CaseCrm {
    public CaseCrm(String dateCreationCase,  String login, String codification,String idInteraction) {
        this.idInteraction = idInteraction;
        this.codification = codification;
        this.dateCreationCase = dateCreationCase;
        this.login = login;
    }

    private String idInteraction;
        private String codification;
        private String dateCreationCase;
        private String login;

        // Getters and Setters

    public String getIdInteraction() {
        return idInteraction;
    }

    public void setIdInteraction(String idInteraction) {
        this.idInteraction = idInteraction;
    }

    public String getCodification() {
            return codification;
        }

        public void setCodification(String codification) {
            this.codification = codification;
        }

        public String getDateCreationCase() {
            return dateCreationCase;
        }

        public void setDateCreationCase(String dateCreationCase) {
            this.dateCreationCase = dateCreationCase;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

    public List<String>  getCrmFields() {
            return List.of(this.codification,this.dateCreationCase,this.idInteraction,this.dateCreationCase);

    }
    public String getKey() {
        return login + "|" + idInteraction + "|" + codification;
    }
}

