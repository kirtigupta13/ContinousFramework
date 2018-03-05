package com.cerner.devcenter.education.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.authentication.LdapReader;
import com.cerner.devcenter.education.exceptions.DuplicateUserInterestedCategoryException;
import com.cerner.devcenter.education.managers.CategoryManager;
import com.cerner.devcenter.education.managers.CompletedUserResourceManager;
import com.cerner.devcenter.education.managers.ResourceManager;
import com.cerner.devcenter.education.managers.UserInterestedCategoryManager;
import com.cerner.devcenter.education.managers.UserRecommendedResourceManager;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.CompletedResource;
import com.cerner.devcenter.education.models.CompletedUserResource;
import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.models.UserInterestedCategory;
import com.cerner.devcenter.education.models.ResourceCategoryRelation;
import com.cerner.devcenter.education.models.UserRecommendedResource;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthenticationStatusUtil;
import com.cerner.devcenter.education.utils.CompletionRating;
import com.cerner.devcenter.education.utils.Constants;

/**
 * Unit test class for: {@link HomeController}
 *
 * @author James Kellerman (JK042311)
 * @author Amos Bailey (AB032627)
 * @author Rishabh Bhojak (RB048032)
 * @author Mayur Rajendran (MT049536)
 * @author Vincent Dasari (VD049645)
 * @author Santosh Kumar (SK051343)
 */
@RunWith(MockitoJUnitRunner.class)
public class HomeControllerTest {

    private static final int VALID_CATEGORY_ID = 4;
    private static final String VALID_CATEGORY_DESC = "Fundamentals of Java.";
    private static final String VALID_CATEGORY_NAME = "Java";
    private static final int VALID_DIFFICULTY_LEVEL = 1;
    private static final int VALID_INTEREST_LEVEL = 5;
    private static final String VALID_URL = "http://www.junit.org";
    private static final String VALID_USER_ID = "TestId";
    private static final String VALID_RESOURCE_DESC = "Learn loops in java.";
    private static final String VALID_RESOURCE_NAME = "Loop in Java";
    private static final String VALID_DESC = "java";
    private static final String VALID_KEYWORD = "keyword";
    private static final String KEYWORD = "zzz";
    private static final String EMPTY_STRING = "";
    private static final String BLANK_STRING = "   ";
    private static final int VALID_RESOURCE_ID = 2;
    private static final int NUMBER_OF_COMPLETED_RESOURCES_TO_RETURN = 5;
    private static final double MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES = 2;
    private static final int MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED = 0;
    private static final int VALID_NUMBER_OF_COMPLETED_RESOURCES = 5;
    private static final String VALID_TOP_CATEGORY = "Object Oriented Programming";
    private static final String NONE = "None";
    private static final String VALID_FIRST_NAME = "John";
    private static final String TEST = "test";

    private static final String HOME_PAGE = "home_page";
    private static final String LOGIN_REDIRECT = "redirect:/login";
    private static final String SEARCH = "search";

    private static final String USER_DETAILS = "userDetails";
    private static final String MESSAGE = "message";
    private static final String RECOMMENDED_RESOURCES = "recommendedResources";
    private static final String ALL_CATEGORIES = "allCategories";
    private static final String COMPLETED_RESOURCES = "completedResources";
    private static final String CATEGORY_ERROR = "categoryError";
    private static final String CR_WIDGET_MESSAGE = "completedResourceWidgetMessage";
    private static final String AUTHENTICATION_TEST = "test";
    private static final String NUMBER_OF_COMPLETED_RESOURCES_REQUIRED = "numberOfCompletedResourcesRequired";
    private static final String NUMBER_OF_COMPLETED_RESOURCES = "numberOfCompletedResources";
    private static final String TOP_CATEGORY = "topCategory";
    private static final String RESOURCE_LIST = "resourceList";
    private static final String NON_CHOSEN_CATEGORY_LIST = "nonchosenCategoryList";
    private static final String CHOSEN_CATEGORY_LIST = "chosenCategoryList";
    private static final String NO_RESOURCE = "noResource";
    private static final String NO_CATEGORY = "noCategory";
    private static final String UNABLE_TO_RETRIEVE_RESOURCE = "noRetrievedResources";
    private static final String UNABLE_TO_RETRIEVE_CATEGORY = "noRetrievedCategory";
    private static final String WELCOME_WIDGET_TITLE = "welcomeWidgetTitle";

    private static final String MODEL_NULL_ERROR_MESSAGE = "Model object cannot be null.";
    private static final String CATEGORY_INVALID_ERROR = "Category Id must be greater than 0";
    private static final String SESSION_NULL = "Session cannot be null";
    private static final String NOT_LOGGED_IN = "Admin is not logged in";
    private static final String NO_RESULTS = "No results";
    private static final String ERROR_FILTERING_RESOURCES = "Error filtering recommended resources based on the selected category";

    private static final String NO_RECOMMENDED_RESOURCES_MESSAGE = "We do not have any recommended resources for you. Please visit your profile and add your interests for results.";
    private static final String RECOMMENDED_RESOURCES_RETRIEVAL_FAILURE = "homepage.recommendedResources.retrieval.failure";
    private static final String NO_RECOMMENDED_RESOURCES = "com.cerner.devcenter.education.controllers.noRecommendedResource";
    private static final String NO_COMPLETED_RESOURCES = "homepage.completedResources.widget.message.noCompletedResources";
    private static final String COMPLETED_RESOURCES_RETRIEVAL_FAILURE = "homepage.completedResources.widget.message.errorCompletedResources";
    private static final String CATEGORY_LIST_RETURNED_EMPTY = "com.cerner.devcenter.education.controllers.noCategoriesReturned";
    private static final String GET_REQUEST_ERROR = "resources.getAllRequests.error";
    private static final String NO_RESOURCE_FOUND = "No resources found";
    private static final String NO_CATEGORY_FOUND = "No categories found";
    private static final String GENERAL_DETAILS_WIDGET_TITLE = "homepage.generalDetails.widget.title";

    private static URL validResourceUrl;
    private static final Resource RESOURCE = new Resource(
            VALID_RESOURCE_ID,
            validResourceUrl,
            VALID_RESOURCE_DESC,
            VALID_RESOURCE_NAME);
    private static final Category CATEGORY = new Category(VALID_CATEGORY_ID, VALID_CATEGORY_NAME, VALID_CATEGORY_DESC);
    private static final UserRecommendedResource USER_RECOMMENDED_RESOURCE = new UserRecommendedResource(
            RESOURCE,
            CATEGORY,
            VALID_DIFFICULTY_LEVEL,
            VALID_INTEREST_LEVEL);
    private static final UserProfileDetails USER = new UserProfileDetails(
            "Name,Name",
            "Role",
            "TestId",
            "Email",
            "DevCenter",
            "Manager",
            "Project");

    private static final List<UserRecommendedResource> LIST_OF_RECOMMENDED_RESOURCES = Collections
            .<UserRecommendedResource>singletonList(USER_RECOMMENDED_RESOURCE);
    private static final List<CompletedResource> LIST_OF_COMPLETED_RESOURCES = Collections
            .<CompletedResource>singletonList(
                    new CompletedResource(
                            VALID_USER_ID,
                            VALID_RESOURCE_ID,
                            VALID_RESOURCE_NAME,
                            validResourceUrl,
                            CompletionRating.SATISFIED,
                            new Date()));

    private static final ModelAndView MODEL = new ModelAndView();
    private static final ResourceBundle I18N_BUNDLE = ResourceBundle.getBundle("i18n", Locale.getDefault());

    private List<Category> listOfCategories = getTestCategoryList(5);
    private List<UserInterestedCategory> listOfUserInterestedTopics;
    private final String userId = "TestId";
    private UserRecommendedResource recommendResourceUser;
    private Resource resource;
    private Category category;
    private List<UserRecommendedResource> listOfRecommendResourceUser;
    private List<Category> userInterestedCategories;
    private UserInterestedCategory userInterestedTopic;
    private UserProfileDetails user;
    private AnonymousAuthenticationToken token;
    private List<GrantedAuthority> grantedAuths;

    @InjectMocks
    private HomeController homeController;
    @Mock
    private LdapReader ldapReader;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    @Mock
    private AuthenticationStatusUtil status;
    @Mock
    private UserRecommendedResourceManager userRecommendedResourceManager;
    @Mock
    private CategoryManager categoryManager;
    @Mock
    private UserInterestedCategoryManager userInterestedTopicManager;
    @Mock
    private UserInterestedCategory userInterestedTopicMock;
    @Mock
    private CompletedUserResourceManager mockCompletedUserResourceManager;
    @Mock
    private ResourceCategoryRelation resourceCategoryRelation;
    @Mock
    private ManagerException managerException;
    @Mock
    private ResourceManager resourceManager;
    @Mock
    private Appender mockAppender;
    @Mock
    private UserProfileDetails mockUserProfileDetails;
    @Captor
    private ArgumentCaptor captorLoggingEvent;

