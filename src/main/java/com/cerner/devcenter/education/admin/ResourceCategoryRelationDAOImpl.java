package com.cerner.devcenter.education.admin;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cerner.devcenter.education.exceptions.ResourceIdNotFoundException;
import com.cerner.devcenter.education.exceptions.CategoryIdNotFoundException;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.models.ResourceCategoryRelation;
import com.cerner.devcenter.education.models.ResourceType;
import com.google.common.base.Preconditions;

/**
 * This class is implemented from {@link ResourceCategoryRelationDAO} and is
 * responsible for performing database operations for
 * {@link ResourceCategoryRelation} objects on tables named
 * category_resource_reltn, category, resources.
 *
 * @author Abhi Purella (AP045635)
 * @author Vincent Dasari (VD049645)
 * @author Rishabh Bhojak (RB048032)
 * @author Santosh Kumar (SK051343)
 */
@Repository("ResourceTopicRelationDAO")
public class ResourceCategoryRelationDAOImpl implements ResourceCategoryRelationDAO {
    private static final String GET_RESOURCES_WITH_DIFFICULTY_LEVEL_BY_CATEGORY_ID_QUERY = "SELECT r.name as resource_name, r.description as resource_description, r.link, r.resource_id,"
            + " r.type_id, tp.type_name as resource_type_name,"
            + " ct.id, ct.name as category_name, ct.description as category_description, ctr.difficulty_level"
            + " FROM resource r" + " INNER JOIN category_resource_reltn ctr" + " on r.resource_id = ctr.resource_id"
            + " INNER JOIN type tp" + " on r.type_id = tp.type_id" + " INNER JOIN category ct"
            + " on ct.id = ctr.category_id" + " WHERE ct.id = ?" + "ORDER BY r.resource_id LIMIT(?) OFFSET(?)";
    private static final String GET_RESOURCES_BY_CATEGORY_ID_AND_TYPE_ID_QUERY = "SELECT r.name as resource_name, r.description as resource_description, r.link, r.resource_id,"
            + " r.type_id, tp.type_name as resource_type_name,"
            + " ct.id, ct.name as category_name, ct.description as category_description, ctr.difficulty_level"
            + " FROM resource r" + " INNER JOIN category_resource_reltn ctr" + " on r.resource_id = ctr.resource_id"
            + " INNER JOIN type tp" + " on r.type_id = tp.type_id" + " INNER JOIN category ct"
            + " on ct.id = ctr.category_id" + " WHERE ct.id = ? and r.type_id = ?"
            + " ORDER BY r.resource_id LIMIT(?) OFFSET(?)";
    private static final String GET_ALL_RESOURCES_QUERY = "SELECT r.resource_id, r.description as resource_description, r.link,"
            + " r.name as resource_name, r.type_id, r.resource_owner, rt.type_name as resource_type_name, ct.id, ct.name as category_name, ct.description as category_description,"
            + " ctr.difficulty_level, AVG(c.completion_rating) as avg_rating"
            + " FROM resource r INNER JOIN category_resource_reltn ctr on r.resource_id = ctr.resource_id"
            + " INNER JOIN category ct on ct.id = ctr.category_id INNER JOIN type rt on r.type_id = rt.type_id"
            + " LEFT JOIN completed_user_resource c on r.resource_id = c.resource_id GROUP BY r.resource_id, rt.type_name, ct.id, ctr.difficulty_level ORDER BY resource_id;";
    private static final String INSERT_CATEGORY_RESOURCE_DIFFICULTY_QUERY = "INSERT into category_resource_reltn (category_id, resource_id, difficulty_level) VALUES(?,?,?)";
    private static final String SEARCH_BY_CATEGORY_NAME_AND_DIFFICULTY_LEVEL_QUERY = "SELECT r.resource_id, r.description as resource_description, r.link,"
            + " r.name as resource_name, r.type_id, rt.type_name as resource_type_name, ct.id, ct.name as category_name, ct.description as category_description,"
            + " ctr.difficulty_level, AVG(c.completion_rating) as avg_rating"
            + " FROM resource r INNER JOIN category_resource_reltn ctr on r.resource_id = ctr.resource_id"
            + " INNER JOIN category ct on ct.id = ctr.category_id INNER JOIN type rt on r.type_id = rt.type_id"
            + " LEFT JOIN completed_user_resource c on r.resource_id = c.resource_id WHERE lower(ct.name) ILIKE ? AND ctr.difficulty_level = ?"
            + " GROUP BY r.resource_id, rt.type_name, ct.id, ctr.difficulty_level ORDER BY resource_id;";

