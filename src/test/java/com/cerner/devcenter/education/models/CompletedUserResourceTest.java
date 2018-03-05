package com.cerner.devcenter.education.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.utils.CompletionRating;

/**
 * Tests the functionalities of {@link CompletedUserResource}
 *
 * @author Vinutha Nuchimaniyanda(VN046193)
 * @author Mayur Rajendran(MT049536)
 * @author Navya Rangeneni (NR046827)
 * @author Rishabh Bhojak (RB048032)
 * @author Vincent Dasari (VD049645)
 */
public class CompletedUserResourceTest {

    private static final String VALID_USER_ID = "VN046193";
    private static final String WHITESPACE = "    ";
    private static final String EMPTY = "";
    private static final int VALID_RESOURCE_ID = 4;
    private static final String USER_ID_ILLEGAL_ARGUMENT_MESSAGE = "User ID cannot be null/empty/whitespace";
    private static final String RESOURCE_ID_ILLEGAL_ARGUMENT_MESSAGE = "Resource ID must be greater than 0.";
    private static final String COMPLETION_RATING_ILLEGAL_ARGUMENT_MESSAGE = "Completion rating is not valid";
    private static final String COMPLETED_DATE_GREATER_THAN_ZERO = "CompletionDate should be greater than 0";
    private static final long VALID_DATE = 1453005248;

    private CompletedUserResource completedUserResource;

    /**
     * Test constructor {@link CompletedUserResource#CompletedUserResource(String, int,
     * CompletionRating, long)} with valid user id, resource id, completion date
     * and completion rating.
     */
    @Test
    public void testConstructorValidValues() {
        for (final CompletionRating rating : CompletionRating.values()) {
            completedUserResource = new CompletedUserResource(VALID_USER_ID, VALID_RESOURCE_ID, rating, VALID_DATE);
            assertEquals(VALID_USER_ID, completedUserResource.getUserId());
            assertEquals(VALID_RESOURCE_ID, completedUserResource.getResourceId());
            assertEquals(rating, completedUserResource.getCompletedRating());
            assertEquals(VALID_DATE, completedUserResource.getCompletionDate());
        }
    }

    /**
     * Test {@link CompletedUserResource#CompletedUserResource(String, int, CompletionRating, long)}
     * when user id is empty string. Expects {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWhenUserIdEmpty() {
        try {
            new CompletedUserResource(EMPTY, VALID_RESOURCE_ID, CompletionRating.SATISFIED, VALID_DATE);
        } catch (final IllegalArgumentException e) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Test {@link CompletedUserResource#CompletedUserResource(String, int, CompletionRating, long)}
     * when user id is <code>null</code> string. Expects
     * {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWhenUserIdNull() {
        try {
            new CompletedUserResource(null, VALID_RESOURCE_ID, CompletionRating.SATISFIED, VALID_DATE);
        } catch (final IllegalArgumentException e) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Test {@link CompletedUserResource#CompletedUserResource(String, int, CompletionRating, long)}
     * when user id is whitespace string. Expects
     * {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWhenUserIdWhitespace() {
        try {
            new CompletedUserResource(WHITESPACE, VALID_RESOURCE_ID, CompletionRating.SATISFIED, VALID_DATE);
        } catch (final IllegalArgumentException e) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Test {@link CompletedUserResource#CompletedUserResource(String, int, CompletionRating, long)}
     * when resource id is negative. Expects {@link IllegalArgumentException} .
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorResourceIdWhenNegative() {
        try {
            new CompletedUserResource(VALID_USER_ID, -2, CompletionRating.SATISFIED, VALID_DATE);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_ID_ILLEGAL_ARGUMENT_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Test {@link CompletedUserResource#CompletedUserResource(String, int, CompletionRating, long)}
     * when resource id is 0. Expects {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorResourceIdWhenZero() {
        try {
            new CompletedUserResource(VALID_USER_ID, 0, CompletionRating.SATISFIED, VALID_DATE);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_ID_ILLEGAL_ARGUMENT_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Test {@link CompletedUserResource#CompletedUserResource(String, int, CompletionRating, long)}
     * when completion rating is <code>null</code>. Expects
     * {@link NullPointerException}.
     */
    @Test(expected = NullPointerException.class)
    public void testConstructorCompletedRatingWhenNull() {
        try {
            new CompletedUserResource(VALID_USER_ID, VALID_RESOURCE_ID, null, VALID_DATE);
        } catch (final NullPointerException e) {
            assertEquals(COMPLETION_RATING_ILLEGAL_ARGUMENT_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Test {@link CompletedUserResource#CompletedUserResource(String, int, CompletionRating, long)}
     * when Date is 0. Expects {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWhenCompletionDateIsZero() throws DAOException {
        try {
            new CompletedUserResource(VALID_USER_ID, VALID_RESOURCE_ID, CompletionRating.SATISFIED, 0);
        } catch (final IllegalArgumentException e) {
            assertEquals(COMPLETED_DATE_GREATER_THAN_ZERO, e.getMessage());
            throw e;
        }
    }

    /**
     * Test {@link CompletedUserResource#CompletedUserResource(String, int, CompletionRating, long)}
     * when Completion Date is negative. Expects
     * {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWhenCompletionDateIsNegative() throws DAOException {
        try {
            new CompletedUserResource(VALID_USER_ID, VALID_RESOURCE_ID, CompletionRating.SATISFIED, -1);
        } catch (final IllegalArgumentException e) {
            assertEquals(COMPLETED_DATE_GREATER_THAN_ZERO, e.getMessage());
            throw e;
        }
    }
}