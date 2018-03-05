package com.cerner.devcenter.education.controllers;

import static com.cerner.devcenter.education.utils.Constants.AVAILABLE_USERS;
import static com.cerner.devcenter.education.utils.Constants.LOG_ERROR_BINDING;
import static com.cerner.devcenter.education.utils.Constants.LOG_ERROR_DELETE_USER;
import static com.cerner.devcenter.education.utils.Constants.MANAGE_ADMINS;
import static com.cerner.devcenter.education.utils.Constants.MESSAGE;
import static com.cerner.devcenter.education.utils.Constants.REDIRECT_LOGIN;
import static com.cerner.devcenter.education.utils.Constants.RETRIEVE_USERS_LIST_ERROR;
import static com.cerner.devcenter.education.utils.Constants.USER_DELETE_EXCEPTION_MESSAGE;
import static com.cerner.devcenter.education.utils.Constants.USER_REMOVED_SUCCESSFUL_MESSAGE;
import static com.google.common.base.Preconditions.checkArgument;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.managers.UserManager;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.User;
import com.cerner.devcenter.education.user.UserDetails;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthenticationStatusUtil;
import com.cerner.devcenter.education.utils.AuthorizationLevel;
import com.cerner.devcenter.education.utils.Constants;

/**
 * Controller for adding a user.
 *
 * @author Surbhi Singh (SS043472)
 * @author Manoj Raj Devalla (MD042936)
 * @author Asim Mohammed (AM045300)
 * @author Anudeep Kumar Gadam (AG045334)
 * @author Jacob Zimmermann (JZ022690)
 * @author Rishabh Bhojak (RB048032)
 */
@Controller
@RequestMapping("/app")
public class UserController {

    private static final Logger LOGGER = Logger.getLogger(UserController.class);
    private static final String USER_DETAILS = "userDetails";
    private static final String USER_ID = "userID";
    private static final String AUTHORIZATION_LEVEL = "authorizationLevel";
    private static final String AUTH_LEVEL_OF_USER = "authLevels";
    private static final String USER_ADD_ADMIN = "/user/addAdmin";
    private static final String USER_REMOVE_ADMIN = "/user/removeAdmin";

    private static final String NEW_ADMIN_LOG_MESSAGE = "User {0} was promoted to admin";
    private static final String POST_NOT_LOGGED_IN = "{0} was sent a POST without being logged in";
    private static final String POST_NOT_ADMIN = "{0} was sent a POST without being an admin";
    private static final String USER_ID_INVALID = "UserId cannot be null, blank, or empty";
    private static final String SESSION_INVALID = "Session cannot be null";
    private static final String ADD_NULL_USER_MESSAGE = "Cannot add a null user";
    private static final String USER_ID_NULL = "UserId cannot be null";
    private static final String AUTH_LEVEL_VALID_VALUES = "authLevel must be 0 or 1 or 2";

    private static final String ADMIN_ADDED_SUCCESS = "com.cerner.devcenter.education.controllers.UserController.adminAddedSuccessfully";
    private static final String ADMIN_ADDED_FAIL = "com.cerner.devcenter.education.controllers.UserController.addAdminError";
    private static final String BINDING_RESULT_ERROR = "com.cerner.devcenter.education.controllers.UserController.bindingResultError";
    private static final String USER_EXISTS_ERROR = "com.cerner.devcenter.education.controllers.UserController.userExistsError";
    private static final String USER_ADDED_SUCCESSFULLY = "com.cerner.devcenter.education.controllers.UserController.addedSuccessfully ";
    private static final String USER_ADD_EXCEPTION = "com.cerner.devcenter.education.controllers.UserController.userAddExceptionMessage";

    private static final String USER_ALREADY_EXISTS = "Error: User already exists";
    private static final String USER_ADDING_TABLE_ERROR = "Error: while adding user to user table";
    private static final String USER_STATUS_TO_ADMIN_ERROR = "Error setting user status to admin";

    /**
     * Creates a new {@link UserController} used for testing and Spring beans.
     */
    public UserController() {
        this(AuthenticationStatusUtil.getInstance());
    }

