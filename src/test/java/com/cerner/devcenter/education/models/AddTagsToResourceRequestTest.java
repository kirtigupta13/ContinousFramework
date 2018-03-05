package com.cerner.devcenter.education.models;

import static com.cerner.devcenter.education.utils.TagTestUtil.getTagNameList;
import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.cerner.devcenter.education.utils.Constants;

/**
 * Tests the functionality of the {@link AddTagsToResourceRequest} class.
 *
 * @author Amos Bailey (AB032627)
 * @author Navya Rangeneni(NR046827)
 */
@RunWith(MockitoJUnitRunner.class)
public class AddTagsToResourceRequestTest {

    private static final int VALID_RESOURCE_ID = 1;
    private static final int NEGATIVE_RESOURCE_ID = -1;
    private static final String EMPTY_STRING = "";
    private static final String BLANK_STRING = "     ";

    private AddTagsToResourceRequest resourceTagsRequestWrapper;
    private List<String> tagNameList;

    @Before
    public void setup() throws SQLException {
        resourceTagsRequestWrapper = new AddTagsToResourceRequest();
        resourceTagsRequestWrapper.setTagNameList(getTagNameList(5));
        resourceTagsRequestWrapper.setResourceID(VALID_RESOURCE_ID);
        tagNameList = getTagNameList(5);
    }

    /**
     * Expects {@link AddTagsToResourceRequest#setResourceID(int)} to throw
     * {@link IllegalArgumentException} when id is Negative.
     */

    @Test(expected = IllegalArgumentException.class)
    public void testResourceIDSetterNegativeID() {
        try {
            resourceTagsRequestWrapper.setResourceID(NEGATIVE_RESOURCE_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(Constants.RESOURCE_ID_MUST_BE_POSITIVE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link AddTagsToResourceRequest#setResourceID(int)} to throw
     * {@link IllegalArgumentException} when id is zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testResourceIDSetterZeroID() {
        try {
            resourceTagsRequestWrapper.setResourceID(0);
        } catch (final IllegalArgumentException e) {
            assertEquals(Constants.RESOURCE_ID_MUST_BE_POSITIVE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects the given valid id to be set on the resource request when passed
     * to {@link AddTagsToResourceRequest#setResourceID(int)}.
     */
    @Test
    public void testResourceIDSetterValidInput() {
        final int newResourceID = VALID_RESOURCE_ID + 1;
        resourceTagsRequestWrapper.setResourceID(newResourceID);
        assertEquals(newResourceID, resourceTagsRequestWrapper.getResourceID());
    }

    /**
     * Expects to get valid id when
     * {@link AddTagsToResourceRequest#getResourceID()} is called
     */
    @Test
    public void testResourceIDGetter() {
        assertEquals(VALID_RESOURCE_ID, resourceTagsRequestWrapper.getResourceID());
    }

    /**
     * Expects {@link AddTagsToResourceRequest#setTagNameList(List)} to throw
     * {@link IllegalArgumentException} when tag name is null list.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testTagNameListSetterNullList() {
        try {
            resourceTagsRequestWrapper.setTagNameList(null);
        } catch (final IllegalArgumentException e) {
            assertEquals(Constants.TAG_LIST_NULL, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link AddTagsToResourceRequest#setTagNameList(List)} to throw
     * {@link IllegalArgumentException} when tag name list has a null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testTagNameListSetterListContainsNull() {
        try {
            tagNameList.add(null);
            resourceTagsRequestWrapper.setTagNameList(tagNameList);
        } catch (final IllegalArgumentException e) {
            assertEquals(Constants.TAG_NAME_CANNOT_BE_NULL, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link AddTagsToResourceRequest#setTagNameList(List)} to throw
     * {@link IllegalArgumentException} when tag name list has a blank string.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testTagNameListSetterListContainsBlankString() {
        try {
            tagNameList.add(BLANK_STRING);
            resourceTagsRequestWrapper.setTagNameList(tagNameList);
        } catch (final IllegalArgumentException e) {
            assertEquals(Constants.TAG_NAME_CANNOT_BE_BLANK_OR_EMPTY, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link AddTagsToResourceRequest#setTagNameList(List)} to throw
     * {@link IllegalArgumentException} when tag name list has a empty string.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testTagNameListSetterListContainsEmptyString() {
        try {
            tagNameList.add(EMPTY_STRING);
            resourceTagsRequestWrapper.setTagNameList(tagNameList);
        } catch (final IllegalArgumentException e) {
            assertEquals(Constants.TAG_NAME_CANNOT_BE_BLANK_OR_EMPTY, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects the given valid tag name list to be set when passed to
     * {@link AddTagsToResourceRequest#setTagNameList(List)}.
     */
    @Test
    public void testTagNameListSetterValidInput() {
        resourceTagsRequestWrapper.setTagNameList(tagNameList);
        assertEquals(tagNameList, resourceTagsRequestWrapper.getTagNameList());
    }

    /**
     * Expects to get valid tag name list when
     * {@link AddTagsToResourceRequest#getTagNameList()} is called
     */
    @Test
    public void testTagNameListGetter() {
        assertEquals(getTagNameList(5), resourceTagsRequestWrapper.getTagNameList());
    }
}
