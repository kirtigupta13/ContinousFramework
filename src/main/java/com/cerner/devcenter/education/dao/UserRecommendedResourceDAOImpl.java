package com.cerner.devcenter.education.dao;

import static com.cerner.devcenter.education.utils.QueryExpanderUtil.expandPlaceholders;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.models.ResourceStatus;
import com.cerner.devcenter.education.models.UserRecommendedResource;
import com.cerner.devcenter.education.utils.Constants;
import com.google.common.base.Preconditions;

/**
 * Class responsible for performing database operations for
 * {@link UserRecommendedResource} objects.
 *
 * @author Gunjan Kaphle (GK045931)
 * @author Amos Bailey (AB032627)
 * @author Mayur Rajendran (MT049536)
 * @author Santosh Kumar (SK051343)
 */
@Repository("UserRecommendedResourceDAOImpl")
public class UserRecommendedResourceDAOImpl implements UserRecommendedResourceDAO {

    private static final String INVALID_MINIMUM_RATING_REQUIRED_ERROR_MESSAGE = "The minimum rating required should be non-negative";
    private static final String INVALID_NUMBER_OF_RATINGS_REQUIRED_ERROR_MESSAGE = "The minimum number of ratings required should be non-negative";
    private static final String INVALID_USER_ID_ERROR_MESSAGE = "User Id cannot be null, empty or whitespace.";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // @formatter:off
    private static final String GET_RECOMMENDED_RESOURCES_FOR_USER = "SELECT r.name as resource_name, r.description as resource_description, "
            + "r.link, r.resource_id, ct.id, ct.name as category_name, ct.description as category_description, crt.difficulty_level, "
            + "uc.interest_level FROM resource r "
            + "INNER JOIN category_resource_reltn crt ON r.resource_id = crt.resource_id "
            + "INNER JOIN user_interested_category uc ON (crt.category_id = uc.category_id AND crt.difficulty_level BETWEEN uc.skill_level - 1 AND uc.skill_level + 1 AND uc.interest_level > 2) "
            + "INNER JOIN category ct ON uc.category_id = ct.id "
            + "LEFT JOIN completed_user_resource c ON r.resource_id = c.resource_id "
            + "WHERE uc.user_id = ? AND r.status = " + "'" + ResourceStatus.Available.toString() + "'"
            + " AND r.resource_id NOT IN (SELECT DISTINCT resource_id FROM completed_user_resource cur WHERE cur.user_id = uc.user_id) "
            + "GROUP BY r.resource_id, ct.id, crt.difficulty_level, uc.interest_level "
            + "ORDER BY uc.interest_level DESC, (CASE WHEN (avg(c.completion_rating) < ? AND count(c.completion_rating) > ?) THEN 1 ELSE 0 END), "
            + "avg(c.completion_rating) DESC NULLS LAST";

    private static final String GET_RECOMMENDED_RESOURCES_FOR_USER_BY_CATEGORIES = "SELECT r.name as resource_name, r.description as resource_description, "
            + "r.link, r.resource_id, ct.id, ct.name as category_name, ct.description as category_description, crt.difficulty_level, "
            + "uc.interest_level FROM resource r "
            + "INNER JOIN category_resource_reltn crt ON r.resource_id = crt.resource_id "
            + "INNER JOIN user_interested_category uc ON (crt.category_id = uc.category_id AND crt.difficulty_level BETWEEN uc.skill_level - 1 AND uc.skill_level + 1 AND uc.interest_level > 2) "
            + "INNER JOIN category ct ON uc.category_id = ct.id "
            + "LEFT JOIN completed_user_resource c ON r.resource_id = c.resource_id "
            + "WHERE (uc.user_id = (?) AND ct.id IN (%s) AND r.status = " + "'" + ResourceStatus.Available.toString()
            + "'"
            + "AND r.resource_id NOT IN (SELECT DISTINCT resource_id FROM completed_user_resource cur WHERE cur.user_id = uc.user_id)) "
            + "GROUP BY r.resource_id, ct.id, crt.difficulty_level, uc.interest_level "
            + "ORDER BY uc.interest_level DESC, (CASE WHEN (avg(c.completion_rating) < ? AND count(c.completion_rating) > ?) THEN 1 ELSE 0 END), "
            + "avg(c.completion_rating) DESC NULLS LAST";

    // @formatter:on
    private static final String EMPTY_RESULT_ERROR_MESSAGE = "Error: the specified query did not return any results";
    private static final UserRecommendedResourceRowMapper rowMapper = new UserRecommendedResourceRowMapper();

