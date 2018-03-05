package com.cerner.devcenter.education.models;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test cases that test the functions of {@link Category}
 *
 * @author JS033441
 * @author Jacob Zimmermann (JZ022690)
 * @author Rishabh Bhojak (RB048032)
 */
public class CategoryTest {

    private static final String VALID_CATEGORY_NAME = "Scalability";
    private static final String VALID_CATEGORY_DESCRIPTION = "Knowledge of Cerner's development lifecycle and documentation approach and relevance to the development cycle";
    private static final String EMPTY_STRING = "";
    private static final String BLANK_STRING = "  ";
    private static final String MAP_EMPTY_ERROR_MESSAGE = "map cannot be empty";
    private static final String INVALID_DIFFICULTY_LEVEL = "difficultyLevel must be on a scale of 1-5";
    private static final String RESOURCE_COUNT_ERROR_MESSAGE = "resource count cannot be negative";
    private static final String INVALID_ID_MSG = "Category ID should be greater than zero";
    private static final String INVALID_NAME_MSG = "Category name is null/empty/blank";
    private static final String INVALID_DESCRIPTION_MSG = "Category description is null/empty/blank";
    private static final String KEY_NULL_ERROR_MESSAGE = "difficultyLevel cannot be null";
    private static final String VALUE_NULL_ERROR_MESSAGE = "resource count cannot be null";
    private static final String INVALID_RESOURCE_COUNT = "Resource Count cannot be negative";
    private static final String MAP_NULL_ERROR_MESSAGE = "map cannot be null";
    private static final String TEST = "test";

    private static final int VALID_RESOURCE_COUNT = 22;
    private static final int NEGATIVE_RESOURCES_COUNT = -1;
    private static final int NEGATIVE_CATEGORY_ID = -1;
    private static final int ZERO_CATEGORY_ID = 0;
    private static final int VALID_CATEGORY_ID = 1;
    private static final Map<Integer, Integer> VALID_MAP_VALUE = createMap(1, 1);

