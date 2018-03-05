package com.cerner.devcenter.education.managers;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cerner.devcenter.education.admin.CategoryDAO;
import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.dao.UserSubscriptionDAO;
import com.cerner.devcenter.education.exceptions.DuplicateUserSubscriptionException;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.UserSubscription;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * This class connects the controller class and the {@link UserSubscriptionDAO}
 * class.
 *
 * @author Mani Teja Kurapati (MK051340)
 */
@Service("userSubscriptionManager")
public class UserSubscriptionManager {
    @Autowired
    UserSubscriptionDAO userSubscriptionDAO;

    @Autowired
    CategoryDAO categoryDAO;

    private static final String INVALID_USER_SUBSCRIBED_CATEGORY = "User subscribed Category cannot be null.";
    private static final String ERROR_CATEGORY_ALREADY_SUBSCRIBED = "User has already subscribed to category.";
    private static final String ERROR_SUBSCRIBING_TO_CATEGORY = "Error subscribing to category by user id";
    private static final String ERROR_REMOVING_SUBSCRIPTION = "Error deleting subscription";
    private static final String ERROR_RETRIEVING_CATEGORY_IDS = "Error retrieving all subscribed categories by user id";

    public UserSubscriptionManager() {
    }

    /**
     * Adds the @{link {@link UserSubscription} data to the user_subscription
     * table in the database.
     *
     * @param userSubscription
     *            a {@link UserSubscription} object that contains the user id
     *            and category id
     * @return a {@link Boolean} value that returns true if the query runs
     *         successfully else returns false
     * @throws DuplicateUserSubscriptionException
     *             if the category has already been added in the database as
     *             subscription for that particular user
     * @throws IllegalArgumentException
     *             when the {@link UserSubscription} object is null
     */
    public boolean addUserSubscription(final UserSubscription userSubscription)
            throws DuplicateUserSubscriptionException {
        Preconditions.checkArgument(userSubscription != null, INVALID_USER_SUBSCRIBED_CATEGORY);

        try {
            if (checkIfCategoryAlreadySubscribedByUser(userSubscription)) {
                throw new DuplicateUserSubscriptionException(ERROR_CATEGORY_ALREADY_SUBSCRIBED);
            }
            return userSubscriptionDAO.addUserSusbcription(userSubscription);
        } catch (final DAOException e) {
            throw new ManagerException(ERROR_SUBSCRIBING_TO_CATEGORY, e);
        }
    }

    /**
     * Checks if user has already subscribed to category.
     *
     * @param userSubscription
     *            a {@link UserSubscription} object.
     * @return a {@link Boolean} value that returns true if the user has already
     *         subscribed to category.
     */
    boolean checkIfCategoryAlreadySubscribedByUser(final UserSubscription userSubscription) {
        final int userSubscribedCategoryId = userSubscription.getCategoryId();
        final String userId = userSubscription.getUserId();
        final List<Category> userSubscribedCategories = getSubscribedcategoriesByUser(userId);
        for (final Category SingleUserSubscribedCategory : userSubscribedCategories) {
            if (SingleUserSubscribedCategory.getId() == userSubscribedCategoryId) {
                return true;
            }
        }

        return false;
    }

    /**
     * Retrieves a {@link List} of {@link Category} objects subscribed by a
     * user.
     *
     * @param userId
     *            the id of the user for whom subscribed categories are
     *            required.
     * @return a {@link List} of {@link Category} objects subscribed by a
     *         particular user from the database
     * @throws ManagerException
     *             when there is an error retrieving all subscribed categories
     *             by particular user
     */
    public List<Category> getSubscribedcategoriesByUser(final String userId) {
        checkArgument(!Strings.isNullOrEmpty(userId), "user Id is null/empty");
        try {
            return userSubscriptionDAO.getSubscribedCategoriesByUser(userId);
        } catch (final DAOException e) {
            throw new ManagerException(ERROR_RETRIEVING_CATEGORY_IDS, e);
        }
    }

    /**
     * Deletes a {@link UserSubscription} for a particular user.
     *
     * @param userSubscription
     *            a {@link UserSubscription} object that needs to be deleted
     *            (can't be null)
     * @return a {@link Boolean} value that returns true if the userSubscription
     *         is deleted else false
     * @throws IllegalArgumentException
     *             when user subscription object is null
     */

    public boolean deleteUserSubscription(final UserSubscription userSubscription) {
        checkArgument(userSubscription != null, INVALID_USER_SUBSCRIBED_CATEGORY);
        try {
            return userSubscriptionDAO.deleteUserSubscription(userSubscription);
        } catch (final DAOException daoException) {
            throw new ManagerException(ERROR_REMOVING_SUBSCRIPTION, daoException);
        }
    }
}
