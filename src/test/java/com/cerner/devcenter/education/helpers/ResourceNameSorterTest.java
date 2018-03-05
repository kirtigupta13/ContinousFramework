package com.cerner.devcenter.education.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;

import com.cerner.devcenter.education.models.Resource;

/**
 * Tests {@link ResourceNameSorter} functionality.
 * 
 * @author Anudeep Kumar Gadam (AG045334).
 *
 */
public class ResourceNameSorterTest {

    @InjectMocks
    private ResourceNameSorter resourceComparator;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private Resource resource1, resource2;
    private static final String url = "http://www.google.com";
    private static final String DESCRIPTION = "search";
    private static final String DESCRIPTION1 = "searching";
    private static final String NAME = "google";
    private static final String NAME1 = "facebook";
    private static final String EMPTY_STRING = "";
    private static final String BLANK_STIRNG = "  ";
    private static final String NAME_NULL = null;
    private static final String DESCRIPTION_NULL = null;

    @Before
    public void setUp() {
        resourceComparator = new ResourceNameSorter(DESCRIPTION);
    }

    @After
    public void tearDown() {
        resourceComparator = null;
    }

    /**
     * Tests constructor when search is null.
     */
    @Test
    public void testConstructorForNullSearch() {
        expectedException.expect(IllegalArgumentException.class);
        new ResourceNameSorter(null);
    }

    /**
     * Tests constructor when search is empty.
     */
    @Test
    public void testConstructorForEmptySearch() {
        expectedException.expect(IllegalArgumentException.class);
        new ResourceNameSorter(EMPTY_STRING);
    }

    /**
     * Tests {@link ResourceNameSorter#compare(Resource, Resource)} when name of
     * first resource object is null.
     * 
     * @throws MalformedURLException
     */
    @Test
    public void testComapreForResource1WithNameNull() throws MalformedURLException {
        expectedException.expect(IllegalArgumentException.class);
        resource1 = new Resource(1, new URL(url), DESCRIPTION, NAME_NULL);
        resource2 = new Resource(2, new URL(url), DESCRIPTION, NAME);
        resourceComparator.compare(resource1, resource2);
    }

    /**
     * Tests {@link ResourceNameSorter#compare(Resource, Resource)} when
     * description of first resource object is null.
     * 
     * @throws MalformedURLException
     */
    @Test
    public void testComapreForResource1WithDescriptionNull() throws MalformedURLException {
        expectedException.expect(IllegalArgumentException.class);
        resource1 = new Resource(1, new URL(url), DESCRIPTION_NULL, NAME);
        resource2 = new Resource(2, new URL(url), DESCRIPTION, NAME);
        resourceComparator.compare(resource1, resource2);
    }

    /**
     * Tests {@link ResourceNameSorter#compare(Resource, Resource)} when name of
     * second resource object is null.
     * 
     * @throws MalformedURLException
     */
    @Test
    public void testComapreForResource2WithNameNull() throws MalformedURLException {
        expectedException.expect(IllegalArgumentException.class);
        resource1 = new Resource(1, new URL(url), DESCRIPTION, NAME);
        resource2 = new Resource(2, new URL(url), DESCRIPTION, NAME_NULL);
        resourceComparator.compare(resource1, resource2);
    }

    /**
     * Tests {@link ResourceNameSorter#compare(Resource, Resource)} when
     * description of second resource object is null.
     * 
     * @throws MalformedURLException
     */
    @Test
    public void testComapreForResource2WithDescriptionNull() throws MalformedURLException {
        expectedException.expect(IllegalArgumentException.class);
        resource1 = new Resource(1, new URL(url), DESCRIPTION, NAME);
        resource2 = new Resource(2, new URL(url), DESCRIPTION_NULL, NAME);
        resourceComparator.compare(resource1, resource2);
    }

    /**
     * Tests {@link ResourceNameSorter#compare(Resource, Resource)} when first argument is
     * same as the second argument
     * 
     * @throws MalformedURLException
     */
    @Test
    public void testCompareWhenNameAndDescriptionAreSame() throws MalformedURLException {
        resource1 = new Resource(1, new URL(url), DESCRIPTION, NAME);
        resource2 = new Resource(2, new URL(url), DESCRIPTION, NAME);
        assertEquals(0, resourceComparator.compare(resource1, resource2));
    }

    /**
     * Tests {@link ResourceNameSorter#compare(Resource, Resource)} when name of first
     * resource is less than name of second resource
     * 
     * @throws MalformedURLException
     */
    @Test
    public void testCompareWithResource1NameLessThanResource2Name() throws MalformedURLException {
        resource1 = new Resource(1, new URL(url), DESCRIPTION, NAME);
        resource2 = new Resource(2, new URL(url), DESCRIPTION, NAME1);
        int result = resourceComparator.compare(resource1, resource2);
        assertTrue(result < 0);
    }

    /**
     * Tests {@link ResourceNameSorter#compare(Resource, Resource)} when name of first
     * resource is greater than name of second resource
     * 
     * @throws MalformedURLException
     */
    @Test
    public void testCompareWithResource1NameGreaterThanResource2Name() throws MalformedURLException {
        resource1 = new Resource(1, new URL(url), DESCRIPTION, NAME1);
        resource2 = new Resource(2, new URL(url), DESCRIPTION, NAME);
        int result = resourceComparator.compare(resource1, resource2);
        assertTrue(result > 0);
    }

    /**
     * Tests {@link ResourceNameSorter#compare(Resource, Resource)} when description of
     * first resource is less than description of second resource
     * 
     * @throws MalformedURLException
     */
    @Test
    public void testCompareWithResource1DescLessThanResource2Desc() throws MalformedURLException {
        resource1 = new Resource(1, new URL(url), DESCRIPTION, NAME);
        resource2 = new Resource(2, new URL(url), DESCRIPTION1, NAME);
        int result = resourceComparator.compare(resource1, resource2);
        assertTrue(result < 0);
    }

    /**
     * Tests {@link ResourceNameSorter#compare(Resource, Resource)} when description of
     * first resource is greater than description of second resource
     * 
     * @throws MalformedURLException
     */
    @Test
    public void testCompareWithResource1DescGreaterThanResource2Desc() throws MalformedURLException {
        resource1 = new Resource(1, new URL(url), DESCRIPTION1, NAME);
        resource2 = new Resource(2, new URL(url), DESCRIPTION, NAME);
        int result = resourceComparator.compare(resource1, resource2);
        assertTrue(result > 0);
    }
}
