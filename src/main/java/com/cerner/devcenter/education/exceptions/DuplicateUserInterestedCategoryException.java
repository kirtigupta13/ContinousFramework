package com.cerner.devcenter.education.exceptions;

/**
 * An exception to handle error for duplicate user interested topics.
 * 
 * @author Gunjan Kaphle (GK045931)
 */
public class DuplicateUserInterestedCategoryException extends Exception {

    private static final long serialVersionUID = -5236590675770414673L;

    /**
     * Create new {@link DuplicateUserInterestedCategoryException}}.
     */
    public DuplicateUserInterestedCategoryException(String message) {
        super(message);
    }
}
