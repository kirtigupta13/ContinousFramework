package com.cerner.devcenter.education.controllers;

import static com.google.common.base.Preconditions.checkArgument;

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
import com.cerner.devcenter.education.managers.UserResourceRatingManager;
import com.cerner.devcenter.education.models.UserResourceRating;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthenticationStatusUtil;
import com.cerner.devcenter.education.utils.Rating;
import com.cerner.devcenter.education.utils.Status;

/**
 * This controller is for a user to mark a learning resource as complete along
 * with the rating .
 *
 * @author Asim Mohammed (AM045300)
<<<<<<< HEAD
 * @author Satnosh Kumar (SK051343)
=======
 * @author Santosh Kumar (SK051343)
>>>>>>> 21224e2... ACADEM-23254 Fixed the issue of using search bar while in profile, manage admin page
 */
@Controller
@RequestMapping("/app")
public class UserResourceRatingController {

    @Autowired
    private AuthenticationStatusUtil status;
    @Autowired
    private UserResourceRatingManager userResourceRatingManager;

    private static final String REDIRECT_LOGIN = "redirect:/login";
    private static final String REDIRECT_HOMEPAGE = "redirect:/app/home_page";
    private static final String USERDETAILS = "userDetails";
    private static final String MESSAGE = "message";
    private static final String INVALID_HTTPSESSION_OBJECT = "HttpSession object passed can't be null";
    private static final String INVALID_HTTPSERVLET_OBJECT = "HttpServletRequest object passed can't be null";
    private static final String INVALID_RESOURCE_ID = "resourceId must be non-null numeric value";
    private static final String INVALID_RATING = "rating must be non-null numeric value";
    private static final Logger LOGGER = Logger.getLogger(UserResourceRatingController.class);

    private static ResourceBundle i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

    /**
     * Add user's completion status and feedback on a learning resource
     *
     * @param session
     *            {@link HttpSession} that contains objects related to current
     *            session (must not be <code>null</code>)
     * @param request
     *            {@link HttpServletRequest} that contains the required values
     *            to make updates to the user interested topics (must not be
     *            <code>null</code>
     * @return {@link ModelAndView} that redirects to one of the below pages:
     *         <ul>
     *         <li>login page if the user is not already logged in.</li>
     *         <li>homepage if user is logged in.</li>
     *         <li>home page if user is logged in.</li>
     *         </ul>
     * @throws IllegalArgumentException
     *             when any of the following are true:
     *             <ul>
     *             <li>session is null</li>
     *             <li>request is null</li>
     *             <li>resourceId is not a non-null positive numeric value</li>
     *             <li>rating is not a non-null positive numeric value</li>
     *             </ul>
     */
    @RequestMapping(value = "/addResourceDetails", method = RequestMethod.POST)
    public ModelAndView addResourceDetails(final HttpSession session, final HttpServletRequest request) {
        checkArgument(session != null, INVALID_HTTPSESSION_OBJECT);
        checkArgument(request != null, INVALID_HTTPSERVLET_OBJECT);
        checkArgument(StringUtils.isNumeric(request.getParameter("resourceId")), INVALID_RESOURCE_ID);
        checkArgument(StringUtils.isNumeric(request.getParameter("rating")), INVALID_RATING);
        if (!status.isLoggedIn()) {
            return new ModelAndView(REDIRECT_LOGIN);
        }
        final int resourceId = Integer.valueOf(request.getParameter("resourceId"));
        final Rating RATING = (Integer.valueOf(request.getParameter("rating")) == 1) ? Rating.LIKE : Rating.DISLIKE;
        final UserProfileDetails user = (UserProfileDetails) session.getAttribute(USERDETAILS);
        final ModelAndView model = new ModelAndView(REDIRECT_HOMEPAGE);
        try {
            userResourceRatingManager.addUserResourceRating(
                    new UserResourceRating(user.getUserId(), resourceId, RATING, Status.COMPLETE));
            model.addObject(
                    MESSAGE,
                    i18nBundle.getString(
                            "com.cerner.devcenter.education.controllers.UserResourceController.statusUpdateSuccess"));
        } catch (ManagerException managerException) {
            LOGGER.error(
                    "Error adding user rating and status for the resource: " + resourceId + " " + managerException);

            model.addObject(
                    MESSAGE,
                    i18nBundle.getString(
                            "com.cerner.devcenter.education.controllers.UserResourceController.statusUpdateError"));
        }
        return model;
    }
}
