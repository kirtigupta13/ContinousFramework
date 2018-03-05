package com.cerner.devcenter.education.dao;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Repository;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.Learner;
import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.models.UserSubscription;
import com.google.common.base.Strings;

/**
 * This class is responsible for performing database operations for
 * {@link UserSubscription} objects on a table named 'user_subscription'.
 *
 * @author Mani Teja Kurapati (MK051340)
 */

@Repository("userSubscriptionDAO")
public class UserSubscriptionDAOImpl implements UserSubscriptionDAO {

    private static final String INSERT_SUBSCRIPTION_QUERY = "INSERT INTO user_subscription (user_id, category_id) VALUES(?,?)";
    private static final String DELETE_SUBSCRIPTION_QUERY = "DELETE FROM user_subscription WHERE user_id = ? and category_id = ?";
    private static final String GET_SUBSCRIBED_USERS_QUERY = "SELECT user_id FROM user_subscription WHERE category_id = ?";
    private static final String GET_SUBSCRIBED_CATEGORIES_QUERY = "SELECT c.id, c.name, c.description FROM category c INNER JOIN user_subscription us ON c.id = us.category_id and us.user_id = ?";
    private static final String GET_LEARNERS_SUBSCRIBED_TO_CATEGORIES_BELONGING_TO_RESOURCE = "SELECT DISTINCT l.user_id, l.email FROM (SELECT topic_id FROM topic_resource_reltn WHERE resource_id = ?) as resource_categories JOIN user_subscription us ON resource_categories.topic_id = us.category_id JOIN learners l ON us.user_id = l.user_id";

    private static final int USER_ID_COLUMN_INDEX = 0;
    private static final int CATEGORY_ID_COLUMN_INDEX = 1;
    private static final String LEARNERS_USERID_COLUMN_LABEL = "user_id";
    private static final String LEARNERS_EMAIL_COLUMN_LABEL = "email";
    private static final String EMPTY_RESULT_ERROR_MESSAGE = "Error: the specified query did not return any results";

    private final UserListResultExtractor userListResultExtractor = new UserListResultExtractor();
    private final SubscribedCategoriesResultExtractor subscribedCategoriesResultExtractor = new SubscribedCategoriesResultExtractor();
    private final LearnersSubscribedTOCategoriesBelongingToResourceResultExtractor learnerExtractor = new LearnersSubscribedTOCategoriesBelongingToResourceResultExtractor();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Creates a new {@link UserSubscriptionDAOImpl}. Used for testing and
     * Spring beans.
     */
    public UserSubscriptionDAOImpl() {
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public boolean addUserSusbcription(final UserSubscription userSubscription) throws DAOException {
        checkNotNull(userSubscription, "userSubscription object is null");
        final String userId = userSubscription.getUserId();
        final int categoryId = userSubscription.getCategoryId();
        checkArgument(!Strings.isNullOrEmpty(userId), "User id is null/empty");
        checkArgument(categoryId > 0, "Category Id should be greater than zero");
        try {
            jdbcTemplate.update(INSERT_SUBSCRIPTION_QUERY, userId, categoryId);
            return true;
        } catch (final DataAccessException daoException) {
            throw new DAOException("Error while adding subscription to the database", daoException);
        }
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public boolean deleteUserSubscription(final UserSubscription userSubscription) throws DAOException {
        checkNotNull(userSubscription, "userSubscription object is null");
        final String userId = userSubscription.getUserId();
        final int categoryId = userSubscription.getCategoryId();
        checkArgument(!Strings.isNullOrEmpty(userId), "User id is null/empty");
        checkArgument(categoryId > 0, "Category Id should be greater than zero");
        try {
            jdbcTemplate.update(DELETE_SUBSCRIPTION_QUERY, userId, categoryId);
            return true;
        } catch (final DataAccessException daoException) {
            throw new DAOException("Error while deleting subscription from database", daoException);
        }
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public List<String> getSubscribedUsersByCategory(final Category category) throws DAOException {
        final int categoryId = category.getId();
        checkArgument(categoryId > 0, "Category id should be gretaer than zero");
        try {
            return jdbcTemplate.query(GET_SUBSCRIBED_USERS_QUERY, userListResultExtractor, categoryId);

        } catch (final EmptyResultDataAccessException e) {
            throw new DAOException(EMPTY_RESULT_ERROR_MESSAGE, e);
        }
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public List<Category> getSubscribedCategoriesByUser(final String userId) throws DAOException {
        checkArgument(!Strings.isNullOrEmpty(userId), "User id is null/empty");
        try {
            return jdbcTemplate.query(GET_SUBSCRIBED_CATEGORIES_QUERY, subscribedCategoriesResultExtractor, userId);

        } catch (final EmptyResultDataAccessException e) {
            throw new DAOException(EMPTY_RESULT_ERROR_MESSAGE, e);
        }
    }

    /**
     * Custom {@link ResultSetExtractor} class to extract a {@link ResultSet}
     * and return only user Id's from result set.
     */
    public static class UserListResultExtractor implements ResultSetExtractor<List<String>> {

        @Override
        public List<String> extractData(final ResultSet rs) throws SQLException, DataAccessException {
            final List<String> usersList = new ArrayList<>();
            while (rs.next()) {
                usersList.add(rs.getString(USER_ID_COLUMN_INDEX));
            }
            return usersList;
        }
    }

    /**
     * Custom {@link ResultSetExtractor} class to extract a {@link ResultSet}
     * and return only category Id's from result set.
     */
    public static class SubscribedCategoriesResultExtractor implements ResultSetExtractor<List<Category>> {

        @Override
        public List<Category> extractData(final ResultSet rs) throws SQLException, DataAccessException {
            final List<Category> categoryList = new ArrayList<>();
            while (rs.next()) {
                categoryList.add(new Category(rs.getInt(1), rs.getString(2), rs.getString(3)));
            }
            return categoryList;
        }
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public List<Learner> getLearnersSubscribedToCategoriesBelongingToResource(Resource resource) throws DAOException {
        List<Learner> learnerList = null;
        checkNotNull(resource, "Resource object is null");
        final int resourceId = resource.getResourceId();
        checkArgument(resourceId > 0, "resource id should be gretaer than zero");
        ClassLoader classLoader = getClass().getClassLoader();
        File sqlFile = new File(classLoader.getResource("queries/getLearnersSubscribedToCategories.sql").getFile());
        try {
            BufferedReader in = new BufferedReader(new FileReader(sqlFile));
            LineNumberReader fileReader = new LineNumberReader(in);
            String query = ScriptUtils.readScript(fileReader, "--", ";");
            learnerList = jdbcTemplate.query(query, learnerExtractor, resourceId);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            throw new DAOException(EMPTY_RESULT_ERROR_MESSAGE, emptyResultDataAccessException);
        }
        return learnerList;
    }

    /**
     * Custom {@link ResultSetExtractor} class to extract a {@link ResultSet}
     * and return list of {@link Learner} objects from result set.
     */
    private static class LearnersSubscribedTOCategoriesBelongingToResourceResultExtractor
            implements ResultSetExtractor<List<Learner>> {

        @Override
        public List<Learner> extractData(final ResultSet rs) throws SQLException, DataAccessException {
            final List<Learner> learnerList = new ArrayList<>();
            while (rs.next()) {
                learnerList.add(new Learner(rs.getString(LEARNERS_USERID_COLUMN_LABEL),
                        rs.getString(LEARNERS_EMAIL_COLUMN_LABEL)));
            }
            return learnerList;
        }
    }
}