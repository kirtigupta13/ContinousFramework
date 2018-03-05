package com.cerner.devcenter.education.controllers;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.managers.RequestCategoryManager;
import com.cerner.devcenter.education.models.RequestCategory;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthenticationStatusUtil;

/**
 * Handles requests for adding, deleting and retrieving requested categories.
 */
@Controller
@RequestMapping("/app")
public class RequestCategoryController {

    private static final String REDIRECT_LOGIN = "redirect:/login";

    private static final String ERROR_MESSAGE_ATTRIBUTE = "errorMessage";
    private static final String SUCCESS_MESSAGE_ATTRIBUTE = "successMessage";
    private static final String REQUESTED_CATEGORIES_ATTRIBUTE = "requestedCategories";
    private static final String USER_DETAILS_ATTRIBUTE = "userDetails";

    private static final String NULL_SESSION_ERROR_MESSAGE = "Session is null.";
    private static final String NULL_USER_DETAILS_ERROR_MESSAGE = "UserDetails is null.";
    private static final String NULL_REQUEST_CATEGORY_ERROR_MESSAGE = "RequestCategory object is null.";
    private static final String INVALID_REQUEST_CATEGORY_ID_ERROR_MESSAGE = "RequestCategory id is zero/negative.";
    private static final String INVALID_USER_ID_ERROR_MESSAGE = "UserId is null/empty/blank.";

    private static final String REQUEST_CATEGORY_DB_INSERT_ERROR_MESSAGE = "Error while adding the requested category to the database.";
    private static final String REQUEST_CATEGORY_DB_DELETE_ERROR_MESSAGE = "Error while deleting the requested category from the database.";
    private static final String REQUEST_CATEGORY_DB_READ_ERROR_MESSAGE = "Error while retrieving the requested category from the database.";

