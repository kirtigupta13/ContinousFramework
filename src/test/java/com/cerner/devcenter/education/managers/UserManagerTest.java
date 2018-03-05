package com.cerner.devcenter.education.managers;

import static com.cerner.devcenter.education.utils.TestConstants.DEPARTMENT;
import static com.cerner.devcenter.education.utils.TestConstants.EMAIL_ID;
import static com.cerner.devcenter.education.utils.TestConstants.FIRST_NAME;
import static com.cerner.devcenter.education.utils.TestConstants.LAST_NAME;
import static com.cerner.devcenter.education.utils.TestConstants.MANAGER;
import static com.cerner.devcenter.education.utils.TestConstants.PROJECT;
import static com.cerner.devcenter.education.utils.TestConstants.ROLE;
import static com.cerner.devcenter.education.utils.TestConstants.USER_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.naming.NamingException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.dao.UserDAO;
import com.cerner.devcenter.education.models.User;
import com.cerner.devcenter.education.user.UserDetails;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthorizationLevel;

/**
 * Tests the functionalities of {@link UserManager}
 * 
 * @author Surbhi Singh (SS043472)
 * @author Jacob Zimmermann (JZ022690)
 */
@RunWith(MockitoJUnitRunner.class)
public class UserManagerTest {

    @InjectMocks
    private UserManager userManager;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private UserDAO userDAO;
    @Mock
    private UserDetails userDetail;
    @Mock
    private DataAccessException dataAccessException;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private User user;
    private UserProfileDetails userProfileDetails;
    private AuthorizationLevel authorization = AuthorizationLevel.ADMIN;
    private static final String VALID_USER_ID = "JK044127";
    private static final String INVALID_USER_ID = "JD0441289";
    private static final String INVALID_INPUT = "12$&1@89";
    private static final int INVALID_LEVEL = -1;
    private static final int OUT_OF_RANGE_LEVEL = 3;
    private static final int LEVEL = 1;
    private static final String NULL_USER_ID_MESSAGE = "UserId cannot be null";
    private static final String NULL_AUTHORIZATION_MESSAGE = "AuthorizationLevel cannot be null";
    private static final String ERROR_RETRIEVING_USERS_MESSAGE = "Error retrieving users with authorization level {0} from the database.";

    @Before
    public void setup() {
        user = new User(USER_ID, authorization);
        userProfileDetails = new UserProfileDetails(LAST_NAME + "," + FIRST_NAME, ROLE, USER_ID, EMAIL_ID, DEPARTMENT,
                MANAGER, PROJECT);
    }

    /**
     * Tests when valid input is provided, userId should be added.
     * 
     * @throws NamingException
     * @throws DAOException
     */
    @Test
    public void testAddUser() throws NamingException {
        userProfileDetails = new UserProfileDetails(FIRST_NAME + "," + LAST_NAME, ROLE, USER_ID, EMAIL_ID, DEPARTMENT,
                MANAGER, PROJECT);
        when(userDetail.getUserDetails(anyString())).thenReturn(userProfileDetails);
        userManager.addUser(user, userDetail);
    }

    /**
     * Tests when {@link UserDetails#getUserDetails(String)} throws
     * NamingException
     * 
     * @throws NamingException
     * @throws DAOException
     */
    @Test
    public void testAddUserWhenGetUserDetailsReturnsNamingException() throws NamingException {
        doThrow(NamingException.class).when(userDetail).getUserDetails(anyString());
        assertEquals(false, userManager.addUser(user, userDetail));
    }

    /**
     * Test verifies that an empty {@link List} is returned when there is an
     * error in accessing the database.
     */
    @Test
    public void testGetUsersDataAccessException() {
        when(jdbcTemplate.queryForList(anyString())).thenThrow(dataAccessException);
        assertEquals(userManager.getAllUsers(), Collections.emptyList());
    }

