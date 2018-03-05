package com.cerner.devcenter.education.dao;

import static com.cerner.devcenter.education.utils.AuthorizationLevel.ADMIN;
import static com.cerner.devcenter.education.utils.SqlQueries.CHANGE_AUTH_LEVEL_BASED_ON_USER_ID;
import static com.cerner.devcenter.education.utils.SqlQueries.INSERT_USER_QUERY;
import static com.cerner.devcenter.education.utils.SqlQueries.SELECT_USERS_BY_AUTHORIZATION_LEVEL;
import static com.cerner.devcenter.education.utils.SqlQueries.SELECT_USER_BY_USERID;
import static com.cerner.devcenter.education.utils.TestConstants.DEPARTMENT;
import static com.cerner.devcenter.education.utils.TestConstants.EMAIL_ID;
import static com.cerner.devcenter.education.utils.TestConstants.FIRST_NAME;
import static com.cerner.devcenter.education.utils.TestConstants.LAST_NAME;
import static com.cerner.devcenter.education.utils.TestConstants.MANAGER;
import static com.cerner.devcenter.education.utils.TestConstants.PROJECT;
import static com.cerner.devcenter.education.utils.TestConstants.USER_ID;
import static com.cerner.devcenter.education.utils.TestConstants.USER_PRESENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.dao.UserDAOImpl.UserPresentMapper;
import com.cerner.devcenter.education.dao.UserDAOImpl.UserRowMapper;
import com.cerner.devcenter.education.models.User;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthorizationLevel;;

/**
 * Unit test class for {@link UserDAOImpl}
 *
 * @author Surbhi Singh(SS043472)
 * @author Jacob Zimmermann (JZ022690)
 * @author Vincent Dasari (VD049645)
 */
@RunWith(MockitoJUnitRunner.class)
public class UserDAOImplTest {

    @InjectMocks
    private UserDAOImpl userDAOImpl;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private DataAccessException dataAccessException;
    @Mock
    private ResultSet resultSet;
    @Mock
    private DAOException daoException;
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private UserProfileDetails userProfileDeatils;
    private UserPresentMapper userMapper;
    private UserRowMapper userRowMapper;
    private User user;
    private static final String VALID_USER_ID = "JK044127";
    private static final String INVALID_USER_ID = "JD0441289";
    private static final String INVALID_INPUT = "12$&1@89";
    private static final String ROLE = "ASSOCIATE";
    private static final int INVALID_LEVEL = -1;
    private static final int OUT_OF_RANGE_LEVEL = 3;
    private static final int LEVEL = 1;
    private static final String NULL_AUTHORIZATION_LEVEL_MESSAGE = "AuthorizationLevel cannot be null.";
    private static final String GET_AUTH_LEVEL_USERS_ERROR = "Unable to get users with authorization level from database";

    /**
     * Sets up shared resources for each test case
     */
    @Before
    public void setUp() throws SQLException {

        MockitoAnnotations.initMocks(this);
        user = new User(USER_ID, ADMIN, FIRST_NAME, LAST_NAME, EMAIL_ID, ROLE);
        userProfileDeatils = new UserProfileDetails(LAST_NAME + "," + FIRST_NAME, ROLE, USER_ID, EMAIL_ID, DEPARTMENT,
                MANAGER, PROJECT);
        userMapper = userDAOImpl.new UserPresentMapper();
        userRowMapper = userDAOImpl.new UserRowMapper();
        when(resultSet.getString("user_id")).thenReturn(USER_ID);
        when(resultSet.getString("first_name")).thenReturn(FIRST_NAME);
        when(resultSet.getString("last_name")).thenReturn(LAST_NAME);
        when(resultSet.getString("email_id")).thenReturn(EMAIL_ID);
        when(resultSet.getString("role")).thenReturn(ROLE);
        when(resultSet.getInt("auth_level")).thenReturn(0);
    }

    /**
     * Tear down.
     */
    @After
    public void tearDown() {
        user = null;
        userDAOImpl = null;
        jdbcTemplate = null;
        resultSet = null;
        dataAccessException = null;
        userMapper = null;

    }

    /**
     * Test for {@link UserPresentMapper#mapRow(ResultSet, int)}. Tests method
     * when the result set from the database is a valid set.
     *
     * @throws SQLException
     *             if a SQLException is encountered getting column values
     */
    @Test
    public void mapRowValidResultSet() throws SQLException {
        when(resultSet.getBoolean("user_status")).thenReturn(USER_PRESENT);
        Boolean expectedUserStatus = userMapper.mapRow(resultSet, 1);
        assertEquals(expectedUserStatus, USER_PRESENT);
    }

    /**
     * Test for {@link UserDAOImpl#isUserPresent(String)}. Test method when the
     * query does not return anything.Most likely caused by an invalid query or
     * a table with no data.
     *
     * @throws DAOException
     */
    public void checkDuplicateDataQueryReturnsNothing() throws DAOException {
        when(jdbcTemplate.queryForObject(anyString(), Mockito.any(Object[].class),
                Mockito.any(UserPresentMapper.class))).thenThrow(new EmptyResultDataAccessException(0));
        userDAOImpl.isUserPresent(USER_ID);
    }

