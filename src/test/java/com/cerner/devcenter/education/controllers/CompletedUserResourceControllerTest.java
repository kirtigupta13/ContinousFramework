package com.cerner.devcenter.education.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.managers.CompletedUserResourceManager;
import com.cerner.devcenter.education.models.CompletedResource;
import com.cerner.devcenter.education.models.CompletedUserResource;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthenticationStatusUtil;
import com.cerner.devcenter.education.utils.CompletionRating;

/**
 * Tests the functionalities of {@link CompletedUserResourceController}
 *
 * @author Vinutha Nuchimaniyanda (VN046193)
 * @author Mayur Rajendran (MT049536)
 * @author Rishabh Bhojak (RB048032)
 * @author Vincent Dasari (VD049645)
 */
@RunWith(MockitoJUnitRunner.class)
public class CompletedUserResourceControllerTest {
    private static final String USER_DETAILS = "userDetails";
    private static final String REDIRECT_LOGIN = "redirect:/login";
    private static final String REDIRECT_HOMEPAGE = "forward:/app/home_page";
    private static final String COMPLETED_RESOURCE_PAGE = "completed_resource";
    private static final String MESSAGE = "message";
    private static final String VALID_USERID = "VN046193";
    private static final String VALID_RESOURCEID = "2";
    private static final String ALPHANUMERIC = "ab2c";
    private static final String SPECIAL_CHARACTER = "12&3";
    private static final String NEGATIVE_RESOURCE_ID = "-15";
    private static final String VALID_COMPLETION_RATING_SATISFIED = "3";
    private static final String WHITESPACE = "  ";
    private static final String EMPTY_STRING = "";
    private static final String RESOURCE_ID_EXCEPTION_MESSAGE = "ResourceId must be a numeric value";
    private static final String COMPLETION_RATING_EXCEPTION_MESSAGE = "Completion rating must be a numeric value";
    private static final String VALID_RESOURCE_NAME = "Java";
    private static final URL VALID_RESOURCE_LINK = createUrl("http://java.com");
    private static final int VALID_RESOURCE_ID = 2;
    private static final String COMPLETED_RESOURCES = "completedResources";
    private static final String NO_RECORDS_MESSAGE = "com.cerner.devcenter.education.controllers.CompletedUserResourceController.NoRecordsMessage";
    private static final String COMPLETED_RESOURCE_ERROR_MESSAGE = "com.cerner.devcenter.education.controllers.CompletedUserResourceController.ErrorMessage";
    private static final ResourceBundle I18N_BUNDLE = ResourceBundle.getBundle("i18n", Locale.getDefault());
    private static final String PARAM_RESOURCE_ID = "resource-id";
    private static final String PARAM_RATING = "rating";
    private static final String SUCCESS_MESSAGE = "CompletedUserResourceController.SuccessMessage";
    private static final String USER_NULL_ERROR_MESSAGE = "user cannot be null";
    private static final String ERROR_MESSAGE = "CompletedUserResourceController.ErrorMessage";
    private static final Date VALID_DISPLAY_DATE = new Date();
    private static final String HTTPSESSION_NOT_NULL = "HttpSession object cannot be null";
    private static final String HTTPSERVLET_NOT_NULL = "HttpServletRequest object cannot be null";
    private static final String RESOURCE_ID_ZERO = "ResourceId is equal to zero";
    private static final String ZERO = "0";
    private static final String SIXTY = "60";
    private static final String COMPLETION_RATING_NOT_VALID = "Completion rating is not valid";
    private static final String ERROR_QUERING_MANAGER_EXCEPTION = "Error quering completed resources for user: VN046193 with the exception: com.cerner.devcenter.education.admin.ManagerException";
    private static final String ERROR_ADDING_USER_EXCEPTION = "Error adding user rating and status for the resource: 2 with the exception: com.cerner.devcenter.education.admin.ManagerException";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @InjectMocks
    private CompletedUserResourceController completedUserResourceController;
    @Mock
    private CompletedUserResourceManager completedUserResourceManager;
    @Mock
    private AuthenticationStatusUtil loginStatus;
    @Mock
    private MockHttpSession httpSession;
    @Mock
    private MockHttpServletRequest httpServletRequest;
    @Mock
    private CompletedUserResource completedUserResource;
    @Mock
    private UserProfileDetails user;
    @Mock
    private ManagerException managerException;
    @Mock
    private Appender mockAppender;
    @Captor
    private ArgumentCaptor captorLoggingEvent;
    private ModelAndView modelAndView;

