package com.cerner.devcenter.education.controllers;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.managers.CompletedUserResourceManager;
import com.cerner.devcenter.education.models.CompletedResource;
import com.cerner.devcenter.education.models.CompletedUserResource;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthenticationStatusUtil;
import com.cerner.devcenter.education.utils.CompletionRating;

/**
 * Controller to mark a resource as complete along with the rating for a user
 * and show the completed resources by the user .
 *
 * @author Vinutha Nuchimaniyanda (VN046193)
 * @author Rishabh Bhojak (RB048032)
 * @author Vincent Dasari (VD049645)
 */
@Controller
@RequestMapping("/app")
public class CompletedUserResourceController {

    private static final String REDIRECT_LOGIN = "redirect:/login";
    private static final String REDIRECT_HOMEPAGE = "forward:/app/home_page";
    private static final String COMPLETED_RESOURCE_PAGE = "completed_resource";
    private static final String MESSAGE = "message";
    private static final String USER_PROFILE_DETAILS = "userDetails";
    private static final String SUCCESS_MESSAGE = "CompletedUserResourceController.SuccessMessage";
    private static final String ERROR_MESSAGE = "CompletedUserResourceController.ErrorMessage";
    private static final String NO_RECORDS_MESSAGE = "com.cerner.devcenter.education.controllers.CompletedUserResourceController.NoRecordsMessage";
    private static final String COMPLETED_RESOURCE_ERROR_MESSAGE = "com.cerner.devcenter.education.controllers.CompletedUserResourceController.ErrorMessage";
    private static final String COMPLETED_RESOURCES = "completedResources";
    private static final String USER_NULL_ERROR_MESSAGE = "user cannot be null";
    private static final String COMPLETED_DATE_ZERO = "CompletionDate should not be 0";
    private static final String HTTPSESSION_NOT_NULL = "HttpSession object cannot be null";
    private static final String HTTPSERVLET_NOT_NULL = "HttpServletRequest object cannot be null";
    private static final String COMPLETED_DATE_GREATER_THAN_ZERO = "CompletionDate should be greater than 0";
    private static final String PARAM_RESOURCE_ID = "resource-id";
    private static final String PARAM_RATING = "rating";
    private static final String RESOURCE_ID_EXCEPTION_MESSAGE = "ResourceId must be a numeric value";
    private static final String COMPLETION_RATING_EXCEPTION_MESSAGE = "Completion rating must be a numeric value";
    private static final String COMPLETION_RATING_NOT_VALID = "Completion rating is not valid";
    private static final String RESOURCE_ID_ZERO = "ResourceId is equal to zero";
    private static final String ERROR_ADDING_EXCEPTION = "Error adding user rating and status for the resource: %d with the exception: %s";
    private static final String ERROR_QUERING_EXCEPTION = "Error quering completed resources for user: %s with the exception: %s";
    private static final Logger LOGGER = Logger.getLogger(CompletedUserResourceController.class);
    private static final ResourceBundle I18N_BUNDLE = ResourceBundle.getBundle("i18n", Locale.getDefault());

    private AuthenticationStatusUtil loginStatus = AuthenticationStatusUtil.getInstance();

    @Autowired
    private CompletedUserResourceManager completedUserResourceManager;

