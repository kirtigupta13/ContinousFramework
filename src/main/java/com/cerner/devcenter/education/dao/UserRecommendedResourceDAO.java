package com.cerner.devcenter.education.dao;

import java.util.List;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.UserRecommendedResource;

/**
 * Defines the operations for the {@link UserRecommendedResource} objects.
 *
 * @author Gunjan Kaphle (GK045931)
 * @author Mayur Rajendran (MT049536)
 */
public interface UserRecommendedResourceDAO {

    /**
     * Performs a query on the data source that will return a {@link List} of
     * {@link UserRecommendedResource} objects that contains the resources
     * recommended to user based on interest and skill level. The recommended
     * resources are returned in descending order of interest level, average
     * rating and number of ratings.
     *
     * @param userId
     *            a {@link String} that is the unique id of the user. Cannot be
     *            null, empty, or blank.
     * @param minimumRatingRequired
     *            a {@link Double} representing the minimum rating required for
     *            the resource to be ranked above resources that do not have a
     *            rating or enough ratings. Cannot be negative.
     * @param minimumNumberOfRatingsRequired
     *            an {@link Integer} representing the minimum number of ratings
     *            before a resource can be considered to be rated
     * @return a sorted {@link List} (by interest level, average rating and
     *         number of ratings) of {@link UserRecommendedResource} objects
     *         that are recommended to the user. If none are found, the list
     *         will be empty.
     * @throws DAOException
     *             when there is an error while trying to get all resources from
     *             the data source
     * @throws IllegalArgumentException
     *             When the specified user ID is null, empty, or blank.
     */
    List<UserRecommendedResource> getRecommendedResourcesForTheUser(
            String userId,
            double minimumRatingRequired,
            int minimumNumberOfRatingsRequired) throws DAOException;

    /**
     * Performs a query on the data source that will return a {@link List} of
     * {@link UserRecommendedResource} objects that contains the resources
     * recommended to user such that the resources are in the specified
     * category. The recommended resources are returned in descending order of
     * interest level, average rating and number of ratings.
     *
     * @param userId
     *            a {@link String} that is the unique id of the user. Cannot be
     *            null, empty, or blank.
     * @param categories
     *            A {@link List} of {@link Category} objects. The list cannot be
     *            null or empty. The list cannot contain a category which is
     *            null, has an ID that is not positive, or has a name that is
     *            null/empty/blank.
     * @param minimumRatingRequired
     *            a {@link Double} representing the minimum rating required for
     *            the resource to be ranked above resources that do not have a
     *            rating or enough ratings. Cannot be negative.
     * @param minimumNumberOfRatingsRequired
     *            an {@link Integer} representing the minimum number of ratings
     *            before a resource can be considered to be rated. Cannot be
     *            negative.
     * @return a sorted list (by interest level, average rating and number of
     *         ratings) {@link List} of {@link UserRecommendedResource} objects
     *         that are recommended to the user and are associated to one of the
     *         specified categories. If no such resources are found, an empty
     *         list will be returned.
     * @throws A
     *             {@link DAOException} when there is an error while trying to
     *             get all resources from the data source
     * @throws An
     *             {@link IllegalArgumentException} when the userId is null,
     *             empty, or blank. When the list of categories is null or
     *             empty. When the list of categories contains a category with
     *             an ID that is not positive, or a category whose name is
     *             null/empty/blank.
     *
     */
    List<UserRecommendedResource> getRecommendedResourcesForTheUserInCategories(
            String userId,
            List<Category> categories,
            double minimumRatingRequired,
            int minimumNumberOfRatingsRequired) throws DAOException;
}
