package com.cerner.devcenter.education.models;

import static com.google.common.base.Preconditions.checkArgument;

import java.net.URL;

import org.apache.commons.lang3.StringUtils;

import com.cerner.devcenter.education.helpers.HttpURLValidator;

/**
 * This model serves the purpose of holding the resource and its relation to
 * category. A resource might have different difficulty levels across categories, this
 * class serves the purpose of acting as a container for moving such data across
 * the application.
 *
 * @author Abhi Purella (AP045635)
 * @author Navya Rangeneni (NR046827)
 * @author Vincent Dasari (VD049645)
 * @author Rishabh Bhojak (RB048032)
 * @author Santosh Kumar (SK051343)
 */
public class ResourceCategoryRelation {

    private static final String RESOURCE_ID_ERROR_MESSAGE = "Resource ID should be greater than 0.";
    private static final String RESOURCE_NAME_ERROR_MESSAGE = "Resource name is null/empty/blank";
    private static final String RESOURCE_LINK_ERROR_MESSAGE = "Error in resource link";
    private static final String RESOURCE_DESCRIPTION_ERROR_MESSAGE = "Resource description is null/empty";
    private static final String CATEGORY_DESCRIPTION_ERROR_MESSAGE = "Category description is null/empty";
    private static final String CATEGORY_ID_ERROR_MESSAGE = "Category ID should be greater than 0";
    private static final String CATEGORY_NAME_ERROR_MESSAGE = "Category name is null/empty";
    private static final String DIFFICULTY_LEVEL_ERROR_MESSAGE = "difficultyLevel should be on a scale of 1-5";
    private static final String RESOURCE_TYPE_ERROR_MESSAGE = "Resource Type object cannot be null";
    private static final String AVERAGE_RATING_ERROR_MESSAGE = "Average Rating can not be less than 0";
    private static final String RESOURCE_OWNER_ERROR_MESSAGE = "Resource owner cannot be null/empty/blank";
    private static final int RESOURCE_OWNER_LENGTH_UPPER_BOUND = 8;
    private static final String INVALID_LENGTH_RESOURCE_OWNER_ERROR_MESSAGE = "Resource owner is greater than 8 characters.";

    private int resourceId;
    private String resourceName;
    private URL resourceLink;
    private String resourceDescription;
    private ResourceType resourceType;
    private int difficultyLevel;
    private int categoryId;
    private String categoryName;
    private String categoryDescription;
    private double averageRating;
    private String resourceOwner;

    /**
     * @param resourceId
     *            integer representing the unique identifier for each resource
     *            (must be greater than 0).
     * @param difficultyLevel
     *            integer which represents the difficulty of the resource under
     *            the category (must be on a scale of 1-5).
     * @param categoryId
     *            integer which represents the categoryId of the resource under the
     *            category (must be greater than 0).
     */
    public ResourceCategoryRelation(final int resourceId, final int difficultyLevel, final int categoryId) {
        this.setResourceId(resourceId);
        this.setDifficultyLevel(difficultyLevel);
        this.setCategoryId(categoryId);
    }

    /**
     * Default constructor of the ResourceCategoryRelation class with package
     * private scope that has no implementation and is used during testing.
     */
    ResourceCategoryRelation() {
    }

    /**
     * Parameterized constructor for the {@link ResourceCategoryRelation} class
     * sets the difficultyLevel to default value i.e. 0.
     *
     * @param resourceId
     *            integer representing the unique identifier for each resource
     *            (must be greater than 0).
     * @param resourceName
     *            {@link String} containing the resourceName of the resource
     *            (cannot be null or empty and has to be a valid name).
     * @param resourceLink
     *            {@link URL} representing the URL link for the resource (cannot
     *            be null, empty or not properly formed).
     * @param resourceType
     *            an {@link ResourceType} that represents the type of the
     *            resource. (must not be null)
     * @param categoryId
     *            integer which represents the categoryId of the resource under the
     *            category (must be greater than 0).
     * @param categoryName
     *            a string representing the categoryName of the category (cannot be
     *            null or empty and has to be a valid categoryName).
     * @param resourceDescription
     *            {@link String} that contains a resourceDescription of the
     *            resource (cannot be null or empty).
     * @param categoryDescription
     *            {@link String} that contains a categoryDescription of the category
     *            (cannot be null or empty).
     */
    public ResourceCategoryRelation(final int resourceId, final String resourceName, final URL resourceLink,
            final ResourceType resourceType, final int categoryId, final String categoryName,
            final String resourceDescription, final String categoryDescription) {
        this.setResourceId(resourceId);
        this.setResourceName(resourceName);
        this.setResourceLink(resourceLink);
        this.setResourceType(resourceType);
        this.setCategoryId(categoryId);
        this.setCategoryName(categoryName);
        this.setResourceDescription(resourceDescription);
        this.setCategoryDescription(categoryDescription);
    }