    private static final String CATEGORY_ID = "id";
    private static final String RESOURCE_ID = "resource_id";
    private static final String RESOURCE_NAME = "resource_name";
    private static final String RESOURCE_OWNER = "resource_owner";
    private static final String RESOURCE_LINK = "link";
    private static final String RESOURCE_TYPE_ID = "type_id";
    private static final String RESOURCE_TYPE_NAME = "resource_type_name";
    private static final String RESOURCE_DIFFICULTY_LEVEL = "difficulty_level";
    private static final String CATEGORY_NAME = "category_name";
    private static final String RESOURCE_DESCRIPTION = "resource_description";
    private static final String CATEGORY_DESCRIPTION = "category_description";
    private static final String AVERAGE_RATING = "avg_rating";

    private static final String INVALID_CATEGORY_ID = "The category id should be greater than 0";
    private static final String INVALID_RESOURCE_ID = "Resource Type ID is invalid because it is less than or equal to 0";
    private static final String INVALID_RESOURCE_LIMIT = "Resource Limit is invalid because it is less than or equal to 0";
    private static final String INVALID_OFFSET = "Offset is invalid because it is less than or equal to 0";
    private static final String RESOURCE_ID_NOT_POSITIVE = "resourceId must be greater than 0";
    private static final String CATEGORY_ID_NOT_POSITIVE = "categoryId must be greater than 0";
    private static final String INVALID_RESOURCE_CATEGORY = "The resourceCategoryRelation can not be null";
    private static final String INVALID_URL_IN_DB = "Error: Invalid URL in database; table 'category_resource_reltn' for row with category id";
    private static final String DB_ACCESSING_ERROR = "There was an error while attempting to access to database";
    private static final String RESOURCE_NOT_FOUND = "no resource found for the passes in resourceId";
    private static final String CATEGORY_NOT_FOUND = "no category found for the passes in categoryId";
    private static final String RETRIEVING_RESOURCES_ERROR = "Error while retrieving all resources for a particular category and type";
    private static final String RETRIEVING_RESOURCES_RATING_ERROR = "Error while retrieving all resources and their corresponding average ratings for a particular category";
    private static final String INVALID_URL_ERROR_MESSAGE = "Error: Invalid URL in database; table 'category_resource_reltn' for row with category id ";
    private static final String INVALID_CATEGORY_NAME_ERROR_MESSAGE = "Category Name can't be blank/empty/null";
    private static final String INVALID_RESOURCE_LEVEL_ERROR_MESSAGE = "Resource Difficulty Level must be greater than 0";

    private static final ResourceCategoryRelationRowMapper rowMapper = new ResourceCategoryRelationRowMapper();

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ResourceDAO resourceDAO;
    @Autowired
    private CategoryDAO categoryDAO;

    @Override
    public List<ResourceCategoryRelation> getResourcesAndDifficultyLevelByCategoryId(
            final int categoryId,
            final int resourcesLimit,
            final int offSet) throws DAOException {
        Preconditions.checkArgument(categoryId > 0, INVALID_CATEGORY_ID);
        try {
            return jdbcTemplate.query(
                    GET_RESOURCES_WITH_DIFFICULTY_LEVEL_BY_CATEGORY_ID_QUERY,
                    rowMapper,
                    categoryId,
                    resourcesLimit,
                    offSet);
        } catch (final DataAccessException daoException) {
            throw new DAOException(RETRIEVING_RESOURCES_RATING_ERROR, daoException);
        }
    }

    @Override
    public List<ResourceCategoryRelation> getResourcesByCategoryIdAndTypeIdWithPagination(
            final int categoryId,
            final int resourceTypeId,
            final int resourcesLimit,
            final int offSet) throws DAOException {
        checkArgument(categoryId > 0, INVALID_CATEGORY_ID);
        checkArgument(resourceTypeId > 0, INVALID_RESOURCE_ID);
        checkArgument(resourcesLimit > 0, INVALID_RESOURCE_LIMIT);
        checkArgument(offSet >= 0, INVALID_OFFSET);
        try {
            return jdbcTemplate.query(
                    GET_RESOURCES_BY_CATEGORY_ID_AND_TYPE_ID_QUERY,
                    rowMapper,
                    categoryId,
                    resourceTypeId,
                    resourcesLimit,
                    offSet);
        } catch (final DataAccessException e) {
            throw new DAOException(RETRIEVING_RESOURCES_ERROR, e);
        }
    }

    /**
     * Custom {@link RowMapper} class to map a {@link ResultSet} to a new
     * {@link ResourceCategoryRelation} object.
     */
    static class ResourceCategoryRelationRowMapper implements RowMapper<ResourceCategoryRelation> {
        /**
         * @see org.springframework.jdbc.core.RowMapper#mapRow(ResultSet, int)
         *
         * @throws SQLException
         *             when link from the database is invalid and throws
         *             {@link MalformedURLException}
         */
        @Override
        public ResourceCategoryRelation mapRow(final ResultSet row, final int rowNum) throws SQLException {
            int categoryId = 0;
            try {
                categoryId = row.getInt("id");
                return new ResourceCategoryRelation(
                        row.getInt("resource_id"),
                        row.getString("resource_name"),
                        new URL(row.getString("link")),
                        new ResourceType(row.getInt("type_id"), row.getString("resource_type_name")),
                        row.getInt("difficulty_level"),
                        categoryId,
                        row.getString("category_name"),
                        row.getString("resource_description"),
                        row.getString("category_description"));
            } catch (final MalformedURLException malformedURLException) {
                throw new RuntimeException(INVALID_URL_IN_DB + categoryId);
            }
        }
    }

