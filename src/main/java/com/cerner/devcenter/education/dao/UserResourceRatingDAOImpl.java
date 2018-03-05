package com.cerner.devcenter.education.dao;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.UserResourceRating;

/**
 * Responsible for performing database operations for {@link UserResourceRating}
 * objects on a table named user_resource_rating.
 * 
 * @author Gunjan Kaphle (GK045931)
 * @author Asim Mohammed (AM045300)
 */
@Repository("userResourceRatingDAO")
public class UserResourceRatingDAOImpl implements UserResourceRatingDAO {

	private static final String ADD_USER_RESOURCE_RATING = "INSERT INTO user_resource_rating (user_id, resource_id, rating, completion_status) VALUES (?,?,?,?)";

	private static final Logger LOGGER = Logger.getLogger(UserResourceRatingDAOImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public boolean addUserResourceRating(String userId, int resourceId, int rating, int status) throws DAOException {
		checkArgument(StringUtils.isNotBlank(userId), "User ID cannot be null/empty/whitespace.");
		checkArgument(resourceId > 0, "Resource ID must be greater than zero.");
		checkArgument(rating == 0 || rating == 1, "Rating must be 0 or 1");
		checkArgument(status == 0 || status == 1, "Status must be 0 or 1");

		try {
			jdbcTemplate.update(ADD_USER_RESOURCE_RATING, userId, resourceId, rating, status);
			return true;
		} catch (DataAccessException daoException) {
			LOGGER.error("Error adding user rating and status for the resource: " + resourceId + " " + daoException);
			throw new DAOException("Error: unable to execute query and add the rating for the resource.", daoException);
		}
	}
}