    /**
     * Parameterized constructor for the {@link ResourceCategoryRelation} class.
     *
     * @param resourceId
     *            integer representing the unique identifier for each resource
     *            (must be greater than 0).
     * @param resourceName
     *            {@link String} containing the name of the resource (cannot be
     *            null or empty and has to be a valid name).
     * @param resourceLink
     *            {@link URL} representing the URL link for the resource (cannot
     *            be null, empty or not properly formed).
     * @param resourceType
     *            an {@link Integer} that represents the type of the resource.
     *            (must be greater than 0)
     * @param difficultyLevel
     *            integer which represents the difficulty of the resource under
     *            the category (must be on a scale of 1-5).
     * @param categoryId
     *            integer which represents the categoryId of the resource under the
     *            category (must be greater than 0).
     * @param categoryName
     *            a {@link String} representing the categoryName of the category
     *            (cannot be null or empty and has to be a valid categoryName).
     * @param resourceDescription
     *            {@link String} that contains a resourceDescription of the
     *            resource (cannot be null or empty).
     * @param categoryDescription
     *            {@link String} that contains a categoryDescription of the category
     *            (cannot be null or empty).
     */
    public ResourceCategoryRelation(final int resourceId, final String resourceName, final URL resourceLink,
            final ResourceType resourceType, final int difficultyLevel, final int categoryId, final String categoryName,
            final String resourceDescription, final String categoryDescription) {
        this.setResourceId(resourceId);
        this.setResourceName(resourceName);
        this.setResourceLink(resourceLink);
        this.setResourceType(resourceType);
        this.setDifficultyLevel(difficultyLevel);
        this.setCategoryId(categoryId);
        this.setCategoryName(categoryName);
        this.setResourceDescription(resourceDescription);
        this.setCategoryDescription(categoryDescription);
    }

    /**
     * Parameterized constructor for the {@link ResourceCategoryRelation} class.
     *
     * @param resourceId
     *            integer representing the unique identifier for each resource
     *            (must be greater than 0).
     * @param resourceName
     *            {@link String} containing the name of the resource (cannot be
     *            null or empty and has to be a valid name).
     * @param resourceLink
     *            {@link URL} representing the URL link for the resource (cannot
     *            be null, empty or not properly formed).
     * @param resourceType
     *            an {@link Integer} that represents the type of the resource.
     *            (must be greater than 0)
     * @param difficultyLevel
     *            integer which represents the difficulty of the resource under
     *            the category (must be on a scale of 1-5).
     * @param categoryId
     *            integer which represents the categoryId of the resource under the
     *            category (must be greater than 0).
     * @param categoryName
     *            a {@link String} representing the categoryName of the category
     *            (cannot be null or empty and has to be a valid categoryName).
     * @param resourceDescription
     *            {@link String} that contains a resourceDescription of the
     *            resource (cannot be null or empty).
     * @param categoryDescription
     *            {@link String} that contains a categoryDescription of the category
     *            (cannot be null or empty).
     * @param resourceOwner
     *            a String representing the resourceOwner ID of the
     *            {@link Resource}. Cannot be <code>null</code>, blank or empty.
     * @param averageRating
     *            double which represents the average rating of the resource
     *            (cannot be less than 0)
     */
    public ResourceCategoryRelation(final int resourceId, final String resourceName, final URL resourceLink,
            final ResourceType resourceType, final int difficultyLevel, final int categoryId, final String categoryName,
            final String resourceDescription, final String categoryDescription, final double averageRating,
            final String resourceOwner) {
        this(resourceId, resourceName, resourceLink, resourceType, difficultyLevel, categoryId, categoryName,
                resourceDescription, categoryDescription);
        this.setAverageRating(averageRating);
        this.setResourceOwner(resourceOwner);
    }

