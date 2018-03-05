package com.cerner.devcenter.education.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import com.cerner.devcenter.education.exceptions.DuplicateUserInterestedCategoryException;
import com.cerner.devcenter.education.managers.CategoryManager;
import com.cerner.devcenter.education.managers.UserInterestedCategoryManager;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.UserInterestedCategory;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.TestConstants;

/**
 * Test class for {@link ProfileController}
 *
 * @author James Kellerman (JK042311)
 * @author Anudeep Kumar Gadam (AG045334)
 * @author Vincent Dasari (VD049645)
 * @author Santosh Kumar (SK051343)
 **/

@RunWith(MockitoJUnitRunner.class)
public class ProfileControllerTest {

    private static final String ADD_CATEGORY_SUCCESSFUL_MESSAGE = "Your category has been added successfully.";
    private static final String DUPLICATE_ADD_CATEGORY_MESSAGE = "You have already marked this category as interested.";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String SUCCESS_MESSAGE = "successMessage";
    private static final String VIEW_NAME = "profile";
    private static final String DELETE_USER_INTERESTED_CATEGORY_SUCCESSFUL_MESSAGE = "Your interested category deleted successfully.";
    private static final String FORWARD_VIEW_NAME = "forward:myProfile";
    private static final String USER_DETAILS = "userDetails";
    private static final String SKILL_LEVEL = "skillLevel";
    private static final String INTEREST_LEVEL = "interestLevel";
    private static final String CATEGORY_ID = "categoryId";
    private static final String BINDING_RESULT_ERROR_MESSAGE = "com.cerner.devcenter.education.controllers.bindingResultError";
    private static final String INTERSTED_CATEGORY_NOT_DELETED = "profilePage.label.userInterestedCategoryNotDeleted";
    private static final String INTERSTED_CATEGORY_DELETED = "profilePage.label.userInterestedCategoryDeleted";
    private static final String INTERESTED_CATEGORY_SELECTION = "profile.userInterestedCategory.selection";
    private static final String PROFILE_PAGE_ERROR = "profilePage.label.categoryNotAdded";
    private static final String TEST = "test";

    private static ResourceBundle i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @InjectMocks
    private ProfileController profileController;
    @Mock
    private HttpSession session;
    @Mock
    private CategoryManager categoryManager;
    @Mock
    private BindingResult result;
    @Mock
    private HttpServletRequest request;
    @Mock
    private UserInterestedCategoryManager userInterestedCategoryManager;
    @Mock
    private UserInterestedCategory userInterestedCategoryMock;
    private AnonymousAuthenticationToken token;
    private List<GrantedAuthority> grantedAuths;
    private UserProfileDetails user;
    private Category category;
    private List<Category> listOfCategories;
    private List<UserInterestedCategory> listOfUserInterestedCategories;
    private UserInterestedCategory userInterestedCategory;
    private ModelAndView modelView = new ModelAndView();
    private int[] categoryIds;

    /**
     * Initializes the Mocked objects, sets up invalid login credentials, and
     * creates a test user to be used for the {@link ProfileControllerTest}.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        grantedAuths = new ArrayList<GrantedAuthority>();
        listOfCategories = new ArrayList<Category>();
        listOfUserInterestedCategories = new ArrayList<UserInterestedCategory>();
        grantedAuths.add(new SimpleGrantedAuthority("dummy"));
        token = new AnonymousAuthenticationToken("abc", "abc", grantedAuths);
        category = new Category(10, "Security", "Learn the basics of security practices.");
        listOfUserInterestedCategories.add(userInterestedCategory);
        listOfCategories.add(category);
        user = new UserProfileDetails("Name,Name", "Role", "TestId", "Email", "DevCenter", "Manager", "Project");
        userInterestedCategory = new UserInterestedCategory(user.getUserId(), category, 4, 3);
        categoryIds = new int[2];
        categoryIds[0] = 1;
        categoryIds[1] = 2;
        when(session.getAttribute(USER_DETAILS)).thenReturn(user);
        when(categoryManager.getAllCategories()).thenReturn(listOfCategories);
        when(request.getParameter(SKILL_LEVEL)).thenReturn("4");
        when(request.getParameter(INTEREST_LEVEL)).thenReturn("3");
        when(request.getParameter(CATEGORY_ID)).thenReturn("10");
    }

    /**
     * Test for myProfile page when user is still not logged in
     */
    @Test
    public void testmyProfileWhenNotLoggedIn() {
        SecurityContextHolder.getContext().setAuthentication(token);
        ModelAndView testModelView = profileController.profile(userInterestedCategory, session);
        assertEquals(testModelView.getViewName(), TestConstants.REDIRECT_LOGIN);
    }

