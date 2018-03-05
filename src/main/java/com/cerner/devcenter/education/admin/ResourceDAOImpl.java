package com.cerner.devcenter.education.admin;

import static com.google.common.base.Preconditions.checkArgument;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cerner.devcenter.education.helpers.HttpURLValidator;
import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.models.ResourceStatus;
import com.cerner.devcenter.education.models.ResourceType;
import com.cerner.devcenter.education.utils.Constants;
import com.google.common.base.Preconditions;

/**
 * This class is responsible for performing database operations for
 * {@link Resource} objects on a table named resource.
 *
 * @author Piyush Bandil (PB042879)
 * @author James Kellerman (JK042311)
 * @author Sreelakshmi Chintha (SC043016)
 * @author Chaitali Kharangate (CK042502)
 * @author Surbhi Singh (SS043472)
 * @author Samuel Stephen(SS044662)
 * @author Wuchen Wang (WW044343)
 * @author Jacob Zimmermann (JZ022690)
 * @author Mayur Rajendran (MT049536)
 * @author Vincent Dasari (VD049645)
 * @author Rishabh Bhojak (RB048032)
 * @author Santosh Kumar (SK051343)
 */
@Repository("resourceDAO")
public class ResourceDAOImpl implements ResourceDAO {
    private static final String RESOURCE_ID = "resource_id";
    private static final String DESCRIPTION = "description";
    private static final String LINK = "link";
    private static final String NAME = "name";
    private static final String TYPE_ID = "type_id";
    private static final String TYPE_NAME = "type_name";

    private static final String GET_MAX_ID = "SELECT MAX(resource_id) FROM resource";
    private static final String INSERT_RESOURCE = "INSERT INTO resource (description, name, link, type_id, resource_owner, status) VALUES(?,?,?,?,?,?::status)";
    private static final String DELETE_RESOURCE_BY_ID = "DELETE FROM resource WHERE resource_id = ?";
    private static final String GET_RESOURCE_COUNT_BY_CATEGORY_ID = "SELECT count(*) FROM resource r INNER JOIN category_resource_reltn c on r.resource_id=c.resource_id WHERE c.category_id=?";
    private static final String GET_RESOURCE_DESCRIPTION_BY_ID = "SELECT description FROM resource WHERE resource_id = ?";
    private static final String GET_SEARCHED_RESOURCES_QUERY = "SELECT r.*, rt.type_name FROM resource r INNER JOIN type rt on r.type_id = rt.type_id where (lower(r.name) ILIKE ? OR lower(r.description) ILIKE ?) AND r.status = "
            + "'" + ResourceStatus.Available.toString() + "'";
    private static final Logger LOGGER = Logger.getLogger(ResourceDAOImpl.class);
    private static final String GET_RESOURCE = "SELECT r.resource_id, r.description, r.link, r.name, r.type_id, t.type_name FROM resource r INNER JOIN type t on r.type_id = t.type_id WHERE r.resource_id=?";
    private static final String GET_RESOURCES_BY_CATEGORY = "SELECT r.*, rt.type_name FROM resource r INNER JOIN category_resource_reltn c on r.resource_id=c.resource_id INNER JOIN type rt on r.type_id = rt.type_id WHERE c.category_id=?";
    private static final String CHECK_RESOURCE_EXISTS_QUERY = "SELECT count(*) FROM resource WHERE name = ?";
    private static final String TYPE_NAME_QUERY = "SELECT type_id FROM type WHERE type_name=?";
    private static final String EDIT_RESOURCE = "UPDATE resource SET name=?, link=?, skill_level=?, type_id=?, resource_owner=? WHERE resource_id=?";

    private static final String EMPTY_RESULT = "Error: the specified query did not return any results";
    private static final String EMPTY_FIELDS_IN_RESULT = "Resource has empty/null field(s)";
    private static final String GET_RESOURCE_BY_ID_FAILURE = "Error while extracting resource by its ID";
    private static final String INSERT_RESOURCE_FAILURE = "Error while adding resource to the database";
    private static final String DATABASE_ACCESS_FAILURE = "There was an error while attempting to access the database";
    private static final String GET_RESOURCES_BY_CATEGORY_ID_FAILURE = "Error while retrieving all resources for a particular category";
    private static final String INVALID_URL = "Error: Invalid URL in database; table 'course' for row with resource id ";
    private static final String DATA_RETRIEVAL_FAILURE = "Error retrieving data from database";
    private static final String GET_RESOURCE_BY_ID_FAILURE_LOG = "Unable to execute get resource description by resource id:";
    private static final String NULL_RESOURCE_TYPE = "Resource Type cannot be null";
    private static final String NEGATIVE_RESOURCE_ID = "Resource id must be greater than 0";
    private static final String INVALID_CATEGORY_ID = "Category Id should be positive integer";

