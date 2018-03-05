package com.cerner.devcenter.education.models;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.commons.lang3.StringUtils;

/**
 * A RequestCategory is the requested category.
 *
 * @author Vatsal Kesarwani (VK049896)
 */
public class RequestCategory {

    private static final int CATEGORY_NAME_LENGTH_UPPER_BOUND = 100;
    private static final String INVALID_REQUEST_CATEGORY_ID_ERROR_MESSAGE = "Request category id is zero/negative.";
    private static final String INVALID_REQUEST_CATEGORY_NAME_ERROR_MESSAGE = "Request category name is null/empty/blank.";
    private static final String INVALID_LENGTH_REQUEST_CATEGORY_NAME_ERROR_MESSAGE = "Request category name is greater than 100 characters.";

    private int id;
    private String name;
    private boolean isApproved;

    /**
     * Default constructor, required by Spring.
     */
    public RequestCategory() {
    }

    /**
     * Returns the category identifier of this requested category.
     *
     * @return The requested category id. Will never be negative
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the category identifier of this requested category.
     *
     * @param id
     *            The requested category identifier. Cannot be zero or negative
     * @throws IllegalArgumentException
     *             when <code>id</code> is zero/negative
     */
    public void setId(final int id) {
        checkArgument(id > 0, INVALID_REQUEST_CATEGORY_ID_ERROR_MESSAGE);
        this.id = id;
    }

    /**
     * Returns the category name of this requested category.
     *
     * @return The requested category name. This may be <code>null</code>
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the category name of this requested category.
     *
     * @param name
     *            the requested category name. Cannot be <code>null</code>,
     *            empty or blank
     * @throws IllegalArgumentException
     *             when
     *             <ul>
     *             <li><code>name</code> is <code>null</code>/empty/blank</li>
     *             <li><code>name</code> length is greater than 100</li>
     *             </ul>
     */
    public void setName(final String name) {
        checkArgument(StringUtils.isNotBlank(name), INVALID_REQUEST_CATEGORY_NAME_ERROR_MESSAGE);
        checkArgument(StringUtils.length(name) <= CATEGORY_NAME_LENGTH_UPPER_BOUND,
                INVALID_LENGTH_REQUEST_CATEGORY_NAME_ERROR_MESSAGE);
        this.name = name;
    }

    /**
     * Returns whether or not this requested category has been approved.
     *
     * @return <code>True</code> if this request is approved, <code>False</code>
     *         otherwise
     */
    public boolean isApproved() {
        return isApproved;
    }

    /**
     * Sets whether or not this requested category has been approved.
     *
     * @param isApproved
     *            <code>True</code> if this request is approved,
     *            <code>False</code> otherwise
     */
    public void setApproved(final boolean isApproved) {
        this.isApproved = isApproved;
    }
}