package com.cerner.devcenter.education.models;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.commons.lang3.StringUtils;

/**
 * A user subscription is a subscribed category for a particular user.
 *
 * @author Mani Teja Kurapati (MK051340)
 */
public class UserSubscription {

    private static final String INVALID_USER_ID_ERROR_MSG = "User id is null/empty/blank.";
    private static final String INVALID_CATEGORY_ID = "Category ID should be greater than zero";

    private String userId;
    private int categoryId;

    /**
     * Default constructor, required by Spring.
     */
    public UserSubscription() {
    }

    /**
     * Returns the user identifier for subscribed category.
     *
     * @return The user id. This can be null.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user identifier for this subscription.
     *
     * @param userId
     *            The user identifier for this subscription. Cannot be null,
     *            empty or blank.
     * @throws IllegalArgumentException
     *             when <code>userId</code> is null/empty/blank.
     */
    public void setUserId(final String userId) {
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID_ERROR_MSG);
        this.userId = userId;
    }

    /**
     * Returns the category identifier of subscribed category.
     *
     * @return The category Id. This can be null.
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * Sets the Category identifier of the subscribed category.
     *
     * @param categoryId
     *            an identifier representing subscribed category (cannot be 0 or
     *            negative).
     * @throws IllegalArgumentException
     *             if categoryId is zero or negative
     */
    public void setCategoryId(final int categoryId) {
        checkArgument(categoryId > 0, INVALID_CATEGORY_ID);
        this.categoryId = categoryId;
    }
}
