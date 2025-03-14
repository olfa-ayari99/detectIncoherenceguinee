package com.keyrus.jade.detectincoherence.repository;


import com.keyrus.jade.detectincoherence.dto.Codification;
import com.keyrus.jade.detectincoherence.utils.ConstantsHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CodificationRepository {

    @Autowired
    private JdbcTemplate jdbcTemplateTwo;

    public List<Codification> fetchDataCRM(String sql) {
        return jdbcTemplateTwo.query(sql, new RowMapper<Codification>() {
            @Override
            public Codification mapRow(ResultSet rs, int rowNum) throws SQLException {
                Codification codification = new Codification();
                codification.setSujet(rs.getString(ConstantsHolder.CRMCASESUBJECT) != null
                        ? rs.getString(ConstantsHolder.CRMCASESUBJECT) : "");
                codification.setCategorie(rs.getString(ConstantsHolder.CRMCASECATEGORY) != null
                        ? rs.getString(ConstantsHolder.CRMCASECATEGORY) : "");
                codification.setMotif(rs.getString(ConstantsHolder.CRMCASEREASON) != null
                        ? rs.getString(ConstantsHolder.CRMCASEREASON) : "");
                return codification;
            }
        });
    }



}
