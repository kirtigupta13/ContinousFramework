package com.cerner.devcenter.education.helpers;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;

import com.cerner.devcenter.education.models.Resource;

/**
 * Compares {@link Resource} objects based on LevenshteinDistance of their name
 * and if score difference is zero, compares based on description.
 * 
 * @author Anudeep Kumar Gadam (AG045334)
 * @author Santosh Kumar (SK051343)
 * 
 */
public class ResourceNameSorter implements Comparator<Resource> {

    private String search;
    private static final String INVALID_SEARCH = "Search string cannot be null or empty";

    /**
     * Constructor to set the search string.
     * 
     * @param search
     *            represents the user entered search(cannot be null or empty)
     * @throws IllegalArgumentException
     *             when search string is null or empty
     */
    public ResourceNameSorter(String search) {
        checkArgument(StringUtils.isNotEmpty(search), INVALID_SEARCH);
        this.search = search;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(Resource resource1, Resource resource2) {
        String resourceName1 = resource1.getResourceName();
        String resourceName2 = resource2.getResourceName();
        int resource1Diff = getLevenshteinDistanceFromSearch(resourceName1);
        int resource2Diff = getLevenshteinDistanceFromSearch(resourceName2);
        int scoreDiff = resource1Diff - resource2Diff;
        if (scoreDiff == 0) {
            return getLevenshteinDistanceFromSearch(resource1.getDescription())
                    - getLevenshteinDistanceFromSearch(resource2.getDescription());
        }
        return scoreDiff;
    }

    /**
     * Gets the LevenshteinDistance of the compareString from the search string.
     * 
     * This is the number of changes needed to change compareString into search
     * string, where each change is a single character modification (deletion,
     * insertion or substitution).
     * 
     * <p>
     * <a href=
     * "http://people.cs.pitt.edu/~kirk/cs1501/Pruhs/Fall2006/Assignments/editdistance/Levenshtein%20Distance.htm">
     * Reference link</a>
     * </p>
     * 
     * <pre>
     * StringUtils.getLevenshteinDistance(null, *) = IllegalArgumentException
     * StringUtils.getLevenshteinDistance(*, null) = IllegalArgumentException
     * StringUtils.getLevenshteinDistance("","") = 0
     * StringUtils.getLevenshteinDistance("","a") = 1
     * StringUtils.getLevenshteinDistance("aaapppp", "") = 7
     * StringUtils.getLevenshteinDistance("frog", "fog") = 1
     * StringUtils.getLevenshteinDistance("fly", "ant") = 3
     * StringUtils.getLevenshteinDistance("elephant", "hippo") = 7
     * StringUtils.getLevenshteinDistance("hippo", "elephant") = 7
     * StringUtils.getLevenshteinDistance("hippo", "zzzzzzzz") = 8
     * StringUtils.getLevenshteinDistance("hello", "hallo") = 1
     * </pre>
     * 
     * @param compareString
     *            string whose distance needs to be calculated from the search
     *            string (Can't be null).
     * @return Levenstein distance between search string and parameter passed.
     * @throws IllegalArgumentException
     *             when compareString is <code>null</code>.
     */
    private int getLevenshteinDistanceFromSearch(String compareString) {
        checkArgument(compareString != null, INVALID_SEARCH);
        return StringUtils.getLevenshteinDistance(compareString.toLowerCase().trim(), search.toLowerCase().trim());
    }
}