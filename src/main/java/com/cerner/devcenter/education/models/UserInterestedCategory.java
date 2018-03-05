package com.cerner.devcenter.education.models;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Represents the user interested category that stores the user's interest in
 * various topics along with their skill level and interest level for that
 * particular category.
 * 
 * @author Gunjan Kaphle (GK045931)
 * @author Jacob Zimmermann (JZ022690)
 * @author Navya Rangeneni (NR046827)
 * @author Santosh Kumar (SK051343)
 */
@Component
public class UserInterestedCategory {
    private String userID;
    private Category category;
    private int skillLevel;
    private int interestLevel;

    private static final String USER_ID_ERROR_MESSAGE = "User ID cannot be null/blank/empty";
    private static final String CATEGORY_ERROR_MESSAGE = "Category cannot be null.";
    private static final String SKILL_LEVEL_ERROR_MESSAGE = "Skill level must be between 1 and 5.";
    private static final String INTEREST_LEVEL_ERROR_MESSAGE = "Interest level must be between 1 and 5.";

    /**
     * Creates an empty {@link UserInterestedCategory}. Should only be used for
     * testing and Spring.
     */
    public UserInterestedCategory() {

    }

    /**
     * Create a new {@link UserInterestedCategory} with given values.
     * 
     * @param userId
     *            a {@link String} that is the user id of the user. Cannot be
     *            null, blank, or empty.
     * @param category
     *            a {@link Category} object that contains category id, name, and
     *            description. Cannot be null.
     * @param skillLevel
     *            a {@link Integer} that is the skill level of the user in that
     *            particular category. Must be between 1 and 5 inclusive.
     * @param interestLevel
     *            a {@link Integer} that is the interest level of the user for
     *            that particular category. Must be between 1 and 5 inclusive.
     * @throws IllegalArgumentException
     *             when any of the following occur:
     *             <li>userId is null, blank, or empty</li>
     *             <li>category is null</li>
     *             <li>skillLevel is not between 1 and 5</li>
     *             <li>interestLevel is not between 1 and 5</li>
     */
    public UserInterestedCategory(String userId, Category category, int skillLevel, int interestLevel) {
        checkParametersValidity(userId, category, skillLevel, interestLevel);
        this.userID = userId;
        this.category = category;
        this.skillLevel = skillLevel;
        this.interestLevel = interestLevel;
    }

    /**
     * Getter method that returns the id of the user.
     * 
     * @return a {@link String} which is the unique id of the user
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Setter method that sets the id of the user.
     * 
     * @param userID
     *            a {@link String} that is the unique id of the user. Must not
     *            be null, blank, or empty
     * @throws IllegalArgumentException
     *             when userId is null, blank, or empty
     */
    public void setUserID(String userID) {
        checkArgument(StringUtils.isNotBlank(userID), USER_ID_ERROR_MESSAGE);
        this.userID = userID;
    }

    /**
     * Getter method that returns a {@link Category} that the user is interested
     * in.
     * 
     * @return a {@link Category} object that contains the category id, name,
     *         and description
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Setter method that sets the {@link Category}.
     * 
     * @param category
     *            a {@link Category} object that contains the category id, name,
     *            and description. Must not be null
     * @throws IllegalArgumentException
     *             when category is null
     */
    public void setCategory(Category category) {
        checkArgument(category != null, CATEGORY_ERROR_MESSAGE);
        this.category = category;
    }

    /**
     * Getter method that gets the skill level of that particular category for
     * the user.
     * 
     * @return an {@link Integer} that represents the users skill level on that
     *         particular category
     */
    public int getSkillLevel() {
        return skillLevel;
    }

    /**
     * Setter method that sets the skill level of that particular category on a
     * scale of 1 to 5.
     * 
     * @param skillLevel
     *            an {@link Integer} that represents the users skill level on a
     *            particular category. Must be between 1 and 5 inclusive.
     * @throws IllegalArgumentException
     *             when skillLevel is not between 1 and 5
     */
    public void setSkillLevel(int skillLevel) {
        checkArgument(skillLevel > 0 && skillLevel < 6, SKILL_LEVEL_ERROR_MESSAGE);
        this.skillLevel = skillLevel;
    }

    /**
     * Getter method that returns the users interest level of that particular
     * category on a scale of 1 to 5.
     * 
     * @return an {@link Integer} that represents the users interest level on
     *         that particular category
     */
    public int getInterestLevel() {
        return interestLevel;
    }

    /**
     * Setter method that sets the user's interest level of that particular
     * category on a scale of 1 to 5.
     * 
     * @param interestLevel
     *            an {@link Integer} that represents the users skill level on a
     *            particular category. Must be between 1 and 5 inclusive.
     * @throws IllegalArgumentException
     *             when interestLevel is not between 1 and 5
     */
    public void setInterestLevel(int interestLevel) {
        checkArgument(interestLevel > 0 && interestLevel < 6, INTEREST_LEVEL_ERROR_MESSAGE);
        this.interestLevel = interestLevel;
    }

    /**
     * Checks if the parameters passed in for {@link UserInterestedCategory} object
     * are valid or not.
     * 
     * @param userId
     *            a {@link String} that is the unique id of the user. Must not
     *            be null or empty
     * @param category
     *            a {@link Category} object that contains category id, name, and
     *            description. Must not be null
     * @param skillLevel
     *            an {@link Integer} that is the skill level of the user for
     *            that particular category. Must be between 1 and 5
     * @param interestLevel
     *            an {@link Integer} that is the interest level of the user for
     *            that particular category. Must be between 1 and 5
     * @throws IllegalArgumentException
     *             when any of the following occur:
     *             <li>userId is null, blank, or empty</li>
     *             <li>category is null</li>
     *             <li>skillLevel is not between 1 and 5</li>
     *             <li>interestLevel is not between 1 and 5</li>
     */
    void checkParametersValidity(String userId, Category category, int skillLevel, int interestLevel) {
        checkArgument(StringUtils.isNotBlank(userId), USER_ID_ERROR_MESSAGE);
        checkArgument(category != null, CATEGORY_ERROR_MESSAGE);
        checkArgument(skillLevel > 0 && skillLevel < 6, SKILL_LEVEL_ERROR_MESSAGE);
        checkArgument(interestLevel > 0 && interestLevel < 6, INTEREST_LEVEL_ERROR_MESSAGE);
    }
}
