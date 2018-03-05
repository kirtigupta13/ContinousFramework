package com.cerner.devcenter.education.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.cerner.devcenter.education.utils.CompletionRating;

/**
 * Tests the functionalities of {@link CompletedResource}
 *
 * @author Vinutha Nuchimaniyanda(VN046193)
 * @author Navya Rangeneni (NR046827)
 * @author Rishabh Bhojak (RB048032)
 */
public class CompletedResourceTest {
    private static final String VALID_USER_ID = "VN046193";
    private static final int VALID_RESOURCE_ID = 4;
    private static final String VALID_RESOURCE_NAME = "Mocking";
    private static final String EMPTY_STRING = "";
    private static final String WHITESPACE = "   ";
    private static final String USER_ID_ILLEGAL_ARGUMENT_MESSAGE = "User ID cannot be null/empty/whitespace";
    private static final String RESOURCE_ID_ILLEGAL_ARGUMENT_MESSAGE = "Resource ID must be greater than 0.";
    private static final String RESOURCE_NAME_ILLEGAL_ARGUMENT_MESSAGE = "Resource Name cannot be null/empty/whitespace";
    private static final String COMPLETION_RATING_ILLEGAL_ARGUMENT_MESSAGE = "Completed resource rating object cannot be null";
    private static final Date VALID_DISPLAY_DATE = new Date();
    private static final URL VALID_RESOURCE_LINK = createUrl("http://www.junit.org");

    /**
     * Test constructor {@link CompletedResource#CompletedResource(String, int, String, URL,
     * CompletionRating, Date)}, {@link CompletedResource#getUserId()},
     * {@link CompletedResource#getResourceId()},
     * {@link CompletedResource#getResourceName()},
     * {@link CompletedResource#getResourceLink()} and
     * {@link CompletedResource#getCompletedRating()} and
     * {@link CompletedResource#getCompletionDate()} with valid user id,
     * resource id, resource name, resource link with valid SATISFIED rating,
     * completion date and formatted string date.
     */
    @Test
    public void testCompletedResourceConstructorValidValues() {
        final CompletedResource completedResource = new CompletedResource(VALID_USER_ID, VALID_RESOURCE_ID,
                VALID_RESOURCE_NAME, VALID_RESOURCE_LINK, CompletionRating.SATISFIED, VALID_DISPLAY_DATE);
        assertSame(VALID_USER_ID, completedResource.getUserId());
        assertSame(VALID_RESOURCE_ID, completedResource.getResourceId());
        assertSame(VALID_RESOURCE_NAME, completedResource.getResourceName());
        assertSame(VALID_RESOURCE_LINK, completedResource.getResourceLink());
        assertSame(CompletionRating.SATISFIED, completedResource.getCompletedRating());
        assertSame(VALID_DISPLAY_DATE, completedResource.getCompletionDate());
        final Format formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a zzz");
        final String validFormattedDate = formatter.format(VALID_DISPLAY_DATE);
        assertTrue(validFormattedDate.equals(completedResource.getFormattedDate()));
    }

