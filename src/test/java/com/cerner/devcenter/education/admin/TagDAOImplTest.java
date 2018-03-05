package com.cerner.devcenter.education.admin;

import static com.cerner.devcenter.education.utils.TagTestUtil.createTestTags;
import static com.cerner.devcenter.education.utils.TagTestUtil.getTagNameList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.cerner.devcenter.education.admin.ResourceDAOImpl.ResourceRowMapper;
import com.cerner.devcenter.education.admin.TagDAOImpl.TagRowMapper;
import com.cerner.devcenter.education.models.Tag;
import com.cerner.devcenter.education.utils.Constants;

/**
 * Exists to test the {@link TagDAOImpl} class.
 *
 * @author Abhi Purella (AP045635)
 * @author Amos Bailey (AB032627)
 */
@RunWith(MockitoJUnitRunner.class)
public class TagDAOImplTest {
    @InjectMocks
    private TagDAOImpl tagDAOImpl;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private DataAccessException dataAccessException;
    @Mock
    private EmptyResultDataAccessException emptyResultDataAccessException;
    @Mock
    private ResultSet resultSet;
    @Mock
    private Tag mockTag;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static ResourceBundle i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

    private static final int VALID_TAG_ID = 5;
    private static final int MAX_TAG_NUMBER = 5;
    private static final int NEGATIVE_TAG_ID = -47;
    private static final String VALID_TAG_NAME = "commits";
    private static final String EMPTY_RESULT_ERROR_MESSAGE = "Error: the specified query did not return any results";
    private static final String EMPTY_STRING = "";
    private static final String BLANK_STRING = "  ";
    private static final String VALID_SEARCH_STRING = "comm";
    private static final String NULL_STRING = null;
    private Tag tag;
    private TagDAOImpl.TagRowMapper tagRowMapper;
    private List<Tag> newListOfTags;
    private List<String> tagNameList;

    @Before
    public void setup() throws SQLException {
        tag = new Tag(VALID_TAG_ID, VALID_TAG_NAME);
        tagRowMapper = new TagRowMapper();
        newListOfTags = new ArrayList<>();
        newListOfTags.add(tag);
        tagNameList = getTagNameList(7);
        when(resultSet.getInt("tag_id")).thenReturn(VALID_TAG_ID);
        when(resultSet.getString("tag_name")).thenReturn(VALID_TAG_NAME);
    }

    /**
     * This function tests {@link TagDAOImpl#getAllTags()} functionality and
     * expects {@link DAOException}.
     * 
     * @throws DAOException
     */
    @Test
    public void testGetAllTagsThrowsException() throws DAOException {
        doThrow(EmptyResultDataAccessException.class).when(jdbcTemplate).query(anyString(),
                any(ResourceRowMapper.class));
        expectedException.expect(DAOException.class);
        expectedException.expectMessage(EMPTY_RESULT_ERROR_MESSAGE);
        tagDAOImpl.getAllTags();
    }

    /**
     * This function tests {@link TagDAOImpl#getAllTags()} functionality and
     * expects {@link DAOException} and an error message when there is an error
     * getting all the tags from database.
     * 
     * @throws DAOException
     */
    @Test
    public void testGetAllTagsThrowsExceptionForErrorMessage() throws DAOException {
        doThrow(EmptyResultDataAccessException.class).when(jdbcTemplate).query(anyString(),
                any(ResourceRowMapper.class));
        expectedException.expect(DAOException.class);
        expectedException.expectMessage(EMPTY_RESULT_ERROR_MESSAGE);
        tagDAOImpl.getAllTags();
    }

    /**
     * This function verifies {@link TagRowMapper#mapRow(ResultSet, int)}
     * functionality.
     */
    @Test
    public void testMapRowForValidResultSet() throws SQLException {
        tag = tagRowMapper.mapRow(resultSet, 1);
        assertEquals(VALID_TAG_ID, tag.getTagId());
        assertEquals(VALID_TAG_NAME, tag.getTagName());
    }

