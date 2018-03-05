package com.cerner.devcenter.education.controllers;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cerner.devcenter.education.exceptions.DuplicateUserSubscriptionException;
import com.cerner.devcenter.education.managers.CategoryManager;
import com.cerner.devcenter.education.managers.UserSubscriptionManager;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.UserSubscription;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthenticationStatusUtil;
import com.cerner.devcenter.education.utils.Constants;

/**
 * Renders the Subscription page view.
 *
 * @author Mani Teja Kurapati (MK051340)
 */
@Controller
@RequestMapping("/app")
public class UserSubscriptionController {

    private static final String SUBSCRIPTION_VIEWNAME = "user_subscription";
    private static final String USER_SUBSCRIPTION = "userSubscription";
    private static final String USER_DETAILS = "userDetails";
    private static final String SUBSCRIBED_CATEGORIES = "subscribedCategories";
    private static final String USER = "user";
    private static final String SUCCESS = "successMessage";
    private static final String ERROR = "errorMessage";

    private static final String ERROR_BINDING_RESULT = "There was a BindingResult error. Please select valid input.";
    private static final String INVALID_HTTP_SESSION = "HttpSession object passed can't be null";
    private static final String INVALID_USER_SUBSCRIPTION = "User subscription object passed can't be null";
    private static final String INVALID_CATEGORY_ID = "Category id cannot be null/empty";
    private static final String INVALID_MODEL_AND_VIEW = "ModelAndView object passed can't be null";
    private static final String BINDING_RESULT_ERROR = "com.cerner.devcenter.education.controllers.bindingResultError";
    private static final String SUBSCRIPTION_SUCCESS = "subscriptionPage.label.categorySuccessfullySubscribed";
    private static final String SUBSCRIPTION_FAILURE = "subscriptionPage.label.categoryNotAdded";
    private static final String CATEGORY_ALREADY_SUBSCRIBED = "subscriptionPage.label.categoryAlreadySubscribed";

    private static final Logger LOGGER = Logger.getLogger(UserSubscriptionController.class);

    private static ResourceBundle i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

    private final AuthenticationStatusUtil loginState = AuthenticationStatusUtil.getInstance();

    @Autowired
    private CategoryManager categoryManager;
    @Autowired
    private UserSubscriptionManager userSubscriptionManager;

