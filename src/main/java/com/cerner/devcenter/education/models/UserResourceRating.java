package com.cerner.devcenter.education.models;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.lang3.StringUtils;

import com.cerner.devcenter.education.utils.Rating;
import com.cerner.devcenter.education.utils.Status;

/**
 * Represents the user resource rating that stores the user's id along with the
 * resource id and the user's rating for that particular resource.
 * 
 * @author Gunjan Kaphle (GK045931)
 * @author Asim Mohammed (AM045300)
 * @author Navya Rangeneni (NR046827)
 */
public class UserResourceRating {

    private static final String USER_ID_ERROR_MESSAGE = "User ID cannot be null/empty/blank.";
    private static final String RESOURCE_ID_ERROR_MESSAGE = "Resource ID must be greater than 0.";
    private static final String RATING_ERROR_MESSAGE = "Rating must not be null.";
    private static final String STATUS_ERROR_MESSAGE = "Status for a resource cannot be null.";

    private String userId;
    private int resourceId;
    private Rating rating;
    private Status status;

    /**
     * Default constructor used for testing purposes.
     */
    UserResourceRating() {

    }

    /**
     * Constructor to set the attributes of UserResourceRating.
     * 
     * @param userId
     *            a {@link String} that is the user id of the user. Must not be
     *            null or empty
     * @param resourceId
     *            an {@link Integer} that is the resource id of a resource. Must
     *            be greater than 0
     * @param rating
     *            a {@link Rating} that is the user's rating for that
     *            particular resource. Must not be null
     * @param status
     *            a {@link Status} that is the user's status on a learning
     *            resource. Must not be null
     * @throws IllegalArgumentException
     *             when any of the following conditions are true:
     *             <ul>
     *             <li>userId is null/empty</li>
     *             <li>resource Id is less than or equal to 0</li>
     *             <li>rating is null</li>
     *             <li>status is null</li>
     *             <ul>
     */
    public UserResourceRating(String userId, int resourceId, Rating rating, Status status) {
        checkArgument(StringUtils.isNotBlank(userId), USER_ID_ERROR_MESSAGE);
        checkArgument(resourceId > 0, RESOURCE_ID_ERROR_MESSAGE);
        checkNotNull(rating, RATING_ERROR_MESSAGE);
        checkNotNull(status, STATUS_ERROR_MESSAGE);

        this.userId = userId;
        this.resourceId = resourceId;
        this.rating = rating;
        this.status = status;
    }

    /**
     * Getter method that returns the id of the user.
     * 
     * @return a {@link String} which is the unique id of the user
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Setter method that sets the id of the user.
     * 
     * @param userId
     *            a {@link String} that is the unique id of the user. Must not
     *            be null or empty
     * @throws IllegalArgumentException
     *             when userId is null or empty
     */
    public void setUserId(String userId) {
        checkArgument(StringUtils.isNotBlank(userId), USER_ID_ERROR_MESSAGE);
        this.userId = userId;
    }

    /**
     * Getter method that returns a the resource id of a {@link Resource}.
     * 
     * @return an {@link Integer} that is the unique id of the {@link Resource}
     */
    public int getResourceId() {
        return resourceId;
    }

    /**
     * Setter method that sets the resource id.
     * 
     * @param resourceId
     *            an {@link Integer} that is the unique id of the
     *            {@link Resource}. Must be greater than 0
     * @throws IllegalArgumentException
     *             when resource id is less than or equal to 0
     */
    public void setResourceId(int resourceId) {
        checkArgument(resourceId > 0, RESOURCE_ID_ERROR_MESSAGE);
        this.resourceId = resourceId;
    }

    /**
     * Getter method that returns the user's rating for that resource.
     * 
     * @return a {@link Rating} that is the user's rating for the resource
     */
    public Rating getRating() {
        return rating;
    }

    /**
     * Setter method that sets the rating of the resource for a user
     * 
     * @param rating
     *            that is the user rated number for the
     *            resource. Must be between 0 and 5
     * @throws IllegalArgumentException
     *             when rating is null
     */
    public void setRating(Rating rating) {
        checkNotNull(rating, RATING_ERROR_MESSAGE);
        this.rating = rating;
    }

    /**
     * Getter method that returns the status of a particular resource for a
     * user.
     * 
     * @return a {@link Status} that is the status of a particular resource for
     *         a user
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Setter method that sets the status of the resource for a user
     * 
     * @param status
     *            that is the status of a resource for a user
     */
    public void setStatus(Status status) {
        checkNotNull(status, STATUS_ERROR_MESSAGE);
        this.status = status;
    }
}
