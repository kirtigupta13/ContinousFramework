package com.cerner.devcenter.education.admin;

import java.net.URL;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.models.ResourceType;

/**
 * This interface defines operations for {@link Resource} objects.
 *
 * @author NO032013
 * @author Piyush Bandil (PB042879)
 * @author James Kellerman (JK042311)
 * @author Chaitali Kharangate (CK042502)
 * @author Jacob Zimmermann (JZ022690)
 * @author Vincent Dasari (VD049645)
 * @author Rishabh Bhojak (RB048032)
 * @author Santosh Kumar (SK051343)
 */
public interface ResourceDAO {

    /**
     * Performs a query on the data source that will return a {@link List}
     * containing the resources that are currently searched.
     *
     * @param search
     *            {@link String} the user entered to search (cannot be empty or
     *            null)
     *
     * @return a {@link List} of {@link Resource} objects
     *
     * @throws DAOException
     *             when there is an error while trying to get all resources from
     *             the data source
     * @throws IllegalArgumentException
     *             when search is null or empty
     */
    List<Resource> getSearchedResources(String search) throws DAOException;

    /**
     * Returns a {@link Resource} from a database that has the same id as the
     * passed in value.
     *
     * @param id
     *            an {@link Integer} that represents the primary key of the
     *            resource we wish to retrieve from the database
     * @return {@link Resource} from the database that has the same primary key
     *         as the passed in id, if no resource with this key can be found,
     *         null is returned
     * @throws IllegalArgumentException
     *             when id is non-positive
     * @throws DAOException
     *             when
     *             {@link JdbcTemplate#queryForObject(String, Class, Object...)}
     *             throws {@link DataAccessException}
     */
    Resource getById(int id) throws DAOException;

    /**
     * Adds a new resource to the resource table. Takes the input name,
     * description, link url, and resource type and adds them to the resource
     * table.
     *
     * @param description
     *            {@link String} description of the resource to be added to the
     *            DB (Cannot be null, blank, or empty)
     * @param name
     *            {@link String} name of the resource to be added to the DB
     *            (Cannot be null, blank, or empty)
     * @param resourceLink
     *            A {@link URL} object to the location of the resource to add.
     *            It must be a valid URL with schema of either http or https and
     *            cannot be a null value
     * @param resourceType
     *            A {@link ResourceType} object that defines the type of the
     *            resource. It cannot be a null value.
     * @param resourceOwner
     *            A {@link String} that represents the userId of the user
     *            uploaded the resource. It cannot be a null value.
     * @param resourceStatus
     *            A {@link String} that represents the status of the resource.
     *            It must be Available/Pending/Deleted.
     * @return a {@code int} containing the new resource id for the resource
     *         that was added.
     * @throws DAOException
     *             when {@link JdbcTemplate#update(String, Object...)} throws
     *             {@link DataAccessException}
     * @throws IllegalArgumentException
     *             <ul>
     *             <li>when the description is null/blank/empty</li>
     *             <li>when the name is null/blank/empty</li>
     *             <li>when the resourceLink is not valid or null</li>
     *             <li>when the resourceType is null</li>
     *             <li>when the resourceOwner is null/blank/empty</li>
     *             <li>when resourceStatus is <code>null</code> or not
     *             Available/Pending/Deleted.</li>
     *             </ul>
     */
    int addResource(
            final String description,
            final String name,
            final URL resourceLink,
            final ResourceType resourceType,
            final String resourceOwner,
            final String resourceStatus) throws DAOException;

    /**
     * Deletes the {@link Resource} with the given resource ID from the resource
     * table.
     *
     * @param resourceId
     *            integer representing the unique identifier of the
     *            {@link Resource} (cannot be negative).
     * @throws DAOException
     *             when there is an error while attempting to delete from the
     *             database
     * @throws IllegalArgumentException
     *             when resourceId is zero or negative
     */
    void deleteById(int resourceId) throws DAOException;

    /**
     * Retrieves resource description by resource id.
     *
     * @param resourceId
     *            a unique id used to find a specific {@link Resource}
     * @return resource description if operation is successful, otherwise
     *         returns null
     */
    String getResourceDescriptionById(int resourceId);

    /**
     * Extracts List of Resource based on the passed categoryId.
     *
     * @param categoryId
     *            The ID used to uniquely identify a category
     * @return a {@link List} of {@link Resource} corresponding to the passed
     *         categoryId from the database
     *
     * @throws DAOException
     *             when
     *             {@link JdbcTemplate#query(String, org.springframework.jdbc.core.RowMapper, Object...)}
     *             throws {@link DataAccessException}
     * @throws IllegalArgumentException
     *             when categoryId is non-positive
     */
    List<Resource> getResourcesByCategoryId(int categoryId) throws DAOException;

    /**
     * Extracts total number of resources per particular Category.
     *
     * @param categoryId
     *            The ID to uniquely identify a category, it must be greater
     *            than zero.
     * @return total number of resources corresponding to passed categoryId
     * @throws DAOException
     *             when there is an error while attempting to delete from the
     *             database
     * @throws IllegalArgumentException
     *             when categoryId is negative
     */
    int getResourceCountByCategoryId(int categoryId) throws DAOException;

    /***
     * Check if a resource exists in the database with the given name
     *
     * @param resourceName
     *            {@link String} with the name of the resource to check against
     *            the database. Cannot be null, empty, or blank.
     * @return <code> boolean </code>
     *         <li>true - if a resource exists in the database with the provided
     *         resource name</li>
     *         <li>false - if no resource exists in the database with the
     *         provided resource name</li>
     * @throws DAOException
     *             when there is an error while attempting to check the database
     * @throws IllegalArgumentException
     *             when resourceName is null, empty, or blank.
     */
    boolean checkResourceExists(String resourceName) throws DAOException;

    /**
     * Edits the {@link Resource} with the given resource ID from the resource
     * table otherwise throws a {@link DAOException} if an invalid resourceId
     * has been passed
     *
     * @param resourceId
     *            {@code Integer} representing the unique identifier of the
     *            {@link Resource}. It must be greater than 0.
     * @param resourceName
     *            {@code String} representing the unique identifier of the
     *            {@link Resource} (cannot be <code>null/empty/blank</code>).
     * @param resourceLink
     *            URL representing the unique identifier of the {@link Resource}
     *            (cannot be <code>null/empty/blank</code>).
     * @param resourceDifficultyLevel
     *            {@link Integer} representing the unique identifier of the
     *            {@link Resource}. It must be greater than 0.
     * @param resourceType
     *            {@code String} representing the unique identifier of the
     *            {@link Resource} (cannot be <code>null/empty/blank</code>).
     * @param resourceOwner
     *            {@code String} representing the resourceOwner ID of the
     *            {@link Resource}. Cannot be <code>null/empty/blank</code>.
     * @throws DAOException
     *             when there is an error while attempting to delete from the
     *             database
     * @throws IllegalArgumentException
     *             when any of the inputs are invalid
     */
    void updateResource(
            final int resourceId,
            final String resourceName,
            final URL resourceLink,
            final int resourceDifficultyLevel,
            final String resourceType,
            final String resourceOwner) throws DAOException;
}
