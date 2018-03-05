package com.cerner.devcenter.education.managers;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.admin.ResourceTypeDAO;
import com.cerner.devcenter.education.exceptions.DuplicateResourceTypeFoundException;
import com.cerner.devcenter.education.models.ResourceType;

/**
 * This class is used as a connects or links between the controller classes and
 * the {@link ResourceTypeDAO} class.
 * 
 * @author Gunjan Kaphle (GK045931)
 */

@Service("resourceTypeManager")
public class ResourceTypeManager {

    private ResourceBundle i18nBundle;

    @Autowired
    ResourceTypeDAO resourceTypeDAO;

    public ResourceTypeManager() {
        i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());
    }

    /**
     * Gets the resource type repository.
     * 
     * @return the resourceTypeDAO
     */
    public ResourceTypeDAO getResourceTypeDAO() {
        return resourceTypeDAO;
    }

    /**
     * Sets the resource type repository.
     * 
     * @param resourceTypeDAO
     *            the resource type DAO to set
     */
    public void setResourceTypeDAO(ResourceTypeDAO resourceTypeDAO) {
        this.resourceTypeDAO = resourceTypeDAO;
    }

    /**
     * Adds a resourceType.
     *
     * @param resourceTypeName
     *            a {@link String} object is the name of the resource type
     *            entered by the user
     * @return instance of {@link ResourceType}
     * @throws ManagerException
     *             when a manager is not able to access the data source
     * @throws {@link DuplicateResourceTypeFoundException} when resource type
     *             already exists
     */
    public ResourceType addResourceType(String resourceTypeName) {
        try {
            checkArgument(StringUtils.isNotBlank(resourceTypeName), "Resource Type cannot be empty or null.");
            if (checkIfResourceTypeAlreadyExists(resourceTypeName)) {
                throw new DuplicateResourceTypeFoundException("Resource Type already exists in the database.");
            }
            return resourceTypeDAO.addResourceType(resourceTypeName);
        } catch (DAOException e) {
            throw new ManagerException("Error adding resource type", e);
        }
    }

    /**
     * Check to see if resourceType Already exists or not
     * 
     * @param resourceType
     *            a {@link String} that is the type of the resource that we use
     *            to check if it exists in database
     * @return true if the type already exists in the database
     * @throws DuplicateResourceTypeFoundException
     *             if the resource type already exists in the database
     */
    boolean checkIfResourceTypeAlreadyExists(String resourceType) throws DAOException {
        List<ResourceType> resourceTypes = getAllResourceTypes();
        for (ResourceType singleResourceType : resourceTypes) {
            if (StringUtils.equalsIgnoreCase(singleResourceType.getResourceType(), resourceType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves a {@link ResourceType} for the provided id.
     * 
     * @param resourceTypeId
     *            represents the unique id of the resource type to be retrieved.
     *            Must be a positive integer.
     * @return {@link ResourceType} for the provided id, if no resource with
     *         this key can be found, null is returned
     * @throws ManagerException
     *             when a manager is not able to access the data source and
     *             catches {@link DAOException}.
     */
    public ResourceType getResourceTypeById(int resourceTypeId) {
        checkArgument(resourceTypeId > 0,
                i18nBundle.getString("com.cerner.devcenter.education.admin.ResourceTypeDAOImpl.typeIdInvalid"));
        try {
            return resourceTypeDAO.getById(resourceTypeId);
        } catch (DAOException e) {
            throw new ManagerException("Error retrieving resource by its id", e);
        }
    }

    /**
     * This method retrieves a list of all resource types.
     *
     * @return a {@link List} of all {@link ResourceType}
     * 
     * @throws ManagerException
     *             when a manager is not able to access the data source and
     *             catches {@link DAOException}.
     */
    public List<ResourceType> getAllResourceTypes() {
        try {
            return resourceTypeDAO.getAllResourceTypes();
        } catch (DAOException e) {
            throw new ManagerException("Error retrieving all resource types from the data source", e);
        }
    }
}
