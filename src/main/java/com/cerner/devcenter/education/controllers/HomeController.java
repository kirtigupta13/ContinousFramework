package com.cerner.devcenter.education.controllers;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.util.CollectionUtils;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.authentication.LdapReader;
import com.cerner.devcenter.education.exceptions.DuplicateUserInterestedCategoryException;
import com.cerner.devcenter.education.managers.CategoryManager;
import com.cerner.devcenter.education.managers.CompletedUserResourceManager;
import com.cerner.devcenter.education.managers.LearnerManager;
import com.cerner.devcenter.education.managers.ResourceManager;
import com.cerner.devcenter.education.managers.UserInterestedCategoryManager;
import com.cerner.devcenter.education.managers.UserManager;
import com.cerner.devcenter.education.managers.UserRecommendedResourceManager;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.CompletedResource;
import com.cerner.devcenter.education.models.Learner;
import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.models.ResourceCategoryRelation;
import com.cerner.devcenter.education.models.UserInterestedCategory;
import com.cerner.devcenter.education.models.UserRecommendedResource;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthenticationStatusUtil;
import com.cerner.devcenter.education.utils.CompletionRating;
import com.cerner.devcenter.education.utils.Constants;

/**
 * This controller is for rendering the Home page view.
 *
 * @author James Kellerman (JK042311)
 * @author Surbhi Singh (SS043472)
 * @author Nithya Roopa Banda (NB044557)
 * @author Manoj Raj Devalla (MD042936)
 * @author Amos Bailey (AB032627)
 * @author Jacob Zimmerman (JZ022690)
 * @author Rishabh Bhojak (RB048032)
 * @author Mayur Rajendran (MT049536)
 * @author Vincent Dasari (VD049645)
 * @author Santosh Kumar (SK051343)
 */
@Controller
@RequestMapping("/")
public class HomeController {

    private static final String ADMIN_CONSOLE = "admin_console";
    private static final String BASE = "OU=Office Locations,DC=northamerica,DC=cerner,DC=net";
    private static final String HOME_PAGE = "home_page";
    private static final String HOME_REDIRECT = "redirect:/app/home_page";
    private static final String LOGIN_REDIRECT = "redirect:/login";
    private static final String LOGIN_FORCE_REDIRECT = "redirect:/login_force";

    private static final String ROLE = "role";
    private static final String USER_DETAILS = "userDetails";
    private static final String RECOMMENDED_RESOURCES = "recommendedResources";
    private static final String MESSAGE = "message";
    private static final String CATEGORY_ADDED_MESSAGE = "categoryAddedMessage";
    private static final String COMPLETED_RESOURCES = "completedResources";
    private static final String ALL_CATEGORIES = "allCategories";
    private static final String USER_INTERESTED_CATEGORIES = "userInterestedCategories";
    private static final String CATEGORY_ERROR = "categoryError";
    private static final String NO_RESOURCE = "noResource";
    private static final String NO_CATEGORY = "noCategory";
    private static final String RESOURCE_LIST = "resourceList";
    private static final String NON_CHOSEN_CATEGORY_LIST = "nonchosenCategoryList";
    private static final String CHOSEN_CATEGORY_LIST = "chosenCategoryList";
    private static final String SEARCH = "search";
    private static final String UNABLE_TO_RETRIEVE_RESOURCE = "noRetrievedResources";
    private static final String UNABLE_TO_RETRIEVE_CATEGORY = "noRetrievedCategory";
    private static final String COMPLETED_RESOURCE_WIDGET_MESSAGE = "completedResourceWidgetMessage";
    private static final String NUMBER_OF_COMPLETED_RESOURCES_REQUIRED = "numberOfCompletedResourcesRequired";
    private static final String NUMBER_OF_COMPLETED_RESOURCES = "numberOfCompletedResources";
    private static final String WELCOME_WIDGET_TITLE = "welcomeWidgetTitle";
    private static final String TOP_CATEGORY = "topCategory";
    private static final String NONE = "None";

    private static final String RESOURCE_CATEGORY_RELATION = "resourceCategoryRelation";

