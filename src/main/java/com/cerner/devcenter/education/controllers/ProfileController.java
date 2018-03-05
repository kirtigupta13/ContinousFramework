package com.cerner.devcenter.education.controllers;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.cerner.devcenter.education.exceptions.DuplicateUserInterestedCategoryException;
import com.cerner.devcenter.education.managers.CategoryManager;
import com.cerner.devcenter.education.managers.UserInterestedCategoryManager;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.UserInterestedCategory;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthenticationStatusUtil;
import com.cerner.devcenter.education.utils.Constants;

/**
 * Renders the Login page view.
 *
 * @author Anudeep Kumar Gadam (AG045334)
 * @author Jacob Zimmermann (JZ022690)
 * @author Vincent Dasari (VD049645)
 * @author Santosh Kumar (SK051343)
 */
@Controller
@RequestMapping("/app")
public class ProfileController {

    private static final String PROFILE_FORWARD = "forward:myProfile";
    private static final String USER_DETAILS = "userDetails";
    private static final String PROFILE_VIEWNAME = "profile";
    private static final String USER = "user";
    private static final String USER_INTERESTED_CATEGORIES = "userInterestedCategory";
    private static final String SUCCESS = "successMessage";
    private static final String ERROR = "errorMessage";
    private static final String SKILL_LEVEL = "skillLevel";
    private static final String INTEREST_LEVEL = "interestLevel";
    private static final String CATEGORY_ID = "categoryId";

    private static final String ERROR_BINDING_RESULT = "There was a BindingResult error. Please select valid input.";
    private static final String INVALID_HTTP_SESSION = "HttpSession object passed can't be null";
    private static final String INVALID_USER_INTERESTED_CATEGORY = "User interested category object passed can't be null";
    private static final String INVALID_MODEL_AND_VIEW = "ModelAndView object passed can't be null";
    private static final String INVALID_HTTP_SERVLET_REQUEST = "HttpServletRequest object passed can't be null";

    private static final String BINDING_RESULT_ERROR = "com.cerner.devcenter.education.controllers.bindingResultError";
    private static final String CATEGORY_SUCCESS = "profilePage.label.categorySuccessfullyAdded";
    private static final String CATEGORY_NOT_ADDED = "profilePage.label.categoryNotAdded";
    private static final String CATEGORY_ALREADY_EXISTS = "profilePage.label.categoryAlreadyExists";
    private static final String BINDING_RESULT_ERROR_ON_DELETE = "com.cerner.devcenter.education.controllers.bindingResultErrorOnDelete";
    private static final String CATEGORY_DELETE = "profilePage.label.userInterestedCategoryDeleted";
    private static final String CATEGORY_NOT_DELETED = "profilePage.label.userInterestedCategoryNotDeleted";
    private static final String INTERESTED_CATEGORY_SUCCESS = "profilePage.label.successUpdateUserInterestedCategory";
    private static final String INTERESTED_CATEGORY_ERROR = "profilePage.label.errorUpdateUserInterestedCategory";
    private static final String INTERESTED_CATEGORY_SELECTION = "profile.userInterestedCategory.selection";
    private static final String INTERESTED_CATEGORY_DELETED = "profilePage.label.userInterestedCategoryDeleted";
    private static final String INTERESTED_CATEGORY_NOT_DELETED = "profilePage.label.userInterestedCategoryNotDeleted";
    private static final Logger LOGGER = Logger.getLogger(ProfileController.class);

    private static ResourceBundle i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

    private AuthenticationStatusUtil loginState = AuthenticationStatusUtil.getInstance();

    @Autowired
    private CategoryManager categoryManager;
    @Autowired
    private UserInterestedCategoryManager userInterestedCategoryManager;