    @Override
    public void addResourceCategoryRelationWithDifficultyLevel(final ResourceCategoryRelation resourceCategoryRelation)
            throws DAOException, ResourceIdNotFoundException, CategoryIdNotFoundException {
        checkNotNull(resourceCategoryRelation, INVALID_RESOURCE_CATEGORY);
        checkArgument(resourceCategoryRelation.getResourceId() > 0, RESOURCE_ID_NOT_POSITIVE);
        checkArgument(resourceCategoryRelation.getCategoryId() > 0, CATEGORY_ID_NOT_POSITIVE);
        if (resourceDAO.getById(resourceCategoryRelation.getResourceId()) == null) {
            throw new ResourceIdNotFoundException(RESOURCE_NOT_FOUND);
        }
        if (categoryDAO.getById(resourceCategoryRelation.getCategoryId()) == null) {
            throw new CategoryIdNotFoundException(CATEGORY_NOT_FOUND);
        }
        try {
            jdbcTemplate.update(
                    INSERT_CATEGORY_RESOURCE_DIFFICULTY_QUERY,
                    new Object[] { resourceCategoryRelation.getCategoryId(), resourceCategoryRelation.getResourceId(),
                            resourceCategoryRelation.getDifficultyLevel() });
        } catch (final DataAccessException daoException) {
            throw new DAOException(DB_ACCESSING_ERROR, daoException);
        }
    }

    @Override
    public List<ResourceCategoryRelation> getAllResourcesAndAverageRatings() throws DAOException {
        try {
            return jdbcTemplate.query(GET_ALL_RESOURCES_QUERY, new ResourceCategoryRelationAndAverageRatingRowMapper());
        } catch (final DataAccessException daoException) {
            throw new DAOException(RETRIEVING_RESOURCES_RATING_ERROR, daoException);
        }
    }

    /**
     * Custom {@link RowMapper} to map a {@link ResultSet} to a new
     * {@link ResourceCategoryRelation} object.
     */
    static class ResourceCategoryRelationAndAverageRatingRowMapper implements RowMapper<ResourceCategoryRelation> {
        /**
         * Maps a {@link ResultSet} to a {@link ResourceCategoryRelation} which
         * includes information about the {@link Resource}, {@link Category} it
         * belongs to, the average rating of all the ratings, and the resource
         * owner ID for that particular {@link ResourceCategoryRelation}
         *
         * @see org.springframework.jdbc.core.RowMapper#mapRow(ResultSet, int)
         *
         * @throws SQLException
         *             when link from the database is invalid and throws
         *             {@link MalformedURLException}
         */
        @Override
        public ResourceCategoryRelation mapRow(final ResultSet row, final int rowNum) throws SQLException {
            int categoryId = 0;
            try {
                categoryId = row.getInt(CATEGORY_ID);
                return new ResourceCategoryRelation(
                        row.getInt(RESOURCE_ID),
                        row.getString(RESOURCE_NAME),
                        new URL(row.getString(RESOURCE_LINK)),
                        new ResourceType(row.getInt(RESOURCE_TYPE_ID), row.getString(RESOURCE_TYPE_NAME)),
                        row.getInt(RESOURCE_DIFFICULTY_LEVEL),
                        categoryId,
                        row.getString(CATEGORY_NAME),
                        row.getString(RESOURCE_DESCRIPTION),
                        row.getString(CATEGORY_DESCRIPTION),
                        row.getDouble(AVERAGE_RATING),
                        row.getString(RESOURCE_OWNER));
            } catch (final MalformedURLException malformedURLException) {
                throw new RuntimeException(INVALID_URL_ERROR_MESSAGE + categoryId);
            }
        }
    }

    @Override
    public List<ResourceCategoryRelation> searchResourcesByCategoryNameAndDifficultyLevel(
            final String categoryName,
            final int difficultyLevel) throws DAOException {
        checkArgument(StringUtils.isNotBlank(categoryName), INVALID_CATEGORY_NAME_ERROR_MESSAGE);
        checkArgument(difficultyLevel > 0, INVALID_RESOURCE_LEVEL_ERROR_MESSAGE);
        try {
            return jdbcTemplate.query(
                    SEARCH_BY_CATEGORY_NAME_AND_DIFFICULTY_LEVEL_QUERY,
                    new ResourceCategoryRelationAndAverageRatingRowMapper(),
                    categoryName,
                    difficultyLevel);
        } catch (final DataAccessException daoException) {
            throw new DAOException(RETRIEVING_RESOURCES_RATING_ERROR, daoException);
        }
    }
}