    @Override
    public List<UserRecommendedResource> getRecommendedResourcesForTheUser(
            final String userId,
            final double minimumRatingRequired,
            final int minimumNumberOfRatingsRequired) throws DAOException {
        Preconditions.checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID_ERROR_MESSAGE);
        Preconditions.checkArgument(minimumRatingRequired >= 0, INVALID_MINIMUM_RATING_REQUIRED_ERROR_MESSAGE);
        Preconditions
                .checkArgument(minimumNumberOfRatingsRequired >= 0, INVALID_NUMBER_OF_RATINGS_REQUIRED_ERROR_MESSAGE);
        try {
            return jdbcTemplate.query(
                    GET_RECOMMENDED_RESOURCES_FOR_USER,
                    rowMapper,
                    userId,
                    minimumRatingRequired,
                    minimumNumberOfRatingsRequired);
        } catch (final EmptyResultDataAccessException e) {
            throw new DAOException(EMPTY_RESULT_ERROR_MESSAGE, e);
        }
    }

    @Override
    public List<UserRecommendedResource> getRecommendedResourcesForTheUserInCategories(
            final String userId,
            final List<Category> categories,
            final double minimumRatingRequired,
            final int minimumNumberOfRatingsRequired) throws DAOException {
        Preconditions.checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID_ERROR_MESSAGE);
        Preconditions.checkArgument(categories != null, Constants.CATEGORY_LIST_NULL);
        Preconditions.checkArgument(categories.size() > 0, Constants.CATEGORY_LIST_EMPTY);
        Preconditions.checkArgument(minimumRatingRequired >= 0, INVALID_MINIMUM_RATING_REQUIRED_ERROR_MESSAGE);
        Preconditions
                .checkArgument(minimumNumberOfRatingsRequired >= 0, INVALID_NUMBER_OF_RATINGS_REQUIRED_ERROR_MESSAGE);
        final List<Object> queryParameters = new ArrayList<>(categories.size() + 1);
        queryParameters.add(userId);
        for (final Category category : categories) {
            verifyCategoryArgument(category);
            queryParameters.add(category.getId());
        }
        queryParameters.add(minimumRatingRequired);
        queryParameters.add(minimumNumberOfRatingsRequired);
        try {
            return jdbcTemplate.query(
                    String.format(
                            GET_RECOMMENDED_RESOURCES_FOR_USER_BY_CATEGORIES,
                            expandPlaceholders(categories.size())),
                    rowMapper,
                    queryParameters.toArray());
        } catch (final DataAccessException dataAccessException) {
            throw new DAOException(Constants.ERROR_RETRIEVING_RECOMMENDED_RESOURCES, dataAccessException);
        }
    }

    /**
     * Checks a category object to see if it is a valid argument to a method,
     * i.e., ensures it isn't null, its ID is positive, and its name is not
     * null/blank/empty.
     *
     * @param category
     *            The {@link Category} object to check.
     * @throws IllegalArgumentException
     *             When the category is null, when its ID is not positive, or
     *             when its name is null/empty/blank.
     */
    private static void verifyCategoryArgument(final Category category) {
        Preconditions.checkArgument(category != null, Constants.CATEGORY_LIST_HAS_NULL_ITEM);
        Preconditions.checkArgument(category.getId() > 0, Constants.CATEGORY_ID_MUST_BE_POSITIVE);
        Preconditions.checkArgument(category.getName() != null, Constants.CATEGORY_NAME_NULL);
        Preconditions.checkArgument(StringUtils.isNotBlank(category.getName()), Constants.CATEGORY_NAME_EMPTY_OR_BLANK);
    }

    /**
     * Custom {@link RowMapper} class to map a {@link ResultSet} to a new
     * {@link UserRecommendedResource} object.
     */
    static class UserRecommendedResourceRowMapper implements RowMapper<UserRecommendedResource> {
        /**
         * @see org.springframework.jdbc.core.RowMapper#mapRow(ResultSet, int)
         *
         * @throws SQLException
         *             when link from the database is invalid and throws
         *             {@link MalformedURLException}
         */
        @Override
        public UserRecommendedResource mapRow(final ResultSet row, final int rowNum) throws SQLException {
            try {
                final Resource resource = new Resource(
                        row.getInt("resource_id"),
                        new URL(row.getString("link")),
                        row.getString("resource_description"),
                        row.getString("resource_name"));
                final Category category = new Category(
                        row.getInt("id"),
                        row.getString("category_name"),
                        row.getString("category_description"));
                return new UserRecommendedResource(
                        resource,
                        category,
                        row.getInt("difficulty_level"),
                        row.getInt("interest_level"));
            } catch (final MalformedURLException malformedURLException) {
                throw new RuntimeException(
                        "Error: Invalid URL in database; table 'resource' for row with resource id: "
                                + row.getInt("resource_id"));
            }
        }
    }
}