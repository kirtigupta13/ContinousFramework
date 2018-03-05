package com.cerner.devcenter.education.helpers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link StringDistanceSorter} class.
 * 
 * @author Abhi Purella (AP045635)
 *
 */
public class StringDistanceSorterTest {

    private StringDistanceSorter stringDistanceSorter;

    private static final String STRING = "message";

    @Before
    public void setUp() {
        stringDistanceSorter = new StringDistanceSorter();
    }

    @After
    public void tearDown() {
        stringDistanceSorter = null;
    }

    /**
     * Tests setSearch when search is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetSearchForEmptySearch() {
        stringDistanceSorter.setSearch("");
    }

    /**
     * Tests setSearch when search is empty spaces.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetSearchForEmptySpacesSearch() {
        stringDistanceSorter.setSearch(" ");
    }

    /**
     * Tests setSearch when search is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetSearchForNullSearch() {
        stringDistanceSorter.setSearch(null);
    }

    /**
     * Tests constructor when search is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorForNullSearch() {
        new StringDistanceSorter(null);
    }

    /**
     * Tests constructor when search is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorForEmptySearch() {
        new StringDistanceSorter("");
    }

    /**
     * Tests constructor when search is empty spaces.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorForEmptySpacesSearch() {
        new StringDistanceSorter(" ");
    }

    /**
     * Tests compare for equal strings.
     */
    @Test
    public void testcompareForSameStrings() {
        stringDistanceSorter = new StringDistanceSorter("search");
        assertEquals(0, stringDistanceSorter.compare(STRING, STRING));
    }

    /**
     * Tests compare for different strings.
     */
    @Test
    public void testcompareForDifferentStrings() {
        stringDistanceSorter = new StringDistanceSorter("search");
        assertEquals(1, stringDistanceSorter.compare("bearch", "aearch"));
    }
}
