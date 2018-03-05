package com.cerner.devcenter.education.managers;

import java.util.TreeMap;

/**
 * This interface is a manager for the options that the user will be able to select in the form for:
 * 1. Specifying his/her skill level in a particular category and
 * 2. Specifying the relevance level for that category to his/her job.
 * 
 * @author SB033185
 */
public interface OptionManager {

    /**
     * This function returns the set of options available for selecting 'skill level'.
     * 
     * @return A mapping of option-captions to option-values for 'skill level' field.
     */
    public TreeMap<Integer, String> getSkillOptions();

    /**
     * This function returns the set of options available for selecting 'relevance level'.
     * 
     * @return A mapping of option-captions to option-values for 'relevance level' field.
     */
    public TreeMap<Integer, String> getRelevanceOptions();

}