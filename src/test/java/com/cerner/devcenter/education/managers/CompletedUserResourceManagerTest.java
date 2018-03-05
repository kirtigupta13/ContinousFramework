package com.cerner.devcenter.education.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggingEvent;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
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

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.dao.CompletedUserResourceDAO;
import com.cerner.devcenter.education.dao.CompletedUserResourceDAOImpl;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.CompletedResource;
import com.cerner.devcenter.education.models.CompletedUserResource;
import com.cerner.devcenter.education.utils.CompletionRating;

/**
 * Tests the functionalities of {@link CompletedUserResourceManager}
 *
 * @author Vinutha Nuchimaniyanda (VN046193)
 * @author Mayur Rajendran (MT049536)
 * @author Rishabh Bhojak (RB048032)
 * @author Vincent Dasari (VD049645)
 */
@RunWith(MockitoJUnitRunner.class)
public class CompletedUserResourceManagerTest {

    private static final String VALID_USER_ID = "VN046193";
    private static final String VALID_RESOURCE_NAME = "Java";
    private static final String EMPTY_STRING = "";
    private static final String WHITESPACE = "  ";
    private static final int VALID_RESOURCE_ID = 2;
    private static final int VALID_NUMBER_OF_EXPECTED_COMPLETED_RESOURCES = 5;
    private static final int NEGATIVE_NUMBER_OF_EXPECTED_COMPLETED_RESOURCES = -1;
    private static final int ZERO_EXPECTED_COMPLETED_RESOURCES = 0;
    private static final long VALID_DATE = 1453005248;
    private static final URL VALID_RESOURCE_LINK = createUrl("http://java.com");
    private static final Date VALID_DISPLAY_DATE = new Date();
    private static final int VALID_NUMBER_OF_COMPLETED_RESOURCES = 5;
    private static final String VALID_CATEGORY_NAME = "Object Oriented Programming";
    private static final String VALID_EXCEPTION_MESSAGE = "An Exception Occurred.";

    private static final String USER_ID_ILLEGAL_ARGUMENT_ERROR_MESSAGE = "User ID cannot be null/empty/whitespace";
    private static final String INVALID_NUMBER_OF_COMPLETED_RESOURCES_ERROR_MESSAGE = "Number of Completed Resources to display was not greater than 0";
    private static final String COMPLETED_RESOURCE_NOT_NULL = "Completed resource information object cannot be null";

    private static final String ERROR_ADD_COMPLETION_RATING = "Error: unable to execute query and add the completion rating for the user.";
    private static final String ERROR_QUERY_COMPLETION_RATING = "Error: unable to execute query to retrieve completed resources for the user.";
    private static final String ERROR_LOGGER_STRING = "Error adding completion rating for the resource: 2 and user: VN046193  with the exception: daoException";
    private static final String ERROR_QUERYING_DAO_EXCEPTION = "Error executing query to retrieve completed resources for the user: VN046193 with the exception daoException";
    private static final String ERROR_RETRIEVING_RESULTS_ERROR_MESSAGE = "Error: unable to execute query to retrieve completed resources for the user.";

    private static final List<CompletedResource> LIST_OF_COMPLETED_RESOURCES = new ArrayList<CompletedResource>();
    private static final CompletedUserResource COMPLETED_USER_RESOURCE = new CompletedUserResource(VALID_USER_ID,
            VALID_RESOURCE_ID, CompletionRating.SATISFIED, VALID_DATE);
    private static final CompletedResource COMPLETED_RESOURCE = new CompletedResource(VALID_USER_ID, VALID_RESOURCE_ID,
            VALID_RESOURCE_NAME, VALID_RESOURCE_LINK, CompletionRating.EXTREMELY_SATISFIED, VALID_DISPLAY_DATE);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @InjectMocks
    private CompletedUserResourceManager completedUserResourceManager;
    @Mock
    private CompletedUserResourceDAO completedUserResourceDAO;
    @Mock
    private DAOException daoException;
    @Mock
    private Appender mockAppender;
    @Captor
    private ArgumentCaptor captorLoggingEvent;

