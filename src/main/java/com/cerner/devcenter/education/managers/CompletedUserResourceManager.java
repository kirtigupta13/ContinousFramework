package com.cerner.devcenter.education.managers;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.dao.CompletedUserResourceDAO;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.CompletedResource;
import com.cerner.devcenter.education.models.CompletedUserResource;

/**
 * Manager class that acts as a Service between the controller class and
 * {@link CompletedUserResourceDAO}
 *
 * @author Vinutha Nuchimaniyanda (VN046193)
 * @author Rishabh Bhojak (RB048032)
 * @author Mayur Rajendran (MT049536)
 * @author Vincent Dasari (VD049645)
 */
@Service("completedUserResourceManager")
public class CompletedUserResourceManager {

    private static final String COMPLETED_RESOURCE_NOT_NULL_ERROR_MESSAGE = "Completed resource information object cannot be null";
    private static final String INVALID_NUMBER_OF_COMPLETED_RESOURCES_ERROR_MESSAGE = "Number of Completed Resources to display was not greater than 0";
    private static final String USER_ID_ILLEGAL_ARGUMENT_ERROR_MESSAGE = "User ID cannot be null/empty/whitespace";

    private static final String ERROR_ADDING_COMPLETION_EXCEPTION_ERROR_MESSAGE = "Error adding completion rating for the resource: %d and user: %s  with the exception: %s";
    private static final String ERROR_ADD_COMPLETION_RATING_ERROR_MESSAGE = "Error: unable to execute query and add the completion rating for the user.";
    private static final String ERROR_EXECUTING_QUERY_ERROR_MESSAGE = "Error executing query to retrieve completed resources for the user: %s with the exception %s";
    private static final String ERROR_RETRIEVING_RESULTS_ERROR_MESSAGE = "Error: unable to execute query to retrieve completed resources for the user.";
    private static final String RETURNED_LIST_OF_COMPLETED_RESOURCES_NULL_ERROR_MESSAGE = "The returned list was null.";

    private static final Logger LOGGER = Logger.getLogger(CompletedUserResourceManager.class);

    @Autowired
    private CompletedUserResourceDAO completedUserResourceDAO;

    /**
     * Adds the {@link CompletedUserResource} data to the
     * completed_user_resource table in the database.
     *
     * @param completedUserResource
     *            a {@link CompletedUserResource} object. Must not be null.
     * @return a true if the rating for resource was added successfully to the
     *         database.
     * @throws IllegalArgumentException
     *             when completedUserResource is null.
     * @throws ManagerException
     *             when unable to successfully save the rating.
     */
    public boolean addCompletedUserResourceRating(final CompletedUserResource completedUserResource)
            throws ManagerException {
        checkNotNull(completedUserResource, COMPLETED_RESOURCE_NOT_NULL_ERROR_MESSAGE);
        try {
            return completedUserResourceDAO.addCompletedUserResourceRating(completedUserResource.getUserId(),
                    completedUserResource.getResourceId(), completedUserResource.getCompletedRating(),
                    completedUserResource.getCompletionDate());
        } catch (final DAOException daoException) {
            LOGGER.error(String.format(ERROR_ADDING_COMPLETION_EXCEPTION_ERROR_MESSAGE,
                    completedUserResource.getResourceId(), completedUserResource.getUserId(), daoException));
            throw new ManagerException(ERROR_ADD_COMPLETION_RATING_ERROR_MESSAGE, daoException);
        }
    }

    /**
     * Retrieves the completed user resource information from
     * completed_user_resource and resource tables in the database.
     *
     * @param userId
     *            is the unique id of the user. Must not be
     *            null/empty/whitespace
     * @return a list with {@link CompletedResource} object that contains
     *          <code>resourceId</code>, <code>resourceName</code>,
     *          <code>resourceLink</code>, <code>completionRating</code> and
     *          <code>completionDate</code>
     * @throws IllegalArgumentException
     *             when userId is null or empty or whitespace
     * @throws ManagerException
     *             when unable to retrieve completed resources for a user
     */
    public List<CompletedResource> getCompletedResourcesByUserId(final String userId) throws ManagerException {
        checkArgument(StringUtils.isNotBlank(userId), USER_ID_ILLEGAL_ARGUMENT_ERROR_MESSAGE);
        try {
            return completedUserResourceDAO.getCompletedResources(userId);
        } catch (final DAOException daoException) {
            LOGGER.error(String.format(ERROR_EXECUTING_QUERY_ERROR_MESSAGE, userId, daoException));
            throw new ManagerException(ERROR_RETRIEVING_RESULTS_ERROR_MESSAGE, daoException);
        }
    }

