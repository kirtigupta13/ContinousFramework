package com.cerner.devcenter.education.dao;

import java.util.List;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.Learner;
import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.models.UserSubscription;

/**
 * This interface is responsible for performing database operations for
 * {@link UserSubscription} objects.
 *
 * @author Mani Teja Kurapati (MK051340)
 */
public interface UserSubscriptionDAO {

    /**
     * Perform an operation to add a user subscription
     *
     * @param userSubscription
     *            the {@link UserSubscription} object that has user subscription
     *            information
     * @return true if subscription is added to database successfully or else
     *         returns false
     * @throws DAOException
     *             if there is an error in executing update
     */
    public boolean addUserSusbcription(UserSubscription userSubscription) throws DAOException;

    /**
     * Deletes a {@link UserSubscription} from the database corresponding to
     * passed userSubscription.
     *
     * @param userSubscription
     *            the {@link UserSubscription} object that has user subscription
     *            information
     * @return true if subscription is deleted successfully or else returns
     *         false
     * @throws DAOException
     *             when there is an error while trying to delete
     *             userSubscription from database
     */
    boolean deleteUserSubscription(UserSubscription userSubscription) throws DAOException;

    /**
     * This function returns a list of all users subscribed to a particular
     * category from database.
     *
     * @param category
     *            the {@link Category} object that contain's category id
     *            information.
     * @return a {@link List} of user Id's subscribed to particular category
     *         from database
     * @throws DAOException
     *             when there is an error while trying to get the user Id's from
     *             database
     */
    List<String> getSubscribedUsersByCategory(Category category) throws DAOException;

    /**
     * This function returns a list of all categories subscribed by a particular
     * user.
     *
     * @param userId
     *            the id of the user
     * @return a {@link List} of {@link Category} objects subscribed by
     *         particular user from database
     *
     * @throws DAOException
     *             when there is an error while trying to get the list of
     *             {@link Category} from database
     */
    List<Category> getSubscribedCategoriesByUser(String userId) throws DAOException;

    /**
     * This function returns a list of Learners subscribed to categories
     * belonging to a particular resource.
     *
     * @param resource
     *            {@link Resource} object which contains resource information
     *            which should include resource id.
     *
     * @return a {@link List} of {@link Learner} objects which contains learners
     *         subscribed to categories belonging to supplied {@link Resource}.
     *
     * @throws DAOException
     *             when there is an error while trying to execute the query.
     */
    List<Learner> getLearnersSubscribedToCategoriesBelongingToResource(Resource resource) throws DAOException;
}
