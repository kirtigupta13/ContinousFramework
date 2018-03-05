package com.cerner.devcenter.education.models;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.commons.lang3.StringUtils;

/**
 * Learner object contains learner details user id and email id.
 *
 * @author Mani Teja Kurapati(MK051340)
 *
 */
public class Learner {

    private static final String INVALID_USER_ID_ERROR_MSG = "User id is null/empty/blank.";
    private static final String INVALID_EMAIL_ID = "Email Id is null/empty/blank";

    private String userId;
    private String emailId;

    /**
     * Creates a new {@link Learner} Should only be used by Spring and testing.
     */
    public Learner() {
    }

    /**
     * Used to create a new {@link Learner} with the given user id.
     *
     * @param userId
     *            the userId to set for the learner.
     * @throws IllegalArgumentException
     *             when user id is <code>null<code>, blank, or empty
     */
    public Learner(final String userId) {
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID_ERROR_MSG);
        this.userId = userId;
    }

    /**
     * Used to create a new {@link Learner} with the given user id and email id.
     *
     * @param userId
     *            the userId to set for the learner.
     * @param emailId
     *            the emailId to set for the learner.
     * @throws IllegalArgumentException
     *             when
     *             <ul>
     *             <li>user id is <code>null<code>, blank, or empty</li>
     *             <li>email id is <code>null</code>, blank, or empty</li>
     *             </ul>
     */
    public Learner(final String userId, final String emailId) {
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID_ERROR_MSG);
        checkArgument(StringUtils.isNotBlank(emailId), INVALID_EMAIL_ID);
        this.userId = userId;
        this.emailId = emailId;
    }

    /**
     * Returns the user id of the learner.
     *
     * @return the userId. This can be null.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user id of the learner.
     *
     * @param userId
     *            the userId to set for the learner.
     */
    public void setUserId(final String userId) {
        checkArgument(StringUtils.isNotBlank(userId), INVALID_USER_ID_ERROR_MSG);
        this.userId = userId;
    }

    /**
     * Returns the email id of the learner.
     *
     * @return the emailId. This can be null.
     */
    public String getEmailId() {
        return emailId;
    }

    /**
     * Sets the email id of the learner.
     *
     * @param emailId
     *            the emailId to set for the learner.
     */
    public void setEmailId(final String emailId) {
        checkArgument(StringUtils.isNotBlank(emailId), INVALID_EMAIL_ID);
        this.emailId = emailId;
    }
}
