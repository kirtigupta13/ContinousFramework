package com.cerner.devcenter.education.managers;

import static com.cerner.devcenter.education.utils.TagNameVerifier.verifyTagNameArgument;
import static com.cerner.devcenter.education.utils.TagNameVerifier.verifyTagNameCollectionArgument;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.admin.ResourceTagRelationDAO;
import com.cerner.devcenter.education.admin.TagDAO;
import com.cerner.devcenter.education.models.Tag;
import com.cerner.devcenter.education.utils.Constants;

/**
 * Used as a connector or links between the controller classes and the
 * {@link Tag} class.
 * 
 * @author Abhi Purella (AP045635)
 * @author Amos Bailey (AB032627)
 */
@Service("tagManager")
public class TagManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(TagManager.class);
    @Autowired
    private TagDAO tagDAO;
    @Autowired
    private ResourceTagRelationDAO resourceTagRelationDAO;

    private static ResourceBundle i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());
    private static final String ERROR_RETRIEVING_SEARCHED_TAGS_MESSAGE = "Error retrieving searched tags from the database";
    private static final String ERROR_RETRIEVING_ALL_TAGS_MESSAGE = "Error retrieving tags from the data source";

    /**
     * Used to retrieve a list of all {@link Tag}.
     *
     * @return a {@link List} of {@link Tag}
     * 
     * @throws ManagerException
     *             when a manager is not able to access the data source and
     *             catches {@link DAOException}.
     */
    public List<Tag> getAllTags() {
        try {
            return tagDAO.getAllTags();
        } catch (DAOException e) {
            throw new ManagerException(ERROR_RETRIEVING_ALL_TAGS_MESSAGE, e);
        }
    }

    /**
     * This method returns a {@link List} of {@link Tag} objects matching the
     * search string.
     * 
     * @param search
     *            input {@link String} given by user (cannot be null, empty or
     *            blank).
     * @return {@link List} of searched {@link Tag} (will not be null).
     * @throws ManagerException
     *             When there is an error retrieving searched tags from the
     *             database.
     * @throws IllegalArgumentException
     *             when an invalid/blank search keyword is entered.
     */
    public List<Tag> getSearchedTags(String search) {
        checkArgument(StringUtils.isNotBlank(search), i18nBundle.getString(Constants.SEARCH_INVALID_I18N));
        try {
            return tagDAO.getSearchedTags(search.toLowerCase());
        } catch (DAOException daoException) {
            LOGGER.error(ERROR_RETRIEVING_SEARCHED_TAGS_MESSAGE, daoException);
            throw new ManagerException(ERROR_RETRIEVING_SEARCHED_TAGS_MESSAGE, daoException);
        }
    }

    /**
     * Given a set of tagNames, this method adds all of them to the specified
     * resource.
     * 
     * @param tagNameSet
     *            A set of {@link String} objects. Each tag name becomes
     *            associated with the resource. Cannot be empty or null. Each
     *            tag name cannot be empty, blank, or null.
     * @param resourceID
     *            The integer representing the unique ID of the resource to add
     *            the tags to. Must be positive.
     * @throws ManagerException
     *             When a DAO throws a DAOException.
     * @throws IllegalArgumentException
     *             When the provided set is empty or null, or when the set
     *             contains elements that are null, blank, or empty.
     */
    public void addTagsToResource(final Set<String> tagNameSet, final int resourceID) throws ManagerException {
        verifyTagNameCollectionArgument(tagNameSet);
        addMissingTags(tagNameSet);

        List<Tag> tagsToAddToResource;
        try {
            tagsToAddToResource = tagDAO.getTagsWithNameInCollection(tagNameSet);
        } catch (DAOException daoException) {
            throw new ManagerException(Constants.ERROR_FINDING_TAGS_BY_NAME, daoException);
        }

        try {
            resourceTagRelationDAO.addTagsToResource(resourceID, tagsToAddToResource);
        } catch (DAOException daoException) {
            throw new ManagerException(Constants.ERROR_ADDING_TAG_RESOURCE_RELTN, daoException);
        }
    }

    /**
     * Given a collection of Strings representing tag names, this method will
     * add a tag with that name to the database provided that a tag with that
     * name (ignoring case) does not already exist.
     * 
     * @param tagNames
     *            A collection of String objects. The collection cannot be empty
     *            or null. The collection cannot contain Strings which are
     *            empty, null, or blank.
     * @throws IllegalArgumentException
     *             When the provided collection is empty or null, or when the
     *             collection contains elements that are null, blank, or empty.
     * @throws ManagerException
     *             When there is an error searching the database for matching
     *             tags.
     */
    private void addMissingTags(final Collection<String> tagNames) throws ManagerException {
        verifyTagNameCollectionArgument(tagNames);
        List<String> missingTags = getMissingTagNames(tagNames);
        if (!missingTags.isEmpty()) {
            addMultipleTags(missingTags);
        }
    }

    /**
     * Given a collection of strings, this method returns the strings in that
     * set which are not currently represented by a tag object in the database.
     * 
     * @param tagNames
     *            A collection of Strings. The collection cannot be empty or
     *            null, or contain elements that are empty, blank, or null.
     * @return A list of Strings, all of which were not matched to a tag name.
     * @throws IllegalArgumentException
     *             When the provided collection is empty or null, or when the
     *             collection contains elements that are null, blank, or empty.
     * @throws ManagerException
     *             When there is an error searching the database for matching
     *             tags.
     */
    private List<String> getMissingTagNames(final Collection<String> tagNames) throws ManagerException {
        verifyTagNameCollectionArgument(tagNames);
        List<Tag> existingTags;
        try {
            existingTags = tagDAO.getTagsWithNameInCollection(tagNames);
        } catch (DAOException daoException) {
            throw new ManagerException(Constants.ERROR_FINDING_TAGS_BY_NAME, daoException);
        }
        Set<String> existingTagNames = new HashSet<String>(existingTags.size());
        for (Tag tag : existingTags) {
            existingTagNames.add(tag.getTagName().toLowerCase());
        }
        List<String> missingTagNames = new ArrayList<String>();
        for (String tagName : tagNames) {
            if (!existingTagNames.contains(tagName.toLowerCase())) {
                missingTagNames.add(tagName);
            }
        }
        return missingTagNames;
    }

    /**
     * Adds a tag to the database with the specified name, assuming a tag with
     * that name has not already been added.
     * 
     * @param tagName
     *            A {@link String} that is the name of the tag to be added.
     *            Cannot be empty, null, or blank.
     * @throws ManagerException
     *             when the TagDAO throws a DAOException while attempting to add
     *             the tag.
     * @throws IllegalArgumentException
     *             when the tag name is null, empty, or blank.
     */
    public void addTag(final String tagName) throws ManagerException {
        verifyTagNameArgument(tagName);

        try {
            tagDAO.addTagToDB(tagName);
        } catch (DAOException daoException) {
            LOGGER.error(Constants.ERROR_ADDING_TAG_TO_DB, daoException);
            throw new ManagerException(Constants.ERROR_ADDING_TAG_TO_DB, daoException);
        }
    }

    /**
     * Adds tags to the database with the specified names, assuming a tag with
     * each name has not already been added.
     * 
     * @param tagNames
     *            A collection of {@link String} objects representing the name
     *            of the tag to be added. The collection cannot be empty or
     *            null. Each tag name in the collection cannot be null, empty,
     *            or blank.
     * @throws ManagerException
     *             when the TagDAO throws a DAOException.
     * @throws IllegalArgumentException
     *             When the provided collection is empty or null, or when the
     *             collection contains elements that are null, blank, or empty.
     */
    public void addMultipleTags(final Collection<String> tagNames) throws ManagerException {
        verifyTagNameCollectionArgument(tagNames);

        try {
            tagDAO.batchAddTags(tagNames);
        } catch (DAOException daoException) {
            LOGGER.error(Constants.ERROR_ADDING_TAG_TO_DB, daoException);
            throw new ManagerException(Constants.ERROR_ADDING_TAG_TO_DB, daoException);
        }
    }
}
