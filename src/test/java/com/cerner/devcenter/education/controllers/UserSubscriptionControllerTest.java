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
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import com.cerner.devcenter.education.exceptions.DuplicateUserSubscriptionException;
import com.cerner.devcenter.education.managers.CategoryManager;
import com.cerner.devcenter.education.managers.UserSubscriptionManager;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.UserSubscription;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.TestConstants;

/**
 * Test class for {@link UserSubscriptionController}
 *
 * @author Mani Teja Kurapati (MK051340)
 */
@RunWith(MockitoJUnitRunner.class)
public class UserSubscriptionControllerTest {

    private static final String CATEGORY_SUBSCRIPTION_SUCCESSFUL_MESSAGE = "subscriptionPage.label.categorySuccessfullySubscribed";
    private static final String DUPLICATE_ADD_SUBSCRIPTION_MESSAGE = "subscriptionPage.label.categoryAlreadySubscribed";
    private static final String BINDING_RESULT_ERROR_MESSAGE = "com.cerner.devcenter.education.controllers.bindingResultError";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String SUCCESS_MESSAGE = "successMessage";
    private static final String VIEW_NAME = "user_subscription";
    private static final String USER_DETAILS = "userDetails";
    private static final String VALID_USER_ID = "MK051340";
    private static final int VALID_CATEGORY_ID = 1;
    private static final String TEST = "test";

    private static ResourceBundle i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @InjectMocks
    private UserSubscriptionController userSubscriptionController;
    @Mock
    private HttpSession session;
    @Mock
    private CategoryManager categoryManager;
    @Mock
    private BindingResult result;
    @Mock
    private HttpServletRequest request;
    @Mock
    private UserSubscriptionManager userSubscriptionManager;
    @Mock
    private UserSubscription mockUserSubscription;
    private AnonymousAuthenticationToken token;
    private List<GrantedAuthority> grantedAuths;
    private UserProfileDetails user;
    private Category category;
    private List<Category> listOfCategories;
    private List<Category> subscribedCategories;
    private final ModelAndView modelView = new ModelAndView();

    /**
     * Initializes the Mocked objects, sets up invalid login credentials, and
     * creates a test user to be used for the
     * {@link UserSubscriptionControllerTest}.
     */
    @Before
    public void setUp() {
        grantedAuths = new ArrayList<GrantedAuthority>();
        grantedAuths.add(new SimpleGrantedAuthority("dummy"));
        token = new AnonymousAuthenticationToken("abc", "abc", grantedAuths);
        listOfCategories = new ArrayList<Category>();
        subscribedCategories = new ArrayList<Category>();
        category = new Category(10, "Security", "Learn the basics of security practices.");
        listOfCategories.add(category);
        subscribedCategories.add(category);
        user = new UserProfileDetails("Name,Name", "Role", "TestId", "Email", "DevCenter", "Manager", "Project");
        when(session.getAttribute(USER_DETAILS)).thenReturn(user);
        when(categoryManager.getAllCategories()).thenReturn(listOfCategories);
        when(mockUserSubscription.getUserId()).thenReturn(VALID_USER_ID);
        when(mockUserSubscription.getCategoryId()).thenReturn(VALID_CATEGORY_ID);
    }

    /**
     * Test for mySubscriptions page when user is still not logged in
     */
    @Test
    public void testmySubscriptionsWhenNotLoggedIn() {
        SecurityContextHolder.getContext().setAuthentication(token);
        final ModelAndView testModelView = userSubscriptionController.mySubscriptions(mockUserSubscription, session);
        assertEquals(testModelView.getViewName(), TestConstants.REDIRECT_LOGIN);
    }

