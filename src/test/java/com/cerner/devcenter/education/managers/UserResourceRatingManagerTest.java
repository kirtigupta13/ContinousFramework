package com.cerner.devcenter.education.managers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.dao.UserResourceRatingDAO;
import com.cerner.devcenter.education.models.UserResourceRating;
import com.cerner.devcenter.education.utils.Rating;
import com.cerner.devcenter.education.utils.Status;

/**
 * Class to test the functionalities of {@link UserResourceRatingManager}.
 * 
 * @author Gunjan Kaphle (GK045931)
 * @author Asim Mohammed (AM045300)
 */
@RunWith(MockitoJUnitRunner.class)
public class UserResourceRatingManagerTest {

	@InjectMocks
	private UserResourceRatingManager userResourceRatingManager;

	@Mock
	private UserResourceRatingDAO mockUserResourceRatingDAO;

	public static final String VALID_USER_ID = "AB12345";
	public static final int VALID_RESOURCE_ID = 4;
	public static final int RATING_LIKE = 1;
	public static final int RATING_DISLIKE = 0;
	public static final int STATUS_COMPLETE = 1;
	public static final int STATUS_INCOMPLETE = 0;

	private UserResourceRating userResourceRating;

	@Before
	public void setup() throws DAOException {
		userResourceRating = new UserResourceRating(VALID_USER_ID, VALID_RESOURCE_ID, Rating.LIKE, Status.COMPLETE);
		when(mockUserResourceRatingDAO.addUserResourceRating(VALID_USER_ID, VALID_RESOURCE_ID, RATING_LIKE,
				STATUS_COMPLETE)).thenReturn(true);
	}

	@After
	public void tearDown() {
		userResourceRating = null;
	}

	/**
	 * Test
	 * {@link UserResourceRatingManager#addUserResourceRating(UserResourceRating)}
	 * when passed in {@link UserResourceRating} object is null. Expects
	 * {@link IllegalArgumentException}.
	 */
	@Test(expected = NullPointerException.class)
	public void testAddResourceRatingWhenUserResourceRatingNull() {
		userResourceRatingManager.addUserResourceRating(null);
	}

	/**
	 * Test
	 * {@link UserResourceRatingManager#addUserResourceRating(UserResourceRating)}
	 * when
	 * {@link UserResourceRatingDAO#addUserResourceRating(String, int, int, int)}
	 * throws {@link DAOException}. Expects {@link ManagerException}.
	 * 
	 * @throws DAOException
	 */
	@Test(expected = ManagerException.class)
	public void testAddResourceRatingThrowsManagerException() throws DAOException {
		when(mockUserResourceRatingDAO.addUserResourceRating(VALID_USER_ID, VALID_RESOURCE_ID, RATING_LIKE,
				STATUS_COMPLETE)).thenThrow(new DAOException());
		userResourceRatingManager.addUserResourceRating(userResourceRating);
	}

	/**
	 * Test
	 * {@link UserResourceRatingManager#addUserResourceRating(UserResourceRating)}
	 * when
	 * {@link UserResourceRatingDAO#addUserResourceRating(String, int, int, int)}
	 * returns true.
	 */
	@Test
	public void testAddResourceRating() {
		assertTrue(userResourceRatingManager.addUserResourceRating(userResourceRating));
	}

	/**
	 * Test
	 * {@link UserResourceRatingManager#addUserResourceRating(UserResourceRating)}
	 * when
	 * {@link UserResourceRatingDAO#addUserResourceRating(String, int, int, int)}
	 * returns false.
	 * 
	 * @throws DAOException
	 */
	@Test
	public void testAddResourceRatingUnsuccessful() throws DAOException {
		when(mockUserResourceRatingDAO.addUserResourceRating(VALID_USER_ID, VALID_RESOURCE_ID, RATING_LIKE,
				STATUS_COMPLETE)).thenReturn(false);
		assertFalse(userResourceRatingManager.addUserResourceRating(userResourceRating));
	}

	/**
	 * Test
	 * {@link UserResourceRatingManager#addUserResourceRating(UserResourceRating)}
	 * when
	 * {@link UserResourceRatingDAO#addUserResourceRating(String, int, int, int)}
	 * with rating as dislike returns true.
	 * 
	 * @throws DAOException
	 */
	@Test
	public void testAddResourceRatingWhenRatingDislike() throws DAOException {
		when(mockUserResourceRatingDAO.addUserResourceRating(VALID_USER_ID, VALID_RESOURCE_ID, RATING_DISLIKE,
				STATUS_COMPLETE)).thenReturn(true);
		userResourceRating.setRating(Rating.DISLIKE);
		assertTrue(userResourceRatingManager.addUserResourceRating(userResourceRating));
	}

	/**
	 * Test
	 * {@link UserResourceRatingManager#addUserResourceRating(UserResourceRating)}
	 * when
	 * {@link UserResourceRatingDAO#addUserResourceRating(String, int, int, int)}
	 * with resource rating is like returns true.
	 * 
	 * @throws DAOException
	 */
	@Test
	public void testAddResourceRatingWhenRatingLike() throws DAOException {
		when(mockUserResourceRatingDAO.addUserResourceRating(VALID_USER_ID, VALID_RESOURCE_ID, RATING_LIKE,
				STATUS_INCOMPLETE)).thenReturn(true);
		userResourceRating.setRating(Rating.LIKE);
		assertTrue(userResourceRatingManager.addUserResourceRating(userResourceRating));
	}

	/**
	 * Test
	 * {@link UserResourceRatingManager#addUserResourceRating(UserResourceRating)}
	 * when
	 * {@link UserResourceRatingDAO#addUserResourceRating(String, int, int, int)}
	 * with resource status as complete returns true.
	 * 
	 * @throws DAOException
	 */
	@Test
	public void testAddResourceRatingWhenStatusComplete() throws DAOException {
		when(mockUserResourceRatingDAO.addUserResourceRating(VALID_USER_ID, VALID_RESOURCE_ID, RATING_LIKE,
				STATUS_COMPLETE)).thenReturn(true);
		userResourceRating.setStatus(Status.COMPLETE);
		assertTrue(userResourceRatingManager.addUserResourceRating(userResourceRating));
	}

	/**
	 * Test
	 * {@link UserResourceRatingManager#addUserResourceRating(UserResourceRating)}
	 * when
	 * {@link UserResourceRatingDAO#addUserResourceRating(String, int, int, int)}
	 * with resource status as incomplete returns true.
	 * 
	 * @throws DAOException
	 */
	@Test
	public void testAddResourceRatingWhenStatusIncomplete() throws DAOException {
		when(mockUserResourceRatingDAO.addUserResourceRating(VALID_USER_ID, VALID_RESOURCE_ID, RATING_LIKE,
				STATUS_INCOMPLETE)).thenReturn(true);
		userResourceRating.setStatus(Status.INCOMPLETE);
		assertTrue(userResourceRatingManager.addUserResourceRating(userResourceRating));
	}
}
