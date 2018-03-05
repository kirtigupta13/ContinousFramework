package com.cerner.devcenter.education.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
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
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.CompletedResource;
import com.cerner.devcenter.education.models.CompletedUserResource;
import com.cerner.devcenter.education.utils.CompletionRating;

/**
 * Tests the functionalities of {@link CompletedUserResourceDAOImpl}
 *
 * @author Vinutha Nuchimaniyanda (VN046193)
 * @author Mayur Rajendran (MT049536)
 * @author Rishabh Bhojak (RB048032)
 * @author Vincent Dasari (VD049645)
 * @author Santosh kumar (SK051343)
 */
@RunWith(MockitoJUnitRunner.class)
public class CompletedUserResourceDAOImplTest {

    private static final String EMPTY_STRING = "";
    private static final String WHITESPACE = "   ";
    private static final String VALID_USER_ID = "VN046193";
    private static final String VALID_RESOURCE_NAME = "Java";
    private static final int VALID_RESOURCE_ID = 2;
    private static final int VALID_NUMBER_OF_COMPLETED_RESOURCES_TO_RETURN = 5;
    private static final int NEGATIVE_NUMBER_OF_COMPLETED_RESOURCES_TO_RETURN = -1;
    private static final int ZERO_COMPLETED_RESOURCES_TO_RETURN = 0;
    private static final long VALID_DATE = 1453005248;
    private static final Date VALID_DISPLAY_DATE = new Date();
    private static final URL VALID_RESOURCE_LINK = createUrl("http://www.junit.org");
    private static final int VALID_NUMBER_OF_COMPLETED_RESOURCES = 10;
    private static final String VALID_CATEGORY_NAME = "Object Oriented Programming";

    private static final String USER_ID_ILLEGAL_ARGUMENT_MESSAGE = "User ID cannot be null/empty/whitespace.";
    private static final String RESOURCE_ID_ILLEGAL_ARGUMENT_MESSAGE = "Resource ID must be greater than zero.";
    private static final String COMPLETED_DATE_GREATER_THAN_ZERO = "CompletionDate should be greater than 0";
    private static final String INVALID_NUMBER_OF_REQUIRED_COMPLETED_RESOURCES_ERROR_MESSAGE = "Number of required completed resources to display must be greater than 0";

    private static final String ERROR_ADDING_COMPLETION_RATING = "Error: unable to execute query and add the completion rating for the user.";
    private static final String FAILED_EXCEPTION = "Failed with Exception: ";
    private static final String ERROR_LOGGER_STRING = "Error adding completion rating for the resource: 2 and user: VN046193  with the exception: dataAccessException";
    private static final String ERROR_LOGGER_QUERYING_STRING = "Error querying completed resources for user: VN046193 with the exception: dataAccessException";
    private static final String ERROR_RETRIEVING_RESOURCES = "Error: unable to execute query and retrieve completed resources for the user.";
    private static final String GET_SPECIFIC_NUMBER_OF_COMPLETED_RESOURCES_BY_USER_ID_ERROR_MESSAGE = "Error retrieving: %d completed resources for user: %s";
    private static final String ERROR_GETTING_COUNT_OF_COMPLETED_RESOURCES_ERROR_MESSAGE = "Unable to get count of completed resources for the user: %s";
    private static final String ERROR_GETTING_CATEGORY_WITH_MOST_COMPLETED_RESOURCES_ERROR_MESSAGE = "Unable to retrieve name of category with the most number of completed resources for the user: %s";