    /**
     * Creates a new {@link UserController} with the given
     * {@link AuthenticationStatusUtil}
     * 
     * @param status
     *            Authentication status of a user
     */
    public UserController(AuthenticationStatusUtil status) {
        this.status = status;
        this.i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());
    }

    private ResourceBundle i18nBundle;
    @Autowired
    private AuthenticationStatusUtil status;
    @Autowired
    private UserManager userManager;

    /**
     * Loads the page where an Admin can add/remove users if the user is admin,
     * else redirects to a access denied page.
     *
     * @param session
     *            a {@link HttpSession} object that stores the current session.
     *            Cannot be <code>null</code>.
     * @return {@link ModelAndView} object that redirects to one of the below
     *         pages:
     *         <ul>
     *         <li>add/remove user page if user is admin</li>
     *         <li>restricted error message page if user is not admin</li>
     *         <li>general error page if the user session no longer exists</li>
     *         </ul>
     */
    @RequestMapping(value = "/manage_admins", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView showManageAdminsPage(HttpSession session) {
        checkArgument(session != null, Constants.SESSION_NULL_ERROR_MESSAGE);
        if (!status.isLoggedIn()) {
            return redirectsNotLoggedIn();
        }
        UserProfileDetails user = (UserProfileDetails) session.getAttribute(USER_DETAILS);
        if (!userManager.isAdminUser(user.getUserId())) {
            return status.redirectsAccessDenied();
        }
        ModelAndView modelView = new ModelAndView(MANAGE_ADMINS);
        modelView.addObject(AUTH_LEVEL_OF_USER, AuthorizationLevel.values());
        modelView.addObject(Constants.USER, new User());
        try {
            final List<User> allUsersList = userManager.getAllUsersWithAuthorizationLevel(AuthorizationLevel.ADMIN);
            if (allUsersList.isEmpty()) {
                LOGGER.error(RETRIEVE_USERS_LIST_ERROR);
                modelView.addObject(MESSAGE, RETRIEVE_USERS_LIST_ERROR);
            } else {
                modelView.addObject(AVAILABLE_USERS, allUsersList);
            }
        } catch (ManagerException ex) {
            LOGGER.error(RETRIEVE_USERS_LIST_ERROR, ex);
            modelView.addObject(MESSAGE, RETRIEVE_USERS_LIST_ERROR);
        }
        return modelView;
    }

    /**
     * Loads the page where an Admin can add new users.
     *
     * @param user
     *            an {@link User} object of User.
     * @param result
     *            contains the {@link BindingResult} details to check whether
     *            there are binding errors or not in your controllers.
     * @param request
     *            a {@link HttpServletRequest} object
     * @return Returns the Spring {@link ModelAndView} object with the view name
     *         of the JSP page that is to be loaded.
     * @throws IllegalArgumentException
     *             when user object is null or userID is empty string
     */
    @RequestMapping(value = "/add_user", method = { RequestMethod.POST })
    public ModelAndView addUser(User user, BindingResult result, HttpServletRequest request) {
        if (!status.isLoggedIn()) {
            return redirectsNotLoggedIn();
        }
        checkArgument(user != null, ADD_NULL_USER_MESSAGE);
        ModelAndView modelView = new ModelAndView(MANAGE_ADMINS);
        modelView.addObject(AUTH_LEVEL_OF_USER, AuthorizationLevel.values());
        String userID = request.getParameter(USER_ID);
        AuthorizationLevel level = AuthorizationLevel.valueOf(request.getParameter(AUTHORIZATION_LEVEL));
        if (result != null && result.hasErrors()) {
            LOGGER.error(result.getAllErrors());
            modelView.addObject(MESSAGE, i18nBundle.getString(BINDING_RESULT_ERROR));
            return modelView;
        }
        if (userManager.isUserPresent(userID)) {
            LOGGER.error(USER_ALREADY_EXISTS);
            modelView.addObject(MESSAGE, i18nBundle.getString(USER_EXISTS_ERROR));
        } else {
            boolean userWasAdded = userManager.addUser(new User(userID, level), new UserDetails());
            if (userWasAdded) {
                modelView.addObject(MESSAGE, i18nBundle.getString(USER_ADDED_SUCCESSFULLY));
            } else {
                LOGGER.error(USER_ADDING_TABLE_ERROR);
                modelView.addObject(MESSAGE, i18nBundle.getString(USER_ADD_EXCEPTION));
            }
        }
        final List<User> allUsersList = userManager.getAllUsers();
        if (allUsersList.isEmpty()) {
            LOGGER.error(RETRIEVE_USERS_LIST_ERROR);
            modelView.addObject(MESSAGE, RETRIEVE_USERS_LIST_ERROR);
        } else {
            modelView.addObject(AVAILABLE_USERS, allUsersList);
        }
        return modelView;
    }

    /**
     * responds to the ajax so that user authentication level can be changed.
     *
     * @param userId
     *            represents the unique userId of the user.(cannot be null or
     *            empty)
     * @param authLevel
     *            represents the authorization level to which the user with
     *            specified userId needs to be changed.(must be 0 or 1 or 2).
     * @return true to the ajax call if the operation is success else false.
     */
    @RequestMapping(value = "/changeUserRole", method = RequestMethod.GET)
    public @ResponseBody boolean changeAuthorizationLevelBasedOnUserId(
            @RequestParam(USER_ID) String userId,
            @RequestParam("authLevel") int authLevel) {
        checkArgument(userId != null, USER_ID_NULL);
        checkArgument(authLevel >= 0 && authLevel <= 2, AUTH_LEVEL_VALID_VALUES);
        boolean IsUserRoleChanged = userManager.changeAuthorizationLevelBasedOnUserId(userId, authLevel);
        return IsUserRoleChanged;
    }

    /**
     * Loads the page where an Admin can remove users.
     *
     * @param user
     *            an {@link User} object of User, cannot be null.
     * @param result
     *            contains the {@link BindingResult} details to check whether
     *            there are binding errors or not in your controllers.
     * @param request
     *            a {@link HttpServletRequest} object
     * @return Returns the Spring {@link ModelAndView} object with the view name
     *         of the JSP page that is to be loaded.
     */
    @RequestMapping(value = "/remove_user", method = { RequestMethod.POST })
    public ModelAndView removeUser(User user, final BindingResult result, HttpServletRequest request) {
        if (!status.isLoggedIn()) {
            return redirectsNotLoggedIn();
        }
        ModelAndView modelView = new ModelAndView(MANAGE_ADMINS);
        modelView.addObject(AUTH_LEVEL_OF_USER, AuthorizationLevel.values());
        if (user == null) {
            LOGGER.error(USER_DELETE_EXCEPTION_MESSAGE);
            modelView.addObject(MESSAGE, USER_DELETE_EXCEPTION_MESSAGE);
        }
        if (result != null && result.hasErrors()) {
            LOGGER.error(result.getAllErrors());
            modelView.addObject(MESSAGE, LOG_ERROR_BINDING);
            return modelView;
        }
        String userID = request.getParameter(USER_ID);
        boolean userWasDeleted = userManager.deleteUser(new User(userID, AuthorizationLevel.ASSOCIATE));
        if (userWasDeleted) {
            modelView.addObject(MESSAGE, USER_REMOVED_SUCCESSFUL_MESSAGE);
        } else {
            LOGGER.error(LOG_ERROR_DELETE_USER);
            modelView.addObject(MESSAGE, USER_DELETE_EXCEPTION_MESSAGE);
            return modelView;
        }
        final List<User> allUsersList = userManager.getAllUsers();
        if (allUsersList.isEmpty()) {
            LOGGER.error(RETRIEVE_USERS_LIST_ERROR);
            modelView.addObject(MESSAGE, RETRIEVE_USERS_LIST_ERROR);
        } else {
            modelView.addObject(AVAILABLE_USERS, userManager.getAllUsers());
        }
        return modelView;
    }

    /**
     * Redirects to the login page when admin is not logged in.
     *
     * @return the model and view that redirects to login page
     */
    private ModelAndView redirectsNotLoggedIn() {
        ModelAndView model = new ModelAndView(REDIRECT_LOGIN);
        LOGGER.error(Constants.USER_NOT_LOGGED_IN);
        model.addObject(MESSAGE, Constants.USER_NOT_LOGGED_IN);
        return model;
    }

    /**
     * Remove admin status from user based on the userId.
     *
     * @param userId
     *            {@link String} containing the unique userId of the
     *            user.(cannot be null, empty, or blank).
     * @param session
     *            a {@link HttpSession} object that stores the current session.
     *            Cannot be <code>null</code>.
     * @return true - if the change was successful. false - if there was an
     *         error setting authorization to admin
     */
    @RequestMapping(value = "/removeAdmin", method = RequestMethod.POST)
    public @ResponseBody boolean removeAdminBasedOnUserId(@RequestParam("userId") String userId, HttpSession session) {
        if (!status.isLoggedIn()) {
            LOGGER.warn(MessageFormat.format(POST_NOT_LOGGED_IN, USER_REMOVE_ADMIN));
            return false;
        }
        UserProfileDetails user = (UserProfileDetails) session.getAttribute(USER_DETAILS);
        if (!userManager.isAdminUser(user.getUserId())) {
            LOGGER.warn(MessageFormat.format(POST_NOT_ADMIN, USER_REMOVE_ADMIN));
            return false;
        }
        checkArgument(StringUtils.isNotBlank(userId), USER_ID_INVALID);
        return userManager.changeAuthorizationLevelBasedOnUserId(userId, AuthorizationLevel.ASSOCIATE.getLevel());
    }

    /***
     * Add admin status to user based on the userId
     *
     * @param userId
     *            {@link String} containing the unique userId of the user to
     *            give admin status. Cannot be null, blank, or empty.
     * @param session
     *            a {@link HttpSession} object that stores the current session.
     *            Cannot be <code>null</code>.
     * @return {@link ModelAndView}
     *         <ul>
     *         <li>redirect to the login page if not logged in</li>
     *         <li>redirect to admin block page if not admin status return
     *         to</li>
     *         <li>manageAdmins main page otherwise</li>
     *         </ul>
     */
    @RequestMapping(value = "/addAdmin", method = RequestMethod.POST)
    public @ResponseBody ModelAndView addAdminBasedOnUserId(
            @RequestParam("userId") String userId,
            HttpSession session) {
        checkArgument(StringUtils.isNotBlank(userId), USER_ID_INVALID);
        checkArgument(session != null, SESSION_INVALID);
        if (!status.isLoggedIn()) {
            LOGGER.warn(MessageFormat.format(POST_NOT_LOGGED_IN, USER_ADD_ADMIN));
            return redirectsNotLoggedIn();
        }
        UserProfileDetails user = (UserProfileDetails) session.getAttribute(USER_DETAILS);
        if (!userManager.isAdminUser(user.getUserId())) {
            LOGGER.warn(MessageFormat.format(POST_NOT_ADMIN, USER_ADD_ADMIN));
            return status.redirectsAccessDenied();
        }
        if (userManager.isUserPresent(userId)) {
            userManager.changeAuthorizationLevelBasedOnUserId(userId, AuthorizationLevel.ADMIN.getLevel());
            return showManageAdminsPage(session).addObject(MESSAGE, i18nBundle.getString(ADMIN_ADDED_SUCCESS));
        } else {
            boolean userWasAdded = userManager.addUser(new User(userId, AuthorizationLevel.ADMIN), new UserDetails());
            if (userWasAdded) {
                LOGGER.info(MessageFormat.format(NEW_ADMIN_LOG_MESSAGE, userId));
                return showManageAdminsPage(session).addObject(MESSAGE, i18nBundle.getString(ADMIN_ADDED_SUCCESS));
            } else {
                LOGGER.error(USER_STATUS_TO_ADMIN_ERROR);
                return showManageAdminsPage(session).addObject(MESSAGE, i18nBundle.getString(ADMIN_ADDED_FAIL));
            }
        }
    }
}
