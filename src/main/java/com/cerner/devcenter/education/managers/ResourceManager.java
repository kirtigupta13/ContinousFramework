package com.cerner.devcenter.education.managers;

import static com.google.common.base.Preconditions.checkArgument;

import java.net.URL;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cerner.devcenter.education.admin.CategoryResourceRelationDAO;
import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.admin.ResourceDAO;
import com.cerner.devcenter.education.admin.ResourceCategoryRelationDAO;
import com.cerner.devcenter.education.dao.ResourceRequestDAO;
import com.cerner.devcenter.education.exceptions.ItemAlreadyExistsException;
import com.cerner.devcenter.education.exceptions.ResourceIdNotFoundException;
import com.cerner.devcenter.education.exceptions.CategoryIdNotFoundException;
import com.cerner.devcenter.education.helpers.HttpURLValidator;
import com.cerner.devcenter.education.helpers.RoundToHigherIntHelper;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.models.ResourceRequest;
import com.cerner.devcenter.education.models.ResourceCategoryRelation;
import com.cerner.devcenter.education.user.UserDetails;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.google.common.base.Preconditions;

/**
 * Used as a <code>Manager</code> that will act as a <code>Service</code>
 * between the controller classes and the {@link ResourceDAO} class.
 *
 * @author Samuel Stephen (SS044662)
 * @author Wuchen Wang (WW044343)
 * @author Jacob Zimmermann (JZ022690)
 * @author Navya Rangeneni (NR046827)
 * @author Vincent Dasari (VD049645)
 * @author Vatsal Kesarwani (VK049896)
 * @author Rishabh Bhojak (RB048032)
 */
@Service("resourceManager")
public class ResourceManager {
    private static final String RESOURCE_CANNOT_BE_NULL = "Resource argument cannot be null";
    private static final String RESOURCE_EXISTS_ERROR = "Resource {0} already exists in resource table";
    private static final String CATEGORY_RELATION_ERROR = "Error adding resource and its relation to a particular category";
    private static final String INSERT_RESOURCE_FAILURE = "Error while adding a resource";

