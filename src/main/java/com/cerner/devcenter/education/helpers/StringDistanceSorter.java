package com.cerner.devcenter.education.helpers;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;

/**
 * Used to compares the two strings based on their Levenshtein distance from the
 * search string.
 * 
 * @author Abhi Purella (AP045635)
 *
 */
public class StringDistanceSorter implements Comparator<String> {

    private String search;
    private static final String INVALID_SEARCH = "Search string cannot be null or empty";

    /**
     * Constructor to set the search string.
     * 
     * @param search
     *            represents the user entered search(cannot be null or empty)
     */
    public StringDistanceSorter(String search) {
        setSearch(search);
    }

    /**
     * Default constructor with package private scope for unit testing.
     */
    public StringDistanceSorter() {
    }

    /**
     * @param search
     *            represents the user entered search(cannot be null or empty)
     * @throws IllegalArgumentException
     *             when search string is empty or null
     */
    public void setSearch(String search) {
        checkArgument(StringUtils.isNotBlank(search), INVALID_SEARCH);
        this.search = search;
    }

    @Override
    public int compare(String o1, String o2) {
        int scoreDiff = compareStrings(o1) - compareStrings(o2);
        if ((compareStrings(o1) == 0 && compareStrings(o2) == 0) || scoreDiff == 0) {
            return o1.compareTo(o2);
        }
        return (compareStrings(o1) - compareStrings(o2));
    }

    /**
     * Gets the LevenshteinDistance of the compareString from the search string.
     * 
     * @param compareString
     *            string whose distance needs to be calculated from the search
     *            string
     * @return integer representing the distance
     */
    private int compareStrings(String compareString) {
        return StringUtils.getLevenshteinDistance(compareString.toLowerCase().trim(), search.toLowerCase().trim());
    }
}