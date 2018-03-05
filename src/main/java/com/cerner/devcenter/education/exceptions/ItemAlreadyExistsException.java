package com.cerner.devcenter.education.exceptions;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.commons.lang3.StringUtils;

/***
 * An {@link Exception} used to signal if an item already exists that matches
 * one that is being added to the database.
 * 
 * @author Jacob Zimmermann (JZ022690)
 *
 */
public class ItemAlreadyExistsException extends Exception {
    private static final long serialVersionUID = -7592001489803710862L;

    /***
     * Create a new {@link ItemAlreadyExistsException} with a given message and
     * optional cause.
     * 
     * @param message
     *            {@link String} containing the error message. Cannot be null,
     *            blank, or empty.
     * @throws IllegalArgumentException
     *             if message is null, blank, or empty.
     */
    public ItemAlreadyExistsException(final String message) {
        super(getValidMessage(message));
    }

    /***
     * Ensure the message is not null, blank, or empty.
     * 
     * @param message
     *            {@link String} message to check
     * @return message if it is not null, blank, or empty.
     * @throws IllegalArgumentException
     *             if message is null, blank, or empty.
     */
    private static String getValidMessage(final String message) {
        checkArgument(StringUtils.isNotBlank(message), "Message is invalid.");
        return message;
    }
}
