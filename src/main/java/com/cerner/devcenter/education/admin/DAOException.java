package com.cerner.devcenter.education.admin;

/**
 * A wrapper exception to generalize Data Access Object errors.
 *
 * @author NO032013
 * @author NB044557
 */
public class DAOException extends Exception {
    private static final long serialVersionUID = -2943469731629321273L;

    /**
     * Create new DAOException.
     */
    public DAOException() {
        super();
    }

    /**
     * Create new DAOException with a description of the error.
     *
     * @param message
     *            String description of the error
     */
    public DAOException(String message) {
        super(message);
    }

    /**
     * Create new DAOException with a description of error and the thrown object that caused the error.
     *
     * @param message
     *            String description of the error
     * @param cause
     *            {@link Throwable} object that caused the exception
     */
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create new DAOException with the thrown object that caused the error.
     *
     * @param cause
     *            {@link Throwable} object that caused the exception
     */
    public DAOException(Throwable cause) {
        super(cause);
    }

    /**
     * Create new DAOException with the thrown object that caused the error, a description of the error, and whether the
     * exception should be suppressible or throwable
     *
     * @param message
     *            String description of the error
     * @param cause
     *            {@link Throwable} object that caused the exception
     * @param enableSuppression
     *            whether or not suppression is enabled or disabled
     * @param writableStackTrace
     *            whether or not the stack trace should be writable
     */
    public DAOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
