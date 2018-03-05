package com.cerner.devcenter.education.models;

import java.util.Map;

/**
 * This interface is responsible for providing resources for relevant categories
 * that are identified based on their rating.
 * 
 * @author Ananta Saple
 *
 */
public interface SkillEvaluator 
{
    /**
     * This method will calculate rating for a category based on Skill level 
     * and Job importance.
     * 
     * @param skillLevel     Skill level of user in a category [Valid range: 1 - 5] 
     * @param jobImportance  Importance of category in user's job [Valid range: 1 - 5]
     * @return The calculated rating value for a category
     * @throws IllegalArgumentException for invalid out of range argument values
     */
     public int calculateRating(int skillLevel, int jobImportance);
     
     /**
      * This method will sort and return category IDs in descending order of rating values 
      * 
      * @param categoryRatingMap Input map consisting of category IDs and their respective ratings 
      * @return Sorted map in descending order based on rating
      * @throws IllegalArgumentException when value of argument is null
      */
      public Map<Integer, Integer> sortCategories(Map<Integer, Integer> categoryRatingMap);
}