    /**
     * This function tests {@link TagDAOImpl#getAllTags()} functionality. Test
     * method with a valid query that returns rows of result as expected. The
     * {@link List} of {@link Tag} objects returned should also match the
     * correct data.
     */
    @Test
    public void testGetAllTagsWhenValidQueryForRows() throws DAOException {
        List<Tag> tagsList = createTestTags(MAX_TAG_NUMBER);
        when(jdbcTemplate.query(anyString(), any(TagRowMapper.class))).thenReturn(tagsList);
        List<Tag> resultTagsList = tagDAOImpl.getAllTags();
        assertEquals(resultTagsList.size(), tagsList.size());

        for (int i = 0; i < resultTagsList.size(); i++) {
            assertEquals(tagsList.get(i).getTagId(), resultTagsList.get(i).getTagId());
            assertEquals(tagsList.get(i).getTagName(), resultTagsList.get(i).getTagName());
        }
    }

    /**
     * This tests {@link TagDAOImpl#getSearchedTags(String)} functionality and
     * expects {@link IllegalArgumentException} when search string is empty
     */
    @Test
    public void testGetSearchedTagsForEmptyString() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(i18nBundle.getString(Constants.SEARCH_INVALID_I18N));
        tagDAOImpl.getSearchedTags(EMPTY_STRING);
    }

    /**
     * This tests {@link TagDAOImpl#getSearchedTags(String)} functionality and
     * expects {@link IllegalArgumentException} when search string is blank,
     * i.e. contains only whitespace characters.
     */
    @Test
    public void testGetSearchedTagsForBlankString() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(i18nBundle.getString(Constants.SEARCH_INVALID_I18N));
        tagDAOImpl.getSearchedTags(BLANK_STRING);
    }

    /**
     * This tests {@link TagDAOImpl#getSearchedTags(String)} functionality and
     * expects {@link IllegalArgumentException} when search string is null.
     */
    @Test
    public void testGetSearchedTagsForNullString() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(i18nBundle.getString(Constants.SEARCH_INVALID_I18N));
        tagDAOImpl.getSearchedTags(NULL_STRING);
    }

    /**
     * This tests {@link TagDAOImpl#getSearchedTags(String)}. This expects a
     * {@link DAOException} when a {@link EmptyResultDataAccessException} is
     * thrown by performing the query.
     */
    @Test
    public void testGetSearchedTagsThrowsDAOException() throws DAOException {
        expectedException.expect(DAOException.class);
        expectedException.expectMessage(EMPTY_RESULT_ERROR_MESSAGE);
        when(jdbcTemplate.query(anyString(), any(TagRowMapper.class), anyString()))
                .thenThrow(emptyResultDataAccessException);
        tagDAOImpl.getSearchedTags(VALID_SEARCH_STRING);
    }

    /**
     * This function tests {@link TagDAOImpl#getSearchedTags(String)} with a
     * valid query that returns the {@link List} of {@link Tag} objects that are
     * expected.
     */
    @Test
    public void testGetSearchedTagsWhenValidSearch() throws DAOException {
        List<Tag> tagsList = createTestTags(MAX_TAG_NUMBER);
        when(jdbcTemplate.query(anyString(), any(TagRowMapper.class), anyString())).thenReturn(tagsList);
        assertEquals(tagsList, tagDAOImpl.getSearchedTags(VALID_SEARCH_STRING));
    }

    /**
     * This function tests {@link TagDAOImpl#getTagByID(int)} with the value 0
     * as as tag ID and expects and {@link IllegalArgumentException} to be
     * thrown.
     */
    @Test
    public void testGetTagByIDWithIdZero() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_ID_MUST_BE_POSITIVE);
        tagDAOImpl.getTagByID(0);
    }

    /**
     * This function tests {@link TagDAOImpl#getTagByID(int)} with the a
     * negative value as as tag ID and expects and
     * {@link IllegalArgumentException} to be thrown.
     */
    @Test
    public void testGetTagByIDWithNegativeID() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_ID_MUST_BE_POSITIVE);
        tagDAOImpl.getTagByID(NEGATIVE_TAG_ID);
    }

    /**
     * This function tests {@link TagDAOImpl#getTagByID(int)} with 1 for the
     * value for the TagID.
     */
    @Test
    public void testGetTagByIDTagID1() throws DAOException {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), anyVararg())).thenReturn(tag);
        assertEquals(tagDAOImpl.getTagByID(1), tag);
    }

    /**
     * Tests {@link TagDAOImpl#getTagByID(int)} when the database query returns
     * a valid tag.
     */
    @Test
    public void testGetTagByIDValidTag() throws DAOException {
        when(jdbcTemplate.queryForObject(anyString(), any(TagRowMapper.class), anyInt())).thenReturn(tag);
        assertEquals(tagDAOImpl.getTagByID(VALID_TAG_ID), tag);
    }

    /**
     * Tests {@link TagDAOImpl#getTagByID(int)} when the database query throws a
     * {@link DataAccessException}.
     * 
     * Expects a {@link DAOException} to be thrown.
     */
    @Test
    public void testGetTagByIDThrowsDAOException() throws DAOException {
        expectedException.expect(DAOException.class);
        expectedException.expectMessage(String.format(Constants.ERROR_GETTING_TAG_BY_ID, VALID_TAG_ID));
        when(jdbcTemplate.queryForObject(anyString(), any(TagRowMapper.class), anyInt()))
                .thenThrow(dataAccessException);
        tagDAOImpl.getTagByID(VALID_TAG_ID);
    }

    /**
     * Test {@link TagDAOImpl#addTagToDB(String)} by passing in a null string.
     * Expects a {@link IllegalArgumentException}.
     */
    @Test
    public void testAddTagToDBNullString() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_NULL);
        tagDAOImpl.addTagToDB(NULL_STRING);
    }

    /**
     * Test {@link TagDAOImpl#addTagToDB(String)} by passing in an empty string.
     * Expects a {@link IllegalArgumentException}
     */
    @Test
    public void testAddTagToDBEmptyString() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_BLANK_OR_EMPTY);
        tagDAOImpl.addTagToDB(EMPTY_STRING);
    }

    /**
     * Test {@link TagDAOImpl#addTagToDB(String)} by passing in an empty string.
     * Expects a {@link IllegalArgumentException}
     */
    @Test
    public void testAddTagToDBBlankString() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_BLANK_OR_EMPTY);
        tagDAOImpl.addTagToDB(BLANK_STRING);
    }

    /**
     * Test {@link TagDAOImpl#addTagToDB(String)} when the input is valid and
     * there are no exceptions.
     */
    @Test
    public void testAddTagToDBValidInput() throws DAOException {
        // ensure an exception is not thrown.
        tagDAOImpl.addTagToDB(VALID_TAG_NAME);
    }

    /**
     * Tests {@link TagDAOImpl#batchAddTags(Collection)} with an empty list.
     * Expects an {@link IllegalArgumentException}.
     */
    @Test
    public void testBatchAddTagsEmptyList() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_LIST_EMPTY);
        tagDAOImpl.batchAddTags(Collections.<String>emptyList());
    }

    /**
     * Tests {@link TagDAOImpl#batchAddTags(Collection)} with a null list.
     * Expects an {@link IllegalArgumentException}.
     */
    @Test
    public void testBatchAddTagsNullList() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_LIST_NULL);
        tagDAOImpl.batchAddTags(null);
    }

    /**
     * Tests {@link TagDAOImpl#batchAddTags(Collection)} with a list containing
     * null. Expects an {@link IllegalArgumentException}.
     */
    @Test
    public void testBatchAddTagsListContainsNull() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_NULL);
        tagNameList.add(null);
        tagDAOImpl.batchAddTags(tagNameList);
    }

    /**
     * Tests {@link TagDAOImpl#batchAddTags(Collection)} with a list containing
     * the empty string. Expects an {@link IllegalArgumentException}.
     */
    @Test
    public void testBatchAddTagsListContainsEmptyString() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_BLANK_OR_EMPTY);
        tagNameList.add(EMPTY_STRING);
        tagDAOImpl.batchAddTags(tagNameList);
    }

    /**
     * Tests {@link TagDAOImpl#batchAddTags(Collection)} with a list containing
     * a blank string. Expects an {@link IllegalArgumentException}.
     */
    @Test
    public void testBatchAddTagsListContainsBlankString() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_BLANK_OR_EMPTY);
        tagNameList.add(BLANK_STRING);
        tagDAOImpl.batchAddTags(tagNameList);
    }

    /**
     * Tests {@link TagDAOImpl#getTagByName(String)} when the input is null.
     * Expects an {@link IllegalArgumentException}
     */
    @Test
    public void testGetTagByNameNullString() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_NULL);
        tagDAOImpl.getTagByName(NULL_STRING);
    }

    /**
     * Tests {@link TagDAOImpl#getTagByName(String)} when the input is an empty
     * string. Expects an {@link IllegalArgumentException}
     */
    @Test
    public void testGetTagByNameEmptyString() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_BLANK_OR_EMPTY);
        tagDAOImpl.getTagByName(EMPTY_STRING);
    }

    /**
     * Tests {@link TagDAOImpl#getTagByName(String)} when the input is a blank
     * string. Expects an {@link IllegalArgumentException}
     */
    @Test
    public void testGetTagByNameBlankString() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_BLANK_OR_EMPTY);
        tagDAOImpl.getTagByName(BLANK_STRING);
    }

    /**
     * Tests {@link TagDAOImpl#getTagByName(String)} when attempting to querying
     * throws an exception. Expects an {@link DAOException}
     */
    @Test
    public void testGetTagByNameQueryException() throws DAOException {
        expectedException.expect(DAOException.class);
        expectedException.expectMessage(Constants.ERROR_FINDING_TAGS_BY_NAME);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString())).thenThrow(dataAccessException);
        tagDAOImpl.getTagByName(VALID_TAG_NAME);
    }

    /**
     * Tests {@link TagDAOImpl#getTagByName(String)} with valid input and when
     * the tag name is matched to a tag in the database.
     */
    @Test
    public void testGetTagByNameValidInputIsMatched() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString())).thenReturn(newListOfTags);
        assertEquals(tagDAOImpl.getTagByName(VALID_TAG_NAME), newListOfTags.get(0));
    }

    /**
     * Tests {@link TagDAOImpl#getTagByName(String)} with valid input and when
     * the tag name is not matched to a tag in the database. Expects null to be
     * returned.
     */
    @Test
    public void testGetTagByNameValidInputNotMatched() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString()))
                .thenReturn(Collections.<Tag>emptyList());
        assertEquals(tagDAOImpl.getTagByName(VALID_TAG_NAME), null);
    }

    /**
     * Tests {@link TagDAOImpl#getTagsWithNameInCollection(Collection)} with an
     * empty list. Expects an {@link IllegalArgumentException}.
     */
    @Test
    public void testGetTagsWithNameInListEmptyList() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_LIST_EMPTY);
        tagDAOImpl.getTagsWithNameInCollection(Collections.<String>emptyList());
    }

    /**
     * Tests {@link TagDAOImpl#getTagsWithNameInCollection(Collection)} with a
     * null list. Expects an {@link IllegalArgumentException}.
     */
    @Test
    public void testGetTagsWithNameInListNullList() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_LIST_NULL);
        tagDAOImpl.getTagsWithNameInCollection(null);
    }

    /**
     * Tests {@link TagDAOImpl#getTagsWithNameInCollection(Collection)} with a
     * list containing null. Expects an {@link IllegalArgumentException}.
     */
    @Test
    public void testGetTagsWithNameInListListContainsNull() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_NULL);
        tagNameList.add(null);
        tagDAOImpl.getTagsWithNameInCollection(tagNameList);
    }

    /**
     * Tests {@link TagDAOImpl#getTagsWithNameInCollection(Collection)} with a
     * list containing the empty string. Expects an
     * {@link IllegalArgumentException}.
     */
    @Test
    public void testGetTagsWithNameInListListContainsEmptyString() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_BLANK_OR_EMPTY);
        tagNameList.add(EMPTY_STRING);
        tagDAOImpl.getTagsWithNameInCollection(tagNameList);
    }

    /**
     * Tests {@link TagDAOImpl#getTagsWithNameInCollection(Collection)} with a
     * list containing a blank string. Expects an
     * {@link IllegalArgumentException}.
     */
    @Test
    public void testGetTagsWithNameInListListContainsBlankString() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_BLANK_OR_EMPTY);
        tagNameList.add(BLANK_STRING);
        tagDAOImpl.getTagsWithNameInCollection(tagNameList);
    }

    /**
     * Tests {@link TagDAOImpl#getTagsWithNameInCollection(Collection)} when an
     * error occurs. Expects a {@link DAOException} to be thrown.
     */
    @Test
    public void testGetTagsWithNameInListListQueryError() throws DAOException {
        expectedException.expect(DAOException.class);
        expectedException.expectMessage(Constants.ERROR_FINDING_TAGS_BY_NAME);
        when(jdbcTemplate.query(anyString(), any(TagRowMapper.class), anyVararg())).thenThrow(dataAccessException);
        tagDAOImpl.getTagsWithNameInCollection(tagNameList);
    }

}