    /**
     * Returns the resourceID of the {@link ResourceCategoryRelation} object.
     *
     * @return the resourceId an integer representing the unique resourceId of
     *         the resource.
     */
    public int getResourceId() {
        return resourceId;
    }

    /**
     * Sets the resourceID of the {@link ResourceCategoryRelation} object after
     * necessary validations.
     *
     * @param resourceId
     *            an integer representing the unique resourceId of the resource
     *            (must be greater than 0).
     *
     * @throws IllegalArgumentException
     *             if resourceId is less than zero
     */
    public void setResourceId(final int resourceId) {
        checkArgument(resourceId > 0, RESOURCE_ID_ERROR_MESSAGE);
        this.resourceId = resourceId;
    }

    /**
     * Returns the resource name
     *
     * @return the resourceName that contains resource name
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Sets the resource name
     *
     * @param resourceName
     *            string that contains resourceName of resource (cannot be null
     *            or empty and has to be a valid name).
     *
     * @throws IllegalArgumentException
     *             when resource name is null/empty.
     */
    public void setResourceName(final String resourceName) {
        checkArgument(StringUtils.isNotBlank(resourceName), RESOURCE_NAME_ERROR_MESSAGE);
        this.resourceName = resourceName;
    }

    /**
     * Retrieves the resourceLink {@link URL} object of the resource
     *
     * @return the resourceLink {@link URL} object of the resource
     */
    public URL getResourceLink() {
        return resourceLink;
    }

    /**
     * Sets the resource link of the resource
     *
     * @param resourceLink
     *            the resourceLink {@link URL} object of the resource (cannot be
     *            null, empty or not properly formed).
     *
     * @throws IllegalArgumentException
     *             when the URL is not valid (i.e. could be null, empty or not
     *             properly formed).
     */
    public void setResourceLink(final URL resourceLink) {
        checkArgument(HttpURLValidator.verifyURL(resourceLink), RESOURCE_LINK_ERROR_MESSAGE);
        this.resourceLink = resourceLink;
    }

    /**
     * Returns the resourceDescription of the resource.
     *
     * @return resourceDescription, a {@link String}, of the resource.
     */
    public String getResourceDescription() {
        return resourceDescription;
    }

    /**
     * Sets the resourceDescription of the resource.
     *
     * @param resourceDescription
     *            {@link String} that contains a resourceDescription of the
     *            resource (not null, not empty).
     *
     * @throws IllegalArgumentException
     *             when resource description is null/empty.
     */
    public void setResourceDescription(final String resourceDescription) {
        checkArgument(StringUtils.isNotBlank(resourceDescription), RESOURCE_DESCRIPTION_ERROR_MESSAGE);
        this.resourceDescription = resourceDescription;
    }

    /**
     * Returns the categoryDescription of the category.
     *
     * @return categoryDescription, a {@link String}, of the category.
     */
    public String getCategoryDescription() {
        return categoryDescription;
    }

    /**
     * Sets the categoryDescription of the resource.
     *
     * @param categoryDescription
     *            {@link String} that contains a categoryDescription of the category
     *            (not null, not empty).
     *
     * @throws IllegalArgumentException
     *             when category description is null/empty.
     */
    public void setCategoryDescription(final String categoryDescription) {
        checkArgument(StringUtils.isNotBlank(categoryDescription), CATEGORY_DESCRIPTION_ERROR_MESSAGE);
        this.categoryDescription = categoryDescription;
    }

