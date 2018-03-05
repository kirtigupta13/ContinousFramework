package com.cerner.devcenter.education.admin;

import static com.google.common.base.Preconditions.checkArgument;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cerner.devcenter.education.models.Category;
import com.google.common.base.Strings;

/**
 * This class is responsible for performing database operations for
 * {@link Category} objects on a table named category.
 *
 * @author Piyush Bandil (PB042879)
 * @author Chaitali Kharangate (CK042502)
 * @author Nikhil Agrawal (NA044293)
 * @author Surbhi Singh (SS043472)
 * @author Jacob Zimmermann (JZ022690)
 * @author Vatsal Kesarwani (VK049896)
 * @author Mani Teja Kurapati (MK051340)
 * @author Santosh Kumar (SK051343)
 */
@Repository("categoryDAO")
public class CategoryDAOImpl implements CategoryDAO {

    private static final String GET_MAX_ID_QUERY = "SELECT MAX(id) FROM category";
    private static final String GET_CATEGORY_QUERY = "SELECT id, name, description FROM category WHERE id=?";
    private static final String GET_CATEGORY_BY_NAME_QUERY = "SELECT id, name, description FROM category WHERE name=?";
    private static final String DELETE_CATEGORY_QUERY = "DELETE FROM category WHERE id=?";
    private static final String INSERT_CATEGORY_QUERY = "INSERT INTO category (name, description, difficulty_level) VALUES(?,?,?)";
    private static final String GET_ALL_CATEGORIES_QUERY = "SELECT ct.id, ct.name, ct.description, COUNT(CASE WHEN tr.difficulty_level = '1' THEN 1 END) AS skill_level_1, COUNT(CASE WHEN tr.difficulty_level = '2' THEN 1 END) AS skill_level_2,COUNT(CASE WHEN tr.difficulty_level = '3' THEN 1 END) AS skill_level_3,COUNT(CASE WHEN tr.difficulty_level = '4' THEN 1 END) AS skill_level_4,COUNT(CASE WHEN tr.difficulty_level = '5' THEN 1 END) AS skill_level_5, COUNT(tr.resource_id) AS resource_count FROM category AS ct LEFT JOIN topic_resource_reltn AS tr ON (tr.topic_id = ct.id) GROUP BY ct.id, ct.name"; 
    private static final String GET_CATEGORIES_BY_IDS_QUERY = "SELECT id, name, description FROM category WHERE id IN (:categoryIds)";
    private static final String QUERY_UPDATE_CATEGORY = "UPDATE category SET name = ?, description = ? WHERE id = ?";
    private static final String GET_NON_CHOSEN_CATEGORIES_QUERY = "SELECT id, name, description FROM category ct WHERE (lower(ct.name) ILIKE ? OR lower(ct.description) ILIKE ?)"
            + "AND ct.id NOT IN (SELECT category_id from user_interested_category where user_id = ?)";
    private static final String GET_CHSOEN_CATEGORIES_QUERY = "SELECT ct.id, ct.name, ct.description FROM (SELECT id, name, description from category WHERE lower(name) ILIKE ?"
            + "OR lower(description) ILIKE ?)ct JOIN (SELECT category_id from user_interested_category WHERE user_id = ?)uc ON ct.id = uc.category_id";
    private static final String GET_CATEGORY_BY_NAME = "SELECT id, name, description FROM category WHERE name=?";
    
    private static final String DELETE_CATEGORY_ERROR = "Error while deleting the category";
    private static final String INSERT_CATEGORY_ERROR = "Error while adding category to the database";
    private static final String GET_CATEGORY_BY_ID_ERROR = "Error while getting the category id";
    private static final String GET_CATEGORY_BY_NAME_ERROR = "Error while getting the category by name";
    private static final String GET_ALL_CATEGORIES_ERROR = "Error while retrieving all categories";
    private static final String DB_ERROR_UPDATE_CATEGORY = "Error while updating the category";
    private static final String SKILL_LEVEL_ONE = "skill_level_1";
    private static final String SKILL_LEVEL_TWO = "skill_level_2";
    private static final String SKILL_LEVEL_THREE = "skill_level_3";
    private static final String SKILL_LEVEL_FOUR = "skill_level_4";
    private static final String SKILL_LEVEL_FIVE = "skill_level_5"; 
    private static final String RESOURCE_COUNT = "resource_count";
    private static final String ZERO_CATEGORY_ID = "Category id cannot be zero";
    private static final String INVALID_CATEGORY_NAME = "Category name cannot be empty";
    private static final String INVALID_CATEGORY_DESCRIPTION = "Category description cannot be empty";
    private static final String NULL_CATEGORY = "Category cannot be null";
    private static final String INVALID_DIFFICULTY_LEVEL = "difficultyLevel must be on a scale of 1-5";

