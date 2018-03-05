package com.cerner.devcenter.education.managers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.dao.ResourceRequestDAO;
import com.cerner.devcenter.education.dao.ResourceRequestDAOImpl;
import com.cerner.devcenter.education.models.ResourceRequest;

import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

/**
 * Tests the functionalities of {@link ResourceRequestManager}
 *
 * @author Navya Rangeneni (NR046827)
 * @author Vatsal Kesarwani (VK049896)
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourceRequestManagerTest {

    private static final String USER_ID = "ZY123456";
    private static final String CATEGORY_NAME = "Java";
    private static final String RESOURCE_NAME = "YouTube";
    private static final String EMPTY_REQUEST_IDS = "Request IDs array can't be null or empty.";
    private static final String ERROR_DELETING = "Error: unable to delete the resource request.";
    private static final int[] REQUEST_IDS = { 1, 2, 3 };
    private static final String RESOURCE_REQUEST_DB_READ_ERROR = "Error retrieving all resource requests from the database";
    private static final String RESOURCE_REQUEST_DB_READ_LOGGER_MESSAGE = "Error retrieving requested resources from the database";
    private static final String USER_ID_NULL_ERROR = "UserId cannot be null";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @InjectMocks
    private ResourceRequestManager resourceRequestManager;
    @Mock
    private ResourceRequestDAO resourceRequestDAO;
    @Mock
    private JdbcTemplate mockJdbcTemplate;
    @Mock
    private Appender mockAppender;
    @Mock
    private DAOException daoException;

    private final List<ResourceRequest> listOfResourceRequests = new ArrayList<ResourceRequest>();
    private final ResourceRequest resourceRequest = new ResourceRequest();

    @Before
    public void setUp() throws DAOException {
        resourceRequest.setUserId(USER_ID);
        listOfResourceRequests.add(resourceRequest);
        when(resourceRequestDAO.getAllResourceRequests(USER_ID)).thenReturn(listOfResourceRequests);
        LogManager.getRootLogger().addAppender(mockAppender);
    }

    @After
    public void tearDown() {
        LogManager.getRootLogger().removeAppender(mockAppender);
    }

    /**
     * Test {@link ResourceRequestManager#addResourceRequest(ResourceRequest)}
     * when ResourceRequest object is null. Expects
     * {@link IllegalArgumentException}.
     */
    @Test
    public void testAddResourceRequestWhenResourceRequestThrowsIllegalArgumentException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Can't add a null ResourceRequest");
        resourceRequestManager.addResourceRequest(null);
    }

    /**
     * Test {@link ResourceRequestManager#addResourceRequest(ResourceRequest)}
     * when
     * {@link ResourceRequestDAOImpl#addResourceRequest(ResourceRequest)}
     * is invoked. Expects {@link IllegalArgumentException}.
     */
    @Test
    public void testAddResourceRequestWhenDaoClassInvoked() throws DAOException {
        resourceRequestManager.addResourceRequest(resourceRequest);
        Mockito.verify(resourceRequestDAO, Mockito.times(1)).addResourceRequest(resourceRequest);
    }

    /**
     * Test {@link ResourceRequestManager#addResourceRequest(ResourceRequest)}
     * when
     * {@link ResourceRequestDAOImpl#addResourceRequest(ResourceRequest)}
     * throws DAOException . Expects {@link IllegalArgumentException}.
     */
    @Test
    public void testAddResourceRequestWhenDaoThrowsException() throws ManagerException, DAOException {
        doThrow(daoException).when(resourceRequestDAO).addResourceRequest(resourceRequest);
        expectedException.expect(ManagerException.class);
        expectedException.expectCause(Matchers.<Throwable>equalTo(daoException));
        expectedException.expectMessage("Error encountered while adding resource request ");
        resourceRequestManager.addResourceRequest(resourceRequest);
    }

    /**
     * Test {@link ResourceRequestManager#addResourceRequest(ResourceRequest)}
     * when
     * {@link ResourceRequestDAOImpl#addResourceRequest(ResourceRequest)}
     * throws exception and verifies log message . Expects
     * {@link IllegalArgumentException}.
     */
    @Test
    public void testAddResourceRequestWhenDaoThrowsExceptionVerifyLogMsg() throws ManagerException, DAOException {
        doThrow(daoException).when(resourceRequestDAO).addResourceRequest(resourceRequest);
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage("Error encountered while adding resource request ");
        final TestLogger logger = TestLoggerFactory.getTestLogger(ResourceRequestManager.class);
        logger.clearAll();
        try {
            resourceRequestManager.addResourceRequest(resourceRequest);
        } catch (final ManagerException managerException) {
            final List<LoggingEvent> listevents = logger.getLoggingEvents();
            assertEquals(uk.org.lidalia.slf4jext.Level.ERROR, listevents.get(0).getLevel());
            assertEquals("Error encountered while adding resource request1", listevents.get(0).getMessage());
            throw new ManagerException(managerException.getMessage(), daoException);
        }
    }

    /**
     * Test {@link ResourceRequestManager#deleteResourceRequestsInBatch(int[])}
     * with empty requestIds. Expects {@link IllegalArgumentException}.
     */
    @Test
    public void testDeleteResourceRequestsInBatchWithRequestIdsIsEmpty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(EMPTY_REQUEST_IDS);
        resourceRequestManager.deleteResourceRequestsInBatch(new int[0]);
    }

    /**
     * Test {@link ResourceRequestManager#deleteResourceRequestsInBatch(int[])}
     * with null requestIds. Expects {@link IllegalArgumentException}.
     */
    @Test
    public void testDeleteResourceRequestsInBatchWithRequestIdsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(EMPTY_REQUEST_IDS);
        resourceRequestManager.deleteResourceRequestsInBatch(null);
    }

    /**
     * Test {@link ResourceRequestManager#deleteResourceRequestsInBatch(int[])}
     * when {@link ResourceRequestDAOImpl#deleteResourceRequests(int[])} throws
     * exception. Expects {@link IllegalArgumentException}.
     */
    @Test
    public void testDeleteResourceRequestsInBatchWhenDAOThrowsException() throws DAOException {
        doThrow(daoException).when(resourceRequestDAO).deleteResourceRequests(REQUEST_IDS);
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage(ERROR_DELETING);
        final TestLogger logger = TestLoggerFactory.getTestLogger(ResourceRequestManager.class);
        logger.clear();
        try {
            resourceRequestManager.deleteResourceRequestsInBatch(REQUEST_IDS);
        } catch (final ManagerException managerException) {
            final List<LoggingEvent> listevents = logger.getLoggingEvents();
            assertEquals(uk.org.lidalia.slf4jext.Level.ERROR, listevents.get(0).getLevel());
            assertEquals(ERROR_DELETING, listevents.get(0).getMessage());
            throw managerException;
        }
    }

    /**
     * Test {@link ResourceRequestManager#deleteResourceRequestsInBatch(int[])}
     * when {@link ResourceRequestDAOImpl#deleteResourceRequests(int[])} is
     * invoked in valid state. Expects {@link IllegalArgumentException}.
     */
    @Test
    public void testDeleteResourceRequestsInBatch() throws DAOException {
        final int[] expectedRequestIds = Arrays.copyOf(REQUEST_IDS, REQUEST_IDS.length);
        resourceRequestManager.deleteResourceRequestsInBatch(REQUEST_IDS);
        Mockito.verify(resourceRequestDAO, Mockito.times(1)).deleteResourceRequests(expectedRequestIds);
    }

    /**
     * Test
     * {@link ResourceRequestManager#getAllResourceRequestsOfUser(String)},
     * when userId is null. Expects {@link IllegalArgumentException}.
     */

    @Test
    public void testGetAllResourceRequestOfUserWhenUserIdIsNull() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(USER_ID_NULL_ERROR);
        resourceRequestManager.getAllResourceRequestsOfUser(null);
    }

    /**
     * Test
     * {@link ResourceRequestManager#getAllResourceRequestsOfUser(String)},
     * when userId is empty. Expects {@link IllegalArgumentException}.
     */
    @Test
    public void testGetAllResourceRequestOfUserWhenUserIdIsEmpty() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(USER_ID_NULL_ERROR);
        resourceRequestManager.getAllResourceRequestsOfUser("");
    }

    /**
     * Test
     * {@link ResourceRequestManager#getAllResourceRequestsOfUser(String)},
     * when userId is blank. Expects {@link IllegalArgumentException}.
     */
    @Test
    public void testGetAllResourceRequestOfUserWhenUserIdIsBlank() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(USER_ID_NULL_ERROR);
        resourceRequestManager.getAllResourceRequestsOfUser(" ");
    }

    /**
     * Test {@link ResourceRequestManager#getAllResourceRequestsOfUser(String)}
     * when {@link ResourceRequestDAOImpl#getAllResourceRequests(String)}
     * throws DAOException and verify log message.
     */
    @Test
    public void testGetAllResourceRequestsOfUserWhenDaoThrowsExceptionVerifyLogMsg()
            throws ManagerException, DAOException {
        doThrow(daoException).when(resourceRequestDAO).getAllResourceRequests(USER_ID);
        expectedException.expect(ManagerException.class);
        expectedException.expectCause(Matchers.<Throwable>equalTo(daoException));
        expectedException.expectMessage(RESOURCE_REQUEST_DB_READ_ERROR);
        final TestLogger logger = TestLoggerFactory.getTestLogger(ResourceRequestManager.class);
        logger.clearAll();
        try {
            resourceRequestManager.getAllResourceRequestsOfUser(USER_ID);
        } catch (final ManagerException managerException) {
            final List<LoggingEvent> listevents = logger.getAllLoggingEvents();
            assertEquals(uk.org.lidalia.slf4jext.Level.ERROR, listevents.get(0).getLevel());
            assertEquals(RESOURCE_REQUEST_DB_READ_LOGGER_MESSAGE, listevents.get(0).getMessage());
            throw managerException;
        }
    }

    /**
     * Test {@link ResourceRequestManager#getAllResourceRequestsOfUser(String)}
     * whether valid list is returned when there is no error.
     */
    @Test
    public void testGetAllResourceRequestsOfUser() throws DAOException {
        assertEquals(listOfResourceRequests, resourceRequestManager.getAllResourceRequestsOfUser(USER_ID));
    }
}