    private static final String RESOURCE_NAME_NULL = "Resource Name can not be null";
    private static final String RESOURCE_LINK_NULL = "Resource Link can not be null";
    private static final String RESOURCE_TYPE_NULL = "Resource Type can not be null";
    private static final String RESOURCE_LEVEL_NOT_POSITIVE = "Resource Difficulty Level must be greater than 0";
    private static final String RESOURCE_ID_NOT_POSITIVE = "Resource Id must be greater than 0";
    private static final String DATABASE_ACCESS_ERROR = "There was an error while attempting to access the database";
    private static final String RESOURCE_NAME_EMPTY = "Resource Name can't be empty/blank";
    private static final String RESOURCE_LINK_EMPTY = "Resource Link can't be empty/blank";
    private static final String RESOURCE_TYPE_EMPTY = "Resource Type can't be empty/blank";
    private static final String RESOURCE_OWNER_ERROR_MESSAGE = "Resource owner cannot be null/empty/blank";
    private static final String INVALID_STRING = "Search string cannot be null or empty";
    private static final String INVALID_ID = "The id is invalid";
    private static final String RESOURCE_STATUS_NULL_ERROR_MESSAGE = "Resource status cannot be null";
    private static final String RESOURCE_STATUS_INVALID = "Resource status must be Available/Pending/Deleted";

    private static final ResourceRowMapper rowMapper = new ResourceRowMapper();

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CategoryResourceRelationDAO categoryResourceRelationDAO;

    @Override
    public List<Resource> getSearchedResources(final String search) throws DAOException {
        Preconditions.checkArgument(StringUtils.isNotEmpty(search), INVALID_STRING);
        try {
            return jdbcTemplate.<Resource>query(GET_SEARCHED_RESOURCES_QUERY, rowMapper, "%" + search + "%",
                    "%" + search + "%");
        } catch (final EmptyResultDataAccessException emptyResultEx) {
            throw new DAOException(EMPTY_RESULT, emptyResultEx);
        }
    }

    @Override
    public Resource getById(final int id) throws DAOException {
        Preconditions.checkArgument(id > 0, INVALID_ID);
        Resource queriedResource;
        try {
            queriedResource = jdbcTemplate.queryForObject(GET_RESOURCE, rowMapper, id);
            if (queriedResource.getDescription().isEmpty() || queriedResource.getResourceLink() == null
                    || queriedResource.getResourceType() == null) {
                throw new DAOException(EMPTY_FIELDS_IN_RESULT);
            } else {
                return queriedResource;
            }
        } catch (final DataAccessException daoException) {
            throw new DAOException(GET_RESOURCE_BY_ID_FAILURE, daoException);
        }
    }

    @Override
    public String getResourceDescriptionById(final int resourceId) {
        try {
            return jdbcTemplate.queryForObject(GET_RESOURCE_DESCRIPTION_BY_ID, String.class, resourceId);
        } catch (final DataAccessException emptyResultEx) {
            LOGGER.error(GET_RESOURCE_BY_ID_FAILURE_LOG + resourceId, emptyResultEx);
        }
        return null;
    }

    @Override
    public int addResource(final String description, final String name, final URL resourceLink,
            final ResourceType resourceType, final String resourceOwner, final String resourceStatus)
            throws DAOException {
        checkArgument(StringUtils.isNotBlank(name), Constants.RESOURCE_NAME_INVALID);
        checkArgument(StringUtils.isNotBlank(description), Constants.RESOURCE_DESCRIPTION_INVALID);
        checkArgument(HttpURLValidator.verifyURL(resourceLink), Constants.RESOURCE_URL_INVALID);
        checkArgument(resourceType != null, NULL_RESOURCE_TYPE);
        checkArgument(StringUtils.isNotBlank(resourceOwner), RESOURCE_OWNER_ERROR_MESSAGE);
        checkArgument(resourceStatus != null, RESOURCE_STATUS_NULL_ERROR_MESSAGE);
        try {
            jdbcTemplate.update(INSERT_RESOURCE, description, name, resourceLink.toString(),
                    resourceType.getResourceTypeId(), resourceOwner, resourceStatus);
            return jdbcTemplate.queryForObject(GET_MAX_ID, Integer.class);
        } catch (final DataAccessException daoException) {
            throw new DAOException(INSERT_RESOURCE_FAILURE, daoException);
        }
    }

