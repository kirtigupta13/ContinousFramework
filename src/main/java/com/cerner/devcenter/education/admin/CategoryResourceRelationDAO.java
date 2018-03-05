package com.cerner.devcenter.education.admin;

import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.Resource;

/**
 * This interface is responsible for performing database operations for relationships between
 * {@link Category} and {@link Resource}.
 *
 * @author Piyush Bandil (PB042879)
 * @author James Kellerman (JK042311)
 *
 */
public interface CategoryResourceRelationDAO {

    /**
     * Adds a new relation between a resource and a category to the database.
     *
     * @param resource
     *            a {@link Resource} that we wish to add a relationship for in
     *            the database (Must not be null, ID must be a positive number,
     *            ID must be a resource key in the database)
     * @param category
     *            a {@link Category} that we wish to add a relationship for in
     *            the database (Must not be null, ID must be a positive number,
     *            ID must be a category key in the database
     * @throws IllegalArgumentException
     *             when the passed IDs are not primary keys in their respective
     *             tables
     * @throws NullPointerException
     *             when the either resource or category are passed as null
     * @throws DAOException
     *             when a mapping is unable to be added to the database for any
     *             reason
     */
     void addMappingsToDB(Resource resource, Category category) throws DAOException;
    
    /**
     * Deletes an existing category-resource mapping from the
     * category_resource_reltn table using the {@link Resource} id.
     * 
     * @param resourceId
     *            the resource id of the category-resource relation that needs
     *            to be deleted.
     * @throws DAOException
     *             when there is an error while attempting to delete from the
     *             database
     * @throws IllegalArgumentException
     *             when id of {@link Resource} is less than or equal to zero
     */
     void deleteById(int resourceId) throws DAOException;
}
