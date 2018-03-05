package com.cerner.devcenter.education.admin;

import java.util.List;

import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.models.Tag;

/**
 * Responsible for performing database operations for relationships between
 * {@link Tag} and {@link Resource}.
 *
 * @author Amos Bailey (AB032627)
 *
 */
public interface ResourceTagRelationDAO {

    /**
     * Adds a new relation between a resource and multiple tags to the database.
     *
     * @param resourceID
     *            an integer representing the ID of a resource that we wish to
     *            add a relationship for in the database (Must be a positive
     *            number)
     * @param tagList
     *            a list of {@link Tag} objects that we wish to add a
     *            relationship for in the database (Must not be null or empty,
     *            and each tag contained in the list must not be null)
     * @throws IllegalArgumentException
     *             when the passed resourceID does not exist, when the
     *             resourceID is not positive, when tagList is null, when the
     *             tag list contains a null object, when a tag's ID is not
     *             positive, or when a tag's name is null/blank/empty.
     * @throws DAOException
     *             when a mapping is unable to be added to the database for any
     *             reason
     */
    void addTagsToResource(int resourceID, List<Tag> tagList) throws DAOException;
}
