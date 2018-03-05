package com.cerner.devcenter.education.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cerner.devcenter.education.admin.CategoryResourceRelationDAO;
import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.admin.ResourceDAO;
import com.cerner.devcenter.education.admin.ResourceCategoryRelationDAO;
import com.cerner.devcenter.education.authentication.LdapReader;
import com.cerner.devcenter.education.dao.ResourceRequestDAO;
import com.cerner.devcenter.education.dao.ResourceRequestDAOImpl;
import com.cerner.devcenter.education.exceptions.ItemAlreadyExistsException;
import com.cerner.devcenter.education.exceptions.ResourceIdNotFoundException;
import com.cerner.devcenter.education.exceptions.CategoryIdNotFoundException;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.models.ResourceRequest;
import com.cerner.devcenter.education.models.ResourceStatus;
import com.cerner.devcenter.education.models.ResourceCategoryRelation;
import com.cerner.devcenter.education.models.ResourceType;
import com.cerner.devcenter.education.user.UserDetails;
import com.cerner.devcenter.education.user.UserProfileDetails;

import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

/**
 * Tests the functionalities of {@link ResourceManager} class
 *
 * @author Wuchen Wang (WW044343)
 * @author Jacob Zimmermann (JZ022690)
 * @author Vincent Dasari (VD049645)
 * @author Vatsal Kesarwani (VK049896)
 * @author Rishabh Bhojak (RB048032)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(LdapReader.class)
public class ResourceManagerTest {

    @InjectMocks
    private ResourceManager resourceManager;
    @Mock
    private Resource mockResource;
    @Mock
    private ResourceDAO mockResourceDAO;
    @Mock
    private Category mockCategory;
    @Mock
    private ResourceCategoryRelationDAO mockResourceCategoryRelationDAO;
    @Mock
    private ResourceCategoryRelation mockResourceCategoryRelation;
    @Mock
    private CategoryResourceRelationDAO categoryResourceRelationDAO;
    @Mock
    private ResourceRequestDAO resourceRequestDAO;
    @Mock
    private DAOException daoException;
    @Mock
    private SearchResult searchResults;
    @Mock
    private Attributes attributes;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    private Appender mockAppender;
    @Mock
    private LdapReader reader;
    @Mock
    private UserProfileDetails mockUser;
    @Mock
    private UserDetails mockUserDetail;

    private DirContext dirContext;
    private Resource resource;
    private ResourceRequest resourceRequest;
    private UserProfileDetails user;
    private List<Resource> listOfResources;
    private List<ResourceRequest> listOfResourceRequests;
    private List<Category> listOfCategories;
    private Map<Integer, Integer> testResourceDifficultyForCategoryMap;

    private static final int VALID_RESOURCE_ID = 5;
    private static final int RESOURCE_ID_SMALLER_THAN_ZERO = -1;
    private static URL STATIC_URL = null;
    private static final String VALID_RESOURCE_NAME = "name";
    private static final String VALID_RESOURCE_DESCRIPTION = "description";
    private static final String VALID_RESOURCE_OWNER = "RB048032";
    private static final String VALID_RESOURCE_STATUS = ResourceStatus.Available.toString();
    private static final ResourceType VALID_RESOURCE_TYPE = new ResourceType(1, "Youtube");
    private static final int INVALID_RESOURCE_ID = 150;
    private static final String INVALID_RESOURCE_STATUS = "Published";
    private static final int NEGATIVE_RESOURCE_ID = -2;
    private static final String DESCRIPTION = "test";
    private static final int VALID_CATEGORY_ID = 5;
    private static final int RESOURCES_PER_PAGE = 10;
    private static final int MAX_CATEGORY_NUMBER = 5;
    private static final int VALID_DIFFICULTY_LEVEL = 1;
    private static final String VALID_CATEGORY_NAME = "java";
    private static final String VALID_CATEGORY_DESCRIPTION = "Programming Language";
    private static final String EMPTY_STRING = "";
    private static final String BLANK_STRING = " ";
    private static final String VALID_TYPE = "EBook";
    private static final String TOKEN = "token";
    private static final String BASE = "OU=Office Locations,DC=northamerica,DC=cerner,DC=net";

    private static final String RESOURCE_EXISTS_ERROR = "Resource {0} already exists in resource table";
    private static final String RESOURCE_CANNOT_BE_NULL = "Resource argument cannot be null";
    private static final String CATEGORY_RELATION_ERROR = "Error adding resource and its relation to a particular category";
    private static final String INSERT_RESOURCE_FAILURE = "Error while adding a resource";
    private static final String RESOURCE_NAME_INVALID = "Resource name is invalid";
    private static final String EDIT_RESORUCE_ERROR = "Error editing resource using its ID";
    private static final String RESOURCE_ID_INVALID = "ResourceID must be greater than 0";
    private static final String RESOURCE_LEVEL_INVALID = "Resource level must be greater than 0";
    private static final String RESOURCE_NAME_NULL = "Resource Name can not be null";
    private static final String RESOURCE_LINK_NULL = "Resource Link can not be null";
    private static final String RESOURCE_TYPE_NULL = "Resource Type can not be null";
    private static final String RESOURCE_NAME_EMPTY = "Resource Name can't be empty/blank";
    private static final String RESOURCE_DESCRIPTION_INVALID = "Resource Description cannot be null/empty/blank";
    private static final String RESOURCE_LINK_EMPTY = "Resource Link can't be empty/blank";
    private static final String RESOURCE_TYPE_EMPTY = "Resource Type can't be empty/blank";
    private static final String RESOURCE_STATUS_NULL = "Resource status cannot be null";
    private static final String RESOURCE_STATUS_INVALID = "Resource status must be Available/Pending/Deleted";
    private static final String CATEGORY_DIFFICULTY_MAP_NULL = "Resource Difficulty for Category map cannot be null";
    private static final String CATEGORY_DIFFICULTY_MAP_EMPTY = "Resource Difficulty for Category map cannot be null";
    private static final String CATEGORY_DIFFICULTY_MAP_NULL_KEY = "Resource Difficulty for Category map cannot have a null key";
    private static final String CATEGORY_DIFFICULTY_MAP_NULL_VALUE = "Resource Difficulty for Category map cannot have null value(s)";

    static {
        try {
            STATIC_URL = new URL("http://www.testing.com");
        } catch (final MalformedURLException e) {
            STATIC_URL = null;
        }
    }

    @Before
    public void setup() throws MalformedURLException, DAOException, NamingException {
        dirContext = new InitialDirContext();
        user = new UserProfileDetails("Name,Name", "Role", "TestId", "Email", "DevCenter", "Manager", "Project");
        MockitoAnnotations.initMocks(this);
        resource = new Resource(VALID_RESOURCE_ID, STATIC_URL, VALID_RESOURCE_DESCRIPTION, VALID_RESOURCE_NAME);
        listOfResources = new ArrayList<Resource>();
        listOfResources.add(resource);
        resourceRequest = new ResourceRequest();
        resourceRequest.setCategoryName(VALID_CATEGORY_NAME);
        resourceRequest.setResourceName(VALID_RESOURCE_NAME);
        listOfResourceRequests = new ArrayList<ResourceRequest>();
        listOfResourceRequests.add(resourceRequest);
        testResourceDifficultyForCategoryMap = new HashMap<Integer, Integer>();
        testResourceDifficultyForCategoryMap.put(5, 1);
        testResourceDifficultyForCategoryMap.put(6, 1);
        testResourceDifficultyForCategoryMap.put(7, 1);
        testResourceDifficultyForCategoryMap.put(8, 1);
        testResourceDifficultyForCategoryMap.put(9, 1);

        // Mock Resource
        when(mockResource.getResourceId()).thenReturn(VALID_RESOURCE_ID);
        when(mockResource.getResourceLink()).thenReturn(STATIC_URL);
        when(mockResource.getDescription()).thenReturn(VALID_RESOURCE_DESCRIPTION);
        when(mockResource.getResourceName()).thenReturn(VALID_RESOURCE_NAME);
        when(mockResource.getResourceOwner()).thenReturn(VALID_RESOURCE_OWNER);
        when(mockResource.getResourceDifficultyForCategory()).thenReturn(testResourceDifficultyForCategoryMap);
        when(mockResource.getResourceType()).thenReturn(VALID_RESOURCE_TYPE);
        when(mockResource.getResourceStatus()).thenReturn(VALID_RESOURCE_STATUS);

        // Mock ResourceDAO
        when(mockResourceDAO.getById(VALID_RESOURCE_ID)).thenReturn(resource);
        when(mockResourceDAO.getResourcesByCategoryId(VALID_CATEGORY_ID)).thenReturn(listOfResources);
        when(mockResourceDAO.addResource(VALID_RESOURCE_DESCRIPTION, VALID_RESOURCE_NAME,
                STATIC_URL, VALID_RESOURCE_TYPE, VALID_RESOURCE_OWNER, VALID_RESOURCE_STATUS)).thenReturn(VALID_RESOURCE_ID);

        when(resourceRequestDAO.getAllResourceRequests()).thenReturn(listOfResourceRequests);

        LogManager.getRootLogger().addAppender(mockAppender);
    }

    @After
    public void tearDown() {
        LogManager.getRootLogger().removeAppender(mockAppender);
    }

    /**
     * Tests {@link ResourceManager#addResourceAndRelations(Resource)}
     * functionality, expects {@link IllegalArgumentException} when
     * {@link Resource} is null
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test
    public void testAddResourceAndRelationsWhenResourceIsNull() throws ItemAlreadyExistsException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_CANNOT_BE_NULL);
        resourceManager.addResourceAndRelations(null);
    }

    /**
     * Tests {@link ResourceManager#addResourceAndRelations(Resource)}
     * {@link ResourceDAO#addResource(String, String, URL, ResourceType, String)}
     * throws {@link DAOException}
     *
     * @throws ManagerException
     *             to pass the test
     */
    @Test
    public void testAddResourceAndRelationsThrowsManagerException() throws DAOException, ItemAlreadyExistsException {
        resource.setResourceOwner(VALID_RESOURCE_OWNER);
        resource.setResourceStatus(VALID_RESOURCE_STATUS);
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage("Error adding resource and its relation to a particular category");
        when(mockResourceDAO.addResource(resource.getDescription(), resource.getResourceName(),
                resource.getResourceLink(), resource.getResourceType(), resource.getResourceOwner(),
                resource.getResourceStatus())).thenThrow(new DAOException());
        resourceManager.addResourceAndRelations(resource);
    }

    /***
     * Tests {@link ResourceManager#addResourceAndRelations(Resource)} when
     * resource name is null.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test
    public void testAddResourceAndRelationsWithNullResourceName() throws ItemAlreadyExistsException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_NAME_INVALID);
        when(mockResource.getResourceName()).thenReturn(null);
        resourceManager.addResourceAndRelations(mockResource);
    }

    /***
     * Tests {@link ResourceManager#addResourceAndRelations(Resource)} when
     * resource name is empty.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test
    public void testAddResourceAndRelationsWithEmptyResourceName() throws ItemAlreadyExistsException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_NAME_INVALID);
        when(mockResource.getResourceName()).thenReturn(EMPTY_STRING);
        resourceManager.addResourceAndRelations(mockResource);
    }

    /***
     * Tests {@link ResourceManager#addResourceAndRelations(Resource)} when
     * resource name is blank.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test
    public void testAddResourceAndRelationsWithBlankResourceName() throws ItemAlreadyExistsException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_NAME_INVALID);
        when(mockResource.getResourceName()).thenReturn(BLANK_STRING);
        resourceManager.addResourceAndRelations(mockResource);
    }

    /***
     * Tests {@link ResourceManager#addResourceAndRelations(Resource)} throws
     * {@link ItemAlreadyExistsException} when the database already contains a
     * resource of the given name.
     *
     * @throws ItemAlreadyExistsException
     *             to pass the test
     */
    @Test
    public void testAddResourceAndRelationsWhenResourceExists() throws ItemAlreadyExistsException {
        expectedException.expect(ItemAlreadyExistsException.class);
        expectedException.expectMessage(MessageFormat.format(RESOURCE_EXISTS_ERROR, resource.getResourceName()));
        when(resourceManager.checkResourceExists(VALID_RESOURCE_NAME)).thenReturn(true);
        resourceManager.addResourceAndRelations(mockResource);
    }

    /***
     * Tests {@link ResourceManager#addResourceAndRelations(Resource)} calls
     * {@link CategoryResourceRelationDAO#addMappingsToDB(Resource, Category)}
     * once per category the resource contains.
     */
    @Test
    public void testAddResourceAndRelationsCategoryResourceRelationDAOCall()
            throws DAOException, ItemAlreadyExistsException {
        listOfCategories = createTestCategories();
        when(mockResource.getCategories()).thenReturn(listOfCategories);
        resourceManager.addResourceAndRelations(mockResource);
        verify(categoryResourceRelationDAO, times(MAX_CATEGORY_NUMBER))
                .addMappingsToDB(any(Resource.class), any(Category.class));
    }

    /**
     * Tests that @{link ResourceManager#getResourceById(int)} throws an
     * {@link IllegalArgumentException} when getResourceById is negative id.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourceFromDBWhenIdIsNegative() {
        resourceManager.getResourceById(RESOURCE_ID_SMALLER_THAN_ZERO);
    }

    /**
     * Tests that @{link ResourceManager#getResourceById(int)} throws an
     * {@link IllegalArgumentException} when getResourceById is Zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourceFromDBWhenIdIsZero() {
        resourceManager.getResourceById(0);
    }

    /**
     * Tests {@link ResourceManager#getResourceById(int)} functionality, expects
     * {@link ManagerException} when {@link ResourceDAO#getById(int)} throws
     * {@link DAOException}
     */
    @Test(expected = ManagerException.class)
    public void testGetResourceFromDBThrowsManagerException() throws Exception {
        doThrow(DAOException.class).when(mockResourceDAO).getById(VALID_RESOURCE_ID);
        resourceManager.getResourceById(VALID_RESOURCE_ID);
    }

    /**
     * Tests {@link ResourceManager#getResourceById(int)} functionality is
     * called by ResourceManager and getResourceById() returns a valid resource.
     */
    @Test
    public void testGetResourceFromDB() {
        final Resource newResource = resourceManager.getResourceById(VALID_RESOURCE_ID);
        assertEquals(resource.getResourceId(), newResource.getResourceId());
        assertEquals(resource.getDescription(), newResource.getDescription());
        assertEquals(resource.getResourceLink(), newResource.getResourceLink());
        assertEquals(resource.getResourceType(), newResource.getResourceType());
    }

    /**
     * Tests that {@link CategoryResourceRelationDAO#deleteById(int)} and
     * {@link ResourceDAO#deleteById(int)} are called by the
     * {@link ResourceManager}.
     */
    @Test
    public void testDeleteResource() throws DAOException {
        listOfCategories = createTestCategories();

        for (final Category category : listOfCategories) {
            resource.addCategory(category);
        }

        resourceManager.deleteResource(VALID_RESOURCE_ID);
        verify(mockResourceDAO).deleteById(VALID_RESOURCE_ID);
    }

    /**
     * Tests that a {@link IllegalArgumentException} is thrown when id of
     * {@link Resource} passed is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteByIdWithNegativeResourceId() {
        resourceManager.deleteResource(NEGATIVE_RESOURCE_ID);
    }

    /**
     * Tests that a {@link IllegalArgumentException} is thrown when id of
     * {@link Resource} passed is valid but not present in database.
     */
    @Test
    public void testDeleteByIdWithInvalidResourceId() throws DAOException {
        resourceManager.deleteResource(INVALID_RESOURCE_ID);
        verify(mockResourceDAO).deleteById(INVALID_RESOURCE_ID);
    }

    /**
     * Tests that a {@link IllegalArgumentException} is thrown when id of
     * {@link Resource} passed is zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteByIdWithZeroResourceId() {
        resourceManager.deleteResource(0);
    }

    /**
     * Tests that a {@link ManagerException} is thrown when there is an error in
     * accessing the database.
     */
    @Test(expected = ManagerException.class)
    public void testDeleteByIdDAOExceptionByResourceDAO() throws DAOException {
        doThrow(DAOException.class).when(mockResourceDAO).deleteById(VALID_RESOURCE_ID);
        resourceManager.deleteResource(VALID_RESOURCE_ID);
    }

    /**
     * Tests {@link ResourceManager#getResourcesByCategoryId(int)}
     * functionality, expects {@link IllegalArgumentException} when category_id
     * is negative
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesByCategoryIdWhenIdIsNegative() {
        resourceManager.getResourcesByCategoryId(RESOURCE_ID_SMALLER_THAN_ZERO);
    }

    /**
     * Tests {@link ResourceManager#getResourcesByCategoryId(int)}
     * functionality, expects {@link IllegalArgumentException} when category_id
     * is zero
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesByCategoryIDWhenIdIsZero() {
        resourceManager.getResourcesByCategoryId(0);
    }

    /**
     * Tests {@link ResourceManager#getResourcesByCategoryId(int)}
     * functionality, expects {@link ManagerException} when
     * {@link ResourceDAO#getResourcesByCategoryId(int)} throws
     * {@link DAOException}
     */
    @Test(expected = ManagerException.class)
    public void testGetResourcesByCategoryIdThrowsManagerException() throws DAOException {
        doThrow(DAOException.class).when(mockResourceDAO).getResourcesByCategoryId(VALID_CATEGORY_ID);
        resourceManager.getResourcesByCategoryId(VALID_CATEGORY_ID);
    }

    /**
     * Tests {@link ResourceManager#getResourcesByCategoryId(int)} functionality
     * with valid input.
     */
    @Test
    public void testGetResourceByCategoryIDValid() {
        final List<Resource> newListOfResources = resourceManager.getResourcesByCategoryId(VALID_CATEGORY_ID);
        assertEquals(resource.getResourceId(), newListOfResources.get(0).getResourceId());
        assertEquals(resource.getDescription(), newListOfResources.get(0).getDescription());
        assertEquals(resource.getResourceLink(), newListOfResources.get(0).getResourceLink());
    }

    /**
     * Tests {@link ResourceManager#getResourceDAO()} functionality
     */
    @Test
    public void testResourceDAOGetter() {
        assertSame(mockResourceDAO, resourceManager.getResourceDAO());
    }

    /**
     * Tests {@link ResourceManager#getCategoryResourceRelationDAO()}
     * functionality
     */
    @Test
    public void testCategoryResourceRelationDAOGetter() {
        assertSame(categoryResourceRelationDAO, resourceManager.getCategoryResourceRelationDAO());
    }

    /**
     * Verifies {@link ResourceManager#retrieveResourceDescriptionById(int)}
     * when ResourceId of {@link Resource} is not present in resource table.
     */
    @Test
    public void testRetrieveResourceDescriptionById_WhenResourceIdIsInvalid() {
        when(mockResourceDAO.getResourceDescriptionById(INVALID_RESOURCE_ID)).thenReturn(null);
        assertEquals(null, resourceManager.retrieveResourceDescriptionById(INVALID_RESOURCE_ID));
        verify(mockResourceDAO).getResourceDescriptionById(INVALID_RESOURCE_ID);
    }

    /**
     * Verifies {@link ResourceManager#retrieveResourceDescriptionById(int)}
     * when ResourceId of {@link Resource} is present in resource table.
     */
    @Test
    public void testRetrieveResourceDescriptionById_WhenResourceIdIsvalid() {
        when(mockResourceDAO.getResourceDescriptionById(VALID_RESOURCE_ID)).thenReturn(DESCRIPTION);
        assertEquals(DESCRIPTION, resourceManager.retrieveResourceDescriptionById(VALID_RESOURCE_ID));
        verify(mockResourceDAO).getResourceDescriptionById(VALID_RESOURCE_ID);
    }

    /**
     * Verifies {@link ResourceManager#retrieveResourceDescriptionById(int)}
     * when ResourceId of {@link Resource} is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRetrieveResourceDescriptionById_WhenResourceIdIsNegative() throws IllegalArgumentException {
        resourceManager.retrieveResourceDescriptionById(NEGATIVE_RESOURCE_ID);
        expectedException.expectMessage("resourceId must be a positive number");
    }

    /**
     * Tests
     * {@link ResourceManager#addResourceCategoryRelationWithDifficultyLevel(Resource)}
     * functionality for resource link.
     */
    @Test
    public void testAddResourceTopicRelationWithDifficultyLevelForValidInputResourceLink()
            throws DAOException, ResourceIdNotFoundException, CategoryIdNotFoundException {
        when(mockResource.getResourceOwner()).thenReturn(VALID_RESOURCE_OWNER);
        when(mockResource.getResourceStatus()).thenReturn(VALID_RESOURCE_STATUS);
        assertEquals(mockResource.getResourceLink(),
                resourceManager.addResourceCategoryRelationWithDifficultyLevel(mockResource).getResourceLink());
    }

    /**
     * Tests
     * {@link ResourceManager#addResourceCategoryRelationWithDifficultyLevel(Resource)}
     * functionality for resource name.
     */
    @Test
    public void testAddResourceTopicRelationWithDifficultyLevelForValidInputResourceName()
            throws DAOException, ResourceIdNotFoundException, CategoryIdNotFoundException {
        when(mockResource.getResourceOwner()).thenReturn(VALID_RESOURCE_OWNER);
        when(mockResource.getResourceStatus()).thenReturn(VALID_RESOURCE_STATUS);
        assertEquals(mockResource.getResourceName(),
                resourceManager.addResourceCategoryRelationWithDifficultyLevel(mockResource).getResourceName());
    }

    /**
     * Tests
     * {@link ResourceManager#addResourceCategoryRelationWithDifficultyLevel(Resource)}
     * functionality for resource description.
     */
    @Test
    public void testAddResourceTopicRelationWithDifficultyLevelForValidInputResourceDescription()
            throws DAOException, ResourceIdNotFoundException, CategoryIdNotFoundException {
        when(mockResource.getResourceOwner()).thenReturn(VALID_RESOURCE_OWNER);
        when(mockResource.getResourceStatus()).thenReturn(VALID_RESOURCE_STATUS);
        assertEquals(mockResource.getDescription(),
                resourceManager.addResourceCategoryRelationWithDifficultyLevel(mockResource).getDescription());
    }

    /**
     * Tests
     * {@link ResourceManager#addResourceCategoryRelationWithDifficultyLevel(Resource)}
     * functionality, expects {@link IllegalArgumentException} when
     * {@link Resource} is null
     */
    @Test
    public void testAddResourceCategoryRelationWithDifficultyLevelWhenResourceIsNull()
            throws ResourceIdNotFoundException, CategoryIdNotFoundException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_CANNOT_BE_NULL);
        resourceManager.addResourceCategoryRelationWithDifficultyLevel(null);
    }

    /**
     * Tests
     * {@link ResourceManager#addResourceCategoryRelationWithDifficultyLevel(Resource)}
     * functionality, expects {@link IllegalArgumentException} when Resource
     * name is Invalid
     */
    @Test
    public void testAddResourceCategoryRelationWithDifficultyLevelWhenResourceNameIsInvalid()
            throws ResourceIdNotFoundException, CategoryIdNotFoundException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_NAME_EMPTY);
        when(mockResource.getResourceName()).thenReturn(" ");
        resourceManager.addResourceCategoryRelationWithDifficultyLevel(mockResource);
    }

    /**
     * Tests
     * {@link ResourceManager#addResourceCategoryRelationWithDifficultyLevel(Resource)}
     * functionality, expects {@link IllegalArgumentException} when Resource
     * description is Invalid
     */
    @Test
    public void testAddResourceCategoryRelationWithDifficultyLevelWhenResourceDescriptionIsInvalid()
            throws ResourceIdNotFoundException, CategoryIdNotFoundException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_DESCRIPTION_INVALID);
        when(mockResource.getDescription()).thenReturn("");
        resourceManager.addResourceCategoryRelationWithDifficultyLevel(mockResource);
    }

    /**
     * Tests
     * {@link ResourceManager#addResourceCategoryRelationWithDifficultyLevel(Resource)}
     * functionality, expects {@link IllegalArgumentException} when Resource
     * link is null
     */
    @Test
    public void testAddResourceCategoryRelationWithDifficultyLevelWhenResourceLinkIsNull()
            throws ResourceIdNotFoundException, CategoryIdNotFoundException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_LINK_NULL);
        when(mockResource.getResourceLink()).thenReturn(null);
        resourceManager.addResourceCategoryRelationWithDifficultyLevel(mockResource);
    }

    /**
     * Tests
     * {@link ResourceManager#addResourceCategoryRelationWithDifficultyLevel(Resource)}
     * functionality, expects {@link IllegalArgumentException} when Resource
     * type is null
     */
    @Test
    public void testAddResourceCategoryRelationWithDifficultyLevelWhenResourceTypeIsNull()
            throws ResourceIdNotFoundException, CategoryIdNotFoundException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_TYPE_NULL);
        when(mockResource.getResourceType()).thenReturn(null);
        resourceManager.addResourceCategoryRelationWithDifficultyLevel(mockResource);
    }

    /**
     * Tests
     * {@link ResourceManager#addResourceCategoryRelationWithDifficultyLevel(Resource)}
     * functionality, expects {@link IllegalArgumentException} when
     * resourceDifficultyLevelForCategory map is null
     */
    @Test
    public void testAddResourceCategoryRelationWithDifficultyLevelWhenResourceDifficultyMapIsNull()
            throws ResourceIdNotFoundException, CategoryIdNotFoundException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(CATEGORY_DIFFICULTY_MAP_NULL);
        when(mockResource.getResourceDifficultyForCategory()).thenReturn(null);
        resourceManager.addResourceCategoryRelationWithDifficultyLevel(mockResource);
    }

    /**
     * Tests
     * {@link ResourceManager#addResourceCategoryRelationWithDifficultyLevel(Resource)}
     * functionality, expects {@link IllegalArgumentException} when
     * resourceDifficultyLevelForCategory map is empty
     */
    @Test
    public void testAddResourceTopicRelationWithDifficultyLevelWhenResourceDifficultyMapIsEmpty()
            throws ResourceIdNotFoundException, CategoryIdNotFoundException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(CATEGORY_DIFFICULTY_MAP_EMPTY);
        when(mockResource.getResourceDifficultyForCategory()).thenReturn(Collections.<Integer, Integer>emptyMap());
        resourceManager.addResourceCategoryRelationWithDifficultyLevel(mockResource);
    }

    /**
     * Tests
     * {@link ResourceManager#addResourceCategoryRelationWithDifficultyLevel(Resource)}
     * functionality, expects {@link IllegalArgumentException} when
     * Resource status is null
     */
    @Test
    public void testAddResourceTopicRelationWithDifficultyLevelWhenResourceStatusIsNull()
            throws ResourceIdNotFoundException, CategoryIdNotFoundException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_STATUS_NULL);
        when(mockResource.getResourceStatus()).thenReturn(null);
        resourceManager.addResourceCategoryRelationWithDifficultyLevel(mockResource);
    }
    
    /**
     * Tests
     * {@link ResourceManager#addResourceCategoryRelationWithDifficultyLevel(Resource)}
     * functionality, expects {@link ManagerException} when
     * {@link ResourceDAO#addResource(String, String, URL, ResourceType, String, String)} throws
     * {@link DAOException}
     */
    public void testAddResourceTopicRelationWithDifficultyLevelThrowsManagerExceptionAddingResource()
            throws MalformedURLException, DAOException, ResourceIdNotFoundException, CategoryIdNotFoundException {
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage(INSERT_RESOURCE_FAILURE);
        resource.setResourceOwner(VALID_RESOURCE_OWNER);
        resource.setResourceType(VALID_RESOURCE_TYPE);
        resource.setResourceStatus(VALID_RESOURCE_STATUS);
        when(mockResourceDAO.addResource(resource.getDescription(), resource.getResourceName(),
                resource.getResourceLink(), resource.getResourceType(), resource.getResourceOwner(), resource.getResourceStatus())).thenThrow(new DAOException());
        resourceManager.addResourceCategoryRelationWithDifficultyLevel(resource);
    }

    /**
     * Tests
     * {@link ResourceManager#addResourceCategoryRelationWithDifficultyLevel(Resource)}
     * functionality, expects {@link ManagerException} when
     * {@link ResourceCategoryRelationDAO#addResourceCategoryRelationWithDifficultyLevel(ResourceCategoryRelation)}
     * throws {@link DAOException}
     */
    public void testAddResourceTopicRelationWithDifficultyLevelThrowsManagerExceptionAddingResourceCategoryRelation()
            throws MalformedURLException, DAOException, ResourceIdNotFoundException, CategoryIdNotFoundException {
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage(CATEGORY_RELATION_ERROR);
        listOfCategories = createTestCategories();
        when(mockResource.getCategories()).thenReturn(listOfCategories);
        when(mockResource.getResourceOwner()).thenReturn(VALID_RESOURCE_OWNER);
        when(mockResource.getResourceStatus()).thenReturn(VALID_RESOURCE_STATUS);
        doThrow(new DAOException()).when(mockResourceCategoryRelationDAO)
                .addResourceCategoryRelationWithDifficultyLevel(any(ResourceCategoryRelation.class));
        resourceManager.addResourceCategoryRelationWithDifficultyLevel(mockResource);
    }

    /**
     * Tests {@link ResourceManager#getResourcesByCategoryId(int)}
     * functionality, expects {@link IllegalArgumentException} when categoryId
     * is zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesByCategoryIdWhenIdIsZero() {
        resourceManager.getResourcesByCategoryId(0);
    }

    /**
     * Verifies
     * {@link ResourceManager#getPageCountForResourcesByCategoryId(int, int)}
     * when Id of {@link Category} is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testgetPageCountForResourcesByCategoryId_WhenCategoryIdIsNegative() {
        resourceManager.getPageCountForResourcesByCategoryId(-1, RESOURCES_PER_PAGE);
    }

    /**
     * Verifies
     * {@link ResourceManager#getPageCountForResourcesByCategoryId(int, int)}
     * when Id of {@link Category} is zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testgetPageCountForResourcesByCategoryId_WhenCategoryIdIsZero() {
        resourceManager.getPageCountForResourcesByCategoryId(0, RESOURCES_PER_PAGE);
    }

    /**
     * Verifies
     * {@link ResourceManager#getPageCountForResourcesByCategoryId(int, int)}
     * produces correct output with valid input.
     */
    @Test
    public void testgetPageCountForResourcesByCategoryIdWithValidInput() throws DAOException {
        when(mockResourceDAO.getResourceCountByCategoryId(VALID_CATEGORY_ID)).thenReturn(38);
        assertEquals(4, resourceManager.getPageCountForResourcesByCategoryId(VALID_CATEGORY_ID, RESOURCES_PER_PAGE));
    }

    /**
     * Verifies
     * {@link ResourceManager#getPageCountForResourcesByCategoryId(int, int)}
     * expects {@link ManagerException} when
     * {@link ResourceDAO#getResourceCountByCategoryId(int)} throws
     * {@link DAOException}
     */
    @Test(expected = ManagerException.class)
    public void testgetPageCountForResourcesByCategoryIdForManagerException() throws DAOException {
        when(mockResourceDAO.getResourceCountByCategoryId(anyInt())).thenThrow(DAOException.class);
        resourceManager.getPageCountForResourcesByCategoryId(VALID_CATEGORY_ID, RESOURCES_PER_PAGE);
    }

    /**
     * Tests {@link ResourceManager#getSearchedResources(String)} functionality,
     * expects {@link ManagerException} when
     * {@link ResourceDAO#getSearchedResources(String)} throws
     * {@link DAOException}.
     */
    @Test(expected = ManagerException.class)
    public void testGetSearchedResourcesThrowsManagerException() throws DAOException {
        when(mockResourceDAO.getSearchedResources(VALID_RESOURCE_NAME)).thenThrow(new DAOException());
        resourceManager.getSearchedResources(VALID_RESOURCE_NAME);
    }

    /**
     * Tests {@link ResourceManager#getSearchedResources(String)} functionality
     * is called by ResourceManager.
     */
    @Test
    public void testGetSearchedResourcesForValid() throws DAOException {
        when(mockResourceDAO.getSearchedResources(VALID_RESOURCE_NAME)).thenReturn(listOfResources);
        final Resource newResource = resourceManager.getSearchedResources(VALID_RESOURCE_NAME).get(0);
        assertEquals(VALID_RESOURCE_NAME, newResource.getResourceName());
        assertEquals(VALID_RESOURCE_DESCRIPTION, newResource.getDescription());
        assertEquals(VALID_RESOURCE_ID, newResource.getResourceId());
    }

    /**
     * Tests {@link ResourceManager#getSearchedResources(String)} functionality
     * with empty search string.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetSearchedResourcesForEmpty() throws DAOException {
        resourceManager.getSearchedResources(EMPTY_STRING);
    }

    /**
     * Tests {@link ResourceManager#getSearchedResources(String)} functionality
     * with null search string.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetSearchedResourcesForNull() throws DAOException {
        resourceManager.getSearchedResources(null);
    }

    /***
     * Tests {@link ResourceManager#checkResourceExists(String)} functionality
     * when resource name is empty.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test
    public void testCheckResourceExistsWithEmptyName() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_NAME_INVALID);
        resourceManager.checkResourceExists(EMPTY_STRING);
    }

    /***
     * Tests {@link ResourceManager#checkResourceExists(String)} functionality
     * when resource name is blank.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test
    public void testCheckResourceExistsWithBlankName() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_NAME_INVALID);
        resourceManager.checkResourceExists(BLANK_STRING);
    }

    /***
     * Tests {@link ResourceManager#checkResourceExists(String)} functionality
     * when resource name is null.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test
    public void testCheckResourceExistsWithNullName() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_NAME_INVALID);
        resourceManager.checkResourceExists(null);
    }

    /***
     * Test {@link ResourceManager#checkResourceExists(String)} throws a
     * {@link ManagerException} when {@link ResourceDAO} throws a
     * {@link DAOException}
     *
     * @throws ManagerException
     *             to pass the test
     */
    @Test
    public void testCheckResourceExistsThrowsManagerException() throws DAOException {
        when(mockResourceDAO.checkResourceExists(VALID_RESOURCE_NAME)).thenThrow(new DAOException());
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage("Error checking resource name against data");
        resourceManager.checkResourceExists(VALID_RESOURCE_NAME);
    }

    /***
     * Test {@link ResourceManager#checkResourceExists(String)} calls
     * {@link ResourceDAO#checkResourceExists(String)} once
     */
    @Test
    public void testCheckResourceExistsValid() throws DAOException {
        resourceManager.checkResourceExists(VALID_RESOURCE_NAME);
        verify(mockResourceDAO, times(1)).checkResourceExists(VALID_RESOURCE_NAME);
    }

    /**
     * Test {@link ResourceManager#getAllResourceRequests()} when
     * {@link ResourceRequestDAOImpl#getAllResourceRequests()} throws
     * DAOException and verifies log message.
     */
    @Test
    public void testgetAllResourceRequestWhenDaoThrowsExceptionVerifyLogMsg() throws ManagerException, DAOException {
        doThrow(daoException).when(resourceRequestDAO).getAllResourceRequests();
        expectedException.expect(ManagerException.class);
        expectedException.expectCause(Matchers.<Throwable>equalTo(daoException));
        expectedException.expectMessage("Error retrieving all resource requests from the database");
        final TestLogger logger = TestLoggerFactory.getTestLogger(ResourceManager.class);

        try {
            resourceManager.getAllResourceRequests();
        } catch (final ManagerException managerException) {
            final List<LoggingEvent> listevents = logger.getAllLoggingEvents();
            assertEquals(uk.org.lidalia.slf4jext.Level.ERROR, listevents.get(0).getLevel());
            assertEquals("Error retrieving requested resources from the database", listevents.get(0).getMessage());
            throw managerException;
        }
    }

    /**
     * Validates {@link ResourceManager#getAllResourceRequests()} whether valid
     * list is returned when there is no error.
     */
    @Test
    public void testgetAllResourceRequestWhenNoError() throws DAOException {
        assertEquals(listOfResourceRequests, resourceManager.getAllResourceRequests());
    }

    /**
     * Expects
     * {@link ResourceManager#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the ID of the
     * {@link Resource} is zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithZeroId() {
        try {
            resourceManager.editResource(
                    0,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    VALID_TYPE,
                    VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(RESOURCE_ID_INVALID, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceManager#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the ID of the
     * {@link Resource} is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithNegativeId() {
        try {
            resourceManager.editResource(
                    -1,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    VALID_TYPE,
                    VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(RESOURCE_ID_INVALID, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceManager#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the name of the
     * {@link Resource} is <code>null</code>.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithNullResourceName() {
        try {
            resourceManager.editResource(
                    VALID_RESOURCE_ID,
                    null,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    VALID_TYPE,
                    VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(RESOURCE_NAME_NULL, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceManager#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the resourceName of the
     * {@link Resource} is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithEmptyResourceName() {
        try {
            resourceManager.editResource(
                    VALID_RESOURCE_ID,
                    EMPTY_STRING,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    VALID_TYPE,
                    VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(RESOURCE_NAME_EMPTY, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceManager#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the resourceName of the
     * {@link Resource} is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithBlankResourceName() {
        try {
            resourceManager.editResource(
                    VALID_RESOURCE_ID,
                    BLANK_STRING,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    VALID_TYPE,
                    VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(RESOURCE_NAME_EMPTY, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceManager#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the URL of the
     * {@link Resource} is <code>null</code>.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithNullResourceURL() {
        try {
            resourceManager.editResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    null,
                    VALID_DIFFICULTY_LEVEL,
                    VALID_TYPE,
                    VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(RESOURCE_LINK_NULL, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceManager#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the URL of the
     * {@link Resource} is invalid.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithInvalidResourceLink() throws MalformedURLException {
        try {
            resourceManager.editResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    new URL("ftp", "somehost", "somefile"),
                    VALID_DIFFICULTY_LEVEL,
                    VALID_TYPE,
                    VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(RESOURCE_LINK_EMPTY, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceManager#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the difficulty level of
     * the {@link Resource} is zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithZeroResourceDifficultyLevel() {
        try {
            resourceManager.editResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    0,
                    VALID_TYPE,
                    VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(RESOURCE_LEVEL_INVALID, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceManager#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the difficulty level of
     * the {@link Resource} is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithNegativeResourceDifficultyLevel() {
        try {
            resourceManager.editResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    -1,
                    VALID_TYPE,
                    VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(RESOURCE_LEVEL_INVALID, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceManager#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the type of the
     * {@link Resource} is <code>null</code>.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithNullResourceType() {
        try {
            resourceManager.editResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    null,
                    VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(RESOURCE_TYPE_NULL, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceManager#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the name of the
     * {@link Resource} is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithEmptyResourceType() {
        try {
            resourceManager.editResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    EMPTY_STRING,
                    VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(RESOURCE_TYPE_EMPTY, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceManager#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the name of the
     * {@link Resource} is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithBlankResourceType() {
        try {
            resourceManager.editResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    BLANK_STRING,
                    VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(RESOURCE_TYPE_EMPTY, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceManager#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the resource owner of the
     * {@link Resource} is <code>null</code>.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithNullResourceOwner() {
        try {
            resourceManager.editResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    VALID_TYPE,
                    null);
        } catch (final ManagerException e) {
            assertEquals(RESOURCE_TYPE_EMPTY, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceManager#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the resource owner of the
     * {@link Resource} is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithEmptyResourceOwner() {
        try {
            resourceManager.editResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    VALID_TYPE,
                    EMPTY_STRING);
        } catch (final ManagerException e) {
            assertEquals(RESOURCE_TYPE_EMPTY, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceManager#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the resource owner of the
     * {@link Resource} is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithBlankResourceOwner() {
        try {
            resourceManager.editResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    VALID_TYPE,
                    BLANK_STRING);
        } catch (final ManagerException e) {
            assertEquals(RESOURCE_TYPE_EMPTY, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceManager#editResource(int, String, URL, int, String, String)}
     * to run successfully when no exceptions are thrown.
     */
    @Test
    public void testEditResourceWithValidInputs() throws NamingException {
        PowerMockito.mockStatic(LdapReader.class);
        when(LdapReader.getInstance()).thenReturn(reader);
        when(reader.getDirContext()).thenReturn(dirContext);
        when(reader.getSearchResults(dirContext, BASE, TOKEN)).thenReturn(searchResults);
        when(searchResults.getAttributes()).thenReturn(attributes);
        when(reader.getUserDetails(attributes)).thenReturn(user);
        resourceManager.editResource(
                VALID_RESOURCE_ID,
                VALID_RESOURCE_NAME,
                STATIC_URL,
                VALID_DIFFICULTY_LEVEL,
                VALID_TYPE,
                VALID_RESOURCE_OWNER);
    }

    /**
     * Expects
     * {@link ResourceManager#editResource(int, String, URL, int, String, String)}
     * to return false when getUserID returns <code>null</code>.
     */
    @Test
    public void testEditResourceWithUserDetailsGetUserReturnsNull() throws NamingException {
        PowerMockito.mockStatic(LdapReader.class);
        when(LdapReader.getInstance()).thenReturn(reader);
        when(reader.getDirContext()).thenReturn(dirContext);
        when(reader.getSearchResults(dirContext, BASE, TOKEN)).thenReturn(searchResults);
        when(searchResults.getAttributes()).thenReturn(attributes);
        when(reader.getUserDetails(attributes)).thenReturn(user);
        when(mockUser.getUserId()).thenReturn(null);
        assertEquals(
                false,
                resourceManager.editResource(
                        VALID_RESOURCE_ID,
                        VALID_RESOURCE_NAME,
                        STATIC_URL,
                        VALID_DIFFICULTY_LEVEL,
                        VALID_TYPE,
                        VALID_RESOURCE_OWNER));
    }

    /**
     * Expects
     * {@link ResourceManager#editResource(int, String, URL, int, String, String)}
     * to return false when user is <code>null</code>.
     */
    @Test
    public void testEditResourceWithUserDetailsIsNull() throws NamingException {
        PowerMockito.mockStatic(LdapReader.class);
        when(LdapReader.getInstance()).thenReturn(reader);
        when(reader.getDirContext()).thenReturn(dirContext);
        when(reader.getSearchResults(dirContext, BASE, TOKEN)).thenReturn(searchResults);
        when(searchResults.getAttributes()).thenReturn(attributes);
        when(reader.getUserDetails(attributes)).thenReturn(null);
        assertEquals(
                false,
                resourceManager.editResource(
                        VALID_RESOURCE_ID,
                        VALID_RESOURCE_NAME,
                        STATIC_URL,
                        VALID_DIFFICULTY_LEVEL,
                        VALID_TYPE,
                        VALID_RESOURCE_OWNER));
    }

    /**
     * Expects
     * {@link ResourceManager#editResource(int, String, URL, int, String, String)}
     * to throw {@link ManagerException} when there is an error in accessing the
     * database.
     */
    @Test(expected = ManagerException.class)
    public void testeditResourceDAOExceptionByResourceDAO() throws DAOException {
        doThrow(DAOException.class).when(mockResourceDAO).updateResource(
                VALID_RESOURCE_ID,
                VALID_RESOURCE_NAME,
                STATIC_URL,
                VALID_DIFFICULTY_LEVEL,
                VALID_TYPE,
                VALID_RESOURCE_OWNER);
        try {
            resourceManager.editResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    VALID_TYPE,
                    VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(EDIT_RESORUCE_ERROR, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceManager#editResource(int, String, URL, int, String, String)}
     * to throw {@link ManagerException} when user is not a valid user.
     */
    @Test(expected = ManagerException.class)
    public void testEditResourceNamingException() throws NamingException, DAOException {
        doThrow(NamingException.class).when(mockUserDetail).getUserDetails(anyString());
        try {
            resourceManager.editResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    VALID_TYPE,
                    VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(EDIT_RESORUCE_ERROR, e.getMessage());
            throw e;
        }
    }

    /**
     * Validates that a {@link IllegalArgumentException} is thrown when invalid
     * inputs have been passed is valid but not present in database.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditByIdWithInvalidInputs() throws DAOException {
        resourceManager.editResource(0, null, null, 0, null, null);
        verify(mockResourceDAO).updateResource(0, null, null, 0, null, null);
    }

    /**
     * Helper method to initialize a list of {@link Category}.
     */

    private List<Category> createTestCategories() {
        final List<Category> categories = new ArrayList<Category>();

        for (int i = 0; i < MAX_CATEGORY_NUMBER; i++) {
            final Category temp = new Category(
                    VALID_CATEGORY_ID + i,
                    VALID_CATEGORY_NAME + i,
                    VALID_CATEGORY_DESCRIPTION + i);
            categories.add(temp);
        }
        return categories;
    }

    /**
     * Helper method to initialize a list of CategoriesIDs.
     */
    private List<Integer> createTestCategoriesIDs() {
        final List<Integer> categoriesIDs = new ArrayList<Integer>();

        for (int i = 0; i < MAX_CATEGORY_NUMBER; i++) {
            categoriesIDs.add(i);
        }
        return categoriesIDs;
    }
}