    @BeforeClass
    public static void onlyOnce() {
        LIST_OF_COMPLETED_RESOURCES.add(COMPLETED_RESOURCE);
    }

    @Before
    public void setUp() throws DAOException {
        LogManager.getRootLogger().addAppender(mockAppender);
    }

    @After
    public void tearDown() {
        LogManager.getRootLogger().removeAppender(mockAppender);
    }

    /**
     * Test
     * {@link CompletedUserResourceManager#addCompletedUserResourceRating(CompletedUserResource)}
     * when
     * {@link CompletedUserResourceDAO#addCompletedUserResourceRating(String, int, CompletionRating, long)}
     * returns true.
     */
    @Test
    public void testAddCompletedUserResourceRatingValid() throws DAOException {
        when(completedUserResourceDAO.addCompletedUserResourceRating(VALID_USER_ID, VALID_RESOURCE_ID,
                CompletionRating.SATISFIED, VALID_DATE)).thenReturn(true);
        assertTrue(completedUserResourceManager.addCompletedUserResourceRating(COMPLETED_USER_RESOURCE));
    }

    /**
     * Test
     * {@link CompletedUserResourceManager#addCompletedUserResourceRating(CompletedUserResource)}
     * when
     * {@link CompletedUserResourceDAO#addCompletedUserResourceRating(String, int, CompletionRating, long)}
     * returns false.
     */
    @Test
    public void testAddCompletedUserResourceRatingInvalid() throws DAOException {
        when(completedUserResourceDAO.addCompletedUserResourceRating(VALID_USER_ID, VALID_RESOURCE_ID,
                CompletionRating.SATISFIED, VALID_DATE)).thenReturn(false);
        assertFalse(completedUserResourceManager.addCompletedUserResourceRating(COMPLETED_USER_RESOURCE));
    }

