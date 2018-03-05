package com.cerner.devcenter.education.controllers;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;
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

import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.managers.CategoryManager;
import com.cerner.devcenter.education.managers.UserManager;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthenticationStatusUtil;
import com.cerner.devcenter.education.utils.Constants;
import com.google.common.base.Preconditions;

/**
 * This is a controller class that handles requests related to adding a category
 * 
 * @author Gunjan Kaphle (GK045931)
 * @author Anudeep Kumar Gadam (AG045334)
 * @author Asim Mohammed (AM045300)
 * @author Anudeep Kumar Gadam (AG045334)
 * @author Jacob Zimmermann (JZ022690)
 * @author Santosh Kumar (SK051343)
 */

@Controller
@RequestMapping("/app")
public class CategoryController {

    private static final String CATEGORY = "category";
    private static final String ALL_CATEGORIES = "categories";
    private static final String MESSAGE = "message";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String SUCCESS_MESSAGE = "successMessage";
    private static final String ADD_CATEGORY = "add_category";
    private static final String REDIRECT_LOGIN = "redirect:/login";
    private static final Logger LOGGER = Logger.getLogger(CategoryController.class);
    private static final String USER_DETAILS = "userDetails";
    private static final String CATEGORY_NULL_ERROR_MESSAGE = "addCategoryPage.userGuideMessage";
    private static final String BINDING_RESULT_ERROR_MESSAGE = "com.cerner.devcenter.education.controllers.bindingResultError";
    private static final String CATEGORY_EXISTS_ERROR_MESSAGE = "addCategoryPage.message.alreadyPresent";
    private static final String CATEGORY_SUCCESS_MESSAGE = "addCategoryPage.successMessage";
    private static final String CATEGORY_ADD_ERROR_MESSAGE = "com.database.query.fails";
    private static final String USER_NOT_LOGGED_IN = "common.user.notLoggedIn";

    private static ResourceBundle i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

    @Autowired
    private CategoryManager categoryManager;
    @Autowired
    private AuthenticationStatusUtil status;
    @Autowired
    private UserManager userManager;

    /**
     * Return the page to add a category if the user is admin, else redirects to
     * a access denied page.
     * 
     * @param category
     *            a {@link Category} object that holds the value name and
     *            description.
     * @param model
     *            a {@link ModelAndView} object that is injected to the method.
     * @param session
     *            a {@link HttpSession} containing the session details.
     * @return {@link ModelAndView} object that redirects to one of the below
     *         pages:
     *         <ul>
     *         <li>add/remove category page if user is admin</li>
     *         <li>restricted error message page if user is not admin</li>
     *         <li>general error page if the user session no longer exists</li>
     *         </ul>
     * @throws IllegalArgumentException
     *             when any of the below is true:
     *             <ul>
     *             <li>category argument is null</li>
     *             <li>model argument is null</li>
     *             <li>session argument is null</li>
     *             </ul>
     * 
     */
    @RequestMapping(value = "/show_add_category", method = RequestMethod.GET)
    public ModelAndView showAddCategoryPage(
            @ModelAttribute(CATEGORY) final Category category,
            final ModelAndView model,
            final HttpSession session) {
        checkArgument(session != null, Constants.SESSION_NULL_ERROR_MESSAGE);
        checkArgument(category != null, Constants.CATEGORY_OBJECT_NULL_ERROR_MESSAGE);
        checkArgument(model != null, Constants.MODEL_OBJECT_NULL_ERROR_MESSAGE);
        if (!status.isLoggedIn()) {
            return redirectsNotLoggedIn(model);
        }
        final UserProfileDetails user = (UserProfileDetails) session.getAttribute(USER_DETAILS);
        if (!userManager.isAdminUser(user.getUserId())) {
            return status.redirectsAccessDenied();
        }
        model.setViewName(ADD_CATEGORY);
        model.addObject(ALL_CATEGORIES, categoryManager.getAllCategories());
        model.addObject(MESSAGE, i18nBundle.getString(CATEGORY_NULL_ERROR_MESSAGE));

        return model;
    }

