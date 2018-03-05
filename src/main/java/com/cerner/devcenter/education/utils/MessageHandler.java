package com.cerner.devcenter.education.utils;

import static com.google.common.base.Preconditions.checkArgument;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;

/***
 * Used to help store different levels of messages. Typically for displaying to
 * the user through the UI.
 * 
 * @author Jacob Zimmermann (JZ022690)
 */
public class MessageHandler {
    private List<String> errorMessages = new ArrayList<String>();
    private List<String> warningMessages = new ArrayList<String>();
    private List<String> successMessages = new ArrayList<String>();

    private static final String INVALID_ERROR_MESSAGE = "Error message cannot be null, blank, or empty.";
    private static final String INVALID_WARNING_MESSAGE = "Warning message cannot be null, blank, or empty.";
    private static final String INVALID_SUCCESS_MESSAGE = "Success message cannot be null, blank, or empty.";
    private static final String INVALID_HTML_CLASS_MESSAGE = "HTML class cannot be null, blank, or empty.";
    private static final String NULL_MESSAGE_LIST_MESSAGE = "Message list cannot be null.";

    private static final String HTML_FORMAT = "<div class=\"{0}\">{1}</div>";

    /***
     * @return {@link ImmutableList} of {@link String} containing the error
     *         messages. Can be empty. Cannot be null.
     */
    public ImmutableList<String> getErrorMessages() {
        return ImmutableList.copyOf(errorMessages);
    }

    /***
     * Add an error message to the error message list
     * 
     * @param errorMessage
     *            {@link String} containing the error message to add. Cannot be
     *            null, blank, or empty.
     */
    public void addError(String errorMessage) {
        checkArgument(StringUtils.isNotBlank(errorMessage), INVALID_ERROR_MESSAGE);
        errorMessages.add(errorMessage);
    }

    /***
     * @return {@link ImmutableList} of {@link String} containing the warning
     *         messages. Can be empty. Cannot be null.
     */
    public ImmutableList<String> getWarningMessages() {
        return ImmutableList.copyOf(warningMessages);
    }

    /***
     * Add an warning message to the warning message list
     * 
     * @param warningMessage
     *            {@link String} containing the warning message to add. Cannot
     *            be null, blank, or empty.
     */
    public void addWarning(String warningMessage) {
        checkArgument(StringUtils.isNotBlank(warningMessage), INVALID_WARNING_MESSAGE);
        warningMessages.add(warningMessage);
    }

    /***
     * @return {@link ImmutableList} of {@link String} containing the success
     *         messages. Can be empty. Cannot be null.
     */
    public ImmutableList<String> getSuccessMessages() {
        return ImmutableList.copyOf(successMessages);
    }

    /***
     * Add an success message to the success message list
     * 
     * @param successMessage
     *            {@link String} containing the success message to add. Cannot
     *            be null, blank, or empty.
     */
    public void addSuccess(String successMessage) {
        checkArgument(StringUtils.isNotBlank(successMessage), INVALID_SUCCESS_MESSAGE);
        successMessages.add(successMessage);
    }

    /***
     * Create a list of the messages using HTML div's to break up the messages.
     * 
     * @param messageList
     *            {@link List} of {@link String} containing the messages to use.
     * @param htmlClass
     *            {@link String} containing the htmlClass to use for styling the
     *            elements. Cannot be null, blank, or empty.
     * @return {@link String} containing the HTML div messages
     * @throws IllegalArgumentException
     *             if htmlClass is null, blank, or empty
     */
    public static String buildMessageHTML(List<String> messageList, String htmlClass) {
        checkArgument(messageList != null, NULL_MESSAGE_LIST_MESSAGE);
        checkArgument(StringUtils.isNotBlank(htmlClass), INVALID_HTML_CLASS_MESSAGE);
        StringBuilder messageBuilder = new StringBuilder();
        MessageFormat htmlFormatter = new MessageFormat(HTML_FORMAT);
        for (String msg : messageList) {
            messageBuilder.append(htmlFormatter.format(new Object[] { htmlClass, msg }));
        }
        return messageBuilder.toString();
    }
}
