package com.cerner.devcenter.education.exceptions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.text.MessageFormat;

import org.apache.poi.ss.util.CellAddress;
import org.springframework.util.StringUtils;

import com.cerner.devcenter.education.models.Resource;

/***
 * An {@link Exception} to handle error for creating a {@link Resource} from a
 * row in an excel file
 * 
 * @author JZ022690
 *
 */
public class ExcelException extends Exception {

    private static final long serialVersionUID = 6128888703990511579L;
    private CellAddress cellAddress;
    private String fileName;
    private ExcelExceptionTypes excelType;

    /***
     * Create a new {@link ExcelException}
     * 
     * @param excelType
     *            The {@link ExcelExceptionTypes} that specifies the type of
     *            excel exception and messages to log and display. Cannot be
     *            null.
     * @param cellAddress
     *            The {@link CellAddress} of the cell that caused the error.
     *            Cannot be null.
     * @param cause
     *            The {@link Throwable} with information about the root cause.
     *            Can be null if cause is unknown or nonexistent.
     * @throws NullPointerException
     *             when the excelType or cellAddress are null
     */
    public ExcelException(ExcelExceptionTypes excelType, CellAddress cellAddress, Throwable cause) {
        super(buildMessage(excelType, cellAddress), cause);
        this.cellAddress = cellAddress;
        this.excelType = excelType;
    }

    /***
     * Create a new {@link ExcelException} for workbook related issues
     * 
     * @param excelType
     *            The {@link ExcelExceptionTypes} that specifies the type of
     *            excel exception and messages to log and display.
     * @param fileName
     *            The {@link String} name of the file that caused the exception.
     * @param cause
     *            The {@link Throwable} with information about the root cause.
     *            Can be null if cause is unknown or nonexistent.
     */
    public ExcelException(ExcelExceptionTypes excelType, String fileName, Throwable cause) {
        super(buildMessage(excelType, fileName), cause);
        this.excelType = excelType;
        this.fileName = fileName;
    }

    /***
     * @return {@link String} with the address of the cell or empty if the
     *         exception isn't tied to a cell.
     */
    public String getAddress() {
        return cellAddress == null ? "" : cellAddress.toString();
    }

    /***
     * 
     * @return {@link String} with the name of the file or empty if the
     *         exception is not tied to a file
     */
    public String getFileName() {
        return StringUtils.isEmpty(fileName) ? "" : fileName;
    }

    /***
     * @return {@link ExcelExceptionTypes} that specifies the exception type.
     */
    public ExcelExceptionTypes getExcelType() {
        return this.excelType;
    }

    /***
     * Used to check the arguments passed to
     * {@link ExcelException#ExcelException(ExcelExceptionTypes, CellAddress, Throwable)}
     * and create the message to pass through <code>super</code>.
     * 
     * @param ereType
     *            The {@link ExcelExceptionTypes} that specifies the type of
     *            excel error. Cannot be null.
     * @param cellAddress
     *            The {@link CellAddress} of the cell that caused the error.
     *            Cannot be null.
     * @return {@link String} with the built message to be passed to
     *         <code>super</code>.
     * @throws NullPointerException
     *             when the excelType or cellAddress are null
     */
    private static String buildMessage(ExcelExceptionTypes excelType, CellAddress cellAddress) {
        checkNotNull(excelType, "ExcelExceptionType cannot be null");
        checkNotNull(cellAddress, "CellAddress cannot be null");
        return MessageFormat.format(excelType.getLogMessage(), cellAddress);
    }

    /***
     * Used to check the arguments passed to
     * {@link ExcelException#ExcelException(ExcelExceptionTypes, String, Throwable)}
     * and create the message to pass through <code>super</code>.
     * 
     * @param excelType
     *            {@link ExcelExceptionTypes} cannot be null
     * @param fileName
     *            {@link String} filename of the offending file, cannot be empty
     *            or null
     * @return {@link String} log message of the given exception type
     * @throws NullPointerException
     *             when excelType is null
     */
    private static String buildMessage(ExcelExceptionTypes excelType, String fileName) {
        checkNotNull(excelType, "ExcelExceptionType cannot be null");
        checkArgument(!StringUtils.isEmpty(fileName), "FileName cannot be empty or null");
        return MessageFormat.format(excelType.getLogMessage(), fileName);
    }

}
