package com.cerner.devcenter.education.models;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;

import com.cerner.devcenter.education.controllers.ProfileController;

/**
 * Contains a unique ID, a name, short description and the count of resources
 * associated with category. Used to describe the subject matter of a
 * {@link Resource} or interested learning areas of a user. Validations on data
 * attributes of this class are done using the ObjectAndInputValidator (
 * {@link com.cerner.devcenter.education.helpers.ObjectAndInputValidator}).
 *
 * @author SB033185
 * @author Samuel Stephen (SS044662)
 * @author Jacob Zimmermann (JZ022690)
 * @author Rishabh Bhojak (RB048032)
 * @author Navya Rangeneni (NR046827)
 */
public class Category {

    private static final String INVALID_ID = "Category ID should be greater than zero";
    private static final String INVALID_NAME = "Category name is null/empty/blank";
    private static final String INVALID_DESCRIPTION = "Category description is null/empty/blank";
    private final static String INVALID_RESOURCE_COUNT = "Resource Count cannot be negative";
    private final static String MAP_EMPTY_ERROR_MESSAGE = "map cannot be empty";
    private final static String MAP_NULL_ERROR_MESSAGE = "map cannot be null";
    private final static String KEY_NULL_ERROR_MESSAGE = "difficultyLevel cannot be null";
    private final static String VALUE_NULL_ERROR_MESSAGE = "resource count cannot be null";
    private final static String INVALID_DIFFICULTY_LEVEL = "difficultyLevel must be on a scale of 1-5";
    private final static String RESOURCE_COUNT_ERROR_MESSAGE = "resource count cannot be negative";

    private final static int MIN_DIFFICULY_LEVEL = 1;
    private final static int MAX_DIFFICULY_LEVEL = 5;

    private int id;
    private int resourcesCount;
    private String name;
    private String description;
    private int difficultyLevel;
    private Map<Integer, Integer> resourceCountPerSkillLevel = new HashMap<>();

    /***
     * Creates a new {@link Category} Should only be used by Spring and testing.
     */
    public Category() {
    }

    /**
     * Used to create a new {@link Category} with the given id, name,
     * description, resourceCount and resourceCountPerSkillLevel
     *
     * @param id
     *            integer representing the unique identifier for the category
     *            (cannot be zero or negative).
     * @param name
     *            String containing the name of the category (cannot be
     *            <code>null</code>, blank, or empty).
     * @param description
     *            String containing the description of the category (cannot be
     *            <code>null</code>, blank, or empty).
     * @param resourcesCount
     *            integer representing the number of resources associated with
     *            the category (cannot be negative)
     * @param resourceCountPerSkillLevel
     *            is a {@link Map} collection representing the {@link Integer}
     *            difficultyLevel as its key and {@link Integer} resource count
     *            for that difficultyLevel as its value (difficulty level should
     *            be greater than zero and less than five and resource count
     *            should be greater than or equal to zero)
     * @throws IllegalArgumentException
     *             when
     *             <ul>
     *             <li>id is zero or negative</li>
     *             <li>name is <code>null</code>, blank, or empty</li>
     *             <li>description is <code>null</code>, blank, or empty</li>
     *             <li>recourceCount is less than zero</li>
     *             <li>{@link Map} keys difficultyLevel is less than one or
     *             greater than five</li>
     *             <li>{@link Map} values resource count for any difficultyLevel
     *             is less than zero</li>
     *             </ul>
     */

    public Category(final int id, final String name, final String description, final int resourcesCount,
            Map<Integer, Integer> resourceCountPerSkillLevel) {
        this(id, name, description);
        setResourcesCount(resourcesCount);
        setResourceCountPerSkillLevel(resourceCountPerSkillLevel);
    }

    /**
     * Used to create a new {@link Category} with the given id, name, and
     * description
     *
     * @param id
     *            integer representing the unique identifier for the category
     *            (cannot be zero or negative).
     * @param name
     *            String containing the name of the category (cannot be null,
     *            blank, or empty).
     * @param description
     *            String containing the description of the category (cannot be
     *            null, blank, or empty).
     * @throws IllegalArgumentException
     *             when
     *             <ul>
     *             <li>id is zero or negative</li>
     *             <li>name is <code>null</code>, blank, or empty</li>
     *             <li>description is <code>null</code>, blank, or empty</li>
     *             </ul>
     */
    public Category(final int id, final String name, final String description) {
        this(id);
        setName(name);
        setDescription(description);
    }

    /**
     * Used to create a {@link Category} while updating the user interested
     * category within {@link ProfileController}
     *
     * @param categoryId
     *            an {@link Integer} that is the unique id of the category. Must
     *            be greater than zero
     * @throws IllegalArgumentException
     *             when categoryId is zero or negative
     */
    public Category(final int categoryId) {
        setId(categoryId);
    }

    /**
     * Returns the ID of the {@link Category} object.
     *
     * @return integer that represents the ID of the category object.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the {@link Category} object after necessary validations.
     *
     * @param id
     *            an integer representing the unique ID of the category (cannot
     *            be 0 or negative).
     * @throws IllegalArgumentException
     *             if id is less zero or negative
     */
    public void setId(final int id) {
        checkArgument(id > 0, INVALID_ID);
        this.id = id;
    }

    /**
     * Returns the name of the {@link Category} object.
     *
     * @return String that represents the name of the category object.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the {@link Category} object after necessary validations.
     *
     * @param name
     *            String representing the name of the category (cannot be null,
     *            blank, or empty).
     * @throws IllegalArgumentException
     *             when name is null, blank, or empty
     */
    public void setName(final String name) {
        checkArgument(StringUtils.isNotBlank(name), INVALID_NAME);
        this.name = name;
    }