    /**
     * Test when user tries to access the profile page while logged in
     */
    @Test
    public void testmyProfileWhenUserIsLoggedIn() {
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(TEST, TEST, grantedAuths));
        when(session.getAttribute(USER_DETAILS)).thenReturn(user);
        when(categoryManager.getAllCategories()).thenReturn(listOfCategories);
        ModelAndView testModelView = profileController.profile(userInterestedCategory, session);
        assertTrue(testModelView.getModel().containsValue(user));
        assertTrue(testModelView.getModel().containsValue(listOfCategories));
        assertTrue(testModelView.getModel().containsValue(userInterestedCategory));
    }

    /**
     * Test
     * {@link ProfileController#addInterestedCategory(UserInterestedCategory, BindingResult, HttpSession, ModelAndView)}
     * when user is not logged in. Must redirect to login page.
     * 
     * @throws DuplicateUserInterestedTopicException
     *             when the category for the user has already been added to the
     *             database
     */
    @Test
    public void testAddInterestedCategoryWhenNotLoggedIn() {
        SecurityContextHolder.getContext().setAuthentication(token);
        ModelAndView testModelView = profileController
                .addInterestedCategory(userInterestedCategory, result, session, modelView);
        assertEquals(testModelView.getViewName(), TestConstants.REDIRECT_LOGIN);
    }

    /**
     * Test
     * {@link ProfileController#addInterestedCategory(UserInterestedCategory, BindingResult, HttpSession, ModelAndView)}
     * when we have Binding errors.
     * 
     * @throws DuplicateUserInterestedTopicException
     *             when the category for the user has already been added to the
     *             database
     */
    @Test
    public void testAddInterestedCategoriesWhenBindingErrors() {
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(TEST, TEST, grantedAuths));
        when(result.hasErrors()).thenReturn(true);
        assertEquals(
                i18nBundle.getString(BINDING_RESULT_ERROR_MESSAGE),
                profileController
                        .addInterestedCategory(userInterestedCategory, result, session, modelView)
                        .getModel()
                        .get(ERROR_MESSAGE));
    }

    /**
     * Test
     * {@link ProfileController#addInterestedCategory(UserInterestedCategory, BindingResult, HttpSession, ModelAndView)}
     * when {@link UserInterestedCategory} is null. Expects
     * {@link IllegalArgumentException}.
     * 
     * @throws DuplicateUserInterestedTopicException
     *             when the category for the user has already been added to the
     *             database
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddInterestedCategoryWhenUserInterestedCategoryIsNull() {
        profileController.addInterestedCategory(null, result, session, modelView);
    }

    /**
     * Test
     * {@link ProfileController#addInterestedCategory(UserInterestedCategory, BindingResult, HttpSession, ModelAndView)}
     * when {@link HttpSession} is null. Expects
     * {@link IllegalArgumentException}.
     * 
     * @throws DuplicateUserInterestedTopicException
     *             when the category for the user has already been added to the
     *             database
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddInterestedCategoryWhenHttpSessionIsNull() {
        profileController.addInterestedCategory(userInterestedCategory, result, null, modelView);
    }

    /**
     * Test
     * {@link ProfileController#addInterestedCategory(UserInterestedCategory, BindingResult, HttpSession, ModelAndView)}
     * when {@link ModelAndView} is null. Expects
     * {@link IllegalArgumentException}.
     * 
     * @throws DuplicateUserInterestedTopicException
     *             when the category for the user has already been added to the
     *             database
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddInterestedCategoryWhenModelAndViewIsNull() {
        profileController.addInterestedCategory(userInterestedCategory, result, session, null);
    }

    /**
     * Test
     * {@link ProfileController#addInterestedCategory(UserInterestedCategory, BindingResult, HttpSession, ModelAndView)}
     * with all valid inputs.
     * 
     * @throws DuplicateUserInterestedTopicException
     *             when the category for the user has already been added to the
     *             database
     */
    @Test
    public void testAddInterestedCategoryWithValidInputs() {
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(TEST, TEST, grantedAuths));
        when(session.getAttribute(USER_DETAILS)).thenReturn(user);
        when(categoryManager.getAllCategories()).thenReturn(listOfCategories);
        ModelAndView testModelView = profileController
                .addInterestedCategory(userInterestedCategory, result, session, modelView);
        assertTrue(testModelView.getModel().containsValue(user));
        assertTrue(testModelView.getModel().containsValue(listOfCategories));
        assertTrue(testModelView.getModel().containsValue(userInterestedCategory));
    }

    /**
     * Test
     * {@link ProfileController#addInterestedCategory(UserInterestedCategory, BindingResult, HttpSession, ModelAndView)}
     * to display valid message after input is successful with all valid inputs.
     * 
     * @throws DuplicateUserInterestedTopicException
     *             when the category for the user has already been added to the
     *             database
     */
    @Test
    public void testWhenAddInterestedCategorySuccessful() throws DuplicateUserInterestedCategoryException {
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(TEST, TEST, grantedAuths));
        when(userInterestedCategoryManager.addUserInterestedCategory(userInterestedCategory)).thenReturn(true);
        ModelAndView testModelView = profileController
                .addInterestedCategory(userInterestedCategory, result, session, modelView);
        assertEquals(ADD_CATEGORY_SUCCESSFUL_MESSAGE, testModelView.getModel().get(SUCCESS_MESSAGE));
        assertEquals(VIEW_NAME, testModelView.getViewName());
    }

    /**
     * Test
     * {@link ProfileController#addInterestedCategory(UserInterestedCategory, BindingResult, HttpSession, ModelAndView)}
     * to display message notifying the user that the category has already been
     * added as interested.
     * 
     * @throws DuplicateUserInterestedTopicException
     *             when the category for the user has already been added to the
     *             database
     */
    @Test
    public void testAddInterestedCategoryWhenCategoryAlreadyExists() throws DuplicateUserInterestedCategoryException {
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(TEST, TEST, grantedAuths));
        doThrow(DuplicateUserInterestedCategoryException.class)
                .when(userInterestedCategoryManager)
                .addUserInterestedCategory(userInterestedCategory);
        ModelAndView testModelView = profileController
                .addInterestedCategory(userInterestedCategory, result, session, modelView);
        assertEquals(DUPLICATE_ADD_CATEGORY_MESSAGE, testModelView.getModel().get(ERROR_MESSAGE));
        assertEquals(VIEW_NAME, testModelView.getViewName());
    }

    /**
     * Tests
     * {@link ProfileController#deleteUserInterestedCategory(UserInterestedCategory, BindingResult, HttpSession, ModelAndView)}
     * when the {@link Category} that's being passed in
     * {@link UserInterestedCategory} is <code>null</code>.
     */
    @Test
    public void testAddInterestedCategoryWhenCategoryNull() {
        when(userInterestedCategoryMock.getCategory()).thenReturn(null);
        ModelAndView testModelView = profileController
                .addInterestedCategory(userInterestedCategoryMock, result, session, modelView);
        assertEquals(VIEW_NAME, testModelView.getViewName());
        assertEquals(i18nBundle.getString(PROFILE_PAGE_ERROR), testModelView.getModel().get(ERROR_MESSAGE));
    }

    /**
     * Test
     * {@link ProfileController#deleteUserInterestedCategory(UserInterestedCategory, BindingResult, HttpSession, ModelAndView)}
     * when user is not logged in. Must redirect to login page.
     * 
     * @throws DuplicateUserInterestedTopicException
     *             when the category for the user has already been added to the
     *             database
     */
    @Test
    public void testDeleteUserInterestedCategoryWhenNotLoggedIn() {
        SecurityContextHolder.getContext().setAuthentication(token);
        ModelAndView testModelView = profileController
                .addInterestedCategory(userInterestedCategory, result, session, modelView);
        assertEquals(testModelView.getViewName(), TestConstants.REDIRECT_LOGIN);
    }

    /**
     * Test
     * {@link ProfileController#deleteUserInterestedCategory(UserInterestedCategory, BindingResult, HttpSession, ModelAndView)}
     * when we have Binding errors.
     * 
     * @throws DuplicateUserInterestedTopicException
     *             when the category for the user has already been added to the
     *             database
     */
    @Test
    public void testDeleteUserInterestedCategoryWhenBindingErrors() {
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(TEST, TEST, grantedAuths));
        when(result.hasErrors()).thenReturn(true);
        assertEquals(
                i18nBundle.getString(BINDING_RESULT_ERROR_MESSAGE),
                profileController
                        .addInterestedCategory(userInterestedCategory, result, session, modelView)
                        .getModel()
                        .get(ERROR_MESSAGE));
    }

    /**
     * Test
     * {@link ProfileController#updateUserInterestedCategory(HttpSession, HttpServletRequest, ModelAndView)}
     * when the {@link HttpSession} object is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateInterestedCategoryWhenSessionIsNull() {
        profileController.updateUserInterestedCategory(null, request, modelView);
    }

    /**
     * Test
     * {@link ProfileController#deleteUserInterestedCategory(UserInterestedCategory, BindingResult, HttpSession, ModelAndView)}
     * when {@link UserInterestedCategory} is null. Expects
     * {@link IllegalArgumentException}.
     * 
     * @throws DuplicateUserInterestedTopicException
     *             when the category for the user has already been added to the
     *             database
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteUserInterestedCategoryWhenUserInterestedCategoryIsNull() {
        profileController.addInterestedCategory(null, result, session, modelView);
    }

    /**
     * Test
     * {@link ProfileController#deleteUserInterestedCategory(UserInterestedCategory, BindingResult, HttpSession, ModelAndView)}
     * when {@link HttpSession} is null. Expects
     * {@link IllegalArgumentException}.
     * 
     * @throws DuplicateUserInterestedTopicException
     *             when the category for the user has already been added to the
     *             database
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteUserInterestedCategoryWhenHttpSessionIsNull() {
        profileController.addInterestedCategory(userInterestedCategory, result, null, modelView);
    }

    /**
     * Test
     * {@link ProfileController#deleteUserInterestedCategory(UserInterestedCategory, BindingResult, HttpSession, ModelAndView)}
     * when {@link ModelAndView} is null. Expects
     * {@link IllegalArgumentException}.
     * 
     * @throws DuplicateUserInterestedTopicException
     *             when the category for the user has already been added to the
     *             database
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteUserInterestedCategoryWhenModelAndViewIsNull() {
        profileController.addInterestedCategory(userInterestedCategory, result, session, null);
    }

    /**
     * Test
     * {@link ProfileController#deleteUserInterestedCategory(UserInterestedCategory, BindingResult, HttpSession, ModelAndView)}
     * with all valid inputs.
     * 
     * @throws DuplicateUserInterestedTopicException
     *             when the category for the user has already been added to the
     *             database
     */
    @Test
    public void testDeleteUserInterestedCategoriesWithValidInputs() {
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(TEST, TEST, grantedAuths));
        when(session.getAttribute(USER_DETAILS)).thenReturn(user);
        when(categoryManager.getAllCategories()).thenReturn(listOfCategories);
        ModelAndView testModelView = profileController
                .addInterestedCategory(userInterestedCategory, result, session, modelView);
        assertTrue(testModelView.getModel().containsValue(user));
        assertTrue(testModelView.getModel().containsValue(listOfCategories));
    }

    /**
     * Test
     * {@link ProfileController#deleteUserInterestedCategory(UserInterestedCategory, BindingResult, HttpSession, ModelAndView)}
     * to display valid message after input is successful with all valid inputs.
     */
    @Test
    public void testWhenDeleteUserInterestedCategorySuccessful() {
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(TEST, TEST, grantedAuths));
        when(userInterestedCategoryManager.deleteUserInterestedCategory(any(UserInterestedCategory.class)))
                .thenReturn(true);
        ModelAndView testModelView = profileController
                .deleteUserInterestedCategory(userInterestedCategory, result, session, modelView);
        assertEquals(DELETE_USER_INTERESTED_CATEGORY_SUCCESSFUL_MESSAGE, testModelView.getModel().get(SUCCESS_MESSAGE));
        assertEquals(VIEW_NAME, testModelView.getViewName());
    }

    /**
     * {@link ProfileController#updateUserInterestedCategory(HttpSession, HttpServletRequest, ModelAndView)}
     * when the {@link HttpServletRequest} object is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateInterestedCategoryWhenRequestIsNull() {
        profileController.updateUserInterestedCategory(session, null, modelView);
    }

    /**
     * Test
     * {@link ProfileController#updateUserInterestedCategory(HttpSession, HttpServletRequest, ModelAndView)}
     * when the {@link ModelAndView} object is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateInterestedCategoryWhenModelIsNull() {
        profileController.updateUserInterestedCategory(session, request, null);
    }

    /**
     * Test
     * {@link ProfileController#updateUserInterestedCategory(HttpSession, HttpServletRequest, ModelAndView)}
     * will forward to the profile page once user is done editing.
     */
    @Test
    public void testForwardViewNameWhenUpdateInterestedCategory() {
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(TEST, TEST, grantedAuths));
        when(userInterestedCategoryManager.updateUserInterestedCategory(userInterestedCategory)).thenReturn(true);
        ModelAndView testModelView = profileController.updateUserInterestedCategory(session, request, modelView);
        assertEquals(FORWARD_VIEW_NAME, testModelView.getViewName());
    }

    /**
     * Test
     * {@link ProfileController#updateUserInterestedCategory(HttpSession, HttpServletRequest, ModelAndView)}
     * will return a model with error message when
     * {@link UserInterestedCategoryManager#updateUserInterestedCategory(UserInterestedCategory)}
     * has error updating the interested category.
     */
    @Test
    public void testErrorMessageWhenUpdateInterestedCategoryUnsuccessful() {
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(TEST, TEST, grantedAuths));
        when(
                userInterestedCategoryManager.updateUserInterestedCategory(
                        new UserInterestedCategory(user.getUserId(), new Category(10), 4, 3))).thenReturn(false);
        ModelAndView testModelView = profileController.updateUserInterestedCategory(session, request, modelView);
        assertEquals(
                i18nBundle.getString("profilePage.label.errorUpdateUserInterestedCategory"),
                testModelView.getModel().get(ERROR_MESSAGE));
    }

    /**
     * Test
     * {@link ProfileController#updateUserInterestedCategory(HttpSession, HttpServletRequest, ModelAndView)}
     * will return a model with success message when
     * {@link UserInterestedCategoryManager#updateUserInterestedCategory(UserInterestedCategory)}
     * successfully updates the interested category.
     */
    @Test
    public void testSuccessMessageWhenUpdateInterestedCategorySuccessful() {
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(TEST, TEST, grantedAuths));
        when(userInterestedCategoryManager.updateUserInterestedCategory(any(UserInterestedCategory.class)))
                .thenReturn(true);
        ModelAndView testModelView = profileController.updateUserInterestedCategory(session, request, modelView);
        assertEquals(
                i18nBundle.getString("profilePage.label.successUpdateUserInterestedCategory"),
                testModelView.getModel().get(SUCCESS_MESSAGE));
    }

    /**
     * Test
     * {@link ProfileController#batchDeleteUserInterestedCategories(HttpSession, int[], ModelAndView)}
     * when session is <code>null</code>
     */
    public void testBatchDeleteUserInterestedCategoriesWhenSessionIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        profileController.batchDeleteUserInterestedCategories(null, categoryIds, modelView);
    }

    /**
     * Test
     * {@link ProfileController#batchDeleteUserInterestedCategories(HttpSession, int[], ModelAndView)}
     * when model is <code>null</code>
     */
    @Test
    public void testBatchDeleteUserInterestedCategoriesWhenModelAndViewIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        profileController.batchDeleteUserInterestedCategories(session, categoryIds, null);
    }

    /**
     * Test
     * {@link ProfileController#batchDeleteUserInterestedCategories(HttpSession, int[], ModelAndView)}
     * when user is not logged in.
     */
    @Test
    public void testBatchDeleteUserInterestedCategoriesWhenNotLoggedIn() {
        SecurityContextHolder.getContext().setAuthentication(token);
        ModelAndView testModelView = profileController
                .batchDeleteUserInterestedCategories(session, categoryIds, modelView);
        assertEquals(testModelView.getViewName(), TestConstants.REDIRECT_LOGIN);
    }

    /**
     * Test
     * {@link ProfileController#batchDeleteUserInterestedCategories(HttpSession, int[], ModelAndView)}
     * with valid values returns correct view name.
     */
    @Test
    public void testForwardViewNameWhenBatchDeleteUserInterestedCategories() {
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(TEST, TEST, grantedAuths));
        when(userInterestedCategoryManager.deleteUserInterestedCategoriesInBatch(TEST, categoryIds)).thenReturn(true);
        ModelAndView testModelView = profileController
                .batchDeleteUserInterestedCategories(session, categoryIds, modelView);
        assertEquals(FORWARD_VIEW_NAME, testModelView.getViewName());
    }

    /**
     * Test
     * {@link ProfileController#batchDeleteUserInterestedCategories(HttpSession, int[], ModelAndView)}
     * returns correct error message.
     */
    @Test
    public void testErrorMessageWhenBatchDeleteUserInterestedCategoriesUnsuccessful() {
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(TEST, TEST, grantedAuths));
        when(userInterestedCategoryManager.deleteUserInterestedCategoriesInBatch(user.getUserId(), categoryIds))
                .thenReturn(false);
        ModelAndView testModelView = profileController
                .batchDeleteUserInterestedCategories(session, categoryIds, modelView);
        assertEquals(i18nBundle.getString(INTERSTED_CATEGORY_NOT_DELETED), testModelView.getModel().get(ERROR_MESSAGE));
    }

    /**
     * Test
     * {@link ProfileController#batchDeleteUserInterestedCategories(HttpSession, int[], ModelAndView)}
     * returns correct success message.
     */
    @Test
    public void testSuccessMessageWhenBatchDeleteUserInterestedCategoriesSuccessful() {
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(TEST, TEST, grantedAuths));
        when(userInterestedCategoryManager.deleteUserInterestedCategoriesInBatch(user.getUserId(), categoryIds))
                .thenReturn(true);
        ModelAndView testModelView = profileController
                .batchDeleteUserInterestedCategories(session, categoryIds, modelView);
        assertEquals(i18nBundle.getString(INTERSTED_CATEGORY_DELETED), testModelView.getModel().get(SUCCESS_MESSAGE));
    }

    /**
     * Test
     * {@link ProfileController#batchDeleteUserInterestedCategories(HttpSession, int[], ModelAndView)}
     * returns error message when categoryIds is null
     */
    @Test
    public void testErrorMessageWhenBatchDeleteArrayIsNull() {
        ModelAndView testModelView = profileController.batchDeleteUserInterestedCategories(session, null, modelView);
        assertEquals(i18nBundle.getString(INTERESTED_CATEGORY_SELECTION), testModelView.getModel().get(ERROR_MESSAGE));
    }

    /**
     * Test
     * {@link ProfileController#batchDeleteUserInterestedCategories(HttpSession, int[], ModelAndView)}
     * returns error message when categoryIds is empty
     */
    @Test
    public void testErrorMessageWhenBatchDeleteArrayIsEmpty() {
        categoryIds = new int[0];
        ModelAndView testModelView = profileController
                .batchDeleteUserInterestedCategories(session, categoryIds, modelView);
        assertEquals(i18nBundle.getString(INTERESTED_CATEGORY_SELECTION), testModelView.getModel().get(ERROR_MESSAGE));
    }
}
