package com.cerner.devcenter.education.exceptions;

/**
 * {@code Exception} that handles the error of categoryId not found in database.
 * 
 * @author Abhi Purella AP045635
 * @author Santosh Kumar (SK051343)
 */
public class CategoryIdNotFoundException extends Exception {
    private static final long serialVersionUID = -6734767968910880733L;

    /**
     * to create new {@link CategoryIdNotFoundException}
     * 
     * @param message
     *            message for exception.
     */
    public CategoryIdNotFoundException(String message) {
        super(message);
    }
}
