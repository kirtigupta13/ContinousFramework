package com.cerner.devcenter.education.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.managers.UserResourceRatingManager;
import com.cerner.devcenter.education.models.UserResourceRating;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthenticationStatusUtil;

/**
 * Test class for UserResourceRatingController
 * 
 * @author Asim Mohammed (045300)
 */
@RunWith(MockitoJUnitRunner.class)
public class UserResourceRatingControllerTest {

	@InjectMocks
	private UserResourceRatingController userResourceRatingController;

	@Mock
	private AuthenticationStatusUtil loginStatus;
	@Mock
	private UserResourceRatingManager userResourceRatingManager;
	@Mock
	private UserProfileDetails user;
	@Mock
	private MockHttpSession session;
	@Mock
	private MockHttpServletRequest request;

	private static final String REDIRECT_LOGIN = "redirect:/login";
	private static final String USERDETAILS = "userDetails";
	private static final String REDIRECT_HOMEPAGE = "redirect:/app/home_page";
	private static final String RATING_LIKE = "1";
	private static final String RATING_DISLIKE = "0";
	private static final String VALID_USERID = "AM045300";
	private static final String VALID_RESOURCEID = "290";
	private static final String ALPA_NUMERIC_VALUE = "ab290";
	private static final String NEGATIVE_VALUE = "-290";
	private static final String WHITESPACE = "  ";
	private static final String EMPTY = "";

	private static ResourceBundle i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

	/**
	 * Initialize the test variables
	 * 
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	@Before
	public void setup() throws IllegalArgumentException, IllegalAccessException {
		when((UserProfileDetails) session.getAttribute(USERDETAILS)).thenReturn(user);
		when(user.getUserId()).thenReturn(VALID_USERID);
		when(request.getParameter("resourceId")).thenReturn(VALID_RESOURCEID);
		when(request.getParameter("rating")).thenReturn(RATING_LIKE);
		when(loginStatus.isLoggedIn()).thenReturn(true);
	}

	@After
	public void tearDown() {
		userResourceRatingController = null;
		userResourceRatingManager = null;
		loginStatus = null;
		user = null;
		session = null;
		request = null;
	}

	/**
	 * This tests
	 * {@link UserResourceRatingController#addResourceDetails(HttpSession, HttpServletRequest)}
	 * , expects {@link IllegalArgumentException} when {@link HttpSession}
	 * object is null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddResourceDetailsWhenSessionIsNull() {
		userResourceRatingController.addResourceDetails(null, request);
	}

	/**
	 * This tests
	 * {@link UserResourceRatingController#addResourceDetails(HttpSession, HttpServletRequest)}
	 * , expects {@link IllegalArgumentException} when
	 * {@link HttpServletRequest} object is null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddResourceDetailsWhenRequestIsNull() {
		userResourceRatingController.addResourceDetails(session, null);
	}

	/**
	 * This tests
	 * {@link UserResourceRatingController#addResourceDetails(HttpSession, HttpServletRequest)}
	 * , expects {@link NullPointerException} when {@link String} request
	 * parameter resource id is null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddResourceDetailsWhenRequestParamResourceIdIsNull() {
		when(request.getParameter("resourceId")).thenReturn(null);
		userResourceRatingController.addResourceDetails(session, request);
	}

	/**
	 * This tests
	 * {@link UserResourceRatingController#addResourceDetails(HttpSession, HttpServletRequest)}
	 * , expects {@link NullPointerException} when {@link String} request
	 * parameter resource id is alpha-numeric.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddResourceDetailsWhenRequestParamResourceIdIsAlphaNumeric() {
		when(request.getParameter("resourceId")).thenReturn(ALPA_NUMERIC_VALUE);
		userResourceRatingController.addResourceDetails(session, request);
	}

	/**
	 * This tests
	 * {@link UserResourceRatingController#addResourceDetails(HttpSession, HttpServletRequest)}
	 * , expects {@link NullPointerException} when {@link String} request
	 * parameter resource id is negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddResourceDetailsWhenRequestParamResourceIdIsNegative() {
		when(request.getParameter("resourceId")).thenReturn(NEGATIVE_VALUE);
		userResourceRatingController.addResourceDetails(session, request);
	}

	/**
	 * This tests
	 * {@link UserResourceRatingController#addResourceDetails(HttpSession, HttpServletRequest)}
	 * , expects {@link NullPointerException} when {@link String} request
	 * parameter resource id is whitespace only.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddResourceDetailsWhenRequestParamResourceIdIsWhitespace() {
		when(request.getParameter("resourceId")).thenReturn(WHITESPACE);
		userResourceRatingController.addResourceDetails(session, request);
	}

	/**
	 * This tests
	 * {@link UserResourceRatingController#addResourceDetails(HttpSession, HttpServletRequest)}
	 * , expects {@link NullPointerException} when {@link String} request
	 * parameter resource id is empty.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddResourceDetailsWhenRequestParamResourceIdIsEmpty() {
		when(request.getParameter("resourceId")).thenReturn(EMPTY);
		userResourceRatingController.addResourceDetails(session, request);
	}

	/**
	 * This tests
	 * {@link UserResourceRatingController#addResourceDetails(HttpSession, HttpServletRequest)}
	 * , expects {@link NullPointerException} when {@link String} request
	 * parameter rating is null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddResourceDetailsWhenRequestParamRatingIsNull() {
		when(request.getParameter("rating")).thenReturn(null);
		userResourceRatingController.addResourceDetails(session, request);
	}

	/**
	 * This tests
	 * {@link UserResourceRatingController#addResourceDetails(HttpSession, HttpServletRequest)}
	 * , expects {@link NullPointerException} when {@link String} request
	 * parameter rating is alpha-numeric.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddResourceDetailsWhenRequestParamRatingIsAlphaNumeric() {
		when(request.getParameter("rating")).thenReturn(ALPA_NUMERIC_VALUE);
		userResourceRatingController.addResourceDetails(session, request);
	}

	/**
	 * This tests
	 * {@link UserResourceRatingController#addResourceDetails(HttpSession, HttpServletRequest)}
	 * , expects {@link NullPointerException} when {@link String} request
	 * parameter rating is alpha-numeric.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddResourceDetailsWhenRequestParamRatingIsNegative() {
		when(request.getParameter("rating")).thenReturn(NEGATIVE_VALUE);
		userResourceRatingController.addResourceDetails(session, request);
	}

	/**
	 * This tests
	 * {@link UserResourceRatingController#addResourceDetails(HttpSession, HttpServletRequest)}
	 * , expects {@link NullPointerException} when {@link String} request
	 * parameter rating is whitespace only.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddResourceDetailsWhenRequestParamRatingIsWhitespace() {
		when(request.getParameter("rating")).thenReturn(WHITESPACE);
		userResourceRatingController.addResourceDetails(session, request);
	}

	/**
	 * This tests
	 * {@link UserResourceRatingController#addResourceDetails(HttpSession, HttpServletRequest)}
	 * , expects {@link NullPointerException} when {@link String} request
	 * parameter rating is empty value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddResourceDetailsWhenRequestParamRatingIsEmpty() {
		when(request.getParameter("rating")).thenReturn(EMPTY);
		userResourceRatingController.addResourceDetails(session, request);
	}

	/**
	 * This tests
	 * {@link UserResourceRatingController#addResourceDetails(HttpSession, HttpServletRequest)}
	 * , user when not logged in returns redirect link to login
	 */
	@Test
	public void testAddResourceStatusFeedbackWhenUserNotLoggedIn() {
		when(loginStatus.isLoggedIn()).thenReturn(false);
		ModelAndView view = userResourceRatingController.addResourceDetails(session, request);
		assertEquals(REDIRECT_LOGIN, view.getViewName());
	}