    @Before
    public void setup() {
        when((UserProfileDetails) httpSession.getAttribute(USER_DETAILS)).thenReturn(user);
        when(httpServletRequest.getParameter(PARAM_RESOURCE_ID)).thenReturn(VALID_RESOURCEID);
        when(httpServletRequest.getParameter(PARAM_RATING)).thenReturn(VALID_COMPLETION_RATING_SATISFIED);
        when(user.getUserId()).thenReturn(VALID_USERID);
        when(loginStatus.isLoggedIn()).thenReturn(true);
        LogManager.getRootLogger().addAppender(mockAppender);
    }

    @After
    public void tearDown() {
        LogManager.getRootLogger().removeAppender(mockAppender);
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#addCompletedUserResource(HttpSession, HttpServletRequest)}
     * expects {@link NullPointerException} when {@link HttpSession} object is
     * null.
     */
    @Test
    public void testAddCompletedResourceDetailsWhenSessionIsNull() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(HTTPSESSION_NOT_NULL);
        completedUserResourceController.addCompletedUserResource(null, httpServletRequest);
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#addCompletedUserResource(HttpSession, HttpServletRequest)}
     * expects {@link NullPointerException} when {@link HttpServletRequest}
     * object is null.
     */
    @Test
    public void testAddcompletedResourceDetailsWhenRequestIsNull() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(HTTPSERVLET_NOT_NULL);
        completedUserResourceController.addCompletedUserResource(httpSession, null);
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#addCompletedUserResource(HttpSession, HttpServletRequest)}
     * expects {@link IllegalArgumentException} when request parameter resource
     * id is null.
     */
    @Test
    public void testAddCompletedResourceDetailsWhenResourceIdIsNull() {
        when(httpServletRequest.getParameter(PARAM_RESOURCE_ID)).thenReturn(null);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_ID_EXCEPTION_MESSAGE);
        completedUserResourceController.addCompletedUserResource(httpSession, httpServletRequest);

    }

