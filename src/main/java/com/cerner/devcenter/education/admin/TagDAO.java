package com.cerner.devcenter.education.admin;

import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cerner.devcenter.education.models.Tag;

/**
 * Defines operations for {@link Tag} data objects.
 *
 * @author Abhi Purella (AP045635)
 * @author Amos Bailey (AB032627)
 */
public interface TagDAO {

    /**
     * Performs a query on the data source that will return a {@link List}
     * object containing list of the {@link Tag} objects present in DB.
     * 
     * @return a {@link List} of {@link Tag} objects
     * 
     * @throws DAOException
     *             when there is an error while trying to get tags from the data
     *             source
     */
    List<Tag> getAllTags() throws DAOException;

    /**
     * Performs a query on the database which will return a {@link List}
     * containing {@link Tag} objects whose name partially matches the search
     * string.
     * 
     * @param search
     *            A {@link String} to match the tags with. The string cannot be
     *            null, empty, or blank.
     * @return a {@link List} of {@link Tag} objects
     * @throws DAOException
     *             when there is an error retrieving tags from the database.
     */
    List<Tag> getSearchedTags(String search) throws DAOException;

    /**
     * Performs a query on the database which will return a {@link Tag} whose ID
     * matches the specified ID.
     * 
     * @param id
     *            An integer representing the ID of the tag we wish to retrieve.
     *            Must be positive.
     * @return {@link Tag} The tag with the specified ID.
     * @throws IllegalArgumentException
     *             when the ID is not positive.
     * @throws DAOException
     *             when a tag with the specified ID is not found, or
     *             {@link JdbcTemplate#queryForObject(String, Class, Object...)}
     *             throws {@link DataAccessException}
     */
    Tag getTagByID(int id) throws DAOException;

    /**
     * Inserts a tag into the database with the specified name.
     * 
     * @param tagName
     *            A {@link String} with the name of the tag to add to the
     *            database. Cannot be null, empty, or blank.
     * @throws DAOException
     *             when there is an error adding the tag to the database.
     * @throws IllegalArgumentException
     *             When the tag name is null, empty, or blank.
     */
    void addTagToDB(String tagName) throws DAOException;

    /**
     * Given a collection of tag names, inserts a tag into the database with
     * each name given.
     * 
     * @param tagNames
     *            A collection of strings, each string representing a tag name.
     *            The collection cannot be null or empty. Each tag name cannot
     *            be null, empty, or blank.
     * @throws DAOException
     *             When there is an error adding tags to the database.
     * @throws IllegalArgumentException
     *             When the collection of tag names is null, empty, or the
     *             collection contains a string that is null, empty, or blank.
     */
    void batchAddTags(Collection<String> tagNames) throws DAOException;

    /**
     * Finds tags whose name matches the specified name (ignoring case).
     * 
     * @param tagName
     *            A {@link String} representing the tag name to search for.
     *            Cannot be empty or blank.
     * @return A {@link Tag} object whose name matches the search string
     *         (ignoring case). Returns null if no such tag is found.
     * @throws DAOException
     *             When the query throws a DataAccessException
     * @throws IllegalArgumentException
     *             When the tag name is null, empty, or blank.
     */
    Tag getTagByName(String tagName) throws DAOException;

    /**
     * Finds tags whose name matches a name contained in the list (ignoring
     * case).
     * 
     * @param tagNames
     *            A collection of {@link String} objects representing the tag
     *            names to search for. The list cannot be empty or null. Each
     *            tag name to search by cannot be null, empty, or blank.
     * @return A List of {@link Tag} objects whose names each match one of the
     *         search names (ignoring case). Returns an empty list in the case
     *         of no matches.
     * @throws DAOException
     *             When the query throws a DataAccessException
     * @throws IllegalArgumentException
     *             When the collection of tag names is null, empty, or the
     *             collection contains a string that is null, empty, or blank.
     */
    List<Tag> getTagsWithNameInCollection(Collection<String> tagNames) throws DAOException;
}
