package com.cerner.devcenter.education.admin;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cerner.devcenter.education.models.ResourceType;

/**
 * This interface defines operations for {@link ResourceType} data objects.
 *
 * @author Gunjan Kaphle (GK045931)
 * @author JZ022690
 */
public interface ResourceTypeDAO {

    /**
     * Performs a query on the data source that will return a {@link List} object containing all of
     * the types of resources in the database.
     * 
     * @return a {@link List} of {@link ResourceType} objects
     * 
     * @throws DAOException
     *             when there is an error while trying to get all resources from the data source
     * 
     */
    List<ResourceType> getAllResourceTypes() throws DAOException;

    /**
     * Returns a {@link ResourceType} object that has the same value as the id that is passed in.
     * The id should be greater than zero.
     * 
     * @param id
     *            an {@link Integer} that represents the primary key of the resourceType. Id must be
     *            greater than zero.
     * 
     * @return a {@link ResourceType} object
     * 
     * @throws DAOException
     *             when there is an error while trying to get a resource type from the data source
     * 
     */
    ResourceType getById(int id) throws DAOException;

    /**
     * Returns a {@link ResourceType} object that has the same value as the name that is passed in.
     * 
     * @param name
     *            a {@link String} that represents the name of the resourceType. name cannot be null
     *            or empty.
     * 
     * @return a {@link ResourceType} object
     * 
     * @throws DAOException
     *             when there is an error while trying to get a resource type from the data source
     * @throws IllegalArgumentException
     *             when the name is null or empty.
     */
    ResourceType getByName(String name) throws DAOException;

    /**
     * Adds a new resourceType to the resource type table. Takes the the resource type name as an
     * input. The name cannot be <code>null</code>.
     *
     * @param resourceTypeName
     *            {@link String} name that defines the type of the resource. Cannot be
     *            <code>null</code>
     * 
     * @return a {@link ResourceType} object that represents what was added to the database
     * 
     * @throws DAOExeption
     *             when {@link JdbcTemplate#update(String, Object...)} throws
     *             {@link DataAccessException}
     */
    ResourceType addResourceType(String resourceTypeName) throws DAOException;
}