    /**
     * Handles the add operation with the passed parameters. Skips adding
     * category if it is already present.
     * 
     * @param category
     *            a {@link Category} object that contains the category name and
     *            description. Must not be null.
     * @param model
     *            a {@link ModelAndView} object that is injected to the method.
     * @param result
     *            {@link BindingResult} details if there are any binding errors.
     * @return the {@link ModelAndView} that loads add_category page with the
     *         new category entered by the user.
     * @throws IllegalArgumentException
     *             when one of the following is true:
     *             <ul>
     *             <li>category parameter is null</li>
     *             <li>model parameter is null</li>
     *             <li>result parameter is null</li>
     *             </ul>
     */
    @RequestMapping(value = "/add_category_page", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView addCategory(
            @ModelAttribute(CATEGORY) final Category category,
            final ModelAndView model,
            final BindingResult result) {
        Preconditions.checkArgument(category != null, Constants.CATEGORY_OBJECT_NULL_ERROR_MESSAGE);
        Preconditions.checkArgument(model != null, Constants.MODEL_OBJECT_NULL_ERROR_MESSAGE);
        Preconditions.checkArgument(result != null, Constants.RESULT_OBJECT_NULL_ERROR_MESSAGE);
        if (!status.isLoggedIn()) {
            return redirectsNotLoggedIn(model);
        }
        model.setViewName(ADD_CATEGORY);
        if (result.hasErrors()) {
            LOGGER.error(Constants.BINDING_RESULT_ERROR);
            model.addObject(MESSAGE, i18nBundle.getString(CATEGORY_NULL_ERROR_MESSAGE));
            model.addObject(ERROR_MESSAGE, i18nBundle.getString(BINDING_RESULT_ERROR_MESSAGE));
            return model;
        }
        final String categoryName = category.getName();
        try {
            if (categoryManager.isCategoryAlreadyPresent(categoryName)) {
                model.addObject(SUCCESS_MESSAGE, i18nBundle.getString(CATEGORY_EXISTS_ERROR_MESSAGE));
            } else {
                categoryManager.addCategory(category);
                model.addObject(SUCCESS_MESSAGE, i18nBundle.getString(CATEGORY_SUCCESS_MESSAGE));
            }
            model.addObject(ALL_CATEGORIES, categoryManager.getAllCategories());
        } catch (final ManagerException managerException) {
            LOGGER.error(Constants.QUERY_FAIL_ERROR, managerException);
            model.addObject(ERROR_MESSAGE, i18nBundle.getString(CATEGORY_ADD_ERROR_MESSAGE));
        }
        model.addObject(MESSAGE, i18nBundle.getString(CATEGORY_NULL_ERROR_MESSAGE));
        return model;
    }

    /**
     * Redirects to login page when admin is not logged in.
     * 
     * @param model
     *            a {@link ModelAndView} object that is injected to the method.
     * @return Returns the {@link ModelAndView} that loads the login page.
     */
    private ModelAndView redirectsNotLoggedIn(final ModelAndView model) {
        model.setViewName(REDIRECT_LOGIN);
        LOGGER.error(Constants.USER_NOT_LOGGED_IN);
        model.addObject(MESSAGE, i18nBundle.getString(USER_NOT_LOGGED_IN));
        return model;
    }

    /**
     * Deletes the category from database for the passed in categoryId.
     * 
     * @param categoryId
     *            categoryId of the {@link Category} that needs to be deleted
     *            (must be greater than 0)
     * @return true if category gets deleted else false
     */
    @RequestMapping(value = "/delete_category", method = RequestMethod.GET)
    public @ResponseBody boolean deleteCategoryBasedOnCategoryId(@RequestParam("categoryId") final int categoryId) {
        checkArgument(categoryId > 0, Constants.ID_INVALID_ERROR_MESSAGE);
        try {
            categoryManager.deleteCategoryById(categoryId);
        } catch (final ManagerException managerException) {
            LOGGER.error(Constants.CATEGORY_DELETE_ERROR_MESSAGE, new ManagerException());
            return false;
        }
        return true;
    }

    /**
     * Handles the request for category search. Gets list of category names that
     * contains the search string and sorts them in alphabetical order. Returns
     * 10 or less results per request.
     * 
     * @param search
     *            string user entered to perform search (cannot be null, empty
     *            or blank).
     * @param session
     *            a {@link HttpSession} object that stores the current session.
     *            Cannot be <code>null</code>.
     * @return {@link List} of category names (cannot be null).
     * @throws IllegalArgumentException
     *             when invalid search keyword is entered.
     */
    @RequestMapping(value = "/category_autocomplete", method = RequestMethod.GET)
    public @ResponseBody List<Category> categoryAutocomplete(
            @RequestParam("search") final String search,
            final HttpSession session) {
        UserProfileDetails user = (UserProfileDetails) session.getAttribute(USER_DETAILS);
        final String userId = user.getUserId();
        checkArgument(StringUtils.isNotBlank(search), Constants.SEARCH_INVALID_ERROR_MESSAGE);
        List<Category> categories = new ArrayList<>();
        final List<Category> unAddedCategoryList = categoryManager.nonchosenCategories(search.toLowerCase(), userId);
        final List<Category> matchedCategoryList = categoryManager.chosenCategories(search.toLowerCase(), userId);
        categories.addAll(unAddedCategoryList);
        categories.addAll(matchedCategoryList);
        return categories.subList(0, Math.min(categories.size(), Constants.AUTOFILL_SIZE));
    }
}
