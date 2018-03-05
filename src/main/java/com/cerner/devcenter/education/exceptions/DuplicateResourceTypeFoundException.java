package com.cerner.devcenter.education.exceptions;

/**
 * An exception to handle error for duplicate resource types
 * 
 * @author Gunjan Kaphle GK045931
 */
public class DuplicateResourceTypeFoundException extends RuntimeException {

    private static final long serialVersionUID = -8512456083218872343L;

    /**
     * Create new {@link DuplicateResourceTypeFoundException}}
     */
    public DuplicateResourceTypeFoundException(String message) {
        super(message);
    }

}
