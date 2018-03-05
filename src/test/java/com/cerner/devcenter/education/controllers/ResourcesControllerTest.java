package com.cerner.devcenter.education.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.exceptions.CategoryIdNotFoundException;
import com.cerner.devcenter.education.exceptions.DuplicateResourceTypeFoundException;
import com.cerner.devcenter.education.exceptions.ResourceIdNotFoundException;
import com.cerner.devcenter.education.managers.CategoryManager;
import com.cerner.devcenter.education.managers.EmailManager;
import com.cerner.devcenter.education.managers.ResourceCategoryRelationManager;
import com.cerner.devcenter.education.managers.ResourceManager;
import com.cerner.devcenter.education.managers.ResourceRequestManager;
import com.cerner.devcenter.education.managers.ResourceTypeManager;
import com.cerner.devcenter.education.managers.TagManager;
import com.cerner.devcenter.education.managers.UserManager;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.CategoryResourceForm;
import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.models.ResourceCategoryRelation;
import com.cerner.devcenter.education.models.ResourceRequest;
import com.cerner.devcenter.education.models.ResourceStatus;
import com.cerner.devcenter.education.models.ResourceType;
import com.cerner.devcenter.education.models.Tag;
import com.cerner.devcenter.education.models.User;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthenticationStatusUtil;
import com.cerner.devcenter.education.utils.Constants;

import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

