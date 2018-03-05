package com.cerner.devcenter.education.dao;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cerner.devcenter.education.admin.DAOException;

/**
 * Tests the functionalities of {@link UserResourceRatingDAOImpl}
 * 
 * @author Gunjan Kaphle (GK045931)
 */
@RunWith(MockitoJUnitRunner.class)
public class UserResourceRatingDAOImplTest {

	@InjectMocks
	private UserResourceRatingDAOImpl userResourceRatingDAOImpl;

	@Mock
	private JdbcTemplate jdbcTemplate;
	@Mock
	private DataAccessException dataAccessException;

	private static final String ADD_USER_RESOURCE_RATING = "INSERT INTO user_resource_rating (user_id, resource_id, rating, completion_status) VALUES (?,?,?,?)";
	private static final String VALID_USER_ID = "AB12345";
	private static final int VALID_RESOURCE_ID = 4;
	private static final int INVALID_RESOURCE_ID = -7;
	private static final int VALID_RATING = 1;
	private static final int VALID_STATUS = 1;
	private static final int SMALL_VALUE = 1;

	@Before
	public void setup() {
		when(jdbcTemplate.update(ADD_USER_RESOURCE_RATING, VALID_USER_ID, VALID_RESOURCE_ID, VALID_RATING,
				VALID_STATUS)).thenReturn(1);
	}

	@After
	public void tearDown() {
		jdbcTemplate = null;
		dataAccessException = null;
	}

	/**
	 * Test
	 * {@link UserResourceRatingDAOImpl#addUserResourceRating(String, int, int, int)}
	 * when user id is null. Expects {@link IllegalArgumentException}.
	 * 
	 * @throws DAOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddUserResourceRatingWhenUserIdNull() throws DAOException {
		userResourceRatingDAOImpl.addUserResourceRating(null, VALID_RESOURCE_ID, VALID_RATING, VALID_STATUS);
	}

	/**
	 * Test
	 * {@link UserResourceRatingDAOImpl#addUserResourceRating(String, int, int, int)}
	 * when user id is empty. Expects {@link IllegalArgumentException}.
	 * 
	 * @throws DAOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddUserResourceRatingWhenUserIdEmpty() throws DAOException {
		userResourceRatingDAOImpl.addUserResourceRating("", VALID_RESOURCE_ID, VALID_RATING, VALID_STATUS);
	}

	/**
	 * Test
	 * {@link UserResourceRatingDAOImpl#addUserResourceRating(String, int, int, int)}
	 * when user id is just white spaces. Expects
	 * {@link IllegalArgumentException}.
	 * 
	 * @throws DAOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddUserResourceRatingWhenUserIdWhitespaces() throws DAOException {
		userResourceRatingDAOImpl.addUserResourceRating("    ", VALID_RESOURCE_ID, VALID_RATING, VALID_STATUS);
	}

	/**
	 * Test
	 * {@link UserResourceRatingDAOImpl#addUserResourceRating(String, int, int, int)}
	 * when resource id is negative. Expects {@link IllegalArgumentException}.
	 * 
	 * @throws DAOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddUserResourceRatingWhenResourceIdNegative() throws DAOException {
		userResourceRatingDAOImpl.addUserResourceRating(VALID_USER_ID, INVALID_RESOURCE_ID, VALID_RATING, VALID_STATUS);
	}

	/**
	 * Test
	 * {@link UserResourceRatingDAOImpl#addUserResourceRating(String, int, int, int)}
	 * when resource id is zero. Expects {@link IllegalArgumentException}.
	 * 
	 * @throws DAOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddUserResourceRatingWhenResourceIdZero() throws DAOException {
		userResourceRatingDAOImpl.addUserResourceRating(VALID_USER_ID, 0, VALID_RATING, VALID_STATUS);
	}

	/**
	 * Test
	 * {@link UserResourceRatingDAOImpl#addUserResourceRating(String, int, int, int)}
	 * when rating is dislike.
	 * 
	 * @throws DAOException
	 */
	@Test
	public void testAddUserResourceWhenRatingDislike() throws DAOException {
		assertTrue(userResourceRatingDAOImpl.addUserResourceRating(VALID_USER_ID, VALID_RESOURCE_ID,
				VALID_RATING - SMALL_VALUE, VALID_STATUS));
	}

	/**
	 * Test
	 * {@link UserResourceRatingDAOImpl#addUserResourceRating(String, int, int, int)}
	 * when rating is like.
	 * 
	 * @throws DAOException
	 */
	@Test
	public void testAddUserResourceWhenRatingLike() throws DAOException {
		assertTrue(userResourceRatingDAOImpl.addUserResourceRating(VALID_USER_ID, VALID_RESOURCE_ID, VALID_RATING,
				VALID_STATUS));
	}