    /**
     * Test for {@link UserDAOImpl#addUser(UserProfileDetails, String, int)}. Test method
     * when the insert statement fails to complete successfully.
     *
     * @throws DAOException
     */
    public void testAddUserUpdateFails() throws DAOException {
        String[] userName = userProfileDeatils.getName().split(",");

        when(jdbcTemplate.update(INSERT_USER_QUERY.getQuery(),
                new Object[] { userProfileDeatils.getUserId(), userName[1], userName[0], userProfileDeatils.getEmail(),
                        userProfileDeatils.getRole(), AuthorizationLevel.ADMIN.getLevel() })).thenThrow(daoException);

        userDAOImpl.addUser(userProfileDeatils, "ADMIN", 0);
    }

    /**
     * Test for {@link UserDAOImpl#addUser(UserProfileDetails, String, int)}. Test method
     * when query complete successfully.
     */
    @Test
    public void testAddUserSuccess() throws Exception {
        String[] userName = userProfileDeatils.getName().split(",");
        when(jdbcTemplate
                .queryForObject(INSERT_USER_QUERY.getQuery(),
                        new Object[] { userProfileDeatils.getUserId(), userName[1], userName[0],
                                userProfileDeatils.getEmail(), userProfileDeatils.getRole(), 0 },
                        Integer.class)).thenReturn(1);
        userDAOImpl.addUser(userProfileDeatils, "ADMIN", 0);
    }

    /**
     * This function verifies {@link UserRowMapper#mapRow(ResultSet, int)}
     * functionality
     */
    @Test
    public void testMapRowValidResultSet() throws SQLException {
        user = userRowMapper.mapRow(resultSet, 1);
        assertEquals(FIRST_NAME, user.getFirstName());
        assertEquals(LAST_NAME, user.getLastName());
        assertEquals(EMAIL_ID, user.getEmail());
        assertEquals(ROLE, user.getRole());
    }

    /**
     * Test verifies that an empty {@link List} is returned when there is an
     * error in accessing the database.
     */
    @Test
    public void testGetUsersDataAccessException() {
        when(jdbcTemplate.queryForList(anyString())).thenThrow(dataAccessException);
        assertEquals(userDAOImpl.getAllUsers(), Collections.emptyList());
    }

    /**
     * Test verifies {@link UserDAOImpl#isUserAlreadyPresent(String)} returns
     * false when DataAccessException occurred.
     *
     * @throws DataAccessException
     */
    @Test
    public void testisUserAlreadyPresent_WhenDataAccessExceptionOccurred() throws DataAccessException {
        when(jdbcTemplate.queryForObject(SELECT_USER_BY_USERID.getQuery(), String.class, user.getUserID()))
                .thenThrow(dataAccessException);
        assertFalse(userDAOImpl.isUserAlreadyPresent(user.getUserID()));
    }

    /**
     * Test verifies {@link UserDAOImpl#isUserAlreadyPresent(String)} when
     * UserId of {@link User} is null.
     */
    @Test
    public void testisUserAlreadyPresent_WhenUserIdIsNull() {
        when(jdbcTemplate.queryForObject(SELECT_USER_BY_USERID.getQuery(), String.class, (String) null))
                .thenThrow(dataAccessException);
        assertFalse(userDAOImpl.isUserAlreadyPresent(null));
    }

    /**
     * Test verifies {@link UserDAOImpl#isUserAlreadyPresent(String)} when
     * UserId of {@link User} is invalid.
     */
    @Test
    public void testisUserAlreadyPresent_WhenUserIdIsInvalid() {
        when(jdbcTemplate.queryForObject(SELECT_USER_BY_USERID.getQuery(), String.class, INVALID_USER_ID))
                .thenReturn(null);
        assertFalse(userDAOImpl.isUserAlreadyPresent(INVALID_USER_ID));
    }

    /**
     * Test verifies {@link UserDAOImpl#isUserAlreadyPresent(String)} when
     * UserId of {@link User} is valid.
     */
    @Test
    public void testisUserAlreadyPresent_WhenUserIdIsValid() {
        when(jdbcTemplate.queryForObject(SELECT_USER_BY_USERID.getQuery(), String.class, VALID_USER_ID))
                .thenReturn(VALID_USER_ID);
        assertEquals(true, userDAOImpl.isUserAlreadyPresent(VALID_USER_ID));
    }

    /**
     * Test verifies {@link UserDAOImpl#isUserAlreadyPresent(String)} when
     * UserId of {@link User} is in invalid format.
     */
    @Test
    public void testisUserAlreadyPresent_WhenUserIdInValidFormat() {
        when(jdbcTemplate.queryForObject(SELECT_USER_BY_USERID.getQuery(), String.class, INVALID_INPUT))
                .thenThrow(dataAccessException);
        assertFalse(userDAOImpl.isUserAlreadyPresent(INVALID_INPUT));
    }