    /**
     * Returns the description of the {@link Category} object.
     *
     * @return String that represents the description of the category object.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the {@link Category} object after necessary
     * validations.
     *
     * @param description
     *            a String representing the description of the category (cannot
     *            be null, blank, or empty and has to be a valid description).
     * @throws IllegalArgumentException
     *             when description is null, blank, or empty
     */
    public void setDescription(final String description) {
        checkArgument(StringUtils.isNotBlank(description), INVALID_DESCRIPTION);
        this.description = description;
    }

    /***
     * Check the parameters of a {@link Category} for illegal values
     *
     * @param id
     *            integer representing the unique identifier for the category
     *            (cannot be zero or negative).
     * @param name
     *            String containing the name of the category (cannot be null,
     *            blank, or empty).
     * @param description
     *            String containing the description of the category (cannot be
     *            null, blank, or empty).
     * @throws IllegalArgumentException
     *             when
     *             <ul>
     *             <li>id is zero or negative</li>
     *             <li>name is <code>null</code>, blank, or empty</li>
     *             <li>description is <code>null</code>, blank, or empty</li>
     *             </ul>
     */
    public static void checkCategoryParametersValidity(final int id, final String name, final String description) {
        checkArgument(id > 0, INVALID_ID);
        checkArgument(StringUtils.isNotBlank(name), INVALID_NAME);
        checkArgument(StringUtils.isNotBlank(description), INVALID_DESCRIPTION);
    }

    /**
     * Returns a resourcesCount of {@link Category} object.
     *
     * @return integer that represents the resourcesCount of the category
     *         object.
     */
    public int getResourcesCount() {
        return resourcesCount;
    }

    /**
     * Sets the count of number of resources of the {@link Category} object.
     *
     * @param resourcesCount
     *            a integer representing the count of resources for the
     *            category.
     * @throws IllegalArgumentException
     *             when resourceCount is less than zero
     */
    public void setResourcesCount(final int resourcesCount) {
        checkArgument(resourcesCount >= 0, INVALID_RESOURCE_COUNT);
        this.resourcesCount = resourcesCount;
    }

    /**
     * Returns resourceCountPerSkillLevel {@link Map} collection of
     * difficultyLevel as its key and resourceCount for that difficultyLevelas
     * its value.
     *
     * @return {@link Map} collection that contains {@link Integer}
     *         difficultyLevel as its key and {@link Integer} resourceCount for
     *         that difficultyLevel as its value.
     */
    public Map<Integer, Integer> getResourceCountPerSkillLevel() {
        return resourceCountPerSkillLevel;
    }

    /**
     * Sets the difficultyLevel and resourceCount for that difficulty level of
     * the {@link Resource} object for a {@link Category} object.
     *
     * @param resourceCountPerSkillLevel
     *            a {@link Map} collection that contains {@link Integer}
     *            difficultyLevel as its key and {@link Integer} resourceCount
     *            for that difficultyLevel as its value.
     * @throws IllegalArgumentException
     *             when
     *             <ul>
     *             <li>resourceCountPerSkillLevel is <code>null</code></li>
     *             <li>resourceCountPerSkillLevel {@link Map} size is less than
     *             or equal to zero</li>
     *             <li>{@link Map} keys difficultyLevel is
     *             <code>null</code></li>
     *             <li>{@link Map} keys difficultyLevel is less than one or
     *             greater than five</li>
     *             <li>{@link Map} values resourceCount is
     *             <code>null</code></li>
     *             <li>{@link Map} values resourceCount for any difficultyLevel
     *             is less than zero</li>
     *             </ul>
     */
    public void setResourceCountPerSkillLevel(Map<Integer, Integer> resourceCountPerSkillLevel) {
        checkArgument(resourceCountPerSkillLevel != null, MAP_NULL_ERROR_MESSAGE);
        checkArgument(resourceCountPerSkillLevel.size() > 0, MAP_EMPTY_ERROR_MESSAGE);
        for (Map.Entry<Integer, Integer> entries : resourceCountPerSkillLevel.entrySet()) {
            Integer key = entries.getKey();
            Integer value = entries.getValue();
            checkArgument(key != null, KEY_NULL_ERROR_MESSAGE);
            checkArgument(key > 0 && key < 6, INVALID_DIFFICULTY_LEVEL);
            checkArgument(value != null, VALUE_NULL_ERROR_MESSAGE);
            checkArgument(value >= 0, RESOURCE_COUNT_ERROR_MESSAGE);
        }
        this.resourceCountPerSkillLevel = resourceCountPerSkillLevel;
    }

    /**
     * Returns difficulty level of the {@link Category}.
     *
     * @return integer that represents difficulty level of the category object.
     */
    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    /**
     * Sets the difficulty level of {@link Category}.
     *
     * @param difficultyLevel
     *            integer representing difficulty level of {@link Category}
     *
     * @throws IllegalArgumentException
     *             when difficulty level is not in scale 1-5.
     */
    public void setDifficultyLevel(int difficultyLevel) {
        Range<Integer> desiredRange = Range.between(MIN_DIFFICULY_LEVEL, MAX_DIFFICULY_LEVEL);
        checkArgument(desiredRange.contains(difficultyLevel), INVALID_DIFFICULTY_LEVEL);
        this.difficultyLevel = difficultyLevel;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Category other = (Category) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Category [id=" + id + ", name=" + name + ", description=" + description + ", difficultyLevel="
                + difficultyLevel + "]";
    }
}