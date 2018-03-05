package com.cerner.devcenter.education.models;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.commons.lang3.StringUtils;

/**
 * A UserRequestRelation is a user with their requested category identifier.
 *
 * @author Vatsal Kesarwani (VK049896)
 */
public class UserRequestRelation {

    private static final int USER_ID_LENGTH = 8;
    private static final String INVALID_REQUEST_CATEGORY_ID_ERROR_MESSAGE = "Request category id is zero/negative.";
    private static final String INVALID_USER_ID_ERROR_MESSAGE = "User id is null/empty/blank.";
    private static final String INVALID_LENGTH_USER_ID_ERROR_MESSAGE = "User id is not equal to 8 characters.";

    private int requestCategoryId;
    private String userId;

    /**
     * Default constructor, required by Spring.
     */
    public UserRequestRelation() {
    }

    /**
     * Returns the category identifier of this requested category.
     *
     * @return The requested category id. Will never be negative
     */
    public int getRequestCategoryId() {
        return requestCategoryId;
    }

    /**
     * Sets the category identifier of this requested category.
     *
     * @param requestCategoryId
     *            the requested category identifier. Cannot be zero or negative
     * @throws IllegalArgumentException
     *             when <code>requestCategoryId</code> is zero/negative
     */
    public void setRequestCategoryId(final int requestCategoryId) {
        checkArgument(requestCategoryId > 0, INVALID_REQUEST_CATEGORY_ID_ERROR_MESSAGE);
        this.requestCategoryId = requestCategoryId;
    }

    /**
     * Returns the user identifier of this requested category.
     *
     * @return The requested category name. This may be <code>null</code>
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user identifier of this requested category.
     *
     * @param userId
     *            the requested category name. Cannot be <code>null</code>,
     *            empty or blank
     * @throws IllegalArgumentException
     *             when
     *             <ul>
     *             <li><code>userId</code> is <code>null</code>/empty/blank</li>
     *             <li><code>userId</code> length is not equal to 8</li>
     *             </ul>
     */
    public void setUserId(final String userId) {
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID_ERROR_MESSAGE);
        checkArgument(StringUtils.length(userId) == USER_ID_LENGTH, INVALID_LENGTH_USER_ID_ERROR_MESSAGE);
        this.userId = userId;
    }
}