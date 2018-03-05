package com.cerner.devcenter.education.dao;

import static com.google.common.base.Preconditions.checkArgument;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.CompletedResource;
import com.cerner.devcenter.education.models.CompletedUserResource;
import com.cerner.devcenter.education.utils.CompletionRating;

/**
 * Responsible for performing database operations for
 * {@link CompletedUserResource} objects on a table named
 * completed_user_resource.
 *
 * @author Vinutha Nuchimaniyanda (VN046193)
 * @author Rishabh Bhojak (RB048032)
 * @author Mayur Rajendran (MT049536)
 * @author Vincent Dasari (VD049645)
 * @author Santosh Kumar (SK051343)
 */
@Repository("completedUserResourceDAO")
public class CompletedUserResourceDAOImpl implements CompletedUserResourceDAO {

    private static final String ADD_USER_RESOURCE_COMPLETION_RATING = "INSERT INTO completed_user_resource (user_id, resource_id, completion_rating, completion_date) VALUES (?,?,?,?)";
    private static final String GET_USER_COMPLETED_RESOURCE = "SELECT c.user_id, r.resource_id, r.name, r.link, c.completion_rating, c.completion_date "
            + "FROM completed_user_resource c "
            + "INNER JOIN resource r ON r.resource_id = c.resource_id WHERE c.user_id = ? ORDER BY c.completion_date DESC";
    private static final String GET_SPECIFIC_NUMBER_OF_COMPLETED_RESOURCES_BY_USER_ID_QUERY = "SELECT c.user_id, r.resource_id, r.name, r.link, c.completion_rating, c.completion_date "
            + "FROM completed_user_resource c "
            + "INNER JOIN resource r ON r.resource_id = c.resource_id WHERE c.user_id = ? ORDER BY c.completion_date DESC LIMIT ?";
    private static final String GET_COUNT_OF_COMPLETED_RESOURCES_BY_USER_ID_QUERY = "SELECT COUNT(completion_rating) as count "
            + "FROM completed_user_resource " + "WHERE user_id = ?";
    private static final String GET_CATEGORY_NAME_WITH_MOST_COMPLETED_RESOURCES_BY_USER_ID_QUERY = "SELECT ct.name FROM completed_user_resource cr "
            + "INNER JOIN category_resource_reltn crt ON cr.resource_id = crt.resource_id "
            + "INNER JOIN category ct on ct.id = crt.category_id WHERE user_id = ? GROUP BY ct.name, ct.id "
            + "ORDER BY COUNT(ct.name) DESC LIMIT 1";

    private static final String USER_ID = "user_Id";
    private static final String RESOURCE_ID = "resource_id";
    private static final String NAME = "name";
    private static final String LINK = "link";
    private static final String COMPLETION_RATING = "completion_rating";
    private static final String COMPLETION_DATE = "completion_date";

    private static final String EMPTY_STRING = "";

    private static final String COMPLETED_DATE_GREATER_THAN_ZERO = "CompletionDate should be greater than 0";
    private static final String USER_ID_ILLEGAL_ARGUMENT_MESSAGE = "User ID cannot be null/empty/whitespace.";
    private static final String RESOURCE_ID_ILLEGAL_ARGUMENT_MESSAGE = "Resource ID must be greater than zero.";
    private static final String COMPLETION_RATING_ILLEGAL_ARGUMENT_MESSAGE = "Completion Rating is invalid";
    private static final String INVALID_NUMBER_OF_REQUIRED_COMPLETED_RESOURCES_ERROR_MESSAGE = "Number of required completed resources to display must be greater than 0";

    private static final String ERROR_QUERING_EXCEPTION = "Error querying completed resources for user: %s with the exception: %s";
    private static final String ERROR_ADDING_COMPLETION_EXCEPTION = "Error adding completion rating for the resource: %d and user: %s  with the exception: %s";
    private static final String ERROR_RETRIEVING_RESULTS = "Error: unable to execute query and retrieve completed resources for the user.";
    private static final String GET_SPECIFIC_NUMBER_OF_COMPLETED_RESOURCES_BY_USER_ID_ERROR_MESSAGE = "Error retrieving: %d completed resources for user: %s";
    private static final String EXECUTING_QUERY_ERROR_MESSAGE = "Error: unable to execute query and add the completion rating for the user.";
    private static final String ERROR_INVALID_URL = "Error: Invalid URL in database; table 'resource' for row with resource id: ";
    private static final String ERROR_GETTING_COUNT_OF_COMPLETED_RESOURCES_ERROR_MESSAGE = "Unable to get count of completed resources for the user: %s";
    private static final String ERROR_GETTING_CATEGORY_WITH_MOST_COMPLETED_RESOURCES_ERROR_MESSAGE = "Unable to retrieve name of category with the most number of completed resources for the user: %s";

