package com.cerner.devcenter.education.dao;

import java.util.List;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.User;
import com.cerner.devcenter.education.models.UserInterestedCategory;

/**
 * Interface that defines database operations for {@link UserInterestedCategory}
 * data objects.
 * 
 * @author Gunjan Kaphle (GK045931)
 * @author Anudeep Kumar Gadam (AG045334)
 * @author Santosh Kumar (SK051343)
 */
public interface UserInterestedCategoryDAO {

    /**
     * Performs a query on database that returns a {@link List} of
     * {@link UserInterestedCategory} for that particular user id.
     * 
     * @param userId
     *            a {@link String} that is the id of the user. Cannot be
     *            null/empty
     * @return a {@link List} of {@link UserInterestedCategory} object that
     *         contains the {@link Category} object, skill level, and interest
     *         level for that particular user id
     * @throws DAOException
     *             if there is an error retrieving the data
     */
    List<UserInterestedCategory> getUserInterestedCategoryByUserId(String userId) throws DAOException;

    /**
     * Performs a query to add user id, category id, skill level, and interest
     * level to the user interested category table.
     * 
     * @param userId
     *            a {@link String} that is the id of the user. Cannot be
     *            null/empty
     * @param category
     *            a {@link Category} object that the user finds interesting.
     *            Cannot be null/empty
     * @param skillLevel
     *            an {@link Integer} that is used to represent the skill level
     *            of the user in that particular object. Must be between 1 and 5
     * @param interestLevel
     *            an {@link Integer} that is used to represent the interest
     *            level of the user in that particular object. Must be between 1
     *            and 5
     * @return a boolean value that returns true if the interested category for
     *         the user was added successfully
     * @throws DAOException
     *             if there is an error adding the data to database
     */
    boolean addUserInterestedCategory(String userId, Category category, int skillLevel, int interestLevel)
            throws DAOException;

    /**
     * Performs a query to delete user interested category based on passed in
     * userId and topicId.
     * 
     * @param userId
     *            a {@link String} that is the id of the user. Cannot be
     *            null/empty
     * @param topicId
     *            represents the id of the category to be retrieved. Must be
     *            greater than 0
     * @return a boolean value that returns true if the interested category for
     *         the user is deleted successfully
     * @throws IllegalArgumentException
     *             if the passed in userId or topicId are invalid
     * @throws DAOException
     *             if there is an error updating the data on the database
     */
    boolean deleteUserInterestedCategory(String userId, int topicId) throws DAOException;

    /**
     * Performs a query to delete {@link UserInterestedCategory} in batch based
     * on passed in {@link User} ID and array of {@link Category} ID
     * 
     * @param userId
     *            {@link String} that contains ID of {@link User} (can't be
     *            <code>null</code> or empty or blank)
     * @param categoryIds
     *            Array that contains {@link Category} ID (can't be
     *            <code>null</code> or empty).
     * @return a {@link Boolean} value, <code>true</code> if
     *         {@link UserInterestedCategory} is deleted else
     *         <code>false</code>.
     * @throws IllegalArgumentException
     *             <ul>
     *             <li>when <b>userId</b> is <code>null</code> or empty</li>
     *             <li>when <b>topicId</b> is empty or <code>null</code>.</li>
     *             </ul>
     * @throws DAOException
     *             if there is an error performing batch delete.
     */
    boolean deleteUserInterestedCategoryInBatch(final String userId, final int[] categoryIds) throws DAOException;

    /**
     * Performs a query to update the skill level and interest level to the user
     * interested category table according to user id and category.
     * 
     * @param userId
     *            {@link String} input given by user in order to search(cannot
     *            be empty, blank or null).
     * 
     * @param category
     *            a {@link Category} object that the user finds interesting.
     *            Cannot be null/empty
     * @param skillLevel
     *            an {@link Integer} that is used to represent the skill level
     *            of the user in that particular object. Must be between 1 and 5
     * @param interestLevel
     *            an {@link Integer} that is used to represent the interest
     *            level of the user in that particular object. Must be between 1
     *            and 5
     * @return a boolean value that returns true if the interested category for
     *         the user was updated successfully
     * @throws DAOException
     *             if there is an error updating the data on the database
     * @throws IllegalArgumentException
     *             When any of the following are true:
     *             <ul>
     *             <li>userId is null or empty</li>
     *             <li>category object is null</li>
     *             <li>skillLevel is not between 1 and 5</li>
     *             <li>interestLevel is not between 1 and 5</li>
     *             </ul>
     */
    boolean updateUserInterestedCategory(String userId, Category category, int skillLevel, int interestLevel)
            throws DAOException;
}