    /**
     * This function tests
     * {@link UserDAOImpl#changeAuthorizationLevelBasedOnUserId(String, int)}
     * functionality and expects {@link IllegalArgumentException} when userId is
     * passed in as null
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testChangeAuthorizationLevelBasedOnUserIdForNullUserId() {
        userDAOImpl.changeAuthorizationLevelBasedOnUserId(null, LEVEL);
    }

    /**
     * This function tests
     * {@link UserDAOImpl#changeAuthorizationLevelBasedOnUserId(String, int)}
     * functionality and expects {@link IllegalArgumentException} when userId is
     * passed in as empty.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testChangeAuthorizationLevelBasedOnUserIdForEmptyUserId() {
        userDAOImpl.changeAuthorizationLevelBasedOnUserId("", LEVEL);
    }

    /**
     * This function tests
     * {@link UserDAOImpl#changeAuthorizationLevelBasedOnUserId(String, int)}
     * functionality and expects {@link IllegalArgumentException} when authLevel
     * is passed in as a negative int.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testChangeAuthorizationLevelBasedOnUserIdForNegativeAuthLevel() {
        userDAOImpl.changeAuthorizationLevelBasedOnUserId(VALID_USER_ID, INVALID_LEVEL);
    }

    /**
     * This function tests
     * {@link UserDAOImpl#changeAuthorizationLevelBasedOnUserId(String, int)}
     * functionality and expects {@link IllegalArgumentException} when authLevel
     * is passed in as a out of range.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testChangeAuthorizationLevelBasedOnUserIdForOutOfRangeAuthLevel() {
        userDAOImpl.changeAuthorizationLevelBasedOnUserId(VALID_USER_ID, OUT_OF_RANGE_LEVEL);
    }

    /**
     * This function tests
     * {@link UserDAOImpl#changeAuthorizationLevelBasedOnUserId(String, int)}
     * functionality for valid input.
     *
     * @throws DAOException
     */
    @Test
    public void testChangeAuthorizationLevelBasedOnUserIdForValidInput() {
        when(jdbcTemplate.update(CHANGE_AUTH_LEVEL_BASED_ON_USER_ID.getQuery(), ROLE, LEVEL, VALID_USER_ID))
                .thenReturn(1);
        userDAOImpl.changeAuthorizationLevelBasedOnUserId(VALID_USER_ID, LEVEL);
        verify(jdbcTemplate).update(CHANGE_AUTH_LEVEL_BASED_ON_USER_ID.getQuery(), ROLE, LEVEL, VALID_USER_ID);
    }

    /***
     * Verify that
     * {@link UserDAO#getAllUsersWithAuthorizationLevel(AuthorizationLevel)}
     * will throw an {@link IllegalArgumentException} when given a null
     * {@link AuthorizationLevel}
     */
    @Test
    public void testGetAllUsersWithAuthorizationLevel_nullAuthLevel() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(NULL_AUTHORIZATION_LEVEL_MESSAGE);
        userDAOImpl.getAllUsersWithAuthorizationLevel(null);
    }

    /***
     * Verify
     * {@link UserDAO#getAllUsersWithAuthorizationLevel(AuthorizationLevel)}
     * returns a {@link List} of {@link User} when given a valid
     * {@link AuthorizationLevel}
     */
    @Test
    public void testGetAllUsersWithAuthorizationLevel_validAuthLevel() throws DAOException {
        List<User> expected = new ArrayList<User>(Arrays.asList(new User(VALID_USER_ID, AuthorizationLevel.ADMIN)));
        when(jdbcTemplate.query(eq(SELECT_USERS_BY_AUTHORIZATION_LEVEL.getQuery()), any(UserRowMapper.class),
                eq(AuthorizationLevel.ADMIN.getLevel()))).thenReturn(expected);
        assertEquals(expected, userDAOImpl.getAllUsersWithAuthorizationLevel(AuthorizationLevel.ADMIN));
    }

    /***
     * Verify that
     * {@link UserDAO#getAllUsersWithAuthorizationLevel(AuthorizationLevel)}
     * will throw an {@link DAOException} when jdbcTemplate throws a
     * {@link DataAccessException}
     */
    @Test
    public void testGetAllUsersWithAuthorizationLevel_throwsDAOException() throws DAOException {
        expectedException.expect(DAOException.class);
        expectedException.expectMessage(GET_AUTH_LEVEL_USERS_ERROR);
        when(jdbcTemplate.query(eq(SELECT_USERS_BY_AUTHORIZATION_LEVEL.getQuery()), any(UserRowMapper.class),
                eq(AuthorizationLevel.ADMIN.getLevel()))).thenThrow(dataAccessException);
        userDAOImpl.getAllUsersWithAuthorizationLevel(AuthorizationLevel.ADMIN);
    }

}
