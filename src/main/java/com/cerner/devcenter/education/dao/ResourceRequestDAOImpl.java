package com.cerner.devcenter.education.dao;

import static com.google.common.base.Preconditions.checkArgument;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.ResourceRequest;

/**
 * @author Navya Rangeneni (NR046827)
 * @author Vatsal Kesarwani (VK049896)
 */
@Repository("resourceRequestDAO")
public class ResourceRequestDAOImpl implements ResourceRequestDAO {

    private static final String INSERT_RESOURCE_REQUEST_QUERY = "INSERT INTO resource_request (user_id, category_name, resource_name) VALUES (?,?,?)";
    private static final String GET_ALL_RESOURCE_REQUESTS_QUERY = "SELECT id, user_id, category_name, resource_name, is_approved FROM resource_request";
    private static final String GET_ALL_RESOURCE_REQUESTS_OF_USER_QUERY = "SELECT id, user_id, category_name, resource_name, is_approved FROM resource_request WHERE user_id = ?";
    private static final String GET_ALL_RESOURCE_REQUESTS_FOR_IS_APPROVED_QUERY = "SELECT id, user_id, category_name, resource_name, is_approved FROM resource_request WHERE request_is_approved = ?";
    private static final String DELETE_RESOURCE_REQUESTS_IN_BATCH_QUERY = "DELETE FROM resource_request WHERE id = ?";

    private static final String REQUEST_ID = "id";
    private static final String USER_ID = "user_id";
    private static final String CATEGORY_NAME = "category_name";
    private static final String RESOURCE_NAME = "resource_name";
    private static final String IS_APPROVED = "is_approved";

    private static final String RESOURCE_REQUEST_DB_INSERT_ERROR_MSG = "Error while adding resource request to the database.";
    private static final String RESOURCE_REQUEST_DB_READ_ERROR_MSG = "Error while retrieving all resource requests.";
    private static final String RESOURCE_REQUEST_DB_DELETE_ERROR_MSG = "Error while deleting the resource request.";

    private static final String NULL_RESOURCE_REQUEST_ERROR_MSG = "ResourceRequest object is null.";
    private static final String NULL_RESULT_SET_ERROR_MSG = "ResultSet object is null.";
    private static final String NULL_USER_ID_ERROR_MSG = "UserId is null.";
    private static final String NULL_CATEGORY_NAME_ERROR_MSG = "CategoryName is null";
    private static final String NULL_RESOURCE_NAME_ERROR_MSG = "ResourceName is null";
    private static final String INVALID_REQUEST_IDS_ERROR_MSG = "Resource request ID's array is null/empty.";
    private static final String INVALID_USER_ID_ERROR_MSG = "UserId is null/empty/blank.";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void addResourceRequest(final ResourceRequest resourceRequest) throws DAOException {
        checkArgument(resourceRequest != null, NULL_RESOURCE_REQUEST_ERROR_MSG);
        final String userId = resourceRequest.getUserId();
        final String categoryName = resourceRequest.getCategoryName();
        final String resourceName = resourceRequest.getResourceName();
        checkArgument(userId != null, NULL_USER_ID_ERROR_MSG);
        checkArgument(categoryName != null, NULL_CATEGORY_NAME_ERROR_MSG);
        checkArgument(resourceName != null, NULL_RESOURCE_NAME_ERROR_MSG);
        try {
            jdbcTemplate.update(INSERT_RESOURCE_REQUEST_QUERY, userId, categoryName, resourceName);
        } catch (final DataAccessException dataAccessException) {
            throw new DAOException(RESOURCE_REQUEST_DB_INSERT_ERROR_MSG, dataAccessException);
        }
    }

    @Override
    public List<ResourceRequest> getAllResourceRequests() throws DAOException {
        try {
            return jdbcTemplate.query(GET_ALL_RESOURCE_REQUESTS_QUERY, new ResourceRequestsRowMapper());
        } catch (final DataAccessException daoException) {
            throw new DAOException(RESOURCE_REQUEST_DB_READ_ERROR_MSG, daoException);
        }
    }

    @Override
    public List<ResourceRequest> getAllResourceRequests(final String userId) throws DAOException {
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID_ERROR_MSG);
        try {
            return jdbcTemplate.query(GET_ALL_RESOURCE_REQUESTS_OF_USER_QUERY, new ResourceRequestsRowMapper(), userId);
        } catch (final DataAccessException daoException) {
            throw new DAOException(RESOURCE_REQUEST_DB_READ_ERROR_MSG, daoException);
        }
    }

    @Override
    public List<ResourceRequest> getAllResourceRequests(final boolean isApproved) throws DAOException {
        try {
            return jdbcTemplate.query(GET_ALL_RESOURCE_REQUESTS_FOR_IS_APPROVED_QUERY, new ResourceRequestsRowMapper(),
                    isApproved);
        } catch (final DataAccessException daoException) {
            throw new DAOException(RESOURCE_REQUEST_DB_READ_ERROR_MSG, daoException);
        }
    }

    @Override
    public void deleteResourceRequests(final int[] requestIds) throws DAOException {
        checkArgument(ArrayUtils.isNotEmpty(requestIds), INVALID_REQUEST_IDS_ERROR_MSG);
        try {
            jdbcTemplate.batchUpdate(DELETE_RESOURCE_REQUESTS_IN_BATCH_QUERY, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                    ps.setInt(1, requestIds[i]);
                }

                @Override
                public int getBatchSize() {
                    return requestIds.length;
                }
            });
        } catch (final DataAccessException dataAccessException) {
            throw new DAOException(RESOURCE_REQUEST_DB_DELETE_ERROR_MSG, dataAccessException);
        }
    }

    /**
     * Custom {@link RowMapper} class to map a {@link ResultSet} to a new
     * {@link ResourceRequest} object.
     */
    private static class ResourceRequestsRowMapper implements RowMapper<ResourceRequest> {

        @Override
        public ResourceRequest mapRow(final ResultSet row, final int rowNum) throws SQLException {
            checkArgument(row != null, NULL_RESULT_SET_ERROR_MSG);
            final ResourceRequest resourceRequest = new ResourceRequest();
            resourceRequest.setId(row.getInt(REQUEST_ID));
            resourceRequest.setCategoryName(row.getString(CATEGORY_NAME));
            resourceRequest.setResourceName(row.getString(RESOURCE_NAME));
            resourceRequest.setUserId(row.getString(USER_ID));
            resourceRequest.setApproved(row.getBoolean(IS_APPROVED));
            return resourceRequest;
        }
    }
}