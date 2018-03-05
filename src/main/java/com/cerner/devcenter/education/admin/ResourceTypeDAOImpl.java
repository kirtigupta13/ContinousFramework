package com.cerner.devcenter.education.admin;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cerner.devcenter.education.models.ResourceType;
import com.google.common.base.Strings;

/**
 * This class is responsible for performing database operations for
 * {@link ResourceType} objects on a table named type.
 *
 * @author Gunjan Kaphle (GK045931)
 * @author JZ022690
 * @author Santosh Kumar (SK051343)
 */

@Repository("resourceTypeDAO")
public class ResourceTypeDAOImpl implements ResourceTypeDAO {
    private static final String GET_ALL_RESOURCES_TYPE_QUERY = "SELECT * FROM type";
    private static final String GET_MAX_ID_QUERY = "SELECT MAX(type_id) FROM type";
    private static final String INSERT_RESOURCE_TYPE_QUERY = "INSERT INTO type (type_name) VALUES(?)";
    private static final String GET_RESOURCE_BY_TYPE_ID = "SELECT type_id, type_name FROM type WHERE type_id=?";
    private static final String GET_TYPE_BY_NAME = "SELECT type_id, type_name FROM type WHERE type_name=?";
    private static final String EMPTY_RESULT_ERROR_MESSAGE = "Error: the specified query did not return any results";

    private static final String INVALID_RESOURCE_TYPE_ID = "The id for resource type is invalid because it is less than or equal to zero";
    private static final String INVALID_RESOURCE_TYPE_NAME = "The name for resource type is invalid because it is either null or empty.";
    private static final String RESOURCE_ID_ERROR = "Error while extracting resource by its id";
    private static final String TYPE_EXTRACTION_ERROR = "Error while extracting type by its name";
    private static final String DB_RESOURCE_ERROR = "Error while adding resource to the database";

    private static final ResourceTypeRowMapper rowMapper = new ResourceTypeRowMapper();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<ResourceType> getAllResourceTypes() throws DAOException {
        try {
            return jdbcTemplate.query(GET_ALL_RESOURCES_TYPE_QUERY, rowMapper);
        } catch (EmptyResultDataAccessException emptyResultEx) {
            throw new DAOException(EMPTY_RESULT_ERROR_MESSAGE, emptyResultEx);
        }
    }

    @Override
    public ResourceType getById(int id) throws DAOException {
        checkArgument(id > 0, INVALID_RESOURCE_TYPE_ID);
        ResourceType queriedResource;
        try {
            queriedResource = jdbcTemplate.queryForObject(GET_RESOURCE_BY_TYPE_ID, rowMapper, id);
        } catch (DataAccessException daoException) {
            throw new DAOException(RESOURCE_ID_ERROR, daoException);
        }
        return queriedResource;
    }

    @Override
    public ResourceType getByName(String name) throws DAOException {
        checkArgument(!Strings.isNullOrEmpty(name), INVALID_RESOURCE_TYPE_NAME);
        ResourceType queriedResource;
        try {
            queriedResource = jdbcTemplate.queryForObject(GET_TYPE_BY_NAME, rowMapper, name);
        } catch (DataAccessException daoException) {
            throw new DAOException(TYPE_EXTRACTION_ERROR, daoException);
        }
        return queriedResource;
    }

    @Override
    public ResourceType addResourceType(String resourceTypeName) throws DAOException {
        checkNotNull(resourceTypeName, INVALID_RESOURCE_TYPE_NAME);
        int maxRows = 0;
        try {
            jdbcTemplate.update(INSERT_RESOURCE_TYPE_QUERY, resourceTypeName);
            maxRows = jdbcTemplate.queryForObject(GET_MAX_ID_QUERY, Integer.class);
            return new ResourceType(maxRows, resourceTypeName);
        } catch (DataAccessException daoException) {
            throw new DAOException(DB_RESOURCE_ERROR, daoException);
        }
    }

    /**
     * Custom {@link RowMapper} class to map a {@link ResultSet} to a new
     * {@link ResourceType} object.
     */
    static class ResourceTypeRowMapper implements RowMapper<ResourceType> {
        /**
         *
         * @throws SQLException
         *             when SQL Exception is encountered using column values
         */
        @Override
        public ResourceType mapRow(ResultSet row, int rowNum) throws SQLException {
            int resourceTypeId = row.getInt("type_id");
            String name = row.getString("type_name");
            return new ResourceType(resourceTypeId, name);
        }
    }
}
