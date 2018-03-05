package com.cerner.devcenter.education.admin;

import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.models.ResourceType;
import com.cerner.devcenter.education.models.Tag;
import com.cerner.devcenter.education.utils.Constants;

/**
 * Tests the functionality of the {@link ResourceTagRelationDAOImpl} class.
 * 
 * @author Amos Bailey (AB032627)
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourceTagRelationDAOImplTest {
    @InjectMocks
    private ResourceTagRelationDAOImpl resourceTagRelationDAO;
    @Mock
    private JdbcTemplate mockJdbcTemplate;
    @Mock
    private ResourceType mockResourceType;
    @Mock
    private ResourceDAOImpl mockResourceDAO;
    @Mock
    private TagDAOImpl validTagListDAO;
    @Mock
    private Tag mockTag;
    @Mock
    private Tag invalidMockTag;
    @Mock
    private DataAccessException mockDataAccessException;
    @Mock
    private Resource mockResource;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final static int VALID_TAG_ID = 1;
    private final static String VALID_TAG_NAME = "MyLittlePony";
    private final static int NEGATIVE_TAG_ID = -1;
    private final static int VALID_RESOURCE_ID = 1;
    private final static int NEGATIVE_RESOURCE_ID = -1;
    private final static String EMPTY_STRING = "";
    private final static String BLANK_STRING = "    \t\t  \n";

    private List<Tag> tagList;

    @Before
    public void setUp() throws MalformedURLException, DAOException {
        when(mockTag.getTagId()).thenReturn(VALID_TAG_ID);
        when(mockTag.getTagName()).thenReturn(VALID_TAG_NAME);
        when(validTagListDAO.getTagByID(VALID_TAG_ID)).thenReturn(mockTag);
        when(mockResourceDAO.getById(VALID_RESOURCE_ID)).thenReturn(mockResource);
        tagList = new ArrayList<Tag>();
        tagList.add(mockTag);
    }

    /**
     * Tests the {@link ResourceTagRelationDAOImpl#addTagsToResource(int,
     * List)} method. Ensures a {@link IllegalArgumentException} is thrown
     * with the expected message when a null tagList is passed.
     */
    @Test
    public void testAddTagsToResourceWithNullTagList() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_LIST_NULL);
        resourceTagRelationDAO.addTagsToResource(VALID_RESOURCE_ID, null);
    }

    /**
     * Tests the {@link ResourceTagRelationDAOImpl#addTagsToResource(int,
     * List)} method. Ensures a {@link IllegalArgumentException} is thrown
     * with the expected message when an empty tagList is passed.
     */
    @Test
    public void testAddTagsToResourceWithEmptyTagList() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_LIST_EMPTY);
        resourceTagRelationDAO.addTagsToResource(VALID_RESOURCE_ID, Collections.<Tag>emptyList());
    }

    /**
     * Tests the {@link ResourceTagRelationDAOImpl#addTagsToResource(int,
     * List)} method. Ensures a {@link IllegalArgumentException} is thrown
     * with the expected message when the resource has a negative ID.
     */
    @Test
    public void testAddTagsToResourceWithNegativeIDResource() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.RESOURCE_ID_MUST_BE_POSITIVE);
        resourceTagRelationDAO.addTagsToResource(NEGATIVE_RESOURCE_ID, tagList);
    }

    /**
     * Tests the {@link ResourceTagRelationDAOImpl#addTagsToResource(int,
     * List)} method. Ensures a {@link IllegalArgumentException} is thrown
     * with the expected message when a resource has an ID of zero.
     */
    @Test
    public void testAddTagsToResourceWithZeroIDResource() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.RESOURCE_ID_MUST_BE_POSITIVE);
        resourceTagRelationDAO.addTagsToResource(0, tagList);
    }

    /**
     * Tests the {@link ResourceTagRelationDAOImpl#addTagsToResource(int,
     * List)} method. Ensures a {@link IllegalArgumentException} is thrown
     * with the expected message when the list contains a tag with a negative
     * ID.
     */
    @Test
    public void testAddTagsToResourceWithNegativeIDTag() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_ID_MUST_BE_POSITIVE);
        when(invalidMockTag.getTagId()).thenReturn(NEGATIVE_RESOURCE_ID);
        tagList.add(invalidMockTag);
        resourceTagRelationDAO.addTagsToResource(VALID_RESOURCE_ID, tagList);
    }

    /**
     * Tests the {@link ResourceTagRelationDAOImpl#addTagsToResource(int,
     * List)} method. Ensures a {@link IllegalArgumentException} is thrown
     * with the expected message when the list contains a null tag.
     */
    @Test
    public void testAddTagsToResourceWithNullTag() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_LIST_HAS_NULL_TAG);
        tagList.add(null);
        resourceTagRelationDAO.addTagsToResource(VALID_RESOURCE_ID, tagList);
    }

    /**
     * Tests the {@link ResourceTagRelationDAOImpl#addTagsToResource(int,
     * List)} method. Ensures a {@link IllegalArgumentException} is thrown
     * with the expected message when a tag has an ID of zero.
     */
    @Test
    public void testAddTagsToResourceWithZeroIDTag() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_ID_MUST_BE_POSITIVE);
        when(invalidMockTag.getTagId()).thenReturn(0);
        tagList.add(invalidMockTag);
        resourceTagRelationDAO.addTagsToResource(VALID_RESOURCE_ID, tagList);
    }

    /**
     * Tests the {@link ResourceTagRelationDAOImpl#addTagsToResource(int,
     * List)} method. Ensures a {@link IllegalArgumentException} is thrown
     * with the expected message when a tag has an null name.
     */
    @Test
    public void testAddTagsToResourceWithNullNameTag() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_NULL);
        when(invalidMockTag.getTagId()).thenReturn(VALID_TAG_ID);
        when(invalidMockTag.getTagName()).thenReturn(null);
        tagList.add(invalidMockTag);
        resourceTagRelationDAO.addTagsToResource(VALID_RESOURCE_ID, tagList);
    }

    /**
     * Tests the {@link ResourceTagRelationDAOImpl#addTagsToResource(int, List)}
     *  method. Ensures a {@link IllegalArgumentException} is thrown
     * with the expected message when a tag has an empty name.
     */
    @Test
    public void testAddTagsToResourceWithEmptyNameTag() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_BLANK_OR_EMPTY);
        when(invalidMockTag.getTagId()).thenReturn(VALID_TAG_ID);
        when(invalidMockTag.getTagName()).thenReturn(EMPTY_STRING);
        tagList.add(invalidMockTag);
        resourceTagRelationDAO.addTagsToResource(VALID_RESOURCE_ID, tagList);
    }

    /**
     * Tests the {@link ResourceTagRelationDAOImpl#addTagsToResource(int,
     * List)} method. Ensures a {@link IllegalArgumentException} is thrown
     * with the expected message when a tag has a blank name.
     */
    @Test
    public void testAddTagsToResourceWithBlankNameTag() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.TAG_NAME_CANNOT_BE_BLANK_OR_EMPTY);
        when(invalidMockTag.getTagId()).thenReturn(VALID_TAG_ID);
        when(invalidMockTag.getTagName()).thenReturn(BLANK_STRING);
        tagList.add(invalidMockTag);
        resourceTagRelationDAO.addTagsToResource(VALID_RESOURCE_ID, tagList);
    }
}