	/**
	 * Test
	 * {@link UserResourceRatingDAOImpl#addUserResourceRating(String, int, int, int)}
	 * when rating is below lower limit. Expects
	 * {@link IllegalArgumentException}.
	 * 
	 * @throws DAOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddUserResourceWhenRatingBelowValid() throws DAOException {
		userResourceRatingDAOImpl.addUserResourceRating(VALID_USER_ID, VALID_RESOURCE_ID,
				VALID_RATING - 2 * SMALL_VALUE, VALID_STATUS);
	}

	/**
	 * Test
	 * {@link UserResourceRatingDAOImpl#addUserResourceRating(String, int, int, int)}
	 * when rating is above upper limit. Expects
	 * {@link IllegalArgumentException}.
	 * 
	 * @throws DAOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddUserResourceWhenRatingOverValid() throws DAOException {
		userResourceRatingDAOImpl.addUserResourceRating(VALID_USER_ID, VALID_RESOURCE_ID, VALID_RATING,
				VALID_STATUS + SMALL_VALUE);
	}

	/**
	 * Test
	 * {@link UserResourceRatingDAOImpl#addUserResourceRating(String, int, int, int)}
	 * when status is a valid (INCOMPLETE) value.
	 * 
	 * @throws DAOException
	 */
	@Test
	public void testAddUserResourceStatusWhenIncomplete() throws DAOException {
		assertTrue(userResourceRatingDAOImpl.addUserResourceRating(VALID_USER_ID, VALID_RESOURCE_ID, VALID_RATING,
				VALID_STATUS - SMALL_VALUE));
	}

	/**
	 * Test
	 * {@link UserResourceRatingDAOImpl#addUserResourceRating(String, int, int, int)}
	 * when status is a valid (COMPLETE) value.
	 * 
	 * @throws DAOException
	 */
	@Test
	public void testAddUserResourceStatusWhenComplete() throws DAOException {
		assertTrue(userResourceRatingDAOImpl.addUserResourceRating(VALID_USER_ID, VALID_RESOURCE_ID, VALID_RATING,
				VALID_STATUS));
	}

	/**
	 * Test
	 * {@link UserResourceRatingDAOImpl#addUserResourceRating(String, int, int, int)}
	 * when status is below lower limit. Expects
	 * {@link IllegalArgumentException}.
	 * 
	 * @throws DAOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddUserResourceStatusWhenBelowValid() throws DAOException {
		userResourceRatingDAOImpl.addUserResourceRating(VALID_USER_ID, VALID_RESOURCE_ID, VALID_RATING,
				VALID_STATUS - 2 * SMALL_VALUE);
	}

	/**
	 * Test
	 * {@link UserResourceRatingDAOImpl#addUserResourceRating(String, int, int, int)}
	 * when status is above upper limit. Expects
	 * {@link IllegalArgumentException}.
	 * 
	 * @throws DAOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddUserResourceStatusWhenOverValid() throws DAOException {
		userResourceRatingDAOImpl.addUserResourceRating(VALID_USER_ID, VALID_RESOURCE_ID, VALID_RATING,
				VALID_STATUS + SMALL_VALUE);
	}

	/**
	 * Test
	 * {@link UserResourceRatingDAOImpl#addUserResourceRating(String, int, int, int)}
	 * when
	 * {@link JdbcTemplate#update(org.springframework.jdbc.core.PreparedStatementCreator)}
	 * throws {@link DataAccessException}. Expects {@link DAOException}.
	 * 
	 * @throws DAOException
	 */
	@Test(expected = DAOException.class)
	public void testAddUserResourceRatingWhenJdbcThrowsDataAccessException() throws DAOException {
		when(jdbcTemplate.update(ADD_USER_RESOURCE_RATING, VALID_USER_ID, VALID_RESOURCE_ID, VALID_RATING,
				VALID_STATUS)).thenThrow(dataAccessException);
		userResourceRatingDAOImpl.addUserResourceRating(VALID_USER_ID, VALID_RESOURCE_ID, VALID_RATING, VALID_STATUS);
	}

	/**
	 * Test
	 * {@link UserResourceRatingDAOImpl#addUserResourceRating(String, int, int, int)}
	 * when all inputs are valid.
	 * 
	 * @throws DAOException
	 */
	@Test
	public void testAddUserResourceRatingWhenAllInputsAreValid() throws DAOException {
		boolean isAddToDbSuccessful = userResourceRatingDAOImpl.addUserResourceRating(VALID_USER_ID, VALID_RESOURCE_ID,
				VALID_RATING, VALID_STATUS);
		assertTrue(isAddToDbSuccessful);
	}
}
