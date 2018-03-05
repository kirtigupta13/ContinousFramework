package com.cerner.devcenter.education.models;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents the tag which holds the tag's information(tagId and tagName) which
 * can be associated to a {@link Resource}.
 * 
 * @author Abhi Purella (AP045635)
 * @author Navya Rangeneni (NR046827)
 */
public class Tag {

    private static final String TAG_ID_ERROR_MESSAGE = "tagId must be greater than 0.";
    private static final String TAG_NAME_ERROR_MESSAGE = "tagName is null/empty/blank.";

    private int tagId;
    private String tagName;

    /**
     * Parameterized constructor for the {@link Tag} class.
     * 
     * @param tagId
     *            an {@link Integer} that represents the unique tagId of the Tag
     *            (must be greater than 0)
     * @param tagName
     *            a {@link String} representing the tagName of the Tag (cannot
     *            be null or empty)
     * @throws IllegalArgumentException
     *             when tagId is not greater than 0 or tagName is null/empty
     */
    public Tag(int tagId, String tagName) {
        checkArgument(tagId > 0, TAG_ID_ERROR_MESSAGE);
        checkArgument(StringUtils.isNotBlank(tagName), TAG_NAME_ERROR_MESSAGE);
        this.tagId = tagId;
        this.tagName = tagName;
    }

    /**
     * Default constructor of the {@link Tag} class with package private scope
     * that has no implementation and is used during testing.
     */
    Tag() {
    }

    /**
     * Getter method that returns the tagId of the Tag.
     * 
     * @return the tagId represents the unique tagId of the Tag
     */
    public int getTagId() {
        return tagId;
    }

    /**
     * Setter method that sets the tagId of the Tag.
     * 
     * @param tagId
     *            an {@link Integer} that represents the unique tagId of the Tag
     *            (must be greater than 0)
     * @throws IllegalArgumentException
     *             when tagId is not greater than 0
     */
    public void setTagId(int tagId) {
        checkArgument(tagId > 0, TAG_ID_ERROR_MESSAGE);
        this.tagId = tagId;
    }

    /**
     * Getter method that returns the tagName of the Tag.
     * 
     * @return the tagName represents the tagName of the Tag
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Setter method that sets the tagName of the Tag.
     * 
     * @param tagName
     *            a {@link String} representing the tagName of the Tag (cannot
     *            be null or empty)
     * @throws IllegalArgumentException
     *             when tagName is null/empty
     */
    public void setTagName(String tagName) {
        checkArgument(StringUtils.isNotBlank(tagName), TAG_NAME_ERROR_MESSAGE);
        this.tagName = tagName;
    }

    @Override
    public int hashCode() {
        return this.tagId;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        final Tag otherTag = (Tag) object;

        // Only check TagID for equality since tagID is the primary key.
        if (otherTag.tagId != this.tagId) {
            return false;
        }
        return true;
    }
}