    @BeforeClass
    public static void onlyOnce() throws MalformedURLException {
        validResourceUrl = new URL(VALID_URL);
    }

    /**
     * Initializes the Mocked objects, sets up invalid login credentials, and
     * creates a test user to be used for the {@link HomeControllerTest}.
     *
     * @throws SQLException
     *             when there is an error with the query
     */
    @Before
    public void setup() throws SQLException {
        when(session.getAttribute(USER_DETAILS)).thenReturn(USER);
        LogManager.getRootLogger().addAppender(mockAppender);
        resource = new Resource(VALID_RESOURCE_ID, validResourceUrl, VALID_RESOURCE_DESC, VALID_RESOURCE_NAME);
        recommendResourceUser = new UserRecommendedResource(
                resource,
                CATEGORY,
                VALID_DIFFICULTY_LEVEL,
                VALID_INTEREST_LEVEL);
        listOfRecommendResourceUser = new ArrayList<UserRecommendedResource>();
        listOfRecommendResourceUser.add(recommendResourceUser);
        userInterestedCategories = new ArrayList<>();
        userInterestedCategories.add(CATEGORY);
        user = new UserProfileDetails("Name,Name", "Role", "TestId", "Email", "DevCenter", "Manager", "Project");
        grantedAuths = new ArrayList<GrantedAuthority>();
        grantedAuths.add(new SimpleGrantedAuthority("dummy"));
        listOfCategories = new ArrayList<Category>();
        listOfUserInterestedTopics = new ArrayList<UserInterestedCategory>();
        token = new AnonymousAuthenticationToken("abc", "abc", grantedAuths);
        category = new Category(10, "Security", "Learn the basics of security practices.");
        listOfUserInterestedTopics.add(userInterestedTopic);
        listOfCategories.add(category);
        userInterestedTopic = new UserInterestedCategory(user.getUserId(), category, 4, 3);
        when(session.getAttribute(USER_DETAILS)).thenReturn(user);

    }

    /**
     * Removes the mockAppender added
     */
    @After
    public void tearDown() {
        LogManager.getRootLogger().removeAppender(mockAppender);
    }

    /**
     * Invoke {@link HomeController#welcomePage(HttpServletRequest)} function
     * while not logged in.
     *
     * @throws DAOException
     *             when there is an error getting the user details from the
     *             database
     * @throws NamingException
     *             when the user is not authenticated
     */
    @Test
    public void testWelcomePageWhileNotLoggedIn() throws NamingException, DAOException {
        grantedAuths = new ArrayList<GrantedAuthority>();
        grantedAuths.add(new SimpleGrantedAuthority("dummy"));
        token = new AnonymousAuthenticationToken("abc", "abc", grantedAuths);
        SecurityContextHolder.getContext().setAuthentication(token);
        final ModelAndView testModelView = homeController.welcomePage(request);
        assertEquals(LOGIN_REDIRECT, testModelView.getViewName());
    }

    /**
     * Expects
     * {@link HomeController#showHomePage(HttpServletRequest, ModelAndView, HttpSession)}
     * to throw an {@link IllegalArgumentException} when {@link ModelAndView
     * model} is <code>null</code>.
     *
     * @throws NamingException
     *             when the user is not authenticated
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test(expected = IllegalArgumentException.class)
    public void testShowHomePageWhenModelIsNull() throws NamingException, SQLException {
        try {
            homeController.showHomePage(request, null, session);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(MODEL_NULL_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link HomeController#showHomePage(HttpServletRequest, ModelAndView, HttpSession)}
     * to throw an {@link IllegalArgumentException} when session is
     * <code>null</code>.
     *
     * @throws NamingException
     *             when the user is not authenticated
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test(expected = IllegalArgumentException.class)
    public void testShowHomePageWhenSessionIsNull() throws NamingException, SQLException {
        try {
            homeController.showHomePage(request, MODEL, null);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(Constants.SESSION_NULL_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * This tests
     * {@link HomeController#showHomePage(HttpServletRequest, ModelAndView, HttpSession)}
     * when the user is not logger in. Will be redirected to the login page.
     *
     * @throws NamingException
     * @throws SQLException
     */
    @Test
    public void testHomePageWhileNotLoggedIn() throws NamingException, SQLException {
        when(session.getAttribute("userInterestedCategories")).thenReturn(new ArrayList<Category>());
        grantedAuths = new ArrayList<GrantedAuthority>();
        grantedAuths.add(new SimpleGrantedAuthority("dummy"));
        token = new AnonymousAuthenticationToken("abc", "abc", grantedAuths);
        SecurityContextHolder.getContext().setAuthentication(token);
        final ModelAndView testModelView = homeController.showHomePage(request, MODEL, session);
        assertEquals(LOGIN_REDIRECT, testModelView.getViewName());
    }

