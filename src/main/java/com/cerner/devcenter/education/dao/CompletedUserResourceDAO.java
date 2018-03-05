package com.cerner.devcenter.education.dao;

import java.util.List;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.CompletedResource;
import com.cerner.devcenter.education.models.CompletedUserResource;
import com.cerner.devcenter.education.utils.CompletionRating;

/**
 * Interface that defines database operations for {@link CompletedUserResource}
 * data objects.
 *
 * @author Vinutha Nuchimaniyanda (VN046193)
 * @author Rishabh Bhojak (RB048032)
 * @author Mayur Rajendran (MT049536)
 * @author Vincent Dasari (VD049645)
 */
public interface CompletedUserResourceDAO {
    /**
     * Performs a query to add user id, resource id and completed rating to the
     * completed_user_resource table in the database.
     *
     * @param userId
     *            is the id of the user. Must not be
     *            <code>null</code>/empty/whitespace
     * @param resourceId
     *            is the id of the resource. Must be greater than 0
     * @param completedRating
     *            is used to represent the the CompletionRating enum value
     *            provided by the user for the resource being completed
     * @param completionDate
     *            is used to get the current date when the user marks the
     *            resource as complete
     * @return <code>true</code> if the rating for resource by the user was
     *         added successfully.
     * @return a true if the rating for resource by the user was added
     *         successfully.
     * @throws DAOException
     *             if there is an error adding the data to the database
     * @throws IllegalArgumentException
     *             when any of the following are true:
     *             <ul>
     *             <li>userId is <code>null</code> or empty or whitespace</li>
     *             <li>resourceId is less than or equal to 0</li>
     *             <li>completedRating is not 0 or 1</li>
     *             <li>completionDate should be greater than 0</li>
     *             </ul>
     */
    boolean addCompletedUserResourceRating(
            final String userId,
            final int resourceId,
            final CompletionRating completedRating,
            final long completionDate) throws DAOException;

    /**
     * Performs a query to retrieve the completed user resource information from
     * the completed_user_resource and resource table in the database.
     *
     * @param userId
     *            is the unique id of the user. Must not be
     *            null/empty/whitespace
     * @return a list with {@link CompletedResource} object that contains
     *         <code>resourceId</code>, <code>resourceName</code>,
     *         <code>resourceLink</code>, <code>completionRating</code> and
     *         <code>completionDate</code>. An empty list is returned if there
     *         are no completed resources for the user
     * @throws DAOException
     *             if there is an error retrieving data from the database
     * @throws IllegalArgumentException
     *             if the userId is null or empty or whitespace
     */
    List<CompletedResource> getCompletedResources(final String userId) throws DAOException;

    /**
     * Returns a {@link List} containing at most the specified number of the
     * most recently {@link CompletedResource Completed Resources} by the user.
     *
     * @param userId
     *            a string representing the unique ID of the user. Cannot be
     *            <code>null</code>, empty or whitespace.
     * @param numberOfRequiredCompletedResources
     *            an integer representing the maximum size of the {@link List}
     *            of {@link CompletedResource completed resources} to be
     *            returned. Must be greater than 0.
     * @return a {@link List} containing at most the required number of
     *         {@link CompletedResource completed resources} by the user ordered
     *         by most recently completed resources first. If no resources have
     *         been completed by the user, returns an empty {@link List}.
     * @throws DAOException
     *             if there was an error retrieving the completed resources
     * @throws IllegalArgumentException
     *             <ul>
     *             <li>if userId is <code>null</code>, empty or whitespace.</li>
     *             <li>if numberOfRequiredCompletedResources is not greater than
     *             0.</li>
     *             </ul>
     */
    List<CompletedResource> getMostRecentlyCompletedResources(String userId, int numberOfRequiredCompletedResources)
            throws DAOException;

    /**
     * Returns the number of {@link CompletedResource resources completed} by
     * the user.
     *
     * @param userId
     *            a string representing the unique ID of the user. Cannot be
     *            <code>null</code>, empty or whitespace.
     * @return the number of resources that have been completed by the user.
     *         Guaranteed not to be negative.
     * @throws IllegalArgumentException
     *             when userId is <code>null</code>, empty or whitespace
     * @throws DAOException
     *             when there is an error getting the number of resources
     *             completed by the user
     */
    int getCountOfResourcesCompletedByUser(String userId) throws DAOException;

    /**
     * Retrieves the name of the {@link Category} for which the user has
     * completed the most number of resources, or an empty string if the user
     * has no {@link CompletedResource completed resources}.
     *
     * @param userId
     *            a string representing the unique ID of the user. Cannot be
     *            <code>null</code>, empty or whitespace.
     * @return a string representing the name of the category with the most
     *         number of resources completed by the user. If the user has no
     *         completed resources, will return an empty string ("").
     * @throws IllegalArgumentException
     *             when userId is <code>null</code>, empty or whitespace
     * @throws DAOException
     *             when there is an error getting the name of the category with
     *             the most number of resources completed by the user
     *
     */
    String getCategoryNameWithMostCompletedResourcesByUser(String userId) throws DAOException;
}