package com.cerner.devcenter.education.models;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The following class contains test cases that test the functions of the (
 * {@link ResourceCategoryRelation})
 *
 * @author Abhi Purella (AP045635)
 * @author Navya Rangeneni (NR046827)
 * @author Vincent Dasari (VD049645)
 * @author Rishabh Bhojak (RB048032)
 * @author Santosh Kumar (SK0513430
 */
public class ResourceCategoryRelationTest {

    private static final String RESOURCE_ID_ERROR_MESSAGE = "Resource ID should be greater than 0.";
    private static final String RESOURCE_NAME_ERROR_MESSAGE = "Resource name is null/empty/blank";
    private static final String RESOURCE_DESCRIPTION_ERROR_MESSAGE = "Resource description is null/empty";
    private static final String CATEGORY_DESCRIPTION_ERROR_MESSAGE = "Category description is null/empty";
    private static final String CATEGORY_ID_ERROR_MESSAGE = "Category ID should be greater than 0";
    private static final String CATEGORY_NAME_ERROR_MESSAGE = "Category name is null/empty";
    private static final String DIFFICULTY_LEVEL_ERROR_MESSAGE = "difficultyLevel should be on a scale of 1-5";
    private static final String RESOURCE_TYPE_ERROR_MESSAGE = "Resource Type object cannot be null";
    private final static String EMPTY_STRING = "";
    private final static String BLANK_STRING = "    ";
    private static final String VALID_RESOURCE_OWNER = "owner";
    private static final int RESOURCE_OWNER_LENGTH_UPPER_BOUND = 8;
    private static final String INVALID_LENGTH_RESOURCE_OWNER_ERROR_MESSAGE = "Resource owner is greater than 8 characters.";
    private static final String AVERAGE_RATING_ERROR_MESSAGE = "Average Rating can not be less than 0";
    private static final String RESOURCE_OWNER_ERROR_MESSAGE = "Resource owner cannot be null/empty/blank";

    private static URL resourcelink;
    private ResourceType resourceType;
    private ResourceCategoryRelation resourceCategoryRelation;

    @Before
    public void setUp() throws MalformedURLException {
        resourceCategoryRelation = new ResourceCategoryRelation();
        resourcelink = new URL("http://www.junit.org");
        resourceType = new ResourceType(2, "YouTube");
    }

    @After
    public void tearDown() {
        resourceCategoryRelation = null;
    }

    /**
     * Tests setResourceId when resourceId is zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceIdForZero() {
        try {
            resourceCategoryRelation.setResourceId(0);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_ID_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests setResourceId when resourceId is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceIdForNegative() {
        try {
            resourceCategoryRelation.setResourceId(-1);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_ID_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests setResourceId and getResourceId with valid resourceId.
     */
    @Test
    public void testSetAndGetResourceIdForValidResourceID() {
        resourceCategoryRelation.setResourceId(1);
        assertEquals(1, resourceCategoryRelation.getResourceId());
    }

    /**
     * Tests setResourceLink and getResourceLink with valid URL.
     */
    @Test
    public void testGetAndSetResourceLinkForValidURL() throws MalformedURLException {
        resourceCategoryRelation.setResourceLink(resourcelink);
        assertEquals(resourcelink, resourceCategoryRelation.getResourceLink());
    }

    /**
     * Tests setResourceName with null
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceNameForNull() {
        try {
            resourceCategoryRelation.setResourceName(null);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_NAME_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests setResourceName with empty
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceNameForEmptyString() {
        try {
            resourceCategoryRelation.setResourceName("");
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_NAME_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests setResourceName with whiteSpace
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetRespourceNameForWhiteSpace() {
        try {
            resourceCategoryRelation.setResourceName(" ");
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_NAME_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests setResourceName and getResourceName with valid name.
     */
    @Test
    public void testSetGetResourceNameForValidResourceName() {
        resourceCategoryRelation.setResourceName("Test");
        assertEquals("Test", resourceCategoryRelation.getResourceName());
    }

    /**
     * Test {@link ResourceCategoryRelation#setResourceType(ResourceType)} when
     * resource type null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceTypeWhenNull() {
        try {
            resourceCategoryRelation.setResourceType(null);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_TYPE_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Test {@link ResourceCategoryRelation#setResourceType(ResourceType)} and
     * {@link ResourceCategoryRelation#getResourceType()} with valid input.
     */
    @Test
    public void testSetGetResourceTypeValid() {
        resourceCategoryRelation.setResourceType(resourceType);
        assertEquals(resourceType, resourceCategoryRelation.getResourceType());
    }

    /**
     * Tests setDifficultyLevel when DifficultyLevel is zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetDifficultyLevelForZero() {
        try {
            resourceCategoryRelation.setDifficultyLevel(0);
        } catch (final IllegalArgumentException e) {
            assertEquals(DIFFICULTY_LEVEL_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests setDifficultyLevel when DifficultyLevel is negative.
     *
     * @throws IllegalArgumentException
     *             when DifficultyLevel is negative
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetDifficultyLevelForNegative() {
        try {
            resourceCategoryRelation.setDifficultyLevel(-1);
        } catch (final IllegalArgumentException e) {
            assertEquals(DIFFICULTY_LEVEL_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests setDifficultyLevel and getDifficultyLevel with valid
     * DifficultyLevel.
     */
    @Test
    public void testSetDifficultyLevelForValid() {
        resourceCategoryRelation.setDifficultyLevel(1);
        assertEquals(1, resourceCategoryRelation.getDifficultyLevel());
    }

    /**
     * Tests setDifficultyLevel and getDifficultyLevel with boundary
     * DifficultyLevel value.
     */
    @Test
    public void testSetDifficultyLevelForBoundary() {
        resourceCategoryRelation.setDifficultyLevel(5);
        assertEquals(5, resourceCategoryRelation.getDifficultyLevel());
    }

    /**
     * Tests setDifficultyLevel with out of limits DifficultyLevel value.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetDifficultyLevelForOutOfLimits() {
        try {
            resourceCategoryRelation.setDifficultyLevel(6);
        } catch (final IllegalArgumentException e) {
            assertEquals(DIFFICULTY_LEVEL_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with valid input.
     */
    @Test
    public void testEightInputConstructorForValidInput() throws MalformedURLException {
        resourceCategoryRelation = new ResourceCategoryRelation(
                1,
                "test",
                resourcelink,
                resourceType,
                1,
                "test",
                "resourceDescription",
                "categoryDescription");
        assertEquals(1, resourceCategoryRelation.getResourceId());
        assertEquals("test", resourceCategoryRelation.getResourceName());
        assertEquals(resourcelink, resourceCategoryRelation.getResourceLink());
        assertEquals(resourceType, resourceCategoryRelation.getResourceType());
        assertEquals(1, resourceCategoryRelation.getCategoryId());
        assertEquals("test", resourceCategoryRelation.getCategoryName());
        assertEquals("resourceDescription", resourceCategoryRelation.getResourceDescription());
        assertEquals("categoryDescription", resourceCategoryRelation.getCategoryDescription());

    }

    /**
     * Tests constructor for with invalid categoryID.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testThreeInputConstructorForInvalidResourceCategoryId() {
        try {
            new ResourceCategoryRelation(1, 1, 0);
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_ID_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with invalid resourceId.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testThreeInputConstructorForInvalidResourceResourceId() {
        try {
            new ResourceCategoryRelation(0, 1, 1);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_ID_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with invalid resource name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testThreeInputConstructorForInvalidResourceDifficultyLevel() {
        try {
            new ResourceCategoryRelation(1, 0, 1);
        } catch (final IllegalArgumentException e) {
            assertEquals(DIFFICULTY_LEVEL_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests three constructor for with valid input.
     */
    @Test
    public void testThreeInputConstructorForValidInput() {
        resourceCategoryRelation = new ResourceCategoryRelation(1, 1, 1);
        assertEquals(1, resourceCategoryRelation.getResourceId());
        assertEquals(1, resourceCategoryRelation.getDifficultyLevel());
        assertEquals(1, resourceCategoryRelation.getCategoryId());
    }

    /**
     * Tests constructor for with invalid resource name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEightInputConstructorForInvalidResourceName() {
        try {
            new ResourceCategoryRelation(
                    1,
                    "",
                    resourcelink,
                    resourceType,
                    1,
                    "test",
                    "resourceDescription",
                    "categoryDescription");
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_NAME_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with null resourceName.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEightInputConstructorForNullResourceName() {
        try {
            new ResourceCategoryRelation(
                    1,
                    null,
                    resourcelink,
                    resourceType,
                    1,
                    "test",
                    "resourceDescription",
                    "categoryDescription");
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_NAME_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with invalid categoryId.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEightInputConstructorForInvalidCategoryId() {
        try {
            new ResourceCategoryRelation(
                    1,
                    "test",
                    resourcelink,
                    resourceType,
                    0,
                    "test",
                    "resourceDescription",
                    "categoryDescription");
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_ID_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with invalid resourceID.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEightInputConstructorForInvalidResourceId() {
        try {
            new ResourceCategoryRelation(
                    0,
                    "test",
                    resourcelink,
                    resourceType,
                    1,
                    "test",
                    "resourceDescription",
                    "categoryDescription");
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_ID_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with invalid category name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEightInputConstructorForInvalidCategoryName() {
        try {
            new ResourceCategoryRelation(
                    1,
                    "test",
                    resourcelink,
                    resourceType,
                    1,
                    "",
                    "resourceDescription",
                    "categoryDescription");
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_NAME_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with null categoryName.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEightInputConstructorForNullCategoryName() {
        try {
            new ResourceCategoryRelation(
                    1,
                    "test",
                    resourcelink,
                    resourceType,
                    1,
                    null,
                    "resourceDescription",
                    "categoryDescription");
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_NAME_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with invalid resourceDescription.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEightInputConstructorForInvalidResourceDescription() {
        try {
            new ResourceCategoryRelation(1, "test", resourcelink, resourceType, 1, "test", "", "categoryDescription");
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_DESCRIPTION_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with null resourceDescription.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEightInputConstructorForNullResourceDescription() {
        try {
            new ResourceCategoryRelation(1, "test", resourcelink, resourceType, 1, "test", null, "categoryDescription");
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_DESCRIPTION_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with invalid categoryDescription.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEightInputConstructorForInvalidCategoryDescription() {
        try {
            new ResourceCategoryRelation(1, "test", resourcelink, resourceType, 1, "test", "resourceDescription", "");
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_DESCRIPTION_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with null categoryDescription.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEightInputConstructorForNullCategoryDescription() {
        try {
            new ResourceCategoryRelation(1, "test", resourcelink, resourceType, 1, "test", "resourceDescription", null);
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_DESCRIPTION_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor with {@link ResourceType} as null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEightInputConstructorForNullResourceType() {
        try {
            new ResourceCategoryRelation(
                    1,
                    "test",
                    resourcelink,
                    null,
                    1,
                    "test",
                    "resourceDescription",
                    "categoryDescription");
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_TYPE_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with valid input.
     */
    @Test
    public void testMultipleInputConstructorForValidInput() {
        resourceCategoryRelation = new ResourceCategoryRelation(
                1,
                "test",
                resourcelink,
                resourceType,
                1,
                1,
                "test",
                "resourceDescription",
                "categoryDescription");
        assertEquals(1, resourceCategoryRelation.getResourceId());
        assertEquals("test", resourceCategoryRelation.getResourceName());
        assertEquals(1, resourceCategoryRelation.getDifficultyLevel());
        assertEquals(resourcelink, resourceCategoryRelation.getResourceLink());
        assertEquals(resourceType, resourceCategoryRelation.getResourceType());
        assertEquals(1, resourceCategoryRelation.getCategoryId());
        assertEquals("test", resourceCategoryRelation.getCategoryName());
        assertEquals("resourceDescription", resourceCategoryRelation.getResourceDescription());
        assertEquals("categoryDescription", resourceCategoryRelation.getCategoryDescription());
    }

    /**
     * Tests constructor for with invalid resourceID.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMultipleInputConstructorForInvalidResourceID() {
        try {
            new ResourceCategoryRelation(
                    0,
                    "test",
                    resourcelink,
                    resourceType,
                    1,
                    1,
                    "test",
                    "resourceDescription",
                    "categoryDescription");
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_ID_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with invalid resource name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMultipleInputConstructorForInvalidResourceName() {
        try {
            new ResourceCategoryRelation(
                    1,
                    "",
                    resourcelink,
                    resourceType,
                    1,
                    1,
                    "test",
                    "resourceDescription",
                    "categoryDescription");
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_NAME_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with null resource name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMultipleInputConstructorForNullResourceName() {
        try {
            new ResourceCategoryRelation(
                    1,
                    null,
                    resourcelink,
                    resourceType,
                    1,
                    1,
                    "test",
                    "resourceDescription",
                    "categoryDescription");
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_NAME_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with invalid category name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMultipleInputConstructorForInvalidCategoryName() {
        try {
            new ResourceCategoryRelation(
                    1,
                    "test",
                    resourcelink,
                    resourceType,
                    1,
                    1,
                    "",
                    "resourceDescription",
                    "categoryDescription");
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_NAME_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with null category name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMultipleInputConstructorForNullCategoryName() {
        try {
            new ResourceCategoryRelation(
                    1,
                    "test",
                    resourcelink,
                    resourceType,
                    1,
                    1,
                    null,
                    "resourceDescription",
                    "categoryDescription");
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_NAME_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with invalid resourceDescription.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMultipleInputConstructorForInvalidResourceDescription() {
        try {
            new ResourceCategoryRelation(
                    1,
                    "test",
                    resourcelink,
                    resourceType,
                    1,
                    1,
                    "test",
                    "",
                    "categoryDescription");
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_DESCRIPTION_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with null resourceDescription.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMultipleInputConstructorForNullResourceDescription() {
        try {
            new ResourceCategoryRelation(
                    1,
                    "test",
                    resourcelink,
                    resourceType,
                    1,
                    1,
                    "test",
                    null,
                    "categoryDescription");
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_DESCRIPTION_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with invalid categoryDescription.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMultipleInputConstructorForInvalidCategoryDescription() {
        try {
            new ResourceCategoryRelation(
                    1,
                    "test",
                    resourcelink,
                    resourceType,
                    1,
                    1,
                    "test",
                    "resourceDescription",
                    "");
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_DESCRIPTION_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with null categoryDescription.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMultipleInputConstructorForNullCategoryDescription() {
        try {
            new ResourceCategoryRelation(
                    1,
                    "test",
                    resourcelink,
                    resourceType,
                    1,
                    1,
                    "test",
                    "resourceDescription",
                    null);
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_DESCRIPTION_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with invalid difficulty Level.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMultipleInputConstructorForInvalidDifficultyLevel() {
        try {
            new ResourceCategoryRelation(
                    1,
                    "test",
                    resourcelink,
                    resourceType,
                    0,
                    1,
                    "test",
                    "resourceDescription",
                    "categoryDescription");
        } catch (final IllegalArgumentException e) {
            assertEquals(DIFFICULTY_LEVEL_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests constructor for with invalid categoryID.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMultipleInputConstructorForInvalidCategoryID() {
        try {
            new ResourceCategoryRelation(
                    1,
                    "test",
                    resourcelink,
                    resourceType,
                    1,
                    0,
                    "test",
                    "resourceDescription",
                    "categoryDescription");
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_ID_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects the multiple argument constructor
     * {@link ResourceCategoryRelation#ResourceCategoryRelation(int, String, URL, ResourceType, int, int, String, String, String, double, String)}
     * to set the values properly when valid inputs have been passed.
     */
    @Test
    public void testTenArgumentConstructorValidInput() throws MalformedURLException {
        resourceCategoryRelation = new ResourceCategoryRelation(
                1,
                "test",
                resourcelink,
                resourceType,
                1,
                1,
                "test",
                "resourceDescription",
                "categoryDescription",
                1.0,
                "owner");
        assertEquals(1, resourceCategoryRelation.getResourceId());
        assertEquals("test", resourceCategoryRelation.getResourceName());
        assertEquals(1, resourceCategoryRelation.getDifficultyLevel());
        assertEquals(resourcelink, resourceCategoryRelation.getResourceLink());
        assertEquals(resourceType, resourceCategoryRelation.getResourceType());
        assertEquals(1, resourceCategoryRelation.getCategoryId());
        assertEquals("test", resourceCategoryRelation.getCategoryName());
        assertEquals("resourceDescription", resourceCategoryRelation.getResourceDescription());
        assertEquals("categoryDescription", resourceCategoryRelation.getCategoryDescription());
        assertEquals(1.0, resourceCategoryRelation.getAverageRating(), 0.01);
        assertEquals("owner", resourceCategoryRelation.getResourceOwner());
    }

    /**
     * Expects the multiple argument constructor
     * {@link ResourceCategoryRelation#ResourceCategoryRelation(int, String, URL, ResourceType, int, int, String, String, String, double, String)}
     * to throw {@link IllegalArgumentException} when invalid average rating has
     * been passed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testTenArgumentConstructorInvalidAverageRating() throws MalformedURLException {
        try {
            new ResourceCategoryRelation(
                    1,
                    "test",
                    resourcelink,
                    resourceType,
                    1,
                    1,
                    "test",
                    "resourceDescription",
                    "categoryDescription",
                    -1,
                    "owner");
        } catch (IllegalArgumentException e) {
            assertEquals(AVERAGE_RATING_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests setCategoryId when categoryId is zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetCategoryIdForZero() {
        try {
            resourceCategoryRelation.setCategoryId(0);
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_ID_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests setCategoryId when categoryId is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetCategoryIdForNegative() {
        try {
            resourceCategoryRelation.setCategoryId(-1);
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_ID_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests setCategoryId and getCategoryId with valid categoryId.
     */
    @Test
    public void testSetAndGetCategoryIdForValidCategoryID() {
        resourceCategoryRelation.setResourceId(1);
        assertEquals(1, resourceCategoryRelation.getResourceId());
    }

    /**
     * Tests setCategoryName with null
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetCategoryNameForNull() {
        try {
            resourceCategoryRelation.setCategoryName(null);
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_NAME_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests setCategoryName with empty
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetCategoryNameForEmptyString() {
        try {
            resourceCategoryRelation.setCategoryName("");
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_NAME_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests setCategoryName with whiteSpace
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetCategoryNameForWhiteSpace() {
        try {
            resourceCategoryRelation.setCategoryName(" ");
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_NAME_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests setCategoryName and getCategoryName with valid name.
     */
    @Test
    public void testSetCategoryNameForValidCategoryName() {
        resourceCategoryRelation.setCategoryName("Test");
        assertEquals("Test", resourceCategoryRelation.getCategoryName());
    }

    /**
     * Tests setResourceDescription with null
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceDescriptionForNull() {
        try {
            resourceCategoryRelation.setResourceDescription(null);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_DESCRIPTION_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests setResourceDescription with empty
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceDescriptionForEmptyString() {
        try {
            resourceCategoryRelation.setResourceDescription("");
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_DESCRIPTION_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests setResourceDescription with whiteSpace
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceDescriptionForWhiteSpace() {
        try {
            resourceCategoryRelation.setResourceDescription(" ");
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_DESCRIPTION_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests setResourceDescription and getResourceDescription with valid
     * description.
     */
    @Test
    public void testSetGetResourceDescriptionForValidResourceDescription() {
        resourceCategoryRelation.setResourceDescription("Test");
        assertEquals("Test", resourceCategoryRelation.getResourceDescription());
    }

    /**
     * Tests setCategoryDescription with null
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetCategoryDescriptionForNull() {
        try {
            resourceCategoryRelation.setCategoryDescription(null);
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_DESCRIPTION_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests setCategoryDescription with empty
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetCategoryDescriptionForEmptyString() {
        try {
            resourceCategoryRelation.setCategoryDescription("");
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_DESCRIPTION_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests setCategoryDescription with whiteSpace
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetCategoryDescriptionForWhiteSpace() {
        try {
            resourceCategoryRelation.setCategoryDescription(" ");
        } catch (final IllegalArgumentException e) {
            assertEquals(CATEGORY_DESCRIPTION_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Tests setCategoryDescription and getCategoryDescription with valid
     * description.
     */
    @Test
    public void testSetCategoryDescriptionForValidCategoryDescription() {
        resourceCategoryRelation.setCategoryDescription("Test");
        assertEquals("Test", resourceCategoryRelation.getCategoryDescription());
    }

    /**
     * Expects {@link ResourceCategoryRelation#setAverageRating(double)} to
     * throw an {@link IllegalArgumentException} with a AVERAGE_RATING_NEGATIVE
     * message when the argument is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetAverageRatingForNegativeRating() {
        try {
            resourceCategoryRelation.setAverageRating(-1);
        } catch (final IllegalArgumentException e) {
            assertEquals(AVERAGE_RATING_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link ResourceCategoryRelation#setAverageRating(double)} to set
     * average rating to 0 when the argument is a zero value (boundary
     * condition).
     */
    @Test
    public void testSetAverageRatingForZeroRating() {
        resourceCategoryRelation.setAverageRating(0);
        assertEquals(0, resourceCategoryRelation.getAverageRating(), 0.01);
    }

    /**
     * Expects {@link ResourceCategoryRelation#setResourceOwner(String)} to
     * throw an {@link IllegalArgumentException} when the resourceOwner argument
     * is <code>null</code>
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceOwnerForNullResourceOwner() {
        try {
            resourceCategoryRelation.setResourceOwner(null);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_OWNER_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link ResourceCategoryRelation#setResourceOwner(String)} to
     * throw an {@link IllegalArgumentException} when the resourceOwner argument
     * is blank
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceOwnerForBlankResourceOwner() {
        try {
            resourceCategoryRelation.setResourceOwner(BLANK_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_OWNER_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link ResourceCategoryRelation#setResourceOwner(String)} to
     * throw an {@link IllegalArgumentException} when the resourceOwner argument
     * is empty
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceOwnerForEmptyResourceOwner() {
        try {
            resourceCategoryRelation.setResourceOwner(EMPTY_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_OWNER_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects the given valid resourceOwner to be set on the resource when
     * passed to {@link ResourceCategoryRelation#setResourceOwner(String)}.
     */
    @Test
    public void testSetResourceOwnerForValidValue() {
        resourceCategoryRelation.setResourceOwner(VALID_RESOURCE_OWNER);
        assertEquals(VALID_RESOURCE_OWNER, resourceCategoryRelation.getResourceOwner());
    }

    /**
     * Expects {@link ResourceCategoryRelation#setResourceOwner(String)} to
     * throw {@link IllegalArgumentException} when resourceOwner ID length is
     * greater than upper bound
     * ({@link ResourceCategoryRelationTest#RESOURCE_OWNER_LENGTH_UPPER_BOUND}).
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceOwnerForLengthGreaterThanUpperBound() {
        try {
            resourceCategoryRelation.setResourceOwner(
                    StringUtils.leftPad(VALID_RESOURCE_OWNER, RESOURCE_OWNER_LENGTH_UPPER_BOUND + 1, 'A'));
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_LENGTH_RESOURCE_OWNER_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects the given valid name with length equal to upper bound
     * ({@link ResourceCategoryRelationTest#RESOURCE_OWNER_LENGTH_UPPER_BOUND})
     * to be set on the resource when passed to
     * {@link ResourceCategoryRelation#setResourceOwner(String)}.
     */
    @Test
    public void testSetResourceOwnerForLengthEqualToUpperBound() {
        final String resourceOwner = StringUtils.leftPad(VALID_RESOURCE_OWNER, RESOURCE_OWNER_LENGTH_UPPER_BOUND, 'A');
        resourceCategoryRelation.setResourceOwner(resourceOwner);
        assertEquals(resourceOwner, resourceCategoryRelation.getResourceOwner());
    }

    /**
     * Expects the given valid name with length less than upper bound
     * ({@link ResourceCategoryRelationTest#RESOURCE_OWNER_LENGTH_UPPER_BOUND})
     * to be set on the resource when passed to
     * {@link ResourceCategoryRelation#setResourceOwner(String)}.
     */
    @Test
    public void testSetResourceOwnerForLengthLessThanUpperBound() {
        final String resourceOwner = StringUtils
                .leftPad(VALID_RESOURCE_OWNER, RESOURCE_OWNER_LENGTH_UPPER_BOUND - 1, 'A');
        resourceCategoryRelation.setResourceOwner(resourceOwner);
        assertEquals(resourceOwner, resourceCategoryRelation.getResourceOwner());
    }
}