    /**
     * This tests
     * {@link HomeController#showHomePage(HttpServletRequest, ModelAndView, HttpSession)}
     * where the user can see the list of recommended resources.
     *
     * @throws NamingException
     * @throws SQLException
     */
    @Test
    public void testShowHomePageWhenReturnedRecommendedResourcesListIsValid() throws NamingException, SQLException {
        when(status.isLoggedIn()).thenReturn(true);
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("test", "test", grantedAuths));
        when(session.getAttribute("userInterestedCategories")).thenReturn(userInterestedCategories);
        when(session.getAttribute("userDetails")).thenReturn(user);
        when(
                userRecommendedResourceManager.getRecommendedResourcesByUserId(
                        VALID_USER_ID,
                        MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                        MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED)).thenReturn(LIST_OF_RECOMMENDED_RESOURCES);
        when(categoryManager.getAllCategories()).thenReturn(listOfCategories);
        when(mockCompletedUserResourceManager.getCountOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_NUMBER_OF_COMPLETED_RESOURCES);
        when(mockCompletedUserResourceManager.getCategoryNameWithMostNumberOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_TOP_CATEGORY);
        final ModelAndView newModel = homeController.showHomePage(request, MODEL, session);
        assertEquals(newModel.getModel().get(RECOMMENDED_RESOURCES), LIST_OF_RECOMMENDED_RESOURCES);
    }

    /**
     * This tests
     * {@link HomeController#showHomePage(HttpServletRequest, ModelAndView, HttpSession)}
     * where the returned recommended list is empty. Should display a message.
     *
     * @throws NamingException
     *             when the user is not authenticated
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testErrorMessageWhenRecommendedListIsEmpty() throws NamingException, SQLException {
        when(status.isLoggedIn()).thenReturn(true);
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("test", "test", grantedAuths));
        when(session.getAttribute("userDetails")).thenReturn(USER);
        when(
                userRecommendedResourceManager.getRecommendedResourcesByUserId(
                        VALID_USER_ID,
                        MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                        MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED))
                                .thenReturn(Collections.<UserRecommendedResource>emptyList());
        when(session.getAttribute("userInterestedCategories")).thenReturn(userInterestedCategories);
        when(mockCompletedUserResourceManager.getCountOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_NUMBER_OF_COMPLETED_RESOURCES);
        when(mockCompletedUserResourceManager.getCategoryNameWithMostNumberOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_TOP_CATEGORY);
        final ModelAndView newModel = homeController.showHomePage(request, MODEL, session);
        assertEquals(NO_RECOMMENDED_RESOURCES_MESSAGE, newModel.getModel().get("message"));
    }

    /**
     * Expects
     * {@link HomeController#showHomePage(HttpServletRequest, ModelAndView, HttpSession)}
     * to redirect to the homepage and the model to contain an error message and
     * an empty {@link List} of {@link UserRecommendedResource recommended
     * resources} if the
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserId(String, double, int)}
     * throws a {@link ManagerException}.
     *
     * @throws NamingException
     *             when the user is not authenticated
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testShowHomePageWhenRecommendedResourceManagerThrowsException() throws SQLException, NamingException {
        when(status.isLoggedIn()).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(AUTHENTICATION_TEST, AUTHENTICATION_TEST, grantedAuths));
        when(
                userRecommendedResourceManager.getRecommendedResourcesByUserId(
                        VALID_USER_ID,
                        MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                        MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED)).thenThrow(new ManagerException());
        when(session.getAttribute("userInterestedCategories")).thenReturn(userInterestedCategories);
        when(mockCompletedUserResourceManager.getCountOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_NUMBER_OF_COMPLETED_RESOURCES);
        when(mockCompletedUserResourceManager.getCategoryNameWithMostNumberOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_TOP_CATEGORY);
        final ModelAndView returnedModelAndView = homeController.showHomePage(request, MODEL, session);
        assertEquals(HOME_PAGE, returnedModelAndView.getViewName());
        assertEquals(
                I18N_BUNDLE.getString(RECOMMENDED_RESOURCES_RETRIEVAL_FAILURE),
                returnedModelAndView.getModel().get(MESSAGE));
        assertEquals(
                Collections.<UserRecommendedResource>emptyList(),
                returnedModelAndView.getModel().get(RECOMMENDED_RESOURCES));
    }

    /**
     * Expects
     * {@link HomeController#showHomePage(HttpServletRequest, ModelAndView, HttpSession)}
     * to redirect to the homepage and the model to contain a message and an
     * empty {@link UserRecommendedResource} {@link List} when
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserId(String, double, int)}
     * returns <code>null</code>.
     *
     * @throws NamingException
     *             when the user is not authenticated
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testShowHomePageWhenRecommendedResourceListIsNull() throws SQLException, NamingException {
        when(status.isLoggedIn()).thenReturn(true);
        when(
                userRecommendedResourceManager.getRecommendedResourcesByUserId(
                        VALID_USER_ID,
                        MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                        MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED)).thenReturn(null);
        when(session.getAttribute("userInterestedCategories")).thenReturn(userInterestedCategories);
        when(mockCompletedUserResourceManager.getCountOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_NUMBER_OF_COMPLETED_RESOURCES);
        when(mockCompletedUserResourceManager.getCategoryNameWithMostNumberOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_TOP_CATEGORY);
        final ModelAndView returnedModelAndView = homeController.showHomePage(request, MODEL, session);
        assertEquals(HOME_PAGE, returnedModelAndView.getViewName());
        assertEquals(I18N_BUNDLE.getString(NO_RECOMMENDED_RESOURCES), returnedModelAndView.getModel().get(MESSAGE));
        assertEquals(
                Collections.<UserRecommendedResource>emptyList(),
                returnedModelAndView.getModel().get(RECOMMENDED_RESOURCES));
    }

    /**
     * Expects
     * {@link HomeController#showHomePage(HttpServletRequest, ModelAndView, HttpSession)}
     * to redirect to the homepage and the model to contain a list with no null
     * elements when
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserId(String, double, int)}
     * returns a {@link UserRecommendedResource} {@link List} containing
     * <code>null</code> elements.
     *
     * @throws NamingException
     *             when the user is not authenticated
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testShowHomePageWhenRecommendedResourceListContainsNullElements() throws NamingException, SQLException {
        when(status.isLoggedIn()).thenReturn(true);
        final List<UserRecommendedResource> recommendedResourceListWithNull = new ArrayList<>();
        recommendedResourceListWithNull.add(null);
        recommendedResourceListWithNull.add(USER_RECOMMENDED_RESOURCE);
        when(
                userRecommendedResourceManager.getRecommendedResourcesByUserId(
                        VALID_USER_ID,
                        MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                        MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED))
                                .thenReturn(recommendedResourceListWithNull);
        when(session.getAttribute("userInterestedCategories")).thenReturn(userInterestedCategories);
        when(mockCompletedUserResourceManager.getCountOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_NUMBER_OF_COMPLETED_RESOURCES);
        when(mockCompletedUserResourceManager.getCategoryNameWithMostNumberOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_TOP_CATEGORY);
        final ModelAndView returnedModelAndView = homeController.showHomePage(request, MODEL, session);
        assertEquals(HOME_PAGE, returnedModelAndView.getViewName());
        assertEquals(LIST_OF_RECOMMENDED_RESOURCES, returnedModelAndView.getModel().get(RECOMMENDED_RESOURCES));
    }

    /**
     * Tests
     * {@link HomeController#showHomePage(HttpServletRequest, ModelAndView, HttpSession)}
     * to ensure the returned model contains all categories.
     *
     * @throws NamingException
     *             when the user is not authenticated
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testShowHomePageModelHasListOfAllCategories() throws SQLException, NamingException {
        when(status.isLoggedIn()).thenReturn(true);
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("test", "test", grantedAuths));
        when(session.getAttribute("userDetails")).thenReturn(USER);
        when(
                userRecommendedResourceManager.getRecommendedResourcesByUserId(
                        VALID_USER_ID,
                        MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                        MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED)).thenReturn(listOfRecommendResourceUser);
        when(session.getAttribute("userInterestedCategories")).thenReturn(userInterestedCategories);
        when(categoryManager.getAllCategories()).thenReturn(listOfCategories);
        when(mockCompletedUserResourceManager.getCountOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_NUMBER_OF_COMPLETED_RESOURCES);
        when(mockCompletedUserResourceManager.getCategoryNameWithMostNumberOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_TOP_CATEGORY);
        final ModelAndView newModel = homeController.showHomePage(request, MODEL, session);
        assertEquals(newModel.getModel().get(ALL_CATEGORIES), listOfCategories);
    }

    /**
     * Tests
     * {@link HomeController#showHomePage(HttpServletRequest, ModelAndView, HttpSession)}
     * when {@link CategoryManager#getAllCategories()} throws a
     * {@link ManagerException}.
     *
     * @throws NamingException
     *             when the user is not authenticated
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testShowHomePageModelWhenCategoryManagerThrowsException() throws SQLException, NamingException {
        when(status.isLoggedIn()).thenReturn(true);
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("test", "test", grantedAuths));

        when(session.getAttribute("userDetails")).thenReturn(USER);
        when(
                userRecommendedResourceManager.getRecommendedResourcesByUserId(
                        VALID_USER_ID,
                        MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                        MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED)).thenReturn(listOfRecommendResourceUser);
        when(session.getAttribute("userInterestedCategories")).thenReturn(userInterestedCategories);
        when(categoryManager.getAllCategories()).thenThrow(new ManagerException());
        when(mockCompletedUserResourceManager.getCountOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_NUMBER_OF_COMPLETED_RESOURCES);
        when(mockCompletedUserResourceManager.getCategoryNameWithMostNumberOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_TOP_CATEGORY);
        final ModelAndView newModel = homeController.showHomePage(request, MODEL, session);

        assertEquals(newModel.getModel().get(ALL_CATEGORIES), Collections.<Category>emptyList());
        assertEquals(
                newModel.getModel().get(CATEGORY_ERROR),
                I18N_BUNDLE.getString("com.cerner.devcenter.education.controllers.errorRetrievingCategories"));
    }

    /**
     * Expects
     * {@link HomeController#showHomePage(HttpServletRequest, ModelAndView, HttpSession)}
     * to redirect to the homepage, and the model to contain a message and an
     * empty {@link Category} {@link List} when
     * {@link CategoryManager#getAllCategories()} returns <code>null</code>.
     *
     * @throws NamingException
     *             when the user is not authenticated
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testShowHomePageWhenCategoryListIsNull() throws NamingException, SQLException {
        when(status.isLoggedIn()).thenReturn(true);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(AUTHENTICATION_TEST, AUTHENTICATION_TEST, grantedAuths));
        when(categoryManager.getAllCategories()).thenReturn(null);
        when(session.getAttribute("userInterestedCategories")).thenReturn(userInterestedCategories);
        when(mockCompletedUserResourceManager.getCountOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_NUMBER_OF_COMPLETED_RESOURCES);
        when(mockCompletedUserResourceManager.getCategoryNameWithMostNumberOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_TOP_CATEGORY);
        final ModelAndView returnedModelAndView = homeController.showHomePage(request, MODEL, session);
        assertEquals(HOME_PAGE, returnedModelAndView.getViewName());
        assertEquals(
                I18N_BUNDLE.getString(CATEGORY_LIST_RETURNED_EMPTY),
                returnedModelAndView.getModel().get(CATEGORY_ERROR));
        assertEquals(Collections.<Category>emptyList(), returnedModelAndView.getModel().get(ALL_CATEGORIES));
    }

    /**
     * Expects
     * {@link HomeController#showHomePage(HttpServletRequest, ModelAndView, HttpSession)}
     * to redirect to the homepage, and the model to contain a message and an
     * empty {@link Category} {@link List} when
     * {@link CategoryManager#getAllCategories()} returns an empty {@link List}.
     *
     * @throws NamingException
     *             when the user is not authenticated
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testShowHomePageWhenCategoryListIsEmpty() throws NamingException, SQLException {
        when(status.isLoggedIn()).thenReturn(true);
        when(categoryManager.getAllCategories()).thenReturn(Collections.<Category>emptyList());
        when(session.getAttribute("userInterestedCategories")).thenReturn(userInterestedCategories);
        when(mockCompletedUserResourceManager.getCountOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_NUMBER_OF_COMPLETED_RESOURCES);
        when(mockCompletedUserResourceManager.getCategoryNameWithMostNumberOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_TOP_CATEGORY);
        final ModelAndView returnedModelAndView = homeController.showHomePage(request, MODEL, session);
        assertEquals(HOME_PAGE, returnedModelAndView.getViewName());
        assertEquals(
                I18N_BUNDLE.getString(CATEGORY_LIST_RETURNED_EMPTY),
                returnedModelAndView.getModel().get(CATEGORY_ERROR));
        assertEquals(Collections.<Category>emptyList(), returnedModelAndView.getModel().get(ALL_CATEGORIES));
    }

    /**
     * Expects
     * {@link HomeController#showHomePage(HttpServletRequest, ModelAndView, HttpSession)}
     * to redirect to homepage, and the model to contain a {@link List} with no
     * <code>null</code> elements when
     * {@link CategoryManager#getAllCategories()} returns a {@link List} of
     * {@link Category categories} with <code>null</code> elements.
     *
     * @throws NamingException
     *             when the user is not authenticated
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testShowHomePageWhenCategoryListContainsNullElements() throws NamingException, SQLException {
        when(status.isLoggedIn()).thenReturn(true);
        final List<Category> categoryListWithNullElements = new ArrayList<>();
        categoryListWithNullElements.add(null);
        when(categoryManager.getAllCategories()).thenReturn(categoryListWithNullElements);
        when(session.getAttribute("userInterestedCategories")).thenReturn(userInterestedCategories);
        when(mockCompletedUserResourceManager.getCountOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_NUMBER_OF_COMPLETED_RESOURCES);
        when(mockCompletedUserResourceManager.getCategoryNameWithMostNumberOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_TOP_CATEGORY);
        final ModelAndView returnedModelAndView = homeController.showHomePage(request, MODEL, session);
        assertEquals(HOME_PAGE, returnedModelAndView.getViewName());
        assertEquals(Collections.<Category>emptyList(), returnedModelAndView.getModel().get(ALL_CATEGORIES));
    }

    /**
     * Expects
     * {@link HomeController#showHomePage(HttpServletRequest, ModelAndView, HttpSession)}
     * to redirect to the homepage, and the model to contain a message and an
     * empty {@link CompletedResource} {@link List} when
     * {@link CompletedUserResourceManager#getMostRecentlyCompletedResources(String, int)}
     * returns an empty {@link List}.
     *
     * @throws NamingException
     *             when the user is not authenticated
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testShowHomePageWhenCompletedUserResourceListIsEmpty() throws NamingException, SQLException {
        when(status.isLoggedIn()).thenReturn(true);
        when(
                mockCompletedUserResourceManager
                        .getMostRecentlyCompletedResources(VALID_USER_ID, NUMBER_OF_COMPLETED_RESOURCES_TO_RETURN))
                                .thenReturn(Collections.<CompletedResource>emptyList());
        when(session.getAttribute("userInterestedCategories")).thenReturn(userInterestedCategories);
        when(mockCompletedUserResourceManager.getCountOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_NUMBER_OF_COMPLETED_RESOURCES);
        when(mockCompletedUserResourceManager.getCategoryNameWithMostNumberOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_TOP_CATEGORY);
        final ModelAndView returnedModelAndView = homeController.showHomePage(request, MODEL, session);
        assertEquals(HOME_PAGE, returnedModelAndView.getViewName());
        assertEquals(
                I18N_BUNDLE.getString(NO_COMPLETED_RESOURCES),
                returnedModelAndView.getModel().get(CR_WIDGET_MESSAGE));
        assertEquals(
                Collections.<CompletedUserResource>emptyList(),
                returnedModelAndView.getModel().get(COMPLETED_RESOURCES));
    }

    /**
     * Expects
     * {@link HomeController#showHomePage(HttpServletRequest, ModelAndView, HttpSession)}
     * to redirect to the homepage, and the model to contain an error message
     * and an empty {@link CompletedResource} {@link List} when
     * {@link CompletedUserResourceManager#getMostRecentlyCompletedResources(String, int)}
     * throws a {@link ManagerException}.
     *
     * @throws NamingException
     *             when the user is not authenticated
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testShowHomePageWhenCompletedUserResourceManagerThrowsException() throws NamingException, SQLException {
        when(status.isLoggedIn()).thenReturn(true);
        when(
                mockCompletedUserResourceManager
                        .getMostRecentlyCompletedResources(VALID_USER_ID, NUMBER_OF_COMPLETED_RESOURCES_TO_RETURN))
                                .thenThrow(new ManagerException());
        when(session.getAttribute("userInterestedCategories")).thenReturn(userInterestedCategories);
        when(mockCompletedUserResourceManager.getCountOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_NUMBER_OF_COMPLETED_RESOURCES);
        when(mockCompletedUserResourceManager.getCategoryNameWithMostNumberOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_TOP_CATEGORY);
        final ModelAndView returnedModelAndView = homeController.showHomePage(request, MODEL, session);
        assertEquals(HOME_PAGE, returnedModelAndView.getViewName());
        assertEquals(
                I18N_BUNDLE.getString(COMPLETED_RESOURCES_RETRIEVAL_FAILURE),
                returnedModelAndView.getModel().get(CR_WIDGET_MESSAGE));
        assertEquals(
                Collections.<CompletedUserResource>emptyList(),
                returnedModelAndView.getModel().get(COMPLETED_RESOURCES));
    }

    /**
     * Expects
     * {@link HomeController#showHomePage(HttpServletRequest, ModelAndView, HttpSession)}
     * to redirect to the homepage and contain a {@link List} of all
     * {@link Category categories}, a {@link List} of
     * {@link UserRecommendedResource recommended resources}, a {@link List} of
     * the most recently {@link CompletedResource completed resources} for the
     * user, a welcome message, the number of {@link CompletedResource resources
     * completed} by the user, and the name of the {@link Category} with most
     * number of {@link CompletedResource completed resources}.
     *
     * @throws NamingException
     *             when the user is not authenticated
     *
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testShowHomePageWhenAllManagersReturnValidResults() throws SQLException, NamingException {
        when(status.isLoggedIn()).thenReturn(true);
        when(session.getAttribute(USER_DETAILS)).thenReturn(mockUserProfileDetails);
        when(mockUserProfileDetails.getFirstName()).thenReturn(VALID_FIRST_NAME);
        when(mockUserProfileDetails.getUserId()).thenReturn(VALID_USER_ID);
        when(
                mockCompletedUserResourceManager
                        .getMostRecentlyCompletedResources(VALID_USER_ID, NUMBER_OF_COMPLETED_RESOURCES_TO_RETURN))
                                .thenReturn(LIST_OF_COMPLETED_RESOURCES);
        when(categoryManager.getAllCategories()).thenReturn(listOfCategories);
        when(
                userRecommendedResourceManager.getRecommendedResourcesByUserId(
                        VALID_USER_ID,
                        MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                        MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED)).thenReturn(LIST_OF_RECOMMENDED_RESOURCES);
        when(session.getAttribute("userInterestedCategories")).thenReturn(userInterestedCategories);
        when(mockCompletedUserResourceManager.getCountOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_NUMBER_OF_COMPLETED_RESOURCES);
        when(mockCompletedUserResourceManager.getCategoryNameWithMostNumberOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_TOP_CATEGORY);
        final ModelAndView returnedModelAndView = homeController.showHomePage(request, MODEL, session);
        assertEquals(HOME_PAGE, returnedModelAndView.getViewName());
        assertEquals(LIST_OF_COMPLETED_RESOURCES, returnedModelAndView.getModel().get(COMPLETED_RESOURCES));
        assertEquals(listOfCategories, returnedModelAndView.getModel().get(ALL_CATEGORIES));
        assertEquals(LIST_OF_RECOMMENDED_RESOURCES, returnedModelAndView.getModel().get(RECOMMENDED_RESOURCES));
        assertEquals(
                NUMBER_OF_COMPLETED_RESOURCES_TO_RETURN,
                returnedModelAndView.getModel().get(NUMBER_OF_COMPLETED_RESOURCES_REQUIRED));
        assertEquals(
                VALID_NUMBER_OF_COMPLETED_RESOURCES,
                returnedModelAndView.getModel().get(NUMBER_OF_COMPLETED_RESOURCES));
        assertEquals(VALID_TOP_CATEGORY, returnedModelAndView.getModel().get(TOP_CATEGORY));
        assertEquals(
                MessageFormat.format(I18N_BUNDLE.getString(GENERAL_DETAILS_WIDGET_TITLE), VALID_FIRST_NAME),
                returnedModelAndView.getModel().get(WELCOME_WIDGET_TITLE));
    }

    /**
     * Expects {@link HomeController#getCompletedResourcesInfoForUser(String)}
     * to put "None" into the {@link ModelAndView model} returned by
     * {@link HomeController#showHomePage(HttpServletRequest, ModelAndView, HttpSession)}
     * when
     * {@link CompletedUserResourceManager#getCategoryNameWithMostNumberOfResourcesCompletedByUser(String)}
     * returns an empty string.
     *
     * @throws NamingException
     *             when the user is not authenticated
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testGetCompletedResourcesInfoForUserWhenCompletedUserResourceManagerGetNameOfCategoryReturnsEmptyString()
            throws SQLException, NamingException {
        when(status.isLoggedIn()).thenReturn(true);
        when(
                mockCompletedUserResourceManager
                        .getMostRecentlyCompletedResources(VALID_USER_ID, NUMBER_OF_COMPLETED_RESOURCES_TO_RETURN))
                                .thenReturn(LIST_OF_COMPLETED_RESOURCES);
        when(categoryManager.getAllCategories()).thenReturn(listOfCategories);
        when(
                userRecommendedResourceManager.getRecommendedResourcesByUserId(
                        VALID_USER_ID,
                        MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                        MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED)).thenReturn(LIST_OF_RECOMMENDED_RESOURCES);
        when(session.getAttribute("userInterestedCategories")).thenReturn(userInterestedCategories);
        when(mockCompletedUserResourceManager.getCountOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_NUMBER_OF_COMPLETED_RESOURCES);
        when(mockCompletedUserResourceManager.getCategoryNameWithMostNumberOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(EMPTY_STRING);
        final ModelAndView returnedModelAndView = homeController.showHomePage(request, MODEL, session);
        assertEquals(NONE, returnedModelAndView.getModel().get(TOP_CATEGORY));
    }

    /**
     * Expects {@link HomeController#getCompletedResourcesInfoForUser(String)}
     * to put "None" into the {@link ModelAndView model} returned by
     * {@link HomeController#showHomePage(HttpServletRequest, ModelAndView, HttpSession)}
     * when
     * {@link CompletedUserResourceManager#getCompletedResourcesByUserId(String)}
     * returns 0.
     */
    @Test
    public void testGetCompletedResourcesInfoForUserWhenCompletedUserResourcesIsZero()
            throws SQLException, NamingException {
        when(status.isLoggedIn()).thenReturn(true);
        when(mockCompletedUserResourceManager.getMostRecentlyCompletedResources(VALID_USER_ID,
                NUMBER_OF_COMPLETED_RESOURCES_TO_RETURN)).thenReturn(LIST_OF_COMPLETED_RESOURCES);
        when(categoryManager.getAllCategories()).thenReturn(listOfCategories);
        when(userRecommendedResourceManager.getRecommendedResourcesByUserId(VALID_USER_ID,
                MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED)).thenReturn(LIST_OF_RECOMMENDED_RESOURCES);
        when(session.getAttribute("userInterestedCategories")).thenReturn(userInterestedCategories);
        when(mockCompletedUserResourceManager.getCountOfResourcesCompletedByUser(VALID_USER_ID)).thenReturn(0);
        final ModelAndView returnedModelAndView = homeController.showHomePage(request, MODEL, session);
        assertEquals(NONE, returnedModelAndView.getModel().get(TOP_CATEGORY));
    }

    /**
     * Expects {@link HomeController#getCompletedResourcesInfoForUser(String)}
     * to put 0 and "None" into the {@link ModelAndView model} returned by
     * {@link HomeController#showHomePage(HttpServletRequest, ModelAndView, HttpSession)}
     * when
     * {@link CompletedUserResourceManager#getCountOfResourcesCompletedByUser(String)}
     * throws a {@link ManagerException}.
     *
     * @throws NamingException
     *             when the user is not authenticated
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testGetCompletedResourcesInfoForUserWhenCompletedUserResourceManagerGetCountOfCompletedResourcesThrowsManagerException()
            throws SQLException, NamingException {
        when(status.isLoggedIn()).thenReturn(true);
        when(
                mockCompletedUserResourceManager
                        .getMostRecentlyCompletedResources(VALID_USER_ID, NUMBER_OF_COMPLETED_RESOURCES_TO_RETURN))
                                .thenReturn(LIST_OF_COMPLETED_RESOURCES);
        when(categoryManager.getAllCategories()).thenReturn(listOfCategories);
        when(
                userRecommendedResourceManager.getRecommendedResourcesByUserId(
                        VALID_USER_ID,
                        MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                        MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED)).thenReturn(LIST_OF_RECOMMENDED_RESOURCES);
        when(session.getAttribute("userInterestedCategories")).thenReturn(userInterestedCategories);
        when(mockCompletedUserResourceManager.getCountOfResourcesCompletedByUser(VALID_USER_ID))
                .thenThrow(new ManagerException());
        when(mockCompletedUserResourceManager.getCategoryNameWithMostNumberOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_TOP_CATEGORY);
        final ModelAndView returnedModelAndView = homeController.showHomePage(request, MODEL, session);
        assertEquals(0, returnedModelAndView.getModel().get(NUMBER_OF_COMPLETED_RESOURCES));
        assertEquals(NONE, returnedModelAndView.getModel().get(TOP_CATEGORY));
    }

    /**
     * Expects {@link HomeController#getCompletedResourcesInfoForUser(String)}
     * to put "None" and 0 into the {@link ModelAndView model} returned by
     * {@link HomeController#showHomePage(HttpServletRequest, ModelAndView, HttpSession)}
     * when
     * {@link CompletedUserResourceManager#getCategoryNameWithMostNumberOfResourcesCompletedByUser(String)}
     * throws a {@link ManagerException}.
     *
     * @throws NamingException
     *             when the user is not authenticated
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testGetCompletedResourcesInfoForUserWhenCompletedUserResourceManagerGetNameOfCategoryThrowsManagerException()
            throws SQLException, NamingException {
        when(status.isLoggedIn()).thenReturn(true);
        when(
                mockCompletedUserResourceManager
                        .getMostRecentlyCompletedResources(VALID_USER_ID, NUMBER_OF_COMPLETED_RESOURCES_TO_RETURN))
                                .thenReturn(LIST_OF_COMPLETED_RESOURCES);
        when(categoryManager.getAllCategories()).thenReturn(listOfCategories);
        when(
                userRecommendedResourceManager.getRecommendedResourcesByUserId(
                        VALID_USER_ID,
                        MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                        MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED)).thenReturn(LIST_OF_RECOMMENDED_RESOURCES);
        when(session.getAttribute("userInterestedCategories")).thenReturn(userInterestedCategories);
        when(mockCompletedUserResourceManager.getCountOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_NUMBER_OF_COMPLETED_RESOURCES);
        when(mockCompletedUserResourceManager.getCategoryNameWithMostNumberOfResourcesCompletedByUser(VALID_USER_ID))
                .thenThrow(new ManagerException());
        final ModelAndView returnedModelAndView = homeController.showHomePage(request, MODEL, session);
        assertEquals(NONE, returnedModelAndView.getModel().get(TOP_CATEGORY));
        assertEquals(0, returnedModelAndView.getModel().get(NUMBER_OF_COMPLETED_RESOURCES));
    }

    /**
     * Expects
     * {@link HomeController#searchResource(String, ResourceCategoryRelation, HttpSession)}
     * to set {@link ModelAndView}'s ViewName to the login page when the user is
     * not logged in after searching.
     */

    @Test
    public void testSearchResources_NotLoggedIn() {
        when(status.isLoggedIn()).thenReturn(false);
        final ModelAndView newModel = homeController.searchResource(VALID_KEYWORD, resourceCategoryRelation, session);
        assertEquals(NOT_LOGGED_IN, newModel.getModel().get(MESSAGE));
        assertEquals(LOGIN_REDIRECT, newModel.getViewName());
    }

    /**
     * Expects
     * {@link HomeController#searchResource(String, ResourceCategoryRelation, HttpSession)}
     * to throw {@link NullPointerException} when HttpSession is null.
     */
    @Test(expected = NullPointerException.class)
    public void testSearchResource_SessionIsNull() {
        try {
            homeController.searchResource(VALID_KEYWORD, resourceCategoryRelation, null);
        } catch (final NullPointerException e) {
            assertEquals(SESSION_NULL, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link HomeController#searchResource(String, ResourceCategoryRelation, HttpSession)}
     * to set {@link ModelAndView}'s model to the throw invalid string name
     * error when search {@link String} is empty.
     */
    @Test
    public void testSearchResource_EmptyString() {
        when(status.isLoggedIn()).thenReturn(true);
        final ModelAndView modelAndView = homeController
                .searchResource(EMPTY_STRING, resourceCategoryRelation, session);
        assertEquals(NO_RESULTS, modelAndView.getModel().get(NO_RESOURCE));
        assertEquals(SEARCH, modelAndView.getViewName());
    }

    /**
     * Expects
     * {@link HomeController#searchResource(String, ResourceCategoryRelation, HttpSession)}
     * to set {@link ModelAndView}'s model to the throw invalid string name
     * error when search {@link String} is blank.
     */
    @Test
    public void testSearchResource_BlankString() {
        when(status.isLoggedIn()).thenReturn(true);
        final ModelAndView modelAndView = homeController
                .searchResource(BLANK_STRING, resourceCategoryRelation, session);
        assertEquals(NO_RESULTS, modelAndView.getModel().get(NO_RESOURCE));
        assertEquals(SEARCH, modelAndView.getViewName());
    }

    /**
     * Expects
     * {@link HomeController#searchResource(String, ResourceCategoryRelation, HttpSession)}
     * to set {@link ModelAndView}'s model to the throw invalid string name
     * error when search {@link String} is null.
     */
    @Test
    public void testSearchResource_NullString() {
        when(status.isLoggedIn()).thenReturn(true);
        final ModelAndView modelAndView = homeController.searchResource(null, resourceCategoryRelation, session);
        assertEquals(NO_RESULTS, modelAndView.getModel().get(NO_RESOURCE));
        assertEquals(SEARCH, modelAndView.getViewName());
    }

    /**
     * Expects
     * {@link HomeController#searchResource(String, ResourceCategoryRelation, HttpSession)}
     * to redirect to the search page, and the model to contain a No Resource
     * Found error message and an empty {@link Resource} List when
     * ResourceManager returns empty.
     */
    @Test
    public void testSearchResource_ResourcesReturnsEmpty() {
        when(resourceManager.getSearchedResources(KEYWORD)).thenReturn(Collections.<Resource>emptyList());
        when(status.isLoggedIn()).thenReturn(true);
        final ModelAndView modelAndView = homeController.searchResource(KEYWORD, resourceCategoryRelation, session);
        assertTrue(Collections.<Resource>emptyList().equals(modelAndView.getModel().get(RESOURCE_LIST)));
        assertTrue(NO_RESOURCE_FOUND.equals(modelAndView.getModel().get(NO_RESOURCE)));
        assertEquals(SEARCH, modelAndView.getViewName());
    }

    /**
     * Expects
     * {@link HomeController#searchResource(String, ResourceCategoryRelation, HttpSession)}
     * to redirect to the search page, and the model to contain a No Resource
     * Found error message and a <code>null</code> {@link Resource} List when
     * ResourceManager returns <code>null</code>.
     */
    @Test
    public void testSearchResource_ResourcesReturnsNull() {
        when(resourceManager.getSearchedResources(KEYWORD)).thenReturn(null);
        when(status.isLoggedIn()).thenReturn(true);
        final ModelAndView modelAndView = homeController.searchResource(KEYWORD, resourceCategoryRelation, session);
        assertTrue(NO_RESOURCE_FOUND.equals(modelAndView.getModel().get(NO_RESOURCE)));
        assertEquals(null, modelAndView.getModel().get(RESOURCE_LIST));
        assertEquals(SEARCH, modelAndView.getViewName());
    }

    /**
     * Expects
     * {@link HomeController#searchResource(String, ResourceCategoryRelation, HttpSession)}
     * to redirect to the search page, and the model to contain a No Resource
     * Found error message and an empty {@link Category} List when
     * CategoryManager returns empty.
     */
    @Test
    public void testSearchResource_ChosenCategoriesReturnsEmpty() {
        when(categoryManager.chosenCategories(KEYWORD, userId)).thenReturn(Collections.<Category>emptyList());
        when(status.isLoggedIn()).thenReturn(true);
        final ModelAndView modelAndView = homeController.searchResource(KEYWORD, resourceCategoryRelation, session);
        assertTrue(Collections.<Category>emptyList().equals(modelAndView.getModel().get(CHOSEN_CATEGORY_LIST)));
        assertTrue(NO_CATEGORY_FOUND.equals(modelAndView.getModel().get(NO_CATEGORY)));
        assertEquals(SEARCH, modelAndView.getViewName());
    }

    /**
     * Expects
     * {@link HomeController#searchResource(String, ResourceCategoryRelation, HttpSession)}
     * to redirect to the search page, and the model to contain a No Category
     * Found error message and a <code>null</code> {@link Category} List when
     * CategoryManager returns <code>null</code>.
     */
    @Test
    public void testSearchResource_ChosenCategoryReturnsNull() {
        when(categoryManager.chosenCategories(KEYWORD, userId)).thenReturn(null);
        when(status.isLoggedIn()).thenReturn(true);
        final ModelAndView modelAndView = homeController.searchResource(KEYWORD, resourceCategoryRelation, session);
        assertTrue(NO_CATEGORY_FOUND.equals(modelAndView.getModel().get(NO_CATEGORY)));
        assertEquals(null, modelAndView.getModel().get(CHOSEN_CATEGORY_LIST));
        assertEquals(SEARCH, modelAndView.getViewName());
    }

    /**
     * Expects
     * {@link HomeController#searchResource(String, ResourceCategoryRelation, HttpSession)}
     * to set the returned {@link ModelAndView}'s ViewName to the searchResource
     * page and it's model to hold a {@link List} containing the correct
     * {@link Category} for the search {@link String}.
     */
    @Test
    public void testSearchResource_ChosenCategoriesSuccess() {
        final List<Category> chsoenCategory = Collections.singletonList(new Category(1, KEYWORD, VALID_DESC));
        when(categoryManager.chosenCategories(KEYWORD, userId)).thenReturn(chsoenCategory);
        when(status.isLoggedIn()).thenReturn(true);
        ModelAndView modelAndView = homeController.searchResource(KEYWORD, resourceCategoryRelation, session);
        assertEquals(SEARCH, modelAndView.getViewName());
        assertSame(chsoenCategory, modelAndView.getModel().get(CHOSEN_CATEGORY_LIST));
    }

    /**
     * Expects
     * {@link HomeController#searchResource(String, ResourceCategoryRelation, HttpSession)}
     * to redirect to the search page, and the model to contain a No Resource
     * Found error message and an empty {@link Category} List when
     * CategoryManager returns empty.
     */
    @Test
    public void testSearchResource_NonchosenCategoriesReturnsEmpty() {
        when(categoryManager.nonchosenCategories(KEYWORD, userId)).thenReturn(Collections.<Category>emptyList());
        when(status.isLoggedIn()).thenReturn(true);
        ModelAndView modelAndView = homeController.searchResource(KEYWORD, resourceCategoryRelation, session);
        assertTrue(Collections.<Category>emptyList().equals(modelAndView.getModel().get(NON_CHOSEN_CATEGORY_LIST)));
        assertTrue(NO_CATEGORY_FOUND.equals(modelAndView.getModel().get(NO_CATEGORY)));
        assertEquals(SEARCH, modelAndView.getViewName());
    }

    /**
     * Expects
     * {@link HomeController#searchResource(String, ResourceCategoryRelation, HttpSession)}
     * to redirect to the search page, and the model to contain a No Category
     * Found error message and a <code>null</code> {@link Category} List when
     * CategoryManager returns <code>null</code>.
     */
    @Test
    public void testSearchResource_NonchosenCategoryReturnsNull() {
        when(categoryManager.nonchosenCategories(KEYWORD, userId)).thenReturn(null);
        when(status.isLoggedIn()).thenReturn(true);
        final ModelAndView modelAndView = homeController.searchResource(KEYWORD, resourceCategoryRelation, session);
        assertTrue(NO_CATEGORY_FOUND.equals(modelAndView.getModel().get(NO_CATEGORY)));
        assertEquals(null, modelAndView.getModel().get(NON_CHOSEN_CATEGORY_LIST));
        assertEquals(SEARCH, modelAndView.getViewName());
    }

    /**
     * Expects
     * {@link HomeController#searchResource(String, ResourceCategoryRelation, HttpSession)}
     * to set the returned {@link ModelAndView}'s ViewName to the searchResource
     * page and it's model to hold a {@link List} containing the correct
     * {@link Category} for the search {@link String}.
     */
    @Test
    public void testSearchResource_NonchosenCategoriesSuccess() {
        final List<Category> nonchosenCategory = Collections.singletonList(new Category(1, KEYWORD, VALID_DESC));
        when(categoryManager.nonchosenCategories(KEYWORD, userId)).thenReturn(nonchosenCategory);
        when(status.isLoggedIn()).thenReturn(true);
        final ModelAndView modelAndView = homeController.searchResource(KEYWORD, resourceCategoryRelation, session);
        assertEquals(SEARCH, modelAndView.getViewName());
        assertSame(nonchosenCategory, modelAndView.getModel().get(NON_CHOSEN_CATEGORY_LIST));
    }

    /**
     * Test
     * {@link HomeController#addCategory(String, String, String, HttpSession)}
     * when {@link HttpSession} is null. Expects
     * {@link IllegalArgumentException}.
     *
     * @throws DuplicateUserInterestedCategoryException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddInterestedTopicWhenHttpSessionIsNull() throws DuplicateUserInterestedCategoryException {
        homeController.addCategory("12", "1", "2", null);
    }

    /**
     * Test
     * {@link HomeController#addCategory(String, String, String, HttpSession)}
     * when {@link Category} Id is null. Expects
     * {@link IllegalArgumentException}.
     *
     * @throws DuplicateUserInterestedCategoryException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddInterestedTopicWhenCategoryIdIsNull() throws DuplicateUserInterestedCategoryException {
        homeController.addCategory(null, "1", "2", session);
    }

    /**
     * Test
     * {@link HomeController#addCategory(String, String, String, HttpSession)}
     * when skill level is null. Expects {@link IllegalArgumentException}.
     *
     * @throws DuplicateUserInterestedCategoryException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddInterestedTopicWhenSkillLevelIsNull() throws DuplicateUserInterestedCategoryException {
        homeController.addCategory("12", null, "2", session);
    }

    /**
     * Test
     * {@link HomeController#addCategory(String, String, String, HttpSession)}
     * when interest level is null. Expects {@link IllegalArgumentException}.
     *
     * @throws DuplicateUserInterestedCategoryException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddInterestedTopicWhenInterestLevelIsNull() throws DuplicateUserInterestedCategoryException {
        homeController.addCategory("12", "1", null, session);
    }

    /**
     * Test
     * {@link HomeController#addCategory(String, String, String, HttpSession)}
     * with all valid inputs.
     *
     * @throws DuplicateUserInterestedCategoryException
     */
    @Test
    public void testAddCategoryWithValidInputs() throws DuplicateUserInterestedCategoryException {
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(TEST, TEST, grantedAuths));
        when(session.getAttribute(USER_DETAILS)).thenReturn(user);
        when(userInterestedTopicManager.addUserInterestedCategory(any(UserInterestedCategory.class))).thenReturn(true);
        final boolean isAddedToDatabase = homeController.addCategory("12", "1", "2", session);
        assertTrue(isAddedToDatabase);
    }

    /**
     * Expects
     * {@link HomeController#searchResource(String, ResourceCategoryRelation, HttpSession)}
     * to set the returned {@link ModelAndView}'s ViewName to the searchResource
     * page and it's model to hold a {@link List} containing the correct
     * {@link Resource} for the search {@link String}.
     */
    @Test
    public void testSearchResource_ResourceSuccess() {
        final List<Resource> resources = Collections.singletonList(new Resource(1, validResourceUrl, VALID_DESC));
        when(resourceManager.getSearchedResources(KEYWORD)).thenReturn(resources);
        when(status.isLoggedIn()).thenReturn(true);
        final ModelAndView modelAndView = homeController.searchResource(KEYWORD, resourceCategoryRelation, session);
        assertEquals(SEARCH, modelAndView.getViewName());
        assertSame(resources, modelAndView.getModel().get(RESOURCE_LIST));
    }

    /**
     * Expects
     * {@link HomeController#searchResource(String, ResourceCategoryRelation, HttpSession)}
     * to set the returned {@link ModelAndView}'s ViewName to the searchResource
     * page and it's model to contain an error message when
     * {@link ResourceManager#getSearchedResources(String)} throws a
     * {@link ManagerException}.
     */
    @Test
    public void testShowCompletedResources_ManagerException() {
        when(resourceManager.getSearchedResources(KEYWORD)).thenThrow(managerException);
        when(status.isLoggedIn()).thenReturn(true);
        final ModelAndView modelAndView = homeController.searchResource(KEYWORD, resourceCategoryRelation, session);
        assertEquals(SEARCH, modelAndView.getViewName());
        assertNull(modelAndView.getModel().get(RESOURCE_LIST));
        assertEquals(
                I18N_BUNDLE.getString(GET_REQUEST_ERROR),
                modelAndView.getModel().get(UNABLE_TO_RETRIEVE_RESOURCE));
    }

    /**
     * Expects
     * {@link HomeController#searchResource(String, ResourceCategoryRelation, HttpSession)}
     * to set the returned {@link ModelAndView}'s ViewName to the searchResource
     * page and it's model to contain an error message when
     * {@link CategoryManager#nonchosenCategories(String, String)} throws a
     * {@link ManagerException} When there is an error retrieving searched
     * resources from the database.
     */
    @Test
    public void testShowCompletedCateogry_ManagerException() {
        when(categoryManager.nonchosenCategories(KEYWORD, userId)).thenThrow(managerException);
        when(status.isLoggedIn()).thenReturn(true);
        final ModelAndView modelAndView = homeController.searchResource(KEYWORD, resourceCategoryRelation, session);
        assertEquals(SEARCH, modelAndView.getViewName());
        assertNull(modelAndView.getModel().get(NON_CHOSEN_CATEGORY_LIST));
        assertEquals(
                I18N_BUNDLE.getString(GET_REQUEST_ERROR),
                modelAndView.getModel().get(UNABLE_TO_RETRIEVE_CATEGORY));
    }

    /**
     * Test helper method for creating a list of {@link Category} objects.
     *
     * @param size
     *            The number of {@link Category} objects. to place in the list.
     *            Cannot be negative.
     * @return A list of unique {@link Category} objects of the specified size.
     * @throws IllegalArgumentException
     *             If size is negative.
     */
    private static List<Category> getTestCategoryList(final int size) {
        final List<Category> categoryList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            categoryList.add(new Category(VALID_CATEGORY_ID + i, VALID_CATEGORY_NAME + i, VALID_CATEGORY_DESC));
        }
        return categoryList;
    }

    /**
     * Expects {@link HomeController#filterRecommendedResources(int)} to throw
     * {@link IllegalArgumentException} when the categoryId is -1 (invalid).
     *
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFilterRecommendedResourcesInvalidCategoryId() throws SQLException {
        try {
            homeController.filterRecommendedResources(-1);
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_INVALID_ERROR, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link HomeController#filterRecommendedResources(int)} to throw
     * {@link IllegalArgumentException} when the categoryId is 0 (invalid).
     *
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFilterRecommendedResourcesZeroCategoryId() throws SQLException {
        try {
            homeController.filterRecommendedResources(0);
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_INVALID_ERROR, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link HomeController#filterRecommendedResources(int)} to execute
     * as expected when valid category ID is passed.
     *
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testFilterRecommendedResources() throws SQLException {
        final List<Category> userInterestedCategoriesTest = new ArrayList<>();

        when(categoryManager.getCategoryById(VALID_CATEGORY_ID)).thenReturn(CATEGORY);
        when(session.getAttribute(USER_DETAILS)).thenReturn(user);
        when(session.getAttribute("userInterestedCategories")).thenReturn(new ArrayList<Category>());
        userInterestedCategoriesTest.add(CATEGORY);
        when(
                userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(
                        VALID_USER_ID,
                        userInterestedCategoriesTest,
                        MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                        MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED)).thenReturn(LIST_OF_RECOMMENDED_RESOURCES);
        assertEquals(LIST_OF_RECOMMENDED_RESOURCES, homeController.filterRecommendedResources(VALID_CATEGORY_ID));

    }

    /**
     * Expects {@link HomeController#filterRecommendedResources(int)} to log the
     * error message when the {@link ManagerException} is thrown by
     * {@link CategoryManager#getCategoryById(int)}.
     *
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testFilterRecommendedResourcesExceptionForGetCategoryById() throws SQLException {
        when(categoryManager.getCategoryById(VALID_CATEGORY_ID)).thenThrow(ManagerException.class);
        homeController.filterRecommendedResources(VALID_CATEGORY_ID);
        Mockito.verify(mockAppender).doAppend((LoggingEvent) captorLoggingEvent.capture());
        final LoggingEvent loggingEvent = (LoggingEvent) captorLoggingEvent.getValue();
        assertEquals(Level.ERROR, loggingEvent.getLevel());
        assertEquals(ERROR_FILTERING_RESOURCES, loggingEvent.getRenderedMessage());
    }

    /**
     * Expects {@link HomeController#filterRecommendedResources(int)} to log the
     * error message when the {@link ManagerException} is thrown by
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserIdAndCategories(String, List, double, int)}.
     *
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testFilterRecommendedResourcesExceptionForRecommendedResources() throws SQLException {
        final List<Category> userInterestedCategoriesTest = new ArrayList<>();

        when(categoryManager.getCategoryById(VALID_CATEGORY_ID)).thenReturn(CATEGORY);
        userInterestedCategoriesTest.add(CATEGORY);
        when(session.getAttribute("userInterestedCategories")).thenReturn(new ArrayList<Category>());
        when(session.getAttribute("userDetails")).thenReturn(user);
        when(
                userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(
                        VALID_USER_ID,
                        userInterestedCategoriesTest,
                        MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                        MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED)).thenThrow(ManagerException.class);
        homeController.filterRecommendedResources(VALID_CATEGORY_ID);
        Mockito.verify(mockAppender).doAppend((LoggingEvent) captorLoggingEvent.capture());
        final LoggingEvent loggingEvent = (LoggingEvent) captorLoggingEvent.getValue();
        assertEquals(Level.ERROR, loggingEvent.getLevel());
        assertEquals(ERROR_FILTERING_RESOURCES, loggingEvent.getRenderedMessage());
    }

    /**
     * Expects {@link HomeController#filterRecommendedResources(int)} to return
     * a valid {@link List} of {@link UserRecommendedResource recommended
     * resources} when a filter has been selected.
     *
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testFilterRecommendedResourcesAddFilter() throws SQLException {
        final List<Category> userInterestedCategoriesTest = new ArrayList<>();
        when(categoryManager.getCategoryById(VALID_CATEGORY_ID)).thenReturn(CATEGORY);
        userInterestedCategoriesTest.add(CATEGORY);
        when(session.getAttribute(USER_DETAILS)).thenReturn(USER);
        when(session.getAttribute("userInterestedCategories")).thenReturn(new ArrayList<Category>());
        when(
                userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(
                        VALID_USER_ID,
                        userInterestedCategoriesTest,
                        MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                        MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED)).thenReturn(LIST_OF_RECOMMENDED_RESOURCES);
        assertEquals(LIST_OF_RECOMMENDED_RESOURCES, homeController.filterRecommendedResources(VALID_CATEGORY_ID));
    }

    /**
     * Expects {@link HomeController#filterRecommendedResources(int)} to return
     * a valid {@link List} of {@link UserRecommendedResource recommended
     * resources} when a filter has been selected and then removed.
     *
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testFilterRecommendedResourcesRemoveFilter() throws SQLException {
        final List<Category> userInterestedCategoriesTest = new ArrayList<>();
        when(categoryManager.getCategoryById(VALID_CATEGORY_ID)).thenReturn(CATEGORY);
        userInterestedCategoriesTest.add(CATEGORY);
        when(session.getAttribute(USER_DETAILS)).thenReturn(USER);
        when(session.getAttribute("userInterestedCategories")).thenReturn(new ArrayList<Category>());
        when(
                userRecommendedResourceManager.getRecommendedResourcesByUserId(
                        VALID_USER_ID,
                        MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                        MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED)).thenReturn(LIST_OF_RECOMMENDED_RESOURCES);
        homeController.filterRecommendedResources(VALID_CATEGORY_ID);
        assertEquals(LIST_OF_RECOMMENDED_RESOURCES, homeController.filterRecommendedResources(VALID_CATEGORY_ID));
    }

    /**
     * Expects {@link HomeController#filterRecommendedResources(int)} to return
     * a valid {@link List} of {@link UserRecommendedResource recommended
     * resources} when two filters have been selected.
     *
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testFilterRecommendedResourcesTwoFilters() throws SQLException {
        final List<Category> userInterestedCategoriesTest = new ArrayList<>();
        final Category newCategory = new Category(1, VALID_CATEGORY_NAME, VALID_CATEGORY_DESC);
        when(categoryManager.getCategoryById(VALID_CATEGORY_ID)).thenReturn(CATEGORY);
        userInterestedCategoriesTest.add(CATEGORY);
        when(session.getAttribute(USER_DETAILS)).thenReturn(USER);
        when(session.getAttribute("userInterestedCategories")).thenReturn(new ArrayList<Category>());
        when(
                userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(
                        VALID_USER_ID,
                        userInterestedCategoriesTest,
                        MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                        MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED)).thenReturn(LIST_OF_RECOMMENDED_RESOURCES);
        homeController.filterRecommendedResources(VALID_CATEGORY_ID);
        when(categoryManager.getCategoryById(2)).thenReturn(newCategory);
        userInterestedCategoriesTest.add(newCategory);
        assertEquals(LIST_OF_RECOMMENDED_RESOURCES, homeController.filterRecommendedResources(2));
    }

    /**
     * Expects {@link HomeController#filterRecommendedResources(int)} to return
     * a valid {@link List} of {@link UserRecommendedResource recommended
     * resources} when two filters have been selected and one has been removed.
     *
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testFilterRecommendedResourcesTwoFiltersThenRemoveOne() throws SQLException {
        final List<Category> userInterestedCategoriesTest = new ArrayList<>();
        final Category newCategory = new Category(1, VALID_CATEGORY_NAME, VALID_CATEGORY_DESC);
        when(categoryManager.getCategoryById(VALID_CATEGORY_ID)).thenReturn(CATEGORY);
        userInterestedCategoriesTest.add(CATEGORY);
        when(session.getAttribute(USER_DETAILS)).thenReturn(USER);
        when(session.getAttribute("userInterestedCategories")).thenReturn(new ArrayList<Category>());
        when(
                userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(
                        VALID_USER_ID,
                        userInterestedCategoriesTest,
                        MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                        MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED)).thenReturn(listOfRecommendResourceUser);
        homeController.filterRecommendedResources(VALID_CATEGORY_ID);
        when(categoryManager.getCategoryById(2)).thenReturn(newCategory);
        userInterestedCategoriesTest.add(newCategory);
        homeController.filterRecommendedResources(2);
        userInterestedCategoriesTest.remove(CATEGORY);
        when(
                userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(
                        VALID_USER_ID,
                        userInterestedCategoriesTest,
                        MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                        MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED)).thenReturn(listOfRecommendResourceUser);
        assertEquals(listOfRecommendResourceUser, homeController.filterRecommendedResources(VALID_CATEGORY_ID));
    }

    /**
     * Expects {@link HomeController#filterRecommendedResources(int)} to return
     * a valid {@link List} of {@link UserRecommendedResource recommended
     * resources} when three filters have been selected and one has been
     * removed.
     *
     * @throws SQLException
     *             when there is an error with the query
     */
    @Test
    public void testFilterRecommendedResourcesThreeFiltersThenRemoveOne() throws SQLException {
        final List<Category> userInterestedCategoriesTest = new ArrayList<>();
        final Category newCategory = new Category(1, VALID_CATEGORY_NAME, VALID_CATEGORY_DESC);
        final Category thirdCategory = new Category(3, VALID_CATEGORY_NAME, VALID_CATEGORY_DESC);
        when(categoryManager.getCategoryById(VALID_CATEGORY_ID)).thenReturn(CATEGORY);
        userInterestedCategoriesTest.add(CATEGORY);
        when(session.getAttribute(USER_DETAILS)).thenReturn(user);
        when(session.getAttribute("userInterestedCategories")).thenReturn(new ArrayList<Category>());
        when(
                userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(
                        VALID_USER_ID,
                        userInterestedCategoriesTest,
                        MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                        MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED)).thenReturn(LIST_OF_RECOMMENDED_RESOURCES);
        homeController.filterRecommendedResources(VALID_CATEGORY_ID);
        when(categoryManager.getCategoryById(2)).thenReturn(newCategory);
        userInterestedCategoriesTest.add(newCategory);
        homeController.filterRecommendedResources(2);
        userInterestedCategoriesTest.remove(CATEGORY);
        when(categoryManager.getCategoryById(3)).thenReturn(thirdCategory);
        userInterestedCategoriesTest.add(thirdCategory);
        homeController.filterRecommendedResources(3);
        when(
                userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(
                        VALID_USER_ID,
                        userInterestedCategoriesTest,
                        MINIMUM_RATING_REQUIRED_FOR_RESOURCE_TO_BE_RANKED_HIGHER_THAN_NEW_RESOURCES,
                        MINIMUM_NUMBER_OF_RATINGS_BEFORE_RESOURCE_IS_RANKED)).thenReturn(LIST_OF_RECOMMENDED_RESOURCES);
        assertEquals(LIST_OF_RECOMMENDED_RESOURCES, homeController.filterRecommendedResources(VALID_CATEGORY_ID));
    }
}