    private static final String RESOURCE_ID_NOT_POSITIVE = "Resource Id must be positive";
    private static final String RESOURCE_NAME_INVALID = "Resource name is invalid";
    private static final String RESOURCE_LINK_NULL = "Resource Link can not be null";
    private static final String RESOURCE_TYPE_NULL = "Resource Type can not be null";
    private static final String EDIT_RESORUCE_ERROR = "Error editing resource using its ID";
    private static final String RESOURCE_ID_INVALID = "ResourceID must be greater than 0";
    private static final String RESOURCE_LEVEL_INVALID = "Resource level must be greater than 0";
    private static final String RESOURCE_NAME_EMPTY = "Resource Name can't be empty/blank";
    private static final String RESOURCE_LINK_EMPTY = "Resource Link can't be empty/blank";
    private static final String RESOURCE_TYPE_EMPTY = "Resource Type can't be empty/blank";
    private static final String RESOURCE_OWNER_ERROR_MESSAGE = "Resource owner cannot be null/empty/blank";
    private static final String RESOURCE_DESCRIPTION_INVALID = "Resource Description cannot be null/empty/blank";
    private static final String RESOURCE_STATUS_NULL_ERROR_MESSAGE = "Resource status cannot be null";
    private static final String RESOURCE_STATUS_INVALID = "Resource status must be Available/Pending/Deleted";
    private static final String CATEGORY_DIFFICULTY_MAP_INVALID = "Resource Difficulty for Category map cannot be null/empty";
    private static final String CATEGORY_DIFFICULTY_MAP_NULL_KEY = "Resource Difficulty for Category map cannot have a null key";
    private static final String CATEGORY_DIFFICULTY_MAP_NULL_VALUE = "Resource Difficulty for Category map cannot have null value(s)";

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceManager.class);

    private final ResourceBundle i18nBundle;
    @Autowired
    ResourceDAO resourceDAO;
    @Autowired
    CategoryResourceRelationDAO categoryResourceRelationDAO;
    @Autowired
    ResourceCategoryRelationDAO resourceCategoryRelationDAO;
    @Autowired
    ResourceRequestDAO resourceRequestDAO;

    public ResourceManager() {
        i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());
    }

    /**
     * Adds a {@link Resource} and any relations it has with {@link Category}.
     *
     * @param resource
     *            {@link Resource} object that contains the information to be
     *            added. Cannot be null. Name cannot be null, blank, or empty.
     * @throws ItemAlreadyExistsException
     *             when the resource already exists in the database
     * @throws ManagerException
     *             when a manger is not able to access the data source and
     *             catches {@link DAOException}.
     */
    public void addResourceAndRelations(final Resource resource) throws ItemAlreadyExistsException {
        try {
            checkArgument(resource != null, RESOURCE_CANNOT_BE_NULL);
            checkArgument(StringUtils.isNotBlank(resource.getResourceName()), RESOURCE_NAME_INVALID);
            if (checkResourceExists(resource.getResourceName())) {
                throw new ItemAlreadyExistsException(
                        MessageFormat.format(RESOURCE_EXISTS_ERROR, resource.getResourceName()));
            }
            resource.setResourceId(
                    resourceDAO.addResource(
                            resource.getDescription(),
                            resource.getResourceName(),
                            resource.getResourceLink(),
                            resource.getResourceType(),
                            resource.getResourceOwner(),
                            resource.getResourceStatus()));
            for (final Category category : resource.getCategories()) {
                categoryResourceRelationDAO.addMappingsToDB(resource, category);
            }
        } catch (final DAOException dAOException) {
            throw new ManagerException(CATEGORY_RELATION_ERROR, dAOException);
        }
    }

    /**
     * Adds a resource and any relations it has with categories.
     *
     * @param resource
     *            a {@link Resource} object that contains the information to be
     *            added (Must not be null, Must not have a null description,
     *            Must not have a null link, Must not have any categories that
     *            are null)
     * @return {@link Resource} which is added otherwise return null.
     * @throws ManagerException
     *             When manager:
     *             <ul>
     *             <li>is not able to insert a resource and catches
     *             {@link DAOException}</li>
     *             <li>is not able to insert a relation of resource and a
     *             category and catches {@link DAOException}</li>
     *             </ul>
     * @throws IllegalArgumentException
     *             <ul>
     *             <li>when resource is null</li>
     *             <li>when name of the resource is null/blank/empty</li>
     *             <li>when description of the resource is null/blank/empty</li>
     *             <li>when type of the resource is null</li>
     *             <li>when link of the resource is null</li>
     *             <li>when resourceStatus of the resource is <code>null</code>
     *             or not Available/Pending/Deleted</li>
     *             <li>when resourceDifficultyLevelForCategory map is
     *             null/empty</li>
     *             </ul>
     */
    public Resource addResourceCategoryRelationWithDifficultyLevel(final Resource resource)
            throws ResourceIdNotFoundException, CategoryIdNotFoundException {
        checkArgument(resource != null, RESOURCE_CANNOT_BE_NULL);
        checkArgument(StringUtils.isNotBlank(resource.getResourceName()), RESOURCE_NAME_EMPTY);
        checkArgument(StringUtils.isNotBlank(resource.getDescription()), RESOURCE_DESCRIPTION_INVALID);
        checkArgument(resource.getResourceType() != null, RESOURCE_TYPE_NULL);
        checkArgument(resource.getResourceLink() != null, RESOURCE_LINK_NULL);
        checkArgument(StringUtils.isNotBlank(resource.getResourceOwner()), RESOURCE_OWNER_ERROR_MESSAGE);
        checkArgument(resource.getResourceStatus() != null, RESOURCE_STATUS_NULL_ERROR_MESSAGE);
        checkArgument(
                !CollectionUtils.isEmpty(resource.getResourceDifficultyForCategory()),
                CATEGORY_DIFFICULTY_MAP_INVALID);
        try {
            resource.setResourceId(
                    resourceDAO.addResource(
                            resource.getDescription(),
                            resource.getResourceName(),
                            resource.getResourceLink(),
                            resource.getResourceType(),
                            resource.getResourceOwner(),
                            resource.getResourceStatus()));
        } catch (final DAOException daoException) {
            throw new ManagerException(INSERT_RESOURCE_FAILURE, daoException);
        }
        for (final Map.Entry<Integer, Integer> entry : resource.getResourceDifficultyForCategory().entrySet()) {
            try {
                resourceCategoryRelationDAO.addResourceCategoryRelationWithDifficultyLevel(
                        new ResourceCategoryRelation(resource.getResourceId(), entry.getValue(), entry.getKey()));
            } catch (DAOException daoException) {
                throw new ManagerException(CATEGORY_RELATION_ERROR, daoException);
            }
        }
        return resource;
    }

    /**
     * Retrieves a {@link Resource} for the provided id.
     *
     * @param resourceId
     *            represents the id of the resource to be retrieved. Must be a
     *            positive integer.
     * @return {@link Resource} for the provided id, if no resource with this
     *         key can be found, null is returned
     * @throws ManagerException
     *             when a manager is not able to access the data source and
     *             catches {@link DAOException}.
     */
    public Resource getResourceById(final int resourceId) {
        checkArgument(resourceId > 0, i18nBundle.getString("com.cerner.devcenter.education.admin.idInvalid"));
        try {
            return resourceDAO.getById(resourceId);
        } catch (final DAOException daoException) {
            throw new ManagerException("Error retrieving resource by its id", daoException);
        }
    }

    /**
     * Deletes a {@link Resource} and the mapping between that {@link Resource}
     * and {@link Category}
     *
     * @param resourceId
     *            Unique Id of the {@link Resource} object to be deleted, it
     *            should be greater than zero.
     */
    public void deleteResource(final int resourceId) {
        checkArgument(resourceId > 0, RESOURCE_ID_NOT_POSITIVE);
        try {
            resourceDAO.deleteById(resourceId);
        } catch (final DAOException daoException) {
            throw new ManagerException("Error deleting resource using its ID", daoException);
        }
    }

    /**
     * Retrieves {@link List} of {@link Resource} based on the passed
     * categoryId.
     *
     * @param categoryId
     *            The ID used to uniquely identify a category
     * @return a {@link List} of {@link Resource} corresponding to the passed
     *         categoryId
     * @throws ManagerException
     *             when a manager is not able to access the data source and
     */
    public List<Resource> getResourcesByCategoryId(final int categoryId) {
        checkArgument(categoryId > 0, i18nBundle.getString("com.cerner.devcenter.education.admin.idInvalid"));
        try {
            return resourceDAO.getResourcesByCategoryId(categoryId);
        } catch (final DAOException daoException) {
            throw new ManagerException("Error retrieving resources for a particular category", daoException);
        }
    }

    /**
     * Calculates total number of pages required for displaying resources per
     * particular category
     *
     * @param categoryId
     *            The ID used to uniquely identify a category, it must be
     *            greater than zero.
     * @param resourceLimit
     *            Maximum number of resources could be visible per page.
     * @return number of pages required to display resources for particular
     *         category.
     * @throws ManagerException
     *             when there is an error accessing data and
     *             {@link DAOException} is caught.
     */
    public int getPageCountForResourcesByCategoryId(final int categoryId, final int resourceLimit)
            throws ManagerException {
        checkArgument(categoryId > 0, "Category Id must be greater than zero");
        try {
            final int resourcesCount = resourceDAO.getResourceCountByCategoryId(categoryId);
            return RoundToHigherIntHelper.roundToHigherInt(resourcesCount, resourceLimit);
        } catch (final DAOException daoException) {
            throw new ManagerException("Error retrieving number of resources for a particular category", daoException);
        }
    }

    /**
     * Get the resource repository.
     *
     * @return the resourceDAO
     */
    public ResourceDAO getResourceDAO() {
        return resourceDAO;
    }

    /**
     * Set the resource repository.
     *
     * @param resourceDAO
     *            the resourceDAO to set
     */
    public void setResourceDAO(final ResourceDAO resourceDAO) {
        this.resourceDAO = resourceDAO;
    }

    /**
     * Get the categoryResourceRelation repository
     *
     * @return the categoryResourceRelationDAO
     */
    public CategoryResourceRelationDAO getCategoryResourceRelationDAO() {
        return categoryResourceRelationDAO;
    }

    /**
     * Set the categoryResourceRelation repository
     *
     * @param categoryResourceRelationDAO
     *            the categoryResourceRelationDAO to set
     */
    public void setCategoryResourceRelationDAO(final CategoryResourceRelationDAO categoryResourceRelationDAO) {
        this.categoryResourceRelationDAO = categoryResourceRelationDAO;
    }

    /**
     * Retrieve the resource description for the specified resource id.
     *
     * @param resourceId
     *            a unique id used to find a specific {@link Resource}
     * @return resource description if operation is successful, otherwise
     *         returns null.
     */
    public String retrieveResourceDescriptionById(final int resourceId) {
        checkArgument(resourceId > 0, RESOURCE_ID_NOT_POSITIVE);
        return resourceDAO.getResourceDescriptionById(resourceId);
    }

    /**
     * This method retrieves a list of resources based on search string.
     *
     * @param search
     *            {@link String} user entered to search in the resources
     * @return a {@link List} of all {@link Resource}
     * @throws ManagerException
     *             when a manager is not able to access the data source and
     *             catches {@link DAOException}
     * @throws IllegalArgumentException
     *             when search string is empty or null
     */
    public List<Resource> getSearchedResources(final String search) {
        Preconditions.checkArgument(
                StringUtils.isNotEmpty(search),
                i18nBundle.getString("com.cerner.devcenter.education.search.invalid"));

        try {
            return resourceDAO.getSearchedResources(search);
        } catch (final DAOException daoException) {
            throw new ManagerException("Error retrieving searched resources from the database", daoException);
        }
    }

    /***
     * This method checks if a resource exists in the database with the given
     * resource name.
     *
     * @param resourceName
     *            {@link String} containing the resource name to check against
     *            the database. Cannot be null, blank, or empty.
     * @return <code> boolean </code>
     *         <li>true - if a resource exists in the database with the provided
     *         resource name</li>
     *         <li>false - if no resource exists in the database with the
     *         provided resource name</li>
     * @throws IllegalArgumentException
     *             if resourceName is null, blank, or empty
     * @throws ManagerException
     *             if there is an error checking the database
     */
    public boolean checkResourceExists(final String resourceName) {
        checkArgument(StringUtils.isNotBlank(resourceName), RESOURCE_NAME_INVALID);
        try {
            return resourceDAO.checkResourceExists(resourceName);
        } catch (final DAOException daoException) {
            throw new ManagerException("Error checking resource name against data", daoException);
        }
    }

    /**
     * This method retrieves a list of all resource requests.
     *
     * @return a {@link List} of all {@link ResourceRequest}
     *
     * @throws ManagerException
     *             when a manager is unable to access data through
     *             {@link ResourceRequestDAO}
     *
     */
    public List<ResourceRequest> getAllResourceRequests() throws ManagerException {
        try {
            return resourceRequestDAO.getAllResourceRequests();
        } catch (final DAOException daoException) {
            LOGGER.error("Error retrieving requested resources from the database", daoException);
            throw new ManagerException("Error retrieving all resource requests from the database", daoException);
        }
    }

    /**
     * Edits a {@link Resource} and the mapping between that {@link Resource}
     * and {@link Category}
     *
     * @param resourceId
     *            a unique ID used to find a specific {@link Resource}, It must
     *            be greater than 0.
     * @param resourceName
     *            a name used to identify a specific {@link Resource}, It must
     *            not be <code>null/empty/blank</code>.
     * @param resourceLink
     *            a unique URL used for a specific {@link Resource}, It must not
     *            be <code>null</code>.
     * @param resourceDifficultyLevel
     *            used to measure how difficult a specific {@link Resource} is.
     *            It must be greater than 0.
     * @param resourceType
     *            used specify the type of {@link Resource}, It must not be
     *            <code>null/empty/blank</code>.
     * @param resourceOwner
     *            a String representing the resourceOwner ID of the
     *            {@link Resource}, It must not be
     *            <code>null/empty/blank</code>.
     * @return false when
     *         <ul>
     *         <li>userProfileDetails is <code>null</code></li>
     *         <li>userProfileDetails userId is <code>null</code></li>
     *         </ul>
     *         true when the resource has been successfully updated
     * @throws ManagerException
     *             when
     *             <ul>
     *             <li>manager is not able to access the data source and catches
     *             {@link DAOException}</li>
     *             <li>LDAP search fails for the user and catches
     *             {@link NamingException}</li>
     *             </ul>
     */
    public boolean editResource(
            final int resourceId,
            final String resourceName,
            final URL resourceLink,
            final int resourceDifficultyLevel,
            final String resourceType,
            final String resourceOwner) {
        checkArgument(resourceId > 0, RESOURCE_ID_INVALID);
        checkArgument(StringUtils.isNotBlank(resourceName), RESOURCE_NAME_EMPTY);
        checkArgument(resourceLink != null, RESOURCE_LINK_NULL);
        checkArgument(HttpURLValidator.verifyURL(resourceLink), RESOURCE_LINK_EMPTY);
        checkArgument(resourceDifficultyLevel > 0, RESOURCE_LEVEL_INVALID);
        checkArgument(StringUtils.isNotBlank(resourceType), RESOURCE_TYPE_EMPTY);
        checkArgument(StringUtils.isNotBlank(resourceOwner), RESOURCE_OWNER_ERROR_MESSAGE);
        try {
            final UserDetails userDetail = new UserDetails();
            final UserProfileDetails userProfileDetails = userDetail.getUserDetails(resourceOwner);
            if (userProfileDetails == null || userProfileDetails.getUserId().equals(null)) {
                return false;
            } else {
                resourceDAO.updateResource(
                        resourceId,
                        resourceName,
                        resourceLink,
                        resourceDifficultyLevel,
                        resourceType,
                        resourceOwner);
                return true;
            }
        } catch (final DAOException daoException) {
            throw new ManagerException(EDIT_RESORUCE_ERROR, daoException);
        } catch (final NamingException namingException) {
            throw new ManagerException(EDIT_RESORUCE_ERROR, namingException);
        }
    }
}
