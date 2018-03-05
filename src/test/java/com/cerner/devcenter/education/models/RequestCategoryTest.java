package com.cerner.devcenter.education.models;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * Tests the {@link RequestCategory} class.
 *
 * @author Vatsal Kesarwani (VK049896)
 */
public class RequestCategoryTest {

    private static final int VALID_ID = 3;
    private static final String VALID_CATEGORY_NAME = "Java";

    private static final int ZERO = 0;
    private static final int NEGATIVE = -1;
    private static final int CATEGORY_NAME_LENGTH_UPPER_BOUND = 100;
    private static final String NULL_STRING = null;
    private static final String EMPTY_STRING = "";
    private static final String BLANK_STRING = " ";

    private static final String INVALID_REQUEST_CATEGORY_ID_EXPECTED_MESSAGE = "Request category id is zero/negative.";
    private static final String INVALID_REQUEST_CATEGORY_NAME_EXPECTED_MESSAGE = "Request category name is null/empty/blank.";
    private static final String INVALID_LENGTH_REQUEST_CATEGORY_NAME_ERROR_MESSAGE = "Request category name is greater than 100 characters.";

    private final RequestCategory requestCategory = new RequestCategory();

    /**
     * Expects the set id to be retrieved from the requested category when
     * {@link RequestCategory#getId()} is called.
     */
    @Test
    public void testGetId() {
        assertEquals(ZERO, requestCategory.getId());
    }

    /**
     * Expects {@link RequestCategory#setId(int)} to throw
     * {@link IllegalArgumentException} when id is zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetId_ZeroId() {
        try {
            requestCategory.setId(ZERO);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_REQUEST_CATEGORY_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link RequestCategory#setId(int)} to throw
     * {@link IllegalArgumentException} when id is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetId_NegativeId() {
        try {
            requestCategory.setId(NEGATIVE);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_REQUEST_CATEGORY_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects the given valid id to be set on the requested category when
     * passed to {@link RequestCategory#setId(int)}.
     */
    @Test
    public void testSetId() {
        requestCategory.setId(VALID_ID);
        assertEquals(VALID_ID, requestCategory.getId());
    }

    /**
     * Expects the set name to be retrieved from the requested category when
     * {@link RequestCategory#getName()} is called.
     */
    @Test
    public void testGetName() {
        assertEquals(NULL_STRING, requestCategory.getName());
    }

    /**
     * Expects {@link RequestCategory#setName(String)} to throw
     * {@link IllegalArgumentException} when name is <code>null</code>.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetName_NullName() {
        try {
            requestCategory.setName(NULL_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_REQUEST_CATEGORY_NAME_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link RequestCategory#setName(String)} to throw
     * {@link IllegalArgumentException} when name is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetName_EmptyName() {
        try {
            requestCategory.setName(EMPTY_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_REQUEST_CATEGORY_NAME_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link RequestCategory#setName(String)} to throw
     * {@link IllegalArgumentException} when name is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetName_BlankName() {
        try {
            requestCategory.setName(BLANK_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_REQUEST_CATEGORY_NAME_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link RequestCategory#setName(String)} to throw
     * {@link IllegalArgumentException} when name length is greater than upper
     * bound ({@link RequestCategoryTest#CATEGORY_NAME_LENGTH_UPPER_BOUND}).
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetName_NameLengthGreaterThanUpperBound() {
        try {
            requestCategory
                    .setName(StringUtils.leftPad(VALID_CATEGORY_NAME, CATEGORY_NAME_LENGTH_UPPER_BOUND + 1, 'A'));
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_LENGTH_REQUEST_CATEGORY_NAME_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects the given valid name with length equal to upper bound
     * ({@link RequestCategoryTest#CATEGORY_NAME_LENGTH_UPPER_BOUND}) to be set
     * on the requested category when passed to
     * {@link RequestCategory#setName(String)}.
     */
    @Test
    public void testSetName_NameLengthEqualToUpperBound() {
        final String name = StringUtils.leftPad(VALID_CATEGORY_NAME, CATEGORY_NAME_LENGTH_UPPER_BOUND, 'A');
        requestCategory.setName(name);
        assertEquals(name, requestCategory.getName());
    }

    /**
     * Expects the given valid name with length less than upper bound
     * ({@link RequestCategoryTest#CATEGORY_NAME_LENGTH_UPPER_BOUND}) to be set
     * on the requested category when passed to
     * {@link RequestCategory#setName(String)}.
     */
    @Test
    public void testSetName_NameLengthLessThanUpperBound() {
        final String name = StringUtils.leftPad(VALID_CATEGORY_NAME, CATEGORY_NAME_LENGTH_UPPER_BOUND - 1, 'A');
        requestCategory.setName(name);
        assertEquals(name, requestCategory.getName());
    }

    /**
     * Expects the given valid name to be set on the requested category when
     * passed to {@link RequestCategory#setName(String)}.
     */
    @Test
    public void testSetName() {
        requestCategory.setName(VALID_CATEGORY_NAME);
        assertEquals(VALID_CATEGORY_NAME, requestCategory.getName());
    }

    /**
     * Expects the set isApproved value to be retrieved from the requested
     * category when {@link RequestCategory#isApproved()} is called.
     */
    @Test
    public void testIsApproved() {
        assertEquals(Boolean.FALSE, requestCategory.isApproved());
    }

    /**
     * Expects the given valid isApproved value to be set on the requested
     * category when passed to {@link RequestCategory#setApproved(boolean)}.
     */
    @Test
    public void testSetApproved() {
        requestCategory.setApproved(Boolean.TRUE);
        assertEquals(Boolean.TRUE, requestCategory.isApproved());
    }
}