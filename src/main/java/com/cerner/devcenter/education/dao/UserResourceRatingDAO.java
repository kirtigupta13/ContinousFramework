package com.cerner.devcenter.education.dao;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.UserResourceRating;

/**
 * Interface that defines database operations for {@link UserResourceRating}
 * data objects.
 * 
 * @author Gunjan Kaphle (GK045931)
 * @author Asim Mohammed (AM045300)
 */
public interface UserResourceRatingDAO {

	/**
	 * Performs a query to add user id, resource id, rating and status to the
	 * user_resource_rating table in the database.
	 * 
	 * @param userId
	 *            a {@link String} that is the id of the user. Must not be
	 *            null/empty
	 * @param resourceId
	 *            an int that represents the id of the resource to add
	 *            to the database. Must be greater than 0
	 * @param rating
	 *            an int that is used to represent the rating given by
	 *            the user for a particular resource. Must be 0 or 1
	 * @param status
	 *            an int that is used to represent status of a resource
	 *            updated by user. Must be 0 or 1
	 * @return True if the rating for resource by the user
	 *         was added successfully and False if the rating
	 *         for the resource by the user was not added to the database
	 * @throws DAOException
	 *             if there is an error adding the data to the database
	 * @throws IllegalArgumentException
	 *             when any of the following are true:
	 *             <ul>
	 *             <li>userId is null or empty or whitespace</li>
	 *             <li>resourceId is less than or equal to 0</li>
	 *             <li>rating is not 0 or 1</li>
	 *             <li>status is not 0 or 1</li>
	 *             </ul>
	 */
	boolean addUserResourceRating(String userId, int resourceId, int rating, int status) throws DAOException;
}
