package com.cerner.devcenter.education.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.User;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthorizationLevel;

/**
 * Interface to add a user
 * 
 * @author Surbhi Singh(SS043472)
 * @author Jacob Zimmermann (JZ022690)
 */
public interface UserDAO {
    /**
     * Check for user exists or not
     *
     * @param userID
     *            the {@link Integer} represents the unique ID of a user
     * @return true if user is already present else false
     * @throws DAOException
     *             when
     *             {@link JdbcTemplate#queryForObject(String, Class, Object...)}
     *             throws {@link DataAccessException}
     */
    public boolean isUserPresent(String userID);

    /**
     * Perform an operation to add a user
     * 
     * @param userProfileDetails
     *            the {@link UserProfileDetails} object that has user
     *            information
     * @param authorizationLevel
     *            the {@link Integer} level of authorization assigned to user
     * @throws DAOException
     *             if there is an error in executing update
     */

    public boolean addUser(UserProfileDetails userProfileDetails, String userRole, int authorizationLevel);

    /**
     * Check if a user with a specific UserID already exists.
     *
     * @param userID
     *            represents the unique ID of a user
     * @return true if user is already present, otherwise false.
     */
    public boolean isUserAlreadyPresent(final String userID);

    /**
     * Retrieves all the users.
     * 
     * @return a {@link List} of User objects.
     */

    public List<User> getAllUsers();

    /**
     * Deletes a user
     * 
     * @param user
     *            {@link User} object which will be deleted.
     * 
     * @throws DAOException
     *             if delete operation fails
     */
    public boolean deleteUser(User user);

    /**
     * Check if user is administrator or not.
     *
     * @param userId
     *            of the user.
     * @return the boolean value if user is administrator or not
     * @throws DAOException
     *             when lookup operation fails
     */
    public boolean isAdminUser(String userId);

    /**
     * Sets the Authorization Level of the user to associate , admin, or
     * instructor based on the passed in userId and authLevel.
     * 
     * @param userId
     *            represents the unique userId of the user.(cannot be null or
     *            empty)
     * @param authLevel
     *            represents the authorization level to which the user with
     *            specified userId needs to be changed.(must be 0, 1 or 2).
     * @return true if the authLevel is set for the specified userId, else
     *         false.
     * @throws IllegalArgumentException
     *             if userId is null/empty or authLevel is not 0, 1 or 2.
     */
    public boolean changeAuthorizationLevelBasedOnUserId(final String userId, final int authLevel);

    /***
     * Retrieve all users with a given authorization level.
     * 
     * @param authLevel
     *            {@link AuthorizationLevel} of the users to retrieve. Cannot be
     *            null.
     * @return {@link List} of {@link User} containing all the users with the
     *         given authorization level. May be empty if no results are found.
     *         Will never be null.
     * @throws IllegalArgumentException
     *             when authLevel is null.
     * @throws DAOException
     *             when there is an error getting the users from the database
     */
    List<User> getAllUsersWithAuthorizationLevel(final AuthorizationLevel authLevel) throws DAOException;
}