    /**
     * Returns the difficulty level of the resource under category
     *
     * @return the difficultyLevel and integer that represents the difficulty
     *         level of the resource under category
     */
    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    /**
     * Sets the difficultyLevel of the resource under category
     *
     * @param difficultyLevel
     *            the difficultyLevel to set (cannot be null or empty).
     *
     * @throws IllegalArgumentException
     *             if difficultyLevel is less than zero
     */
    public void setDifficultyLevel(final int difficultyLevel) {
        checkArgument((difficultyLevel > 0 && difficultyLevel <= 5), DIFFICULTY_LEVEL_ERROR_MESSAGE);
        this.difficultyLevel = difficultyLevel;
    }

    /**
     * Returns the categoryId of the course.
     *
     * @return categoryId
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * Sets the categoryId of the course object to a unique integer.
     *
     * @param categoryId
     *            unique categoryId (must be greater than 0).
     *
     * @throws IllegalArgumentException
     *             if categoryId is less than zero
     */
    public void setCategoryId(final int categoryId) {
        checkArgument(categoryId > 0, CATEGORY_ID_ERROR_MESSAGE);
        this.categoryId = categoryId;
    }

    /**
     * Returns the categoryName of the category.
     *
     * @return categoryName which is a String that represents the name of the category
     *         object.
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Sets the categoryName of the category after necessary validations.
     *
     * @param categoryName
     *            a String representing the categoryName of the category (cannot be
     *            null or empty and has to be a valid categoryName).
     *
     * @throws IllegalArgumentException
     *             when category name is null/empty.
     */
    public void setCategoryName(final String categoryName) {
        checkArgument(StringUtils.isNotBlank(categoryName), CATEGORY_NAME_ERROR_MESSAGE);
        this.categoryName = categoryName;
    }

    /**
     * Gets the resource type of the resource.
     *
     * @return an {@link ResourceType} that is the type of the resource
     */
    public ResourceType getResourceType() {
        return resourceType;
    }

    /**
     * Sets the resource type of the resource after validation.
     *
     * @param resourceType
     *            an {@link ResourceType} object. (must not be null)
     * @throws IllegalArgumentException
     *             when resource type is null
     */
    public void setResourceType(final ResourceType resourceType) {
        checkArgument(resourceType != null, RESOURCE_TYPE_ERROR_MESSAGE);
        this.resourceType = resourceType;
    }

    /**
     * Gets the average rating of the resource.
     *
     * @return averageRating, a double representing the average of all the
     *         ratings of a {@link ResourceCategoryRelation}
     */
    public double getAverageRating() {
        return averageRating;
    }

    /**
     * Sets the average rating of {@link Resource} to an integer value.
     *
     * @param averageRating
     *            integer which represents the average of all ratings for a
     *            particular resource. Cannot be negative.
     * @throws IllegalArgumentException
     *             if averageRating is less than zero
     */
    public void setAverageRating(final double averageRating) {
        checkArgument(averageRating >= 0, AVERAGE_RATING_ERROR_MESSAGE);
        this.averageRating = Math.round(averageRating * 100) / 100;
    }

    /**
     * Returns the resourceOwner string ID of the {@link ResourceCategoryRelation}.
     *
     * @return resourceOwner of the {@link ResourceCategoryRelation}.
     */
    public String getResourceOwner() {
        return resourceOwner;
    }

    /**
     * Sets the resourceOwner of the {@link ResourceCategoryRelation}.
     *
     * @param resourceOwner
     *            that contains resourceOwner ID of the
     *            {@link ResourceCategoryRelation}. Cannot be <code>null</code>,
     *            blank or empty.
     * @throws IllegalArgumentException
     *             when resourceOwner is <code>null</code>, blank or empty.
     */
    public void setResourceOwner(String resourceOwner) {
        checkArgument(StringUtils.isNotBlank(resourceOwner), RESOURCE_OWNER_ERROR_MESSAGE);
        checkArgument(StringUtils.length(resourceOwner) <= RESOURCE_OWNER_LENGTH_UPPER_BOUND,
                INVALID_LENGTH_RESOURCE_OWNER_ERROR_MESSAGE);
        this.resourceOwner = resourceOwner;
    }
}