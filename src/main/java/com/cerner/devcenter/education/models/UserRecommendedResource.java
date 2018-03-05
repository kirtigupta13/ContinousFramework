package com.cerner.devcenter.education.models;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Represents the recommended resource to the user along with the category of
 * that resource, difficulty of that resource for that category and the users
 * interest level in that category.
 * 
 * @author Gunjan Kaphle (GK045931)
 * @author Navya Rangeneni (NR046827)
 * @author Santosh Kumar (SK051343)
 */
public class UserRecommendedResource {

    private static final String RESOURCE_ERROR_MESSAGE = "Resource object cannot be null.";
    private static final String CATEGORY_ERROR_MESSAGE = "Category Object cannot be null.";
    private static final String DIFFICULTY_LEVEL_ERROR_MESSAGE = "Difficulty level must be between 1 and 5.";
    private static final String INTEREST_LEVEL_ERROR_MESSAGE = "Interest level must be between 1 and 5.";
    private Resource resource;
    private Category category;
    private int difficultyLevel;
    private int interestLevel;

    /**
     * Constructor to set the attributes of {@link UserRecommendedResource}
     * 
     * @param resource
     *            a {@link Resource} object. Cannot be null
     * @param category
     *            a {@link Category} object that is the category of the
     *            resource. Cannot be null
     * @param difficultyLevel
     *            an {@link Integer} that is the difficulty level of the
     *            category for that resource. Must be between 1 and 5
     * @param interestLevel
     *            an {@link Integer} that is the interest level of the user.
     *            Must be between 1 and 5
     * @throws IllegalArgumentException
     *             when the resource is null, or category is null, or interest
     *             level is not between 1 and 5, or skill level is not between 1
     *             and 5.
     */
    public UserRecommendedResource(Resource resource, Category category, int difficultyLevel, int interestLevel) {
        checkParametersValidity(resource, category, difficultyLevel, interestLevel);
        this.resource = resource;
        this.category = category;
        this.difficultyLevel = difficultyLevel;
        this.interestLevel = interestLevel;
    }

    /**
     * Default constructor used for testing purposes.
     */
    public UserRecommendedResource() {
    }

    /**
     * Getter method to return the {@link Resource} object.
     * 
     * @return a {@link Resource} object
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Setter method to set the {@link Resource} object.
     * 
     * @param resource
     *            a {@link Resource} object. Cannot be null
     * @throws IllegalArgumentException
     *             when the {@link Resource} object is null
     */
    public void setResource(Resource resource) {
        checkArgument(resource != null, RESOURCE_ERROR_MESSAGE);
        this.resource = resource;
    }

    /**
     * Getter method to get the category object.
     * 
     * @return a {@link Category} object
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Setter method to set the category object.
     * 
     * @param category
     *            a {@link Category} object. Cannot be null
     * @throws IllegalArgumentException
     *             when the {@link Category} object is null
     */
    public void setCategory(Category category) {
        checkArgument(category != null, CATEGORY_ERROR_MESSAGE);
        this.category = category;
    }

    /**
     * Getter method to get the difficulty level of the resource for that
     * category.
     * 
     * @return an {@link Integer} that is the difficulty level of the category
     *         for that resource
     */
    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    /**
     * Setter method to set the difficulty level of the resource for that
     * category.
     * 
     * @param difficultyLevel
     *            an {@link Integer} that is the difficulty level of the
     *            category for that resource. Must be between 1 and 5
     * @throws {@link
     *             IllegalArgumentException} when the difficulty level is not
     *             between 1 and 5
     */
    public void setDifficultyLevel(int difficultyLevel) {
        checkArgument(difficultyLevel > 0 && difficultyLevel < 6, DIFFICULTY_LEVEL_ERROR_MESSAGE);
        this.difficultyLevel = difficultyLevel;
    }

    /**
     * Getter method to set the user's interest level for that category.
     * 
     * @return an {@link Integer} that is the interest level of the user
     */
    public int getInterestLevel() {
        return interestLevel;
    }

    /**
     * Setter method to set the user's interest level for that category.
     * 
     * @param interestLevel
     *            an {@link Integer} that is the interest level of the user.
     *            Must be between 1 and 5
     * @throws {@link
     *             IllegalArgumentException} when the interest level is not
     *             between 1 and 5
     */
    public void setInterestLevel(int interestLevel) {
        checkArgument(interestLevel > 0 && interestLevel < 6, INTEREST_LEVEL_ERROR_MESSAGE);
        this.interestLevel = interestLevel;
    }

    /**
     * Checks if the parameters passed in for {@link UserRecommendedResource}
     * object are valid or not.
     * 
     * @param resource
     *            a {@link Resource} object. Must not be null
     * @param category
     *            a {@link Category} object that is the category of the
     *            resource. Must not be null
     * @param difficultyLevel
     *            an {@link Integer} that is the difficulty level of the
     *            category for that resource. Must be between 1 and 5
     * @param interestLevel
     *            an {@link Integer} that is the interest level of the user.
     *            Must be between 1 and 5
     * @throws {@link
     *             IllegalArgumentException} when the resource is null, or
     *             category is null, or difficulty level is not between 1 and 5,
     *             or interest level is not between 1 and 5.
     */
    void checkParametersValidity(Resource resource, Category category, int difficultyLevel, int interestLevel) {
        checkArgument(resource != null, RESOURCE_ERROR_MESSAGE);
        checkArgument(category != null, CATEGORY_ERROR_MESSAGE);
        checkArgument(difficultyLevel > 0 && difficultyLevel < 6, DIFFICULTY_LEVEL_ERROR_MESSAGE);
        checkArgument(interestLevel > 0 && interestLevel < 6, INTEREST_LEVEL_ERROR_MESSAGE);
    }
}
