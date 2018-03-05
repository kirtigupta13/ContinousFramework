package com.cerner.devcenter.education.models;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Represents the relation between resource and tag.
 * 
 * @author Abhi Purella (AP045635)
 * @author Navya Rangeneni (NR046827)
 */
public class ResourceTagRelation {

    private static final String TAG_ID_ERROR_MESSAGE = "tagId must be greater than 0.";
    private static final String RESOURCE_ID_ERROR_MESSAGE = "resourceId must be greater than 0.";
    private int tagId;
    private int resourceId;

    /**
     * Parameterized constructor for the {@link ResourceTagRelation} class.
     * 
     * @param tagId
     *            an {@link Integer} that represents the unique tagId of the
     *            {@link Tag} (must be greater than 0)
     * @param resourceId
     *            an {@link Integer} that represents the unique resourceId of
     *            the {@link Resource} (must be greater than 0)
     * @throws IllegalArgumentException
     *             when tagId is not greater than 0 or resourceId is not greater
     *             than 0
     */
    public ResourceTagRelation(final int tagId, final int resourceId) {
        checkArgument(tagId > 0, TAG_ID_ERROR_MESSAGE);
        checkArgument(resourceId > 0, RESOURCE_ID_ERROR_MESSAGE);
        this.tagId = tagId;
        this.resourceId = resourceId;
    }

    /**
     * Default constructor of the {@link ResourceTagRelation} class with package
     * private scope that has no implementation and is used during testing.
     */
    ResourceTagRelation() {
    }

    /**
     * Getter method that returns the tagId of the {@link Tag}.
     * 
     * @return the tagId represents the unique tagId of the {@link Tag}
     */
    public int getTagId() {
        return tagId;
    }

    /**
     * Setter method that sets the tagId of the {@link Tag}.
     * 
     * @param tagId
     *            an {@link Integer} that represents the unique tagId of the
     *            {@link Tag} (must be greater than 0)
     * @throws IllegalArgumentException
     *             when tagId is not greater than 0
     */
    public void setTagId(final int tagId) {
        checkArgument(tagId > 0, TAG_ID_ERROR_MESSAGE);
        this.tagId = tagId;
    }

    /**
     * Getter method that returns the resourceId of the {@link Resource}.
     * 
     * @return the resourceId represents the unique resourceId of the
     *         {@link Resource}
     */
    public int getResourceId() {
        return resourceId;
    }

    /**
     * Setter method that sets the resourceId of the {@link Resource}.
     * 
     * @param resourceId
     *            an {@link Integer} that represents the unique resourceId of
     *            the {@link Resource} (must be greater than 0)
     * @throws IllegalArgumentException
     *             when resourceId is not greater than 0
     */
    public void setResourceId(final int resourceId) {
        checkArgument(resourceId > 0, RESOURCE_ID_ERROR_MESSAGE);
        this.resourceId = resourceId;
    }
}
