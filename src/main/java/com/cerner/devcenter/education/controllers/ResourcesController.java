package com.cerner.devcenter.education.controllers;

import static com.google.common.base.Preconditions.checkArgument;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.exceptions.CategoryIdNotFoundException;
import com.cerner.devcenter.education.exceptions.DuplicateResourceTypeFoundException;
import com.cerner.devcenter.education.exceptions.ResourceIdNotFoundException;
import com.cerner.devcenter.education.helpers.HttpURLValidator;
import com.cerner.devcenter.education.helpers.ResourceNameSorter;
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
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthenticationStatusUtil;
import com.cerner.devcenter.education.utils.Constants;

/**
 * Handles requests related to adding, retrieving and removing learning
 * resources.
 *
 * @author Wuchen Wang (WW044343)
 * @author Anudeep Kumar Gadam (AG045334)
 * @author Asim Mohammed (AM045300)
 * @author Anudeep Kumar Gadam (AG045334)
 * @author Navya Rangeneni (NR046827)
 * @author Vincent Dasari (VD049645)
 * @author Rishabh Bhojak (RB048032)
 * @author Santosh Kumar (SK051343)
 */
@Controller
@Qualifier("ResourcesController")
@RequestMapping("/app")
public class ResourcesController {

    private static final String RESOURCE = "resource";
    private static final String RESOURCE_WITH_DIFFICULTY = "resourceWithDifficulty";
    private static final String RESOURCES = "resources";
    private static final String CATEGORIES = "categories";
    private static final String RESOURCE_TYPES = "resourceTypes";
    private static final String TAGS = "tags";
    private static final String CATEGORY_RESOURCE_FORM = "categoryResourceForm";
    private static final String MESSAGE = "message";

    private static final String MANAGE_RESOURCES = "manage_resources";
    private static final String REDIRECT_LOGIN = "redirect:/login";
    private static final String PAGE_NUMBER = "pagenumber";
    private static final String PAGE_COUNT = "pagecount";

    private static final String RESOURCES_PER_PAGE = "resourcesperpage";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String SUCCESS_MESSAGE = "successMessage";
    private static final String USER_DETAILS = "userDetails";
    private static final String REQUEST_RESOURCE = "request_resource";
    private static final String MANAGE_REQUESTS_PAGE = "manage_requests";
    private static final String LIST_OF_REQUESTS = "listOfRequests";

    private static final String RESOURCE_REQUEST_DB_READ_ERROR = "Error retrieving requested resources from the database";
    private static final String RESOURCE_DISPLAY_DB_READ_ERROR = "Error displaying all the resources";
    private static final String REQUEST_RESOURCE_SUCCESS = "successMessage";
    private static final String ERROR_DELETING_RESOURCE_REQUESTS = "Error: unable to delete the resource request.";
    private static final String FORWARD_TO_MANAGE_REQUESTS = "forward:show_requests";
    private static final String SESSION_NULL_ERROR = "session cannot be null";
    private static final String RESOURCE_REQUEST_IDS_ERROR = "resourceRequestIds cannot be null/empty";
    private static final String USER_NULL_ERROR_MESSAGE = "user cannot be null";
    private static final String RESOURCE_NULL_ERROR_MESSAGE = "resource cannot be null";
    private static final String RESOURCE_CATEGORY_RELATION = "resourceCategoryRelation";
    private static final String SEARCH = "search";
    private static final String SEARCH_NOT_NULL = "Search string cannot be null or empty.";

    private static final String USER_GUIDE_MESSAGE = "addResourcePage.userGuideMessage";
    private static final String MANAGE_RESOURCES_ERROR_MESSAGE = "addResourcePage.errorMessage";