    /**
     * Retrieves a {@link CompletedResource} {@link List} of specified size for
     * display in the Completed Resource Widget.
     *
     * @param userId
     *            a string representing the unique ID of the user. Cannot be
     *            <code>null</code>, blank, or whitespace.
     * @param numberOfRequiredCompletedResources
     *            an integer representing the maximum number of completed
     *            resources that should be in the returned list. Cannot be 0 or
     *            negative.
     * @return a {@link List} of {@link CompletedResource resources completed}
     *         by the user of the specified size, or smaller. Guaranteed not to
     *         be <code>null</code> nor contain any <code>null</code> elements.
     *         Might be empty if there are no {@link CompletedResource completed
     *         resources} for the user.
     * @throws IllegalArgumentException
     *             <ul>
     *             <li>when userId is <code>null</code>, empty or
     *             whitespace</li>
     *             <li>when numberOfRequiredCompletedResources is not greater
     *             than 0</li>
     *             </ul>
     * @throws ManagerException
     *             when unable to retrieve the completed resources of the user
     */
    public List<CompletedResource> getMostRecentlyCompletedResources(final String userId,
            final int numberOfRequiredCompletedResources) {
        checkArgument(StringUtils.isNotBlank(userId), USER_ID_ILLEGAL_ARGUMENT_ERROR_MESSAGE);
        checkArgument(numberOfRequiredCompletedResources > 0, INVALID_NUMBER_OF_COMPLETED_RESOURCES_ERROR_MESSAGE);
        final List<CompletedResource> widgetCompletedResources = new ArrayList<CompletedResource>();
        final List<CompletedResource> intermediateCompletedResourcesList;
        try {
            intermediateCompletedResourcesList = completedUserResourceDAO.getMostRecentlyCompletedResources(userId,
                    numberOfRequiredCompletedResources);
        } catch (final DAOException daoException) {
            LOGGER.error(String.format(ERROR_EXECUTING_QUERY_ERROR_MESSAGE, userId, daoException));
            throw new ManagerException(ERROR_RETRIEVING_RESULTS_ERROR_MESSAGE, daoException);
        }
        if (intermediateCompletedResourcesList == null) {
            LOGGER.error(RETURNED_LIST_OF_COMPLETED_RESOURCES_NULL_ERROR_MESSAGE);
            return Collections.<CompletedResource>emptyList();
        } else {
            for (final CompletedResource completedResource : intermediateCompletedResourcesList) {
                if (completedResource != null) {
                    widgetCompletedResources.add(completedResource);
                }
            }
            return widgetCompletedResources;
        }
    }

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
     * @throws ManagerException
     *             when there is an error getting the number of resources
     *             completed by the user
     */
    public int getCountOfResourcesCompletedByUser(final String userId) {
        checkArgument(StringUtils.isNotBlank(userId), USER_ID_ILLEGAL_ARGUMENT_ERROR_MESSAGE);
        try {
            return completedUserResourceDAO.getCountOfResourcesCompletedByUser(userId);
        } catch (final DAOException daoException) {
            throw new ManagerException(daoException.getMessage(), daoException);
        }
    }

    /**
     * Retrieves the name of the {@link Category} for which the user has
     * completed the most number of resources, or an empty string if the user
     * has no {@link CompletedResource completed resources}.
     *
     * @param userId
     *            a string representing the unique ID of the user. Cannot be
     *            <code>null</code>, empty or whitespace.
     * @return a string representing the name of the category with the most
     *         number of resources completed by the user. Guaranteed not to be
     *         <code>null</code> or empty. If the user has no completed
     *         resources, will return an empty string ("").
     * @throws IllegalArgumentException
     *             when userId is <code>null</code>, empty or whitespace
     * @throws ManagerException
     *             when there is an error getting the name of the category with
     *             the most number of resources completed by the user
     */
    public String getCategoryNameWithMostNumberOfResourcesCompletedByUser(final String userId) {
        checkArgument(StringUtils.isNotBlank(userId), USER_ID_ILLEGAL_ARGUMENT_ERROR_MESSAGE);
        try {
            return completedUserResourceDAO.getCategoryNameWithMostCompletedResourcesByUser(userId);
        } catch (final DAOException daoException) {
            throw new ManagerException(daoException.getMessage(), daoException);
        }
    }
}