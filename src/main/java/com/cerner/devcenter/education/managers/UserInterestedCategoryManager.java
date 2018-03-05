package com.cerner.devcenter.education.managers;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.dao.UserInterestedCategoryDAO;
import com.cerner.devcenter.education.exceptions.DuplicateUserInterestedCategoryException;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.UserInterestedCategory;
import com.google.common.base.Preconditions;

/**
 * This class connects the controller classes and the
 * {@link UserInterestedCategoryDAO} class.
 * 
 * @author Gunjan Kaphle (GK045931)
 * @author Anudeep Kumar Gadam (AG045334)
 * @author Santosh Kumar (SK051343)
 */
@Service("userInterestedTopicManager")
public class UserInterestedCategoryManager {
    @Autowired
    UserInterestedCategoryDAO userInterestedCategoryDAO;

    private static final String INVALID_USER_INTERESTED_CATEGORY = "User Interested Category cannot be null.";
    private static final String INVALID_USER_ID = "UserID cannot be null, blank, or empty.";
    private static final String INVALID_CATEGORY_IDS = "categoryIds cannot be null or empty";
    private static final String ERROR_CATEGORY_ALREADY_ADDED = "User has already added the category as interested.";
    private static final String ERROR_ADDING_INTERESTED_CATEGORY = "Error adding the interested category by user id";
    private static final String ERROR_DELETING_CATEGORIES = "Error deleting user interested categories";
    private static final String ERROR_DELETING_CATEGORY_BY_ID = "Error deleting resource using its ID";
    private static final String ERROR_RETRIEVING_CATEGORIES = "Error retrieving all interested categories by user id";
    private static final Logger LOGGER = Logger.getLogger(UserInterestedCategoryManager.class);

    public UserInterestedCategoryManager() {
    }

    /**
     * Adds the @{link {@link UserInterestedCategory} data to the
     * user_interested_category table in the database.
     * 
     * @param userInterestedCategory
     *            a {@link UserInterestedCategory} object that contains the user
     *            id, {@link Category} object, skill level, and interest level
     * @return a {@link Boolean} value that returns true if the query runs
     *         successfully else returns false
     * @throws DuplicateUserInterestedCategoryException
     *             if the category has already been added in the database as
     *             interested for that particular user
     * @throws IllegalArgumentException
     *             when the {@link UserInterestedCategory} object is null
     */
    public boolean addUserInterestedCategory(UserInterestedCategory userInterestedCategory)
            throws DuplicateUserInterestedCategoryException {
        Preconditions.checkArgument(userInterestedCategory != null, INVALID_USER_INTERESTED_CATEGORY);
        try {
            if (checkIfCategoryAlreadyExistsAsInterestedForUser(userInterestedCategory)) {
                throw new DuplicateUserInterestedCategoryException(ERROR_CATEGORY_ALREADY_ADDED);
            }
            return userInterestedCategoryDAO.addUserInterestedCategory(
                    userInterestedCategory.getUserID(),
                    userInterestedCategory.getCategory(),
                    userInterestedCategory.getSkillLevel(),
                    userInterestedCategory.getInterestLevel());
        } catch (DAOException e) {
            throw new ManagerException(ERROR_ADDING_INTERESTED_CATEGORY, e);
        }
    }

    /**
     * Updates the {@link UserInterestedCategory} data to the
     * user_interested_category table in the database according to user's id and
     * category id.
     * 
     * @param userInterestedCategory
     *            a {@link UserInterestedCategory} object
     * @return a {@link Boolean} value that returns true if the update query
     *         runs successfully else returns false
     * @throws ManagerException
     *             if there is an error updating the database
     */
    public boolean updateUserInterestedCategory(UserInterestedCategory userInterestedCategory) {
        Preconditions.checkArgument(userInterestedCategory != null, INVALID_USER_INTERESTED_CATEGORY);
        try {
            return userInterestedCategoryDAO.updateUserInterestedCategory(
                    userInterestedCategory.getUserID(),
                    userInterestedCategory.getCategory(),
                    userInterestedCategory.getSkillLevel(),
                    userInterestedCategory.getInterestLevel());
        } catch (DAOException e) {
            throw new ManagerException(ERROR_ADDING_INTERESTED_CATEGORY, e);
        }
    }

