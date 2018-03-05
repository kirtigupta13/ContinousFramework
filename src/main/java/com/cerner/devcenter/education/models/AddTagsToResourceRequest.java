package com.cerner.devcenter.education.models;

import static com.cerner.devcenter.education.utils.TagNameVerifier.verifyTagNameCollectionArgument;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import com.cerner.devcenter.education.utils.Constants;

/**
 * Represents a request body for adding tags to a pre-existing {@link Resource}
 * object.
 * 
 * @author Amos Bailey (AB032627)
 *
 */
public class AddTagsToResourceRequest {
    private int resourceID;
    private List<String> tagNameList;

    /**
     * Sets the ID of the resource.
     * 
     * @param resourceID
     *            An int. Must be positive.
     * @throws {@link
     *             IllegalArgumentException} when the specified resource ID is
     *             not positive.
     */
    public void setResourceID(final int resourceID) {
        checkArgument(resourceID > 0, Constants.RESOURCE_ID_MUST_BE_POSITIVE);
        this.resourceID = resourceID;
    }

    /**
     * Gets the ID of the resource.
     * 
     * @return An int representing the ID.
     */
    public int getResourceID() {
        return this.resourceID;
    }

    /**
     * Sets the list of tag names.
     * 
     * @param tagNameList
     *            A {@link List} of {@link String} objects representing the tag
     *            names.
     * @throws {@link
     *             IllegalArgumentException} when the specified list is null, or
     *             the list contains a null/empty/blank string.
     */
    public void setTagNameList(final List<String> tagNameList) {
        verifyTagNameCollectionArgument(tagNameList);
        this.tagNameList = tagNameList;
    }

    /**
     * Gets the list of tag names.
     * 
     * @return A {@link List} of {@link String} objects representing the tag
     *         names. Returns null if the list has not yet been set.
     */
    public List<String> getTagNameList() {
        return this.tagNameList;
    }
}
