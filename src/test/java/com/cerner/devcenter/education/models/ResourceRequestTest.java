package com.cerner.devcenter.education.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests the {@link ResourceRequest} class.
 *
 * @author Navya Rangeneni (NR046827)
 * @author Vatsal Kesarwani (VK049896)
 */
public class ResourceRequestTest {

    private static final int VALID_ID = 3;
    private static final String VALID_USER_ID = "AA012345";
    private static final String VALID_CATEGORY_NAME = "Java";
    private static final String VALID_RESOURCE_NAME = "YouTube";
    private static final String NULL_STRING = null;
    private static final String EMPTY_STRING = "";
    private static final String BLANK_STRING = " ";
    private static final int ZERO = 0;
    private static final int NEGATIVE = -1;
    private static final String INVALID_USER_ID_EXPECTED_MSG = "User id is null/empty/blank.";
    private static final String INVALID_RESOURCE_REQUEST_ID_EXPECTED_MSG = "Resource request id is zero/negative.";
    private static final String INVALID_CATEGORY_NAME_EXPECTED_MSG = "Category name is null/empty/blank.";
    private static final String INVALID_RESOURCE_NAME_EXPECTED_MSG = "Resource name is null/empty/blank.";

    private final ResourceRequest resourceRequest = new ResourceRequest();

    /**
     * Expects the set id to be retrieved from the resource request when
     * {@link ResourceRequest#getId()} is called.
     */
    @Test
    public void testGetId() {
        assertEquals(ZERO, resourceRequest.getId());
    }

    /**
     * Expects {@link ResourceRequest#setId(int)} to throw
     * {@link IllegalArgumentException} when id is zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetId_ZeroId() {
        try {
            resourceRequest.setId(ZERO);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_RESOURCE_REQUEST_ID_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link ResourceRequest#setId(int)} to throw
     * {@link IllegalArgumentException} when id is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetId_NegativeId() {
        try {
            resourceRequest.setId(NEGATIVE);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_RESOURCE_REQUEST_ID_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects the given valid id to be set on the resource request when passed
     * to {@link ResourceRequest#setId(int)}.
     */
    @Test
    public void testSetId() {
        resourceRequest.setId(VALID_ID);
        assertEquals(VALID_ID, resourceRequest.getId());
    }

    /**
     * Expects the set userId to be retrieved from the resource request when
     * {@link ResourceRequest#getUserId()} is called.
     */
    @Test
    public void testGetUserId() {
        assertEquals(NULL_STRING, resourceRequest.getUserId());
    }

    /**
     * Expects {@link ResourceRequest#setUserId(String)} to throw
     * {@link IllegalArgumentException} when userId is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetUserId_NullUserId() {
        try {
            resourceRequest.setUserId(NULL_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link ResourceRequest#setUserId(String)} to throw
     * {@link IllegalArgumentException} when userId is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetUserId_EmptyUserId() {
        try {
            resourceRequest.setUserId(EMPTY_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link ResourceRequest#setUserId(String)} to throw
     * {@link IllegalArgumentException} when userId is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetUserId_BlankUserId() {
        try {
            resourceRequest.setUserId(BLANK_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects the given valid userId to be set on the resource request when
     * passed to {@link ResourceRequest#setUserId(String)}.
     */
    @Test
    public void testSetUserId() {
        resourceRequest.setUserId(VALID_USER_ID);
        assertEquals(VALID_USER_ID, resourceRequest.getUserId());
    }

    /**
     * Expects the set categoryName to be retrieved from the resource request
     * when {@link ResourceRequest#getCategoryName()} is called.
     */
    @Test
    public void testGetCategoryName() {
        assertEquals(NULL_STRING, resourceRequest.getCategoryName());
    }

    /**
     * Expects {@link ResourceRequest#setCategoryName(String)} to throw
     * {@link IllegalArgumentException} when categoryName is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetCategoryName_NullCategoryName() {
        try {
            resourceRequest.setCategoryName(NULL_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_CATEGORY_NAME_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link ResourceRequest#setCategoryName(String)} to throw
     * {@link IllegalArgumentException} when categoryName is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetCategoryName_EmptyCategoryName() {
        try {
            resourceRequest.setCategoryName(EMPTY_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_CATEGORY_NAME_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link ResourceRequest#setCategoryName(String)} to throw
     * {@link IllegalArgumentException} when categoryName is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetCategoryName_BlankCategoryName() {
        try {
            resourceRequest.setCategoryName(BLANK_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_CATEGORY_NAME_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects the given valid categoryName to be set on the resource request
     * when passed to {@link ResourceRequest#setCategoryName(String)}.
     */
    @Test
    public void testSetCategoryName() {
        resourceRequest.setCategoryName(VALID_CATEGORY_NAME);
        assertEquals(VALID_CATEGORY_NAME, resourceRequest.getCategoryName());
    }

    /**
     * Expects the set resourceName to be retrieved from the resource request
     * when {@link ResourceRequest#getResourceName()} is called.
     */
    @Test
    public void testGetResourceName() {
        assertEquals(NULL_STRING, resourceRequest.getResourceName());
    }

    /**
     * Expects {@link ResourceRequest#setResourceName(String)} to throw
     * {@link IllegalArgumentException} when resourceName is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceName_NullResourceName() {
        try {
            resourceRequest.setResourceName(NULL_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_RESOURCE_NAME_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link ResourceRequest#setResourceName(String)} to throw
     * {@link IllegalArgumentException} when resourceName is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceName_EmptyResourceName() {
        try {
            resourceRequest.setResourceName(EMPTY_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_RESOURCE_NAME_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link ResourceRequest#setResourceName(String)} to throw
     * {@link IllegalArgumentException} when resourceName is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetResourceName_BlankResourceName() {
        try {
            resourceRequest.setResourceName(BLANK_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_RESOURCE_NAME_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects the given valid resourceName to be set on the resource request
     * when passed to {@link ResourceRequest#setResourceName(String)}.
     */
    @Test
    public void testSetResourceName() {
        resourceRequest.setResourceName(VALID_RESOURCE_NAME);
        assertEquals(VALID_RESOURCE_NAME, resourceRequest.getResourceName());
    }

    /**
     * Expects the set isApproved value to be retrieved from the resource
     * request when {@link ResourceRequest#isApproved()} is called.
     */
    @Test
    public void testIsApproved() {
        assertEquals(Boolean.FALSE, resourceRequest.isApproved());
    }

    /**
     * Expects the given valid isApproved value to be set on the resource
     * request when passed to {@link ResourceRequest#setApproved(boolean)}.
     */
    @Test
    public void testSetApproved() {
        resourceRequest.setApproved(Boolean.TRUE);
        assertEquals(Boolean.TRUE, resourceRequest.isApproved());
    }
}