    /**
     * This tests
     * {@link CompletedUserResourceController#addCompletedUserResource(HttpSession, HttpServletRequest)}
     * expects {@link IllegalArgumentException} when request parameter resource
     * id is alphanumeric.
     */
    @Test
    public void testAddCompletedResourceDetailsWhenResourceIdIsAlphanumeric() {
        when(httpServletRequest.getParameter(PARAM_RESOURCE_ID)).thenReturn(ALPHANUMERIC);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_ID_EXCEPTION_MESSAGE);
        completedUserResourceController.addCompletedUserResource(httpSession, httpServletRequest);
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#addCompletedUserResource(HttpSession, HttpServletRequest)}
     * expects {@link IllegalArgumentException} when request parameter resource
     * id is whitespace.
     */
    @Test
    public void testAddCompletedResourceDetailsWhenResourceIdIsWhitespace() {
        when(httpServletRequest.getParameter(PARAM_RESOURCE_ID)).thenReturn(WHITESPACE);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_ID_EXCEPTION_MESSAGE);
        completedUserResourceController.addCompletedUserResource(httpSession, httpServletRequest);
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#addCompletedUserResource(HttpSession, HttpServletRequest)}
     * expects {@link IllegalArgumentException} when request parameter resource
     * id is a special character.
     */
    @Test
    public void testAddCompletedResourceDetailsWhenResourceIdIsSpecialCharacter() {
        when(httpServletRequest.getParameter(PARAM_RESOURCE_ID)).thenReturn(SPECIAL_CHARACTER);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_ID_EXCEPTION_MESSAGE);
        completedUserResourceController.addCompletedUserResource(httpSession, httpServletRequest);
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#addCompletedUserResource(HttpSession, HttpServletRequest)}
     * expects {@link IllegalArgumentException} when request parameter resource
     * id is Empty.
     */
    @Test
    public void testAddCompletedResourceDetailsWhenResourceIdIsEmpty() {
        when(httpServletRequest.getParameter(PARAM_RESOURCE_ID)).thenReturn(EMPTY_STRING);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_ID_EXCEPTION_MESSAGE);
        completedUserResourceController.addCompletedUserResource(httpSession, httpServletRequest);
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#addCompletedUserResource(HttpSession, HttpServletRequest)}
     * expects {@link IllegalArgumentException} when request parameter resource
     * id is Negative.
     */
    @Test
    public void testAddCompletedResourceDetailsWhenResourceIdIsNegative() {
        when(httpServletRequest.getParameter(PARAM_RESOURCE_ID)).thenReturn(NEGATIVE_RESOURCE_ID);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_ID_EXCEPTION_MESSAGE);
        completedUserResourceController.addCompletedUserResource(httpSession, httpServletRequest);

    }

    /**
     * This tests
     * {@link CompletedUserResourceController#addCompletedUserResource(HttpSession, HttpServletRequest)}
     * expects {@link IllegalArgumentException} when request parameter resource
     * id is zero.
     */
    @Test
    public void testAddCompletedResourceDetailsWhenResourceIdIsZero() {
        when(httpServletRequest.getParameter(PARAM_RESOURCE_ID)).thenReturn(ZERO);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_ID_ZERO);
        completedUserResourceController.addCompletedUserResource(httpSession, httpServletRequest);
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#addCompletedUserResource(HttpSession, HttpServletRequest)}
     * expects {@link IllegalArgumentException} when request parameter
     * completion rating is alphanumeric.
     */
    @Test
    public void testAddCompletedResourceDetailsWhenCompletionRatingAlphanumeric() {
        when(httpServletRequest.getParameter(PARAM_RATING)).thenReturn(ALPHANUMERIC);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(COMPLETION_RATING_EXCEPTION_MESSAGE);
        completedUserResourceController.addCompletedUserResource(httpSession, httpServletRequest);
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#addCompletedUserResource(HttpSession, HttpServletRequest)}
     * expects {@link IllegalArgumentException} when request parameter
     * completion rating is whitespace.
     */
    @Test
    public void testAddCompletedResourceDetailsWhenCompletionRatingWhitespace() {
        when(httpServletRequest.getParameter(PARAM_RATING)).thenReturn(WHITESPACE);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(COMPLETION_RATING_EXCEPTION_MESSAGE);
        completedUserResourceController.addCompletedUserResource(httpSession, httpServletRequest);
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#addCompletedUserResource(HttpSession, HttpServletRequest)}
     * expects {@link IllegalArgumentException} when request parameter
     * completion rating is a special character.
     */
    @Test
    public void testAddCompletedResourceDetailsWhenCompletionRatingIsSpecialCharacter() {
        when(httpServletRequest.getParameter(PARAM_RATING)).thenReturn(SPECIAL_CHARACTER);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(COMPLETION_RATING_EXCEPTION_MESSAGE);
        completedUserResourceController.addCompletedUserResource(httpSession, httpServletRequest);
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#addCompletedUserResource(HttpSession, HttpServletRequest)}
     * expects {@link IllegalArgumentException} when request parameter
     * completion rating is Empty.
     */
    @Test
    public void testAddCompletedResourceDetailsWhenCompletionRatingIsEmpty() {
        when(httpServletRequest.getParameter(PARAM_RATING)).thenReturn(EMPTY_STRING);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(COMPLETION_RATING_EXCEPTION_MESSAGE);
        completedUserResourceController.addCompletedUserResource(httpSession, httpServletRequest);
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#addCompletedUserResource(HttpSession, HttpServletRequest)}
     * expects {@link IllegalArgumentException} when request parameter
     * completion rating is Negative.
     */
    @Test
    public void testAddCompletedResourceDetailsWhenCompletionRatingIsNegative() {
        when(httpServletRequest.getParameter(PARAM_RATING)).thenReturn(NEGATIVE_RESOURCE_ID);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(COMPLETION_RATING_EXCEPTION_MESSAGE);
        completedUserResourceController.addCompletedUserResource(httpSession, httpServletRequest);
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#addCompletedUserResource(HttpSession, HttpServletRequest)}
     * expects {@link IllegalArgumentException} when request parameter
     * completion rating is any other positive integer that is not within range
     * [0,4]
     */
    @Test
    public void testAddCompletedResourceDetailsWhenCompletionRatingIsPositive() {
        when(httpServletRequest.getParameter(PARAM_RATING)).thenReturn(SIXTY);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(COMPLETION_RATING_NOT_VALID);
        completedUserResourceController.addCompletedUserResource(httpSession, httpServletRequest);
    }

    /**
     * Tests
     * {@link CompletedUserResourceController#addCompletedUserResource(HttpSession, HttpServletRequest)}
     * when request parameter completion rating is within proper range. Expects
     * {@link ModelAndView} object, with ViewName = REDIRECT_HOMEPAGE and Model
     * having message = SUCCESS_MESSAGE
     */
    @Test
    public void testAddCompletedResourceDetailsWhenCompletionRatingIsValid() {
        for (final CompletionRating i : CompletionRating.values()) {
            when(httpServletRequest.getParameter(PARAM_RATING)).thenReturn(Integer.toString(i.getValue()));
            final ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName(REDIRECT_HOMEPAGE);
            modelAndView.addObject("message", I18N_BUNDLE.getString(SUCCESS_MESSAGE));
            assertEquals(modelAndView.getView(), completedUserResourceController
                    .addCompletedUserResource(httpSession, httpServletRequest).getView());
            assertEquals(modelAndView.getModel(), completedUserResourceController
                    .addCompletedUserResource(httpSession, httpServletRequest).getModel());
        }
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#addCompletedUserResource(HttpSession, HttpServletRequest)}
     * when not logged in
     */
    @Test
    public void testAddCompletedResourceDetailsWhenNotLoggedIn() {
        when(loginStatus.isLoggedIn()).thenReturn(false);
        modelAndView = completedUserResourceController.addCompletedUserResource(httpSession, httpServletRequest);
        assertEquals(REDIRECT_LOGIN, modelAndView.getViewName());
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#addCompletedUserResource(HttpSession, HttpServletRequest)}
     * when {@link UserProfileDetails} object is null Expects
     * {@link IllegalArgumentException}.
     */
    @Test
    public void testAddCompletedResourceDetailseWhenUserIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(USER_NULL_ERROR_MESSAGE);
        when(httpSession.getAttribute(USER_DETAILS)).thenReturn(null);
        completedUserResourceController.addCompletedUserResource(httpSession, httpServletRequest);
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#addCompletedUserResource(HttpSession, HttpServletRequest)}
     * for valid input
     */
    @Test
    public void testAddCompletedResourceDetailsWhenManagerSuccess() {
        when(completedUserResourceManager.addCompletedUserResourceRating(completedUserResource)).thenReturn(true);
        modelAndView = completedUserResourceController.addCompletedUserResource(httpSession, httpServletRequest);
        assertEquals(REDIRECT_HOMEPAGE, modelAndView.getViewName());
        assertEquals(I18N_BUNDLE.getString(SUCCESS_MESSAGE), modelAndView.getModel().get(MESSAGE));
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#addCompletedUserResource(HttpSession, HttpServletRequest)}
     * expects {@link ManagerException}
     */
    @Test
    public void testAddCompletedResourceDetailsWhenManagerException() {
        when(completedUserResourceManager.addCompletedUserResourceRating(any(CompletedUserResource.class)))
                .thenThrow(new ManagerException());
        modelAndView = completedUserResourceController.addCompletedUserResource(httpSession, httpServletRequest);
        Mockito.verify(mockAppender).doAppend((LoggingEvent) captorLoggingEvent.capture());
        final LoggingEvent loggingEvent = (LoggingEvent) captorLoggingEvent.getValue();
        assertEquals(Level.ERROR, loggingEvent.getLevel());
        assertEquals(ERROR_ADDING_USER_EXCEPTION, loggingEvent.getRenderedMessage());
        assertEquals(I18N_BUNDLE.getString(ERROR_MESSAGE), modelAndView.getModel().get(MESSAGE));
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#showCompletedResource(HttpSession)}
     * expects {@link NullPointerException} when {@link HttpSession} object is
     * null.
     */
    @Test
    public void testShowCompletedResourceDetailsWhenSessionIsNull() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(HTTPSESSION_NOT_NULL);
        completedUserResourceController.showCompletedResource(null);
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#showCompletedResource(HttpSession)}
     * when user is not logged in
     */
    @Test
    public void testShowCompletedResourcesWhenNotLoggedIn() {
        when(loginStatus.isLoggedIn()).thenReturn(false);
        modelAndView = completedUserResourceController.showCompletedResource(httpSession);
        assertEquals(REDIRECT_LOGIN, modelAndView.getViewName());
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#showCompletedResource(HttpSession)}
     * for valid input
     */
    @Test
    public void testShowCompletedResourcesWhenManagerSuccess() {
        final List<CompletedResource> completedResources = Collections
                .singletonList(new CompletedResource(VALID_USERID, VALID_RESOURCE_ID, VALID_RESOURCE_NAME,
                        VALID_RESOURCE_LINK, CompletionRating.SATISFIED, VALID_DISPLAY_DATE));
        when(completedUserResourceManager.getCompletedResourcesByUserId(VALID_USERID)).thenReturn(completedResources);
        modelAndView = completedUserResourceController.showCompletedResource(httpSession);
        assertEquals(COMPLETED_RESOURCE_PAGE, modelAndView.getViewName());
        assertSame(completedResources, modelAndView.getModel().get(COMPLETED_RESOURCES));
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#showCompletedResource(HttpSession)}
     * when the list {@link CompletedResource} is null
     */
    @Test
    public void testShowCompletedResourcesWhenManagerSuccessReturnsNull() {
        when(completedUserResourceManager.getCompletedResourcesByUserId(VALID_USERID)).thenReturn(null);
        modelAndView = completedUserResourceController.showCompletedResource(httpSession);
        assertNull(modelAndView.getModel().get(COMPLETED_RESOURCES));
        assertEquals(I18N_BUNDLE.getString(NO_RECORDS_MESSAGE), modelAndView.getModel().get(MESSAGE));
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#showCompletedResource(HttpSession)}
     * when the list {@link CompletedResource} is empty
     */
    @Test
    public void testShowCompletedResourcesWhenManagerSuccessReturnsEmpty() {
        final List<CompletedResource> completedResources = Collections.emptyList();
        when(completedUserResourceManager.getCompletedResourcesByUserId(VALID_USERID)).thenReturn(completedResources);
        modelAndView = completedUserResourceController.showCompletedResource(httpSession);
        assertSame(completedResources, modelAndView.getModel().get(COMPLETED_RESOURCES));
        assertEquals(I18N_BUNDLE.getString(NO_RECORDS_MESSAGE), modelAndView.getModel().get(MESSAGE));
    }

    /**
     * This tests
     * {@link CompletedUserResourceController#showCompletedResource(HttpSession)}
     * expects {@link ManagerException}
     */
    @Test
    public void testShowCompletedResourcesWhenManagerException() {
        when(completedUserResourceManager.getCompletedResourcesByUserId(VALID_USERID))
                .thenThrow(new ManagerException());
        modelAndView = completedUserResourceController.showCompletedResource(httpSession);
        Mockito.verify(mockAppender).doAppend((LoggingEvent) captorLoggingEvent.capture());
        final LoggingEvent loggingEvent = (LoggingEvent) captorLoggingEvent.getValue();
        assertEquals(Level.ERROR, loggingEvent.getLevel());
        assertEquals(ERROR_QUERING_MANAGER_EXCEPTION, loggingEvent.getRenderedMessage());
        assertEquals(I18N_BUNDLE.getString(COMPLETED_RESOURCE_ERROR_MESSAGE), modelAndView.getModel().get(MESSAGE));
    }

    private static URL createUrl(final String spec) {
        try {
            return new URL(spec);
        } catch (final MalformedURLException e) {
            Assert.fail();
        }
        return null;
    }
}