    private static final String ADD_COMPLETED_USER_RESOURCE = "INSERT INTO completed_user_resource (user_id, resource_id, completion_rating, completion_date) VALUES (?,?,?,?)";
    private static final String GET_COUNT_OF_COMPLETED_RESOURCES_BY_USER_ID_QUERY = "SELECT COUNT(completion_rating) as count FROM completed_user_resource WHERE user_id = ?";
    private static final String GET_CATEGORY_NAME_WITH_MOST_COMPLETED_RESOURCES_BY_USER_ID_QUERY = "SELECT ct.name FROM completed_user_resource cr "
            + "INNER JOIN category_resource_reltn crt ON cr.resource_id = crt.resource_id "
            + "INNER JOIN category ct on ct.id = crt.category_id WHERE user_id = ? GROUP BY ct.name, ct.id "
            + "ORDER BY COUNT(ct.name) DESC LIMIT 1";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @InjectMocks
    private CompletedUserResourceDAOImpl completedUserResourceDAOImpl;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private DataAccessException dataAccessException;
    @Mock
    private Appender mockAppender;
    @Mock
    private RowMapper<CompletedResource> completedResourceRowMapper;
    @Captor
    private ArgumentCaptor captorLoggingEvent;

    @Before
    public void setUp() {
        when(
                jdbcTemplate.update(
                        ADD_COMPLETED_USER_RESOURCE,
                        VALID_USER_ID,
                        VALID_RESOURCE_ID,
                        CompletionRating.SATISFIED.getValue(),
                        VALID_DATE)).thenReturn(1);
        LogManager.getRootLogger().addAppender(mockAppender);
    }

    @After
    public void tearDown() {
        LogManager.getRootLogger().removeAppender(mockAppender);
    }

    /**
     * Test
     * {@link CompletedUserResourceDAOImpl#addCompletedUserResourceRating(String, int, CompletionRating, long)}
     * when
     * {@link JdbcTemplate#update(org.springframework.jdbc.core.PreparedStatementCreator)}
     * throws {@link DataAccessException}. Expects {@link DAOException}.
     */
    @Test
    public void testAddCompletedUserResourceRatingWhenJdbcThrowsDataAccessException() throws DAOException {
        when(
                jdbcTemplate.update(
                        ADD_COMPLETED_USER_RESOURCE,
                        VALID_USER_ID,
                        VALID_RESOURCE_ID,
                        CompletionRating.SATISFIED.getValue(),
                        VALID_DATE)).thenThrow(dataAccessException);
        expectedException.expect(DAOException.class);
        expectedException.expectCause(Matchers.<Throwable>equalTo(dataAccessException));
        expectedException.expectMessage(ERROR_ADDING_COMPLETION_RATING);
        try {
            completedUserResourceDAOImpl.addCompletedUserResourceRating(
                    VALID_USER_ID,
                    VALID_RESOURCE_ID,
                    CompletionRating.SATISFIED,
                    VALID_DATE);
        } catch (final DAOException e) {
            Mockito.verify(mockAppender).doAppend((LoggingEvent) captorLoggingEvent.capture());
            final LoggingEvent loggingEvent = (LoggingEvent) captorLoggingEvent.getValue();
            assertEquals(Level.ERROR, loggingEvent.getLevel());
            assertEquals(ERROR_LOGGER_STRING, loggingEvent.getRenderedMessage());
            throw e;
        }
    }

    /**
     * Test
     * {@link CompletedUserResourceDAOImpl#addCompletedUserResourceRating(String, int, CompletionRating, long)}
     * with valid user id and resource id with valid EXTREMELY_SATISFIED rating.
     * Expects true.
     */
    @Test
    public void testAddCompletedUserResourceRatingValidInputsExtremelySatisfiedRating() throws DAOException {
        assertTrue(
                completedUserResourceDAOImpl.addCompletedUserResourceRating(
                        VALID_USER_ID,
                        VALID_RESOURCE_ID,
                        CompletionRating.EXTREMELY_SATISFIED,
                        VALID_DATE));
    }

    /**
     * Test
     * {@link CompletedUserResourceDAOImpl#addCompletedUserResourceRating(String, int, CompletionRating, long)}
     * with valid user id and resource id with valid SATISFIED rating. Expects
     * true.
     */
    @Test
    public void testAddCompletedUserResourceRatingValidInputsSatisfiedRating() throws DAOException {
        assertTrue(
                completedUserResourceDAOImpl.addCompletedUserResourceRating(
                        VALID_USER_ID,
                        VALID_RESOURCE_ID,
                        CompletionRating.SATISFIED,
                        VALID_DATE));
    }