    @Override
    public void deleteById(final int resourceId) throws DAOException {
        Preconditions.checkArgument(resourceId > 0, NEGATIVE_RESOURCE_ID);
        try {
            jdbcTemplate.update(DELETE_RESOURCE_BY_ID, resourceId);
            categoryResourceRelationDAO.deleteById(resourceId);
        } catch (final DataAccessException daoException) {
            throw new DAOException(DATABASE_ACCESS_FAILURE, daoException);
        }
    }

    @Override
    public List<Resource> getResourcesByCategoryId(final int categoryId) throws DAOException {
        Preconditions.checkArgument(categoryId > 0, INVALID_CATEGORY_ID);
        try {
            return jdbcTemplate.query(GET_RESOURCES_BY_CATEGORY, rowMapper, categoryId);
        } catch (final DataAccessException daoException) {
            throw new DAOException(GET_RESOURCES_BY_CATEGORY_ID_FAILURE, daoException);
        }
    }

    @Override
    public int getResourceCountByCategoryId(final int categoryId) throws DAOException {
        checkArgument(categoryId > 0, "Category Id must be greater than zero");
        try {
            return jdbcTemplate.queryForObject(GET_RESOURCE_COUNT_BY_CATEGORY_ID, Integer.class, categoryId);
        } catch (final DataAccessException daoException) {
            throw new DAOException(DATA_RETRIEVAL_FAILURE, daoException);
        }
    }

    @Override
    public boolean checkResourceExists(final String resourceName) throws DAOException {
        checkArgument(StringUtils.isNotBlank(resourceName), Constants.RESOURCE_NAME_INVALID);
        try {
            return jdbcTemplate.queryForObject(CHECK_RESOURCE_EXISTS_QUERY, Integer.class, resourceName) > 0;
        } catch (final DataAccessException daoException) {
            throw new DAOException(DATA_RETRIEVAL_FAILURE, daoException);
        }
    }

    /**
     * Custom {@link RowMapper} class to map a {@link ResultSet} to a new
     * {@link Resource} object.
     */
    static class ResourceRowMapper implements RowMapper<Resource> {
        /**
         * @see org.springframework.jdbc.core.RowMapper#mapRow(ResultSet, int)
         *
         * @throws DAOException
         *             when link from the database is invalid and throws
         *             {@link MalformedURLException}
         */
        @Override
        public Resource mapRow(final ResultSet row, final int rowNum) throws SQLException {
            final int resourceId = row.getInt(RESOURCE_ID);
            try {
                final URL resourceLink = new URL(row.getString(LINK));
                final String name = row.getString(NAME);
                final String description = row.getString(DESCRIPTION);
                final ResourceType resourceType = new ResourceType(row.getInt(TYPE_ID), row.getString(TYPE_NAME));
                return new Resource(resourceId, resourceLink, description, name, resourceType);
            } catch (final MalformedURLException malformedURLException) {
                throw new RuntimeException(INVALID_URL + resourceId);
            }
        }
    }

    @Override
    public void updateResource(final int resourceId, final String resourceName, final URL resourceLink,
            final int resourceDifficultyLevel, final String resourceType, final String resourceOwner)
            throws DAOException {
        checkArgument(resourceId > 0, RESOURCE_ID_NOT_POSITIVE);
        checkArgument(resourceName != null, RESOURCE_NAME_NULL);
        checkArgument(resourceLink != null, RESOURCE_LINK_NULL);
        checkArgument(resourceDifficultyLevel > 0, RESOURCE_LEVEL_NOT_POSITIVE);
        checkArgument(resourceType != null, RESOURCE_TYPE_NULL);
        checkArgument(StringUtils.isNotBlank(resourceName), RESOURCE_NAME_EMPTY);
        checkArgument(StringUtils.isNotBlank(resourceType), RESOURCE_TYPE_EMPTY);
        checkArgument(HttpURLValidator.verifyURL(resourceLink), RESOURCE_LINK_EMPTY);
        checkArgument(StringUtils.isNotBlank(resourceOwner), RESOURCE_OWNER_ERROR_MESSAGE);
        try {
            int typeId = jdbcTemplate.queryForObject(TYPE_NAME_QUERY, new Object[] { resourceType }, Integer.class);
            jdbcTemplate.update(EDIT_RESOURCE, resourceName, resourceLink.toString(), resourceDifficultyLevel, typeId,
                    resourceOwner, resourceId);
        } catch (DataAccessException daoException) {
            throw new DAOException(DATABASE_ACCESS_ERROR, daoException);
        }
    }
}