/**
 * The class tests the functionalities of the {@link ResourcesController} class.
 *
 * @author Anudeep Kumar Gadam (AG045334)
 * @author Navya Rangeneni(NR046827)
 * @author Vincent Dasari (VD049645)
 * @author Vatsal Kesarwani(VK049896)
 * @author Rishabh Bhojak (RB048032)
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourcesControllerTest {

    private static final String PAGE_RESTRICTED_MESSAGE = "restricted_message";
    private static final String ERROR_PAGE = "error";
    private static final String MESSAGE = "message";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String SUCCESS_MESSAGE = "successMessage";
    private static final String ENTER_INFORMATION_MESSAGE = "Enter resource information";
    private static final String RESOURCES = "resources";
    private static final String CATEGORIES = "categories";
    private static final String TAGS = "tags";
    private static final String SEARCH = "search";
    private static final String RESOURCE_WITH_DIFFICULTY = "resourceWithDifficulty";
    private static final String EMPTY_STRING = "";
    private static final String BLANK_STRING = "   ";

    private static final String SEARCH_NOT_NULL = "Search string cannot be null or empty.";
    private static final String RESOURCE_INPUT_ERROR_MESSAGE = "Resouce object cannot be null";
    private static final String RESOURCE_NULL_ERROR_MESSAGE = "resource cannot be null";
    private static final String GET_ALL_CATEGORY_ERROR_MESSAGE = "Error retrieving all categories from the data source";
    private static final String GET_RESOURCE_ERROR_MESSAGE = "Error retrieving resources for a particular category";
    private static final String RESOURCE_REQUEST_NULL_ERROR_MESSAGE = "resouce request object cannot be null";
    private static final String SESSION_NULL_ERROR_MESSAGE = "session cannot be null";
    private static final String REQUEST_RESOURCE_EMPTY_ERROR = "Please select requests that are to be deleted.";
    private static final String USER_NULL_ERROR_MESSAGE = "user cannot be null";
    private static final String MANAGE_RESOURCE = "manage_resources";
    private static final String LOGIN_REDIRECT = "redirect:/login";
    private static final String ERROR_DELETING_RESOURCE_REQUESTS = "Error: unable to delete the resource request.";
    private static final String REQUEST_RESOURCE_PAGE = "request_resource";
    private static final String VALID_CATEGORY_NAME = "Java";
    private static final String VALID_CATEGORY_DESCRIPTION = "Programming Language";
    private static final String VALID_RESOURCE_DESCRIPTION = "RESOURCE DESCRIPTION";
    private static final String VALID_RESOURCE_TYPE_NAME = "Ebook";
    private static final String VALID_TAG_NAME = "commits";
    private static final String USER_DETAILS = "userDetails";
    private static final String REQUEST_RESOURCE = "request_resource";
    private static final String MANAGE_REQUESTS_PAGE = "manage_requests";
    private static final String MANAGE_RESOURCES = "manage_resources";
    private static final String VALID_RESOURCE_NAME = "resource name";
    private static final String LIST_OF_REQUESTS = "listOfRequests";
    private static final String RESOURCE_TPYES = "resourceTypes";
    private static final String REQUEST_RESOURCE_SUCCESS_MESSAGE = "Request has been sent successfully.";
    private static final String FORWARD_TO_MANAGE_REQUESTS = "forward:show_requests";
    private static final String RESOURCE_REQUEST_IDS_ERROR_MESSAGE = "resourceRequestIds cannot be null/empty";
    private static final String RESOURCE_REQUEST_DB_READ_ERROR = "Error retrieving requested resources from the database";
    private static final String SHOW_ADD_RESOURCES_ERROR_MESSAGE = "addResourcePage.errorMessage";
    private static final String VALID_RESOURCE_OWNER = "Owner";
    private static final int VALID_SELECTED_CATEGORY_ID = 1;
    private static final int VALID_CATEGORY_ID = 1;
    private static final int CATEGORY_ID_SMALLER_THAN_ZERO = -1;
    private static final int RESOURCE_TYPE_ID_SMALLER_THAN_ZERO = -2;
    private static final int VALID_RESOURCE_ID = 5;
    private static final int VALID_RESOURCE_TYPE_ID = 5;
    private static final int VALID_SELECTED_RESOURCE_TYPE_ID = 2;
    private static final int PAGE_NUMBER = 2;
    private static final int RESOURCES_PER_PAGE = 10;
    private static final int VALID_TAG_ID = 10;
    private static final int VALID_DIFFICULTY_LEVEL = 1;
    private static final int VALID_AVERAGE_RATING = 1;
    private static final int[] REQUEST_IDS = new int[] { 1, 2, 3 };
    private static final String RESOURCE_NAME_NULL = "Resource Name can not be null";
    private static final String RESOURCE_LINK_NULL = "Resource Link can not be null";
    private static final String RESOURCE_TYPE_NULL = "Resource Type can not be null";
    private static final String INVALID_RESOURCE_OWNER_ERROR_MESSAGE = "Resource owner cannot be null/empty/blank";
    private static final String RESOURCE_LINK_NULL_ERROR_MESSAGE = "Resource Link can not be null";
    private static final String INVALID_RESOURCE_ID_ERROR_MESSAGE = "Resource Id must be greater than 0";
    private static final String INVALID_RESOURCE_NAME_ERROR_MESSAGE = "Resource Name cannot be null/empty/blank";
    private static final String INVALID_RESOURCE_LINK_ERROR_MESSAGE = "Resource Link cannot be empty/blank";
    private static final String INVALID_RESOURCE_TYPE_ERROR_MESSAGE = "Resource Type cannot be null/empty/blank";
    private static final String INVALID_CATEGORY_NAME_ERROR_MESSAGE = "Category Name can not be null/empty/blank";
    private static final String INVALID_RESOURCE_LEVEL_ERROR_MESSAGE = "Resource Difficulty Level must be greater than 0";

    private static URL staticURL;
    private static ResourceBundle i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    HttpSession session;
    @InjectMocks
    private final ResourcesController resourcesController = new ResourcesController();
    @Mock
    private CategoryManager categoryManager;
    @Mock
    private AuthenticationStatusUtil authenticationStatus;
    @Mock
    private ResourceManager resourceManager;
    @Mock
    private ResourceCategoryRelationManager resourceCategoryRelationManager;
    @Mock
    private ResourceTypeManager resourceTypeManager;
    @Mock
    private BindingResult result;
    @Mock
    private DuplicateResourceTypeFoundException duplicateResourceTypeFoundException;
    @Mock
    private TagManager tagManager;
    @Mock
    private UserProfileDetails userInfo;
    @Mock
    private UserProfileDetails userInfo1;
    @Mock
    private UserManager userManager;
    @Mock
    private ResourceRequest resourceRequest;
    @Mock
    private ResourceRequestManager resourceRequestManager;
    @Mock
    private Appender mockAppender;
    @Mock
    private ManagerException managerException;
    @Mock
    private ModelAndView model;
    @Mock
    private EmailManager emailManager;

    private MockMvc mockMvc;
    private Resource resource;
    private ResourceType resourceType;
    private Category category;
    private List<Category> listOfCategories;
    private List<Resource> listOfResources;
    private List<Integer> checkedCategoriesIdsForResources;
    private Tag tag;
    private List<Tag> listOfTags;
    private ResourceCategoryRelation resourceCategoryRelation;
    private List<ResourceCategoryRelation> listOfResourceCategoryRelations;
    private List<ResourceCategoryRelation> emptyListOfResourceCategoryRelations = Collections.emptyList();

    @Before
    public void setUp() throws Exception {
        mockMvc = standaloneSetup(resourcesController).build();
        category = new Category(VALID_CATEGORY_ID, VALID_CATEGORY_NAME, VALID_CATEGORY_DESCRIPTION);
        resource = new Resource(VALID_RESOURCE_ID, staticURL, VALID_RESOURCE_DESCRIPTION);
        resourceType = new ResourceType(VALID_RESOURCE_TYPE_ID, VALID_RESOURCE_TYPE_NAME);
        resource.setResourceType(resourceType);
        staticURL = new URL("http://www.testing.com");
        checkedCategoriesIdsForResources = new ArrayList<Integer>();
        when(authenticationStatus.isLoggedIn()).thenReturn(true);
        when(result.hasErrors()).thenReturn(false);
        tag = new Tag(VALID_TAG_ID, VALID_TAG_NAME);
        listOfTags = new ArrayList<Tag>();
        listOfTags.add(tag);
        userInfo = new UserProfileDetails("test first, test last", "ADMIN", "test", "test@cerner.com", "Dev Academy",
                "test manager", "Education Evaluation");
        when(session.getAttribute(USER_DETAILS)).thenReturn(userInfo);
        when(userManager.isAdminUser(userInfo.getUserId())).thenReturn(true);
        LogManager.getRootLogger().addAppender(mockAppender);
        when(session.getAttribute(USER_DETAILS)).thenReturn(userInfo);
        when(userManager.isAdminUser(userInfo.getUserId())).thenReturn(true);
        LogManager.getRootLogger().addAppender(mockAppender);
        resourceCategoryRelation = new ResourceCategoryRelation(VALID_RESOURCE_ID, VALID_RESOURCE_NAME, staticURL,
                new ResourceType(VALID_RESOURCE_TYPE_ID, VALID_RESOURCE_TYPE_NAME), VALID_DIFFICULTY_LEVEL,
                VALID_CATEGORY_ID, VALID_CATEGORY_NAME, VALID_RESOURCE_DESCRIPTION, VALID_CATEGORY_DESCRIPTION,
                VALID_AVERAGE_RATING, VALID_RESOURCE_OWNER);
        listOfResourceCategoryRelations = Arrays.asList(resourceCategoryRelation);
    }

    @After
    public void tearDown() throws Exception {
        LogManager.getRootLogger().removeAppender(mockAppender);
    }

    /**
     * {@link ResourcesController#showAddResourcePage(Resource, HttpSession)}
     * Validates add resource page when user is not loggedIn.
     */
    @Test
    public void testAddResourceWhenNotLoggedIn() {
        when(authenticationStatus.isLoggedIn()).thenReturn(false);
        assertEquals(LOGIN_REDIRECT, resourcesController.showAddResourcePage(resource, session).getViewName());
    }

    /**
     * {@link ResourcesController#showAddResourcePage(Resource, HttpSession)}
     * Validates when DAO returns no tags.
     */
    @Test
    public void testAddResourceForTagsWhenEmpty() {
        when(tagManager.getAllTags()).thenReturn(new ArrayList<Tag>());
        assertEquals(0, tagManager.getAllTags().size());
    }

    /**
     * {@link ResourcesController#showAddResourcePage(Resource, HttpSession)}
     * Validates when DAO returns tags.
     */
    @Test
    public void testAddResourceForTags() {
        when(tagManager.getAllTags()).thenReturn(listOfTags);
        final Tag newTag = tagManager.getAllTags().get(0);
        assertEquals(VALID_TAG_ID, newTag.getTagId());
        assertEquals(VALID_TAG_NAME, newTag.getTagName());
    }

    /**
     * {@link ResourcesController#showAddResourcePage(Resource, HttpSession)}
     * Validates add resource page when admin is loggedIn.
     */
    @Test
    public void testShowAddResourceWhenIsLoggedIn() throws Exception {
        mockMvc.perform(get("/app/Show_add_resource").sessionAttr(USER_DETAILS, userInfo))
                .andExpect(view().name(MANAGE_RESOURCE));
    }

    /**
     * {@link ResourcesController#showAddResourcePage(Resource, HttpSession)}
     * Validates that the message to guide the user, is shown.
     */
    @Test
    public void testShowAddResourceDisplaysUserGuideMessage() {
        assertEquals(ENTER_INFORMATION_MESSAGE,
                resourcesController.showAddResourcePage(resource, session).getModel().get(MESSAGE));
    }

    /**
     * Expects
     * {@link ResourcesController#showAddResourcePage(Resource, HttpSession)} to
     * add the {@link Category Categories} to the {@link ModelAndView}.
     */
    @Test
    public void testShowAddResourceCategoriesModel() {
        final List<Category> categories = new ArrayList<>();
        categories.add(new Category(VALID_CATEGORY_ID));
        when(categoryManager.getAllCategories()).thenReturn(categories);
        final ModelAndView modelAndView = resourcesController.showAddResourcePage(resource, session);
        final List<Category> categoryTest = (List<Category>) modelAndView.getModel().get(CATEGORIES);
        assertEquals(categories, categoryTest);
    }

    /**
     * Expects
     * {@link ResourcesController#showAddResourcePage(Resource, HttpSession)} to
     * add the {@link ResourceType ResourceTypes} to the {@link ModelAndView}.
     */
    @Test
    public void testShowAddResourceResourceTypeModel() {
        final List<ResourceType> resourceTypes = new ArrayList<>();
        resourceTypes.add(new ResourceType(VALID_RESOURCE_TYPE_ID, VALID_RESOURCE_TYPE_NAME));
        when(resourceTypeManager.getAllResourceTypes()).thenReturn(resourceTypes);
        final ModelAndView modelAndView = resourcesController.showAddResourcePage(resource, session);
        final List<ResourceType> resourceTypeTest = (List<ResourceType>) modelAndView.getModel().get(RESOURCE_TPYES);
        assertEquals(resourceTypes, resourceTypeTest);
    }

    /**
     * Expects
     * {@link ResourcesController#showAddResourcePage(Resource, HttpSession)} to
     * add the {@link Tag Tags} to the {@link ModelAndView}.
     */
    @Test
    public void testShowAddResourceTagModel() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(new Tag(1, TAGS));
        when(tagManager.getAllTags()).thenReturn(tags);
        final ModelAndView modelAndView = resourcesController.showAddResourcePage(resource, session);
        final List<Tag> tagTest = (List<Tag>) modelAndView.getModel().get(TAGS);
        assertEquals(tags, tagTest);
    }

    /**
     * Expects
     * {@link ResourcesController#showAddResourcePage(Resource, HttpSession)} to
     * add the {@link ResourceCategoryRelation ResourceCategoryRelations} to the
     * {@link ModelAndView}.
     */
    @Test
    public void testShowAddResourceResourceCategoryRelationModel() {
        final List<ResourceCategoryRelation> allResources = new ArrayList<>();
        allResources.add(new ResourceCategoryRelation(VALID_RESOURCE_ID, VALID_RESOURCE_NAME, staticURL, resourceType,
                VALID_DIFFICULTY_LEVEL, VALID_CATEGORY_ID, VALID_CATEGORY_NAME, VALID_RESOURCE_DESCRIPTION,
                VALID_CATEGORY_DESCRIPTION, VALID_AVERAGE_RATING, VALID_RESOURCE_OWNER));
        when(resourceCategoryRelationManager.getResourcesForAllCategories()).thenReturn(allResources);
        final ModelAndView modelAndView = resourcesController.showAddResourcePage(resource, session);
        final List<ResourceCategoryRelation> allResourcesTest = (List<ResourceCategoryRelation>) modelAndView.getModel()
                .get(RESOURCE_WITH_DIFFICULTY);
        assertEquals(allResources, allResourcesTest);
    }

    /**
     * Expects
     * {@link ResourcesController#showAddResourcePage(Resource, HttpSession)} to
     * add an error message to the model when there's an error in retrieving all
     * the {@link Category Categories} from the database.
     */
    @Test
    public void testShowAddResourceWhenCategoryManagerThrowsExceptionOnGetAllCategories() {
        when(categoryManager.getAllCategories()).thenThrow(managerException);
        final ModelAndView modelAndView = resourcesController.showAddResourcePage(resource, session);
        assertEquals(i18nBundle.getString(SHOW_ADD_RESOURCES_ERROR_MESSAGE),
                modelAndView.getModel().get(ERROR_MESSAGE));
        assertEquals(MANAGE_RESOURCES, modelAndView.getViewName());
    }

    /**
     * Expects
     * {@link ResourcesController#showAddResourcePage(Resource, HttpSession)} to
     * add an error message to the model when there's an error in retrieving all
     * the {@link ResourceType ResourceTypes} from the database.
     */
    @Test
    public void testShowAddResourceWhenResourceTypeManagerThrowsExceptionOnGetAllResourceTypes() {
        when(resourceTypeManager.getAllResourceTypes()).thenThrow(managerException);
        final ModelAndView modelAndView = resourcesController.showAddResourcePage(resource, session);
        assertEquals(i18nBundle.getString(SHOW_ADD_RESOURCES_ERROR_MESSAGE),
                modelAndView.getModel().get(ERROR_MESSAGE));
        assertEquals(MANAGE_RESOURCES, modelAndView.getViewName());
    }

    /**
     * Expects
     * {@link ResourcesController#showAddResourcePage(Resource, HttpSession)} to
     * add an error message to the model when there's an error in retrieving all
     * the {@link Tag Tags} from the database.
     */
    @Test
    public void testShowAddResourceWhenTagManagerThrowsExceptionOnGetAllTags() {
        when(tagManager.getAllTags()).thenThrow(managerException);
        final ModelAndView modelAndView = resourcesController.showAddResourcePage(resource, session);
        assertEquals(i18nBundle.getString(SHOW_ADD_RESOURCES_ERROR_MESSAGE),
                modelAndView.getModel().get(ERROR_MESSAGE));
        assertEquals(MANAGE_RESOURCES, modelAndView.getViewName());
    }

    /**
     * Expects
     * {@link ResourcesController#showAddResourcePage(Resource, HttpSession)} to
     * add an error message to the model when there's an error in retrieving all
     * the {@link ResourceCategoryRelation ResourceCategoryRelations} from the
     * database.
     */
    @Test
    public void testShowAddResourceWhenResourceCategoryRelationManagerThrowsExceptionOnGetAllResourcesForAllCategories() {
        when(resourceCategoryRelationManager.getResourcesForAllCategories()).thenThrow(managerException);
        final ModelAndView modelAndView = resourcesController.showAddResourcePage(resource, session);
        assertEquals(i18nBundle.getString(SHOW_ADD_RESOURCES_ERROR_MESSAGE),
                modelAndView.getModel().get(ERROR_MESSAGE));
        assertEquals(MANAGE_RESOURCES, modelAndView.getViewName());
    }

    /**
     * {@link ResourcesController#addResource(Resource, BindingResult, HttpSession)}
     * Validates that the message to guide the user is shown.
     */
    @Test
    public void testAddResourceDisplaysUserGuideMessage()
            throws ResourceIdNotFoundException, CategoryIdNotFoundException {
        assertEquals(ENTER_INFORMATION_MESSAGE,
                resourcesController.addResource(resource, result, session).getModel().get(MESSAGE));
    }

    /**
     * {@link ResourcesController#addResource(Resource, BindingResult, HttpSession)}
     * Validates add resource page when user is loggedIn.
     */
    @Test
    public void testAddResourceWhenIsLoggedIn() throws Exception {
        final String returnedViewName = resourcesController.addResource(resource, result, session).getViewName();
        assertEquals(MANAGE_RESOURCE, returnedViewName);
    }

    /**
     * {@link ResourcesController#addResource(Resource, BindingResult, HttpSession)}
     * Expects {@link ResourceIdNotFoundException} and
     * {@link CategoryIdNotFoundException} when there are binding errors.
     */
    @Test
    public void testAddResourceWhenHasBindingErrors() throws ResourceIdNotFoundException, CategoryIdNotFoundException {
        when(result.hasErrors()).thenReturn(true);
        assertEquals(i18nBundle.getString("com.cerner.devcenter.education.controllers.bindingResultError"),
                resourcesController.addResource(resource, result, session).getModel().get(ERROR_MESSAGE));
    }

    /**
     * {@link ResourcesController#addResource(Resource, BindingResult, HttpSession)}
     * Validates add resource page when non admin User.
     */
    @Test
    public void testAddResourceForNonAdminUser() throws ResourceIdNotFoundException, CategoryIdNotFoundException {
        when(userManager.isAdminUser(userInfo.getUserId())).thenReturn(false);
        assertEquals(MANAGE_RESOURCE, resourcesController.addResource(resource, result, session).getViewName());
    }

    /**
     * Tests
     * {@link ResourcesController#addResource(Resource, BindingResult, HttpSession)}
     * for binding the userId to the resourceOwner
     */
    @Test
    public void testAddResourceforResourceOwner() throws ResourceIdNotFoundException, CategoryIdNotFoundException {
        resourcesController.addResource(resource, result, session);
        assertEquals(userInfo.getUserId(), resource.getResourceOwner());
    }

    /**
     * {@link ResourcesController#addResource(Resource, BindingResult, HttpSession)}
     * Expects {@link IllegalArgumentException} when {@link Resource} object is
     * <code>null</code>.
     */
    @Test
    public void testAddResourceWhenResourceIsNull() throws ResourceIdNotFoundException, CategoryIdNotFoundException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_NULL_ERROR_MESSAGE);
        resourcesController.addResource(null, result, session);
    }

    /**
     * {@link ResourcesController#addResource(Resource, BindingResult, HttpSession)}
     * When a Category ID can't be retrieved, expects {@link ManagerException}
     */
    @Test(expected = ManagerException.class)
    public void testAddResourceWhenCheckedCatIdEmpty() throws ResourceIdNotFoundException, CategoryIdNotFoundException {
        checkedCategoriesIdsForResources.add(VALID_CATEGORY_ID);
        when(resourceTypeManager.getResourceTypeById(VALID_RESOURCE_TYPE_ID)).thenReturn(resourceType);
        when(resourceTypeManager.getResourceTypeById(VALID_RESOURCE_TYPE_ID)).thenReturn(resourceType);
        resource.setCheckedCategoriesIDs(checkedCategoriesIdsForResources);
        doThrow(ManagerException.class).when(categoryManager).getCategoryById(VALID_CATEGORY_ID);
        resourcesController.addResource(resource, result, session);
    }

    /**
     * {@link ResourcesController#addResource(Resource, BindingResult, HttpSession)}
     * When a resource is passed in with a valid Category ID is checked.
     */
    @Test
    public void testAddResourceWhenCheckedCatIdValid() throws ResourceIdNotFoundException, CategoryIdNotFoundException {
        checkedCategoriesIdsForResources.add(VALID_CATEGORY_ID);
        resource.setCheckedCategoriesIDs(checkedCategoriesIdsForResources);
        when(categoryManager.getCategoryById(VALID_CATEGORY_ID)).thenReturn(category);
        when(resourceTypeManager.getResourceTypeById(VALID_RESOURCE_TYPE_ID)).thenReturn(resourceType);
        assertEquals(i18nBundle.getString("addResourcePage.successMessage"),
                resourcesController.addResource(resource, result, session).getModel().get(SUCCESS_MESSAGE));
    }

    /**
     * Tests
     * {@link ResourcesController#addResource(Resource, BindingResult, HttpSession)}
     * for binding the Available resource status to the resource for admin
     */
    @Test
    public void testAddResourceforResourceStatusForAdmin()
            throws ResourceIdNotFoundException, CategoryIdNotFoundException {
        resourcesController.addResource(resource, result, session);
        assertEquals(ResourceStatus.Available.toString(), resource.getResourceStatus());
    }

    /**
     * Tests
     * {@link ResourcesController#addResource(Resource, BindingResult, HttpSession)}
     * for binding the Pending resource status to the resource for non admin
     * user
     */
    @Test
    public void testAddResourceforResourceStatusForNonAdminUser()
            throws ResourceIdNotFoundException, CategoryIdNotFoundException {
        when(userManager.isAdminUser(userInfo.getUserId())).thenReturn(false);
        resourcesController.addResource(resource, result, session);
        assertEquals(ResourceStatus.Pending.toString(), resource.getResourceStatus());
    }

    /**
     * {@link ResourcesController#listCategoryResources()} Verifies the method
     * when the request is made to get a list of categories, the URL is mapped
     * to the correct controller method and the returned JSON document is
     * correct.
     */
    @Test
    public void testListCategoryResources() throws Exception {
        listOfCategories = new ArrayList<Category>();
        listOfCategories.add(category);
        when(categoryManager.getAllCategories()).thenReturn(listOfCategories);
        mockMvc.perform(get("/app/categories").accept("application/json")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].id", is(VALID_CATEGORY_ID)))
                .andExpect(jsonPath("$[0].name", is(VALID_CATEGORY_NAME)))
                .andExpect(jsonPath("$[0].description", is(VALID_CATEGORY_DESCRIPTION)));
    }

    /**
     * {@link ResourcesController#listCategoryResources()} verify that when
     * {@link ManagerException} is thrown at lower level, the method would also
     * propagate the Exception.
     */
    @Test
    public void testListCategoryResourcesErrorHandling() {
        doThrow(new ManagerException(GET_ALL_CATEGORY_ERROR_MESSAGE, new DAOException())).when(categoryManager)
                .getAllCategories();
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage(GET_ALL_CATEGORY_ERROR_MESSAGE);
        resourcesController.listCategoryResources();
    }

    /**
     * {@link ResourcesController#findResources(Integer)} Verify that when the
     * request is made to get a category id associated resources, the URL is
     * mapped to the correct controller method and the returned JSON document is
     * correct.
     */
    @Test
    public void testfindResources() throws Exception {
        listOfResources = new ArrayList<Resource>();
        listOfResources.add(resource);
        when(resourceManager.getResourcesByCategoryId(category.getId())).thenReturn(listOfResources);
        mockMvc.perform(get("/app/categories/" + category.getId()).accept("application/json"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].resourceId", is(VALID_RESOURCE_ID)))
                .andExpect(jsonPath("$[0].resourceLink", is(staticURL.toString())))
                .andExpect(jsonPath("$[0].description", is(VALID_RESOURCE_DESCRIPTION)));
    }

    /**
     * {@link ResourcesController#findResources(Integer)} Verify that when
     * {@link ManagerException} is thrown at lower level, the method would also
     * propagate the Exception.
     */
    @Test
    public void testFindResourcesErrorHandling() {
        doThrow(new ManagerException(GET_RESOURCE_ERROR_MESSAGE, new DAOException())).when(resourceManager)
                .getResourcesByCategoryId(anyInt());
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage(GET_RESOURCE_ERROR_MESSAGE);
        resourcesController.findResources(resource.getResourceId());
    }

    /**
     * {@link ResourcesController#findResources(Integer)} Expects
     * {@link IllegalArgumentException} when category id is a negative integer.
     */
    @Test
    public void testFindResourcesCategoryIdNegative() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("category ID must be a positive integer");
        resourcesController.findResources(CATEGORY_ID_SMALLER_THAN_ZERO);
    }

    /**
     * {@link ResourcesController#findResources(Integer)} Expects
     * {@link IllegalArgumentException} when category ID is invalid.
     */
    @Test
    public void testFindResourcesCategoryIdZERO() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("category ID must be a positive integer");
        resourcesController.findResources(0);
    }

    /**
     * Verify when the request is made to get a list of categories, the URL is
     * mapped to the correct controller method and the resulting view is
     * matched.
     */
    @Test
    public void testShowCategoryResourcesPath() throws Exception {
        mockMvc.perform(get("/app/categoriesForResources")).andExpect(view().name(RESOURCES));
    }

    /**
     * Verify when the request is made to fetch the actual resources, the URL is
     * mapped to the correct controller method and the resulting view is
     * matched.
     */
    @Test
    public void testShowResourcesPath() throws Exception {
        mockMvc.perform(get("/app/Resources?selectedCategoryID=" + VALID_CATEGORY_ID + "&selectedResourceTypeID="
                + VALID_SELECTED_RESOURCE_TYPE_ID + "&resourcesperpage=" + RESOURCES_PER_PAGE))
                .andExpect(view().name(RESOURCES));
    }

    /**
     * Validates the show category page when user is not logged in.
     */
    @Test
    public void testShowCategoryResourcesWhenNotLoggedIn() {
        when(authenticationStatus.isLoggedIn()).thenReturn(false);
        assertEquals(LOGIN_REDIRECT, resourcesController.showCategoryResources().getViewName());
    }

    /**
     * Expects {@link IllegalArgumentException} to be thrown when the selected
     * category ID for {@link CategoryResourceForm} is zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testShowResourcesWhenZeroCategoryIdSelected() {
        final CategoryResourceForm categoryResourceMapper = new CategoryResourceForm(0,
                VALID_SELECTED_RESOURCE_TYPE_ID);
        resourcesController.showResources(categoryResourceMapper, PAGE_NUMBER, RESOURCES_PER_PAGE);
    }

    /**
     * When selected category ID is valid and resource type is 0 (that means
     * user selected all types) for {@link CategoryResourceForm}, the user
     * should be able to view the resources page.
     */
    @Test
    public void testShowResourcesWhenResourceTypeSelectedIsAny() {
        final CategoryResourceForm categoryResourceMapper = new CategoryResourceForm(VALID_SELECTED_CATEGORY_ID, 0);
        assertEquals(RESOURCES, resourcesController
                .showResources(categoryResourceMapper, PAGE_NUMBER, RESOURCES_PER_PAGE).getViewName());
    }

    /**
     * Expects {@link IllegalArgumentException} to be thrown when selected
     * resource type ID is negative for {@link CategoryResourceForm}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testShowResourcesWhenResourceTypeSelectedIsNegative() {
        final CategoryResourceForm categoryResourceMapper = new CategoryResourceForm(VALID_SELECTED_CATEGORY_ID,
                RESOURCE_TYPE_ID_SMALLER_THAN_ZERO);
        assertEquals(RESOURCES, resourcesController
                .showResources(categoryResourceMapper, PAGE_NUMBER, RESOURCES_PER_PAGE).getViewName());
    }

    /**
     * Expects {@link IllegalArgumentException} to be thrown when selected
     * category ID is negative for {@link CategoryResourceForm}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testShowResourcesWhenCategorySelectedIsNegative() {
        final CategoryResourceForm categoryResourceMapper = new CategoryResourceForm(CATEGORY_ID_SMALLER_THAN_ZERO,
                VALID_SELECTED_RESOURCE_TYPE_ID);
        assertEquals(RESOURCES, resourcesController
                .showResources(categoryResourceMapper, PAGE_NUMBER, RESOURCES_PER_PAGE).getViewName());
    }

    /**
     * Expects {@link IllegalArgumentException} to be thrown when the ID of the
     * {@link Category} is zero.It passes method under test a {@link Category}
     * object with an ID of zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testShowResourcesWithZeroCategoryId() {
        final CategoryResourceForm categoryResourceMapper = new CategoryResourceForm(CATEGORY_ID_SMALLER_THAN_ZERO,
                VALID_SELECTED_RESOURCE_TYPE_ID);
        resourcesController.showResources(categoryResourceMapper, PAGE_NUMBER, RESOURCES_PER_PAGE);
    }

    /**
     * Checks whether a message is added to the {@link ModelAndView} when the
     * list of Resources ( {@link Resource}) is empty.
     */
    @Test
    public void testShowAllResourcesPageWithEmptyResourceList() {
        when(resourceManager.getResourcesByCategoryId(VALID_CATEGORY_ID)).thenReturn(new ArrayList<Resource>());
        final CategoryResourceForm categoryResourceMapper = new CategoryResourceForm(VALID_SELECTED_CATEGORY_ID,
                VALID_SELECTED_RESOURCE_TYPE_ID);
        final ModelAndView returnModel = resourcesController.showResources(categoryResourceMapper, PAGE_NUMBER,
                RESOURCES_PER_PAGE);
        assertEquals("resources", returnModel.getViewName());
        assertEquals(i18nBundle.getString("com.cerner.devcenter.education.controllers.errorNoResource"),
                returnModel.getModel().get(ERROR_MESSAGE));
    }

    /**
     * {@link ResourcesController#addResourceType(String)} Expects
     * {@link IllegalArgumentException} when the resource type is
     * <code>null</code>.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddResourceTypeWhenResourceTypeIsNull() {
        resourcesController.addResourceType(null);
    }

    /**
     * {@link ResourcesController#addResourceType(String)} Expects
     * {@link IllegalArgumentException} when the resource type empty
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddResourceTypeWhenResourceTypeIsEmpty() {
        resourcesController.addResourceType("");
    }

    /**
     * {@link ResourcesController#addResourceType(String)} When the resource
     * type is valid.
     */
    @Test
    public void testAddResourceTypeWhenResourceTypeIsValid() {
        when(resourceTypeManager.addResourceType(VALID_RESOURCE_TYPE_NAME)).thenReturn(resourceType);
        assertEquals(resourceType.getResourceType(),
                resourcesController.addResourceType(VALID_RESOURCE_TYPE_NAME).getResourceType());
    }

    /**
     * {@link ResourcesController#addResourceType(String)} When the resource
     * type already exists, the resource type name of the {@link ResourceType}
     * object should be <code>null</code>.
     */
    @Test
    public void testAddResourceTypeWhenResourceTypeAlreadyExists() {
        when(resourceTypeManager.addResourceType(VALID_RESOURCE_TYPE_NAME))
                .thenThrow(duplicateResourceTypeFoundException);
        assertNull(resourcesController.addResourceType(VALID_RESOURCE_TYPE_NAME).getResourceType());
    }

    /**
     * {@link ResourcesController#deleteResource(int)} Expects
     * {@link IllegalArgumentException} when the ID of the {@link Resource} is
     * zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteResourcesWithZeroResourceId() {
        resourcesController.deleteResource(0);
    }

    /**
     * {@link ResourcesController#deleteResource(int)} Expects
     * {@link IllegalArgumentException} when the ID of the {@link Resource} is
     * less than zero.
     */
    @Test(expected = IllegalArgumentException.class)

    public void testDeleteResourcesWithNegativeResourceId() {
        resourcesController.deleteResource(-1);
    }

    /**
     * {@link ResourcesController#deleteResource(int)} Checks whether false is
     * returned if an exception is thrown at
     * {@link ResourceManager#deleteResource(int)}
     */
    @Test
    public void testDeleteResourcesWithException() {
        doThrow(ManagerException.class).when(resourceManager).deleteResource(VALID_RESOURCE_ID);
        assertEquals(false, resourcesController.deleteResource(VALID_RESOURCE_ID));
    }

    /**
     * {@link ResourcesController#deleteResource(int)} Checks whether True is
     * returned when valid ID is passed.
     */
    @Test
    public void testDeleteResourceReturnsCorrectValue() {
        assertEquals(true, resourcesController.deleteResource(VALID_RESOURCE_ID));
    }

    /**
     * Verifies {@link ResourcesController#showCategoryResources()} when the
     * request is made to search the resources, the URL is mapped to the correct
     * controller method and the resulting view is matched.
     */
    @Test
    public void testShowSearchResourcesPathIsCorrect() throws Exception {
        mockMvc.perform(get("/app/searchResources")).andExpect(view().name(SEARCH));
    }

    /**
     * Checks page when user is not logged in.
     */
    @Test
    public void testSearchResourcesWhenNotLoggedIn() {
        when(authenticationStatus.isLoggedIn()).thenReturn(false);
        final ResourceCategoryRelation resourceCategoryRelation = new ResourceCategoryRelation(VALID_RESOURCE_ID,
                VALID_RESOURCE_NAME, staticURL, resourceType, VALID_CATEGORY_ID, VALID_CATEGORY_NAME,
                VALID_RESOURCE_DESCRIPTION, VALID_CATEGORY_DESCRIPTION);
        assertEquals(LOGIN_REDIRECT, resourcesController.showSearchResources(resourceCategoryRelation).getViewName());
    }

    /**
     * Tests {@link ResourcesController#autocomplete(String)} auto-complete when
     * search string is <code>null</code>.
     */
    @Test
    public void testAutocompleteWithNullSearchString() throws DAOException, MalformedURLException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(SEARCH_NOT_NULL);
        resourcesController.autocomplete(null);
    }

    /**
     * Tests {@link ResourcesController#autocomplete(String)} auto-complete when
     * search string is empty.
     */
    @Test
    public void testAutocompleteWithEmptyEmptySearchString() throws DAOException, MalformedURLException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(SEARCH_NOT_NULL);
        resourcesController.autocomplete(EMPTY_STRING);
    }

    /**
     * Tests {@link ResourcesController#autocomplete(String)} auto-complete when
     * search returns empty results.
     */
    @Test
    public void testAutocompleteWhenGetSearchedResourcesReturnsEmpty() throws DAOException, MalformedURLException {
        final List<Resource> emptyResources = new ArrayList<>();
        when(resourceManager.getSearchedResources(SEARCH)).thenReturn(emptyResources);
        resourcesController.autocomplete(SEARCH);
        assertEquals(0, resourcesController.autocomplete(SEARCH).size());
    }

    /**
     * Tests {@link ResourcesController#autocomplete(String)} auto-complete when
     * search returns less than ten resources.
     */
    @Test
    public void testAutocompleteWhenGetSearchedResourcesReturnsLessThanTen()
            throws DAOException, MalformedURLException {
        final List<Resource> resources = new ArrayList<>();
        resources.add(new Resource(1, staticURL, "google", "google"));
        when(resourceManager.getSearchedResources(SEARCH)).thenReturn(resources);
        resourcesController.autocomplete(SEARCH);
        assertEquals(1, resourcesController.autocomplete(SEARCH).size());
    }

    /**
     * Tests {@link ResourcesController#autocomplete(String)} auto-complete when
     * search returns ten resources.
     */
    @Test
    public void testAutocompleteWhenGetSearchedResourcesReturnsTenRecords() throws DAOException, MalformedURLException {
        final List<Resource> resources = new ArrayList<>();
        resources.add(new Resource(1, staticURL, "google1", "google"));
        resources.add(new Resource(2, staticURL, "google2", "google"));
        resources.add(new Resource(3, staticURL, "google3", "google"));
        resources.add(new Resource(4, staticURL, "google4", "google"));
        resources.add(new Resource(5, staticURL, "google5", "google"));
        resources.add(new Resource(6, staticURL, "google6", "google"));
        resources.add(new Resource(7, staticURL, "google7", "google"));
        resources.add(new Resource(8, staticURL, "google8", "google"));
        resources.add(new Resource(9, staticURL, "google9", "google"));
        resources.add(new Resource(10, staticURL, "google10", "google"));
        when(resourceManager.getSearchedResources(SEARCH)).thenReturn(resources);
        resourcesController.autocomplete(SEARCH);
        assertEquals(10, resourcesController.autocomplete(SEARCH).size());
    }

    /**
     * Tests {@link ResourcesController#autocomplete(String)} auto-complete when
     * search returns ten resources for the nearest resource.
     */
    @Test
    public void testAutocompleteWhenGetSearchedResourcesReturnsTenRecordsForTopSearch()
            throws DAOException, MalformedURLException {
        final List<Resource> resources = new ArrayList<>();
        resources.add(new Resource(1, staticURL, "google1", "google"));
        resources.add(new Resource(2, staticURL, "google2", "google"));
        resources.add(new Resource(3, staticURL, "google3", "google"));
        resources.add(new Resource(4, staticURL, "google4", "google"));
        resources.add(new Resource(5, staticURL, "google5", "google"));
        resources.add(new Resource(6, staticURL, "google6", "google"));
        resources.add(new Resource(7, staticURL, "google7", "google"));
        resources.add(new Resource(8, staticURL, "google8", "google"));
        resources.add(new Resource(9, staticURL, "google9", "google"));
        resources.add(new Resource(10, staticURL, "google10", "google"));
        when(resourceManager.getSearchedResources(SEARCH)).thenReturn(resources);
        final List<Resource> actualResources = resourcesController.autocomplete(SEARCH);
        assertTrue("google:google1"
                .equals(actualResources.get(0).getResourceName() + ":" + actualResources.get(0).getDescription()));
    }

    /**
     * Tests {@link ResourcesController#autocomplete(String)} auto-complete when
     * search returns ten resources for the farthest resource.
     */
    @Test
    public void testAutocompleteWhenGetSearchedResourcesReturnsTenRecordsForLastSearch()
            throws DAOException, MalformedURLException {
        final List<Resource> resources = new ArrayList<>();
        resources.add(new Resource(1, staticURL, "google1", "google"));
        resources.add(new Resource(2, staticURL, "google2", "google"));
        resources.add(new Resource(3, staticURL, "google3", "google"));
        resources.add(new Resource(4, staticURL, "google4", "google"));
        resources.add(new Resource(5, staticURL, "google5", "google"));
        resources.add(new Resource(6, staticURL, "google6", "google"));
        resources.add(new Resource(7, staticURL, "google7", "google"));
        resources.add(new Resource(8, staticURL, "google8", "google"));
        resources.add(new Resource(9, staticURL, "google9", "google"));
        resources.add(new Resource(10, staticURL, "google10", "google"));
        when(resourceManager.getSearchedResources(SEARCH)).thenReturn(resources);
        final List<Resource> actualResources = resourcesController.autocomplete(SEARCH);
        assertTrue("google:google10"
                .equals(actualResources.get(9).getResourceName() + ":" + actualResources.get(9).getDescription()));
    }

    /**
     * Tests {@link ResourcesController#autocomplete(String)} auto-complete when
     * search returns more then ten resources to return ten closest resources.
     */
    @Test
    public void testAutocompleteWhenGetSearchedResourcesReturnsMoreThanTen()
            throws DAOException, MalformedURLException {
        final List<Resource> resources = new ArrayList<>();
        resources.add(new Resource(1, staticURL, "google1", "google"));
        resources.add(new Resource(2, staticURL, "google2", "google"));
        resources.add(new Resource(3, staticURL, "google3", "google"));
        resources.add(new Resource(4, staticURL, "google4", "google"));
        resources.add(new Resource(5, staticURL, "google5", "google"));
        resources.add(new Resource(6, staticURL, "google6", "google"));
        resources.add(new Resource(7, staticURL, "google7", "google"));
        resources.add(new Resource(8, staticURL, "google8", "google"));
        resources.add(new Resource(9, staticURL, "google9", "google"));
        resources.add(new Resource(10, staticURL, "google10", "google"));
        resources.add(new Resource(11, staticURL, "google11", "google"));
        when(resourceManager.getSearchedResources(SEARCH)).thenReturn(resources);
        resourcesController.autocomplete(SEARCH);
        assertEquals(Constants.AUTOFILL_SIZE, resourcesController.autocomplete(SEARCH).size());
    }

    /**
     * Tests {@link ResourcesController#autocomplete(String)} auto-complete when
     * search returns more then ten resources for the closest resource.
     */
    @Test
    public void testAutocompleteWhenGetSearchedResourcesReturnsMoreThanTopSearch()
            throws DAOException, MalformedURLException {
        final List<Resource> resources = new ArrayList<>();
        resources.add(new Resource(1, staticURL, "google1", "google"));
        resources.add(new Resource(2, staticURL, "google2", "google"));
        resources.add(new Resource(3, staticURL, "google3", "google"));
        resources.add(new Resource(4, staticURL, "google4", "google"));
        resources.add(new Resource(5, staticURL, "google5", "google"));
        resources.add(new Resource(6, staticURL, "google6", "google"));
        resources.add(new Resource(7, staticURL, "google7", "google"));
        resources.add(new Resource(8, staticURL, "google8", "google"));
        resources.add(new Resource(9, staticURL, "google9", "google"));
        resources.add(new Resource(10, staticURL, "google10", "google"));
        resources.add(new Resource(11, staticURL, "google11", "google"));
        when(resourceManager.getSearchedResources(SEARCH)).thenReturn(resources);
        final List<Resource> actualResources = resourcesController.autocomplete(SEARCH);
        assertTrue("google:google1"
                .equals(actualResources.get(0).getResourceName() + ":" + actualResources.get(0).getDescription()));
    }

    /**
     * Tests {@link ResourcesController#autocomplete(String)} auto-complete when
     * search returns more then ten resources for the closest resource.
     */
    @Test
    public void testAutocompleteWhenGetSearchedResourcesReturnsMoreThanTenForLastSearch()
            throws DAOException, MalformedURLException {
        final List<Resource> resources = new ArrayList<>();
        resources.add(new Resource(1, staticURL, "google1", "google"));
        resources.add(new Resource(2, staticURL, "google2", "google"));
        resources.add(new Resource(3, staticURL, "google3", "google"));
        resources.add(new Resource(4, staticURL, "google4", "google"));
        resources.add(new Resource(5, staticURL, "google5", "google"));
        resources.add(new Resource(6, staticURL, "google6", "google"));
        resources.add(new Resource(7, staticURL, "google7", "google"));
        resources.add(new Resource(8, staticURL, "google8", "google"));
        resources.add(new Resource(9, staticURL, "google9", "google"));
        resources.add(new Resource(10, staticURL, "google10", "google"));
        resources.add(new Resource(11, staticURL, "google11", "google"));
        when(resourceManager.getSearchedResources(SEARCH)).thenReturn(resources);
        final List<Resource> actualResources = resourcesController.autocomplete(SEARCH);
        assertTrue("google:google10"
                .equals(actualResources.get(9).getResourceName() + ":" + actualResources.get(9).getDescription()));
    }

    /**
     * Test {@link ResourcesController#autocomplete(String)} results for sorting
     * order.
     */
    @Test
    public void testAutocompleteResultsSortingOrder() throws MalformedURLException {
        final List<Resource> resources = new ArrayList<>();
        resources.add(new Resource(1, staticURL, "programming language", "java"));
        resources.add(new Resource(2, staticURL, "commands", "linux"));
        resources.add(new Resource(3, staticURL, "language", "hava"));
        resources.add(new Resource(4, staticURL, "framework", "spring"));
        resources.add(new Resource(5, staticURL, "mobile", "jquery"));
        resources.add(new Resource(6, staticURL, "javascript", "java"));
        resources.add(new Resource(7, staticURL, "programming language", "pyhton"));
        resources.add(new Resource(8, staticURL, "java", "Core"));
        resources.add(new Resource(9, staticURL, "mobile", "dojo"));
        resources.add(new Resource(10, staticURL, "javascript", "java"));

        when(resourceManager.getSearchedResources("java")).thenReturn(resources);
        final List<Resource> actualResources = resourcesController.autocomplete("java");

        // Distance for name -> 0, Distance for description -> 6
        assertTrue("java:javascript"
                .equals(actualResources.get(0).getResourceName() + ":" + actualResources.get(0).getDescription()));
        // Distance for name -> 0, Distance for description -> 6
        assertTrue("java:javascript"
                .equals(actualResources.get(1).getResourceName() + ":" + actualResources.get(1).getDescription()));
        // Distance for name -> 0, Distance for description -> 18
        assertTrue("java:programming language"
                .equals(actualResources.get(2).getResourceName() + ":" + actualResources.get(2).getDescription()));
        // Distance for name -> 1, Distance for description -> 6
        assertTrue("hava:language"
                .equals(actualResources.get(3).getResourceName() + ":" + actualResources.get(3).getDescription()));
        // Distance for name -> 4, Distance for description -> 0
        assertTrue("Core:java"
                .equals(actualResources.get(4).getResourceName() + ":" + actualResources.get(4).getDescription()));
        // Distance for name -> 4, Distance for description -> 6
        assertTrue("dojo:mobile"
                .equals(actualResources.get(5).getResourceName() + ":" + actualResources.get(5).getDescription()));
        // Distance for name -> 5, Distance for description -> 6
        assertTrue("jquery:mobile"
                .equals(actualResources.get(6).getResourceName() + ":" + actualResources.get(6).getDescription()));
        // Distance for name -> 5, Distance for description -> 7
        assertTrue("linux:commands"
                .equals(actualResources.get(7).getResourceName() + ":" + actualResources.get(7).getDescription()));
        // Distance for name -> 6, Distance for description -> 8
        assertTrue("spring:framework"
                .equals(actualResources.get(8).getResourceName() + ":" + actualResources.get(8).getDescription()));
        // Distance for name -> 6, Distance for description -> 18
        assertTrue("pyhton:programming language"
                .equals(actualResources.get(9).getResourceName() + ":" + actualResources.get(9).getDescription()));
    }

    /**
     * Test {@link ResourcesController#autocomplete(String)} results for sorting
     * order when a blank string is entered.
     */
    @Test
    public void testAutoCompleteResultsSortingOrderWithBlankString() throws MalformedURLException {
        final List<Resource> resources = new ArrayList<>();
        resources.add(new Resource(1, staticURL, " programming language", "java"));
        resources.add(new Resource(2, staticURL, " commands", "linux"));
        resources.add(new Resource(3, staticURL, " language", "hava"));
        resources.add(new Resource(4, staticURL, " framework", "spring"));
        resources.add(new Resource(5, staticURL, " mobile", "jquery"));
        resources.add(new Resource(6, staticURL, " javascript", "java"));
        resources.add(new Resource(7, staticURL, " programming language", "pyhton"));
        resources.add(new Resource(8, staticURL, " java", "Core"));
        resources.add(new Resource(9, staticURL, " mobile", "dojo"));
        resources.add(new Resource(10, staticURL, " javascript", "java"));

        when(resourceManager.getSearchedResources(" ")).thenReturn(resources);
        final List<Resource> actualResources = resourcesController.autocomplete(" ");

        // Distance for name -> 4, Distance for description -> 4
        assertTrue("Core: java"
                .equals(actualResources.get(0).getResourceName() + ":" + actualResources.get(0).getDescription()));
        // Distance for name -> 4, Distance for description -> 5
        assertTrue("dojo: mobile"
                .equals(actualResources.get(1).getResourceName() + ":" + actualResources.get(1).getDescription()));
        // Distance for name -> 4, Distance for description -> 8
        assertTrue("hava: language"
                .equals(actualResources.get(2).getResourceName() + ":" + actualResources.get(2).getDescription()));
        // Distance for name -> 4, Distance for description -> 10
        assertTrue("java: javascript"
                .equals(actualResources.get(3).getResourceName() + ":" + actualResources.get(3).getDescription()));
        // Distance for name -> 4, Distance for description -> 10
        assertTrue("java: javascript"
                .equals(actualResources.get(4).getResourceName() + ":" + actualResources.get(4).getDescription()));
        // Distance for name -> 4, Distance for description -> 19
        assertTrue("java: programming language"
                .equals(actualResources.get(5).getResourceName() + ":" + actualResources.get(5).getDescription()));
        // Distance for name -> 5, Distance for description -> 8
        assertTrue("linux: commands"
                .equals(actualResources.get(6).getResourceName() + ":" + actualResources.get(6).getDescription()));
        // Distance for name -> 6, Distance for description -> 5
        assertTrue("jquery: mobile"
                .equals(actualResources.get(7).getResourceName() + ":" + actualResources.get(7).getDescription()));
        // Distance for name -> 6, Distance for description -> 9
        assertTrue("spring: framework"
                .equals(actualResources.get(8).getResourceName() + ":" + actualResources.get(8).getDescription()));
        // Distance for name -> 6, Distance for description -> 19
        assertTrue("pyhton: programming language"
                .equals(actualResources.get(9).getResourceName() + ":" + actualResources.get(9).getDescription()));
    }

    /**
     * Verifies
     * {@link ResourcesController#showAddResourcePage(Resource, HttpSession)}
     * when user is not admin then redirects to add/remove resource page.
     */
    @Test
    public void testShowAddResourcePageWhenUserNotAdmin() throws Exception {
        when(userManager.isAdminUser(userInfo.getUserId())).thenReturn(false);
        assertEquals(MANAGE_RESOURCE, resourcesController.showAddResourcePage(resource, session).getViewName());
    }

    /**
     * Verifies
     * {@link ResourcesController#showAddResourcePage(Resource, HttpSession)}
     * when user is admin then redirects to add/remove resource page.
     */
    @Test
    public void testShowAddResourcePageWhenUserIsAdmin() throws Exception {
        assertEquals(MANAGE_RESOURCE, resourcesController.showAddResourcePage(resource, session).getViewName());
    }

    /**
     * Verifies
     * {@link ResourcesController#showAddResourcePage(Resource, HttpSession)}
     * when {@link Resource} is <code>null</code>, expects
     * {@link IllegalArgumentException}}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testShowAddResourcePageWhenResourceIsNull() throws Exception {
        resourcesController.showAddResourcePage(null, session);
    }

    /**
     * {@link ResourcesController#addRequestResource(ResourceRequest, RedirectAttributes, HttpSession)}
     * Validates add resource request page whether user is loggedIn.
     */
    @Test
    public void testAddRequestResourceWhenNotLoggedIn() {
        when(authenticationStatus.isLoggedIn()).thenReturn(false);
        assertEquals(LOGIN_REDIRECT,
                resourcesController.addRequestResource(resourceRequest, null, session).getViewName());
    }

    /**
     * {@link ResourcesController#addRequestResource(ResourceRequest, RedirectAttributes, HttpSession)}
     * When session is <code>null</code> expects
     * {@link IllegalArgumentException}.
     */
    @Test
    public void testAddRequestResourceWhenSessionIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(SESSION_NULL_ERROR_MESSAGE);
        resourcesController.addRequestResource(resourceRequest, null, null);
    }

    /**
     * {@link ResourcesController#addRequestResource(ResourceRequest, RedirectAttributes, HttpSession)}
     * When {@link ResourceRequest} object is <code>null</code> expects
     * {@link IllegalArgumentException}.
     */
    @Test
    public void testAddRequestResourceWhenResourceRequestIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_REQUEST_NULL_ERROR_MESSAGE);
        resourcesController.addRequestResource(null, null, session);
    }

    /**
     * {@link ResourcesController#addRequestResource(ResourceRequest, RedirectAttributes, HttpSession)}
     * When {@link UserProfileDetails} object is <code>null</code> expects
     * {@link IllegalArgumentException}.
     */
    @Test
    public void testAddRequestResourceWhenUserIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(USER_NULL_ERROR_MESSAGE);
        when(session.getAttribute(USER_DETAILS)).thenReturn(null);
        resourcesController.addRequestResource(resourceRequest, null, session);
    }

    /**
     * {@link ResourcesController#addRequestResource(ResourceRequest, RedirectAttributes, HttpSession)}
     * When {@link UserProfileDetails} method executes successfully.
     */
    @Test
    public void testAddRequestResourceReturningModelAndView() {
        final ModelAndView modelAndView = resourcesController.addRequestResource(resourceRequest,
                Mockito.mock(RedirectAttributes.class), session);
        assertEquals("redirect:resourceRequest", modelAndView.getViewName());
    }

    /**
     * {@link ResourcesController#addRequestResource(ResourceRequest, RedirectAttributes, HttpSession)}
     * When {@link ResourceRequestManager#addResourceRequest(ResourceRequest)}
     * throws Manager exception and verifies log message, expects
     * {@link IllegalArgumentException}.
     */
    @Test
    public void testAddRequestResourceWhenThrowsManagerExceptionVerifyLogMsg() throws ManagerException, DAOException {
        doThrow(managerException).when(resourceRequestManager).addResourceRequest(resourceRequest);
        final TestLogger logger = TestLoggerFactory.getTestLogger(ResourcesController.class);
        logger.clear();
        resourcesController.addRequestResource(resourceRequest, null, session);
        final List<LoggingEvent> listevents = logger.getLoggingEvents();
        assertEquals(uk.org.lidalia.slf4jext.Level.ERROR, listevents.get(0).getLevel());
        assertEquals("Error encountered while adding resource request", listevents.get(0).getMessage());
    }

    /**
     * {@link ResourcesController#getAllRequests()} Validates manage request
     * page whether user is loggedIn.
     */
    @Test
    public void testGetAllRequestsWhenNotLoggedIn() {
        when(authenticationStatus.isLoggedIn()).thenReturn(false);
        assertEquals(LOGIN_REDIRECT, resourcesController.getAllRequests().getViewName());
    }

    /**
     * {@link ResourcesController#getAllRequests()} When
     * {@link ResourceManager#getAllResourceRequests()} throws
     * {@link ManagerException} and verifies log message.
     */
    @Test
    public void testGetAllRequestsWhenThrowsManagerException() {
        doThrow(managerException).when(resourceManager).getAllResourceRequests();
        final TestLogger logger = TestLoggerFactory.getTestLogger(ResourcesController.class);
        logger.clear();
        final ModelAndView modelAndView = resourcesController.getAllRequests();
        final List<LoggingEvent> listevents = logger.getLoggingEvents();
        assertEquals(uk.org.lidalia.slf4jext.Level.ERROR, listevents.get(0).getLevel());
        assertEquals("Error retrieving requested resources from the database", listevents.get(0).getMessage());
        assertEquals(i18nBundle.getString("resources.getAllRequests.error"), modelAndView.getModel().get(ERROR_PAGE));
        assertNull(modelAndView.getModel().get(LIST_OF_REQUESTS));
        assertEquals(MANAGE_REQUESTS_PAGE, modelAndView.getViewName());
    }

    /**
     * {@link ResourcesController#getAllRequests()} Checks whether expected view
     * is returned when method executes successfully
     */
    @Test
    public void testGetAllRequests() {
        final List<ResourceRequest> listOfRequests = Collections.singletonList(new ResourceRequest());
        when(resourcesController.getAllRequests().getModel().get(LIST_OF_REQUESTS)).thenReturn(listOfRequests);
        final ModelAndView modelAndView = resourcesController.getAllRequests();
        assertEquals(MANAGE_REQUESTS_PAGE, modelAndView.getViewName());
        assertEquals(listOfRequests, modelAndView.getModel().get(LIST_OF_REQUESTS));
    }

    /**
     * {@link ResourcesController#batchDeleteResourceRequests(HttpSession, int[])}
     * When session is <code>null</code> expects
     * {@link IllegalArgumentException}.
     */
    @Test
    public void testBatchDeleteResourceRequestsWhenSessionIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(SESSION_NULL_ERROR_MESSAGE);
        resourcesController.batchDeleteResourceRequests(null, REQUEST_IDS);
    }

    /**
     * {@link ResourcesController#batchDeleteResourceRequests(HttpSession, int[])}
     * Validates manage requests page whether user is loggedIn.
     */
    @Test
    public void testBatchDeleteResourceRequestsWhenNotLoggedIn() {
        when(authenticationStatus.isLoggedIn()).thenReturn(false);
        assertEquals(LOGIN_REDIRECT,
                resourcesController.batchDeleteResourceRequests(session, REQUEST_IDS).getViewName());
    }

    /**
     * {@link ResourcesController#batchDeleteResourceRequests(HttpSession, int[])}
     * Validates manage requests page when requestIds array is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBatchDeleteResourceRequestsWhenResourceRequestIdsIsEmpty() {
        try {
            resourcesController.batchDeleteResourceRequests(session, new int[0]);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_REQUEST_IDS_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * {@link ResourcesController#batchDeleteResourceRequests(HttpSession, int[])}
     * Validates manage requests page when requestIds array is
     * <code>null</code>.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBatchDeleteResourceRequestsWhenResourceRequestIdsNull() {
        try {
            resourcesController.batchDeleteResourceRequests(session, null);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_REQUEST_IDS_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Checks
     * {@link ResourcesController#batchDeleteResourceRequests(HttpSession, int[])}
     * when {@link ResourceRequestManager#deleteResourceRequestsInBatch(int[])}
     * throws {@link ManagerException}.
     */
    @Test
    public void testBatchDeleteResourceRequestsWhenThrowsManagerException() {
        doThrow(managerException).when(resourceRequestManager).deleteResourceRequestsInBatch(REQUEST_IDS);
        final TestLogger logger = TestLoggerFactory.getTestLogger(ResourcesController.class);
        logger.clear();
        final ModelAndView modelAndView = resourcesController.batchDeleteResourceRequests(session, REQUEST_IDS);
        final List<LoggingEvent> listevents = logger.getLoggingEvents();
        assertEquals(uk.org.lidalia.slf4jext.Level.ERROR, listevents.get(0).getLevel());
        assertEquals(ERROR_DELETING_RESOURCE_REQUESTS, listevents.get(0).getMessage());
        assertEquals(i18nBundle.getString("resources.batchDeleteResourceRequests.error"),
                modelAndView.getModel().get(ERROR_MESSAGE));
        assertEquals(FORWARD_TO_MANAGE_REQUESTS, modelAndView.getViewName());
    }

    /**
     * Checks
     * {@link ResourcesController#batchDeleteResourceRequests(HttpSession, int[])}
     * when {@link ResourceRequestManager#deleteResourceRequestsInBatch(int[])}
     * doesn't throw any exception.
     */
    @Test
    public void testBatchDeleteResourceRequests() {
        final ModelAndView modelAndView = resourcesController.batchDeleteResourceRequests(session, REQUEST_IDS);
        assertEquals(i18nBundle.getString("resources.batchDeleteResourceRequests.success"),
                modelAndView.getModel().get(SUCCESS_MESSAGE));
        assertEquals(FORWARD_TO_MANAGE_REQUESTS, modelAndView.getViewName());
    }

    /**
     * {@link ResourcesController#showRequestResourcePage(String, HttpSession)}
     * Validates resource request page whether user is loggedIn.
     */
    @Test
    public void testShowRequestResourcePageWhenNotLoggedIn() {
        when(authenticationStatus.isLoggedIn()).thenReturn(false);
        assertEquals(LOGIN_REDIRECT, resourcesController.showRequestResourcePage(null, session).getViewName());
    }

    /**
     * {@link ResourcesController#showRequestResourcePage(String, HttpSession)}
     * When session is <code>null</code>, expects
     * {@link IllegalArgumentException}.
     */
    @Test
    public void testShowRequestResourcePageWhenSessionIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(SESSION_NULL_ERROR_MESSAGE);
        resourcesController.showRequestResourcePage(null, null);
    }

    /**
     * {@link ResourcesController#showRequestResourcePage(String, HttpSession)}
     * When {@link User} object is <code>null</code>, expects
     * {@link IllegalArgumentException}.
     */
    @Test
    public void testShowRequestResourcePageWhenUserIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(USER_NULL_ERROR_MESSAGE);
        when(session.getAttribute(USER_DETAILS)).thenReturn(null);
        resourcesController.showRequestResourcePage(null, session);
    }

    /**
     * Checks
     * {@link ResourcesController#showRequestResourcePage(String, HttpSession)}
     * when {@link ResourceRequestManager#getAllResourceRequestsOfUser(String)}
     * throws{@link ManagerException}.
     */
    @Test
    public void testShowRequestResourcePageWhenThrowsManagerException() {
        doThrow(managerException).when(resourceRequestManager).getAllResourceRequestsOfUser(userInfo.getUserId());
        final TestLogger logger = TestLoggerFactory.getTestLogger(ResourcesController.class);
        logger.clearAll();
        final ModelAndView modelAndView = resourcesController.showRequestResourcePage(null, session);
        final List<LoggingEvent> listevents = logger.getLoggingEvents();
        assertEquals(uk.org.lidalia.slf4jext.Level.ERROR, listevents.get(0).getLevel());
        assertEquals(RESOURCE_REQUEST_DB_READ_ERROR, listevents.get(0).getMessage());
        assertEquals(i18nBundle.getString("resources.getAllRequests.error"), modelAndView.getModel().get(ERROR_PAGE));
        assertNull(modelAndView.getModel().get(LIST_OF_REQUESTS));
        assertEquals(REQUEST_RESOURCE, modelAndView.getViewName());
    }

    /**
     * Checks
     * {@link ResourcesController#showRequestResourcePage(String, HttpSession)}
     * when method executes successfully.
     */
    @Test
    public void testShowRequestResourcePage() {
        final List<ResourceRequest> listOfRequests = Collections.singletonList(new ResourceRequest());
        when(resourcesController.showRequestResourcePage(REQUEST_RESOURCE_SUCCESS_MESSAGE, session).getModel()
                .get(LIST_OF_REQUESTS)).thenReturn(listOfRequests);
        final ModelAndView modelAndView = resourcesController.showRequestResourcePage(REQUEST_RESOURCE_SUCCESS_MESSAGE,
                session);
        assertEquals(REQUEST_RESOURCE, modelAndView.getViewName());
        assertEquals(listOfRequests, modelAndView.getModel().get(LIST_OF_REQUESTS));
        assertEquals(REQUEST_RESOURCE_SUCCESS_MESSAGE, modelAndView.getModel().get("successMessage"));
    }

    /**
     * Expects
     * {@link ResourcesController#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the id of the
     * {@link Resource} is zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithZeroId() {
        try {
            resourcesController.editResource(0, VALID_RESOURCE_NAME, staticURL, VALID_DIFFICULTY_LEVEL,
                    VALID_RESOURCE_TYPE_NAME, VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(INVALID_RESOURCE_ID_ERROR_MESSAGE, e.getMessage());
        }
    }

    /**
     * Expects
     * {@link ResourcesController#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the id of the
     * {@link Resource} is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithNegativeId() {
        try {
            resourcesController.editResource(-1, VALID_RESOURCE_NAME, staticURL, VALID_DIFFICULTY_LEVEL,
                    VALID_RESOURCE_TYPE_NAME, VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(INVALID_RESOURCE_ID_ERROR_MESSAGE, e.getMessage());
        }
    }

    /**
     * Expects
     * {@link ResourcesController#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the name of the
     * {@link Resource} is <code>null</code>.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithInvalidResourceName() {
        try {
            resourcesController.editResource(VALID_RESOURCE_ID, null, staticURL, VALID_DIFFICULTY_LEVEL,
                    VALID_RESOURCE_TYPE_NAME, VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(INVALID_RESOURCE_NAME_ERROR_MESSAGE, e.getMessage());
        }
    }

    /**
     * Expects
     * {@link ResourcesController#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the name of the
     * {@link Resource} is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithEmptyResourceName() {
        try {
            resourcesController.editResource(VALID_RESOURCE_ID, EMPTY_STRING, staticURL, VALID_DIFFICULTY_LEVEL,
                    VALID_RESOURCE_TYPE_NAME, VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(INVALID_RESOURCE_NAME_ERROR_MESSAGE, e.getMessage());
        }
    }

    /**
     * Expects
     * {@link ResourcesController#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the name of the
     * {@link Resource} is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithBlankResourceName() {
        try {
            resourcesController.editResource(VALID_RESOURCE_ID, BLANK_STRING, staticURL, VALID_DIFFICULTY_LEVEL,
                    VALID_RESOURCE_TYPE_NAME, VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(INVALID_RESOURCE_NAME_ERROR_MESSAGE, e.getMessage());
        }
    }

    /**
     * Expects
     * {@link ResourcesController#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the URL of the
     * {@link Resource} is <code>null</code>.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithNullResourceURL() {
        try {
            resourcesController.editResource(VALID_RESOURCE_ID, VALID_RESOURCE_NAME, null, VALID_DIFFICULTY_LEVEL,
                    VALID_RESOURCE_TYPE_NAME, VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(RESOURCE_LINK_NULL_ERROR_MESSAGE, e.getMessage());
        }
    }

    /**
     * Expects
     * {@link ResourcesController#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the URL of the
     * {@link Resource} is not valid.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithInvalidResourceLink() throws MalformedURLException {
        try {
            resourcesController.editResource(VALID_RESOURCE_ID, VALID_RESOURCE_NAME,
                    new URL("ftp", "somehost", "somefile"), VALID_DIFFICULTY_LEVEL, VALID_RESOURCE_TYPE_NAME,
                    VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(INVALID_RESOURCE_LINK_ERROR_MESSAGE, e.getMessage());
        }
    }

    /**
     * Expects
     * {@link ResourcesController#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the id of the
     * {@link Resource} is zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithZeroResourceDifficultyLevel() {
        try {
            resourcesController.editResource(VALID_RESOURCE_ID, VALID_RESOURCE_NAME, staticURL, 0,
                    VALID_RESOURCE_TYPE_NAME, VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(INVALID_RESOURCE_LEVEL_ERROR_MESSAGE, e.getMessage());
        }
    }

    /**
     * Expects
     * {@link ResourcesController#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the id of the
     * {@link Resource} is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithNegativeResourceDifficultyLevel() {
        try {
            resourcesController.editResource(VALID_RESOURCE_ID, VALID_RESOURCE_NAME, staticURL, -1,
                    VALID_RESOURCE_TYPE_NAME, VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(INVALID_RESOURCE_LEVEL_ERROR_MESSAGE, e.getMessage());
        }
    }

    /**
     * Expects
     * {@link ResourcesController#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the type of the
     * {@link Resource} is <code>null</code>.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithInvalidResourceType() {
        try {
            resourcesController.editResource(VALID_RESOURCE_ID, VALID_RESOURCE_NAME, staticURL, VALID_DIFFICULTY_LEVEL,
                    null, VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(INVALID_RESOURCE_TYPE_ERROR_MESSAGE, e.getMessage());
        }
    }

    /**
     * Expects
     * {@link ResourcesController#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the type of the
     * {@link Resource} is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithEmptyResourceType() {
        try {
            resourcesController.editResource(VALID_RESOURCE_ID, VALID_RESOURCE_NAME, staticURL, VALID_DIFFICULTY_LEVEL,
                    EMPTY_STRING, VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(INVALID_RESOURCE_TYPE_ERROR_MESSAGE, e.getMessage());
        }
    }

    /**
     * Expects
     * {@link ResourcesController#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the type of the
     * {@link Resource} is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithBlankResourceType() {
        try {
            resourcesController.editResource(VALID_RESOURCE_ID, VALID_RESOURCE_NAME, staticURL, VALID_DIFFICULTY_LEVEL,
                    BLANK_STRING, VALID_RESOURCE_OWNER);
        } catch (final ManagerException e) {
            assertEquals(INVALID_RESOURCE_TYPE_ERROR_MESSAGE, e.getMessage());
        }
    }

    /**
     * Expects
     * {@link ResourcesController#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the resourceOwner of the
     * {@link Resource} is <code>null</code>.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithNullResourceOwner() {
        try {
            resourcesController.editResource(VALID_RESOURCE_ID, VALID_RESOURCE_NAME, staticURL, VALID_DIFFICULTY_LEVEL,
                    VALID_RESOURCE_TYPE_NAME, null);
        } catch (final ManagerException e) {
            assertEquals(INVALID_RESOURCE_OWNER_ERROR_MESSAGE, e.getMessage());
        }
    }

    /**
     * Expects
     * {@link ResourcesController#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the resourceOwner of the
     * {@link Resource} is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithEmptyResourceOwner() {
        try {
            resourcesController.editResource(VALID_RESOURCE_ID, VALID_RESOURCE_NAME, staticURL, VALID_DIFFICULTY_LEVEL,
                    VALID_RESOURCE_TYPE_NAME, EMPTY_STRING);
        } catch (final ManagerException e) {
            assertEquals(INVALID_RESOURCE_OWNER_ERROR_MESSAGE, e.getMessage());
        }
    }

    /**
     * Expects
     * {@link ResourcesController#editResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the resourceOwner of the
     * {@link Resource} is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditResourceWithBlankResourceOwner() {
        try {
            resourcesController.editResource(VALID_RESOURCE_ID, VALID_RESOURCE_NAME, staticURL, VALID_DIFFICULTY_LEVEL,
                    VALID_RESOURCE_TYPE_NAME, BLANK_STRING);
        } catch (final ManagerException e) {
            assertEquals(INVALID_RESOURCE_OWNER_ERROR_MESSAGE, e.getMessage());
        }
    }

    /**
     * Checks whether
     * {@link ResourcesController#editResource(int, String, URL, int, String, String)}
     * returns true with valid inputs
     */
    @Test
    public void testEditResourceWithValidInputs() {
        when(resourceManager.editResource(VALID_RESOURCE_ID, VALID_RESOURCE_NAME, staticURL, VALID_DIFFICULTY_LEVEL,
                VALID_RESOURCE_TYPE_NAME, VALID_RESOURCE_OWNER)).thenReturn(true);
        assertEquals(true, resourcesController.editResource(VALID_RESOURCE_ID, VALID_RESOURCE_NAME, staticURL,
                VALID_DIFFICULTY_LEVEL, VALID_RESOURCE_TYPE_NAME, VALID_RESOURCE_OWNER));
    }

    /**
     * Checks whether
     * {@link ResourcesController#editResource(int, String, URL, int, String, String)}
     * returns false if an exception is thrown
     */
    @Test
    public void testEditResourceWithException() {
        doThrow(ManagerException.class).when(resourceManager).editResource(VALID_RESOURCE_ID, VALID_RESOURCE_NAME,
                staticURL, VALID_DIFFICULTY_LEVEL, VALID_RESOURCE_TYPE_NAME, VALID_RESOURCE_OWNER);
        assertEquals(false, resourcesController.editResource(VALID_RESOURCE_ID, VALID_RESOURCE_NAME, staticURL,
                VALID_DIFFICULTY_LEVEL, VALID_RESOURCE_TYPE_NAME, VALID_RESOURCE_OWNER));
    }

    /**
     * Expects
     * {@link ResourcesController#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to execute properly when valid inputs have been passed.
     */
    @Test
    public void testSearchResourcesByCategoryNameAndDifficultyLevelValid() {
        when(resourcesController.searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME,
                VALID_DIFFICULTY_LEVEL)).thenReturn(listOfResourceCategoryRelations);
        assertEquals(listOfResourceCategoryRelations, resourcesController
                .searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, VALID_DIFFICULTY_LEVEL));
    }

    /**
     * Expects
     * {@link ResourcesController#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to return an empty List of {@link ResourceCategoryRelation
     * ResourceCategoryRelations} when {@link ManagerException} has been caught.
     */
    @Test
    public void testSearchResourcesByCategoryNameAndDifficultyLevelException() {
        when(resourceCategoryRelationManager.searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME,
                VALID_DIFFICULTY_LEVEL)).thenThrow(ManagerException.class);
        assertEquals(emptyListOfResourceCategoryRelations, resourcesController
                .searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, VALID_DIFFICULTY_LEVEL));
    }

    /**
     * Expects
     * {@link ResourcesController#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to return an empty List of {@link ResourceCategoryRelation
     * ResourceCategoryRelations} when there are no
     * {@link ResourceCategoryRelation ResourceCategoryRelations} are present in
     * the database with the specified category name and difficulty level.
     */
    @Test
    public void testSearchResourcesByCategoryNameAndDifficultyLevelReturnsEmpty() {
        when(resourceCategoryRelationManager.searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME,
                VALID_DIFFICULTY_LEVEL)).thenReturn(emptyListOfResourceCategoryRelations);
        assertEquals(emptyListOfResourceCategoryRelations, resourcesController
                .searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, VALID_DIFFICULTY_LEVEL));
    }

    /**
     * Expects
     * {@link ResourcesController#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to throw an {@link IllegalArgumentException} when a <code>null</code>
     * category name is passed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchResourcesByCategoryNameAndDifficultyLevelNullCategoryName() {
        try {
            resourcesController.searchResourcesByCategoryNameAndDifficultyLevel(null, VALID_DIFFICULTY_LEVEL);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_CATEGORY_NAME_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourcesController#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to throw an {@link IllegalArgumentException} when a blank category name
     * is passed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchResourcesByCategoryNameAndDifficultyLevelBlankCategoryName() {
        try {
            resourcesController.searchResourcesByCategoryNameAndDifficultyLevel("", VALID_DIFFICULTY_LEVEL);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_CATEGORY_NAME_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourcesController#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to throw an {@link IllegalArgumentException} when a white space has been
     * passed for category name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchResourcesByCategoryNameAndDifficultyLevelWhiteSpaceCategoryName() {
        try {
            resourcesController.searchResourcesByCategoryNameAndDifficultyLevel("    ", VALID_DIFFICULTY_LEVEL);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_CATEGORY_NAME_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourcesController#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to throw an {@link IllegalArgumentException} when a negative difficulty
     * level is passed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchResourcesByCategoryNameAndDifficultyLevelNegativeDifficultyLevel() {
        try {
            resourcesController.searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, -1);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(INVALID_RESOURCE_LEVEL_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link ResourcesController#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to throw an {@link IllegalArgumentException} when zero (boundary
     * condition) has been passed for difficulty level.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchResourcesByCategoryNameAndDifficultyLevelZeroDifficultyLevel() {
        try {
            resourcesController.searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, 0);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(INVALID_RESOURCE_LEVEL_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }
}