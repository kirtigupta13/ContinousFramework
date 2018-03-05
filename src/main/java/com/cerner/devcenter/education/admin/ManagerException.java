package com.cerner.devcenter.education.admin;

/**
 * A wrapper exception to genericize Data Access Object errors.
 *
 * @author Piyush Bandil (PB042879)
 */
public class ManagerException extends RuntimeException {

    private static final long serialVersionUID = -541416861515770594L;

    /**
     * Create new {@link ManagerException}.
     */
    public ManagerException() {
        super();
    }

    /**
     * Create new ManagerException with a description of error and the thrown
     * object that caused the error.
     *
     * @param message
     *            {@link String} description of the error
     * @param cause
     *            {@link Throwable} object that caused the exception
     */
    public ManagerException(String message, Throwable cause) {
        super(message, cause);
    }

}
