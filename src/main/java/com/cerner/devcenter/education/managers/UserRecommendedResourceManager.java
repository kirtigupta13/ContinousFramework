package com.cerner.devcenter.education.managers;

import static com.google.common.base.Preconditions.checkArgument;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.dao.UserRecommendedResourceDAO;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.UserInterestedCategory;
import com.cerner.devcenter.education.models.UserRecommendedResource;
import com.cerner.devcenter.education.utils.Constants;

/**
 * <code>Manager</code> class that will act as a <code>Service</code> between
 * the controller classes and the {@link UserRecommendedResourceDAO} class.
 *
 * @author Gunjan Kaphle (GK045931)
 * @author Amos Bailey (AB032627)
 * @author Mayur Rajendran (MT049536)
 * @author Santosh Kumar (SK051343)
 */
@Service("userRecommendedResourceManager")
public class UserRecommendedResourceManager {

    private static final String INVALID_MINIMUM_RATING_REQUIRED_ERROR_MESSAGE = "The minimum rating required should be non-negative";
    private static final String INVALID_NUMBER_OF_RATINGS_REQUIRED_ERROR_MESSAGE = "The minimum number of ratings required should be non-negative";
    private static final String INVALID_USER_ID_ERROR_MESSAGE = "User Id cannot be null, empty or whitespace.";

    private static final int MAX_RESOURCES_TO_RECOMMEND = 10;

    @Autowired
    UserRecommendedResourceDAO userRecommendedResourceDAO;
    @Autowired
    UserInterestedCategoryManager userInterestedCategoryManager;

