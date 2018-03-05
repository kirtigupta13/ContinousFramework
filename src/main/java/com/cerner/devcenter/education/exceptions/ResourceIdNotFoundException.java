package com.cerner.devcenter.education.exceptions;

/**
 * {@code Exception} that handles the error of resourceId not found in database.
 * 
 * @author Abhi Purella AP045635
 */
public class ResourceIdNotFoundException extends Exception {
    private static final long serialVersionUID = -8136561740208056090L;

    /**
     * to create new {@link ResourceIdNotFoundException}
     * 
     * @param message
     *            message for exception.
     */
    public ResourceIdNotFoundException(String message) {
        super(message);
    }
}