    /**
     * Test
     * {@link CompletedUserResourceDAOImpl#addCompletedUserResourceRating(String, int, CompletionRating, long)}
     * with valid user id and resource id with valid NEUTRAL rating. Expects
     * true.
     */
    @Test
    public void testAddCompletedUserResourceRatingValidInputsNeutralRating() throws DAOException {
        assertTrue(
                completedUserResourceDAOImpl.addCompletedUserResourceRating(
                        VALID_USER_ID,
                        VALID_RESOURCE_ID,
                        CompletionRating.NEUTRAL,
                        VALID_DATE));
    }

    /**
     * Test
     * {@link CompletedUserResourceDAOImpl#addCompletedUserResourceRating(String, int, CompletionRating, long)}
     * with valid user id and resource id with valid DISSATISFIED rating.
     * Expects true.
     */
    @Test
    public void testAddCompletedUserResourceRatingValidInputsDissatisfiedRating() throws DAOException {
        assertTrue(
                completedUserResourceDAOImpl.addCompletedUserResourceRating(
                        VALID_USER_ID,
                        VALID_RESOURCE_ID,
                        CompletionRating.DISSATISFIED,
                        VALID_DATE));
    }

    /**
     * Test
     * {@link CompletedUserResourceDAOImpl#addCompletedUserResourceRating(String, int, CompletionRating, long)}
     * with valid user id and resource id with valid EXTREMELY_DISSATISFIED
     * rating. Expects true.
     */
    @Test
    public void testAddCompletedUserResourceRatingValidInputsExtremelyDissatisfiedRating() throws DAOException {
        assertTrue(
                completedUserResourceDAOImpl.addCompletedUserResourceRating(
                        VALID_USER_ID,
                        VALID_RESOURCE_ID,
                        CompletionRating.EXTREMELY_DISSATISFIED,
                        VALID_DATE));
    }

