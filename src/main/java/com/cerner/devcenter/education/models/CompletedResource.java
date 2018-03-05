package com.cerner.devcenter.education.models;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.cerner.devcenter.education.utils.CompletionRating;

/**
 * Represents the completed resources by a user that stores the user id,
 * resource id, resource name, resource link along with the user's rating for
 * that resource.
 *
 * @author Vinutha Nuchimaniyanda (VN046193)
 * @author Rishabh Bhojak (RB048032)
 */
public class CompletedResource {

    private static final String COMPLETED_DATE_NOT_NULL = "CompletionDate should not be null";
    private static final String USER_ID_INVALID = "User ID cannot be null/empty/whitespace";
    private static final String RESOURCE_ID_GREATER_THAN_ZERO = "Resource ID must be greater than 0.";
    private static final String COMPLETED_RESOURCE_NOT_NULL = "Completed resource rating object cannot be null";
    private static final String RESOURCE_NAME_INVALID = "Resource Name cannot be null/empty/whitespace";
    private final String userId;
    private final int resourceId;
    private final String resourceName;
    private final URL resourceLink;
    private final CompletionRating completionRating;
    private final Date completionDate;

    /**
     * Constructor to set the attributes for {@link CompletedResource}.
     *
     * @param userId
     *            is the unique id of the user. Must not be null or empty or
     *            blank
     * @param resourceId
     *            is the unique id of a resource. Must be greater than 0
     * @param resourceName
     *            is the name of a resource.
     * @param resourceLink
     *            is the URL link for the resource
     * @param completionRating
     *            an {@link CompletionRating} enumeration constant that is the
     *            user's completion rating for that particular resource. Must be
     *            valid rating
     * @param completionDate
     *            is the completion date of a resource. Should not be null
     * @throws IllegalArgumentException
     *             when any of the following conditions are true:
     *             <ul>
     *             <li>userId is null/empty/whitespace</li>
     *             <li>resource Id is less than or equal to 0</li>
     *             <li>resource name is null/empty/whitespace</li>
     *             <li>resource link is null, empty or not properly formed</li>
     *             <li>completion rating is null</li>
     *             <li>completion date should not be <code>null</code></li>
     *             </ul>
     */
    public CompletedResource(String userId, int resourceId, String resourceName, URL resourceLink,
            CompletionRating completionRating, Date completionDate) {
        checkArgument(StringUtils.isNotBlank(userId), USER_ID_INVALID);
        checkArgument(resourceId > 0, RESOURCE_ID_GREATER_THAN_ZERO);
        checkArgument(StringUtils.isNotBlank(resourceName), RESOURCE_NAME_INVALID);
        checkNotNull(completionRating, COMPLETED_RESOURCE_NOT_NULL);
        checkNotNull(completionDate, COMPLETED_DATE_NOT_NULL);

        this.userId = userId;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.resourceLink = resourceLink;
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
     * Getter method that returns the resource name.
     *
     * @return a String which is name of the resource
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Getter method that returns the resource link.
     *
     * @return a URL that is not malformed
     */
    public URL getResourceLink() {
        return resourceLink;
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
     * @return a {@link Date} which is the completion date for a
     *         resource
     */
    public Date getCompletionDate() {
        return completionDate;
    }

    /**
     * Getter method that returns the formatted completion date.
     *
     * @return a string which is the formatted completion
     *         date for a resource
     */
    public String getFormattedDate() {
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a zzz").format(completionDate);
    }

}