    /**
     * Test verifies {@link UserManager#isUserPresent(String)} when UserId of
     * {@link User} is null.
     */
    @Test
    public void testisUserPresent_WhenUserIdIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(NULL_USER_ID_MESSAGE);
        userManager.isUserPresent(null);
    }

    /**
     * Test verifies {@link UserManager#isUserPresent(String)} when UserId of
     * {@link User} is invalid.
     */
    @Test
    public void testisUserPresent_WhenUserIdIsInvalid() {
        when(userDAO.isUserAlreadyPresent(INVALID_USER_ID)).thenReturn(false);
        assertFalse(userManager.isUserPresent(INVALID_USER_ID));
    }

    /**
     * Test verifies {@link UserManager#isUserPresent(String)} when UserId of
     * {@link User} is valid.
     */
    @Test
    public void testisUserPresent_WhenUserIdIsValid() {
        when(userDAO.isUserAlreadyPresent(VALID_USER_ID)).thenReturn(true);
        assertTrue(userManager.isUserPresent(VALID_USER_ID));
    }

    /**
     * Test verifies {@link UserManager#isUserPresent(String)} when UserId of
     * {@link User} is in invalid format.
     */
    @Test
    public void testisUserPresent_WhenUserIdInValidFormat() {
        when(userDAO.isUserAlreadyPresent(INVALID_INPUT)).thenReturn(false);
        assertFalse(userManager.isUserPresent(INVALID_INPUT));
    }

    /**
     * Tests verifies
     * {@link UserManager#changeAuthorizationLevelBasedOnUserId(String, int)}
     * when UserId of {@link User} is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testChangeAuthorizationLevelBasedOnUserIdWhenUserIdIsNull() {
        userManager.changeAuthorizationLevelBasedOnUserId(null, LEVEL);
    }

    /**
     * Tests verifies
     * {@link UserManager#changeAuthorizationLevelBasedOnUserId(String, int)}
     * when UserId of {@link User} is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testChangeAuthorizationLevelBasedOnUserIdWhenUserIdIsEmpty() {
        userManager.changeAuthorizationLevelBasedOnUserId("", LEVEL);
    }

    /**
     * Tests verifies
     * {@link UserManager#changeAuthorizationLevelBasedOnUserId(String, int)}
     * when authLevel invalid.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testChangeAuthorizationLevelBasedOnUserIdWhenAuthLevelIsInvalid() {
        userManager.changeAuthorizationLevelBasedOnUserId(VALID_USER_ID, INVALID_LEVEL);
    }

    /**
     * Tests verifies
     * {@link UserManager#changeAuthorizationLevelBasedOnUserId(String, int)}
     * when authLevel is negative.
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testChangeAuthorizationLevelBasedOnUserIdWhenAuthLevelIsOutOfRange() {
        userManager.changeAuthorizationLevelBasedOnUserId(VALID_USER_ID, OUT_OF_RANGE_LEVEL);
    }

    /**
     * Tests verifies
     * {@link UserManager#changeAuthorizationLevelBasedOnUserId(String, int)}
     * when authLevel is negative.
     */
    @Test
    public void testChangeAuthorizationLevelBasedOnUserIdForValidInput() {
        when(userDAO.changeAuthorizationLevelBasedOnUserId(VALID_USER_ID, LEVEL)).thenReturn(true);
        userManager.changeAuthorizationLevelBasedOnUserId(VALID_USER_ID, LEVEL);
        verify(userDAO).changeAuthorizationLevelBasedOnUserId(VALID_USER_ID, LEVEL);
    }

    /***
     * Test that
     * {@link UserManager#getAllUsersWithAuthorizationLevel(AuthorizationLevel)}
     * will throw an {@link IllegalArgumentException} when authLevel is null
     * 
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test
    public void testGetAllUsersWithAuthorizationLevel_nullAuthorizationLevel() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(NULL_AUTHORIZATION_MESSAGE);
        userManager.getAllUsersWithAuthorizationLevel(null);
    }

    /***
     * Test that
     * {@link UserManager#getAllUsersWithAuthorizationLevel(AuthorizationLevel)}
     * when given a valid {@link AuthorizationLevel}
     */
    @Test
    public void testGetAllUsersWithAuthorizationLevel_validAuthorizationLevel() throws DAOException {
        List<User> expected = new ArrayList<User>(Arrays.asList(new User(VALID_USER_ID, AuthorizationLevel.ADMIN)));
        when(userDAO.getAllUsersWithAuthorizationLevel(AuthorizationLevel.ADMIN)).thenReturn(expected);
        assertEquals(expected, userManager.getAllUsersWithAuthorizationLevel(AuthorizationLevel.ADMIN));
    }

    /***
     * Test that
     * {@link UserManager#getAllUsersWithAuthorizationLevel(AuthorizationLevel)}
     * will throw a {@link ManagerException} when
     * {@link UserDAO#getAllUsersWithAuthorizationLevel(AuthorizationLevel)}
     * throws a {@link DAOException}
     * 
     * @throws ManagerException
     *             to pass the test
     */
    @Test
    public void testGetAllUsersWithAuthorizationLevel_throwsDAOException() throws DAOException {
        when(userDAO.getAllUsersWithAuthorizationLevel(AuthorizationLevel.ADMIN)).thenThrow(new DAOException());
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage(MessageFormat.format(ERROR_RETRIEVING_USERS_MESSAGE, AuthorizationLevel.ADMIN));
        userManager.getAllUsersWithAuthorizationLevel(AuthorizationLevel.ADMIN);
    }
}