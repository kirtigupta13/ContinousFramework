package com.cerner.devcenter.education.models;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class CategorySkillEvaluatorTest {
    private static final String SKILL_LEVEL_ERROR_MESSAGE = "Value of Skill level should be in the range of 1 - 5";
    private static final String JOB_IMPORTANCE_ERROR_MESSAGE = "Value of Job Importance should be in the range of 1 - 5";
    private static final String CATEGORY_RATING_MAP_ERROR_MESSAGE = "Value of argument categoryRatingMap is null";
    CategorySkillEvaluator tester = new CategorySkillEvaluator();
    Map<Integer, Integer> categoryRatingMap;
    Map<Integer, Integer> expectedSortedMap;

    /**
     * Calculate rating with same valid values for both arguments.
     */
    @Test
    public void testCalculateRatingWithSameValueOfSkillLevelAndJobImportance() {
        assertEquals(25, tester.calculateRating(5, 5));
    }

    /**
     * Calculate rating with negative value of Skill level.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateRatingWithNegativeValueOfSkillLevel() {
        try {
            tester.calculateRating(-1, 2);
        } catch (final IllegalArgumentException e) {
            assertEquals(SKILL_LEVEL_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Calculate rating with negative value of Job importance..
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateRatingWithNegativeValueOfJobImportance() {
        try {
            tester.calculateRating(2, -2);
        } catch (final IllegalArgumentException e) {
            assertEquals(JOB_IMPORTANCE_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Calculate rating with value of Skill level equal to zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateRatingWithValueOfSkillLevelEqualZero() {
        try {
            tester.calculateRating(0, 2);
        } catch (final IllegalArgumentException e) {
            assertEquals(SKILL_LEVEL_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Calculate rating with value of Job importance equal to zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateRatingWithValueOfJobImportanceEqualZero() {
        try {
            tester.calculateRating(4, 0);
        } catch (final IllegalArgumentException e) {
            assertEquals(JOB_IMPORTANCE_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Calculate rating with value of Skill level out of upper valid range (i.e.
     * 5).
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateRatingWithValueOfSkillLevelOutOfUpperRange() {
        try {
            tester.calculateRating(7, 2);
        } catch (final IllegalArgumentException e) {
            assertEquals(SKILL_LEVEL_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Calculate rating with value of Job Importance out of upper valid range
     * (i.e. 5).
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateRatingWithValueOfJobImportanceOutOfUpperRange() {
        try {
            tester.calculateRating(1, 8);
        } catch (final IllegalArgumentException e) {
            assertEquals(JOB_IMPORTANCE_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Invoke {@link CategorySkillEvaluator#sortCategories(Map)} function with map
     * consisting of many categories
     */
    @Test
    public void testSortCategoriesWhenMapContainsMulipleCategories() {
        // Input data
        categoryRatingMap = new HashMap<Integer, Integer>();
        categoryRatingMap.put(5, 20);
        categoryRatingMap.put(2, 16);
        categoryRatingMap.put(4, 25);

        // Expected output data
        expectedSortedMap = new LinkedHashMap<Integer, Integer>();
        expectedSortedMap.put(4, 25);
        expectedSortedMap.put(5, 20);
        expectedSortedMap.put(2, 16);

        assertEquals(new ArrayList<Integer>(expectedSortedMap.values()),
                new ArrayList<Integer>(tester.sortCategories(categoryRatingMap).values()));
    }

    /**
     * Invoke {@link CategorySkillEvaluator#sortCategories(Map)} function with
     * <code>null</code> argument
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSortCategoriesWhenInputIsNULL() {
        try {
            tester.sortCategories(null);
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_RATING_MAP_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }
}
