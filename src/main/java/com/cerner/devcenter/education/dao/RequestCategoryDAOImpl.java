package com.cerner.devcenter.education.dao;

import static com.google.common.base.Preconditions.checkArgument;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.RequestCategory;

/**
 * @author Vatsal Kesarwani (VK049896)
 */
@Repository("requestCategoryDAO")
public class RequestCategoryDAOImpl implements RequestCategoryDAO {

    private static final String REQUEST_CATEGORY_ID = "id";
    private static final String REQUEST_CATEGORY_NAME = "name";
    private static final String IS_APPROVED = "is_approved";

    private static final String QUERY_INSERT_REQUEST_CATEGORY = "INSERT INTO request_category (name) VALUES (?)";
    private static final String QUERY_INSERT_USER_REQUEST_RELATION = "INSERT INTO user_request_category_reltn (user_id, request_category_id) VALUES (?, (SELECT id FROM request_category WHERE name = ?))";
    private static final String QUERY_DELETE_REQUEST_CATEGORY = "DELETE FROM user_request_category_reltn WHERE user_id = ? and request_category_id = ?";
    private static final String QUERY_GET_ALL_REQUEST_CATEGORY = "SELECT id, name, is_approved FROM request_category";
    private static final String QUERY_GET_ALL_REQUEST_CATEGORY_FOR_IS_APPROVED = "SELECT id, name, is_approved FROM request_category WHERE is_approved = ?";

    private static final String REQUEST_CATEGORY_DB_INSERT_ERROR_MESSAGE = "Error while adding the requested category to the database.";
    private static final String REQUEST_CATEGORY_DB_DELETE_ERROR_MESSAGE = "Error while deleting the requested category from the database.";
    private static final String REQUEST_CATEGORY_DB_READ_ERROR_MESSAGE = "Error while retrieving the requested category from the database.";

    private static final String NULL_REQUEST_CATEGORY_ERROR_MESSAGE = "RequestCategory object is null.";
    private static final String NULL_REQUEST_CATEGORY_NAME_ERROR_MESSAGE = "RequestCategory name is null.";
    private static final String NULL_RESULT_SET_ERROR_MESSAGE = "ResultSet object is null.";
    private static final String INVALID_REQUEST_CATEGORY_ID_ERROR_MESSAGE = "RequestCategory id is zero/negative.";
    private static final String INVALID_USER_ID_ERROR_MESSAGE = "UserId is null/empty/blank.";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void addRequestCategory(final RequestCategory requestCategory, final String userId) throws DAOException {
        checkArgument(requestCategory != null, NULL_REQUEST_CATEGORY_ERROR_MESSAGE);
        final String requestCategoryName = requestCategory.getName();
        checkArgument(requestCategoryName != null, NULL_REQUEST_CATEGORY_NAME_ERROR_MESSAGE);
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID_ERROR_MESSAGE);
        try {
            jdbcTemplate.update(QUERY_INSERT_REQUEST_CATEGORY, requestCategoryName);
            jdbcTemplate.update(QUERY_INSERT_USER_REQUEST_RELATION, userId, requestCategoryName);
        } catch (final DataAccessException dataAccessException) {
            throw new DAOException(REQUEST_CATEGORY_DB_INSERT_ERROR_MESSAGE, dataAccessException);
        }
    }

    @Override
    public void deleteRequestCategory(final int requestCategoryId, final String userId) throws DAOException {
        checkArgument(requestCategoryId > 0, INVALID_REQUEST_CATEGORY_ID_ERROR_MESSAGE);
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID_ERROR_MESSAGE);
        try {
            jdbcTemplate.update(QUERY_DELETE_REQUEST_CATEGORY, userId, requestCategoryId);
        } catch (final DataAccessException dataAccessException) {
            throw new DAOException(REQUEST_CATEGORY_DB_DELETE_ERROR_MESSAGE, dataAccessException);
        }
    }

    @Override
    public List<RequestCategory> getAllRequestCategories() throws DAOException {
        try {
            final List<RequestCategory> requestCategories = jdbcTemplate.query(QUERY_GET_ALL_REQUEST_CATEGORY,
                    new RequestCategoryRowMapper());
            if (requestCategories == null) {
                return Collections.emptyList();
            }
            return requestCategories;
        } catch (final DataAccessException dataAccessException) {
            throw new DAOException(REQUEST_CATEGORY_DB_READ_ERROR_MESSAGE, dataAccessException);
        }
    }

    @Override
    public List<RequestCategory> getAllRequestCategories(final boolean isApproved) throws DAOException {
        try {
            final List<RequestCategory> requestCategories = jdbcTemplate
                    .query(QUERY_GET_ALL_REQUEST_CATEGORY_FOR_IS_APPROVED, new RequestCategoryRowMapper(), isApproved);
            if (requestCategories == null) {
                return Collections.emptyList();
            }
            return requestCategories;
        } catch (final DataAccessException dataAccessException) {
            throw new DAOException(REQUEST_CATEGORY_DB_READ_ERROR_MESSAGE, dataAccessException);
        }
    }

    /**
     * Custom {@link RowMapper} class to map a {@link ResultSet} to a new
     * {@link RequestCategory} object.
     */
    private static class RequestCategoryRowMapper implements RowMapper<RequestCategory> {

        @Override
        public RequestCategory mapRow(final ResultSet row, final int rowNum) throws SQLException {
            checkArgument(row != null, NULL_RESULT_SET_ERROR_MESSAGE);
            final RequestCategory requestCategory = new RequestCategory();
            requestCategory.setId(row.getInt(REQUEST_CATEGORY_ID));
            requestCategory.setName(row.getString(REQUEST_CATEGORY_NAME));
            requestCategory.setApproved(row.getBoolean(IS_APPROVED));
            return requestCategory;
        }
    }
}