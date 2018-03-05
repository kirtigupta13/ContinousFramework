package com.cerner.devcenter.education.dao;

import static com.cerner.devcenter.education.utils.SqlQueries.CHANGE_AUTH_LEVEL_BASED_ON_USER_ID;
import static com.cerner.devcenter.education.utils.SqlQueries.CHECK_ADMIN;
import static com.cerner.devcenter.education.utils.SqlQueries.CHECK_DUPLICATE_DATA;
import static com.cerner.devcenter.education.utils.SqlQueries.DELETE_USER;
import static com.cerner.devcenter.education.utils.SqlQueries.INSERT_USER_QUERY;
import static com.cerner.devcenter.education.utils.SqlQueries.SELECT_USERS;
import static com.cerner.devcenter.education.utils.SqlQueries.SELECT_USERS_BY_AUTHORIZATION_LEVEL;
import static com.cerner.devcenter.education.utils.SqlQueries.SELECT_USER_BY_USERID;
import static com.google.common.base.Preconditions.checkArgument;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.User;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthorizationLevel;

/**
 * Implements UserDAO {@link UserDAO} backed by a relational database.
 * 
 * @author Surbhi Singh(SS043472)
 * @author Jacob Zimmermann (JZ022690)
 *
 */
@Repository("addUserDAO")
public class UserDAOImpl implements UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAOImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Checks whether the user with the given ID exists.
     *
     * @param userID
     *            represents the unique ID of a user
     * @return true if user is already present, otherwise false
     */
    @Override
    public boolean isUserPresent(final String userID) {

        try {
            return jdbcTemplate.queryForObject(CHECK_DUPLICATE_DATA.getQuery(), new Object[] { userID },
                    new UserPresentMapper());
        } catch (EmptyResultDataAccessException emptyResultEx) {
            LOGGER.error("Error unable to execute query", emptyResultEx);
            return false;
        }
    }

    /**
     * Checks whether the user for the given userID exists.
     *
     * @param userID
     *            represents the unique ID of a user
     * @return true if user is already present, otherwise false
     */
    @Override
    public boolean isUserAlreadyPresent(final String userID) {

        try {
            String userId = jdbcTemplate.queryForObject(SELECT_USER_BY_USERID.getQuery(), String.class, userID);
            return userId != null;
        } catch (DataAccessException emptyResultEx) {
            LOGGER.error("unable to execute select query on user table", emptyResultEx);
        }
        return false;
    }

    /**
     * Perform operation to add a user
     * 
     * @param userProfileDetails
     *            the {@link UserProfileDetails} object that has user
     *            information
     * @param authorizationLevel
     *            the {@link Integer} level of authorization assigned to user
     * @return true if adding a user is success else false
     */
    @Override
    public boolean addUser(UserProfileDetails userProfileDetails, final String userRole, final int authorizationLevel) {
        checkArgument(userProfileDetails != null, "Error: course object is null");
        try {
            jdbcTemplate.update(INSERT_USER_QUERY.getQuery(), userProfileDetails.getUserId(),
                    userProfileDetails.getFirstName(), userProfileDetails.getLastName(), userProfileDetails.getEmail(),
                    userRole, authorizationLevel);
        } catch (DataAccessException dataAccessEx) {
            LOGGER.error("Error unable to execute query", dataAccessEx);
            return false;
        }
        return true;
    }

    /**
     * Retrieves all the available users.
     * 
     * @return a {@link List} of User objects if transaction is success else
     *         return empty list
     */
    @Override
    public List<User> getAllUsers() {
        try {
            List<Map<String, Object>> users = jdbcTemplate.queryForList(SELECT_USERS.getQuery());
            List<User> usersAvailable = new ArrayList<User>(users.size());
            for (Map<String, Object> user : users) {
                usersAvailable.add(new User(user));
            }
            return usersAvailable;
        } catch (DataAccessException dataAccessEx) {
            LOGGER.error("Error unable to execute query", dataAccessEx);
            return Collections.<User>emptyList();
        }
    }

    /**
     * Deletes a user
     * 
     * @param user
     *            {@link User} object which we will removed.
     * @return boolean value true if operation is success.
     */
    @Override
    public boolean deleteUser(User user) {
        checkArgument(user != null, "Error: User object is NULL");
        String userID = user.getUserID();
        checkArgument(userID != null, "Error: userID cannot be null");
        try {
            jdbcTemplate.update(DELETE_USER.getQuery(), userID);
        } catch (DataAccessException dataAccessEx) {
            LOGGER.error("Error unable to execute query", dataAccessEx);
            return false;
        }
        return true;
    }

    /**
     * Check if user is administrator or not.
     *
     * @param userId
     *            of the user.
     * @return boolean value true if user is administrator else false
     */
    @Override
    public boolean isAdminUser(final String userId) {

        try {
            User user = jdbcTemplate.queryForObject(CHECK_ADMIN.getQuery(), new UserRowMapper(), userId);
            // Admin has a value of zero
            return user.getRole().equalsIgnoreCase(AuthorizationLevel.getAuthorizationForValue(0).toString());
        } catch (DataAccessException dataAccessEx) {
            LOGGER.error("Error unable to execute query", dataAccessEx);
            return false;
        }
    }

    /**
     * Custom {@link RowMapper} class to map a {@link ResultSet} to a new
     * {@link Course} object.
     */
    class UserPresentMapper implements RowMapper<Boolean> {

        /**
         * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet,
         *      int)
         */
        @Override

        public Boolean mapRow(ResultSet row, int rowNum) throws SQLException {
            boolean userPresent = row.getBoolean("user_status");
            return userPresent;
        }
    }

    /**
     * Custom {@link RowMapper} class to map a {@link ResultSet} to a new
     * {@link User} object.
     */
    class UserRowMapper implements RowMapper<User> {

        /**
         * @see org.springframework.jdbc.core.RowMapper#mapRow(ResultSet, int)
         */
        @Override
        public User mapRow(ResultSet row, int rowNum) throws SQLException {
            return new User(row.getString("user_id"), AuthorizationLevel.valueOf(row.getString("role")),
                    row.getString("first_name"), row.getString("last_name"), row.getString("email_id"),
                    row.getString("role"));
        }
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public boolean changeAuthorizationLevelBasedOnUserId(String userId, int authLevel) {
        checkArgument(StringUtils.isNotBlank(userId), "userId is null/empty");
        checkArgument((authLevel >= 0 && authLevel <= 2), "authLevel must be on a scale of 0-2");
        String role = AuthorizationLevel.values()[authLevel].toString();

        try {
            jdbcTemplate.update(CHANGE_AUTH_LEVEL_BASED_ON_USER_ID.getQuery(), role, authLevel, userId);
            return true;
        } catch (DataAccessException dataAccessEx) {
            LOGGER.error("unable to execute the change authorization query by userId", dataAccessEx);
            return false;
        }
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public List<User> getAllUsersWithAuthorizationLevel(final AuthorizationLevel authLevel) throws DAOException {
        checkArgument(authLevel != null, "AuthorizationLevel cannot be null.");
        try {
            return jdbcTemplate.query(SELECT_USERS_BY_AUTHORIZATION_LEVEL.getQuery(), new UserRowMapper(),
                    authLevel.getLevel());
        } catch (DataAccessException dataAccessEx) {
            throw new DAOException("Unable to get users with authorization level from database", dataAccessEx);
        }
    }
}
