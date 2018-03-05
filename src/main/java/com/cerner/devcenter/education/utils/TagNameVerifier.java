package com.cerner.devcenter.education.utils;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

/**
 * Verifies whether or not tag names are valid
 * 
 * @author Amos Bailey (AB032627)
 *
 */
public class TagNameVerifier {
    /**
     * Ensures a tag name is a valid argument for a method. If not, throws an
     * {@link IllegalArgumentException} with an applicable message.
     * 
     * @param tagName
     *            - the tag name to test for validity.
     * @throws IllegalArgumentException
     *             when the tag name is null, empty, or blank.
     */
    public static void verifyTagNameArgument(final String tagName) {
        checkArgument(tagName != null, Constants.TAG_NAME_CANNOT_BE_NULL);
        checkArgument(StringUtils.isNotBlank(tagName), Constants.TAG_NAME_CANNOT_BE_BLANK_OR_EMPTY);
    }

    /**
     * Ensures a collection of tagNames is a valid argument for a method. If
     * not, throws an {@link IllegalArgumentException} with an applicable
     * message.
     * 
     * @param tagNames
     *            A collection of string objects.
     * 
     * @throws IllegalArgumentException
     *             When the collection of tags is empty, null, or contains a
     *             string that is blank, empty, or null.
     */
    public static void verifyTagNameCollectionArgument(final Collection<String> tagNames) {
        checkArgument(tagNames != null, Constants.TAG_LIST_NULL);
        checkArgument(!tagNames.isEmpty(), Constants.TAG_LIST_EMPTY);
        for (String tagName : tagNames) {
            verifyTagNameArgument(tagName);
        }
    }
}