    private static final String SEARCH_ERROR_MESSAGE = "Error searching for resource category relation by category name and difficulty level";
    private static final String INVALID_CATEGORY_RESOURCE_OBJECT = "Category Resource Form object passed can't be null";
    private static final String INVALID_CATEGORY_NAME_ERROR_MESSAGE = "Category Name can not be null/empty/blank";
    private static final String INVALID_CATEGORY_ID = "category ID must be a positive integer";
    private static final String INVALID_SELECTED_CATEGORY_ID = "Selected Category ID must be greater than zero";
    private static final String INVALID_SELECTED_RESOURCE_ID = "Selected Resource Type ID must be a zero (to display all types) or a positive integer";
    private static final String INVALID_RESOURCE_LEVEL_ERROR_MESSAGE = "Resource Difficulty Level must be greater than 0";
    private static final String INVALID_RESOURCE_OWNER_ERROR_MESSAGE = "Resource owner cannot be null/empty/blank";
    private static final String RESOURCE_LINK_NULL_ERROR_MESSAGE = "Resource Link can not be null";
    private static final String RESOURCE_NOT_NULL = "Resouce object cannot be null";
    private static final String RESOURCE_REQUEST_NOT_NULL = "resouce request object cannot be null";
    private static final String INVALID_RESOURCE_TYPE_NAME = "Name for Resource Type must not be null or empty";
    private static final String INVALID_RESOURCE_ID_ERROR_MESSAGE = "Resource Id must be greater than 0";
    private static final String INVALID_RESOURCE_NAME_ERROR_MESSAGE = "Resource Name cannot be null/empty/blank";
    private static final String INVALID_RESOURCE_LINK_ERROR_MESSAGE = "Resource Link cannot be empty/blank";
    private static final String INVALID_RESOURCE_TYPE_ERROR_MESSAGE = "Resource Type cannot be null/empty/blank";
    private static final String EDIT_ERROR = "Error Editing Resource";
    private static final String ERROR_PAGE = "error";

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourcesController.class);

    private static ResourceBundle i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

    @Autowired
    private ResourceManager resourceManager;
    @Autowired
    private ResourceRequestManager resourceRequestManager;
    @Autowired
    private CategoryManager categoryManager;
    @Autowired
    private ResourceTypeManager resourceTypeManager;
    @Autowired
    private AuthenticationStatusUtil authenticationStatus;
    @Autowired
    private ResourceCategoryRelationManager resourceCategoryRelationManager;
    @Autowired
    private TagManager tagManager;
    @Autowired
    private UserManager userManager;
    @Autowired
    private EmailManager emailManager;

    ResourcesController() {
        this(AuthenticationStatusUtil.getInstance());
    }

    private ResourcesController(final AuthenticationStatusUtil authenticationStatus) {
        this.authenticationStatus = authenticationStatus;
    }

    /**
     * Returns the view to add a resource, view, edit and delete, if the user is
     * logged in, else redirects to general error page.
     *
     * @param resource
     *            The {@link Resource} parameter is not directly used, it is
     *            annotated to satisfy the resource attribute required in
     *            returning page. Cannot be <code>null</code>
     * @param session
     *            a {@link HttpSession} object that contains the current session
     *            object.
     * @return {@link ModelAndView} object that redirects to one of the below
     *         pages:
     *         <ul>
     *         <li>add/view/edit/remove resource page (manage resources) if user
     *         is logged in</li>
     *         <li>general error page if the user session no longer exists</li>
     *         </ul>
     * @throws IllegalArgumentException
     *             <ul>
     *             <li>when resource argument is <code>null</code></li>
     *             </ul>
     */
    @RequestMapping(value = "/Show_add_resource", method = RequestMethod.GET)
    public ModelAndView showAddResourcePage(@ModelAttribute(RESOURCE) final Resource resource,
            final HttpSession session) {
        checkArgument(resource != null, RESOURCE_NULL_ERROR_MESSAGE);
        if (!authenticationStatus.isLoggedIn()) {
            return redirectsNotLoggedIn();
        }
        final ModelAndView model = new ModelAndView(MANAGE_RESOURCES);
        try {
            model.addObject(CATEGORIES, categoryManager.getAllCategories());
            model.addObject(RESOURCE_TYPES, resourceTypeManager.getAllResourceTypes());
            model.addObject(TAGS, tagManager.getAllTags());
            model.addObject(RESOURCE_WITH_DIFFICULTY, resourceCategoryRelationManager.getResourcesForAllCategories());
            model.addObject(MESSAGE, i18nBundle.getString(USER_GUIDE_MESSAGE));
        } catch (final ManagerException managerException) {
            LOGGER.error(RESOURCE_DISPLAY_DB_READ_ERROR, managerException);
            model.addObject(ERROR_MESSAGE, i18nBundle.getString(MANAGE_RESOURCES_ERROR_MESSAGE));
        }
        return model;
    }

    /**
     * Handles add resource operation with resource passed from request.
     *
     * @param resource
     *            The {@link Resource} object is used to get the resource link
     *            and description entered. Cannot be <code>null</code>
     * @param result
     *            This contains the {@link BindingResult} details whether there
     *            are binding errors or not in your controllers. * @param
     *            session a {@link HttpSession} object that contains the current
     *            session object.
     * @param session
     *            a {@link HttpSession} object that contains the current session
     *            object.
     * @return Returns the {@link ModelAndView} that loads add_resource page.
     * @throws CategoryIdNotFoundException
     *             when there is no category found for given categoryId.
     * @throws ResourceIdNotFoundException
     *             when there is no resource found for given resourceId.
     */
    @RequestMapping(value = "/Add_resource", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView addResource(@ModelAttribute(RESOURCE) Resource resource, final BindingResult result,
            final HttpSession session) throws ResourceIdNotFoundException, CategoryIdNotFoundException {
        if (!authenticationStatus.isLoggedIn()) {
            return redirectsNotLoggedIn();
        }
        checkArgument(resource != null, RESOURCE_NULL_ERROR_MESSAGE);
        final UserProfileDetails user = (UserProfileDetails) session.getAttribute(USER_DETAILS);
        checkArgument(user != null, USER_NULL_ERROR_MESSAGE);
        final ModelAndView model = new ModelAndView(MANAGE_RESOURCES);
        model.addObject(CATEGORIES, categoryManager.getAllCategories());
        model.addObject(RESOURCE_TYPES, resourceTypeManager.getAllResourceTypes());
        model.addObject(CATEGORY_RESOURCE_FORM, new CategoryResourceForm());
        if (result != null && result.hasErrors()) {
            LOGGER.error("There was a BindingResult error. Please enter valid input.");
            model.addObject(MESSAGE, i18nBundle.getString("addResourcePage.userGuideMessage"));
            model.addObject(ERROR_MESSAGE,
                    i18nBundle.getString("com.cerner.devcenter.education.controllers.bindingResultError"));
            return model;
        }
        if (userManager.isAdminUser(user.getUserId())) {
            resource.setResourceStatus(ResourceStatus.Available.toString());
        } else {
            resource.setResourceStatus(ResourceStatus.Pending.toString());
        }
        resource.setResourceOwner(user.getUserId());
        resource = addCategoriesForResource(resource);
        resource = resourceManager.addResourceCategoryRelationWithDifficultyLevel(resource);
        model.addObject(MESSAGE, i18nBundle.getString("addResourcePage.userGuideMessage"));
        model.addObject(SUCCESS_MESSAGE, i18nBundle.getString("addResourcePage.successMessage"));
        emailManager.sendEmailWhenResourceAdded(resource);
        return model;
    }

    /**
     * Retrieves the category objects corresponding to the IDs of the category.
     *
     * @param resource
     *            a {@link Resource}) object without the checkedCategoryIdList.
     *            (Cannot be <code>null</code>)
     * @return A resource object having list of categories that resource
     *         belongs.
     * @throws ManagerException
     *             when {@link CategoryManager} is not able access the data
     *             source.
     */
    private Resource addCategoriesForResource(final Resource resource) throws ManagerException {
        for (final int categoryID : resource.getCheckedCategoriesIDs()) {
            final Category category = categoryManager.getCategoryById(categoryID);
            resource.addCategory(category);
        }
        return resource;
    }

    /**
     * Handles the request for retrieving categories.
     *
     * @return Returns the Spring {@link ResponseEntity} object.
     * @throws ManagerException
     *             when a manager is not having error accessing the data source.
     */
    @RequestMapping(value = "/categories", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<Category>> listCategoryResources() throws ManagerException {
        final List<Category> categories = categoryManager.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    /**
     * Handles the request for retrieving the resources associated with the
     * category by category id.
     *
     * @return Returns the Spring {@link ResponseEntity} object.
     * @throws ManagerException
     *             when a manager is not having error accessing the data source.
     */
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<Resource>> findResources(@PathVariable("id") final Integer id) throws ManagerException {
        checkArgument(id > 0, INVALID_CATEGORY_ID);
        final List<Resource> resources = resourceManager.getResourcesByCategoryId(id);
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    /**
     * Handles the request for retrieving the resource request page.
     *
     * @param successMessage
     *            This is set as ModelAttribute using RedirectAttributes when
     *            invoked from "resourceRequest" action, else it will be null.
     * @param session
     *            a {@link HttpSession} object that contains the current session
     *            object.Must not be null.
     * @return Returns the Spring {@link ModelAndView} object with the view name
     *         of the JSP page that is to be loaded.
     */
    @RequestMapping(value = "/resourceRequest", method = RequestMethod.GET)
    public ModelAndView showRequestResourcePage(@ModelAttribute("SuccesssMessage") final String successMessage,
            final HttpSession session) {
        checkArgument(session != null, SESSION_NULL_ERROR);
        if (!authenticationStatus.isLoggedIn()) {
            return redirectsNotLoggedIn();
        }
        final UserProfileDetails user = (UserProfileDetails) session.getAttribute(USER_DETAILS);
        checkArgument(user != null, USER_NULL_ERROR_MESSAGE);
        final ModelAndView modelAndView = new ModelAndView(REQUEST_RESOURCE);
        try {
            final List<ResourceRequest> listOfRequests = resourceRequestManager
                    .getAllResourceRequestsOfUser(user.getUserId());
            if (listOfRequests == null || listOfRequests.isEmpty()) {
                modelAndView.addObject(ERROR_PAGE, i18nBundle.getString("resources.getAllRequests.noRecords"));
            }
            modelAndView.addObject(LIST_OF_REQUESTS, listOfRequests);
        } catch (final ManagerException managerException) {
            LOGGER.error(RESOURCE_REQUEST_DB_READ_ERROR, managerException);
            modelAndView.addObject(ERROR_PAGE, i18nBundle.getString("resources.getAllRequests.error"));
        }
        if (StringUtils.isNotBlank(successMessage)) {
            modelAndView.addObject(REQUEST_RESOURCE_SUCCESS, i18nBundle.getString("resources.request.successMessage"));
        }
        return modelAndView;
    }

    /**
     * Handles the request for adding the resource and redirects to
     * "resourceRequest" action.
     *
     * @param resourceRequest
     *            a {@link ResourceRequest} object that contains the category
     *            Name and resource Name. Must not be null.
     * @param redirectAttributes
     *            used to pass success message as flash attribute, so that it
     *            can be used in redirection call to "resourceRequest" action.
     * @param session
     *            a {@link HttpSession} object that contains the current session
     *            object.Must not be null.
     * @return Returns the Spring {@link ModelAndView} object with the view name
     *         of the JSP page that is to be loaded
     * @throws IllegalArgumentException
     *             when {@link ResourceRequest}, session or user is null or
     */
    @RequestMapping(value = "/resourceRequest", method = RequestMethod.POST)
    public ModelAndView addRequestResource(@ModelAttribute("resourceRequest") final ResourceRequest resourceRequest,
            final RedirectAttributes redirectAttributes, final HttpSession session) {
        checkArgument(session != null, "session cannot be null");
        checkArgument(resourceRequest != null, "resouce request object cannot be null");
        if (!authenticationStatus.isLoggedIn()) {
            return redirectsNotLoggedIn();
        }
        final UserProfileDetails user = (UserProfileDetails) session.getAttribute(USER_DETAILS);
        checkArgument(user != null, USER_NULL_ERROR_MESSAGE);
        final ModelAndView modelView = new ModelAndView();
        modelView.setViewName("redirect:resourceRequest");
        resourceRequest.setUserId(user.getUserId());
        try {
            resourceRequestManager.addResourceRequest(resourceRequest);
            redirectAttributes.addFlashAttribute("SuccesssMessage",
                    i18nBundle.getString("resources.request.successMessage"));
        } catch (final ManagerException managerException) {
            LOGGER.error("Error encountered while adding resource request", managerException);
        }
        return modelView;
    }

    /**
     * Handles the request for retrieving the search resources page.
     *
     * @param resourceCategoryRelation
     *            model attribute {@link ResourceCategoryRelation}
     * @return Returns the Spring {@link ModelAndView} object with the view name
     *         of the JSP page that is to be loaded
     */
    @RequestMapping(value = "/searchResources", method = RequestMethod.GET)
    public ModelAndView showSearchResources(
            @ModelAttribute(RESOURCE_CATEGORY_RELATION) final ResourceCategoryRelation resourceCategoryRelation) {
        if (!authenticationStatus.isLoggedIn()) {
            return redirectsNotLoggedIn();
        }
        final ModelAndView modelView = new ModelAndView();
        modelView.setViewName(SEARCH);
        return modelView;
    }

    /**
     * /** Handles the request to auto-complete the search.
     *
     * <p>
     * Gets the list of resources which contains the search string as a part of
     * their name or description, sorts the list based on their nearness to the
     * search string and returns the top ten resource if the count of resources
     * is more than ten, returns the whole list after sorting if they are less
     * then ten.
     * </p>
     *
     * @param search
     *            the string user entered to perform search(can't be
     *            <code>null</code> or empty).
     * @return {@link List} of resources which contain the user entered text in
     *         them and in the order of their distance from search string
     * @throws MalformedURLException
     * @throws IllegalArgumentException
     *             when search string is <code>empty or null</code>
     */
    @RequestMapping(value = "/autocomplete", method = RequestMethod.GET)
    public @ResponseBody List<Resource> autocomplete(@RequestParam(SEARCH) final String search)
            throws MalformedURLException {
        checkArgument(StringUtils.isNotEmpty(search), SEARCH_NOT_NULL);
        final List<Resource> resources = resourceManager.getSearchedResources(search.toLowerCase());
        Collections.sort(resources, new ResourceNameSorter(search));
        if (resources.size() > Constants.AUTOFILL_SIZE) {
            return resources.subList(0, Constants.AUTOFILL_SIZE);
        }
        return resources;
    }

    /**
     * Displays the page that shows all the resources available.
     *
     * @param categoryResourceForm
     *            The {@link CategoryResourceForm} that has the user selected
     *            category id and resource type id in order to filter the
     *            results. If resource type id is 0 then user has selected all
     *            types (the option "any").
     * @return Returns the Spring {@link ModelAndView} object with the view name
     *         of the JSP page that is to be loaded.
     * @throws IllegalArgumentException
     *             when {@link CategoryResourceForm} is <code>null</code> or
     *             category id is less than or equal to 0 or resource type id is
     *             less than 0.
     */
    @RequestMapping(value = "/Resources", method = { RequestMethod.GET })
    public ModelAndView showResources(
            @ModelAttribute(CATEGORY_RESOURCE_FORM) final CategoryResourceForm categoryResourceForm,
            @RequestParam(value = PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(value = RESOURCES_PER_PAGE) Integer resourcesPerPage) {
        int pageCount;
        if (!authenticationStatus.isLoggedIn()) {
            return redirectsNotLoggedIn();
        }
        checkArgument(categoryResourceForm != null, "Category Resource Form object passed can't be null");
        checkArgument(categoryResourceForm.getSelectedCategoryID() > 0,
                "Selected Category ID must be greater than zero");
        checkArgument(categoryResourceForm.getSelectedResourceTypeID() >= 0,
                "Selected Resource Type ID must be a zero (to display all types) or a positive integer");
        if (pageNumber == null) {
            pageNumber = 1;
        }
        if (resourcesPerPage == null) {
            resourcesPerPage = 5;
        }
        final ModelAndView modelView = new ModelAndView();
        modelView.addObject(CATEGORIES, categoryManager.getAllCategories());
        modelView.addObject(RESOURCE_TYPES, resourceTypeManager.getAllResourceTypes());
        List<ResourceCategoryRelation> resourcesRelationAvailable = Collections.emptyList();
        final int userSelectedResourceTypeId = categoryResourceForm.getSelectedResourceTypeID();
        final int userSelectedCategoryId = categoryResourceForm.getSelectedCategoryID();
        pageCount = resourceManager.getPageCountForResourcesByCategoryId(categoryResourceForm.getSelectedCategoryID(),
                resourcesPerPage);
        if (userSelectedResourceTypeId == 0) {
            resourcesRelationAvailable = resourceCategoryRelationManager
                    .getResourcesAndDifficultyLevelByCategoryIdWithPagination(userSelectedCategoryId, resourcesPerPage,
                            pageNumber);
            modelView.addObject(RESOURCE_WITH_DIFFICULTY, resourcesRelationAvailable);
        } else {
            resourcesRelationAvailable = resourceCategoryRelationManager
                    .getResourcesByCategoryIdAndTypeIdWithPagination(userSelectedCategoryId, userSelectedResourceTypeId,
                            resourcesPerPage, pageNumber);
            modelView.addObject(RESOURCE_WITH_DIFFICULTY, resourcesRelationAvailable);
        }
        if (resourcesRelationAvailable.isEmpty()) {
            modelView.addObject(ERROR_MESSAGE,
                    i18nBundle.getString("com.cerner.devcenter.education.controllers.errorNoResource"));
        }
        modelView.addObject(CATEGORY_RESOURCE_FORM, categoryResourceForm);
        modelView.addObject(PAGE_COUNT, pageCount);
        modelView.addObject(PAGE_NUMBER, pageNumber);
        modelView.addObject(RESOURCES_PER_PAGE, resourcesPerPage);
        modelView.setViewName(RESOURCES);
        return modelView;
    }

    /**
     * Retrieve the resource description for the resource id passed in.
     *
     * @param resourceId
     *            a unique id used to find a specific {@link Resource} (must be
     *            positive).
     * @return resource description based on the resource id.
     */
    @RequestMapping(value = "/resource/description", method = RequestMethod.GET)
    public @ResponseBody String getCourseDescription(@RequestParam("id") final int resourceId) {
        checkArgument(resourceId > 0, INVALID_RESOURCE_ID_ERROR_MESSAGE);
        return resourceManager.retrieveResourceDescriptionById(resourceId);
    }

    /**
     * Redirects to the login page when admin is not logged in.
     *
     * @return the model and view that redirects to login jsp page
     */
    private ModelAndView redirectsNotLoggedIn() {
        final ModelAndView model = new ModelAndView(REDIRECT_LOGIN);
        LOGGER.error("Admin not logged in");
        model.addObject(MESSAGE, "Admin is not logged in");
        return model;
    }

    /**
     * Displays the page that shows all the categories of resources.
     *
     * @return Returns the Spring {@link ModelAndView} object with the view name
     *         of the JSP page that is to be loaded.
     */
    @RequestMapping(value = "/categoriesForResources", method = RequestMethod.GET)
    public ModelAndView showCategoryResources() {
        if (!authenticationStatus.isLoggedIn()) {
            return redirectsNotLoggedIn();
        }
        final ModelAndView modelView = new ModelAndView();
        modelView.addObject(CATEGORIES, categoryManager.getAllCategories());
        modelView.addObject(RESOURCE_TYPES, resourceTypeManager.getAllResourceTypes());
        modelView.addObject(CATEGORY_RESOURCE_FORM, new CategoryResourceForm());
        modelView.setViewName(RESOURCES);
        return modelView;
    }

    /**
     * Deletes the resource from database for the resource id passed in.
     *
     * @param resourceId
     *            a unique id used to find a specific {@link Resource}, It must
     *            be positive.
     * @return
     *
     *         <pre>
     *         True
     *         </pre>
     *
     *         , if it gets deleted.
     *
     *         <pre>
     *         False
     *         </pre>
     *
     *         , if there is an error deleting.
     */
    @RequestMapping(value = "/delete/resource", method = RequestMethod.GET)
    public @ResponseBody boolean deleteResource(@RequestParam("id") final int resourceId) {
        checkArgument(resourceId > 0, INVALID_RESOURCE_ID_ERROR_MESSAGE);
        boolean deletionStatus = true;
        try {
            resourceManager.deleteResource(resourceId);
        } catch (final ManagerException managerException) {
            LOGGER.error("Error Deleting Resource", new ManagerException());
            deletionStatus = false;
        }
        return deletionStatus;
    }

    /**
     * Add a new resource type.
     *
     * @param typeName
     *            a {@link String} that is the name of the resource type that
     *            the user wants to add. Cannot be <code>null or empty></code>
     * @return a {@link ResourceType} object with the new resource type id and
     *         resource type name. Returns an object with empty fields when the
     *         resource type already exists, so that UI can display the error
     *         message page
     * @throws IllegalArgumentException
     *             when the resourceTypeName is null or empty
     */
    @RequestMapping(value = "/addResourceType", method = RequestMethod.POST)
    public @ResponseBody ResourceType addResourceType(@RequestBody String typeName) {
        checkArgument(StringUtils.isNotBlank(typeName), INVALID_RESOURCE_TYPE_NAME);
        typeName = typeName.replaceAll("\\W", "");
        ResourceType resourceType = new ResourceType();
        try {
            resourceType = resourceTypeManager.addResourceType(typeName);
        } catch (final DuplicateResourceTypeFoundException e) {
            LOGGER.error("Resource type already exists with name" + typeName);
        }
        return resourceType;
    }

    /**
     * Returns ModelAndView object that contains view set as manage_requests.jsp
     * and model set with the data that needs to display all the resource
     * requests.
     *
     * @return {@link ModelAndView} object that redirects to manage requests
     *         page
     */
    @RequestMapping(value = "/show_requests", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView getAllRequests() {
        if (!authenticationStatus.isLoggedIn()) {
            return redirectsNotLoggedIn();
        }
        final ModelAndView modelAndView = new ModelAndView(MANAGE_REQUESTS_PAGE);
        try {
            final List<ResourceRequest> listOfRequests = resourceManager.getAllResourceRequests();
            if (listOfRequests == null || listOfRequests.isEmpty()) {
                modelAndView.addObject(ERROR_PAGE, i18nBundle.getString("resources.getAllRequests.noRecords"));
            }
            modelAndView.addObject(LIST_OF_REQUESTS, listOfRequests);
        } catch (final ManagerException managerException) {
            LOGGER.error(RESOURCE_REQUEST_DB_READ_ERROR, managerException);
            modelAndView.addObject(ERROR_PAGE, i18nBundle.getString("resources.getAllRequests.error"));
        }
        return modelAndView;
    }

    /**
     * Handles a batch delete on {@link ResourceRequest} with passed in batch of
     * requestIds.
     *
     * @param session
     *            {@link HttpSession} that contains objects related to current
     *            session (can't be <code>null</code>)
     * @param resourceRequestIds
     *            Batch of request IDs to be deleted.Cannot be null or empty.
     * @return {@link ModelAndView} that forwards to the manage requests page
     *         with success/error messages.
     * @throws IllegalArgumentException
     *             when session is <code>null.</code>
     */
    @RequestMapping(value = "/delete_requests", method = RequestMethod.POST)
    public ModelAndView batchDeleteResourceRequests(final HttpSession session,
            @RequestParam(value = "requestIds", required = false) final int[] resourceRequestIds) {
        checkArgument(session != null, SESSION_NULL_ERROR);
        checkArgument(ArrayUtils.isNotEmpty(resourceRequestIds), RESOURCE_REQUEST_IDS_ERROR);
        if (!authenticationStatus.isLoggedIn()) {
            return redirectsNotLoggedIn();
        }
        final ModelAndView modelAndView = new ModelAndView(FORWARD_TO_MANAGE_REQUESTS);
        if (ArrayUtils.isEmpty(resourceRequestIds)) {
            modelAndView.addObject(ERROR_MESSAGE,
                    i18nBundle.getString("resources.batchDeleteResourceRequests.requestIds.empty"));
            LOGGER.error(RESOURCE_REQUEST_IDS_ERROR);
            return modelAndView;
        }
        try {
            resourceRequestManager.deleteResourceRequestsInBatch(resourceRequestIds);
            modelAndView.addObject(SUCCESS_MESSAGE,
                    i18nBundle.getString("resources.batchDeleteResourceRequests.success"));
        } catch (final ManagerException managerException) {
            LOGGER.error(ERROR_DELETING_RESOURCE_REQUESTS, managerException);
            modelAndView.addObject(ERROR_MESSAGE, i18nBundle.getString("resources.batchDeleteResourceRequests.error"));
        }
        return modelAndView;
    }

    /**
     * Edits the resource from database for the resource id passed in.
     *
     * @param resourceId
     *            a unique ID used to find a specific {@link Resource}, It must
     *            be greater than 0.
     * @param resourceName
     *            a name used to identify a specific {@link Resource}, It must
     *            not be <code>null/empty/blank</code>.
     * @param resourceLink
     *            a unique URL used for a specific {@link Resource}, It must not
     *            be <code>null/empty/blank</code>.
     * @param resourceDifficultyLevel
     *            used to measure how difficult a specific {@link Resource} is.
     *            It must be greater than 0.
     * @param resourceType
     *            used specify the type of {@link Resource}, It must not be
     *            <code>null/empty/blank</code>.
     * @param resourceOwner
     *            a String representing the resourceOwner ID of the
     *            {@link Resource}, It cannot be <code>null/empty/blank</code>.
     * @return <code>true</code> if a resource with the given ID existed and was
     *         updated successfully, <code>false</code otherwise
     */
    @RequestMapping(value = "/edit/resource", method = RequestMethod.POST)
    public @ResponseBody boolean editResource(@RequestParam("id") final int resourceId,
            @RequestParam("resourceName") final String resourceName,
            @RequestParam("resourceLink") final URL resourceLink,
            @RequestParam("resourceDifficultyLevel") final int resourceDifficultyLevel,
            @RequestParam("resourceType") final String resourceType,
            @RequestParam("resourceOwner") final String resourceOwner) {
        checkArgument(resourceId > 0, INVALID_RESOURCE_ID_ERROR_MESSAGE);
        checkArgument(StringUtils.isNotBlank(resourceName), INVALID_RESOURCE_NAME_ERROR_MESSAGE);
        checkArgument(resourceLink != null, RESOURCE_LINK_NULL_ERROR_MESSAGE);
        checkArgument(HttpURLValidator.verifyURL(resourceLink), INVALID_RESOURCE_LINK_ERROR_MESSAGE);
        checkArgument(resourceDifficultyLevel > 0, INVALID_RESOURCE_LEVEL_ERROR_MESSAGE);
        checkArgument(StringUtils.isNotBlank(resourceType), INVALID_RESOURCE_TYPE_ERROR_MESSAGE);
        checkArgument(StringUtils.isNotBlank(resourceOwner), INVALID_RESOURCE_OWNER_ERROR_MESSAGE);
        try {
            return resourceManager.editResource(resourceId, resourceName, resourceLink, resourceDifficultyLevel,
                    resourceType, resourceOwner);
        } catch (final ManagerException managerException) {
            LOGGER.error(EDIT_ERROR, managerException);
            return false;
        }
    }

    /**
     * Retrieves a {@link List} of all {@link ResourceCategoryRelation
     * ResourceCategoryRelations} by the specified category name and difficulty
     * level.
     *
     * @param categoryName
     *            a name used to identify a specific
     *            {@link ResourceCategoryRelation} as to which {@link Category}
     *            it belongs to. It cannot be <code>null</code>/empty/blank.
     * @param difficultyLevel
     *            used to measure how difficult a specific
     *            {@link ResourceCategoryRelation} is. It cannot be less than or
     *            equal to 0.
     * @return a {@link List} of all {@link ResourceCategoryRelation
     *         ResourceCategoryRelations}, returns empty {@link List} when
     *         there's an error or when there's no
     *         {@link ResourceCategoryRelation} by the specified category name
     *         and difficulty level
     * @throws IllegalArgumentException
     *             when any of the following are true:
     *             <ul>
     *             <li>categoryName is <code>null</code>/blank/whitespace</li>
     *             <li>difficultyLevel is less than or equal to 0</li>
     *             </ul>
     */
    @RequestMapping(value = "/search/resource", method = RequestMethod.GET)
    public @ResponseBody List<ResourceCategoryRelation> searchResourcesByCategoryNameAndDifficultyLevel(
            @RequestParam("categoryName") final String categoryName,
            @RequestParam("difficultyLevel") final int difficultyLevel) {
        checkArgument(StringUtils.isNotBlank(categoryName), INVALID_CATEGORY_NAME_ERROR_MESSAGE);
        checkArgument(difficultyLevel > 0, INVALID_RESOURCE_LEVEL_ERROR_MESSAGE);
        List<ResourceCategoryRelation> allResources = new ArrayList<>();
        try {
            allResources = resourceCategoryRelationManager.searchResourcesByCategoryNameAndDifficultyLevel(categoryName,
                    difficultyLevel);
        } catch (final ManagerException managerException) {
            LOGGER.error(SEARCH_ERROR_MESSAGE, managerException);
        }
        return allResources;
    }
}