    /**
     * Test
     * {@link CompletedUserResourceDAOImpl#addCompletedUserResourceRating(String, int, CompletionRating, long)}
     * when user id is empty string. Expects {@link IllegalArgumentException}.
     */
    @Test
    public void testAddCompletedUserResourceRatingWhenUserIdEmpty() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(USER_ID_ILLEGAL_ARGUMENT_MESSAGE);
        completedUserResourceDAOImpl.addCompletedUserResourceRating(
                EMPTY_STRING,
                VALID_RESOURCE_ID,
                CompletionRating.SATISFIED,
                VALID_DATE);
    }

    /**
     * Test
     * {@link CompletedUserResourceDAOImpl#addCompletedUserResourceRating(String, int, CompletionRating, long)}
     * when user id is <code>null</code>. Expects
     * {@link IllegalArgumentException}.
     */
    @Test
    public void testAddCompletedUserResourceRatingWhenUserIdNull() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(USER_ID_ILLEGAL_ARGUMENT_MESSAGE);
        completedUserResourceDAOImpl
                .addCompletedUserResourceRating(null, VALID_RESOURCE_ID, CompletionRating.SATISFIED, VALID_DATE);
    }

    /**
     * Test
     * {@link CompletedUserResourceDAOImpl#addCompletedUserResourceRating(String, int, CompletionRating, long)}
     * when user id is whitespaces. Expects {@link IllegalArgumentException}.
     */
    @Test
    public void testAddCompletedUserResourceRatingWhenUserIdWhitespace() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(USER_ID_ILLEGAL_ARGUMENT_MESSAGE);
        completedUserResourceDAOImpl
                .addCompletedUserResourceRating(WHITESPACE, VALID_RESOURCE_ID, CompletionRating.SATISFIED, VALID_DATE);
    }

    /**
     * Test
     * {@link CompletedUserResourceDAOImpl#addCompletedUserResourceRating(String, int, CompletionRating, long)}
     * when resource id is negative. Expects {@link IllegalArgumentException}.
     */
    @Test
    public void testAddCompletedUserResourceRatingWhenResourceIdNegative() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_ID_ILLEGAL_ARGUMENT_MESSAGE);

        completedUserResourceDAOImpl
                .addCompletedUserResourceRating(VALID_USER_ID, -3, CompletionRating.SATISFIED, VALID_DATE);
    }

    /**
     * Test
     * {@link CompletedUserResourceDAOImpl#addCompletedUserResourceRating(String, int, CompletionRating, long)}
     * when resource id is 0. Expects {@link IllegalArgumentException}.
     */
    @Test
    public void testAddCompletedUserResourceRatingWhenResourceIdZero() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_ID_ILLEGAL_ARGUMENT_MESSAGE);
        completedUserResourceDAOImpl
                .addCompletedUserResourceRating(VALID_USER_ID, 0, CompletionRating.SATISFIED, VALID_DATE);
    }

    /**
     * Test
     * {@link CompletedUserResource#CompletedUserResource(String, int, CompletionRating, long)}
     * when Date is 0. Expects {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWhenCompletionDateIsZero() throws DAOException {
        try {
            new CompletedUserResource(VALID_USER_ID, VALID_RESOURCE_ID, CompletionRating.SATISFIED, 0);
        } catch (final IllegalArgumentException e) {
            assertEquals(COMPLETED_DATE_GREATER_THAN_ZERO, e.getMessage());
            throw e;
        }
    }

    /**
     * Test
     * {@link CompletedUserResource#CompletedUserResource(String, int, CompletionRating, long)}
     * when Completion Date is negative. Expects
     * {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWhenCompletionDateIsNegative() throws DAOException {
        try {
            new CompletedUserResource(VALID_USER_ID, VALID_RESOURCE_ID, CompletionRating.SATISFIED, -1);
        } catch (final IllegalArgumentException e) {
            assertEquals(COMPLETED_DATE_GREATER_THAN_ZERO, e.getMessage());
            throw e;
        }
    }

    /**
     * Test {@link CompletedUserResourceDAOImpl#getCompletedResources(String)}
     * when user id is empty string. Expects {@link IllegalArgumentException}.
     */
    @Test
    public void testGetCompletedResourcesWhenUserIdEmpty() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(USER_ID_ILLEGAL_ARGUMENT_MESSAGE);
        completedUserResourceDAOImpl.getCompletedResources(EMPTY_STRING);
    }

    /**
     * Test {@link CompletedUserResourceDAOImpl#getCompletedResources(String)}
     * when user id is NULL. Expects {@link IllegalArgumentException}.
     */
    @Test
    public void testGetCompletedResourcesWhenUserIdNull() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(USER_ID_ILLEGAL_ARGUMENT_MESSAGE);
        completedUserResourceDAOImpl.getCompletedResources(null);
    }

    /**
     * Test {@link CompletedUserResourceDAOImpl#getCompletedResources(String)}
     * when user id is whitespace. Expects {@link IllegalArgumentException}.
     */
    @Test
    public void testGetCompletedResourcesWhenUserIdWhitespace() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(USER_ID_ILLEGAL_ARGUMENT_MESSAGE);
        completedUserResourceDAOImpl.getCompletedResources(WHITESPACE);
    }

    /**
     * Test {@link CompletedUserResourceDAOImpl#getCompletedResources(String)}
     * with valid values. Expects List of {@link CompletedResource} object.
     */
    @Test
    public void testGetCompletedResourceReturnsList() throws DAOException {
        final List<CompletedResource> completedResources = Collections.singletonList(
                new CompletedResource(
                        VALID_USER_ID,
                        VALID_RESOURCE_ID,
                        VALID_RESOURCE_NAME,
                        VALID_RESOURCE_LINK,
                        CompletionRating.SATISFIED,
                        VALID_DISPLAY_DATE));
        when(jdbcTemplate.query(anyString(), any(completedResourceRowMapper.getClass()), anyString()))
                .thenReturn(completedResources);
        final List<CompletedResource> result = completedUserResourceDAOImpl.getCompletedResources(VALID_USER_ID);
        assertSame(completedResources, result);
    }

    /**
     * Test {@link CompletedUserResourceDAOImpl#getCompletedResources(String)}
     * when {@link JdbcTemplate#query(String, RowMapper, Object...)} throws
     * {@link DataAccessException}. Expects {@link DAOException}.
     */
    @Test
    public void testGetCompletedResourcesWhenJdbcThrowsDataAccessException() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(completedResourceRowMapper.getClass()), anyString()))
                .thenThrow(dataAccessException);
        expectedException.expect(DAOException.class);
        expectedException.expectCause(Matchers.<Throwable>equalTo(dataAccessException));
        expectedException.expectMessage(ERROR_RETRIEVING_RESOURCES);
        try {
            completedUserResourceDAOImpl.getCompletedResources(VALID_USER_ID);
        } catch (final DAOException e) {
            Mockito.verify(mockAppender).doAppend((LoggingEvent) captorLoggingEvent.capture());
            final LoggingEvent loggingEvent = (LoggingEvent) captorLoggingEvent.getValue();
            assertEquals(Level.ERROR, loggingEvent.getLevel());
            assertEquals(ERROR_LOGGER_QUERYING_STRING, loggingEvent.getRenderedMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceDAOImpl#getMostRecentlyCompletedResources(String, int)}
     * to throw a {@link DAOException} when
     * {@link JdbcTemplate#query(String, RowMapper)} throws a
     * {@link DataAccessException}.
     */
    @Test(expected = DAOException.class)
    public void testGetMostRecentlyCompletedResourcesWhenJdbcThrowsDataAccessException() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(completedResourceRowMapper.getClass()), anyString(), anyInt()))
                .thenThrow(dataAccessException);
        try {
            completedUserResourceDAOImpl
                    .getMostRecentlyCompletedResources(VALID_USER_ID, VALID_NUMBER_OF_COMPLETED_RESOURCES_TO_RETURN);
        } catch (final DAOException daoException) {
            assertEquals(
                    String.format(
                            GET_SPECIFIC_NUMBER_OF_COMPLETED_RESOURCES_BY_USER_ID_ERROR_MESSAGE,
                            VALID_NUMBER_OF_COMPLETED_RESOURCES_TO_RETURN,
                            VALID_USER_ID),
                    daoException.getMessage());
            throw daoException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceDAOImpl#getMostRecentlyCompletedResources(String, int)}
     * to throw an {@link IllegalArgumentException} when userId is
     * <code>null</code>.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetMostRecentlyCompletedResourcesWhenUserIdIsNull() throws DAOException {
        try {
            completedUserResourceDAOImpl
                    .getMostRecentlyCompletedResources(null, VALID_NUMBER_OF_COMPLETED_RESOURCES_TO_RETURN);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceDAOImpl#getMostRecentlyCompletedResources(String, int)}
     * to throw an {@link IllegalArgumentException} when userId is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetMostRecentlyCompletedResourcesWhenUserIdIsBlank() throws DAOException {
        try {
            completedUserResourceDAOImpl
                    .getMostRecentlyCompletedResources(EMPTY_STRING, VALID_NUMBER_OF_COMPLETED_RESOURCES_TO_RETURN);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceDAOImpl#getMostRecentlyCompletedResources(String, int)}
     * to throw an {@link IllegalArgumentException} when userId is whitespace.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetMostRecentlyCompletedResourcesWhenUserIdIsWhitespace() throws DAOException {
        try {
            completedUserResourceDAOImpl
                    .getMostRecentlyCompletedResources(WHITESPACE, VALID_NUMBER_OF_COMPLETED_RESOURCES_TO_RETURN);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceDAOImpl#getMostRecentlyCompletedResources(String, int)}
     * to throw an {@link IllegalArgumentException} when
     * numberOfRequiredCompletedResources is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetMostRecentlyCompletedResourcesWhenNumberOfRequiredCompletedResourcesIsNegative()
            throws DAOException {
        try {
            completedUserResourceDAOImpl
                    .getMostRecentlyCompletedResources(VALID_USER_ID, NEGATIVE_NUMBER_OF_COMPLETED_RESOURCES_TO_RETURN);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(
                    INVALID_NUMBER_OF_REQUIRED_COMPLETED_RESOURCES_ERROR_MESSAGE,
                    illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceDAOImpl#getMostRecentlyCompletedResources(String, int)}
     * to throw an {@link IllegalArgumentException} when
     * numberOfRequiredCompletedResources is 0.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetMostRecentlyCompletedResourcesWhenNumberOfRequiredCompletedResourcesIs0() throws DAOException {
        try {
            completedUserResourceDAOImpl
                    .getMostRecentlyCompletedResources(VALID_USER_ID, ZERO_COMPLETED_RESOURCES_TO_RETURN);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(
                    INVALID_NUMBER_OF_REQUIRED_COMPLETED_RESOURCES_ERROR_MESSAGE,
                    illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceDAOImpl#getMostRecentlyCompletedResources(String, int)}
     * to return a valid {@link List} of {@link CompletedResource completed
     * resources} when all arguments are valid.
     */
    @Test
    public void testGetMostRecentlyCompletedResourcesWhenArgumentsAreValid() throws DAOException {
        final List<CompletedResource> completedResources = Collections.singletonList(
                new CompletedResource(
                        VALID_USER_ID,
                        VALID_RESOURCE_ID,
                        VALID_RESOURCE_NAME,
                        VALID_RESOURCE_LINK,
                        CompletionRating.SATISFIED,
                        VALID_DISPLAY_DATE));
        when(jdbcTemplate.query(anyString(), any(completedResourceRowMapper.getClass()), anyString(), anyInt()))
                .thenReturn(completedResources);
        assertEquals(
                completedResources,
                (completedUserResourceDAOImpl.getMostRecentlyCompletedResources(
                        VALID_USER_ID,
                        VALID_NUMBER_OF_COMPLETED_RESOURCES_TO_RETURN)));
    }

    /**
     * Expects
     * {@link CompletedUserResourceDAOImpl#getCountOfResourcesCompletedByUser(String)}
     * to throw an {@link IllegalArgumentException} when userId is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetCountOfResourcesCompletedByUserWhenUserIdIsNull() throws DAOException {
        try {
            completedUserResourceDAOImpl.getCountOfResourcesCompletedByUser(null);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceDAOImpl#getCountOfResourcesCompletedByUser(String)}
     * to throw an {@link IllegalArgumentException} when userId is an empty
     * string.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetCountOfResourcesCompletedByUserWhenUserIdIsEmpty() throws DAOException {
        try {
            completedUserResourceDAOImpl.getCountOfResourcesCompletedByUser(EMPTY_STRING);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceDAOImpl#getCountOfResourcesCompletedByUser(String)}
     * to throw an {@link IllegalArgumentException} when userId is whitespace.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetCountOfResourcesCompletedByUserWhenUserIdIsWhitespace() throws DAOException {
        try {
            completedUserResourceDAOImpl.getCountOfResourcesCompletedByUser(WHITESPACE);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceDAOImpl#getCountOfResourcesCompletedByUser(String)}
     * to throw a {@link DAOException} when
     * {@link JdbcTemplate#queryForObject(String, Class, Object...)} throws a
     * {@link DataAccessException}.
     */
    @Test(expected = DAOException.class)
    public void testGetCountOfResourcesCompletedByUserWhenJDBCTemplateThrowsDataAccessException() throws DAOException {
        when(
                jdbcTemplate.queryForObject(
                        GET_COUNT_OF_COMPLETED_RESOURCES_BY_USER_ID_QUERY,
                        Integer.class,
                        VALID_USER_ID)).thenThrow(dataAccessException);
        try {
            completedUserResourceDAOImpl.getCountOfResourcesCompletedByUser(VALID_USER_ID);
        } catch (final DAOException daoException) {
            assertEquals(
                    String.format(ERROR_GETTING_COUNT_OF_COMPLETED_RESOURCES_ERROR_MESSAGE, VALID_USER_ID),
                    daoException.getMessage());
            throw daoException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceDAOImpl#getCountOfResourcesCompletedByUser(String)}
     * to return 0 when
     * {@link JdbcTemplate#queryForObject(String, Class, Object...)} returns
     * <code>null</code>.
     */
    @Test
    public void testGetCountOfResourcesCompletedByUserWhenJDBCTemplateReturnsNull() throws DAOException {
        when(
                jdbcTemplate.queryForObject(
                        GET_COUNT_OF_COMPLETED_RESOURCES_BY_USER_ID_QUERY,
                        Integer.class,
                        VALID_USER_ID)).thenReturn(null);
        assertEquals(0, completedUserResourceDAOImpl.getCountOfResourcesCompletedByUser(VALID_USER_ID));
    }

    /**
     * Expects
     * {@link CompletedUserResourceDAOImpl#getCountOfResourcesCompletedByUser(String)}
     * to return the same valid value
     * {@link JdbcTemplate#queryForObject(String, Class, Object...)} returns a
     * valid count of {@link CompletedResource resources completed} by the user.
     */
    @Test
    public void testGetCountOfResourcesCompletedByUserWhenJDBCTemplateReturnsValidValue() throws DAOException {
        when(
                jdbcTemplate.queryForObject(
                        GET_COUNT_OF_COMPLETED_RESOURCES_BY_USER_ID_QUERY,
                        Integer.class,
                        VALID_USER_ID)).thenReturn(VALID_NUMBER_OF_COMPLETED_RESOURCES);
        assertEquals(
                VALID_NUMBER_OF_COMPLETED_RESOURCES,
                completedUserResourceDAOImpl.getCountOfResourcesCompletedByUser(VALID_USER_ID));
    }

    /**
     * Expects
     * {@link CompletedUserResourceDAOImpl#getCategoryNameWithMostCompletedResourcesByUser(String)}
     * to throw an {@link IllegalArgumentException} when userId is
     * <code>null</code>.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetCategoryNameWithMostCompletedResourcesByUserWhenUserIdIsNull() throws DAOException {
        try {
            completedUserResourceDAOImpl.getCategoryNameWithMostCompletedResourcesByUser(null);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceDAOImpl#getCategoryNameWithMostCompletedResourcesByUser(String)}
     * to throw an {@link IllegalArgumentException} when userId is an empty
     * string.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetCategoryNameWithMostCompletedResourcesByUserWhenUserIdIsEmpty() throws DAOException {
        try {
            completedUserResourceDAOImpl.getCategoryNameWithMostCompletedResourcesByUser(EMPTY_STRING);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceDAOImpl#getCategoryNameWithMostCompletedResourcesByUser(String)}
     * to throw an {@link IllegalArgumentException} when userId is whitespace.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetCategoryNameWithMostCompletedResourcesByUserWhenUserIdIsWhitespace() throws DAOException {
        try {
            completedUserResourceDAOImpl.getCategoryNameWithMostCompletedResourcesByUser(WHITESPACE);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceDAOImpl#getCategoryNameWithMostCompletedResourcesByUser(String)}
     * to throw a {@link DAOException} when
     * {@link JdbcTemplate#queryForObject(String, Class, Object...)} throws a
     * {@link DataAccessException}.
     */
    @Test(expected = DAOException.class)
    public void testGetCategoryNameWithMostCompletedResourcesByUserWhenJDBCTemplateThrowsDataAccessException()
            throws DAOException {
        when(
                jdbcTemplate.queryForObject(
                        GET_CATEGORY_NAME_WITH_MOST_COMPLETED_RESOURCES_BY_USER_ID_QUERY,
                        String.class,
                        VALID_USER_ID)).thenThrow(dataAccessException);
        try {
            completedUserResourceDAOImpl.getCategoryNameWithMostCompletedResourcesByUser(VALID_USER_ID);
        } catch (final DAOException daoException) {
            assertEquals(
                    String.format(ERROR_GETTING_CATEGORY_WITH_MOST_COMPLETED_RESOURCES_ERROR_MESSAGE, VALID_USER_ID),
                    daoException.getMessage());
            throw daoException;
        }
    }

    /**
     * Expects
     * {@link CompletedUserResourceDAOImpl#getCategoryNameWithMostCompletedResourcesByUser(String)}
     * to return an empty string when
     * {@link JdbcTemplate#queryForObject(String, Class, Object...)} returns
     * <code>null</code>.
     */
    @Test
    public void testGetCategoryNameWithMostCompletedResourcesByUserWhenJDBCTemplateReturnsNull() throws DAOException {
        when(
                jdbcTemplate.queryForObject(
                        GET_CATEGORY_NAME_WITH_MOST_COMPLETED_RESOURCES_BY_USER_ID_QUERY,
                        String.class,
                        VALID_USER_ID)).thenReturn(null);
        assertEquals(
                EMPTY_STRING,
                completedUserResourceDAOImpl.getCategoryNameWithMostCompletedResourcesByUser(VALID_USER_ID));
    }

    /**
     * Expects
     * {@link CompletedUserResourceDAOImpl#getCategoryNameWithMostCompletedResourcesByUser(String)}
     * to return an empty string when
     * {@link JdbcTemplate#queryForObject(String, Class, Object...)} returns an
     * empty string.
     */
    @Test
    public void testGetCategoryNameWithMostCompletedResourcesByUserWhenJDBCTemplateReturnsEmptyString()
            throws DAOException {
        when(
                jdbcTemplate.queryForObject(
                        GET_CATEGORY_NAME_WITH_MOST_COMPLETED_RESOURCES_BY_USER_ID_QUERY,
                        String.class,
                        VALID_USER_ID)).thenReturn(EMPTY_STRING);
        assertEquals(
                EMPTY_STRING,
                completedUserResourceDAOImpl.getCategoryNameWithMostCompletedResourcesByUser(VALID_USER_ID));
    }

    /**
     * Expects
     * {@link CompletedUserResourceDAOImpl#getCategoryNameWithMostCompletedResourcesByUser(String)}
     * to return an empty string when
     * {@link JdbcTemplate#queryForObject(String, Class, Object...)} returns
     * whitespace.
     */
    @Test
    public void testGetCategoryNameWithMostCompletedResourcesByUserWhenJDBCTemplateReturnsWhitespace()
            throws DAOException {
        when(
                jdbcTemplate.queryForObject(
                        GET_CATEGORY_NAME_WITH_MOST_COMPLETED_RESOURCES_BY_USER_ID_QUERY,
                        String.class,
                        VALID_USER_ID)).thenReturn(WHITESPACE);
        assertEquals(
                EMPTY_STRING,
                completedUserResourceDAOImpl.getCategoryNameWithMostCompletedResourcesByUser(VALID_USER_ID));
    }

    /**
     * Expects
     * {@link CompletedUserResourceDAOImpl#getCategoryNameWithMostCompletedResourcesByUser(String)}
     * to return the same valid value when
     * {@link JdbcTemplate#queryForObject(String, Class, Object...)} returns a
     * valid {@link Category} name.
     */
    @Test
    public void testGetCategoryNameWithMostCompletedResourcesByUserWhenJDBCTemplateReturnsValidValue()
            throws DAOException {
        when(
                jdbcTemplate.queryForObject(
                        GET_CATEGORY_NAME_WITH_MOST_COMPLETED_RESOURCES_BY_USER_ID_QUERY,
                        String.class,
                        VALID_USER_ID)).thenReturn(VALID_CATEGORY_NAME);
        assertEquals(
                VALID_CATEGORY_NAME,
                completedUserResourceDAOImpl.getCategoryNameWithMostCompletedResourcesByUser(VALID_USER_ID));
    }

    private static URL createUrl(final String spec) {
        try {
            return new URL(spec);
        } catch (final MalformedURLException e) {
            Assert.fail(FAILED_EXCEPTION + e);
        }
        return null;
    }
}