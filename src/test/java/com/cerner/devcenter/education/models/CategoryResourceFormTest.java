package com.cerner.devcenter.education.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Contains test cases that test the functions of the CategoryResourceForm class
 * ( {@link com.cerner.devcenter.education.models.CategoryResourceForm})
 * 
 * @author Gunjan Kaphle (GK045931)
 * @author Navya Rangeneni (NR046827)
 */
public class CategoryResourceFormTest {

    private static final int VALID_SELECTED_CATEGORY_ID = 3;
    private static final int VALID_SELECTED_RESOURCE_TYPE_ID = 5;
    private static final int NEGATIVE_ID = -4;
    private static final int ZERO_ID = 0;
    private static final String INVALID_SELECTED_CATEGORY_ID_ERROR_MESSAGE = "Category ID should be greater than zero";
    private static final String INVALID_SELECTED_RESOURCE_TYPE_ID_ERROR_MESSAGE = "Resource Type ID should be greater than zero";

    private CategoryResourceForm categoryResourceForm = new CategoryResourceForm();

    /**
     * This tests the constructor for {@link CategoryResourceForm} with valid
     * inputs.
     */
    @Test
    public void testMultipleInputConstructorSuccess() {
        final CategoryResourceForm categoryResourceForm = new CategoryResourceForm(VALID_SELECTED_CATEGORY_ID,
                VALID_SELECTED_RESOURCE_TYPE_ID);
        assertEquals(VALID_SELECTED_CATEGORY_ID, categoryResourceForm.getSelectedCategoryID());
        assertEquals(VALID_SELECTED_RESOURCE_TYPE_ID, categoryResourceForm.getSelectedResourceTypeID());
    }

    /**
     * This tests the constructor for {@link CategoryResourceForm} with negative
     * category ID inputs.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMultipleInputConstructorNegativeCategoryId() {
        try {
            new CategoryResourceForm(NEGATIVE_ID, VALID_SELECTED_RESOURCE_TYPE_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_SELECTED_CATEGORY_ID_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * This tests the constructor for {@link CategoryResourceForm} with zero
     * category ID inputs.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMultipleInputConstructorZeroCategoryId() {
        try {
            new CategoryResourceForm(ZERO_ID, VALID_SELECTED_RESOURCE_TYPE_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_SELECTED_CATEGORY_ID_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * This tests the constructor for {@link CategoryResourceForm} with negative
     * resource ID inputs.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMultipleInputConstructorNegativeResourceTypeId() {
        try {
            new CategoryResourceForm(VALID_SELECTED_CATEGORY_ID, NEGATIVE_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_SELECTED_RESOURCE_TYPE_ID_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * This tests the getter and setter for selected category id.
     */
    @Test
    public void testGetterSetterSelectedCategoryID() {
        categoryResourceForm.setSelectedCategoryID(VALID_SELECTED_CATEGORY_ID);
        assertEquals(VALID_SELECTED_CATEGORY_ID, categoryResourceForm.getSelectedCategoryID());
    }

    /**
     * This tests the getter and setter for selected resource type id.
     */
    @Test
    public void testGetterSetterSelectedResourceTypeID() {
        categoryResourceForm.setSelectedResourceTypeID(VALID_SELECTED_RESOURCE_TYPE_ID);
        assertEquals(VALID_SELECTED_RESOURCE_TYPE_ID, categoryResourceForm.getSelectedResourceTypeID());
    }

    /**
     * This test expects {@link IllegalArgumentException} when negative category
     * id is passed in.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeCategoryID() {
        try {
            categoryResourceForm.setSelectedCategoryID(NEGATIVE_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_SELECTED_CATEGORY_ID_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * This test expects {@link IllegalArgumentException} when zero category id
     * is passed in.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testZeroCategoryID() {
        try {
            categoryResourceForm.setSelectedCategoryID(ZERO_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_SELECTED_CATEGORY_ID_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * This test expects {@link IllegalArgumentException} when zero resource
     * type ID is passed in.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeResourceTypeID() {
        try {
            categoryResourceForm.setSelectedResourceTypeID(NEGATIVE_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_SELECTED_RESOURCE_TYPE_ID_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * This test expects {@link IllegalArgumentException} when the
     * {@link CategoryResourceForm#checkParametersValidity(int, int)} is called
     * by passing negative category id.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckParametersValidityWhenNegativeCategoryID() {
        try {
            categoryResourceForm.checkParametersValidity(NEGATIVE_ID, VALID_SELECTED_RESOURCE_TYPE_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_SELECTED_CATEGORY_ID_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * This test expects {@link IllegalArgumentException} when the
     * {@link CategoryResourceForm#checkParametersValidity(int, int)} is called
     * by passing negative resource type id.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckParametersValidityWhenNegativeResourceTypeId() {
        try {
            categoryResourceForm.checkParametersValidity(VALID_SELECTED_CATEGORY_ID, NEGATIVE_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_SELECTED_RESOURCE_TYPE_ID_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }
}