    /**
     * Displays {@link UserProfileDetails} with an option to add interested
     * topics.
     *
     * @param userInterestedCategory
     *            {@link UserInterestedCategory} that contains user id,
     *            category, skill, and interest level of the user (must not be
     *            <code>null</code>)
     * @param session
     *            {@link HttpSession} that contains objects related to current
     *            session (must not be <code>null</code>)
     * @return {@link ModelAndView}
     *         <ul>
     *         <li>redirect to the login page if not logged in</li>
     *         <li>loads the profile page otherwise</li>
     *         </ul>
     */
    @RequestMapping(value = "/myProfile", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView profile(
            @ModelAttribute(USER_INTERESTED_CATEGORIES) UserInterestedCategory userInterestedCategory,
            HttpSession session) {
        checkArgument(userInterestedCategory != null, INVALID_USER_INTERESTED_CATEGORY);
        checkArgument(session != null, INVALID_HTTP_SESSION);
        if (!loginState.isLoggedIn()) {
            return new ModelAndView(Constants.REDIRECT_LOGIN);
        }
        UserProfileDetails user = (UserProfileDetails) session.getAttribute(USER_DETAILS);
        ModelAndView model = new ModelAndView(PROFILE_VIEWNAME);
        user.setUserInterestedCategories(
                userInterestedCategoryManager.getUserInterestedCategoriesById(user.getUserId()));
        model.addObject(USER, user);
        model.addObject(Constants.CATEGORIES, categoryManager.getAllCategories());
        model.addObject(USER_INTERESTED_CATEGORIES, userInterestedCategory);
        return model;
    }

    /**
     * Handles add interested category operation with passed in user interested
     * category.
     *
     * @param userInterestedCategory
     *            The {@link UserInterestedCategory} object that contains user
     *            id, category, skill level, and interest level (must not be
     *            <code>null</code>)
     * @param result
     *            This contains the {@link BindingResult} details to check
     *            whether there are binding errors or not in your controllers.
     * @param session
     *            {@link HttpSession} that contains objects related to current
     *            session (must not be <code>null</code>)
     * @param model
     *            The {@link ModelAndView} that is used to load page and add
     *            objects (must not be <code>null</code>)
     * @return {@link ModelAndView} that loads the profile page
     */
    @RequestMapping(value = "/addInterestedCategory", method = RequestMethod.POST)
    public ModelAndView addInterestedCategory(
            @ModelAttribute(USER_INTERESTED_CATEGORIES) UserInterestedCategory userInterestedCategory,
            BindingResult result,
            HttpSession session,
            ModelAndView model) {
        checkArgument(userInterestedCategory != null, INVALID_USER_INTERESTED_CATEGORY);
        checkArgument(session != null, INVALID_HTTP_SESSION);
        checkArgument(model != null, INVALID_MODEL_AND_VIEW);
        if (!loginState.isLoggedIn()) {
            return new ModelAndView(Constants.REDIRECT_LOGIN);
        }
        model.setViewName(PROFILE_VIEWNAME);
        UserProfileDetails user = (UserProfileDetails) session.getAttribute(USER_DETAILS);
        model.addObject(Constants.CATEGORIES, categoryManager.getAllCategories());
        if (result.hasErrors()) {
            LOGGER.error(ERROR_BINDING_RESULT);
            model.addObject(ERROR, i18nBundle.getString(BINDING_RESULT_ERROR));
            user.setUserInterestedCategories(
                    userInterestedCategoryManager.getUserInterestedCategoriesById(user.getUserId()));
            model.addObject(USER, user);
            return model;
        }
        if (userInterestedCategory.getCategory() != null) {
            try {
                userInterestedCategory.setUserID(user.getUserId());
                boolean isAddedToDatabase = userInterestedCategoryManager
                        .addUserInterestedCategory(userInterestedCategory);
                if (isAddedToDatabase) {
                    model.addObject(SUCCESS, i18nBundle.getString(CATEGORY_SUCCESS));
                } else {
                    model.addObject(ERROR, i18nBundle.getString(CATEGORY_NOT_ADDED));
                }
            } catch (DuplicateUserInterestedCategoryException e) {
                model.addObject(ERROR, i18nBundle.getString(CATEGORY_ALREADY_EXISTS));
            }
        } else {
            model.addObject(ERROR, i18nBundle.getString(CATEGORY_NOT_ADDED));
        }
        user.setUserInterestedCategories(
                userInterestedCategoryManager.getUserInterestedCategoriesById(user.getUserId()));
        model.addObject(USER, user);
        model.addObject(Constants.CATEGORIES, categoryManager.getAllCategories());
        model.addObject(USER_INTERESTED_CATEGORIES, userInterestedCategory);
        return model;
    }

    /**
     * Handles delete interested category operation with passed in user
     * interested category.
     *
     * @param userInterestedCategory
     *            The {@link UserInterestedCategory} object that contains user
     *            id, category, skill level, and interest level (must not be
     *            <code>null</code>)
     * @param result
     *            contains the {@link BindingResult} details to check whether
     *            there are binding errors or not in your controllers.
     * @param session
     *            {@link HttpSession} that contains objects related to current
     *            session (must not be <code>null</code>)
     * @param model
     *            The {@link ModelAndView} that is used to load page and add
     *            objects (must not be <code>null</code>)
     * @return the {@link ModelAndView} that loads the profile page
     * @throws IllegalArgumentException
     *             if userInterestedCategory, session or model are null
     */
    @RequestMapping(value = "/removeUserIntrestedCategory", method = RequestMethod.POST)
    public ModelAndView deleteUserInterestedCategory(
            @ModelAttribute(USER_INTERESTED_CATEGORIES) UserInterestedCategory userInterestedCategory,
            BindingResult result,
            HttpSession session,
            ModelAndView model) {
        checkArgument(userInterestedCategory != null, INVALID_USER_INTERESTED_CATEGORY);
        checkArgument(session != null, INVALID_HTTP_SESSION);
        checkArgument(model != null, INVALID_MODEL_AND_VIEW);
        if (!loginState.isLoggedIn()) {
            return new ModelAndView(Constants.REDIRECT_LOGIN);
        }
        model.setViewName(PROFILE_VIEWNAME);
        UserProfileDetails user = (UserProfileDetails) session.getAttribute(USER_DETAILS);
        model.addObject(Constants.CATEGORIES, categoryManager.getAllCategories());
        if (result.hasErrors()) {
            LOGGER.error(ERROR_BINDING_RESULT);
            model.addObject(ERROR, i18nBundle.getString(BINDING_RESULT_ERROR_ON_DELETE));
            user.setUserInterestedCategories(
                    userInterestedCategoryManager.getUserInterestedCategoriesById(user.getUserId()));
            model.addObject(USER, user);
            return model;
        }
        boolean isUserInterestedCategoryDeleted = userInterestedCategoryManager
                .deleteUserInterestedCategory(userInterestedCategory);
        if (isUserInterestedCategoryDeleted) {
            model.addObject(SUCCESS, i18nBundle.getString(CATEGORY_DELETE));
        } else {
            model.addObject(ERROR, i18nBundle.getString(CATEGORY_NOT_DELETED));
        }
        user.setUserInterestedCategories(
                userInterestedCategoryManager.getUserInterestedCategoriesById(user.getUserId()));
        model.addObject(USER, user);
        return model;
    }

    /**
     * Handles update to skill level and interest level for a particular
     * category for the user.
     *
     * @param session
     *            {@link HttpSession} that contains objects related to current
     *            session (must not be <code>null</code>)
     * @param request
     *            {@link HttpServletRequest} that contains the required values
     *            to make updates to the user interested topics (must not be
     *            <code>null</code>
     * @param model
     *            The {@link ModelAndView} that is used to load page and add
     *            objects (must not be <code>null</code>)
     * @return {@link ModelAndView} that forwards to the profile page with edit
     *         success/error messages
     */
    @RequestMapping(value = "/updateUserInterestedCategories", method = RequestMethod.POST)
    public ModelAndView updateUserInterestedCategory(
            HttpSession session,
            HttpServletRequest request,
            ModelAndView model) {
        checkArgument(session != null, INVALID_HTTP_SESSION);
        checkArgument(request != null, INVALID_HTTP_SERVLET_REQUEST);
        checkArgument(model != null, INVALID_MODEL_AND_VIEW);
        int skillLevel = Integer.valueOf(request.getParameter(SKILL_LEVEL));
        int interestLevel = Integer.valueOf(request.getParameter(INTEREST_LEVEL));
        int categoryId = Integer.valueOf(request.getParameter(CATEGORY_ID));
        UserProfileDetails user = (UserProfileDetails) session.getAttribute(USER_DETAILS);
        UserInterestedCategory userInterestedCategory = new UserInterestedCategory(
                user.getUserId(),
                new Category(categoryId),
                skillLevel,
                interestLevel);
        model.setViewName(PROFILE_FORWARD);
        if (userInterestedCategoryManager.updateUserInterestedCategory(userInterestedCategory)) {
            model.addObject(SUCCESS, i18nBundle.getString(INTERESTED_CATEGORY_SUCCESS));
        } else {
            model.addObject(ERROR, i18nBundle.getString(INTERESTED_CATEGORY_ERROR));
        }
        return model;
    }

    /**
     * Handles a batch delete on {@link UserInterestedCategory} with passed in
     * batch of category ID .
     *
     * @param session
     *            {@link HttpSession} that contains objects related to current
     *            session (can't be <code>null</code>)
     * @param model
     *            The {@link ModelAndView} that is used to load page and add
     *            objects (can't be <code>null</code>)
     * @param categoryIds
     *            Batch of category IDs to be deleted.
     * @return {@link ModelAndView} that forwards to the profile page with
     *         success/error messages.
     * @throws IllegalArgumentException
     *             <ul>
     *             <li>when <b>session</b> is <code>null</code>.</li>
     *             <li>when <b>model</b> is <code>null</code>.</li>
     *             <li>when <b>categoryIds</b> is empty or
     *             <code>null</code>.</li>
     *             </ul>
     */
    @RequestMapping(value = "/removeUserInterestedCategoriesInBatch", method = RequestMethod.POST)
    public ModelAndView batchDeleteUserInterestedCategories(
            HttpSession session,
            @RequestParam(value = "categoryIds", required = false) int[] categoryIds,
            ModelAndView model) {
        checkArgument(session != null, INVALID_HTTP_SESSION);
        checkArgument(model != null, INVALID_MODEL_AND_VIEW);
        if (!loginState.isLoggedIn()) {
            return new ModelAndView(Constants.REDIRECT_LOGIN);
        }
        model.setViewName(PROFILE_FORWARD);
        UserProfileDetails user = (UserProfileDetails) session.getAttribute(USER_DETAILS);
        if (ArrayUtils.isEmpty(categoryIds)) {
            model.addObject(ERROR, i18nBundle.getString(INTERESTED_CATEGORY_SELECTION));
            return model;
        }
        if (userInterestedCategoryManager.deleteUserInterestedCategoriesInBatch(user.getUserId(), categoryIds)) {
            model.addObject(SUCCESS, i18nBundle.getString(INTERESTED_CATEGORY_DELETED));
        } else {
            model.addObject(ERROR, i18nBundle.getString(INTERESTED_CATEGORY_NOT_DELETED));
        }
        return model;
    }
}