    /**
     * Test when user tries to access the mySubscriptions page while logged in
     */
    @Test
    public void testmySubscriptionsWhenUserIsLoggedIn() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(TEST, TEST, grantedAuths));
        when(session.getAttribute(USER_DETAILS)).thenReturn(user);
        when(categoryManager.getAllCategories()).thenReturn(listOfCategories);
        when(userSubscriptionManager.getSubscribedcategoriesByUser(user.getUserId())).thenReturn(subscribedCategories);
        final ModelAndView testModelView = userSubscriptionController.mySubscriptions(mockUserSubscription, session);
        assertTrue(testModelView.getModel().containsValue(listOfCategories));
        assertTrue(testModelView.getModel().containsValue(subscribedCategories));
    }

    /**
     * Test
     * {@link UserSubscriptionController#addSubscription(UserSubscription, BindingResult, HttpSession, ModelAndView)}
     * when user is not logged in. Must redirect to login page.
     */
    @Test
    public void testAddSubscriptionWhenNotLoggedIn() {
        SecurityContextHolder.getContext().setAuthentication(token);
        final ModelAndView testModelView = userSubscriptionController.addSubscription(mockUserSubscription, result,
                session, modelView);
        assertEquals(testModelView.getViewName(), TestConstants.REDIRECT_LOGIN);
    }

    /**
     * Test
     * {@link UserSubscriptionController#addSubscription(UserSubscription, BindingResult, HttpSession, ModelAndView)}
     * when we have Binding errors.
     */
    @Test
    public void testAddSubscriptionWhenBindingErrors() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(TEST, TEST, grantedAuths));
        when(result.hasErrors()).thenReturn(true);
        assertEquals(i18nBundle.getString(BINDING_RESULT_ERROR_MESSAGE), userSubscriptionController
                .addSubscription(mockUserSubscription, result, session, modelView).getModel().get(ERROR_MESSAGE));
    }

    /**
     * Test
     * {@link UserSubscriptionController#addSubscription(UserSubscription, BindingResult, HttpSession, ModelAndView)}
     * when {@link UserSubscription} is null. Expects
     * {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddSubscriptionWhenUserSubscriptionIsNull() {
        userSubscriptionController.addSubscription(null, result, session, modelView);
    }

    /**
     * Test
     * {@link UserSubscriptionController#addSubscription(UserSubscription, BindingResult, HttpSession, ModelAndView)}
     * when {@link HttpSession} is null. Expects
     * {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddSubscriptionWhenHttpSessionIsNull() {
        userSubscriptionController.addSubscription(mockUserSubscription, result, null, modelView);
    }

    /**
     * Test
     * {@link UserSubscriptionController#addSubscription(UserSubscription, BindingResult, HttpSession, ModelAndView)}
     * when {@link ModelAndView} is null. Expects
     * {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddSubscriptionWhenModelAndViewIsNull() {
        userSubscriptionController.addSubscription(mockUserSubscription, result, session, null);
    }

    /**
     * Test
     * {@link UserSubscriptionController#addSubscription(UserSubscription, BindingResult, HttpSession, ModelAndView)}
     * with all valid inputs.
     */
    @Test
    public void testAddSubscriptionWithValidInputs() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(TEST, TEST, grantedAuths));

        when(session.getAttribute(USER_DETAILS)).thenReturn(user);
        when(categoryManager.getAllCategories()).thenReturn(listOfCategories);
        when(userSubscriptionManager.getSubscribedcategoriesByUser(user.getUserId())).thenReturn(subscribedCategories);
        final ModelAndView testModelView = userSubscriptionController.addSubscription(mockUserSubscription, result,
                session, modelView);
        assertTrue(testModelView.getModel().containsValue(listOfCategories));
        assertTrue(testModelView.getModel().containsValue(subscribedCategories));
    }

    /**
     * Test
     * {@link UserSubscriptionController#addSubscription(UserSubscription, BindingResult, HttpSession, ModelAndView)}
     * to display valid message after input is successful with all valid inputs.
     */
    @Test
    public void testWhenAddSubscriptionSuccessful() throws DuplicateUserSubscriptionException {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(TEST, TEST, grantedAuths));
        when(userSubscriptionManager.addUserSubscription(mockUserSubscription)).thenReturn(true);
        final ModelAndView testModelView = userSubscriptionController.addSubscription(mockUserSubscription, result,
                session, modelView);
        assertEquals(i18nBundle.getString(CATEGORY_SUBSCRIPTION_SUCCESSFUL_MESSAGE),
                testModelView.getModel().get(SUCCESS_MESSAGE));
        assertEquals(VIEW_NAME, testModelView.getViewName());
    }

    /**
     * Test
     * {@link UserSubscriptionController#addSubscription(UserSubscription, BindingResult, HttpSession, ModelAndView)}
     * to display message notifying the user that he has already subscribed to
     * category
     */
    @Test
    public void testAddSubscriptionWhenSubscriptionAlreadyExists() throws DuplicateUserSubscriptionException {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(TEST, TEST, grantedAuths));
        doThrow(DuplicateUserSubscriptionException.class).when(userSubscriptionManager)
                .addUserSubscription(mockUserSubscription);
        final ModelAndView testModelView = userSubscriptionController.addSubscription(mockUserSubscription, result,
                session, modelView);
        assertEquals(i18nBundle.getString(DUPLICATE_ADD_SUBSCRIPTION_MESSAGE),
                testModelView.getModel().get(ERROR_MESSAGE));
        assertEquals(VIEW_NAME, testModelView.getViewName());
    }

    /**
     * Test
     * {@link UserSubscriptionController#deleteSubscriptionBasedOnCategoryId(String, HttpSession)}
     * when categoryId is null. Expects {@link IllegalArgumentException}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteSubscriptionBasedOnCategoryIdWhenCategoryIdIsNull() {
        userSubscriptionController.deleteSubscriptionBasedOnCategoryId(null, session);
    }

    /**
     * Test
     * {@link UserSubscriptionController#deleteSubscriptionBasedOnCategoryId(String, HttpSession)}
     * when categoryId is empty. Expects {@link IllegalArgumentException}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteSubscriptionBasedOnCategoryIdWhenCategoryIdIsEmpty() {
        userSubscriptionController.deleteSubscriptionBasedOnCategoryId("", session);
    }

    /**
     * Test
     * {@link UserSubscriptionController#deleteSubscriptionBasedOnCategoryId(String, HttpSession)}
     * when categoryId is not greater than zero. Expects
     * {@link IllegalArgumentException}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteSubscriptionBasedOnCategoryIdWhenCategoryIdIsNotGreaterThanZero() {
        userSubscriptionController.deleteSubscriptionBasedOnCategoryId("-1", session);
    }

    /**
     * Test
     * {@link UserSubscriptionController#deleteSubscriptionBasedOnCategoryId(String, HttpSession)}
     * when {@link HttpSession} object is null. Expects
     * {@link IllegalArgumentException}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteSubscriptionBasedOnCategoryIdWhenSessionIsNull() {
        userSubscriptionController.deleteSubscriptionBasedOnCategoryId("1", null);
    }

    /**
     * Test
     * {@link UserSubscriptionController#deleteSubscriptionBasedOnCategoryId(String, HttpSession)}
     * when valid parameters are provided.
     */
    @Test
    public void testDeleteSubscriptionBasedOnCategoryId() {
        when(session.getAttribute(USER_DETAILS)).thenReturn(user);
        when(userSubscriptionManager.deleteUserSubscription(any(UserSubscription.class))).thenReturn(true);
        assertTrue(userSubscriptionController.deleteSubscriptionBasedOnCategoryId("1", session));
    }
}