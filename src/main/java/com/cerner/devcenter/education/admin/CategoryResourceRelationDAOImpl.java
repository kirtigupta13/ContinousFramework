package com.cerner.devcenter.education.admin;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.Resource;

/**
 * Creates the relationship between each category and its respective resources.
 *
 * @author NO032013
 * @author Piyush Bandil(PB042879)
 * @author James Kellerman (JK042311)
 * @author Laasya Kuppam(LK043600)
 * @author Surbhi Singh (SS043472)
 * @author Santosh Kumar (SK051343)
 */
@Repository("categoryResourceRelationDAO")
public class CategoryResourceRelationDAOImpl implements CategoryResourceRelationDAO {
    private final static String INSERT_CATEGORY_RESOURCE_QUERY = "INSERT INTO category_resource_reltn (category_id, resource_id) VALUES(?,?)";
    private final static String DELETE_CATEGORY_RESOURCE_QUERY = "DELETE FROM category_resource_reltn WHERE resource_id=?";

    private final static String RESOURCE_ID_NOT_FOUND = "Resource not found in database with id =";
    private final static String CATEGORY_ID_NOT_FOUND = "Category not found in database with id =";
    private final static String INVALID_RESOURCE_ID = "Resource id to search for must be greater than 0";
    private final static String INVALID_CATEGORY_ID = "Id to search for cannot be negative";
    private final static String DB_ACCESSING_ERROR = "There was an error while attempting to access to database";
    private final static String NULL_RESOURCE = "The resource can not be null";
    private final static String NULL_CATEGORY = "The category can not be null";

    @Autowired
    private ResourceDAO resourceDAO;
    @Autowired
    private CategoryDAO categoryDAO;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Adds a new relation between a resource and a category to the database.
     * 
     * @throws DAOException
     *             when a mapping is unable to be added to the database
     */
    @Override
    public void addMappingsToDB(Resource resource, Category category) throws DAOException {
        int categoryId = category.getId();
        int resourceId = resource.getResourceId();
        checkNotNull(resource, NULL_RESOURCE);
        checkNotNull(category, NULL_CATEGORY);
        checkArgument(resource.getResourceId() > 0, INVALID_RESOURCE_ID);
        checkArgument(category.getId() > 0, INVALID_CATEGORY_ID);
        checkNotNull(resourceDAO.getById(resource.getResourceId()), RESOURCE_ID_NOT_FOUND + resource.getResourceId());
        checkNotNull(categoryDAO.getById(category.getId()), CATEGORY_ID_NOT_FOUND + category.getId());
        try {
            jdbcTemplate.update(INSERT_CATEGORY_RESOURCE_QUERY, categoryId, resourceId);
        } catch (DataAccessException daoException) {
            throw new DAOException(DB_ACCESSING_ERROR, daoException);
        }
    }

    /**
     * Deletes an existing category-resource mapping from the
     * category_resource_reltn table using the {@link Resource} id.
     * 
     * @throws DAOException
     *             when there is an error while attempting to delete from the
     *             database
     */
    @Override
    public void deleteById(int resourceId) throws DAOException {
        checkArgument(resourceId > 0, INVALID_RESOURCE_ID);
        try {
            jdbcTemplate.update(DELETE_CATEGORY_RESOURCE_QUERY, resourceId);
        } catch (DataAccessException daoException) {
            throw new DAOException(DB_ACCESSING_ERROR, daoException);
        }
    }
}
