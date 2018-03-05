package com.cerner.devcenter.education.admin;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.cerner.devcenter.education.exceptions.ResourceIdNotFoundException;
import com.cerner.devcenter.education.exceptions.CategoryIdNotFoundException;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.ResourceCategoryRelation;
import com.cerner.devcenter.education.models.ResourceType;

/**
 * This interface defines operations for {@link ResourceCategoryRelation}
 * objects.
 *
 * @author Abhi Purella (AP045635)
 * @author Vincent Dasari (VD049645)
 * @author Santosh Kumar (SK051343)
 */
public interface ResourceCategoryRelationDAO {

    /**
     * Extracts List of ResourceCategoryRelation based on the passed in
     * categoryId.
     *
     * @param categoryId
     *            The categoryId used to uniquely identify a category (cannot be
     *            duplicated and should be greater than 0).
     * @return a {@link List} of {@link ResourceCategoryRelation} corresponding
     *         to the passed in categoryId from the database
     *
     * @throws DAOExeption
     *             when
     *             {@link JdbcTemplate#query(String, org.springframework.jdbc.core.RowMapper, Object...)}
     *             throws {@link DataAccessException}
     * @throws IllegalArgumentException
     *             when categoryId is not greater than 0.
     */
    public List<ResourceCategoryRelation> getResourcesAndDifficultyLevelByCategoryId(
            int categoryId,
            int resourcesLimit,
            int offSet) throws DAOException;

    /**
     * Adds a new relation between a resource and a category with difficulty level
     * to the database.
     *
     * @param resourceCategoryRelation
     *            a {@link ResourceCategoryRelation} that needs to be added to
     *            the category_resource_reltn table (cannot be null, resourceId
     *            and categoryId needs to be positive and must have a resource
     *            and a category associated with them).
     * @throws IllegalArgumentException
     *             when resourceId and categoryId are not greater then 0.
     * @throws DAOException
     *             when a mapping cannot be added to the category_resource_reltn
     *             table for any reason.
     * @throws ResourceIdNotFoundException
     *             when there is no resource found for given resourceId.
     * @throws CategoryIdNotFoundException
     *             when there is no category found for given categoryId.
     */
    void addResourceCategoryRelationWithDifficultyLevel(ResourceCategoryRelation resourceCategoryRelation)
            throws DAOException, ResourceIdNotFoundException, CategoryIdNotFoundException;

    /**
     * Extracts a {@link List} of {@link ResourceCategoryRelation} based on the
     * passed in id of {@link Category} and id of {@link ResourceType}.
     *
     * @param categoryId
     *            an {@link Integer} that uniquely identify a category (must be
     *            greater than 0)
     * @param resourceTypeId
     *            an {@link Integer} that identify the type of the resource
     *            (must be greater than 0)
     * @param resourcesLimit
     *            Number of resources to be retrieved from database. (must be
     *            greater than 0)
     * @param offSet
     *            Number of rows to be omitted before retrieving resources from
     *            database. (must be greater than or equal to 0)
     * @return a {@link List} of {@link ResourceCategoryRelation} corresponding
     *         to the passed in categoryId and resourceTypeId from the database
     * @throws DAOExeption
     *             when
     *             {@link JdbcTemplate#query(String, org.springframework.jdbc.core.RowMapper, Object...)}
     *             throws {@link DataAccessException}
     * @throws IllegalArgumentException
     *             when any of the following are true:
     *             <ul>
     *             <li>categoryId is less than or equal to 0</li>
     *             <li>resourceTypeId is less than or equal to 0</li>
     *             <li>resourcesLimit is less than or equal to 0</li>
     *             <li>offset is less than 0</li>
     *             </ul>
     */
    List<ResourceCategoryRelation> getResourcesByCategoryIdAndTypeIdWithPagination(
            int categoryId,
            int resourceTypeId,
            int resourcesLimit,
            int offSet) throws DAOException;

    /**
     * Extracts a {@link List} of {@link ResourceCategoryRelation
     * ResourceCategoryRelations}
     *
     * @return a {@link List} of all {@link ResourceCategoryRelation
     *         ResourceCategoryRelations}, returns empty when there are no
     *         {@link ResourceCategoryRelation ResourceCategoryRelations}
     * @throws DAOException
     *             when there's an error in retrieving all the
     *             {@link ResourceCategoryRelation ResourceCategoryRelations}
     */
    List<ResourceCategoryRelation> getAllResourcesAndAverageRatings() throws DAOException;

    /**
     * Retrieves a {@link List} of all {@link ResourceCategoryRelation
     * ResourceCategoryRelations} with the specified category name and difficulty
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
     * @return a {@link List} of {@link ResourceCategoryRelation
     *         ResourceCategoryRelations}, returns empty {@link List} when there
     *         are no {@link ResourceCategoryRelation ResourceCategoryRelations}
     *         with the specified category name and difficulty level
     * @throws DAOException
     *             when there's an error in retrieving all the
     *             {@link ResourceCategoryRelation ResourceCategoryRelations} with
     *             the specified category name and difficulty level
     * @throws IllegalArgumentException
     *             when any of the following are true:
     *             <ul>
     *             <li>categoryName is <code>null</code>/blank/whitespace</li>
     *             <li>difficultyLevel is less than or equal to 0</li>
     *             </ul>
     */
    List<ResourceCategoryRelation> searchResourcesByCategoryNameAndDifficultyLevel(
            final String categoryName,
            final int difficultyLevel) throws DAOException;

}