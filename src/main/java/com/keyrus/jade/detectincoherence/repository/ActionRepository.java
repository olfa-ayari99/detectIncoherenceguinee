package com.keyrus.jade.detectincoherence.repository;


import com.keyrus.jade.detectincoherence.dto.Action;
import com.keyrus.jade.detectincoherence.utils.ConstantsHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ActionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplateOne;


    public List<Action> fetchDataVue(String sql) {
        return jdbcTemplateOne.query(sql, new RowMapper<Action>() {
            @Override
            public Action mapRow(ResultSet rs, int rowNum) throws SQLException {
                Action action = new Action();
                action.setOperateur(rs.getString(ConstantsHolder.TAGOPERATOR) != null
                        ? rs.getString(ConstantsHolder.TAGOPERATOR) : "");
                action.setTag(
                        rs.getString(ConstantsHolder.TAGACTION) != null ? rs.getString(ConstantsHolder.TAGACTION) : "");
                action.setLibelle(
                        rs.getString(ConstantsHolder.LABELACTION) != null ? rs.getString(ConstantsHolder.LABELACTION) : "");
                action.setSujet(rs.getString(ConstantsHolder.CRMCASESUBJECT) != null
                        ? rs.getString(ConstantsHolder.CRMCASESUBJECT) : "");
                action.setCategorie(rs.getString(ConstantsHolder.CRMCASECATEGORY) != null
                        ? rs.getString(ConstantsHolder.CRMCASECATEGORY) : "");
                action.setMotif(rs.getString(ConstantsHolder.CRMCASEREASON) != null
                        ? rs.getString(ConstantsHolder.CRMCASEREASON) : "");
                action.setActive(rs.getBoolean(ConstantsHolder.ACTIVE));
                action.setEstCreateCaseAuto(rs.getBoolean(ConstantsHolder.EstCreateCaseAuto));

                return action;
            }
        });
    }


}
