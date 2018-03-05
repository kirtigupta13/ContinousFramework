package com.cerner.devcenter.education.managers;

import static com.cerner.devcenter.education.utils.Constants.USER_MANAGER_USERDETAILS_NULL;
import static com.cerner.devcenter.education.utils.Constants.USER_MANAGER_USER_NULL;
import static com.google.common.base.Preconditions.checkArgument;

import java.text.MessageFormat;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.dao.UserDAO;
import com.cerner.devcenter.education.models.User;
import com.cerner.devcenter.education.user.UserDetails;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthorizationLevel;

/**
 * Manages operations such as Add, Delete and Retrieve users list.
 * 
 * @author Surbhi Singh (SS043472)
 * @author Jacob Zimmermann (JZ022690)
 */
@Service("userManager")
public class UserManager {

    private static final Logger LOGGER = Logger.getLogger(UserManager.class);
    private static final String ERROR_RETRIEVING_USERS_MESSAGE = "Error retrieving users with authorization level {0} from the database.";
    private static final String NULL_AUTHORIZATION_MESSAGE = "AuthorizationLevel cannot be null";
    private static final String ERROR_ADDING_USER_MESSAGE = "Error unable to add user";
    private static final String NULL_USER_ID_MESSAGE = "UserId cannot be null";
    private static final String INVALID_USER_ID_MESSAGE = "userId cannot be null/empty";
    private static final String INVALID_AUTH_LEVEL_MESSAGE = "authLevel must be on a scale of 0-2";

    private UserProfileDetails userProfileDetails;

    @Autowired
    private UserDAO userDAO;

    public UserManager() {
    }

    /**
     * Validates the {@link User} object and adds a user.
     * 
     * @param user
     *            a {@link User} object consisting of user details.
     * @param userDetail
     *            a {@link UserDetails} object used to verify user data. This
     *            must not be null.
     * @throws ManagerException
     *             when not able to find user details or unable to get
     *             authenticated
     */
    public boolean addUser(final User user, final UserDetails userDetail) {
        checkArgument(user != null, USER_MANAGER_USER_NULL);
        checkArgument(userDetail != null, USER_MANAGER_USERDETAILS_NULL);

        int authLevel = user.getAuthorizationLevel().getLevel();
        String userRole = "";
        try {
            userProfileDetails = userDetail.getUserDetails(user.getUserID());
            userRole = AuthorizationLevel.getAuthorizationForValue(authLevel).toString();
            userDAO.addUser(userProfileDetails, userRole, authLevel);
        } catch (NamingException namingException) {
            LOGGER.error(ERROR_ADDING_USER_MESSAGE, namingException);
            return false;
        } catch (IllegalArgumentException illegalArgumentException) {
            LOGGER.error(ERROR_ADDING_USER_MESSAGE, illegalArgumentException);
            return false;
        }
        return true;
    }

    /**
     * Check if a specific userID is already present in the database.
     * 
     * @param userID
     *            represents the unique ID of a user
     * 
     * @throws IllegalArgumentException
     *             when userId is null
     * @return true if userId is already present, otherwise false.
     */
    public boolean isUserPresent(final String userID) {
        checkArgument(userID != null, NULL_USER_ID_MESSAGE);
        return userDAO.isUserAlreadyPresent(userID);
    }

    /**
     * Retrieves all the users.
     * 
     * @return a {@link List} of User objects.
     */
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    /**
     * Validates the {@link User} object and deletes a user.
     * 
     * @param user
     *            an {@link User} object will be deleted.
     * @return true if deleting a user is success else false. Returns false when
     *         user object is null
     */
    public boolean deleteUser(User user) {
        if (user != null) {
            return userDAO.deleteUser(user);
        } else {
            return false;
        }
    }

    /**
     * Check if user is administrator or not. ;
     * 
     * @param userId
     *            the {@link Integer} represents a unique ID of a user
     * @return boolean value true if user is administrator else false
     */
    public boolean isAdminUser(final String userId) {
        return userDAO.isAdminUser(userId);
    }

    /**
     * Sets the Authorization Level of the user to associate , admin, or
     * instructor based on the passed in userId and authLevel.
     * 
     * @param userId
     *            represents the unique userId of the user.(cannot be null or
     *            empty)
     * @param authLevel
     *            represents the authorization level to which the user with
     *            specified userId needs to be changed.(must be 0 or 1 or 2).
     * @return true if the authLevel is set for the specified userId, else
     *         false.
     * @throws IllegalArgumentException
     *             if userId is null/empty or authLevel is not 0, 1 or 2.
     */
    public boolean changeAuthorizationLevelBasedOnUserId(String userId, int authLevel) {
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID_MESSAGE);
        checkArgument((authLevel >= 0 && authLevel <= 2), INVALID_AUTH_LEVEL_MESSAGE);
        return userDAO.changeAuthorizationLevelBasedOnUserId(userId, authLevel);
    }

    /***
     * Retrieve all users at a given authorization level
     * 
     * @param authLevel
     *            the {@link AuthorizationLevel} of the users to retrieve.
     *            Cannot be null.
     * @return {@link List} of {@link User} containing the user information of
     *         the given authorization level
     * @throws IllegalArgumentException
     *             when authLevel is null.
     * @throws ManagerException
     *             when there is an error querying the database
     */
    public List<User> getAllUsersWithAuthorizationLevel(AuthorizationLevel authLevel) {
        checkArgument(authLevel != null, NULL_AUTHORIZATION_MESSAGE);
        try {
            return userDAO.getAllUsersWithAuthorizationLevel(authLevel);
        } catch (DAOException ex) {
            throw new ManagerException(MessageFormat.format(ERROR_RETRIEVING_USERS_MESSAGE, authLevel), ex);
        }
    }
}
