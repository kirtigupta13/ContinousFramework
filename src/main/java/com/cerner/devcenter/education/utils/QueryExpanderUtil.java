package com.cerner.devcenter.education.utils;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Contains methods for expanding SQL queries.
 * 
 * @author Amos Bailey (AB032627)
 *
 */
public class QueryExpanderUtil {
    /**
     * Given the number of placeholders needed, creates a string containing that
     * many ? marks, each separated by commas.
     * 
     * E.g., when given the integer 5 as a parameter, the string "?,?,?,?,?"
     * will be generated.
     * 
     * @param places
     *            - The number of placeholders needed. Must be positive.
     * @return A string containing the specified number of placeholders.
     * @throws IllegalArgumentException
     *             when the specified parameter is not positive.
     * 
     */
    public static String expandPlaceholders(final int places) {
        checkArgument(places > 0, Constants.NUMBER_OF_PLACEHOLDERS_MUST_BE_POSITIVE);
        StringBuilder expandedStatement = new StringBuilder(2 * places - 1);
        expandedStatement.append("?");
        for (int i = 1; i < places; i++) {
            expandedStatement.append(",?");
        }
        return expandedStatement.toString();
    }
}