    private static final String MODEL_NULL = "Model object cannot be null.";
    private static final String NULL_SESSION = "Session cannot be null";
    private static final String CATEGORY_INVALID = "Category Id must be greater than 0";
    private static final String INVALID_USER_ID_ERROR_MESSAGE = "UserID cannot be null, empty or whitespace.";

    private static final String ERROR_RETRIEVING_CATEGORIES_ERROR_MESSAGE = "Error retrieving all categories from the database with the following exception: %s";
    private static final String ERROR_RETRIEVING_RECOMMENDED_RESOURCES_ERROR_MESSAGE = "Error retrieving Recommended Resources for user.";
    private static final String ERROR_RETRIEVING_COMPLETED_RESOURCES_ERROR_MESSAGE = "Error retrieving Completed Resources for user";
    private static final String RETURNED_RECOMMENDED_RESOURCES_NULL_ERROR_MESSAGE = "The returned recommended resources for user: %s was null";
    private static final String RETURNED_CATEGORY_LIST_NULL_ERROR_MESSAGE = "The returned list of categories was null";
    private static final String ERROR_FILTERING_RESOURCES = "Error filtering recommended resources based on the selected category";
    private static final String ERROR_REQUESTING = "Error encountered while request";

    private static final String RECOMMENDED_RESOURCES_RETRIEVAL_FAILURE = "homepage.recommendedResources.retrieval.failure";
    private static final String NO_RECOMMENDED_RESOURCES = "com.cerner.devcenter.education.controllers.noRecommendedResource";
    private static final String NO_COMPLETED_RESOURCES = "homepage.completedResources.widget.message.noCompletedResources";
    private static final String COMPLETED_RESOURCES_RETRIEVAL_FAILURE = "homepage.completedResources.widget.message.errorCompletedResources";
    private static final String CATEGORY_RETRIEVAL_FAILURE = "com.cerner.devcenter.education.controllers.errorRetrievingCategories";
    private static final String CATEGORY_LIST_RETURNED_EMPTY = "com.cerner.devcenter.education.controllers.noCategoriesReturned";
    private static final String GENERAL_DETAILS_WIDGET_TITLE = "homepage.generalDetails.widget.title";
    private static final String CATEGORY_ALREADY_ADDED = "common.search.categoryAlreadyAdded";
    private static final String GET_REQUEST_ERROR = "resources.getAllRequests.error";
    private static final String NO_RESOURCE_FOUND = "No resources found";
    private static final String NO_CATEGORY_FOUND = "No categories found";
    private static final String NO_RESULTS = "No results";

    private static final int NUMBER_OF_COMPLETED_RESOURCES_TO_RETURN = 5;
    private static final double MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES = CompletionRating.NEUTRAL
            .getValue();
    private static final int MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED = 0;

    private static final Logger LOGGER = Logger.getLogger(HomeController.class);

    private static final ResourceBundle I18N_BUNDLE = ResourceBundle.getBundle("i18n", Locale.getDefault());

    private AuthenticationStatusUtil status = AuthenticationStatusUtil.getInstance();

    private LdapReader ldapReader;
    private HttpSession session;

    @Autowired
    private UserManager userManager;
    @Autowired
    private CategoryManager categoryManager;
    @Autowired
    private ResourceManager resourceManager;
    @Autowired
    private UserRecommendedResourceManager userRecommendedResourceManager;
    @Autowired
    private CompletedUserResourceManager completedUserResourceManager;
    @Autowired
    private LearnerManager learnerManager;
    @Autowired
    private UserInterestedCategoryManager userInterestedTopicManager;

