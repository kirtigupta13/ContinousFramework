package com.cerner.devcenter.education.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.SkillOptions;

/**
 * This class is responsible for performing database operations for
 * {@link SkillOptions} objects.
 * 
 * @author SC043016
 * @author Surbhi Singh (SS043472)
 */
@Repository("skillOptionsDAO")
public class SkillOptionsDAOImpl implements SkillOptionsDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkillOptionsDAOImpl.class);
    private static final String SELECT_SKILL_OPTIONS = "SELECT value, description FROM skill_options";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * @throws DAOException
     *             when there is an error while trying to get skill options from
     *             database
     */
    @Override
    public Map<Integer, String> getSkillOptions() throws DAOException {
        Map<Integer, String> skillOptions = new TreeMap<>();
        List<SkillOptions> skillList;
        try {
            skillList = jdbcTemplate.query(SELECT_SKILL_OPTIONS, new SkillRowMapper());
            for (SkillOptions skill : skillList) {
                skillOptions.put(skill.getValue(), skill.getDescription());
            }
        } catch (DataAccessException dataAccessException) {
            LOGGER.error("Error in accessing database while trying to get skill options ", dataAccessException);
            throw new DAOException("Error in accessing database while trying to get skill options ",
                    dataAccessException);
        }
        return skillOptions;
    }

    /**
     * Custom {@link RowMapper} class to map a {@link ResultSet} to a new
     * {@link SkillOptions} object.
     */
    class SkillRowMapper implements RowMapper<SkillOptions> {

        /**
         * @see org.springframework.jdbc.core.RowMapper#mapRow(ResultSet, int)
         */
        @Override
        public SkillOptions mapRow(ResultSet row, int rowNum) throws SQLException {
            int skillValue = row.getInt("value");
            String description = row.getString("description");
            return new SkillOptions(skillValue, description);
        }
    }
}
