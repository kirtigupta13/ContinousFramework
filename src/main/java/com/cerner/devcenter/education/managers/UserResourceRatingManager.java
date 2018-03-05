package com.cerner.devcenter.education.managers;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.controllers.UserResourceRatingController;
import com.cerner.devcenter.education.dao.UserResourceRatingDAO;
import com.cerner.devcenter.education.models.UserResourceRating;

/**
 * Service class that connects the {@link UserResourceRatingController} and the
 * {@link UserResourceRatingDAO} class.
 * 
 * @author Gunjan Kaphle (GK045931)
 * @author Asim Mohammed (AM045300)
 */
@Service("userResourceRatingManager")
public class UserResourceRatingManager {

	@Autowired
	UserResourceRatingDAO userResourceRatingDAO;

	private static final Logger LOGGER = Logger.getLogger(UserResourceRatingManager.class);

	/**
	 * Adds the {@link UserResourceRating} data to the user_resource_rating
	 * table in the database.
	 * 
	 * @param userResourceRating
	 *            a {@link UserResourceRating} object. Must not be null.
	 * @return True if the rating for resource by the user was added
	 *         successfully to the database.
	 * @throws IllegalArgumentException
	 *             when userResourceRating is null.
	 * @throws ManagerException
	 *             when unable to successfully store rating and status.
	 */
	public boolean addUserResourceRating(UserResourceRating userResourceRating) {
		checkNotNull(userResourceRating, "Resource info object passed can't be null");

		int status = userResourceRating.getStatus().getStatusValue();
		int rating = userResourceRating.getRating().getRatingValue();

		try {
			return userResourceRatingDAO.addUserResourceRating(userResourceRating.getUserId(),
					userResourceRating.getResourceId(), rating, status);
		} catch (DAOException daoException) {
			LOGGER.error("Error adding user rating and status for the resource: " + userResourceRating.getResourceId()
					+ " " + daoException);

			throw new ManagerException("Error adding user rating and status for the resource.", daoException);
		}
	}
}
