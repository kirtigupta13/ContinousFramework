package com.cerner.devcenter.education.models;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.lang3.StringUtils;

import com.cerner.devcenter.education.utils.CompletionRating;

/**
 * Represents the completed user resource rating that stores the user id,
 * resource id, rating for that particular resource.
 *
 * @author Vinutha Nuchimaniyanda (VN046193)
 * @author Rishabh Bhojak (RB048032)
 * @author Vincent Dasari (VD049645)
 */
public class CompletedUserResource {

    private static final String COMPLETED_DATE_GREATER_THAN_ZERO = "CompletionDate should be greater than 0";
    private static final String USER_ID_INVALID = "User ID cannot be null/empty/whitespace";
    private static final String RESOURCE_ID_GREATER_THAN_ZERO = "Resource ID must be greater than 0.";
    private static final String COMPLETION_RATING_IS_NOT_VALID = "Completion rating is not valid";
    private final String userId;
    private final int resourceId;
    private final CompletionRating completionRating;
    private final long completionDate;

    /**
     * Constructor to set the attributes of CompleteUserResource.
     *
     * @param userId
     *            is the user id of the user. Must not be <code>null</code> or
     *            empty
     * @param resourceId
     *            is the resource id of a resource. Must be greater than 0
     * @param completionRating
     *            an {@link CompletionRating} enumeration constant that is the
     *            user's completion rating for that particular resource. Must be
     *            valid rating
     * @param completionDate
     *            is the date as to when the resource has been marked as
     *            complete
     * @throws IllegalArgumentException
     *             when any of the following conditions are true:
     *             <ul>
     *             <li>userId is <code>null</code>/empty/whitespace</li>
     *             <li>resource Id is less than or equal to 0</li>
     *             <li>completionRating is not greater than or equal to 0 and
     *             less than or equal to 4</li>
     *             <li>completionDate should not be less than or equal to 0</li>
     *             </ul>
     */
    public CompletedUserResource(final String userId, final int resourceId, final CompletionRating completionRating,
            final long completionDate) {
        checkArgument(StringUtils.isNotBlank(userId), USER_ID_INVALID);
        checkArgument(resourceId > 0, RESOURCE_ID_GREATER_THAN_ZERO);
        checkNotNull(completionRating, COMPLETION_RATING_IS_NOT_VALID);
        checkArgument(completionDate > 0, COMPLETED_DATE_GREATER_THAN_ZERO);
        this.userId = userId;
        this.resourceId = resourceId;
        this.completionRating = completionRating;
        this.completionDate = completionDate;
    }

    /**
     * Getter method that returns the user id.
     *
     * @return a String which is unique id for that user
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Getter method that returns the resource id.
     *
     * @return a integer which is unique id of the resource
     */
    public int getResourceId() {
        return resourceId;
    }

    /**
     * Getter method that returns the completion rating.
     *
     * @return a {@link CompletionRating} which is the completion rating for a
     *         resource
     */
    public CompletionRating getCompletedRating() {
        return completionRating;
    }

    /**
     * Getter method that returns the completion date.
     *
     * @return a long date of the completed resource
     *
     */
    public long getCompletionDate() {
        return completionDate;
    }
}