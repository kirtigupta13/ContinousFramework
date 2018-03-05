package com.cerner.devcenter.education.managers;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.admin.ResourceCategoryRelationDAO;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.ResourceCategoryRelation;

/**
 * <code>Manager</code> that acts as a <code>Service</code> between the
 * controller classes and the {@link ResourceCategoryRelationDAO} class.
 *
 * @author Abhi Purella (AP045635)
 * @author Vincent Dasari (VD049645)
 * @author Santosh Kumar (SK051343)
 */
@Service("resourceCategoryRelationManager")
public class ResourceCategoryRelationManager {

    private static final String GET_ALL_RESOURCES_ERROR_MESSAGE = "Error retrieving all the resources";
    private static final String SEARCH_ERROR_MESSAGE = "Error searching for resource category relation by category name and difficulty level";
    private static final String INVALID_CATEGORY_NAME_ERROR_MESSAGE = "Category Name can't be blank/empty/null";
    private static final String INVALID_RESOURCE_LEVEL_ERROR_MESSAGE = "Resource Difficulty Level must be greater than 0";
    private static final String INALID_CATEGORY_ID = "The category id should be greater than 0.";
    private static final String INALID_PAGE_NUMBER = "The category id should be greater than 0.";
    private static final String INALID_RESOURCE_PAGE = "The category id should be greater than 0.";

    @Autowired
    ResourceCategoryRelationDAO resourceCategoryRelationDAO;

    /**
     * Retrieves a {@link List} of {@link ResourceCategoryRelation} based on the
     * passed in categoryId.
     *
     * @param categoryId
     *            The categoryId used to uniquely identify a category(must be
     *            greater than zero).
     * @return a {@link List} of {@link ResourceCategoryRelation} corresponding
     *         to the passed in categoryId
     * @throws IllegalArgumentException
     *             when categoryId passed in is not greater than 0.
     */
    public List<ResourceCategoryRelation> getResourcesAndDifficultyLevelByCategoryIdWithPagination(
            final int categoryId,
            final int resourcesPerPage,
            final int pageNumber) {
        checkArgument(categoryId > 0, INALID_CATEGORY_ID);
        checkArgument(pageNumber > 0, INALID_PAGE_NUMBER);
        checkArgument(resourcesPerPage > 0, INALID_RESOURCE_PAGE);

        try {
            final int offset = (pageNumber - 1) * resourcesPerPage;
            return resourceCategoryRelationDAO
                    .getResourcesAndDifficultyLevelByCategoryId(categoryId, resourcesPerPage, offset);
        } catch (final DAOException daoException) {
            throw new ManagerException("Error retrieving resources for a particular categoryId", daoException);
        }
    }

    /**
     * Retrieves a {@link List} of {@link ResourceCategoryRelation} based on the
     * passed in categoryId and resourceTypeId.
     *
     * @param categoryId
     *            an {@link Integer} that uniquely identifies a category
     * @param resourceTypeId
     *            (must be greater than 0) an {@link Integer} that defines the
     *            type of the resource
     * @param resourcesPerPage
     *            (must be greater than 0) an {@link Integer} that is the number
     *            of resources that the user want to display in the page. (must
     *            be greater than 0)
     * @param pageNumber
     *            an {@link Integer} that is the current page number in the
     *            resources page. (must be greater than 0)
     * @return a {@link List} of {@link ResourceCategoryRelation} corresponding
     *         to the passed in categoryId and resourceTypeId
     * @throws IllegalArgumentException
     *             when any of the following are true:
     *             <ul>
     *             <li>categoryId is less than or equal to 0</li>
     *             <li>resourceTypeId is less than or equal to 0</li>
     *             <li>resourcesPerPage is less than or equal to 0</li>
     *             <li>pageNumber is less than or equal to 0</li>
     *             </ul>
     */
    public List<ResourceCategoryRelation> getResourcesByCategoryIdAndTypeIdWithPagination(
            final int categoryId,
            final int resourceTypeId,
            final int resourcesPerPage,
            final int pageNumber) {
        checkArgument(categoryId > 0, "Category ID is invalid because it is less than or equal to 0.");
        checkArgument(resourceTypeId > 0, "Resource Type ID is invalid because it is less than or equal to 0.");
        checkArgument(resourcesPerPage > 0, "Resources Per Page is invalid because it is less than or equal to 0.");
        checkArgument(pageNumber > 0, "Page Number is invalid because it is less than or equal to 0.");
        try {
            final int offset = (pageNumber - 1) * resourcesPerPage;
            return resourceCategoryRelationDAO.getResourcesByCategoryIdAndTypeIdWithPagination(
                    categoryId,
                    resourceTypeId,
                    resourcesPerPage,
                    offset);
        } catch (final DAOException e) {
            throw new ManagerException("Error retrieving resources for a particular category", e);
        }
    }