    private static final String CATEGORY_ID = "id";
    private static final String CATEGORY_NAME = "name";
    private static final String CATEGORY_DESCRIPTION = "description";
    private static final int MIN_DIFFICULTY_LEVEL = 1;
    private static final int MAX_DIFFICULTY_LEVEL = 5;
    
    private static final String EMPTY_RESULT_ERROR_MESSAGE = "Error: the specified query did not return any results";
    private static final String EMPTY_RESULT_MESSAGE = "Query returned empty result";
    private static final String EMPTY_SEARCH_STRING = "Search string cannot be null or empty";
    private static final Logger LOGGER = Logger.getLogger(CategoryDAOImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * Constructor.
     */
    public CategoryDAOImpl() {
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public Category getById(final int id) throws DAOException {
        checkArgument(id > 0, ZERO_CATEGORY_ID);
        try {
            return jdbcTemplate.queryForObject(GET_CATEGORY_QUERY, new CategoryRowMapper(), id);
        } catch (final DataAccessException daoException) {
            throw new DAOException(GET_CATEGORY_BY_ID_ERROR, daoException);
        }
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public Category getByName(final String name) throws DAOException {
        checkArgument(!Strings.isNullOrEmpty(name), INVALID_CATEGORY_NAME);
        try {
            return jdbcTemplate.queryForObject(GET_CATEGORY_BY_NAME_QUERY, new CategoryRowMapper(), name);
        } catch (final DataAccessException daoException) {
            throw new DAOException(GET_CATEGORY_BY_NAME_ERROR, daoException);
        }
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public Category addCategory(final Category category) throws DAOException {
        checkArgument(category != null, NULL_CATEGORY);
        checkArgument(StringUtils.isNotBlank(category.getName()), INVALID_CATEGORY_NAME);
        checkArgument(StringUtils.isNotBlank(category.getDescription()), INVALID_CATEGORY_DESCRIPTION);
        Range<Integer> desiredDifficultyLevelRange = Range.between(MIN_DIFFICULTY_LEVEL, MAX_DIFFICULTY_LEVEL);
        checkArgument(desiredDifficultyLevelRange.contains(category.getDifficultyLevel()), INVALID_DIFFICULTY_LEVEL);
        int maxRows = 0;
        try {
            jdbcTemplate.update(INSERT_CATEGORY_QUERY, category.getName(), category.getDescription(),
                    category.getDifficultyLevel());
            maxRows = jdbcTemplate.queryForObject(GET_MAX_ID_QUERY, Integer.class);
            return new Category(maxRows, category.getName(), category.getDescription());
        } catch (final DataAccessException daoException) {
            throw new DAOException(INSERT_CATEGORY_ERROR, daoException);
        }
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public void updateCategory(final Category category) throws DAOException {
        checkArgument(category != null, NULL_CATEGORY);
        final int id = category.getId();
        final String name = category.getName();
        final String description = category.getDescription();
        checkArgument(id > 0, ZERO_CATEGORY_ID);
        checkArgument(!Strings.isNullOrEmpty(name), INVALID_CATEGORY_NAME);
        checkArgument(!Strings.isNullOrEmpty(description), INVALID_CATEGORY_DESCRIPTION);
        try {
            jdbcTemplate.update(QUERY_UPDATE_CATEGORY, name, description, id);
        } catch (final DataAccessException daoException) {
            throw new DAOException(DB_ERROR_UPDATE_CATEGORY, daoException);
        }
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public void deleteCategory(final int id) throws DAOException {
        checkArgument(id > 0, ZERO_CATEGORY_ID);
        try {
            jdbcTemplate.update(DELETE_CATEGORY_QUERY, id);
        } catch (final DataAccessException daoException) {
            throw new DAOException(DELETE_CATEGORY_ERROR, daoException);
        }
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public List<Category> getAllCategoryList() throws DAOException {
        try {
            return jdbcTemplate.query(GET_ALL_CATEGORIES_QUERY, new ResourceCountRowMapper());
        } catch (final DataAccessException daoException) {
            throw new DAOException(GET_ALL_CATEGORIES_ERROR, daoException);
        }
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public List<Category> getCategoryListByIds(final List<Integer> categoryIds) throws DAOException {
        final MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("categoryIds", categoryIds);
        try {
            final List<Category> categoryList = namedParameterJdbcTemplate
                    .query(GET_CATEGORIES_BY_IDS_QUERY, parameters, new CategoriesByIdsResultExtractor());
            return categoryList;
        } catch (final DataAccessException daoException) {
            throw new DAOException(GET_ALL_CATEGORIES_ERROR, daoException);
        }
    }
    
    /***
     * {@inheritDoc}
     */
    @Override
    public List<Category> nonchosenCategories(final String search, final String userId) throws DAOException {
        checkArgument(StringUtils.isNotBlank(search), EMPTY_SEARCH_STRING);
        try {
            return jdbcTemplate.query(GET_NON_CHOSEN_CATEGORIES_QUERY, new CategoryRowMapper(), "%" + search + "%",
                    "%" + search + "%", userId);
        } catch (final EmptyResultDataAccessException emptyResultEx) {
            LOGGER.error(EMPTY_RESULT_ERROR_MESSAGE, emptyResultEx);
            throw new DAOException(EMPTY_RESULT_ERROR_MESSAGE, emptyResultEx);
        }
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public List<Category> chosenCategories(final String search, final String userId) throws DAOException {
        checkArgument(StringUtils.isNotBlank(search), EMPTY_SEARCH_STRING);
        try {
            return jdbcTemplate.query(GET_CHSOEN_CATEGORIES_QUERY, new CategoryRowMapper(), "%" + search + "%",
                    "%" + search + "%", userId);
        } catch (final EmptyResultDataAccessException emptyResultEx) {
            LOGGER.error(EMPTY_RESULT_ERROR_MESSAGE, emptyResultEx);
            throw new DAOException(EMPTY_RESULT_ERROR_MESSAGE, emptyResultEx);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCategoryAlreadyPresent(final String categoryName) throws DAOException {
        checkArgument(StringUtils.isNotBlank(categoryName), INVALID_CATEGORY_NAME);
        try {
            jdbcTemplate.queryForObject(GET_CATEGORY_BY_NAME, new CategoryRowMapper(), categoryName);
        } catch (final EmptyResultDataAccessException emptyResultDataAccessException) {
            LOGGER.error(EMPTY_RESULT_MESSAGE, emptyResultDataAccessException);
            return false;
        } catch (final DataAccessException dataAccessException) {
            LOGGER.error("Error adding category: " + categoryName);
            throw new DAOException("Error while getting the category with the given category name",
                    dataAccessException);
        }
        return true;
    }
    

    /**
     * Custom {@link ResultSetExtractor} class to extract a {@link ResultSet}
     * and return categories from result set.
     */
    class CategoriesByIdsResultExtractor implements ResultSetExtractor<List<Category>> {
        @Override
        public List<Category> extractData(final ResultSet rs) throws SQLException, DataAccessException {
            final List<Category> categoryList = new ArrayList<Category>();
            while (rs.next()) {
                categoryList.add(new Category(rs.getInt(1), rs.getString(2), rs.getString(3)));
            }
            return categoryList;
        }

    }

    /**
     * Custom {@link RowMapper} class to map a {@link ResultSet} to a new
     * {@link Category} object.
     */
    class CategoryRowMapper implements RowMapper<Category> {

        /**
         * @see org.springframework.jdbc.core.RowMapper#mapRow(ResultSet, int)
         */
        @Override
        public Category mapRow(final ResultSet row, final int rowNum) throws SQLException {
            final int categoryId = row.getInt(CATEGORY_ID);
            final String name = row.getString(CATEGORY_NAME);
            final String description = row.getString(CATEGORY_DESCRIPTION);
            return new Category(categoryId, name, description);
        }
    }
    public class ResourceCountRowMapper implements RowMapper<Category> {

        /**
         * Maps a {@link ResultSet} to a {@link Category} which includes
         * information about {@link Category} name, description, total number of
         * resources and number of resources per difficultyLevel for that
         * particular {@link Category}
         *
         * @see org.springframework.jdbc.core.RowMapper#mapRow(ResultSet, int)
         */
        @Override
        public Category mapRow(final ResultSet row, final int rowNum) throws SQLException {
            final int topicId = row.getInt(CATEGORY_ID);
            final String name = row.getString(CATEGORY_NAME);
            final String description = row.getString(CATEGORY_DESCRIPTION);
            final int resourceCount = row.getInt(RESOURCE_COUNT);
            Map<Integer, Integer> resourceCountPerSkillLevel = new HashMap<>();
            resourceCountPerSkillLevel.put(1, row.getInt(SKILL_LEVEL_ONE));
            resourceCountPerSkillLevel.put(2, row.getInt(SKILL_LEVEL_TWO));
            resourceCountPerSkillLevel.put(3, row.getInt(SKILL_LEVEL_THREE));
            resourceCountPerSkillLevel.put(4, row.getInt(SKILL_LEVEL_FOUR));
            resourceCountPerSkillLevel.put(5, row.getInt(SKILL_LEVEL_FIVE));
            return new Category(topicId, name, description, resourceCount, resourceCountPerSkillLevel);
        }
    }
}