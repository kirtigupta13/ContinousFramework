package com.cerner.devcenter.education.models;

/**
 * A rating made by the user. Each rating describes the skill level and relevance for a particular category made by the user.
 * 
 * @author SB033185.
 */
public class UserRating{

    private int categoryId;
    private int skill;
    private int relevance;

    public UserRating() {}

    /**
     * Constructor.
     * 
     * @param categoryId The category for the user rating.
     * @param skill The skill level.
     * @param relevance The relevance level.
     */
    public UserRating(int categoryId, int skill, int relevance) {
        this.categoryId = categoryId;
        this.skill = skill;
        this.relevance = relevance;
    }
    
    /**
     * This function returns the categoryId for a particular rating.
     * 
     * @return The categoryId for a rating object.
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * This function sets the categoryId for a particular rating.
     * 
     * @param categoryId The categoryId for a rating.
     */
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * This function returns the skill specified by the user.
     * 
     * @return The skill level specified.
     */
    public int getSkill() {
        return skill;
    }

    /**
     * This function sets the skill level.
     * 
     * @param skill The skill level specified.
     */
    public void setSkill(int skill) {
        this.skill = skill;
    }

    /**
     * This function returns the relevance level specified.
     * 
     * @return The relevance level specified.
     */
    public int getRelevance() {
        return relevance;
    }

    /**
     * This function sets the relevance level given by the user.
     * 
     * @param relevance The relevance level specified.
     */
    public void setRelevance(int relevance) {
        this.relevance = relevance;
    }

}
