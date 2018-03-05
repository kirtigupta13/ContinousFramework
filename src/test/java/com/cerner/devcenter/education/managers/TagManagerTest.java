package com.cerner.devcenter.education.managers;

import static com.cerner.devcenter.education.utils.TagTestUtil.createTestTags;
import static com.cerner.devcenter.education.utils.TagTestUtil.getTagNameList;
import static com.cerner.devcenter.education.utils.TagTestUtil.getTagNameSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.admin.ResourceTagRelationDAO;
import com.cerner.devcenter.education.admin.TagDAO;
import com.cerner.devcenter.education.admin.TagDAOImpl;
import com.cerner.devcenter.education.models.Tag;
import com.cerner.devcenter.education.utils.Constants;

/**
 * Tests the functionalities of {@link TagManager} class.
 * 
 * @author Abhi Purella (AP045635)
 * @author Amos Bailey (AB032627)
 */
@RunWith(MockitoJUnitRunner.class)
public class TagManagerTest {
    @InjectMocks
    private TagManager mockTagManager;
    @Mock
    private TagDAO mockTagDAO;
    @Mock
    private Tag mockTag;
    @Mock
    private DAOException daoException;
    @Mock
    private ResourceTagRelationDAO resourceTagRelationDAO;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Captor
    private ArgumentCaptor<Collection> tagCollectionCaptor;

