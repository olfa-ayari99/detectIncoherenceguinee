package com.keyrus.jade.detectincoherence.repository;


import com.keyrus.jade.detectincoherence.dto.CaseCrm;
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

   /* public List<CaseCrm> fetchDataCRM(String sql) {
        return jdbcTemplateTwo.query(sql, new RowMapper<CaseCrm>() {
            @Override
            public CaseCrm mapRow(ResultSet rs, int rowNum) throws SQLException {
                CaseCrm caseCrm = new CaseCrm();
                caseCrm.setSujet(rs.getString(ConstantsHolder.CRMCASESUBJECT) != null
                        ? rs.getString(ConstantsHolder.CRMCASESUBJECT) : "");
                caseCrm.setCategorie(rs.getString(ConstantsHolder.CRMCASECATEGORY) != null
                        ? rs.getString(ConstantsHolder.CRMCASECATEGORY) : "");
                caseCrm.setMotif(rs.getString(ConstantsHolder.CRMCASEREASON) != null
                        ? rs.getString(ConstantsHolder.CRMCASEREASON) : "");
                return caseCrm;
            }
        });
    }*/



}
