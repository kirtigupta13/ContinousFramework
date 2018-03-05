package com.cerner.devcenter.education.models;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.annotations.VisibleForTesting;

/**
 * This class represents Skill Options that contains a unique Skill Value and
 * Description. Associates use these values to provide skill levels for each
 * Category provided on the SurveyForm.
 *
 * @author Sreelakshmi Chintha (SC043016)
 */
public class SkillOptions {
    private int skillValue;
    private String description;

    /**
     * Default constructor of the SkillOptions class that has no implementation
     * and is used during testing.
     */
    @VisibleForTesting
    public SkillOptions() {
    }

    /**
     * parameterized constructor for SkillOptions Class
     *
     * @param skillValue
     *            an integer value for the skill object
     * @param description
     *            a string representing the description of the skill object
     */
    public SkillOptions(final int skillValue, final String description) {
        checkArgument(skillValue >= 0, "Skill value cannot be a negative number");
        checkArgument((description != null && description.length() > 0), "Skill description is null/empty");
        this.skillValue = skillValue;
        this.description = description;
    }

    /**
     * This function sets the Skill value
     *
     * @param skillValue
     *            an integer representing the unique value of the skill (cannot
     *            be lesser than 0).
     * @throws IllegalArgumentException
     *             if skill value is less than zero
     */
    public void setValue(final int skillValue) {
        checkArgument(skillValue >= 0, "Skill value cannot be a negative number");
        this.skillValue = skillValue;
    }

    /**
     * This function returns the skill value specified by the user
     *
     * @return an integer that represents the skill value specified.
     */
    public int getValue() {
        return skillValue;
    }

    /**
     * This function sets the description of the skill object (after necessary
     * validations).
     *
     * @param description
     *            a String representing the description of the skill object
     *            (cannot be null or empty and has to be a valid description)
     * @throws NullPointerException
     *             when description is null.
     * @throws IllegalArgumentException
     *             when description is invalid.
     */
    public void setDescription(final String description) {
        checkArgument((description != null && description.length() > 0), "Skill description is null/empty");
        this.description = description;
    }

    /**
     * This function returns the description of the skill object.
     *
     * @return a string that represents the description of the skill object.
     */
    public String getDescription() {
        return description;
    }
}