    private static Map<Integer, Integer> createMap(Integer key, Integer value) {
        Map<Integer, Integer> result = new HashMap<Integer, Integer>();
        result.put(key, value);
        return Collections.unmodifiableMap(result);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    Category category;
    Category testCategory;

    @Before
    public void setUp() {
        category = new Category(9, VALID_CATEGORY_NAME, VALID_CATEGORY_DESCRIPTION);
    }

    /***
     * Test {@link Category#checkCategoryParametersValidity(int, String, String)} will
     * throw an {@link IllegalArgumentException} when given a negative id.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test
    public void testThreeArgCategoryConstructorValidParameters() {
        category = new Category(VALID_CATEGORY_ID, VALID_CATEGORY_NAME, VALID_CATEGORY_DESCRIPTION);
        assertEquals(VALID_CATEGORY_ID, category.getId());
        assertEquals(VALID_CATEGORY_NAME, category.getName());
        assertEquals(VALID_CATEGORY_DESCRIPTION, category.getDescription());
    }

    /***
     * Test {@link Category#checkCategoryParametersValidity(int, String, String)} will
     * throw an {@link IllegalArgumentException} when given an id of 0.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testThreeArgCategoryConstructorNegativeCategoryID() {
        try {
            new Category(NEGATIVE_CATEGORY_ID, VALID_CATEGORY_NAME, VALID_CATEGORY_DESCRIPTION);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_ID_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#checkCategoryParametersValidity(int, String, String)} will
     * throw an {@link IllegalArgumentException} when given a null name.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testThreeArgCategoryConstructorZeroCategoryID() {
        try {
            new Category(ZERO_CATEGORY_ID, VALID_CATEGORY_NAME, VALID_CATEGORY_DESCRIPTION);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_ID_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#checkCategoryParametersValidity(int, String, String)} will
     * throw an {@link IllegalArgumentException} when given an empty ("") name.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testThreeArgCategoryConstructorNullCategoryName() {
        try {
            new Category(VALID_CATEGORY_ID, null, VALID_CATEGORY_DESCRIPTION);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_NAME_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#checkCategoryParametersValidity(int, String, String)} will
     * throw an {@link IllegalArgumentException} when given a blank (" ") name.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testThreeArgCategoryConstructorEmptyCategoryName() {
        try {
            new Category(VALID_CATEGORY_ID, "", VALID_CATEGORY_DESCRIPTION);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_NAME_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#checkCategoryParametersValidity(int, String, String)} will
     * throw an {@link IllegalArgumentException} when given a null description.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testThreeArgCategoryConstructorBlankCategoryName() {
        try {
            new Category(VALID_CATEGORY_ID, " ", VALID_CATEGORY_DESCRIPTION);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_NAME_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#Category(int, String, String)} with null category
     * description.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testThreeArgCategoryConstructorNullCategoryDescription() {
        try {
            new Category(VALID_CATEGORY_ID, VALID_CATEGORY_NAME, null);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_DESCRIPTION_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#Category(int, String, String)} with empty category
     * description.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */

    @Test(expected = IllegalArgumentException.class)
    public void testThreeArgCategoryConstructorEmptyCategoryDescription() {
        try {
            new Category(VALID_CATEGORY_ID, VALID_CATEGORY_NAME, "");
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_DESCRIPTION_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#Category(int, String, String)} with blank category
     * description.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testThreeArgCategoryConstructorBlankCategoryDescription() {
        try {
            new Category(VALID_CATEGORY_ID, VALID_CATEGORY_NAME, " ");
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_DESCRIPTION_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link Category#Category(int, String, String, int, Map)} to run
     * successfully with all valid parameters.
     */
    @Test
    public void testCategoryConstructorValidParametersWithResourceCount() {
        category = new Category(VALID_CATEGORY_ID, VALID_CATEGORY_NAME, VALID_CATEGORY_DESCRIPTION,
                VALID_RESOURCE_COUNT, VALID_MAP_VALUE);
        assertEquals(VALID_CATEGORY_ID, category.getId());
        assertEquals(VALID_CATEGORY_NAME, category.getName());
        assertEquals(VALID_CATEGORY_DESCRIPTION, category.getDescription());
        assertEquals(VALID_RESOURCE_COUNT, category.getResourcesCount());
        assertEquals(VALID_MAP_VALUE, category.getResourceCountPerSkillLevel());
    }

    /***
     * Test {@link Category#Category(int, String, String, int, Map)} with negative
     * category ID.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFourArgCategoryConstructorNegativeCategoryID() {
        try {
            new Category(NEGATIVE_CATEGORY_ID, VALID_CATEGORY_NAME, VALID_CATEGORY_DESCRIPTION, VALID_RESOURCE_COUNT,
                    VALID_MAP_VALUE);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_ID_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#Category(int, String, String, int, Map)} with zero
     * category ID.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFourArgCategoryConstructorZeroCategoryID() {
        try {
            new Category(ZERO_CATEGORY_ID, VALID_CATEGORY_NAME, VALID_CATEGORY_DESCRIPTION, VALID_RESOURCE_COUNT,
                    VALID_MAP_VALUE);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_ID_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#Category(int, String, String, int, Map)} with null
     * category name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFourArgCategoryConstructorNullCategoryName() {
        try {
            new Category(VALID_CATEGORY_ID, null, VALID_CATEGORY_DESCRIPTION, VALID_RESOURCE_COUNT, VALID_MAP_VALUE);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_NAME_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#Category(int, String, String, int, Map)} with empty
     * category name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFourArgCategoryConstructorEmptyCategoryName() {
        try {
            new Category(VALID_CATEGORY_ID, "", VALID_CATEGORY_DESCRIPTION, VALID_RESOURCE_COUNT, VALID_MAP_VALUE);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_NAME_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#Category(int, String, String, int, Map)} with blank
     * category name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFourArgCategoryConstructorBlankCategoryName() {
        try {
            new Category(VALID_CATEGORY_ID, " ", VALID_CATEGORY_DESCRIPTION, VALID_RESOURCE_COUNT, VALID_MAP_VALUE);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_NAME_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#Category(int, String, String, int, Map)} with null
     * category description.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFourArgCategoryConstructorNullCategoryDescription() {
        try {
            new Category(VALID_CATEGORY_ID, VALID_CATEGORY_NAME, null, VALID_RESOURCE_COUNT, VALID_MAP_VALUE);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_DESCRIPTION_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#Category(int, String, String, int, Map)} with empty
     * category description.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFourArgCategoryConstructorEmptyCategoryDescription() {
        try {
            new Category(VALID_CATEGORY_ID, VALID_CATEGORY_NAME, "", VALID_RESOURCE_COUNT, VALID_MAP_VALUE);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_DESCRIPTION_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#Category(int, String, String, int, Map)} with blank
     * category description.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFourArgCategoryConstructorBlankCategoryDescription() {
        try {
            new Category(VALID_CATEGORY_ID, VALID_CATEGORY_NAME, " ", VALID_RESOURCE_COUNT, VALID_MAP_VALUE);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_DESCRIPTION_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#Category(int)} will throw an
     * {@link IllegalArgumentException} when given a negative id.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCategoryIntConstructorNegativeId() {
        try {
            new Category(NEGATIVE_CATEGORY_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_ID_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#Category(int)} will throw an
     * {@link IllegalArgumentException} when given an id of 0.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCategoryIntConstructorZeroId() {
        try {
            new Category(ZERO_CATEGORY_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_ID_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#Category(int)} when given a valid id.
     */
    @Test
    public void testCategoryIntConstructorValidId() {
        category = new Category(VALID_CATEGORY_ID);
        assertEquals(VALID_CATEGORY_ID, category.getId());
    }

    /***
     * Test {@link Category#setId(int)} throws {@link IllegalArgumentException}
     * when given a negative ID
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeID() {
        try {
            category.setId(NEGATIVE_CATEGORY_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_ID_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#setId(int)} throws {@link IllegalArgumentException}
     * when given an ID of 0
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testZeroID() {
        try {
            category.setId(ZERO_CATEGORY_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_ID_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#setId(int)} with a valid ID and
     * {@link Category#getId()} returns the set ID.
     */
    @Test
    public void testValidID() {
        category.setId(VALID_CATEGORY_ID);
        assertEquals(VALID_CATEGORY_ID, category.getId());
    }

    /***
     * Test {@link Category#setName(String)} throws
     * {@link IllegalArgumentException} when given a null name.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullName() {
        try {
            category.setName(null);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_NAME_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#setName(String)} throws
     * {@link IllegalArgumentException} when given an empty ("") name.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyName() {
        try {
            category.setName(EMPTY_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_NAME_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#setName(String)} throws
     * {@link IllegalArgumentException} when given an blank (" ") name.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBlankName() {
        try {
            category.setName(BLANK_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_NAME_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#setName(String)} with a valid name and
     * {@link Category#getName()} returns the set name.
     */
    @Test
    public void testValidName() {
        category.setName(VALID_CATEGORY_NAME);
        assertEquals(VALID_CATEGORY_NAME, category.getName());
    }

    /***
     * Test {@link Category#setDescription(String)} throws
     * {@link IllegalArgumentException} when given a null description.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullDesc() {
        try {
            category.setDescription(null);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_DESCRIPTION_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#setDescription(String)} throws
     * {@link IllegalArgumentException} when given an empty ("") description.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyDesc() {
        try {
            category.setDescription(EMPTY_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_DESCRIPTION_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#setDescription(String)} throws
     * {@link IllegalArgumentException} when given an blank (" ") description.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBlankDesc() {
        try {
            category.setDescription(BLANK_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_DESCRIPTION_MSG, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#setDescription(String)} with valid description and
     * {@link Category#getDescription()} returns the set description.
     */
    @Test
    public void testValidDesc() {
        category.setDescription(VALID_CATEGORY_DESCRIPTION);
        assertEquals(VALID_CATEGORY_DESCRIPTION, category.getDescription());
    }

    /***
     * Test {@link Category#setResourcesCount(int)} with a valid resource count
     * and {@link Category#getResourcesCount()} returns the set resource count.
     */
    @Test
    public void testSetResourceCount() {
        category.setResourcesCount(VALID_RESOURCE_COUNT);
        assertEquals(VALID_RESOURCE_COUNT, category.getResourcesCount());
    }

    /***
     * Test {@link Category#checkCategoryParametersValidity(int, String, String)} will
     * throw an {@link IllegalArgumentException} when given a negative id.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckParametersValidityNegativeId() {
        try {
            Category.checkCategoryParametersValidity(NEGATIVE_CATEGORY_ID, VALID_CATEGORY_NAME,
                    VALID_CATEGORY_DESCRIPTION);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_ID_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link Category#setResourceCountPerSkillLevel(Map)} to throw
     * {@link IllegalArgumentException} when resourceCountPerSkillLevel
     * {@link Map} is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceCountPerSkillLevelForEmptyMap() {
        try {
            Map<Integer, Integer> resourceCountPerSkillLevel = Collections.emptyMap();
            category.setResourceCountPerSkillLevel(resourceCountPerSkillLevel);
        } catch (final IllegalArgumentException e) {
            assertEquals(MAP_EMPTY_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#checkCategoryParametersValidity(int, String, String)} will
     * throw an {@link IllegalArgumentException} when given an id of 0.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckParametersValidityZeroId() {
        try {
            Category.checkCategoryParametersValidity(ZERO_CATEGORY_ID, VALID_CATEGORY_NAME, VALID_CATEGORY_DESCRIPTION);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_ID_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link Category#setResourceCountPerSkillLevel(Map)} to throw
     * {@link IllegalArgumentException} when resourceCountPerSkillLevel
     * {@link Map} is <code>null</code>.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceCountPerSkillLevelForNullMap() {
        try {
            category.setResourceCountPerSkillLevel(null);
        } catch (final IllegalArgumentException e) {
            assertEquals(MAP_NULL_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#checkCategoryParametersValidity(int, String, String)} will
     * throw an {@link IllegalArgumentException} when given a null name.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckParametersValidityNullName() {
        try {
            Category.checkCategoryParametersValidity(VALID_CATEGORY_ID, null, VALID_CATEGORY_DESCRIPTION);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_NAME_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link Category#setResourceCountPerSkillLevel(Map)} to throw
     * {@link IllegalArgumentException} when difficultyLevel key of
     * resourceCountPerSkillLevel {@link Map} is zero(less than one).
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceCountPerSkillLevelForKeyZero() {
        try {
            category.setResourceCountPerSkillLevel(createMap(0, 1));
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_DIFFICULTY_LEVEL, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#checkCategoryParametersValidity(int, String, String)} will
     * throw an {@link IllegalArgumentException} when given an empty ("") name.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckParametersValidityEmptyName() {
        try {
            Category.checkCategoryParametersValidity(VALID_CATEGORY_ID, EMPTY_STRING, VALID_CATEGORY_DESCRIPTION);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_NAME_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link Category#setResourceCountPerSkillLevel(Map)} to throw
     * {@link IllegalArgumentException} when difficultyLevel key of
     * resourceCountPerSkillLevel {@link Map} is <code>null</code>.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceCountPerSkillLevelForNullKey() {
        try {
            category.setResourceCountPerSkillLevel(createMap(null, 1));
        } catch (final IllegalArgumentException e) {
            assertEquals(KEY_NULL_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#checkCategoryParametersValidity(int, String, String)} will
     * throw an {@link IllegalArgumentException} when given a blank (" ") name.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckParametersValidityBlankName() {
        try {
            Category.checkCategoryParametersValidity(VALID_CATEGORY_ID, BLANK_STRING, VALID_CATEGORY_DESCRIPTION);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_NAME_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link Category#setResourceCountPerSkillLevel(Map)} to throw
     * {@link IllegalArgumentException} when difficultyLevel key of
     * resourceCountPerSkillLevel {@link Map} is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceCountPerSkillLevelForNegativeKey() {
        try {
            category.setResourceCountPerSkillLevel(createMap(-1, 1));
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_DIFFICULTY_LEVEL, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#checkCategoryParametersValidity(int, String, String)} will
     * throw an {@link IllegalArgumentException} when given a null description.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCategoryConstructorNullDescription() {
        try {
            Category.checkCategoryParametersValidity(VALID_CATEGORY_ID, VALID_CATEGORY_NAME, null);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_DESCRIPTION_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link Category#setResourceCountPerSkillLevel(Map)} to throw
     * {@link IllegalArgumentException} when difficultyLevel, key of
     * resourceCountPerSkillLevel {@link Map} is greater than five.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceCountPerSkillLevelForHighKey() {
        try {
            category.setResourceCountPerSkillLevel(createMap(6, 1));
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_DIFFICULTY_LEVEL, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#checkCategoryParametersValidity(int, String, String)} will
     * throw an {@link IllegalArgumentException} when given an empty ("")
     * description.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckParametersValidityEmptyDescription() {
        try {
            Category.checkCategoryParametersValidity(VALID_CATEGORY_ID, VALID_CATEGORY_NAME, EMPTY_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_DESCRIPTION_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link Category#setResourceCountPerSkillLevel(Map)} to run
     * successfully when difficultyLevel, key of resourceCountPerSkillLevel
     * {@link Map} is a valid key(lower limit).
     */
    @Test
    public void testSetResourceCountPerSkillLevelForValidLowKey() {
        category.setResourceCountPerSkillLevel(createMap(1, 2));
        assertEquals(createMap(1, 2), category.getResourceCountPerSkillLevel());
    }

    /***
     * Expects {@link Category#setResourceCountPerSkillLevel(Map)} to run
     * successfully when difficultyLevel, key of resourceCountPerSkillLevel
     * {@link Map} is a valid key(upper limit).
     */
    @Test
    public void testSetResourceCountPerSkillLevelForValidHighKey() {
        category.setResourceCountPerSkillLevel(createMap(2, 12));
        assertEquals(createMap(2, 12), category.getResourceCountPerSkillLevel());
    }

    /***
     * Expects {@link Category#setResourceCountPerSkillLevel(Map)} to throw
     * {@link IllegalArgumentException} when resource count, value of
     * resourceCountPerSkillLevel {@link Map} is negative for any valid key.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceCountPerSkillLevelForNegativeValue() {
        try {
            category.setResourceCountPerSkillLevel(createMap(2, -1));
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_COUNT_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /***
     * Test {@link Category#checkCategoryParametersValidity(int, String, String)} will
     * throw an {@link IllegalArgumentException} when given a blank (" ")
     * description.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckParametersValidityBlankDescription() {
        try {
            Category.checkCategoryParametersValidity(VALID_CATEGORY_ID, VALID_CATEGORY_NAME, BLANK_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_DESCRIPTION_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link Category#setResourceCountPerSkillLevel(Map)} to throw
     * {@link IllegalArgumentException} when resource count, value of
     * resourceCountPerSkillLevel {@link Map} is zero(less than one) for any
     * valid key.
     */
    @Test
    public void testSetResourceCountPerSkillLevelForZeroValue() {
        category.setResourceCountPerSkillLevel(createMap(2, 0));
        assertEquals(createMap(2, 0), category.getResourceCountPerSkillLevel());
    }

    /***
     * Expects {@link Category#setResourceCountPerSkillLevel(Map)} to throw
     * {@link IllegalArgumentException} when resource count, value of
     * resourceCountPerSkillLevel {@link Map} is <code>null</code> for any valid
     * key.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceCountPerSkillLevelForNullValue() {
        try {
            category.setResourceCountPerSkillLevel(createMap(2, null));
        } catch (final IllegalArgumentException e) {
            assertEquals(VALUE_NULL_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests the {@link Category#equals(Object)} method for three cases-
     * <ul>
     * <li><code>True </code>when both the {@link Category} objects have the
     * same id, but different name and description</li>
     * <li><code>True </code>when both the {@link Category} objects have the
     * same id, name and description</li>
     * <li><code>False </code>when both the {@link Category} objects have the
     * different id, name and description</li>
     * </ul>
     */
    @Test
    public void testEqualsSameObjects() {
        testCategory = new Category(9, "test", "test");
        assertEquals(true, category.equals(new Category(9, TEST, TEST)));
        assertEquals(true, category.equals(new Category(9, VALID_CATEGORY_NAME, VALID_CATEGORY_DESCRIPTION)));
        assertEquals(false, category.equals(new Category(1, TEST, TEST)));
    }

    /***
     * Expects {@link Category#setResourceCountPerSkillLevel(Map)} to run
     * successfully when resource count, value of resourceCountPerSkillLevel
     * {@link Map} is valid for any valid key.
     */
    @Test
    public void testSetResourceCountPerSkillLevelForValidValue() {
        category.setResourceCountPerSkillLevel(createMap(2, 12));
        assertEquals(createMap(2, 12), category.getResourceCountPerSkillLevel());
    }

    /***
     * Expects {@link Category#setResourcesCount(int)} to throw
     * {@link IllegalArgumentException} when resource count is negative
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourcesCount_Negative() {
        try {
            category.setResourcesCount(NEGATIVE_RESOURCES_COUNT);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_RESOURCE_COUNT, e.getMessage());
            throw e;
        }
    }

    /***
     * Tests {@link Category#setDifficultyLevel(int)} and
     * {@link Category#getDifficultyLevel()} for valid difficulty level
     */
    @Test
    public void testSetAndGetDifficultyLevel() {
        category.setDifficultyLevel(1);
        assertEquals(1, category.getDifficultyLevel());
    }

    /***
     * Tests {@link Category#setDifficultyLevel(int)} when difficulty level is
     * less than 1
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetDifficultyLevelLessThanOne() {
        try {
            category.setDifficultyLevel(-1);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_DIFFICULTY_LEVEL, e.getMessage());
            throw e;
        }
    }

    /***
     * Tests {@link Category#setDifficultyLevel(int)} when difficulty level is
     * greater than 5
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetDifficultyLevelGreaterThanFive() {
        try {
            category.setDifficultyLevel(6);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_DIFFICULTY_LEVEL, e.getMessage());
            throw e;
        }
    }
}