    /**
     * Retrieves a {@link List} of all {@link ResourceCategoryRelation
     * ResourceCategoryRelations}
     *
     * @return a {@link List} of {@link ResourceCategoryRelation
     *         ResourceCategoryRelations}, returns empty when there are no
     *         {@link ResourceCategoryRelation ResourceCategoryRelations}
     * @throws ManagerException
     *             when there's an error in retrieving all the
     *             {@link ResourceCategoryRelation ResourceCategoryRelations}
     *             from the database
     */
    public List<ResourceCategoryRelation> getResourcesForAllCategories() {
        try {
            return resourceCategoryRelationDAO.getAllResourcesAndAverageRatings();
        } catch (final DAOException daoException) {
            throw new ManagerException(GET_ALL_RESOURCES_ERROR_MESSAGE, daoException);
        }
    }

    /**
     * Retrieves a {@link List} of all {@link ResourceCategoryRelation
     * ResourceCategoryRelations} with the specified category name and
     * difficulty level.
     *
     * @param categoryName
     *            a name used to identify a specific
     *            {@link ResourceCategoryRelation} as to which {@link Category}
     *            it belongs to. It cannot be <code>null/empty/blank</code>.
     * @param difficultyLevel
     *            used to measure how difficult a specific
     *            {@link ResourceCategoryRelation} is. It cannot be less than or
     *            equal to 0.
     * @return a {@link List} of {@link ResourceCategoryRelation
     *         ResourceCategoryRelations}, returns empty {@link List} when there
     *         are no {@link ResourceCategoryRelation ResourceCategoryRelations}
     *         with the specified category name and difficulty level. Guaranteed
     *         to not be <code>null</code> or contain <code>null</code>
     *         elements.
     * @throws ManagerException
     *             when there's an error in retrieving all the
     *             {@link ResourceCategoryRelation ResourceCategoryRelations}
     *             with the specified category name and difficulty level from
     *             the database
     * @throws IllegalArgumentException
     *             when any of the following are true:
     *             <ul>
     *             <li>categoryName is <code>null</code>/blank/whitespace</li>
     *             <li>difficultyLevel is less than or equal to 0</li>
     *             </ul>
     */
    public List<ResourceCategoryRelation> searchResourcesByCategoryNameAndDifficultyLevel(
            final String categoryName,
            final int difficultyLevel) {
        checkArgument(StringUtils.isNotBlank(categoryName), INVALID_CATEGORY_NAME_ERROR_MESSAGE);
        checkArgument(difficultyLevel > 0, INVALID_RESOURCE_LEVEL_ERROR_MESSAGE);
        List<ResourceCategoryRelation> listOfResourceCategoryRelation = new ArrayList<>();
        try {
            listOfResourceCategoryRelation = resourceCategoryRelationDAO
                    .searchResourcesByCategoryNameAndDifficultyLevel(categoryName, difficultyLevel);
        } catch (final DAOException daoException) {
            throw new ManagerException(SEARCH_ERROR_MESSAGE, daoException);
        }
        if (listOfResourceCategoryRelation == null) {
            return Collections.emptyList();
        } else {
            listOfResourceCategoryRelation.removeAll(Collections.singleton(null));
        }
        return listOfResourceCategoryRelation;
    }
}