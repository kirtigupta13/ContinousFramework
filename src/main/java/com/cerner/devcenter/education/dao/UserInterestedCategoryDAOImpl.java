package com.cerner.devcenter.education.dao;

import static com.google.common.base.Preconditions.checkArgument;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.UserInterestedCategory;

/**
 * Class that is responsible for performing database operations for
 * {@link UserInterestedCategory} objects on a table named
 * user_interested_category.
 * 
 * @author Gunjan Kaphle (GK045931)
 * @author Anudeep Kumar Gadam (AG045334)
 * @author Santosh kumar (SK051343)
 */
@Repository("userInterestedTopicDAO")
public class UserInterestedCategoryDAOImpl implements UserInterestedCategoryDAO {

    private static final String GET_INTERESTED_CATEGORIES_BY_USER_ID = "SELECT uc.user_id, uc.category_id, uc.skill_level, uc.interest_level, ct.name, ct.description FROM user_interested_category uc INNER JOIN category ct ON uc.category_id = ct.id WHERE uc.user_id=? order by uc.interest_level desc";
    private static final String ADD_INTERESTED_CATEGORY_FOR_USER = "INSERT INTO user_interested_category (user_id, category_id, skill_level, interest_level) VALUES (?,?,?,?)";
    private static final String EMPTY_RESULT_ERROR_MESSAGE = "Error: the specified query did not return any results";
    private static final String DELETE_INTERESTED_CATEGORY_FOR_USER = "DELETE FROM user_interested_category WHERE user_id = ? and category_id = ?";
    private static final String UPDATE_INTERESTED_CATEGORY = "UPDATE user_interested_category set skill_level=?, interest_level=? where user_id=? and category_id=?";
    private static final String DELETE_INTERESTED_CATEGORY_FOR_USER_IN_BATCH = "DELETE FROM user_interested_category WHERE user_id = ? and category_id = ?";
    private UserInterestedCategoryRowMapper rowMapper = new UserInterestedCategoryRowMapper();

    private static final String INVALID_USER_ID = "User ID cannot be null/empty";
    private static final String INVALID_CATEGORY = "Category object cannot be null.";
    private static final String INVALID_SKILL_LEVEL = "Skill level must be between 1 and 5.";
    private static final String INVALID_INTEREST_LEVEL = "Interest level must be between 1 and 5.";
    private static final String INVALID_CATEGORY_ID = "Category ID must be greater than 0";
    private static final String INVALID_CATEGORY_IDS = "Category IDs array can't be null or empty";
    private static final String ERROR_DELETING = "Error: unable to delete the userInterestedCategory.";

    private final static Logger LOGGER = Logger.getLogger(UserInterestedCategoryDAOImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /***
     * {@inheritDoc}
     */
    @Override
    public List<UserInterestedCategory> getUserInterestedCategoryByUserId(String userId) throws DAOException {
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID);
        try {
            return jdbcTemplate.query(GET_INTERESTED_CATEGORIES_BY_USER_ID, rowMapper, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new DAOException(EMPTY_RESULT_ERROR_MESSAGE, e);
        }
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public boolean addUserInterestedCategory(String userId, Category category, int skillLevel, int interestLevel)
            throws DAOException {
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID);
        checkArgument(category != null, INVALID_CATEGORY);
        checkArgument(skillLevel > 0 && skillLevel < 6, INVALID_SKILL_LEVEL);
        checkArgument(interestLevel > 0 && interestLevel < 6, INVALID_INTEREST_LEVEL);
        try {
            jdbcTemplate.update(ADD_INTERESTED_CATEGORY_FOR_USER, userId, category.getId(), skillLevel, interestLevel);
            return true;
        } catch (DataAccessException e) {
            throw new DAOException("Error: unable to execute query and add the interested category for the user.", e);
        }
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public boolean updateUserInterestedCategory(String userId, Category category, int skillLevel, int interestLevel)
            throws DAOException {
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID);
        checkArgument(category != null, INVALID_CATEGORY);
        checkArgument(skillLevel > 0 && skillLevel < 6, INVALID_SKILL_LEVEL);
        checkArgument(interestLevel > 0 && interestLevel < 6, INVALID_INTEREST_LEVEL);
        try {
            jdbcTemplate.update(UPDATE_INTERESTED_CATEGORY, skillLevel, interestLevel, userId, category.getId());
            return true;
        } catch (DataAccessException e) {
            throw new DAOException(
                    "Error: unable to execute query and update the interested category for the user.",
                    e);
        }
    }

    /**
     * Custom {@link RowMapper} class to map a {@link ResultSet} to a new
     * {@link UserInterestedCategory} object.
     */
    public static class UserInterestedCategoryRowMapper implements RowMapper<UserInterestedCategory> {
        /**
         * @see org.springframework.jdbc.core.RowMapper#mapRow(ResultSet, int)
         * 
         * @throws SQLException
         *             when there is error receiving data from the database
         */
        @Override
        public UserInterestedCategory mapRow(ResultSet row, int rowNum) throws SQLException {
            return new UserInterestedCategory(
                    row.getString("user_id"),
                    new Category(row.getInt("category_id"), row.getString("name"), row.getString("description")),
                    row.getInt("skill_level"),
                    row.getInt("interest_level"));
        }
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public boolean deleteUserInterestedCategory(String userId, int categoryId) throws DAOException {
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID);
        checkArgument(categoryId > 0, INVALID_CATEGORY_ID);
        try {
            jdbcTemplate.update(DELETE_INTERESTED_CATEGORY_FOR_USER, userId, categoryId);
            return true;
        } catch (DataAccessException dataAccessEx) {
            throw new DAOException(ERROR_DELETING, dataAccessEx);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteUserInterestedCategoryInBatch(final String userId, final int[] categoryIds)
            throws DAOException {
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID);
        checkArgument(ArrayUtils.isNotEmpty(categoryIds), INVALID_CATEGORY_IDS);
        try {
            jdbcTemplate.batchUpdate(DELETE_INTERESTED_CATEGORY_FOR_USER_IN_BATCH, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setString(1, userId);
                    ps.setInt(2, categoryIds[i]);
                }

                @Override
                public int getBatchSize() {
                    return categoryIds.length;
                }
            });
            return true;
        } catch (DataAccessException dataAccessEx) {
            LOGGER.error(ERROR_DELETING, dataAccessEx);
            throw new DAOException(ERROR_DELETING, dataAccessEx);
        }
    }
}