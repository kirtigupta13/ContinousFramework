package com.cerner.devcenter.education.models;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ResourceDAO;


/**
 * This class is used to get education resources for qualified categories.
 * 
 */
public class SkillResourceExtractor implements ResourceExtractor 
{
    @Autowired
    private SkillEvaluator skillEvaluator;
    @Autowired
    private ResourceDAO resourceDao;
    
    /**
     * This method will extract resource URL strings from the database.
     * It then calculates the ratings for each category and sorts them based on those ratings.
     * The sorted list of resources {@link Resource} is then returned. 
     * 
     * This method requires that skillEvaluator has already been set.
     * 
     * @param  userCategoryRatings {@link List} of {@link UserRating}.
     * The sorted list of resources {@link Resource} is then returned. 
     * @param  maxCategories Number of categories for which we need to extract 
     *         resources. If value is 0, then all categories should be considered. 
     * @return Dictionary with key as category ID and value as collection of 
     *         resource URL strings.
     * @throws DAOException when there is an error while trying to get resources
     * @throws IllegalArgumentException when value of maxCategories is negative
     *         or userCategoryRatings is empty
     * @throws NullPointerException when value of userCategoryRatings is null
     *                     
     */
    public Map<Integer, List<Resource>> getResources(List<UserRating> userCategoryRatings, int maxCategories) throws DAOException
    {
        checkNotNull(userCategoryRatings, "Value of userCategoryRating is null");
    	
        checkArgument(!userCategoryRatings.isEmpty(), "userCategoryRatings is empty");

        checkArgument(maxCategories >= 0, "Value of maxCategories is negative");
        
        if(maxCategories == 0 || maxCategories > userCategoryRatings.size())
        {
            //Consider all categories
            maxCategories = userCategoryRatings.size();
        }
        
        Map<Integer, Integer> categoryRatingMap = new HashMap<Integer, Integer>();
        
        // Calculate rating for each category
        for(UserRating userRating : userCategoryRatings)
        {
            int categoryId = userRating.getCategoryId();
            int ratingValue = skillEvaluator.calculateRating(userRating.getSkill(), userRating.getRelevance());
            categoryRatingMap.put(categoryId, ratingValue);
        }
        
        // Sort categories based on rating value
        Map<Integer, Integer> sortCategories = skillEvaluator.sortCategories(categoryRatingMap);
        
        // Get resources from database for qualified categories
        Map<Integer, List<Resource>> outputResourceMap = new LinkedHashMap<Integer, List<Resource>>(maxCategories);
        
        int categoryCounter = 0;
        
        for(Integer categoryId : sortCategories.keySet())
        {
            if(categoryCounter == maxCategories)
            {
                break;
            }
            outputResourceMap.put(categoryId, resourceDao.getResourcesByCategoryId(categoryId));
            categoryCounter++;
        }

        return outputResourceMap;
    }
}