    private static final Logger LOGGER = Logger.getLogger(CompletedUserResourceDAOImpl.class);
    private static final CompletedResourceRowMapper ROWMAPPER = new CompletedResourceRowMapper();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean addCompletedUserResourceRating(
            final String userId,
            final int resourceId,
            final CompletionRating completedRating,
            final long completionDate) throws DAOException {
        checkArgument(StringUtils.isNotBlank(userId), USER_ID_ILLEGAL_ARGUMENT_MESSAGE);
        checkArgument(resourceId > 0, RESOURCE_ID_ILLEGAL_ARGUMENT_MESSAGE);
        checkArgument(completedRating != null, COMPLETION_RATING_ILLEGAL_ARGUMENT_MESSAGE);
        checkArgument(completionDate > 0, COMPLETED_DATE_GREATER_THAN_ZERO);
        try {
            jdbcTemplate.update(
                    ADD_USER_RESOURCE_COMPLETION_RATING,
                    userId,
                    resourceId,
                    completedRating.getValue(),
                    completionDate);
            return true;
        } catch (final DataAccessException dataAccessException) {
            LOGGER.error(String.format(ERROR_ADDING_COMPLETION_EXCEPTION, resourceId, userId, dataAccessException));
            throw new DAOException(EXECUTING_QUERY_ERROR_MESSAGE, dataAccessException);
        }
    }

    @Override
    public List<CompletedResource> getCompletedResources(final String userId) throws DAOException {
        checkArgument(StringUtils.isNotBlank(userId), USER_ID_ILLEGAL_ARGUMENT_MESSAGE);
        try {
            return jdbcTemplate.query(GET_USER_COMPLETED_RESOURCE, ROWMAPPER, userId);
        } catch (final DataAccessException dataAccessException) {
            LOGGER.error(String.format(ERROR_QUERING_EXCEPTION, userId, dataAccessException));
            throw new DAOException(ERROR_RETRIEVING_RESULTS, dataAccessException);
        }
    }

    @Override
    public List<CompletedResource> getMostRecentlyCompletedResources(
            final String userId,
            final int numberOfRequiredResources) throws DAOException {
        checkArgument(StringUtils.isNotBlank(userId), USER_ID_ILLEGAL_ARGUMENT_MESSAGE);
        checkArgument(numberOfRequiredResources > 0, INVALID_NUMBER_OF_REQUIRED_COMPLETED_RESOURCES_ERROR_MESSAGE);
        try {
            return jdbcTemplate.query(
                    GET_SPECIFIC_NUMBER_OF_COMPLETED_RESOURCES_BY_USER_ID_QUERY,
                    ROWMAPPER,
                    userId,
                    numberOfRequiredResources);
        } catch (final DataAccessException dataAccessException) {
            final String errorMessage = String.format(
                    GET_SPECIFIC_NUMBER_OF_COMPLETED_RESOURCES_BY_USER_ID_ERROR_MESSAGE,
                    numberOfRequiredResources,
                    userId);
            LOGGER.error(errorMessage);
            throw new DAOException(errorMessage, dataAccessException);
        }
    }

    @Override
    public int getCountOfResourcesCompletedByUser(final String userId) throws DAOException {
        checkArgument(StringUtils.isNotBlank(userId), USER_ID_ILLEGAL_ARGUMENT_MESSAGE);
        try {
            final Integer countOfResourcesCompleted = jdbcTemplate
                    .queryForObject(GET_COUNT_OF_COMPLETED_RESOURCES_BY_USER_ID_QUERY, Integer.class, userId);
            if (countOfResourcesCompleted == null) {
                return 0;
            } else {
                return countOfResourcesCompleted;
            }
        } catch (final DataAccessException dataAccessException) {
            throw new DAOException(
                    String.format(ERROR_GETTING_COUNT_OF_COMPLETED_RESOURCES_ERROR_MESSAGE, userId),
                    dataAccessException);
        }
    }

    @Override
    public String getCategoryNameWithMostCompletedResourcesByUser(final String userId) throws DAOException {
        checkArgument(StringUtils.isNotBlank(userId), USER_ID_ILLEGAL_ARGUMENT_MESSAGE);
        try {
            final String categoryName = jdbcTemplate.queryForObject(
                    GET_CATEGORY_NAME_WITH_MOST_COMPLETED_RESOURCES_BY_USER_ID_QUERY,
                    String.class,
                    userId);
            if (StringUtils.isBlank(categoryName)) {
                return EMPTY_STRING;
            } else {
                return categoryName;
            }
        } catch (final DataAccessException dataAccessException) {
            throw new DAOException(
                    String.format(ERROR_GETTING_CATEGORY_WITH_MOST_COMPLETED_RESOURCES_ERROR_MESSAGE, userId),
                    dataAccessException);
        }
    }

    /**
     * Custom {@link RowMapper} class to map a {@link ResultSet} to a new
     * {@link CompletedResource} object.
     */
    private static class CompletedResourceRowMapper implements RowMapper<CompletedResource> {

        @Override
        public CompletedResource mapRow(final ResultSet row, final int rowNum) throws SQLException {
            try {
                return new CompletedResource(
                        row.getString(USER_ID),
                        row.getInt(RESOURCE_ID),
                        row.getString(NAME),
                        new URL(row.getString(LINK)),
                        CompletionRating.getRating((row.getInt(COMPLETION_RATING))),
                        new Date(row.getLong(COMPLETION_DATE) * 1000));
            } catch (final MalformedURLException malformedUrlException) {
                throw new RuntimeException(ERROR_INVALID_URL + row.getInt(RESOURCE_ID));
            }
        }
    }
}