    private static ResourceBundle i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());
    private static final int VALID_TAG_ID = 5;
    private static final int DEFAULT_TAG_LIST_SIZE = 5;
    private static final int VALID_RESOURCE_ID = 1;
    private static final String VALID_TAG_NAME = "commits";
    private static final String VALID_SEARCH_STRING = "comm";
    private static final String EMPTY_STRING = "";
    private static final String NULL_STRING = null;
    private static final String BLANK_STRING = "     ";
    private static final int TAG_ID_SMALLER_THAN_ZERO = -8;
    private static final String ERROR_RETRIEVING_SEARCHED_TAGS_MESSAGE = "Error retrieving searched tags from the database";
    private static final String ERROR_RETRIEVING_ALL_TAGS_MESSAGE = "Error retrieving tags from the data source";
    private static final String OTHER_VALID_TAG_NAME = "DevCon 2016";

    private Tag tag;
    private List<Tag> listOfTags;
    private List<String> tagNameList;
    private Set<String> tagNameSet;

    @Before
    public void setup() throws DAOException {
        tag = new Tag(VALID_TAG_ID, VALID_TAG_NAME);
        listOfTags = createTestTags(DEFAULT_TAG_LIST_SIZE);
        tagNameList = getTagNameList(DEFAULT_TAG_LIST_SIZE);
        tagNameSet = getTagNameSet(DEFAULT_TAG_LIST_SIZE);
    }

    /**
     * Tests {@link TagManager#getAllTags()} functionality, expects
     * {@link ManagerException} when {@link TagDAO#getAllTags()} throws
     * {@link DAOException}.
     */
    @Test
    public void testGetAllTagsThrowsManagerException() throws DAOException {
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage(ERROR_RETRIEVING_ALL_TAGS_MESSAGE);
        when(mockTagDAO.getAllTags()).thenThrow(new DAOException());
        mockTagManager.getAllTags();
    }

    /**
     * Tests {@link TagManager#getAllTags()} when {@link TagDAO#getAllTags()}
     * functionality returns empty.
     */
    @Test
    public void testGetAllTagsTypesReturnsEmpty() throws DAOException {
        when(mockTagDAO.getAllTags()).thenReturn(new ArrayList<Tag>());
        assertEquals(mockTagManager.getAllTags().size(), 0);
    }

    /**
     * Tests {@link TagManager#getAllTags()} functionality is called by
     * {@link TagManager}.
     */
    @Test
    public void testGetAllTags() throws DAOException {
        when(mockTagDAO.getAllTags()).thenReturn(listOfTags);
        assertEquals(createTestTags(DEFAULT_TAG_LIST_SIZE), mockTagManager.getAllTags());
    }

    /**
     * Tests {@link TagManager#getSearchedTags(String)}. Expects
     * {@link IllegalArgumentException} when an empty string is passed.
     */
    @Test
    public void testGetSearchedTagsEmptyString() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(i18nBundle.getString(Constants.SEARCH_INVALID_I18N));
        mockTagManager.getSearchedTags(EMPTY_STRING);
    }

    /**
     * Tests {@link TagManager#getSearchedTags(String)}. Expects
     * {@link IllegalArgumentException} when a blank string is passed (a string
     * containing only whitespace characters).
     */
    @Test
    public void testGetSearchedTagsBlankString() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(i18nBundle.getString(Constants.SEARCH_INVALID_I18N));
        mockTagManager.getSearchedTags(BLANK_STRING);
    }

    /**
     * Tests {@link TagManager#getSearchedTags(String)}. Expects
     * {@link IllegalArgumentException} when a null string is passed.
     */
    @Test
    public void testGetSearchedTagsNullString() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(i18nBundle.getString(Constants.SEARCH_INVALID_I18N));
        mockTagManager.getSearchedTags(NULL_STRING);
    }

    /**
     * Tests {@link TagManager#getSearchedTags(String)}. Expects
     * {@link ManagerException} when {@link TagDAOImpl} throws a
     * {@link DAOException}.
     */
    @Test
    public void testGetSearchedTagsThrowsManagerException() throws DAOException {
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage(ERROR_RETRIEVING_SEARCHED_TAGS_MESSAGE);
        when(mockTagDAO.getSearchedTags(VALID_SEARCH_STRING)).thenThrow(DAOException.class);
        mockTagManager.getSearchedTags(VALID_SEARCH_STRING);
    }

    /**
     * Tests {@link TagManager#getSearchedTags(String)}. When a valid search is
     * entered, this ensures that the expected {@link List} of {@link Tag}
     * objects is returned.
     */
    @Test
    public void testGetSearchedTagsValidSearch() throws DAOException {
        when(mockTagDAO.getSearchedTags(VALID_SEARCH_STRING)).thenReturn(listOfTags);
        List<Tag> returnedTagList = mockTagManager.getSearchedTags(VALID_SEARCH_STRING);
        assertEquals(listOfTags, returnedTagList);
    }

    /**
     * Tests {@link TagManager#addTag(String)} with a null string. Expects an
     * {@link IllegalArgumentException}.
     */
    @Test
    public void testAddTagNullTagName() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_NULL);
        mockTagManager.addTag(null);
    }

    /**
     * Tests {@link TagManager#addTag(String)} with a blank string. Expects an
     * {@link IllegalArgumentException}.
     */
    @Test
    public void testAddTagBlankTagName() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_BLANK_OR_EMPTY);
        mockTagManager.addTag(BLANK_STRING);
    }

    /**
     * Tests {@link TagManager#addTag(String)} with an empty string. Expects an
     * {@link IllegalArgumentException}.
     */
    @Test
    public void testAddTagEmptyTagName() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_BLANK_OR_EMPTY);
        mockTagManager.addTag(EMPTY_STRING);
    }

    /**
     * Tests {@link TagManager#addTag(String)} when a DAOException is thrown
     * while querying for same-name tags. Expects a {@link ManagerException}
     */
    @Test
    public void testAddTagCausesDAOException() throws DAOException {
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage(Constants.ERROR_ADDING_TAG_TO_DB);
        doThrow(daoException).when(mockTagDAO).addTagToDB(VALID_TAG_NAME);
        mockTagManager.addTag(VALID_TAG_NAME);
    }

    /**
     * Tests {@link TagManager#addTag(String)} with valid input and when no
     * errors occur.
     */
    @Test
    public void testAddTagValidInputNoErrors() throws DAOException {
        mockTagManager.addTag(VALID_TAG_NAME);
    }

    /**
     * Tests {@link TagManager#addTagsToResource(Set, int)} with a null tag set.
     * Expects {@link IllegalArgumentException}
     */
    @Test
    public void testAddTagsToResourceNullTagList() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_LIST_NULL);
        mockTagManager.addTagsToResource(null, VALID_RESOURCE_ID);
    }

    /**
     * Tests {@link TagManager#addMultipleTags(Collection)} with an empty list.
     * Expects an {@link IllegalArgumentException}.
     */
    @Test
    public void testAddMultipleTagsEmptyList() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_LIST_EMPTY);
        mockTagManager.addMultipleTags(Collections.<String>emptyList());
    }

    /**
     * Tests {@link TagManager#addMultipleTags(Collection)} with a null list.
     * Expects an {@link IllegalArgumentException}.
     */
    @Test
    public void testAddMultipleTagsNullList() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_LIST_NULL);
        mockTagManager.addMultipleTags(null);
    }

    /**
     * Tests {@link TagManager#addMultipleTags(Collection)} with a list
     * containing null. Expects an {@link IllegalArgumentException}.
     */
    @Test
    public void testAddMultipleTagsListContainsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_NULL);
        tagNameList.add(null);
        mockTagManager.addMultipleTags(tagNameList);
    }

    /**
     * Tests {@link TagManager#addMultipleTags(Collection)} with a list
     * containing the empty string. Expects an {@link IllegalArgumentException}.
     */
    @Test
    public void testAddMultipleTagsListContainsEmptyString() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_BLANK_OR_EMPTY);
        tagNameList.add(EMPTY_STRING);
        mockTagManager.addMultipleTags(tagNameList);
    }

    /**
     * Tests {@link TagManager#addMultipleTags(Collection)} with a list
     * containing a blank string. Expects an {@link IllegalArgumentException}.
     */
    @Test
    public void testAddMultipleTagsListContainsBlankString() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_BLANK_OR_EMPTY);
        tagNameList.add(BLANK_STRING);
        mockTagManager.addMultipleTags(tagNameList);
    }

    /**
     * Tests the functionality of {@link TagManager#addMultipleTags(Collection)}
     * when the query throws a {@link DAOException}. Expects a
     * {@link ManagerException} to be thrown.
     */
    @Test
    public void testAddMultipleTagsQueryCausesException() throws DAOException {
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage(Constants.ERROR_ADDING_TAG_TO_DB);
        doThrow(daoException).when(mockTagDAO).batchAddTags(any(List.class));
        mockTagManager.addMultipleTags(tagNameList);
    }

    /**
     * Tests {@link TagManager#addTagsToResource(Set, int)} with a set
     * containing a null string. Expects {@link IllegalArgumentException}
     */
    @Test
    public void testAddTagsToResourceNullItemInTagList() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_NULL);
        Set<String> tagSet = getTagNameSet(5);
        tagSet.add(null);
        mockTagManager.addTagsToResource(tagSet, VALID_RESOURCE_ID);
    }

    /**
     * Tests {@link TagManager#addTagsToResource(Set, int)} with a set
     * containing a blank string. Expects {@link IllegalArgumentException}
     */
    @Test
    public void testAddTagsToResourceBlankItemInTagList() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_BLANK_OR_EMPTY);
        Set<String> tagSet = getTagNameSet(5);
        tagSet.add(BLANK_STRING);
        mockTagManager.addTagsToResource(tagSet, VALID_RESOURCE_ID);
    }

    /**
     * Tests {@link TagManager#addTagsToResource(Set, int)} with a set
     * containing an empty string. Expects {@link IllegalArgumentException}
     */
    @Test
    public void testAddTagsToResourceEmptyItemInTagList() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_BLANK_OR_EMPTY);
        Set<String> tagSet = getTagNameSet(5);
        tagSet.add(EMPTY_STRING);
        mockTagManager.addTagsToResource(tagSet, VALID_RESOURCE_ID);
    }

    /**
     * Tests {@link TagManager#addTagsToResource(Set, int)} with an empty set.
     * Expects {@link IllegalArgumentException}
     */
    @Test
    public void testAddTagsToResourceEmptyList() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_LIST_EMPTY);
        mockTagManager.addTagsToResource(Collections.<String>emptySet(), VALID_RESOURCE_ID);
    }

    /**
     * Tests {@link TagManager#addTagsToResource(Set, int)} with a null set.
     * Expects {@link IllegalArgumentException}
     */
    @Test
    public void testAddTagsToResourceNullList() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_LIST_NULL);
        mockTagManager.addTagsToResource(null, VALID_RESOURCE_ID);
    }

    /**
     * Tests {@link TagManager#addTagsToResource(Set, int)} when a
     * {@link DAOException} is thrown while retrieving tag objects. Expects an
     * {@link ManagerException} to be thrown.
     */
    @Test
    public void testAddTagsToResourceErrorFindingTagsByName() throws DAOException {
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage(Constants.ERROR_FINDING_TAGS_BY_NAME);
        when(mockTagDAO.getTagsWithNameInCollection(any(List.class))).thenThrow(daoException);
        mockTagManager.addTagsToResource(tagNameSet, VALID_RESOURCE_ID);
    }

    /**
     * Tests {@link TagManager#addTagsToResource(Set, int)} when a
     * {@link DAOException} is thrown while adding to the tag/resource relation.
     * Expects an {@link ManagerException} to be thrown.
     */
    @Test
    public void testAddTagsToResourceErrorAddingTagResourceRelation() throws DAOException {
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage(Constants.ERROR_ADDING_TAG_RESOURCE_RELTN);
        when(mockTagDAO.getTagsWithNameInCollection(any(List.class))).thenReturn(listOfTags);
        doThrow(daoException).when(resourceTagRelationDAO).addTagsToResource(anyInt(), any(List.class));
        mockTagManager.addTagsToResource(tagNameSet, VALID_RESOURCE_ID);
    }

    /**
     * Tests {@link TagManager#getMissingTagNames(Collection)} when querying the
     * database for tags with those names throws a {@link DAOException}. Expects
     * a {@link ManagerException} to be thrown.
     */
    @Test
    public void testGetMissingTagNamesErrorFindingTagsByName() throws DAOException {
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage(Constants.ERROR_FINDING_TAGS_BY_NAME);
        when(mockTagDAO.getTagsWithNameInCollection(any(List.class))).thenThrow(daoException);
        mockTagManager.addTagsToResource(tagNameSet, VALID_RESOURCE_ID);
    }

    /**
     * Tests {@link TagManager#getMissingTagNames(Collection)} when all but one
     * tag name is not represented in the database.
     */
    @Test
    public void testGetMissingTagNamesAllButOneTagMissing() throws DAOException {
        listOfTags = createTestTags(1);
        tagNameList = getTagNameList(5);
        Collection<String> expectedReturn = tagNameList.subList(1, 5);
        when(mockTagDAO.getTagsWithNameInCollection(any(Collection.class))).thenReturn(listOfTags);
        mockTagManager.addTagsToResource(new HashSet<String>(tagNameList), VALID_RESOURCE_ID);
        verify(mockTagDAO).batchAddTags(tagCollectionCaptor.capture());
        assertTrue(CollectionUtils.isEqualCollection(expectedReturn, tagCollectionCaptor.getValue()));
    }

    /**
     * Tests {@link TagManager#getMissingTagNames(Collection)} when every tag
     * name is not represented in the database.
     */
    @Test
    public void testGetMissingTagNamesAllTagsMissing() throws DAOException {
        tagNameList = getTagNameList(5);
        when(mockTagDAO.getTagsWithNameInCollection(any(Collection.class))).thenReturn(Collections.<Tag>emptyList());
        mockTagManager.addTagsToResource(new HashSet<String>(tagNameList), VALID_RESOURCE_ID);
        verify(mockTagDAO).batchAddTags(tagCollectionCaptor.capture());
        assertTrue(CollectionUtils.isEqualCollection(tagNameList, tagCollectionCaptor.getValue()));
    }

    /**
     * Tests {@link TagManager#getMissingTagNames(Collection)} when only one tag
     * name is not represented in the database.
     */
    @Test
    public void testGetMissingTagNamesOneTagMissing() throws DAOException {
        listOfTags = createTestTags(4);
        tagNameList = getTagNameList(5);
        List<String> expectedReturn = tagNameList.subList(4, 5);
        when(mockTagDAO.getTagsWithNameInCollection(any(Collection.class))).thenReturn(listOfTags);
        mockTagManager.addTagsToResource(new HashSet<String>(tagNameList), VALID_RESOURCE_ID);
        verify(mockTagDAO).batchAddTags(tagCollectionCaptor.capture());
        assertTrue(CollectionUtils.isEqualCollection(expectedReturn, tagCollectionCaptor.getValue()));
    }

    /**
     * Tests {@link TagManager#addMissingTags(Collection)} when there are no
     * missing tags. Expects {@link TagDAO#batchAddTags(Collection)} to not be
     * called.
     */
    @Test
    public void testAddTagsToResourceNoTagsMissing() throws DAOException {
        listOfTags = createTestTags(5);
        tagNameList = getTagNameList(5);
        when(mockTagDAO.getTagsWithNameInCollection(any(Collection.class))).thenReturn(listOfTags);
        mockTagManager.addTagsToResource(new HashSet<String>(tagNameList), VALID_RESOURCE_ID);
        verify(mockTagDAO, never()).batchAddTags(any(Collection.class));
    }

    /**
     * Tests {@link TagManager#addMissingTags(Collection)} when there is one
     * missing tag. Expects {@link TagDAO#batchAddTags(Collection)} to be called
     * once.
     */
    @Test
    public void testAddTagsToResourceOneTagMissing() throws DAOException {
        when(mockTagDAO.getTagsWithNameInCollection(any(List.class))).thenReturn(listOfTags);
        tagNameSet.add(OTHER_VALID_TAG_NAME);
        mockTagManager.addTagsToResource(tagNameSet, VALID_RESOURCE_ID);
        verify(mockTagDAO, times(1)).batchAddTags(any(Collection.class));
    }

    /**
     * Tests {@link TagManager#addMissingTags(Collection)} when every tag is
     * missing. Expects {@link TagDAO#batchAddTags(Collection)} to be called
     * once.
     */
    @Test
    public void testAddTagsToResourceEveryTagMissing() throws DAOException {
        when(mockTagDAO.getTagsWithNameInCollection(any(List.class))).thenReturn(Collections.<Tag>emptyList());
        mockTagManager.addTagsToResource(tagNameSet, VALID_RESOURCE_ID);
        verify(mockTagDAO, times(1)).batchAddTags(any(List.class));
    }

    /**
     * Tests {@link TagManager#addMissingTags(Collection)} when only one tag is
     * not missing. Expects {@link TagDAO#batchAddTags(Collection)} to be called
     * once.
     */
    @Test
    public void testAddTagsToResourceAllButOneTagMissing() throws DAOException {
        when(mockTagDAO.getTagsWithNameInCollection(any(List.class))).thenReturn(createTestTags(1));
        mockTagManager.addTagsToResource(tagNameSet, VALID_RESOURCE_ID);
        verify(mockTagDAO, times(1)).batchAddTags(any(List.class));
    }
}
