package com.cerner.devcenter.education.managers;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cerner.devcenter.education.admin.CategoryDAO;
import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ResourceTypeDAO;
import com.cerner.devcenter.education.exceptions.ExcelException;
import com.cerner.devcenter.education.exceptions.ExcelExceptionTypes;
import com.cerner.devcenter.education.exceptions.ItemAlreadyExistsException;
import com.cerner.devcenter.education.exceptions.MultiExcelException;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.utils.Constants;
import com.cerner.devcenter.education.utils.MessageHandler;
import com.google.common.net.MediaType;

/***
 * This class is used as a <code>Manager</code> that will act as a
 * <code>Service</code> between the controller classes and the appropriate
 * <code>DAO</code> classes for adding multiple resources.
 * 
 * @author JZ022690
 *
 */
@Service("bulkUploadManager")
public class BulkUploadManager {

    @Autowired
    private CategoryDAO categoryDAO;
    @Autowired
    private ResourceTypeDAO resourceTypeDAO;
    @Autowired
    private ResourceManager resourceManager;

    private static final int NAME_COL = 0;
    private static final int DESC_COL = 1;
    private static final int LINK_COL = 2;
    private static final int TYPE_COL = 3;
    private static final int CATEGORIES_COL = 4;
    private Map<Integer, Integer> resourceDifficultyForCategory = new HashMap<>();

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(MediaType.MICROSOFT_EXCEL.toString(),
            MediaType.OOXML_SHEET.toString());

    private final static Logger LOGGER = LoggerFactory.getLogger(BulkUploadManager.class);

    private static ResourceBundle i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

    /***
     * Wrapper for {@link BulkUploadManager#addExcelData(MultipartFile)}. Reads
     * the {@link Resource} values out of a given file and add them to the
     * database. Log and interpret any {@link ExcelException} into a user
     * message.
     * 
     * @param file
     *            {@link MultipartFile} containing the resource data to be added
     *            to the site. Should be a .xls or .xlsx file
     * @return {@link MessageHandler} containing i18n'd messages for the
     *         warnings, errors, or success
     */
    public MessageHandler addExcelDataSafely(MultipartFile file) {
        MessageHandler messageHandler = new MessageHandler();
        try {
            addExcelData(file);
        } catch (MultiExcelException multiEx) {
            List<ExcelException> exceptions = multiEx.getExceptionList();
            if (!exceptions.isEmpty()) {
                for (ExcelException ex : exceptions) {
                    LOGGER.error("Excel resource exception with cell " + ex.getAddress(), ex);
                    String nextMessage = MessageFormat.format(i18nBundle.getString(ex.getExcelType().getI18NMessage()),
                            ex.getAddress());
                    if (ex.getExcelType() == ExcelExceptionTypes.RESOURCE_EXISTS_ERROR) {
                        messageHandler.addWarning(nextMessage);
                    } else {
                        messageHandler.addError(nextMessage);
                    }
                }
                return messageHandler;
            }
        } catch (ExcelException ex) {
            messageHandler.addError(
                    MessageFormat.format(i18nBundle.getString(ex.getExcelType().getI18NMessage()), ex.getAddress()));
            return messageHandler;
        }
        messageHandler.addSuccess(i18nBundle.getString(Constants.UPLOAD_SUCCESS_I18N));
        return messageHandler;
    }

    /***
     * Read the {@link Resource} values out of a given file and add them to the
     * database.
     * 
     * @param file
     *            {@link MultipartFile} containing the resource data to be added
     *            to the site. Should be a .xls or .xlsx file
     * @throws MultiExcelException
     *             when there are errors with the rows
     * @throws ExcelException
     *             when there is an error with the workbook
     */
    public void addExcelData(MultipartFile file) throws MultiExcelException, ExcelException {
        List<ExcelException> exceptions = new ArrayList<ExcelException>();
        Workbook wb = null;
        wb = createWorkbook(file);
        if (wb == null) {
            throw new ExcelException(ExcelExceptionTypes.FILE_ERROR, file.getOriginalFilename(), null);
        }

        for (Row row : wb.getSheetAt(0)) {
            if (row.getRowNum() == 0) {
                continue;
            }
            try {
                Resource newResource = createResourceFromRow(row);
                resourceManager.addResourceAndRelations(newResource);
            } catch (ExcelException e) {
                exceptions.add(e);
            } catch (ItemAlreadyExistsException e) {
                exceptions.add(new ExcelException(ExcelExceptionTypes.RESOURCE_EXISTS_ERROR,
                        row.getCell(NAME_COL).getAddress(), e));
            }
        }
        if (!exceptions.isEmpty()) {
            throw new MultiExcelException(exceptions);
        }
    }