    /**
     * Adds the completed resource along with the rating
     *
     * @param httpSession
     *            a {@link HttpSession} object. Must not be <code>null</code>
     *            and contains current session objects.
     * @param httpServletRequest
     *            a {@link HttpServletRequest} object. Must not be
     *            <code>null</code> and contains current request objects such as
     *            <code>resource-id</code>,<code>rating</code> and
     *            <code>completionDate</code> to mark a resource as complete
     * @return a modelAndView {@link ModelAndView} and redirects to one of the
     *         below pages
     *         <ul>
     *         <li>homepage if successful</li>
     *         <li>redirects to login page if not logged in</li>
     *         </ul>
     * @throws NullPointerException
     *             when
     *             <ul>
     *             <li>httpSession is <code>null</code></li>
     *             <li>httpServletRequest is <code>null</code></li>
     *             </ul>
     * @throws IllegalArgumentException
     *             when
     *             <ul>
     *             <li>resource-id is not numeric</li>
     *             <li>resource-id is equal to zero</li>
     *             <li>rating is not valid</li>
     *             <li>Completion rating is not numeric</li>
     *             <li>completionDate should not be 0</li>
     *             </ul>
     */
    @RequestMapping(value = "/completeResource", method = RequestMethod.POST)
    public ModelAndView addCompletedUserResource(final HttpSession httpSession,
            final HttpServletRequest httpServletRequest) {
        final ModelAndView modelAndView = new ModelAndView();
        checkNotNull(httpSession, HTTPSESSION_NOT_NULL);
        checkNotNull(httpServletRequest, HTTPSERVLET_NOT_NULL);
        checkArgument(StringUtils.isNumeric(httpServletRequest.getParameter(PARAM_RESOURCE_ID)),
                RESOURCE_ID_EXCEPTION_MESSAGE);
        checkArgument(StringUtils.isNumeric(httpServletRequest.getParameter(PARAM_RATING)),
                COMPLETION_RATING_EXCEPTION_MESSAGE);
        if (!loginStatus.isLoggedIn()) {
            return new ModelAndView(REDIRECT_LOGIN);
        }
        final int resourceId = Integer.valueOf((httpServletRequest.getParameter(PARAM_RESOURCE_ID)));
        checkArgument(resourceId != 0, RESOURCE_ID_ZERO);
        final CompletionRating completionRating = CompletionRating
                .getRating(Integer.valueOf(httpServletRequest.getParameter(PARAM_RATING)));
        checkArgument(completionRating != null, COMPLETION_RATING_NOT_VALID);
        final long completionDate = System.currentTimeMillis() / 1000;
        checkArgument(completionDate > 0, COMPLETED_DATE_GREATER_THAN_ZERO);
        final String date = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss a")
                .format(new java.util.Date(completionDate * 1000));
        final UserProfileDetails user = (UserProfileDetails) httpSession.getAttribute(USER_PROFILE_DETAILS);
        checkArgument(user != null, USER_NULL_ERROR_MESSAGE);
        try {
            completedUserResourceManager.addCompletedUserResourceRating(
                    new CompletedUserResource(user.getUserId(), resourceId, completionRating, completionDate));
            modelAndView.setViewName(REDIRECT_HOMEPAGE);
            final String pattern = I18N_BUNDLE.getString(SUCCESS_MESSAGE);
            final String concatenatedVal = MessageFormat.format(pattern, date);
            modelAndView.addObject(MESSAGE, concatenatedVal);
        } catch (final ManagerException managerException) {
            LOGGER.error(String.format(ERROR_ADDING_EXCEPTION, resourceId, managerException));
            modelAndView.addObject(MESSAGE, I18N_BUNDLE.getString(ERROR_MESSAGE));
        }
        return modelAndView;
    }

    /**
     * Retrieve completed resource along with the rating
     *
     * @param httpSession
     *            a {@link HttpSession} object. Must not be null and contains
     *            current session objects.
     * @return a modelAndView {@link ModelAndView} and redirects to one of the
     *         below pages
     *         <ul>
     *         <li>completed resource page if successful</li>
     *         <li>redirects to login page if not logged in</li>
     *         </ul>
     * @throws NullPointerException
     *             when httpSession is null
     */
    @RequestMapping(value = "/completedResource", method = RequestMethod.GET)
    public ModelAndView showCompletedResource(final HttpSession httpSession) {
        checkNotNull(httpSession, HTTPSESSION_NOT_NULL);
        if (!loginStatus.isLoggedIn()) {
            return new ModelAndView(REDIRECT_LOGIN);
        }
        final ModelAndView modelAndView = new ModelAndView(COMPLETED_RESOURCE_PAGE);
        final UserProfileDetails user = (UserProfileDetails) httpSession.getAttribute(USER_PROFILE_DETAILS);
        try {
            final List<CompletedResource> completedResources = completedUserResourceManager
                    .getCompletedResourcesByUserId(user.getUserId());
            if (completedResources == null || completedResources.isEmpty()) {
                modelAndView.addObject(MESSAGE, I18N_BUNDLE.getString(NO_RECORDS_MESSAGE));
            }
            modelAndView.addObject(COMPLETED_RESOURCES, completedResources);
        } catch (final ManagerException managerException) {
            LOGGER.error(String.format(ERROR_QUERING_EXCEPTION, user.getUserId(), managerException));
            modelAndView.addObject(MESSAGE, I18N_BUNDLE.getString(COMPLETED_RESOURCE_ERROR_MESSAGE));
        }
        return modelAndView;
    }
}