    /**
     * Redirects all users to home page upon successful login.
     *
     * @param request
     *            a valid {@link HttpServletRequest}
     * @return {@link ModelAndView} of the page that the user is being
     *         redirected to
     * @throws NamingException
     *             when the user is not authenticated
     * @throws DAOException
     *             when there is an error getting the user details from the
     *             database
     */
    @RequestMapping(value = { "/home" }, method = RequestMethod.GET)
    public ModelAndView welcomePage(final HttpServletRequest request) throws NamingException, DAOException {
        if (!status.isLoggedIn()) {
            return new ModelAndView(LOGIN_REDIRECT);
        }
        ldapReader = LdapReader.getInstance();
        try {
            final SearchResult searchResult = ldapReader.getSearchResults(
                    ldapReader.getDirContext(),
                    BASE,
                    SecurityContextHolder.getContext().getAuthentication().getName());
            final UserProfileDetails user = ldapReader.getUserDetails(searchResult.getAttributes());
            session = request.getSession(true);
            session.setAttribute(USER_DETAILS, user);
            final List<Category> userInterestedCategories = new ArrayList<>();
            session.setAttribute(USER_INTERESTED_CATEGORIES, userInterestedCategories);
            if (userManager.isAdminUser(user.getUserId())) {
                session.setAttribute(ROLE, "admin");
            }

            Learner learner = new Learner(user.getUserId(), user.getEmail());
            if (!learnerManager.isPresent(learner)) {
                learnerManager.addLearner(learner);
            }

        } catch (final NamingException e) {
            return new ModelAndView(LOGIN_FORCE_REDIRECT);
        }
        return new ModelAndView(HOME_REDIRECT);
    }

