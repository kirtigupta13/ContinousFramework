package com.cerner.devcenter.education.exceptions;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import com.cerner.devcenter.education.models.Resource;

/***
 * A container for {@link ExcelException} primarily used when adding
 * {@link Resource} from an Excel file.
 * 
 * @author Jacob Zimmermann (JZ022690)
 *
 */
public class MultiExcelException extends Exception {
    private static final long serialVersionUID = -8478326471626451990L;

    private List<ExcelException> excelExceptionList;

    /***
     * Create a new {@link MultiExcelException} with a starting {@link List} of
     * {@link ExcelException}
     * 
     * @param startingList
     *            {@link List} of {@link ExcelException} to start with. Can be
     *            empty. Cannot be null.
     * @throws IllegalArgumentException
     *             when startingList is null
     */
    public MultiExcelException(List<ExcelException> startingList) {
        checkArgument(startingList != null, "Starting list cannot be null.");
        excelExceptionList = startingList;
    }

    /***
     * @return {@link List} of {@link ExcelException} contained in this object
     */
    public List<ExcelException> getExceptionList() {
        return excelExceptionList;
    }
}
