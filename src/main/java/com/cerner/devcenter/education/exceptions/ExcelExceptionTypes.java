package com.cerner.devcenter.education.exceptions;

import com.cerner.devcenter.education.utils.Constants;

/***
 * Stores the possible {@link ExcelException} causes and their associated
 * messages.
 * 
 * @author JZ022690
 *
 */
public enum ExcelExceptionTypes {
    RESOURCE_TYPE(Constants.UPLOAD_CELL_RESOURCE_TYPE_ERROR_I18N, "Error with resource type in cell: {0}"),
    LINK(Constants.UPLOAD_CELL_LINK_ERROR_I18N, "Link cannot be converted to a java.net.URL object from cell: {0}"),
    CATEGORY(Constants.UPLOAD_CELL_CATEGORY_ERROR_I18N, "Error with category in cell: {0}"),
    CATEGORY_DIFFICULTY(Constants.UPLOAD_CELL_CATEGORY_DIFFIULTY_ERROR_I18N,
            "Error with category difficulty in cell: {0}"),
    NOT_A_NUMBER(Constants.UPLOAD_CELL_NUMBER_ERROR_I18N, "Expecting a number in cell: {0}"),
    NOT_A_STRING(Constants.UPLOAD_CELL_TEXT_ERROR_I18N, "Expecting a string in cell: {0}"),
    SKIPPED_CELL(Constants.UPLOAD_CELL_SKIPPED_ERROR_I18N, "Column was skipped before reaching cell: {0}"),
    FILE_TYPE_ERROR(Constants.UPLOAD_TYPE_ERROR_I18N, "The file {0} could not be parsed into a workbook."),
    ENCRYPTED(Constants.UPLOAD_ENCRYPTION_ERROR_I18N, "The file {0} is password protected."),
    FILE_FORMAT_ERROR(Constants.UPLOAD_FORMAT_ERROR, "The file {0} was not in a readable format."),
    FILE_ERROR(Constants.UPLOAD_ERROR_I18N, "Could not create a workbook from file {0}."),
    NO_CATEGORY(Constants.UPLOAD_NO_CATEGORY_ERROR_I18N, "Could not find a category in cell: {0}"),
    CATEGORY_DIFFICULTY_PAIR(Constants.UPLOAD_CELL_CATEGORY_PAIR_ERROR_I18N,
            "Could not find 2 values for a category pair in cell: {0}"),
    CATEGORY_NAME(Constants.UPLOAD_CELL_CATEGORY_NAME_ERROR_I18N, "Category name caused a DAOException in cell: {0}"),
    CATEGORY_DIFFICULTY_NOT_AN_INTEGER(Constants.UPLOAD_CELL_DIFFICULTY_NOT_AN_INTEGER_ERROR_I18N,
            "One of the category difficulties is not an integer in cell: {0}"),
    RESOURCE_EXISTS_ERROR(Constants.UPLOAD_RESOURCE_EXISTS_ERROR_I18N,
            "The resource name in cell {0} already exists in the database");

    private final String i18nMessage;
    private final String logMessage;

    /***
     * Creates a new {@link ExcelExceptionTypes}
     * 
     * @param i18nMessage
     *            The value for the localized message to possibly be displayed
     *            to the user.
     * @param logMessage
     *            The message to be logged.
     */
    ExcelExceptionTypes(String i18nMessage, String logMessage) {
        this.i18nMessage = i18nMessage;
        this.logMessage = logMessage;
    }

    /***
     * @return {@link String} for the localized message value.
     */
    public String getI18NMessage() {
        return this.i18nMessage;
    }

    /***
     * @return {@link String} containing the message to be logged.
     */
    public String getLogMessage() {
        return this.logMessage;
    }
}
