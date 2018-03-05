package com.cerner.devcenter.education.models;

import java.util.Map;
import java.util.TreeMap;

public class CategorySkillEvaluator implements SkillEvaluator
{
     public int calculateRating(int skillLevel, int jobImportance)
     {
        // Throw exception if value of Skill level is out of valid range
        if(skillLevel <= 0 || skillLevel > 5)
        {
           throw new IllegalArgumentException("Value of Skill level should be in the range of 1 - 5");
        }
		
        // Throw exception if value of Job importance is out of valid range
        if(jobImportance <= 0 || jobImportance > 5)
        {
            throw new IllegalArgumentException("Value of Job Importance should be in the range of 1 - 5");	
        }
		
        return skillLevel * jobImportance;
     }

     public Map<Integer, Integer> sortCategories(Map<Integer, Integer> categoryRatingMap)
     {
        // Throw exception if the input is null 
        if(categoryRatingMap == null)
        {
            throw new IllegalArgumentException("Value of argument categoryRatingMap is null");
        }
    	
        SkillRatingComparator comparator = new SkillRatingComparator(categoryRatingMap);

        Map<Integer, Integer> sortedMap = new TreeMap<Integer, Integer>(comparator);
        sortedMap.putAll(categoryRatingMap);
 		
        return sortedMap;
     }
}