    /**
     * Displays {@link UserSubscription} categories with an option to subscribe
     * to new categories
     *
     * @param userSubscription
     *            {@link UserSubscription} that contains user id, category
     *            id(must not be <code>null</code>)
     * @param session
     *            {@link HttpSession} that contains objects related to current
     *            session (must not be <code>null</code>)
     * @return model This will return a ModelAndView object with subscriptions
     *         view
     */
    @RequestMapping(value = "/mySubscriptions", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView mySubscriptions(@ModelAttribute(USER_SUBSCRIPTION) final UserSubscription userSubscription,
            final HttpSession session) {
        checkArgument(userSubscription != null, INVALID_USER_SUBSCRIPTION);
        checkArgument(session != null, INVALID_HTTP_SESSION);
        if (!loginState.isLoggedIn()) {
            return new ModelAndView(Constants.REDIRECT_LOGIN);
        }
        final UserProfileDetails user = (UserProfileDetails) session.getAttribute(USER_DETAILS);
        final ModelAndView model = new ModelAndView(SUBSCRIPTION_VIEWNAME);
        model.addObject(Constants.CATEGORIES, categoryManager.getAllCategories());
        model.addObject(SUBSCRIBED_CATEGORIES, userSubscriptionManager.getSubscribedcategoriesByUser(user.getUserId()));
        model.addObject(USER, user);
        return model;
    }

    /**
     * Handles add subscription operation with passed in user subscription
     *
     * @param userSubscription
     *            The {@link UserSubscription} object that contains user id,
     *            category id (must not be <code>null</code>)
     * @param result
     *            This contains the {@link BindingResult} details to check
     *            whether there are binding errors or not in your controllers.
     * @param session
     *            {@link HttpSession} that contains objects related to current
     *            session (must not be <code>null</code>)
     * @param model
     *            The {@link ModelAndView} that is used to load page and add
     *            objects (must not be <code>null</code>)
     * @return {@link ModelAndView} that loads the subscriptions page
     */

    @RequestMapping(value = "/addSubscription", method = RequestMethod.POST)
    public ModelAndView addSubscription(@ModelAttribute(USER_SUBSCRIPTION) final UserSubscription userSubscription,
            final BindingResult result, final HttpSession session, final ModelAndView model) {
        checkArgument(userSubscription != null, INVALID_USER_SUBSCRIPTION);
        checkArgument(session != null, INVALID_HTTP_SESSION);
        checkArgument(model != null, INVALID_MODEL_AND_VIEW);
        if (!loginState.isLoggedIn()) {
            return new ModelAndView(Constants.REDIRECT_LOGIN);
        }
        model.setViewName(SUBSCRIPTION_VIEWNAME);
        final UserProfileDetails user = (UserProfileDetails) session.getAttribute(USER_DETAILS);
        model.addObject(Constants.CATEGORIES, categoryManager.getAllCategories());
        model.addObject(USER, user);
        if (result.hasErrors()) {
            LOGGER.error(ERROR_BINDING_RESULT);
            model.addObject(ERROR, i18nBundle.getString(BINDING_RESULT_ERROR));
            model.addObject(SUBSCRIBED_CATEGORIES,
                    userSubscriptionManager.getSubscribedcategoriesByUser(user.getUserId()));
            return model;
        }
        if (userSubscription.getCategoryId() > 0) {
            try {
                userSubscription.setUserId(user.getUserId());
                final boolean isAddedToDatabase = userSubscriptionManager.addUserSubscription(userSubscription);
                if (isAddedToDatabase) {
                    model.addObject(SUCCESS, i18nBundle.getString(SUBSCRIPTION_SUCCESS));
                } else {
                    model.addObject(ERROR, i18nBundle.getString(SUBSCRIPTION_FAILURE));
                }
            } catch (final DuplicateUserSubscriptionException e) {
                model.addObject(ERROR, i18nBundle.getString(CATEGORY_ALREADY_SUBSCRIBED));
            }
        } else {
            model.addObject(ERROR, i18nBundle.getString(SUBSCRIPTION_FAILURE));
        }
        model.addObject(SUBSCRIBED_CATEGORIES, userSubscriptionManager.getSubscribedcategoriesByUser(user.getUserId()));
        return model;
    }

    /**
     * Deletes the category from subscribed categories for the passed in
     * categoryId.
     *
     * @param categoryId
     *            categoryId of the {@link Category} that needs to be removed
     *            from subscriptions (must be greater than 0)
     * @param session
     *            {@link HttpSession} that contains objects related to current
     *            session (must not be <code>null</code>)
     * @return true if category gets removed from subscriptions else false
     */
    @RequestMapping(value = "/deleteSubscription", method = RequestMethod.POST)
    public @ResponseBody boolean deleteSubscriptionBasedOnCategoryId(
            @RequestParam("categoryId") final String categoryId, final HttpSession session) {
        checkArgument(StringUtils.isNotBlank(categoryId), INVALID_CATEGORY_ID);
        checkArgument(session != null, INVALID_HTTP_SESSION);
        final int intCategoryId = Integer.valueOf(categoryId);
        checkArgument(intCategoryId > 0, Constants.ID_INVALID_ERROR_MESSAGE);
        final UserSubscription userSubscriptionDelete = new UserSubscription();
        final UserProfileDetails user = (UserProfileDetails) session.getAttribute(USER_DETAILS);
        userSubscriptionDelete.setCategoryId(intCategoryId);
        userSubscriptionDelete.setUserId(user.getUserId());
        return userSubscriptionManager.deleteUserSubscription(userSubscriptionDelete);
    }
}