    /**
     * Test {@link CompletedResource#CompletedResource(String, int, String, URL, CompletionRating, Date)} 
     * when user id is empty string. Expects
     * {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCompletedResourceConstructorWhenUserIdEmpty() {
        try {
            new CompletedResource(EMPTY_STRING, VALID_RESOURCE_ID, VALID_RESOURCE_NAME, VALID_RESOURCE_LINK,
                    CompletionRating.SATISFIED, VALID_DISPLAY_DATE);
        } catch (final IllegalArgumentException e) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Test
     * {@link CompletedResource#CompletedResource(String, int, String, URL, CompletionRating, Date)}
     * when user id is null string. Expects {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCompletedResourceConstructorWhenUserIdNull() {
        try {
            new CompletedResource(null, VALID_RESOURCE_ID, VALID_RESOURCE_NAME, VALID_RESOURCE_LINK,
                    CompletionRating.SATISFIED, VALID_DISPLAY_DATE);
        } catch (final IllegalArgumentException e) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Test
     * {@link CompletedResource#CompletedResource(String, int, String, URL, CompletionRating, Date)}
     * when user id is whitespace. Expects {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCompletedResourceConstructorWhenUserIdWhitespace() {
        try {
            new CompletedResource(WHITESPACE, VALID_RESOURCE_ID, VALID_RESOURCE_NAME, VALID_RESOURCE_LINK,
                    CompletionRating.SATISFIED, VALID_DISPLAY_DATE);
        } catch (final IllegalArgumentException e) {
            assertEquals(USER_ID_ILLEGAL_ARGUMENT_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Test
     * {@link CompletedResource#CompletedResource(String, int, String, URL, CompletionRating, Date)}
     * when resource id is less than 1. Expects
     * {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCompletedResourceConstructorResourceIdWhenNegative() {
        try {
            new CompletedResource(VALID_USER_ID, -2, VALID_RESOURCE_NAME, VALID_RESOURCE_LINK,
                    CompletionRating.SATISFIED, VALID_DISPLAY_DATE);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_ID_ILLEGAL_ARGUMENT_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Test
     * {@link CompletedResource#CompletedResource(String, int, String, URL, CompletionRating, Date)}
     * when resource id is 0. Expects {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCompletedResourceConstructorResourceIdWhenZero() {
        try {
            new CompletedResource(VALID_USER_ID, 0, VALID_RESOURCE_NAME, VALID_RESOURCE_LINK,
                    CompletionRating.SATISFIED, VALID_DISPLAY_DATE);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_ID_ILLEGAL_ARGUMENT_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Test
     * {@link CompletedResource#CompletedResource(String, int, String, URL, CompletionRating, Date)}
     * when resource name is empty string. Expects
     * {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCompletedResourceConstructorWhenResourceNameEmpty() {
        try {
            new CompletedResource(VALID_USER_ID, VALID_RESOURCE_ID, EMPTY_STRING, VALID_RESOURCE_LINK,
                    CompletionRating.SATISFIED, VALID_DISPLAY_DATE);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_NAME_ILLEGAL_ARGUMENT_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Test
     * {@link CompletedResource#CompletedResource(String, int, String, URL, CompletionRating, Date)}
     * when resource name is null string. Expects
     * {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCompletedResourceConstructorWhenResourceNameNull() {
        try {
            new CompletedResource(VALID_USER_ID, VALID_RESOURCE_ID, null, VALID_RESOURCE_LINK,
                    CompletionRating.SATISFIED, VALID_DISPLAY_DATE);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_NAME_ILLEGAL_ARGUMENT_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Test
     * {@link CompletedResource#CompletedResource(String, int, String, URL, CompletionRating, Date)}
     * when resource name is whitespace. Expects
     * {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCompletedResourceConstructorWhenResourceNameWhitespace() {
        try {
            new CompletedResource(VALID_USER_ID, VALID_RESOURCE_ID, WHITESPACE, VALID_RESOURCE_LINK,
                    CompletionRating.SATISFIED, VALID_DISPLAY_DATE);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_NAME_ILLEGAL_ARGUMENT_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Test
     * {@link CompletedResource#CompletedResource(String, int, String, URL, CompletionRating, Date)}
     * when completion rating is null. Expects {@link NullPointerException}.
     */
    @Test(expected = NullPointerException.class)
    public void testCompletedResourceConstructorCompletedRatingWhenNull() {
        try {
            new CompletedResource(VALID_USER_ID, VALID_RESOURCE_ID, VALID_RESOURCE_NAME, VALID_RESOURCE_LINK, null,
                    VALID_DISPLAY_DATE);
        } catch (final NullPointerException e) {
            assertEquals(COMPLETION_RATING_ILLEGAL_ARGUMENT_MESSAGE, e.getMessage());
            throw e;
        }
    }

    private static URL createUrl(final String spec) {
        try {
            return new URL(spec);
        } catch (final MalformedURLException e) {
            Assert.fail();
        }
        return null;
    }
}