package com.cerner.devcenter.education.models;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.commons.lang3.StringUtils;

/**
 * A ResourceRequest is a request for a particular resource.
 *
 * @author Navya Rangeneni (NR046827)
 * @author Vatsal Kesarwani (VK049896)
 */
public class ResourceRequest {

    private static final String INVALID_USER_ID_ERROR_MSG = "User id is null/empty/blank.";
    private static final String INVALID_CATEGORY_NAME_ERROR_MSG = "Category name is null/empty/blank.";
    private static final String INVALID_RESOURCE_NAME_ERROR_MSG = "Resource name is null/empty/blank.";
    private static final String INVALID_RESOURCE_REQUEST_ID_ERROR_MSG = "Resource request id is zero/negative.";

    private int id;
    private String userId;
    private String categoryName;
    private String resourceName;
    private boolean isApproved;

    /**
     * Default constructor, required by Spring.
     */
    public ResourceRequest() {
    }

    /**
     * Returns the resource request identifier of this requested resource.
     *
     * @return The resource request id. Will never be negative.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the resource request identifier of this requested resource.
     *
     * @param id
     *            The resource request identifier of this requested resource.
     *            Cannot be zero or negative.
     * @throws IllegalArgumentException
     *             when <code>id</code> is zero/negative.
     */
    public void setId(final int id) {
        checkArgument(id > 0, INVALID_RESOURCE_REQUEST_ID_ERROR_MSG);
        this.id = id;
    }

    /**
     * Returns the user identifier of this requested resource.
     *
     * @return The user id. This may be null.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user identifier of this requested resource.
     *
     * @param userId
     *            The user identifier of this requested resource. Cannot be
     *            null, empty or blank.
     * @throws IllegalArgumentException
     *             when <code>userId</code> is null/empty/blank.
     */
    public void setUserId(final String userId) {
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID_ERROR_MSG);
        this.userId = userId;
    }

    /**
     * Returns the category name of this requested resource.
     *
     * @return The category name. This may be null.
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Sets the category name of this requested resource.
     *
     * @param categoryName
     *            The category name of this requested resource. Cannot be null,
     *            empty or blank.
     * @throws IllegalArgumentException
     *             when <code>categoryName</code> is null/empty/blank.
     */
    public void setCategoryName(final String categoryName) {
        checkArgument(StringUtils.isNotBlank(categoryName), INVALID_CATEGORY_NAME_ERROR_MSG);
        this.categoryName = categoryName;
    }

    /**
     * Returns the resource name of this requested resource.
     *
     * @return The resource name. This may be null.
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Sets the resource name of this requested resource.
     *
     * @param resourceName
     *            The resource name of this requested resource. Cannot be null,
     *            empty or blank.
     * @throws IllegalArgumentException
     *             when <code>resourceName</code> is null/empty/blank.
     */
    public void setResourceName(final String resourceName) {
        checkArgument(StringUtils.isNotBlank(resourceName), INVALID_RESOURCE_NAME_ERROR_MSG);
        this.resourceName = resourceName;
    }

    /**
     * Returns whether or not this requested resource has been approved.
     *
     * @return <code>True</code> if this request is approved, <code>False</code>
     *         otherwise.
     */
    public boolean isApproved() {
        return isApproved;
    }

    /**
     * Sets whether or not this requested resource has been approved.
     *
     * @param isApproved
     *            <code>True</code> if this request is approved,
     *            <code>False</code> otherwise.
     */
    public void setApproved(final boolean isApproved) {
        this.isApproved = isApproved;
    }
}