    private static final String REQUEST_CATEGORY_SUCCESS_MESSAGE = "requestCategory.successMessage";
    private static final String REQUEST_CATEGORY_ERROR_MESSAGE = "requestCategory.errorMessage";

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourcesController.class);

    private static final ResourceBundle I18N_BUNDLE = ResourceBundle.getBundle("i18n", Locale.getDefault());

    @Autowired
    private RequestCategoryManager requestCategoryManager;
    @Autowired
    private AuthenticationStatusUtil authenticationStatus;

    /**
     * Handles the request for adding the requested category.
     *
     * @param requestCategory
     *            a requested category which will be added. Cannot be
     *            <code>null</code>
     * @param session
     *            a {@link HttpSession} that contains information about the
     *            current session. Cannot be <code>null</code>
     * @return a {@link ModelAndView} with success/error messages and the name
     *         of the page that is to be loaded
     * @throws IllegalArgumentException
     *             when
     *             <ul>
     *             <li><code>requestCategory</code> is <code>null</code></li>
     *             <li>{@link RequestCategory#getName()} returns
     *             <code>null</code></li>
     *             <li><code>session</code> is <code>null</code></li>
     *             <li>{@link HttpSession#getAttribute(String)} is
     *             <code>null</code></li>
     *             <li>{@link UserProfileDetails#getUserId()} is
     *             <code>null</code>/empty/blank</li>
     *             </ul>
     */
    @RequestMapping(value = "/addRequestCategory", method = RequestMethod.POST)
    public ModelAndView addRequestCategory(final HttpSession session,
            @ModelAttribute("requestCategory") final RequestCategory requestCategory) {
        if (!authenticationStatus.isLoggedIn()) {
            return new ModelAndView(REDIRECT_LOGIN);
        }
        checkArgument(requestCategory != null, NULL_REQUEST_CATEGORY_ERROR_MESSAGE);
        checkArgument(session != null, NULL_SESSION_ERROR_MESSAGE);
        final UserProfileDetails userProfileDetails = (UserProfileDetails) session.getAttribute(USER_DETAILS_ATTRIBUTE);
        checkArgument(userProfileDetails != null, NULL_USER_DETAILS_ERROR_MESSAGE);
        final String userId = userProfileDetails.getUserId();
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID_ERROR_MESSAGE);
        final ModelAndView modelAndView = new ModelAndView();
        try {
            requestCategoryManager.addRequestCategory(requestCategory, userId);
            modelAndView.addObject(SUCCESS_MESSAGE_ATTRIBUTE, I18N_BUNDLE.getString(REQUEST_CATEGORY_SUCCESS_MESSAGE));
        } catch (final ManagerException managerException) {
            LOGGER.error(REQUEST_CATEGORY_DB_INSERT_ERROR_MESSAGE, managerException);
            modelAndView.addObject(ERROR_MESSAGE_ATTRIBUTE, I18N_BUNDLE.getString(REQUEST_CATEGORY_ERROR_MESSAGE));
        }
        return modelAndView;
    }

    /**
     * Handles the request for deleting the requested category.
     *
     * @param requestCategoryId
     *            a requested category identifier to be deleted. Cannot be
     *            zero/negative
     * @param session
     *            a {@link HttpSession} that contains information about the
     *            current session. Cannot be <code>null</code>
     * @return a {@link ModelAndView} with success/error messages and the name
     *         of the page that is to be loaded
     * @throws IllegalArgumentException
     *             when
     *             <ul>
     *             <li><code>requestCategoryId</code> is zero/negative</li>
     *             <li><code>session</code> is <code>null</code></li>
     *             <li>{@link HttpSession#getAttribute(String)} is
     *             <code>null</code></li>
     *             <li>{@link UserProfileDetails#getUserId()} is
     *             <code>null</code>/empty/blank</li>
     *             </ul>
     */
    @RequestMapping(value = "/deleteRequestCategory", method = RequestMethod.POST)
    public ModelAndView deleteRequestCategory(final HttpSession session,
            @RequestParam(value = "requestCategoryId") final int requestCategoryId) {
        if (!authenticationStatus.isLoggedIn()) {
            return new ModelAndView(REDIRECT_LOGIN);
        }
        checkArgument(requestCategoryId > 0, INVALID_REQUEST_CATEGORY_ID_ERROR_MESSAGE);
        checkArgument(session != null, NULL_SESSION_ERROR_MESSAGE);
        final UserProfileDetails userProfileDetails = (UserProfileDetails) session.getAttribute(USER_DETAILS_ATTRIBUTE);
        checkArgument(userProfileDetails != null, NULL_USER_DETAILS_ERROR_MESSAGE);
        final String userId = userProfileDetails.getUserId();
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID_ERROR_MESSAGE);
        final ModelAndView modelAndView = new ModelAndView();
        try {
            requestCategoryManager.deleteRequestCategory(requestCategoryId, userId);
            modelAndView.addObject(SUCCESS_MESSAGE_ATTRIBUTE, I18N_BUNDLE.getString(REQUEST_CATEGORY_SUCCESS_MESSAGE));
        } catch (final ManagerException managerException) {
            LOGGER.error(REQUEST_CATEGORY_DB_DELETE_ERROR_MESSAGE, managerException);
            modelAndView.addObject(ERROR_MESSAGE_ATTRIBUTE, I18N_BUNDLE.getString(REQUEST_CATEGORY_ERROR_MESSAGE));
        }
        return modelAndView;
    }

    /**
     * Handles the request for retrieving the list of requested categories.
     *
     * @param session
     *            a {@link HttpSession} that contains information about the
     *            current session. Cannot be <code>null</code>
     * @return a {@link ModelAndView} with success/error messages and the name
     *         of the page that is to be loaded
     * @throws IllegalArgumentException
     *             when <code>session</code> is <code>null</code>
     */
    @RequestMapping(value = "/getAllRequestCategories", method = RequestMethod.GET, params = { "session" })
    public ModelAndView getAllRequestCategories(final HttpSession session) {
        if (!authenticationStatus.isLoggedIn()) {
            return new ModelAndView(REDIRECT_LOGIN);
        }
        checkArgument(session != null, NULL_SESSION_ERROR_MESSAGE);
        final ModelAndView modelAndView = new ModelAndView();
        try {
            final List<RequestCategory> requestCategories = requestCategoryManager.getAllRequestCategories();
            modelAndView.addObject(REQUESTED_CATEGORIES_ATTRIBUTE, requestCategories);
            modelAndView.addObject(SUCCESS_MESSAGE_ATTRIBUTE, I18N_BUNDLE.getString(REQUEST_CATEGORY_SUCCESS_MESSAGE));
        } catch (final ManagerException managerException) {
            LOGGER.error(REQUEST_CATEGORY_DB_READ_ERROR_MESSAGE, managerException);
            modelAndView.addObject(ERROR_MESSAGE_ATTRIBUTE, I18N_BUNDLE.getString(REQUEST_CATEGORY_ERROR_MESSAGE));
        }
        return modelAndView;
    }

    /**
     * Handles the request for retrieving the list of all approved requested
     * categories if <code>isApproved</code> is <code>true</code> otherwise
     * retrieves unapproved requested categories.
     *
     * @param isApproved
     *            <code>True</code> to retrieve only approved requested
     *            categories, <code>False</code> to retrieve only unapproved
     *            requested categories
     * @param session
     *            a {@link HttpSession} that contains information about the
     *            current session. Cannot be <code>null</code>
     * @return a {@link ModelAndView} with success/error messages and the name
     *         of the page that is to be loaded
     * @throws IllegalArgumentException
     *             when <code>session</code> is <code>null</code>
     */
    @RequestMapping(value = "/getAllRequestCategories", method = RequestMethod.GET, params = { "session",
            "isApproved" })
    public ModelAndView getAllRequestCategories(final HttpSession session,
            @RequestParam(value = "isApproved") final boolean isApproved) {
        if (!authenticationStatus.isLoggedIn()) {
            return new ModelAndView(REDIRECT_LOGIN);
        }
        checkArgument(session != null, NULL_SESSION_ERROR_MESSAGE);
        final ModelAndView modelAndView = new ModelAndView();
        try {
            final List<RequestCategory> requestCategories = requestCategoryManager.getAllRequestCategories(isApproved);
            modelAndView.addObject(REQUESTED_CATEGORIES_ATTRIBUTE, requestCategories);
            modelAndView.addObject(SUCCESS_MESSAGE_ATTRIBUTE, I18N_BUNDLE.getString(REQUEST_CATEGORY_SUCCESS_MESSAGE));
        } catch (final ManagerException managerException) {
            LOGGER.error(REQUEST_CATEGORY_DB_READ_ERROR_MESSAGE, managerException);
            modelAndView.addObject(ERROR_MESSAGE_ATTRIBUTE, I18N_BUNDLE.getString(REQUEST_CATEGORY_ERROR_MESSAGE));
        }
        return modelAndView;
    }
}