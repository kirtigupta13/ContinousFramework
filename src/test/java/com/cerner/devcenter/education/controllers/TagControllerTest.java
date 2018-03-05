package com.cerner.devcenter.education.controllers;

import static com.cerner.devcenter.education.utils.TagTestUtil.getTagNameList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.managers.TagManager;
import com.cerner.devcenter.education.models.AddTagsToResourceRequest;
import com.cerner.devcenter.education.models.Tag;
import com.cerner.devcenter.education.utils.AuthenticationStatusUtil;
import com.cerner.devcenter.education.utils.Constants;

/**
 * This class tests the functionality of the Tag Controller.
 * 
 * @author Amos Bailey (AB032627)
 */
@RunWith(MockitoJUnitRunner.class)
public class TagControllerTest {

    @InjectMocks
    private TagController tagController = new TagController();
    @Mock
    private TagManager tagManager;
    @Mock
    private AuthenticationStatusUtil status;
    @Mock
    private AddTagsToResourceRequest resourceTagsRequestWrapper;
    @Mock
    private ManagerException managerException;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static ResourceBundle i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

    private static final String VALID_TAG_NAME = "Valid Tag Names";
    private static final int VALID_TAG_ID = 7;
    private static final String VALID_TAG_SEARCH = "Valid";
    private static final int VALID_RESOURCE_ID = 1;
    private static final String BLANK_STRING = "      ";
    private static final String EMPTY_STRING = "";
    private static final String NULL_STRING = null;

    private Set<Tag> tagSet;
    private List<Tag> listOfTags;

    @Before
    public void setUp() throws Exception {
        tagSet = new HashSet<Tag>();
        listOfTags = new ArrayList<Tag>();
        when(status.isLoggedIn()).thenReturn(true);
        when(resourceTagsRequestWrapper.getResourceID()).thenReturn(VALID_RESOURCE_ID);
        when(resourceTagsRequestWrapper.getTagNameList()).thenReturn(getTagNameList(5));
    }

    /**
     * Tests {@link TagController#tagAutocomplete(String)} with an empty string
     * and expects an {@link IllegalArgumentException}.
     */
    @Test
    public void testTagAutoCompleteForEmptyString() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(i18nBundle.getString(Constants.SEARCH_INVALID_I18N));
        tagController.tagAutocomplete(EMPTY_STRING);
    }

    /**
     * Tests {@link TagController#tagAutocomplete(String)} with a null string
     * and expects an {@link IllegalArgumentException}.
     */
    @Test
    public void testTagAutoCompleteForNullString() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(i18nBundle.getString(Constants.SEARCH_INVALID_I18N));
        tagController.tagAutocomplete(NULL_STRING);
    }

    /**
     * Tests {@link TagController#tagAutocomplete(String)} with a blank string
     * (one containing only whitespace characters) and expects an
     * {@link IllegalArgumentException}.
     */
    @Test
    public void testTagAutoCompleteForBlankString() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(i18nBundle.getString(Constants.SEARCH_INVALID_I18N));
        tagController.tagAutocomplete(BLANK_STRING);
    }

    /**
     * Tests {@link TagController#tagAutocomplete(String)} when it returns 0
     * results.
     */
    @Test
    public void testTagAutoCompleteSearchWhenReturnsZeroResults() {
        when(tagManager.getSearchedTags(VALID_TAG_SEARCH)).thenReturn(Collections.<Tag>emptyList());
        assertEquals(0, tagController.tagAutocomplete(VALID_TAG_SEARCH).size());
    }

    /**
     * Tests {@link TagController#tagAutocomplete(String)} when more than 10
     * matching results are returned from the database.
     */
    @Test
    public void testTagAutoCompleteSearchWhenReturnsOverMaxAutoFillSize() {
        for (int i = 0; i < Constants.AUTOFILL_SIZE + 1; i++) {
            listOfTags.add(new Tag(VALID_TAG_ID + i, VALID_TAG_NAME));
        }

        when(tagManager.getSearchedTags(VALID_TAG_SEARCH.toLowerCase())).thenReturn(listOfTags);
        assertEquals(Constants.AUTOFILL_SIZE, tagController.tagAutocomplete(VALID_TAG_SEARCH).size());
    }

    /**
     * Tests {@link TagController#tagAutocomplete(String)} when exactly 10
     * matching results are returned from the database.
     * 
     */
    @Test
    public void testTagAutoCompleteSearchWhenReturnsExactlyMaxAutoFillSize() {
        for (int i = 0; i < Constants.AUTOFILL_SIZE; i++) {
            listOfTags.add(new Tag(VALID_TAG_ID + i, VALID_TAG_NAME));
        }

        when(tagManager.getSearchedTags(VALID_TAG_SEARCH.toLowerCase())).thenReturn(listOfTags);
        assertEquals(Constants.AUTOFILL_SIZE, tagController.tagAutocomplete(VALID_TAG_SEARCH).size());
    }

    /**
     * Tests {@link TagController#tagAutocomplete(String)} when fewer than 10
     * matching results are returned from the database.
     * 
     */
    @Test
    public void testTagAutoCompleteSearchWhenReturnsUnderMaxAutoFillSize() {
        int tagListSize = Constants.AUTOFILL_SIZE - 1;
        for (int i = 0; i < tagListSize; i++) {
            listOfTags.add(new Tag(VALID_TAG_ID + i, VALID_TAG_NAME));
        }

        when(tagManager.getSearchedTags(VALID_TAG_SEARCH.toLowerCase())).thenReturn(listOfTags);
        assertEquals(tagListSize, tagController.tagAutocomplete(VALID_TAG_SEARCH).size());
    }

    /**
     * Tests {@link TagController#addTagsToResource(AddTagsToResourceRequest)}
     * when the tag list is empty.
     */
    @Test
    public void testAddTagsToResourceEmptyList() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_LIST_EMPTY);
        when(resourceTagsRequestWrapper.getTagNameList()).thenReturn(Collections.<String>emptyList());
        tagController.addTagsToResource(resourceTagsRequestWrapper);
    }

    /**
     * Tests that
     * {@link TagController#addTagsToResource(AddTagsToResourceRequest)} fails
     * (returns false) when a {@link ManagerException} occurs while trying to
     * add to the database.
     */
    @Test
    public void testAddTagsToResourceExceptionWhenAddingToDB() {
        doThrow(managerException).when(tagManager).addTagsToResource(Matchers.anySetOf(String.class), anyInt());
        assertFalse(tagController.addTagsToResource(resourceTagsRequestWrapper));
    }

    /**
     * Tests that
     * {@link TagController#addTagsToResource(AddTagsToResourceRequest)} returns
     * true when given valid input and when no errors occur.
     */
    @Test
    public void testAddTagsToResourceValidInputNoErrors() {
        assertTrue(tagController.addTagsToResource(resourceTagsRequestWrapper));
    }
}