    /**
     * Test
     * {@link CompletedUserResourceManager#addCompletedUserResourceRating(CompletedUserResource)}
     * when {@link CompletedUserResource} object is null. Expects
     * {@link IllegalArgumentException}.
     */
    @Test
    public void testAddCompletedUserResourceRatingNull() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(COMPLETED_RESOURCE_NOT_NULL);
        completedUserResourceManager.addCompletedUserResourceRating(null);
    }

    /**
     * Test
     * {@link CompletedUserResourceManager#addCompletedUserResourceRating(CompletedUserResource)}
     * when
     * {@link CompletedUserResourceDAO#addCompletedUserResourceRating(String, int, CompletionRating, long)}
     * throws {@link DAOException}. Expects {@link ManagerException}.
     */
    @Test
    public void testAddCompletedUserResourceRatingWhenForDataAccessException() throws DAOException {
        when(completedUserResourceDAO.addCompletedUserResourceRating(VALID_USER_ID, VALID_RESOURCE_ID,
                CompletionRating.SATISFIED, VALID_DATE)).thenThrow(daoException);
        expectedException.expect(ManagerException.class);
        expectedException.expectCause(Matchers.<Throwable>equalTo(daoException));
        expectedException.expectMessage(ERROR_ADD_COMPLETION_RATING);
        try {
            completedUserResourceManager.addCompletedUserResourceRating(COMPLETED_USER_RESOURCE);
        } catch (final ManagerException e) {
            Mockito.verify(mockAppender).doAppend((LoggingEvent) captorLoggingEvent.capture());
            final LoggingEvent loggingEvent = (LoggingEvent) captorLoggingEvent.getValue();
            assertEquals(Level.ERROR, loggingEvent.getLevel());
            assertEquals(ERROR_LOGGER_STRING, loggingEvent.getRenderedMessage());
            throw e;
        }
    }

    /**
     * Test
     * {@link CompletedUserResourceManager#getCompletedResourcesByUserId(String)}
     * when user id is empty string. Expects {@link IllegalArgumentException}.
     */
    @Test
    public void testGetCompletedResourcesWhenUserIdEmpty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(USER_ID_ILLEGAL_ARGUMENT_ERROR_MESSAGE);
        completedUserResourceManager.getCompletedResourcesByUserId(EMPTY_STRING);
    }

    /**
     * Test
     * {@link CompletedUserResourceManager#getCompletedResourcesByUserId(String)}
     * when user id is NULL. Expects {@link IllegalArgumentException}.
     */
    @Test
    public void testGetCompletedResourcesWhenUserIdNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(USER_ID_ILLEGAL_ARGUMENT_ERROR_MESSAGE);
        completedUserResourceManager.getCompletedResourcesByUserId(null);
    }

    /**
     * Test
     * {@link CompletedUserResourceManager#getCompletedResourcesByUserId(String)}
     * when user id is whitespaces. Expects {@link IllegalArgumentException}.
     */
    @Test
    public void testGetCompletedResourcesWhenUserIdWhitespace() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(USER_ID_ILLEGAL_ARGUMENT_ERROR_MESSAGE);
        completedUserResourceManager.getCompletedResourcesByUserId(WHITESPACE);
    }

    /**
     * Test
     * {@link CompletedUserResourceManager#getCompletedResourcesByUserId(String)}
     * when
     * {@link CompletedUserResourceDAO#getCompletedResources(String)}
     * returns list of {@link CompletedResource} Expects a List of
     * {@link CompletedResource} object
     */
    @Test
    public void testGetCompletedResourcesByUserIdForValidUserId() throws DAOException {
        final List<CompletedResource> completedResources = Collections
                .singletonList(new CompletedResource(VALID_USER_ID, VALID_RESOURCE_ID, VALID_RESOURCE_NAME,
                        VALID_RESOURCE_LINK, CompletionRating.SATISFIED, VALID_DISPLAY_DATE));
        when(completedUserResourceDAO.getCompletedResources(VALID_USER_ID)).thenReturn(completedResources);
        assertSame(completedResources, completedUserResourceManager.getCompletedResourcesByUserId(VALID_USER_ID));
    }

    /**
     * Test
     * {@link CompletedUserResourceManager#getCompletedResourcesByUserId(String)}
     * when
     * {@link CompletedUserResourceDAO#getCompletedResources(String)}
     * throws {@link DAOException}. Expects {@link ManagerException}.
     */
    @Test
    public void testGetCompletedResourcesByUserIdWhenThrowsManagerException() throws DAOException {
        when(completedUserResourceDAO.getCompletedResources(VALID_USER_ID)).thenThrow(daoException);
        expectedException.expect(ManagerException.class);
        expectedException.expectCause(Matchers.<Throwable>equalTo(daoException));
        expectedException.expectMessage(ERROR_QUERY_COMPLETION_RATING);
        try {
            completedUserResourceManager.getCompletedResourcesByUserId(VALID_USER_ID);
        } catch (final ManagerException e) {
            Mockito.verify(mockAppender).doAppend((LoggingEvent) captorLoggingEvent.capture());
            final LoggingEvent loggingEvent = (LoggingEvent) captorLoggingEvent.getValue();
            assertEquals(Level.ERROR, loggingEvent.getLevel());
            assertEquals(ERROR_QUERYING_DAO_EXCEPTION, loggingEvent.getRenderedMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceManager#getMostRecentlyCompletedResources(String, int)}
     * to throw an {@link IllegalArgumentException} when userId is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetMostRecentlyCompletedResourcesWhenUserIdIsNull() {
        try {
            completedUserResourceManager.getMostRecentlyCompletedResources(null,
                    VALID_NUMBER_OF_EXPECTED_COMPLETED_RESOURCES);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceManager#getMostRecentlyCompletedResources(String, int)}
     * to throw an {@link IllegalArgumentException} when userId is an empty
     * string.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetMostRecentlyCompletedResourcesWhenUserIdIsEmpty() {
        try {
            completedUserResourceManager.getMostRecentlyCompletedResources(EMPTY_STRING,
                    VALID_NUMBER_OF_EXPECTED_COMPLETED_RESOURCES);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceManager#getMostRecentlyCompletedResources(String, int)}
     * to throw an {@link IllegalArgumentException} when userId is whitespace.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetMostRecentlyCompletedResourcesWhenUserIdIsWhitespace() {
        try {
            completedUserResourceManager.getMostRecentlyCompletedResources(WHITESPACE,
                    VALID_NUMBER_OF_EXPECTED_COMPLETED_RESOURCES);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceManager#getMostRecentlyCompletedResources(String, int)}
     * to throw an {@link IllegalArgumentException} when the number of resources
     * required is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetCompletedResourcesForWidgetWhenNumberOfResourcesRequiredIsNegative() {
        try {
            completedUserResourceManager.getMostRecentlyCompletedResources(VALID_USER_ID,
                    NEGATIVE_NUMBER_OF_EXPECTED_COMPLETED_RESOURCES);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(INVALID_NUMBER_OF_COMPLETED_RESOURCES_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceManager#getMostRecentlyCompletedResources(String, int)}
     * to throw an {@link IllegalArgumentException} when number of resources
     * required is 0.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetMostRecentlyCompletedResourcesWhenNumberOfResourcesRequiredIs0() {
        try {
            completedUserResourceManager.getMostRecentlyCompletedResources(VALID_USER_ID,
                    ZERO_EXPECTED_COMPLETED_RESOURCES);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(INVALID_NUMBER_OF_COMPLETED_RESOURCES_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceManager#getMostRecentlyCompletedResources(String, int)}
     * to throw a {@link ManagerException} when
     * {@link CompletedUserResourceDAO#getMostRecentlyCompletedResources(String, int)}
     * throws a {@link DAOException}
     */
    @Test(expected = ManagerException.class)
    public void testGetMostRecentlyCompletedResourcesWhenDAOThrowsError() throws DAOException {
        when(completedUserResourceDAO.getMostRecentlyCompletedResources(VALID_USER_ID,
                VALID_NUMBER_OF_EXPECTED_COMPLETED_RESOURCES)).thenThrow(new DAOException());
        try {
            completedUserResourceManager.getMostRecentlyCompletedResources(VALID_USER_ID,
                    VALID_NUMBER_OF_EXPECTED_COMPLETED_RESOURCES);
        } catch (final ManagerException managerException) {
            assertEquals(ERROR_RETRIEVING_RESULTS_ERROR_MESSAGE, managerException.getMessage());
            throw managerException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceManager#getMostRecentlyCompletedResources(String, int)}
     * to return an empty {@link CompletedResource} {@link List} when DAO
     * returns null.
     */
    @Test
    public void testGetMostRecentlyCompletedResourcesWhenDAOReturnsNull() throws DAOException {
        when(completedUserResourceDAO.getMostRecentlyCompletedResources(VALID_USER_ID,
                VALID_NUMBER_OF_EXPECTED_COMPLETED_RESOURCES)).thenReturn(null);
        assertEquals(Collections.<CompletedResource>emptyList(), completedUserResourceManager
                .getMostRecentlyCompletedResources(VALID_USER_ID, VALID_NUMBER_OF_EXPECTED_COMPLETED_RESOURCES));
    }

    /**
     * Expects
     * {@link CompletedUserResourceManager#getMostRecentlyCompletedResources(String, int)}
     * to return an empty {@link CompletedResource} {@link List} when DAO
     * returns an empty {@link List}.
     */
    @Test
    public void testGetMostRecentlyCompletedResourcesWhenDAOReturnsEmptysList() throws DAOException {
        when(completedUserResourceDAO.getMostRecentlyCompletedResources(VALID_USER_ID,
                VALID_NUMBER_OF_EXPECTED_COMPLETED_RESOURCES)).thenReturn(Collections.<CompletedResource>emptyList());
        assertEquals(Collections.<CompletedResource>emptyList(), completedUserResourceManager
                .getMostRecentlyCompletedResources(VALID_USER_ID, VALID_NUMBER_OF_EXPECTED_COMPLETED_RESOURCES));
    }

    /**
     * Expects
     * {@link CompletedUserResourceManager#getMostRecentlyCompletedResources(String, int)}
     * to return a {@link List} of {@link CompletedResource completed resources}
     * that does not contain <code>null</code> elements when
     * {@link CompletedUserResourceDAO#getMostRecentlyCompletedResources(String, int)}
     * returns a {@link List} containing null elements.
     */
    @Test
    public void testGetMostRecentlyCompletedResourcesWhenDAOReturnsListWithNullElements() throws DAOException {
        final List<CompletedResource> listWithNull = new ArrayList<CompletedResource>();
        listWithNull.add(null);
        listWithNull.add(COMPLETED_RESOURCE);
        when(completedUserResourceDAO.getMostRecentlyCompletedResources(VALID_USER_ID,
                VALID_NUMBER_OF_EXPECTED_COMPLETED_RESOURCES)).thenReturn(listWithNull);
        assertEquals(LIST_OF_COMPLETED_RESOURCES, completedUserResourceManager
                .getMostRecentlyCompletedResources(VALID_USER_ID, VALID_NUMBER_OF_EXPECTED_COMPLETED_RESOURCES));
    }

    /**
     * Expects
     * {@link CompletedUserResourceManager#getMostRecentlyCompletedResources(String, int)}
     * to return the same {@link CompletedResource} {@link List} when DAO
     * returns a valid {@link List} of {@link CompletedResource completed
     * resources}.
     */
    @Test
    public void testGetMostRecentlyCompletedResourcesWhenDAOReturnsValidList() throws DAOException {
        when(completedUserResourceDAO.getMostRecentlyCompletedResources(VALID_USER_ID,
                VALID_NUMBER_OF_EXPECTED_COMPLETED_RESOURCES)).thenReturn(LIST_OF_COMPLETED_RESOURCES);
        assertEquals(LIST_OF_COMPLETED_RESOURCES, completedUserResourceManager
                .getMostRecentlyCompletedResources(VALID_USER_ID, VALID_NUMBER_OF_EXPECTED_COMPLETED_RESOURCES));
    }

    /**
     * Expects
     * {@link CompletedUserResourceManager#getCountOfResourcesCompletedByUser(String)}
     * to throw an {@link IllegalArgumentException} when userId is
     * <code>null</code>.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetCountOfResourcesCompletedByUserWhenUserIdIsNull() {
        try {
            completedUserResourceManager.getCountOfResourcesCompletedByUser(null);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceManager#getCountOfResourcesCompletedByUser(String)}
     * to throw an {@link IllegalArgumentException} when userId is an empty
     * string.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetCountOfResourcesCompletedByUserWhenUserIdIsEmpty() {
        try {
            completedUserResourceManager.getCountOfResourcesCompletedByUser(EMPTY_STRING);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceManager#getCountOfResourcesCompletedByUser(String)}
     * to throw an {@link IllegalArgumentException} when userId is whitespace.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetCountOfResourcesCompletedByUserWhenUserIdIsWhitespace() {
        try {
            completedUserResourceManager.getCountOfResourcesCompletedByUser(WHITESPACE);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceManager#getCountOfResourcesCompletedByUser(String)}
     * to throw a {@link ManagerException} when
     * {@link CompletedUserResourceDAO#getCountOfResourcesCompletedByUser(String)}
     * throws a {@link DAOException}.
     */
    @Test(expected = ManagerException.class)
    public void testGetCountOfResourcesCompletedByUserWhenDAOThrowsException() throws DAOException {
        when(completedUserResourceDAO.getCountOfResourcesCompletedByUser(VALID_USER_ID))
                .thenThrow(new DAOException(VALID_EXCEPTION_MESSAGE, new Exception()));
        try {
            completedUserResourceManager.getCountOfResourcesCompletedByUser(VALID_USER_ID);
        } catch (final ManagerException managerException) {
            assertEquals(VALID_EXCEPTION_MESSAGE, managerException.getMessage());
            throw managerException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceManager#getCountOfResourcesCompletedByUser(String)}
     * to return the same valid value, when
     * {@link CompletedUserResourceDAOImpl#getCountOfResourcesCompletedByUser(String)}
     * returns a valid number of {@link CompletedResource resources completed}
     * by the user.
     */
    @Test
    public void testGetCountOfResourcesCompletedByUserIdWithValidInputs() throws DAOException {
        when(completedUserResourceDAO.getCountOfResourcesCompletedByUser(VALID_USER_ID))
                .thenReturn(VALID_NUMBER_OF_COMPLETED_RESOURCES);
        assertEquals(VALID_NUMBER_OF_COMPLETED_RESOURCES,
                completedUserResourceManager.getCountOfResourcesCompletedByUser(VALID_USER_ID));
    }

    /**
     * Expects
     * {@link CompletedUserResourceManager#getCategoryNameWithMostNumberOfResourcesCompletedByUser(String)}
     * to throw an {@link IllegalArgumentException} when userId is
     * <code>null</code>.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetCategoryNameWithMostNumberOfResourcesCompletedByUserWhenUserIdIsNull() {
        try {
            completedUserResourceManager.getCategoryNameWithMostNumberOfResourcesCompletedByUser(null);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceManager#getCategoryNameWithMostNumberOfResourcesCompletedByUser(String)}
     * to throw an {@link IllegalArgumentException} when userId is an empty
     * string.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetCategoryNameWithMostNumberOfResourcesCompletedByUserWhenUserIdIsEmpty() {
        try {
            completedUserResourceManager.getCategoryNameWithMostNumberOfResourcesCompletedByUser(EMPTY_STRING);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceManager#getCategoryNameWithMostNumberOfResourcesCompletedByUser(String)}
     * to throw an {@link IllegalArgumentException} when userId is whitespace.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetCategoryNameWithMostNumberOfResourcesCompletedByUserWhenUserIdIsWhitespace() {
        try {
            completedUserResourceManager.getCategoryNameWithMostNumberOfResourcesCompletedByUser(WHITESPACE);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceManager#getCategoryNameWithMostNumberOfResourcesCompletedByUser(String)}
     * to throw a {@link ManagerException} when
     * {@link CompletedUserResourceDAO#getCategoryNameWithMostCompletedResourcesByUser(String)}
     * throws a {@link DAOException}.
     */
    @Test(expected = ManagerException.class)
    public void testGetCategoryNameWithMostNumberOfResourcesCompletedByUserWhenUserIdWhenDAOThrowsException()
            throws DAOException {
        when(completedUserResourceDAO.getCategoryNameWithMostCompletedResourcesByUser(VALID_USER_ID))
                .thenThrow(new DAOException(VALID_EXCEPTION_MESSAGE, new Exception()));
        try {
            completedUserResourceManager.getCategoryNameWithMostNumberOfResourcesCompletedByUser(VALID_USER_ID);
        } catch (final ManagerException managerException) {
            assertEquals(VALID_EXCEPTION_MESSAGE, managerException.getMessage());
            throw managerException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceManager#getCategoryNameWithMostNumberOfResourcesCompletedByUser(String)}
     * to return the same valid value, when
     * {@link CompletedUserResourceDAOImpl#getCategoryNameWithMostCompletedResourcesByUser(String)}
     * returns a valid {@link Category} name.
     */
    @Test
    public void testGetNameOfCategoryWithMostNumberOfResourcesCompletedByUserWhenUserIdWithValidInputs()
            throws DAOException {
        when(completedUserResourceDAO.getCategoryNameWithMostCompletedResourcesByUser(VALID_USER_ID))
                .thenReturn(VALID_CATEGORY_NAME);
        assertEquals(VALID_CATEGORY_NAME,
                completedUserResourceManager.getCategoryNameWithMostNumberOfResourcesCompletedByUser(VALID_USER_ID));
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