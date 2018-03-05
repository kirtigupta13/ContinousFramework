package com.cerner.devcenter.education.models;

import java.util.*;

/**
 * This class is responsible for comparing categories based on their
 * rating values.
 * 
 * @author Ananta Saple
 *
 */
public class SkillRatingComparator implements Comparator<Object>
{
    Map<Integer, Integer> categoryRatingMap;
    
    /**
     * Constructor for initializing the map.
     * 
     * @param inputMap The input map consisting of categories and their ratings
     */
    public SkillRatingComparator(Map<Integer, Integer> inputMap) 
    {
        this.categoryRatingMap = inputMap;
    }
    
    /**
     * This method is used to compare rating values.
     * 
     * @param category1 The first category to be compared
     * @param category2 The second category to be compared
     * @return result of comparison
     */
    public int compare(Object category1, Object category2) 
    {
        if (categoryRatingMap.get(category1) == categoryRatingMap.get(category2))
        {	
            // To handle categories with same rating value
        	return 1;
        }    
        else
        {	
            return ((Integer) categoryRatingMap.get(category2)).compareTo((Integer)
            		                              categoryRatingMap.get(category1));
        }
   }
}