    /***
     * Extract the data from excel {@link Row} into {@link Resource}
     * 
     * @param row
     *            {@link Row} containing the resource data in the expected
     *            column order. Cannot be null
     * @return {@link Resource} containing the values extracted from the Excel
     *         {@link Row}
     * @throws ExcelException
     *             when there is an error with the data in the {@link Row}
     * @throws NullPointerException
     *             when row is null
     */
    public Resource createResourceFromRow(Row row) throws ExcelException {
        checkNotNull(row, "Row cannot be null");
        Resource newResource = new Resource();
        resourceDifficultyForCategory = new HashMap<>();
        int expectedColumn = 0;
        for (Cell cell : row) {
            int column = cell.getColumnIndex();
            if (column != expectedColumn) {
                throw new ExcelException(ExcelExceptionTypes.SKIPPED_CELL, cell.getAddress(), null);
            }
            switch (column) {
            case NAME_COL:
                newResource.setResourceName(extractCellString(cell));
                break;
            case DESC_COL:
                newResource.setDescription(extractCellString(cell));
                break;
            case LINK_COL:
                try {
                    newResource.setResourceLink(new URL(extractCellString(cell)));
                } catch (MalformedURLException e) {
                    throw new ExcelException(ExcelExceptionTypes.LINK, cell.getAddress(), e);
                }
                break;
            case TYPE_COL:
                try {
                    newResource.setResourceType(resourceTypeDAO.getByName(extractCellString(cell)));
                } catch (DAOException e) {
                    throw new ExcelException(ExcelExceptionTypes.RESOURCE_TYPE, cell.getAddress(), e);
                }
                break;
            case CATEGORIES_COL:
                List<Category> categories = extractCategories(cell);
                for (Category category : categories) {
                    newResource.addCategory(category);
                }
                newResource.setResourceDifficultyForCategory(resourceDifficultyForCategory);
            }
            expectedColumn++;
        }
        return newResource;

    }

    /***
     * Create a workbook from the given {@link MultipartFile}.
     * 
     * @param file
     *            {@link MultipartFile} containing the data to be made into a
     *            {@link Workbook}
     * @return {@link Workbook} created from the file.
     * @throws ExcelException
     *             when it cannot create the workbook from the file.
     */
    private Workbook createWorkbook(MultipartFile file) throws ExcelException {
        if (!ALLOWED_FILE_TYPES.contains(file.getContentType())) {
            throw new ExcelException(ExcelExceptionTypes.FILE_TYPE_ERROR, file.getOriginalFilename(), null);
        }

        try {
            return WorkbookFactory.create(file.getInputStream());
        } catch (EncryptedDocumentException e) {
            throw new ExcelException(ExcelExceptionTypes.ENCRYPTED, file.getOriginalFilename(), e);
        } catch (InvalidFormatException e) {
            throw new ExcelException(ExcelExceptionTypes.FILE_FORMAT_ERROR, file.getOriginalFilename(), e);
        } catch (IOException e) {
            throw new ExcelException(ExcelExceptionTypes.FILE_ERROR, file.getOriginalFilename(), e);
        }
    }

    /***
     * Attempts to extract a {@link String} from the {@link Cell}
     * 
     * @param cell
     *            the {@link Cell} to extract the value from
     * @return {@link String} containing the cell's string value
     * @throws ExcelException
     *             if the cell is not a {@link Cell#CELL_TYPE_STRING}
     */
    private String extractCellString(Cell cell) throws ExcelException {
        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
            return cell.getStringCellValue();
        }
        throw new ExcelException(ExcelExceptionTypes.NOT_A_STRING, cell.getAddress(), null);
    }

    /***
     * Extract a delimited list from a cell using the {@code DELIMITER} constant
     * 
     * @param cell
     *            {@link Cell} that contains the list to be extracted
     * @return {@link String}[ ] containing the values of the list not including
     *         the delimiter.
     * @throws ExcelException
     *             when the cell cannot be read as a string using
     *             {@link BulkUploadManager#extractCellString(Cell)}
     * @throws NullPointerException
     *             when cell is null
     */
    private String[] extractDelimitedList(Cell cell) throws ExcelException {
        checkNotNull(cell, "Cell cannot be null");
        String contents = extractCellString(cell);
        return contents.split(Constants.DELIMITER);
    }

    /***
     * Extract the categories and difficulties from the delimited list within
     * the specified cell. {@code ex: CatA:1;CatB:2;CatC:1;}
     * 
     * @param cell
     *            the cell containing the list of category:difficulty pairs
     * @return {@link List}<{@link Category}> of categories extracted from the
     *         cell. Also sets the local {@code resourceDifficultyForCategory}
     *         map.
     * @throws ExcelException
     *             when there is an error extracting the values from the cell.
     */
    private List<Category> extractCategories(Cell cell) throws ExcelException {
        resourceDifficultyForCategory = new HashMap<>();
        List<Category> categories = new ArrayList<Category>();
        String[] categoryDifficultyPairs = extractDelimitedList(cell);
        for (String pair : categoryDifficultyPairs) {
            Category newCategory = new Category();
            String[] splitPair = pair.split(":");
            if (splitPair.length != 2) {
                throw new ExcelException(ExcelExceptionTypes.CATEGORY_DIFFICULTY_PAIR, cell.getAddress(), null);
            }
            try {
                String categoryName = StringUtils.trim(splitPair[0]);
                if (categoryName.isEmpty()) {
                    throw new ExcelException(ExcelExceptionTypes.CATEGORY_NAME, cell.getAddress(), null);
                }
                newCategory = categoryDAO.getByName(categoryName);
                categories.add(newCategory);
                String categoryDifficulty = StringUtils.trim(splitPair[1]);
                resourceDifficultyForCategory.put(newCategory.getId(), Integer.parseInt(categoryDifficulty));
            } catch (DAOException e) {
                throw new ExcelException(ExcelExceptionTypes.CATEGORY_NAME, cell.getAddress(), e);
            } catch (NumberFormatException e) {
                throw new ExcelException(ExcelExceptionTypes.CATEGORY_DIFFICULTY_NOT_AN_INTEGER, cell.getAddress(), e);
            }
        }
        return categories;
    }
}