	/**
	 * This tests
	 * {@link UserResourceRatingController#addResourceDetails(HttpSession, HttpServletRequest)}
	 * , on returns redirect link to login view
	 */
	@Test
	public void testAddResourceStatusFeedbackWhenAddResourceInfoUnsuccessful() {
		ModelAndView view = userResourceRatingController.addResourceDetails(session, request);
		assertEquals(REDIRECT_HOMEPAGE, view.getViewName());
	}

	/**
	 * This tests
	 * {@link UserResourceRatingController#addResourceDetails(HttpSession, HttpServletRequest)}
	 * , returns the error message to the user
	 */
	@Test
	public void testAddResourceDetailsWhenAddResourceInfoSuccess() {
		when(userResourceRatingManager.addUserResourceRating(any(UserResourceRating.class)))
				.thenThrow(new ManagerException());
		ModelAndView view = userResourceRatingController.addResourceDetails(session, request);
		assertTrue(view.getModel().containsValue(i18nBundle
				.getString("com.cerner.devcenter.education.controllers.UserResourceController.statusUpdateError")));
	}

	/**
	 * This tests
	 * {@link UserResourceRatingController#addResourceDetails(HttpSession, HttpServletRequest)}
	 * , checks for success message to the user
	 */
	@Test
	public void testAddResourceStatusFeedbackWhenAddResourceSuccessMessage() {
		ModelAndView view = userResourceRatingController.addResourceDetails(session, request);
		assertTrue(view.getModel().containsValue(i18nBundle
				.getString("com.cerner.devcenter.education.controllers.UserResourceController.statusUpdateSuccess")));
	}

	/**
	 * This tests
	 * {@link UserResourceRatingController#addResourceDetails(HttpSession, HttpServletRequest)}
	 * , when rating is dislike
	 */
	@Test
	public void testAddResourceStatusFeedbackWhenRatingIsDislike() {
		when(request.getParameter("rating")).thenReturn(RATING_DISLIKE);
		ModelAndView view = userResourceRatingController.addResourceDetails(session, request);
		assertTrue(view.getModel().containsValue(i18nBundle
				.getString("com.cerner.devcenter.education.controllers.UserResourceController.statusUpdateSuccess")));
	}
}