    /**
     * Retrieves a {@link List} of {@link UserInterestedCategory} for a
     * particular user.
     * 
     * @param userId
     *            a {@link String} that is the id of the user
     * @return a {@link List} of {@link UserInterestedCategory} related to the
     *         particular user from the database
     * @throws ManagerException
     *             when there is an error retrieving all interested topics for
     *             particular user
     */
    public List<UserInterestedCategory> getUserInterestedCategoriesById(String userId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID);
        try {
            return userInterestedCategoryDAO.getUserInterestedCategoryByUserId(userId);
        } catch (DAOException e) {
            throw new ManagerException(ERROR_RETRIEVING_CATEGORIES, e);
        }
    }

    /**
     * Checks if category already exists as interested for that particular user.
     * 
     * @param userInterestedCategory
     *            a {@link UserInterestedCategory} object
     * @return a {@link Boolean} value that returns true if the user has already
     *         added the category as one of his interests
     */
    boolean checkIfCategoryAlreadyExistsAsInterestedForUser(UserInterestedCategory userInterestedCategory) {
        String userId = userInterestedCategory.getUserID();
        int categoryId = userInterestedCategory.getCategory().getId();
        List<UserInterestedCategory> userInterestedCategories = getUserInterestedCategoriesById(userId);
        for (UserInterestedCategory singleUserInterestedCategory : userInterestedCategories) {
            if (singleUserInterestedCategory.getCategory().getId() == categoryId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes a {@link UserInterestedCategory} for a particular user.
     * 
     * @param userInterestedCategory
     *            a {@link UserInterestedCategory} object that needs to be
     *            deleted (can't be null)
     * @return a {@link Boolean} value that returns true if the
     *         userInterestedTopic is deleted else false
     * @throws IllegalArgumentException
     *             when user interested category is null
     */
    public boolean deleteUserInterestedCategory(UserInterestedCategory userInterestedCategory) {
        checkArgument(userInterestedCategory != null, INVALID_USER_INTERESTED_CATEGORY);
        try {
            return userInterestedCategoryDAO.deleteUserInterestedCategory(
                    userInterestedCategory.getUserID(),
                    userInterestedCategory.getCategory().getId());
        } catch (DAOException daoException) {
            throw new ManagerException(ERROR_DELETING_CATEGORY_BY_ID, daoException);
        }
    }

    /**
     * Deletes {@link UserInterestedCategory} for a particular user.
     * 
     * @param userId
     *            {@link String} that contains ID of user (can't be
     *            <code>null</code> or blank or empty
     * @param categoryIds
     *            Array that contains Category ID (can't be <code>null</code> or
     *            empty).
     * @return a {@link Boolean} value, true if {@link UserInterestedCategory}
     *         is deleted else false.
     * @throws IllegalArgumentException
     *             <ul>
     *             <li>when <b>userId</b> is <code>null</code> or empty</li>
     *             <li>when <b>categoryIds</b> is empty or
     *             <code>null</code>.</li>
     *             </ul>
     * @throws ManagerException
     *             if there is an error deleting {@link UserInterestedCategory}.
     */
    public boolean deleteUserInterestedCategoriesInBatch(String userId, int[] categoryIds) {
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID);
        checkArgument(ArrayUtils.isNotEmpty(categoryIds), INVALID_CATEGORY_IDS);
        try {
            return userInterestedCategoryDAO.deleteUserInterestedCategoryInBatch(userId, categoryIds);
        } catch (DAOException daoException) {
            String daoExceptionMessage = ERROR_DELETING_CATEGORIES;
            LOGGER.error(daoExceptionMessage, daoException);
            throw new ManagerException(daoExceptionMessage, daoException);
        }
    }
}