    /**
     * Displays the common home page, along with recommended resources for the
     * user and a widget displaying the user's most recently completed
     * resources.
     *
     * @param request
     *            a {@link HttpServletRequest} object
     * @param model
     *            a {@link ModelAndView} object. Model object cannot be
     *            <code>null</code>.
     * @param session
     *            a {@link HttpSession} object that stores the current session.
     *            Cannot be <code>null</code>.
     * @return a {@link ModelAndView} whose view is the home page. The model
     *         contains a {@link List} of {@link UserRecommendedResource
     *         recommended resources}, a {@link List} of the {@link Category
     *         categories} and a {@link List} of {@link CompletedResource
     *         completed resources}.
     * @throws NamingException
     *             when the user is not authenticated
     * @throws SQLException
     *             when there is an error with the query
     */
    @RequestMapping(value = "/app/home_page", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView showHomePage(
            final HttpServletRequest request,
            final ModelAndView model,
            final HttpSession session) throws NamingException, SQLException {
        checkArgument(model != null, MODEL_NULL);
        checkArgument(session != null, Constants.SESSION_NULL_ERROR_MESSAGE);
        final List<Category> userInterestedCategories = (List<Category>) session
                .getAttribute(USER_INTERESTED_CATEGORIES);
        userInterestedCategories.clear();
        if (!status.isLoggedIn()) {
            model.setViewName(LOGIN_REDIRECT);
            return model;
        }
        model.setViewName(HOME_PAGE);
        final Map<String, Object> modelMap = new HashMap<>();
        final UserProfileDetails user = (UserProfileDetails) session.getAttribute(USER_DETAILS);
        final String userId = user.getUserId();
        final List<UserRecommendedResource> recommendedResourcesAvailable = new ArrayList<>();
        List<UserRecommendedResource> intermediateRecommendedResourcesList;
        try {
            intermediateRecommendedResourcesList = userRecommendedResourceManager.getRecommendedResourcesByUserId(
                    userId,
                    MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                    MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED);
            if (intermediateRecommendedResourcesList == null) {
                LOGGER.error(String.format(RETURNED_RECOMMENDED_RESOURCES_NULL_ERROR_MESSAGE, userId));
                intermediateRecommendedResourcesList = Collections.emptyList();
            }
            if (intermediateRecommendedResourcesList.isEmpty()) {
                modelMap.put(MESSAGE, I18N_BUNDLE.getString(NO_RECOMMENDED_RESOURCES));
            } else {
                for (final UserRecommendedResource userRecommendedResource : intermediateRecommendedResourcesList) {
                    if (userRecommendedResource != null) {
                        recommendedResourcesAvailable.add(userRecommendedResource);
                    }
                }
            }
        } catch (final ManagerException managerException) {
            LOGGER.error(ERROR_RETRIEVING_RECOMMENDED_RESOURCES_ERROR_MESSAGE, managerException);
            modelMap.put(MESSAGE, I18N_BUNDLE.getString(RECOMMENDED_RESOURCES_RETRIEVAL_FAILURE));
        }
        modelMap.put(RECOMMENDED_RESOURCES, recommendedResourcesAvailable);
        List<CompletedResource> completedResources = Collections.emptyList();
        try {
            completedResources = completedUserResourceManager
                    .getMostRecentlyCompletedResources(userId, NUMBER_OF_COMPLETED_RESOURCES_TO_RETURN);
            if (completedResources.isEmpty()) {
                modelMap.put(COMPLETED_RESOURCE_WIDGET_MESSAGE, I18N_BUNDLE.getString(NO_COMPLETED_RESOURCES));
            }
        } catch (final ManagerException managerException) {
            LOGGER.error(ERROR_RETRIEVING_COMPLETED_RESOURCES_ERROR_MESSAGE, managerException);
            modelMap.put(
                    COMPLETED_RESOURCE_WIDGET_MESSAGE,
                    I18N_BUNDLE.getString(COMPLETED_RESOURCES_RETRIEVAL_FAILURE));
        }
        modelMap.put(COMPLETED_RESOURCES, completedResources);
        final List<Category> allCategories = new ArrayList<>();
        List<Category> intermediateCategoryList;
        try {
            intermediateCategoryList = categoryManager.getAllCategories();
            if (intermediateCategoryList == null) {
                LOGGER.error(RETURNED_CATEGORY_LIST_NULL_ERROR_MESSAGE);
                intermediateCategoryList = Collections.emptyList();
            }
            if (intermediateCategoryList.isEmpty()) {
                modelMap.put(CATEGORY_ERROR, I18N_BUNDLE.getString(CATEGORY_LIST_RETURNED_EMPTY));
            } else {
                for (final Category category : intermediateCategoryList) {
                    if (category != null) {
                        allCategories.add(category);
                    }
                }
            }
        } catch (final ManagerException managerException) {
            LOGGER.error(String.format(ERROR_RETRIEVING_CATEGORIES_ERROR_MESSAGE, managerException));
            modelMap.put(CATEGORY_ERROR, I18N_BUNDLE.getString(CATEGORY_RETRIEVAL_FAILURE));
        }
        modelMap.put(ALL_CATEGORIES, allCategories);
        modelMap.put(NUMBER_OF_COMPLETED_RESOURCES_REQUIRED, NUMBER_OF_COMPLETED_RESOURCES_TO_RETURN);
        modelMap.put(WELCOME_WIDGET_TITLE, MessageFormat.format(
                I18N_BUNDLE.getString(GENERAL_DETAILS_WIDGET_TITLE),
                ((UserProfileDetails) session.getAttribute(USER_DETAILS)).getFirstName()));
        model.addAllObjects(getCompletedResourcesInfoForUser(userId));
        return model.addAllObjects(modelMap);
    }

    /**
     * Displays the administrator console.
     *
     * @return ModelAndView of the administrator console page.
     */
    @RequestMapping(value = "/app/admin_console", method = RequestMethod.GET)
    public ModelAndView showAdminConsolePage() {
        if (!status.isLoggedIn()) {
            return redirectsNotLoggedIn();
        }
        return new ModelAndView(ADMIN_CONSOLE);
    }

    /**
     * Retrieve searched resources and categories based on the keyword and
     * separating the categories into chosen and non-chosen list
     * 
     * @param keyword
     *            the search query value which is used to get the list of
     *            resources and categories
     *
     * @param keyword
     *            a {@link String} to store the searched query model attribute
     *            {@link ResourceCategoryRelation} category_resource_reltn table
     *            and associated files
     * @param session
     *            a {@link HttpSession} object. Must not be <code>null</code>
     *            and contains current session objects.
     * @return a modelAndView {@link ModelAndView} and redirects to one of the
     *         below pages
     *         <ul>
     *         <li>gets the list of resources and categories if successful</li>
     *         <li>redirects to login page if not logged in</li>
     *         </ul>
     * @throws NullPointerException
     *             when httpSession is <code>null</code>
     */
    @RequestMapping(value = "/app/searchResources", method = RequestMethod.POST)
    public ModelAndView searchResource(
            @RequestParam("searchAutoComplete") final String keyword,
            @ModelAttribute(RESOURCE_CATEGORY_RELATION) final ResourceCategoryRelation resourceCategoryRelation,
            final HttpSession session) {
        checkNotNull(session, NULL_SESSION);
        if (!status.isLoggedIn()) {
            return redirectsNotLoggedIn();
        }
        UserProfileDetails user = (UserProfileDetails) session.getAttribute(USER_DETAILS);
        final String userId = user.getUserId();
        final ModelAndView modelAndView = new ModelAndView(SEARCH);
        if (StringUtils.isBlank(keyword)) {
            modelAndView.addObject(NO_RESOURCE, NO_RESULTS);
            return modelAndView;
        }
        try {
            final List<Resource> resources = resourceManager.getSearchedResources(keyword);
            if (resources == null || resources.isEmpty()) {
                modelAndView.addObject(NO_RESOURCE, NO_RESOURCE_FOUND);
            }
            modelAndView.addObject(RESOURCE_LIST, resources);
        } catch (final ManagerException managerException) {
            LOGGER.error(ERROR_REQUESTING, managerException);
            modelAndView.addObject(UNABLE_TO_RETRIEVE_RESOURCE, I18N_BUNDLE.getString(GET_REQUEST_ERROR));
            return modelAndView;
        }
        try {
            final List<Category> nonchosenCategory = categoryManager.nonchosenCategories(keyword, userId);
            final List<Category> chosenCategory = categoryManager.chosenCategories(keyword, userId);
            if (CollectionUtils.isEmpty(nonchosenCategory) && CollectionUtils.isEmpty(chosenCategory)) {
                modelAndView.addObject(NO_CATEGORY, NO_CATEGORY_FOUND);
            }
            modelAndView.addObject(NON_CHOSEN_CATEGORY_LIST, nonchosenCategory);
            modelAndView.addObject(CHOSEN_CATEGORY_LIST, chosenCategory);
            modelAndView.addObject(CATEGORY_ADDED_MESSAGE, I18N_BUNDLE.getString(CATEGORY_ALREADY_ADDED));
        } catch (final ManagerException managerException) {
            LOGGER.error(ERROR_REQUESTING, managerException);
            modelAndView.addObject(UNABLE_TO_RETRIEVE_CATEGORY, I18N_BUNDLE.getString(GET_REQUEST_ERROR));
        }
        return modelAndView;
    }

    /**
     * Handles add interested category operation with selected user interested
     * category
     *
     * @param category_id
     *            the category Id of the {@link Category} displayed in the
     *            pop-up window
     *
     * @param skill_level
     *            the skill level of the user for that particular
     *            {@link Category} displayed in the pop-up window
     *
     * @param interest_level
     *            the interest level of the user for that particular
     *            {@link Category} displayed in the pop-up window
     *
     * @param session
     *            {@link HttpSession} that contains objects related to current
     *            session (must not be <code>null</code>)
     *
     * @throws DuplicateUserInterestedTopicException
     *             when the category for the user has already been added to the
     *             database
     *
     * @return Boolean that if True the AJAX call is success and the response
     *         function is executed
     */
    @RequestMapping(value = "/app/addInterestedCategories", method = RequestMethod.GET)
    public @ResponseBody boolean addCategory(
            @RequestParam("categoryId") final String category_id,
            @RequestParam("skillLevel") final String skill_level,
            @RequestParam("interestLevel") final String interest_level,
            final HttpSession session) throws DuplicateUserInterestedCategoryException {
        checkArgument(session != null, NULL_SESSION);
        checkArgument(category_id != null, "Category Id cannot be null");
        checkArgument(skill_level != null, "Skill Level cannot be null");
        checkArgument(interest_level != null, "Interest Level cannto be null");
        boolean isAddedToDatabase = false;
        final int skillLevel = Integer.valueOf(skill_level);
        final int interestLevel = Integer.valueOf(interest_level);
        final int categoryId = Integer.valueOf(category_id);
        final UserProfileDetails user = (UserProfileDetails) session.getAttribute(USER_DETAILS);
        final UserInterestedCategory userInterestedTopic = new UserInterestedCategory(
                user.getUserId(),
                new Category(categoryId),
                skillLevel,
                interestLevel);
        isAddedToDatabase = userInterestedTopicManager.addUserInterestedCategory(userInterestedTopic);
        return isAddedToDatabase;
    }

    /**
     * Redirects to the login page when a user/administrator is not logged in.
     *
     * @return ModelAndView that redirects to login page
     */
    private ModelAndView redirectsNotLoggedIn() {
        final ModelAndView model = new ModelAndView(LOGIN_REDIRECT);
        LOGGER.error("Admin not logged in");
        model.addObject("message", "Admin is not logged in");
        return model;
    }

    /**
     * Gathers the list of recommended resources for the category that the user
     * has chosen.
     *
     * @param categoryId
     *            must be greater than 0
     * @return A {@link List} of {@link UserRecommendedResource
     *         UserRecommendedResources}, which is the recommended list of
     *         resources for the category{@link Category} that the user has
     *         chosen to filter
     * @throws SQLException
     *             when there is an error on accessing the database or when
     *             there is an error in the query
     */
    @RequestMapping(value = "app/filterRecommendedResources", method = { RequestMethod.GET })
    public @ResponseBody List<UserRecommendedResource> filterRecommendedResources(
            @RequestParam("id") final int categoryId) throws SQLException {
        checkArgument(categoryId > 0, CATEGORY_INVALID);
        final List<Category> userInterestedCategories = (List<Category>) session
                .getAttribute(USER_INTERESTED_CATEGORIES);
        List<UserRecommendedResource> recommendedResourcesAvailable = new ArrayList<>();
        try {
            final Category category = categoryManager.getCategoryById(categoryId);
            if (userInterestedCategories.contains(category)) {
                userInterestedCategories.remove(category);
            } else {
                userInterestedCategories.add(category);
            }
            final UserProfileDetails user = (UserProfileDetails) session.getAttribute(USER_DETAILS);
            final String userId = user.getUserId();
            if (userInterestedCategories.isEmpty()) {
                recommendedResourcesAvailable = userRecommendedResourceManager.getRecommendedResourcesByUserId(
                        userId,
                        MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                        MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED);
            } else {
                recommendedResourcesAvailable = userRecommendedResourceManager
                        .getRecommendedResourcesByUserIdAndCategories(
                                userId,
                                userInterestedCategories,
                                MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                                MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED);
            }
        } catch (final ManagerException managerException) {
            LOGGER.error(ERROR_FILTERING_RESOURCES, managerException);
        }
        return recommendedResourcesAvailable;
    }

    /**
     * Returns the number of resources completed by the user, as well as the
     * name of the {@link Category} that the user has completed most resources
     * from. If the user has no completed resources, returns 0 for the number,
     * and "None" for the name.
     *
     * @param userId
     *            a {@link String} representing the unique ID of the user.
     *            Cannot be <code>null</code>, empty or whitespace.
     * @return a {@link Map} containing:
     *         <ul>
     *         <li>{@link #NUMBER_OF_COMPLETED_RESOURCES} as key and the no. of
     *         resources completed by the user as value</li>
     *         <li>{@link #TOP_CATEGORY} as key and the name of the
     *         {@link Category} that the user has completed most resources from
     *         as value</li>
     *         </ul>
     */
    private Map<String, Object> getCompletedResourcesInfoForUser(final String userId) {
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID_ERROR_MESSAGE);
        final Map<String, Object> objectMap = new HashMap<>();
        try {
            int numberOfCompletedResources = completedUserResourceManager.getCountOfResourcesCompletedByUser(userId);
            objectMap.put(NUMBER_OF_COMPLETED_RESOURCES, numberOfCompletedResources);
            if (numberOfCompletedResources == 0) {
                objectMap.put(TOP_CATEGORY, NONE);
            } else {
                String topCategory = completedUserResourceManager
                        .getCategoryNameWithMostNumberOfResourcesCompletedByUser(userId);
                if (StringUtils.isBlank(topCategory)) {
                    topCategory = NONE;
                }
                objectMap.put(TOP_CATEGORY, topCategory);
            }
        } catch (final ManagerException managerException) {
            LOGGER.error(managerException.getMessage(), managerException);
            objectMap.put(NUMBER_OF_COMPLETED_RESOURCES, 0);
            objectMap.put(TOP_CATEGORY, NONE);
        }
        return objectMap;
    }
}