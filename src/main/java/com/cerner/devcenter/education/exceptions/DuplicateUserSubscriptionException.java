package com.cerner.devcenter.education.exceptions;

/**
 * An exception to handle error for duplicate user subscribed categories.
 * 
 * @author Mani Teja Kurapati (MK051340)
 */
public class DuplicateUserSubscriptionException extends Exception {

    private static final long serialVersionUID = 5354451272605871930L;

    /**
     * Create new {@link DuplicateUserSubscriptionException}}.
     */
    public DuplicateUserSubscriptionException(String message) {
        super(message);
    }
}