    /**
     * Retrieves a list from the database and creates a top ten list of
     * recommended resources to the user by using the user id of that level and
     * looking into users skill level and interest level for a category. Sorts the
     * list so that the resources from categories that the user is most interested
     * in are at the beginning of the returned list.
     *
     * @param userId
     *            a {@link String} that is the user id of the user for whom we
     *            want to recommend some categories. Cannot be null, empty, or
     *            blank.
     * @param minimumRatingRequired
     *            a {@link Double} representing the minimum rating required for
     *            the resource to be ranked above resources that do not have a
     *            rating or enough ratings. Cannot be negative.
     * @param minimumNumberOfRatingsRequired
     *            an {@link Integer} representing the minimum number of ratings
     *            before a resource can be considered to be rated. Cannot be
     *            negative.
     * @return a {@link List} of {@link UserRecommendedResource} object
     * @throws IllegalArgumentException
     *             When the user ID is null, empty, or blank.
     * @throws SQLException
     *             when there is an error in the query
     */
    public List<UserRecommendedResource> getRecommendedResourcesByUserId(
            final String userId,
            final double minimumRatingRequired,
            final int minimumNumberOfRatingsRequired) throws SQLException {
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID_ERROR_MESSAGE);
        checkArgument(minimumRatingRequired >= 0, INVALID_MINIMUM_RATING_REQUIRED_ERROR_MESSAGE);
        checkArgument(minimumNumberOfRatingsRequired >= 0, INVALID_NUMBER_OF_RATINGS_REQUIRED_ERROR_MESSAGE);
        try {
            final List<UserRecommendedResource> recommendedResourcesFromDB = userRecommendedResourceDAO
                    .getRecommendedResourcesForTheUser(userId, minimumRatingRequired, minimumNumberOfRatingsRequired);
            List<UserRecommendedResource> finalRecommendedResources = new ArrayList<>();
            if (!recommendedResourcesFromDB.isEmpty()) {
                final Map<Integer, List<UserRecommendedResource>> recommendedResourcesByCategory = getRecommendedResourcesByCategory(
                        recommendedResourcesFromDB);
                finalRecommendedResources = findTopTenResources(
                        recommendedResourcesFromDB,
                        recommendedResourcesByCategory,
                        userId);
            }
            return finalRecommendedResources;
        } catch (final DAOException e) {
            throw new ManagerException(Constants.ERROR_RETRIEVING_RECOMMENDED_RESOURCES, e);
        }
    }

    /**
     * Retrieves recommended resources in the specified categories for the user
     * from the database, then creates a top 10 list to display in the database.
     * Sorts the list so that the resources from categories that the user is most
     * interested in are at the beginning of the returned list.
     *
     * @param userId
     *            A {@link String} representing the unique ID of the user.
     *            Cannot be null or blank.
     * @param categories
     *            A {@link List} of {@link Category} objects, representing the
     *            categories to filter by. Cannot be null or empty. Each
     *            category must not be null, must have a positive ID, and its
     *            name must not be null/empty/blank.
     * @param minimumRatingRequired
     *            a {@link Double} representing the minimum rating required for
     *            the resource to be ranked above resources that do not have a
     *            rating or enough ratings. Cannot be negative.
     * @param minimumNumberOfRatingsRequired
     *            an {@link Integer} representing the minimum number of ratings
     *            before a resource can be considered to be rated. Cannot be
     *            negative.
     * @return A {@link List} of {@link UserRecommendedResource} objects.
     * @throws ManagerException
     *             when a DAOException is thrown while performing a query.
     * @throws IllegalArgumentException
     *             When the user ID is null, blank, or empty. When the list of
     *             categories is empty or null. When the list of categories
     *             contains a category that is null, has an ID that is not
     *             positive, or has a name that is null/empty/blank.
     */
    public List<UserRecommendedResource> getRecommendedResourcesByUserIdAndCategories(
            final String userId,
            final List<Category> categories,
            final double minimumRatingRequired,
            final int minimumNumberOfRatingsRequired) throws ManagerException {
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID_ERROR_MESSAGE);
        checkArgument(categories != null, Constants.CATEGORY_LIST_NULL);
        checkArgument(!categories.isEmpty(), Constants.CATEGORY_LIST_EMPTY);
        for (final Category category : categories) {
            checkArgument(category != null, Constants.CATEGORY_LIST_HAS_NULL_ITEM);
            checkArgument(category.getId() > 0, Constants.CATEGORY_ID_MUST_BE_POSITIVE);
            checkArgument(category.getName() != null, Constants.CATEGORY_NAME_NULL);
            checkArgument(StringUtils.isNotBlank(category.getName()), Constants.CATEGORY_NAME_EMPTY_OR_BLANK);
        }
        checkArgument(minimumRatingRequired >= 0, INVALID_MINIMUM_RATING_REQUIRED_ERROR_MESSAGE);
        checkArgument(minimumNumberOfRatingsRequired >= 0, INVALID_NUMBER_OF_RATINGS_REQUIRED_ERROR_MESSAGE);
        try {
            final List<UserRecommendedResource> recommendedResourcesFromDB = userRecommendedResourceDAO
                    .getRecommendedResourcesForTheUserInCategories(
                            userId,
                            categories,
                            minimumRatingRequired,
                            minimumNumberOfRatingsRequired);
            if (recommendedResourcesFromDB.size() > 10) {
                return findTopTenResources(
                        recommendedResourcesFromDB,
                        getRecommendedResourcesByCategory(recommendedResourcesFromDB),
                        userId);
            }
            return recommendedResourcesFromDB;
        } catch (final DAOException daoException) {
            throw new ManagerException(Constants.ERROR_RETRIEVING_RECOMMENDED_RESOURCES, daoException);
        }
    }

    /**
     * Finds the top ten resources to be displayed in the user dashboard.
     *
     * @param allMatchingResources
     *            a {@link List} of all {@link UserRecommendedResource} that was
     *            retrieved from the database
     * @param resourcesByCategoryId
     *            a {@link Map} of key {@link Integer} and value {@link List} of
     *            {@link UserRecommendedResource} that maps the category id with
     *            the resources for that category id
     * @param userId
     *            a {@link String} that is the unique id of the user
     * @return a {@link List} of {@link UserRecommendedResource} that is the top
     *         ten resources to display in the dashboard
     */
    public List<UserRecommendedResource> findTopTenResources(
            final List<UserRecommendedResource> allMatchingResources,
            final Map<Integer, List<UserRecommendedResource>> resourcesByCategoryId,
            final String userId) {
        final List<UserInterestedCategory> userInterestedCategories = userInterestedCategoryManager
                .getUserInterestedCategoriesById(userId);
        final Map<Integer, Integer> interestLevelByUserInterestedCategoryId = createMapForUserInterestedCategoryInterestLevel(
                userInterestedCategories);
        final double totalCountOfInterestLevel = findTotalCountOfInterestLevel(
                resourcesByCategoryId,
                interestLevelByUserInterestedCategoryId);
        final List<UserRecommendedResource> finalResourcesToRecommend = findTheBestTenResourcesAccordingToWeightOfInterestLevel(
                resourcesByCategoryId,
                interestLevelByUserInterestedCategoryId,
                totalCountOfInterestLevel);
        return finalResourcesToRecommend;
    }

    /**
     * Finds and creates a list of the best ten resources according to the list
     * retrieved from the database including the weight for interest level
     * according to user's interest.
     *
     * @param resourcesByCategoryId
     *            a {@link Map} of key {@link Integer} and value {@link List} of
     *            {@link UserRecommendedResource} that maps the category id with
     *            the resources for that category id
     * @param interestLevelByUserInterestedCategoryId
     *            a {@link Map} of key {@link Integer} that represents category id
     *            and value {@link Integer} that represents users interest level
     *            in that category id
     * @param totalCountOfInterestLevel
     *            an {@link Integer} that is the total count of interest level,
     *            which is used to calculate the weightage for each category.
     * @return a {@link List} of {@link UserRecommendedResource} that is the top
     *         ten resources
     */
    public List<UserRecommendedResource> findTheBestTenResourcesAccordingToWeightOfInterestLevel(
            final Map<Integer, List<UserRecommendedResource>> resourcesByCategoryId,
            final Map<Integer, Integer> interestLevelByUserInterestedCategoryId,
            final double totalCountOfInterestLevel) {
        final List<UserRecommendedResource> finalResourcesToRecommend = new ArrayList<UserRecommendedResource>();
        for (final int categoryId : resourcesByCategoryId.keySet()) {
            final double interestLevelOnCurrentCategory = interestLevelByUserInterestedCategoryId.get(categoryId);
            final int numOfResourcesToDisplayForCurrentCategory = findNumberOfResourcesToDisplay(
                    totalCountOfInterestLevel,
                    MAX_RESOURCES_TO_RECOMMEND,
                    interestLevelOnCurrentCategory);
            final List<UserRecommendedResource> currentListOfResourcesForCategory = resourcesByCategoryId
                    .get(categoryId);
            final int sizeToLoopThrough = Math
                    .min(currentListOfResourcesForCategory.size(), numOfResourcesToDisplayForCurrentCategory);
            for (int i = 0; i < sizeToLoopThrough; i++) {
                if (finalResourcesToRecommend.size() < MAX_RESOURCES_TO_RECOMMEND) {
                    finalResourcesToRecommend.add(currentListOfResourcesForCategory.get(i));
                } else {
                    return finalResourcesToRecommend;
                }
            }
        }
        return finalResourcesToRecommend;
    }

    /**
     * Finds the total number of resources to display for the category id
     * according to the interest level, max resources to return and the total
     * count of interest level. The double value from division will always round
     * up to an integer because when we have 10 different categories with equal
     * interest level for all categories, we will still be displaying a resource
     * for each category.
     *
     * @param totalCountOfInterestLevel
     *            a {@link Double} that is the total count of interest level of
     *            all user interested categories
     * @param maxResourcesToReturn
     *            a {@link Double} that represents the number of resources to
     *            show in user dashboard
     * @param interestLevelOnCurrentCategory
     *            a {@link Double} that represents user's interest in current
     *            category
     * @return an {@link Integer} that is the number of resources to display for
     *         current category.
     */
    public int findNumberOfResourcesToDisplay(
            final double totalCountOfInterestLevel,
            final double maxResourcesToReturn,
            final double interestLevelOnCurrentCategory) {
        final int numOfResourcesToDisplayForCurrentCategory = (int) Math
                .ceil((interestLevelOnCurrentCategory / totalCountOfInterestLevel) * maxResourcesToReturn);
        return numOfResourcesToDisplayForCurrentCategory;
    }

    /**
     * Method that find the total count of interest level according the the list
     * of cateogry id's that are passed in.
     *
     * @param resourcesByCategoryId
     *            a {@link Map} of key {@link Integer} and value {@link List} of
     *            {@link UserRecommendedResource} that maps the category id with
     *            the resources for that category id
     * @param interestLevelByUserInterestedCategoryId
     *            a {@link Map} of key {@link Integer} that represents category id
     *            and value {@link Integer} that represents users interest level
     *            in that category id
     * @return a {@link double} that is the total count of the interest level of
     *         the categories that has resources
     */
    public double findTotalCountOfInterestLevel(
            final Map<Integer, List<UserRecommendedResource>> resourcesByCategoryId,
            final Map<Integer, Integer> interestLevelByUserInterestedCategoryId) {
        double totalCountOfInterestLevel = 0;

        for (final int eachCategoryNameThatHasResources : resourcesByCategoryId.keySet()) {
            totalCountOfInterestLevel += interestLevelByUserInterestedCategoryId.get(eachCategoryNameThatHasResources);
        }
        return totalCountOfInterestLevel;
    }

    /**
     * Creates a map from list of recommended resources that was returned from
     * the database. It stores category id as the key and the list of recommended
     * resources as the value.
     *
     * @param givenRecommendedResources
     *            a {@link List} of {@link UserRecommendedResource} that is
     *            returned from the database
     * @return a {@link Map} as {@link Integer} being the key and {@link List}
     *         of {@link UserRecommendedResource} being the value
     */
    public Map<Integer, List<UserRecommendedResource>> getRecommendedResourcesByCategory(
            final List<UserRecommendedResource> givenRecommendedResources) {
        List<UserRecommendedResource> resourcesForCurrentCategory = new ArrayList<>();
        final Map<Integer, List<UserRecommendedResource>> resourcesByCategoryId = new LinkedHashMap<>();
        for (final UserRecommendedResource resource : givenRecommendedResources) {
            final int categoryIdOfCurrentResource = resource.getCategory().getId();
            if (resourcesByCategoryId.containsKey(categoryIdOfCurrentResource)) {
                final List<UserRecommendedResource> recommendedResourcesForCategoryId = resourcesByCategoryId
                        .get(categoryIdOfCurrentResource);
                recommendedResourcesForCategoryId.add(resource);
            } else {
                resourcesForCurrentCategory = new ArrayList<UserRecommendedResource>();
                resourcesForCurrentCategory.add(resource);
                resourcesByCategoryId.put(categoryIdOfCurrentResource, resourcesForCurrentCategory);
            }
        }
        return resourcesByCategoryId;
    }

    /**
     * Creates a map from list of user interested categories that stores the category
     * id as the key and the interest level as the value of that key.
     *
     * @param userInterestedCategories
     *            a {@link List} of {@link UserInterestedCategory} object
     * @return a {@link Map} of key {@link Integer} as the key which is the
     *         categories id and {@link Integer} as the value which is the user's
     *         interest in that category
     */
    public Map<Integer, Integer> createMapForUserInterestedCategoryInterestLevel(
            final List<UserInterestedCategory> userInterestedCategories) {
        final Map<Integer, Integer> interestLevelByUserInterestedCategoryId = new HashMap<>();
        for (final UserInterestedCategory userInterestedCategory : userInterestedCategories) {
            interestLevelByUserInterestedCategoryId
                    .put(userInterestedCategory.getCategory().getId(), userInterestedCategory.getInterestLevel());
        }
        return interestLevelByUserInterestedCategoryId;
    }
}