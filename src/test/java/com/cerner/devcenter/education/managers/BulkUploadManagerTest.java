package com.cerner.devcenter.education.managers;

import static com.cerner.devcenter.education.utils.MessageHandlerTestUtil.assertMessageHandlers;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellAddress;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
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
import com.cerner.devcenter.education.models.ResourceType;
import com.cerner.devcenter.education.utils.ExcelExceptionMatcher;
import com.cerner.devcenter.education.utils.MessageHandler;
import com.cerner.devcenter.education.utils.MultiExcelExceptionMatcher;
import com.google.common.net.MediaType;

/***
 * Test the functionalities of {@link BulkUploadManager}
 * 
 * @author JZ022690
 */
@RunWith(MockitoJUnitRunner.class)
public class BulkUploadManagerTest {

    @InjectMocks
    private BulkUploadManager bulkUploadManager;
    @Mock
    private ResourceManager resourceManager;
    @Mock
    private ResourceTypeDAO resourceTypeDAO;
    @Mock
    private CategoryDAO categoryDAO;
    @Mock
    private MultipartFile testFile;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Resource testResource;
    private Category testCategory;
    private ResourceType testType;
    private Row testRow;
    private List<ExcelException> rowExceptions = new ArrayList<ExcelException>(
            Arrays.asList(new ExcelException(ExcelExceptionTypes.NOT_A_STRING, new CellAddress("A3"), null),
                    new ExcelException(ExcelExceptionTypes.LINK, new CellAddress("C4"),
                            new MalformedURLException("no protocol: not a URL")),
                    new ExcelException(ExcelExceptionTypes.CATEGORY_DIFFICULTY_PAIR, new CellAddress("E8"), null),
                    new ExcelException(ExcelExceptionTypes.SKIPPED_CELL, new CellAddress("F5"), null),
                    new ExcelException(ExcelExceptionTypes.SKIPPED_CELL, new CellAddress("E6"), null),
                    new ExcelException(ExcelExceptionTypes.CATEGORY_DIFFICULTY_PAIR, new CellAddress("E7"), null),
                    new ExcelException(ExcelExceptionTypes.CATEGORY_DIFFICULTY_NOT_AN_INTEGER, new CellAddress("E9"),
                            new NumberFormatException())));
    private MessageHandler messageHandler;
    private Map<Integer, Integer> testDifficulties = new HashMap<>();

    private static final String UPLOAD_SUCCESS_I18N = "bulkUpload.successMessage";
    private static final int VALID_CATEGORY_ID = 1;
    private static final String VALID_CATEGORY_NAME = "Software";
    private static final String VALID_CATEGORY_DESCRIPTION = "development";
    private static final int VALID_CATEGORY_DIFFICULTY = 1;
    private static final int VALID_TYPE_ID = 1;
    private static final String VALID_TYPE_NAME = "Youtube";
    private static final int VALID_RESOURCE_ID = 1;
    private static final String VALID_RESOURCE_NAME = "Valid Upload Resource";
    private static final String VALID_RESOURCE_DESCRIPTION = "Test upload description";
    private static final String VALID_LINK_URL = "http://127.0.0.1";
    private static final String TEST_FILENAME = "test filename";
    private static final String TEST_CELL = "A1";
    private static final String RESOURCE_ALREADY_EXISTS_CELL = "A2";
    private static final String EXCEPTION_MESSAGE = "message";
    private static final String NULL_ROW_ERROR = "Row cannot be null";
    private static final String INVALID_FILE_TYPE = "NOT XLS";

    private static final MediaType XLS_MEDIA_TYPE = MediaType.MICROSOFT_EXCEL;
    private static final MediaType XLSX_MEDIA_TYPE = MediaType.OOXML_SHEET;

    private static final String TEST_EXCEL_LOCATION = "src/test/resources/Test Resources.xlsx";
    private static final String TEST_EXCEL_ROWS_LOCATION = "src/test/resources/Test Rows.xlsx";

    private static ResourceBundle i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());
    private static URL validLink;
    private static Workbook wbRows;

    @Before
    public void setUp() throws EncryptedDocumentException, InvalidFormatException, FileNotFoundException, IOException,
            DAOException, ItemAlreadyExistsException {
        messageHandler = new MessageHandler();
        testCategory = new Category(VALID_CATEGORY_ID, VALID_CATEGORY_NAME, VALID_CATEGORY_DESCRIPTION);
        testType = new ResourceType(VALID_TYPE_ID, VALID_TYPE_NAME);
        validLink = new URL(VALID_LINK_URL);

        testDifficulties.put(VALID_CATEGORY_ID, VALID_CATEGORY_DIFFICULTY);

        testResource = new Resource(VALID_RESOURCE_ID, validLink, VALID_RESOURCE_DESCRIPTION, testType);
        testResource.addCategory(testCategory);
        testResource.setResourceDifficultyForCategory(testDifficulties);

        wbRows = WorkbookFactory.create(new FileInputStream(TEST_EXCEL_ROWS_LOCATION));

        when(categoryDAO.getByName(anyString())).thenReturn(testCategory);
        when(resourceTypeDAO.getByName(VALID_TYPE_NAME)).thenReturn(testType);
        when(testFile.getContentType()).thenReturn(XLSX_MEDIA_TYPE.toString());
        when(testFile.getInputStream()).thenReturn(new FileInputStream(TEST_EXCEL_LOCATION));
        when(testFile.getOriginalFilename()).thenReturn(TEST_FILENAME);
        doNothing().when(resourceManager).addResourceAndRelations(any(Resource.class));
    }

    /***
     * Test {@link BulkUploadManager#addExcelDataSafely(MultipartFile)} returns
     * a success message when there are no errors adding from the file
     */
    @Test
    public void testAddExcelDataSafely_NoExceptions() throws MultiExcelException, ExcelException {
        BulkUploadManager spyManager = spy(bulkUploadManager);
        doNothing().when(spyManager).addExcelData(testFile);
        messageHandler.addSuccess(i18nBundle.getString(UPLOAD_SUCCESS_I18N));
        assertMessageHandlers(messageHandler, spyManager.addExcelDataSafely(testFile));
    }

    /***
     * Test {@link BulkUploadManager#addExcelDataSafely(MultipartFile)} returns
     * 1 error message when only 1 error occurs while adding from the file
     */
    @Test
    public void testAddExcelDataSafely_OneException() throws MultiExcelException, ExcelException {
        BulkUploadManager spyManager = spy(bulkUploadManager);
        List<ExcelException> exceptions = new ArrayList<ExcelException>();
        ExcelException testException = new ExcelException(ExcelExceptionTypes.CATEGORY, new CellAddress(TEST_CELL),
                null);
        exceptions.add(testException);
        String expected = MessageFormat.format(i18nBundle.getString(testException.getExcelType().getI18NMessage()),
                testException.getAddress());
        doThrow(new MultiExcelException(exceptions)).when(spyManager).addExcelData(testFile);
        messageHandler.addError(expected);
        assertMessageHandlers(messageHandler, spyManager.addExcelDataSafely(testFile));
    }

    /***
     * Test {@link BulkUploadManager#addExcelDataSafely(MultipartFile)} returns
     * 4 error messages when there are 4 errors while adding from the file
     */
    @Test
    public void testAddExcelDataSafely_FourExceptions() throws MultiExcelException, ExcelException {
        BulkUploadManager spyManager = spy(bulkUploadManager);
        List<ExcelException> exceptions = new ArrayList<ExcelException>();
        String i18nMessage = MessageFormat.format(i18nBundle.getString(ExcelExceptionTypes.CATEGORY.getI18NMessage()),
                TEST_CELL);
        for (int i = 0; i < 4; i++) {
            messageHandler.addError(i18nMessage);
            exceptions.add(new ExcelException(ExcelExceptionTypes.CATEGORY, new CellAddress(TEST_CELL), null));
        }
        doThrow(new MultiExcelException(exceptions)).when(spyManager).addExcelData(testFile);
        assertMessageHandlers(messageHandler, spyManager.addExcelDataSafely(testFile));
    }

    /***
     * Test {@link BulkUploadManager#addExcelDataSafely(MultipartFile)} catches
     * a single {@link ExcelException} thrown when adding resources from file
     */
    @Test
    public void testAddExcelDataSafely_CatchesExcelException() throws MultiExcelException, ExcelException {
        BulkUploadManager spyManager = spy(bulkUploadManager);
        ExcelException expectedExcelException = new ExcelException(ExcelExceptionTypes.FILE_TYPE_ERROR,
                new CellAddress(TEST_CELL), null);
        doThrow(expectedExcelException).when(spyManager).addExcelData(testFile);
        messageHandler.addError(
                MessageFormat.format(i18nBundle.getString(expectedExcelException.getExcelType().getI18NMessage()),
                        expectedExcelException.getAddress()));
        assertMessageHandlers(messageHandler, spyManager.addExcelDataSafely(testFile));
    }

    /***
     * Test {@link BulkUploadManager#addExcelDataSafely(MultipartFile)}
     * distinguishes {@link ExcelException} of the type
     * {@link ExcelExceptionTypes#RESOURCE_EXISTS_ERROR} as warnings.
     */
    @Test
    public void testAddExcelDataSafely_ResourceExistsWarning() throws MultiExcelException, ExcelException {
        BulkUploadManager spyManager = spy(bulkUploadManager);
        ExcelException expectedExcelException = new ExcelException(ExcelExceptionTypes.RESOURCE_EXISTS_ERROR,
                new CellAddress(TEST_CELL), null);
        doThrow(new MultiExcelException(Arrays.asList(expectedExcelException))).when(spyManager).addExcelData(testFile);
        messageHandler.addWarning(
                MessageFormat.format(i18nBundle.getString(expectedExcelException.getExcelType().getI18NMessage()),
                        expectedExcelException.getAddress()));
        assertMessageHandlers(messageHandler, spyManager.addExcelDataSafely(testFile));
    }

    /***
     * Test {@link BulkUploadManager#addExcelDataSafely(MultipartFile)} will
     * return a messageHandler with both error and warning messages.
     */
    @Test
    public void testAddExcelDataSafely_MixedErrors() throws MultiExcelException, ExcelException {
        BulkUploadManager spyManager = spy(bulkUploadManager);
        ExcelException resourceExistsException = new ExcelException(ExcelExceptionTypes.RESOURCE_EXISTS_ERROR,
                new CellAddress(TEST_CELL), null);
        ExcelException rowErrorException = new ExcelException(ExcelExceptionTypes.SKIPPED_CELL,
                new CellAddress(TEST_CELL), null);
        String rowErrorMessage = MessageFormat.format(
                i18nBundle.getString(rowErrorException.getExcelType().getI18NMessage()),
                rowErrorException.getAddress());
        String resourceExistsMessage = MessageFormat.format(
                i18nBundle.getString(resourceExistsException.getExcelType().getI18NMessage()),
                resourceExistsException.getAddress());
        doThrow(new MultiExcelException(
                Arrays.asList(resourceExistsException, rowErrorException, resourceExistsException, rowErrorException)))
                        .when(spyManager).addExcelData(testFile);
        messageHandler.addWarning(resourceExistsMessage);
        messageHandler.addWarning(resourceExistsMessage);
        messageHandler.addError(rowErrorMessage);
        messageHandler.addError(rowErrorMessage);
        assertMessageHandlers(messageHandler, spyManager.addExcelDataSafely(testFile));
    }

    /***
     * Tests {@link BulkUploadManager#addExcelData(MultipartFile)} creates a
     * {@link List} of {@link ExcelException} that are caught from creating a
     * {@link Resource} from a bad {@link Row}
     */
    @Test
    public void testAddExcelData_ThrowsMultiExcelException() throws ExcelException, FileNotFoundException, IOException,
            EncryptedDocumentException, InvalidFormatException, MultiExcelException {
        expectedException.expect(MultiExcelException.class);
        expectedException.expect(new MultiExcelExceptionMatcher(rowExceptions));
        when(testFile.getInputStream()).thenReturn(new FileInputStream(TEST_EXCEL_ROWS_LOCATION));
        bulkUploadManager.addExcelData(testFile);
    }

    /***
     * Tests {@link BulkUploadManager#addExcelData(MultipartFile)} creates an
     * {@link ExcelException} from a {@link ItemAlreadyExistsException} that is
     * thrown when trying to add a {@link Resource} that already exists
     */
    @Test
    public void testAddExcelData_ItemAlreadyExistsException()
            throws ItemAlreadyExistsException, MultiExcelException, ExcelException {
        expectedException.expect(MultiExcelException.class);
        ItemAlreadyExistsException thrownException = new ItemAlreadyExistsException(EXCEPTION_MESSAGE);
        ExcelException expected = new ExcelException(ExcelExceptionTypes.RESOURCE_EXISTS_ERROR,
                new CellAddress(RESOURCE_ALREADY_EXISTS_CELL), thrownException);
        expectedException.expect(new MultiExcelExceptionMatcher(Arrays.asList(expected)));
        doThrow(thrownException).when(resourceManager).addResourceAndRelations(any(Resource.class));
        bulkUploadManager.addExcelData(testFile);
    }

    /***
     * Verify the happy path of
     * {@link BulkUploadManager#addExcelData(MultipartFile)} when given an xlsx
     * file type
     */
    @Test
    public void testAddExcelData_DataXlsxType() throws MultiExcelException, ExcelException {
        bulkUploadManager.addExcelData(testFile);
    }

    /***
     * Verify the happy path of
     * {@link BulkUploadManager#addExcelData(MultipartFile)} when given an xls
     * file type
     */
    @Test
    public void testAddExcelData_DataXlsType() throws MultiExcelException, ExcelException {
        when(testFile.getContentType()).thenReturn(XLS_MEDIA_TYPE.toString());
        bulkUploadManager.addExcelData(testFile);
    }

    /***
     * Verify the error when
     * {@link BulkUploadManager#addExcelData(MultipartFile)} is given a non
     * XLS/XLSX file type
     */
    @Test
    public void testAddExcelData_DataInvalidFileType() throws MultiExcelException, ExcelException {
        expectedException.expect(ExcelException.class);
        expectedException.expect(new ExcelExceptionMatcher(ExcelExceptionTypes.FILE_TYPE_ERROR));
        when(testFile.getContentType()).thenReturn(INVALID_FILE_TYPE);
        bulkUploadManager.addExcelData(testFile);
    }

    /**
     * Tests the error return of
     * {@link BulkUploadManager#addExcelData(MultipartFile)} when the file
     * throws an {@link EncryptedDocumentException}
     */
    @Test
    public void testAddExcelData_DataEncrypted() throws IOException, MultiExcelException, ExcelException {
        expectedException.expect(ExcelException.class);
        expectedException.expect(new ExcelExceptionMatcher(ExcelExceptionTypes.ENCRYPTED));
        when(testFile.getInputStream()).thenThrow(new EncryptedDocumentException(EXCEPTION_MESSAGE));
        bulkUploadManager.addExcelData(testFile);
    }

    /**
     * Tests the error return of
     * {@link BulkUploadManager#addExcelData(MultipartFile)} when the file
     * throws an {@link IOException}
     */
    @Test
    public void testAddExcelData_IOException() throws IOException, MultiExcelException, ExcelException {
        expectedException.expect(ExcelException.class);
        expectedException.expect(new ExcelExceptionMatcher(ExcelExceptionTypes.FILE_ERROR));
        when(testFile.getInputStream()).thenThrow(new IOException(EXCEPTION_MESSAGE));
        bulkUploadManager.addExcelData(testFile);
    }

    /***
     * Test that {@link BulkUploadManager#createResourceFromRow(Row)} will
     * error with a null row.
     */
    @Test
    public void testCreateResourceFromRow_NullRow() throws ExcelException {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(NULL_ROW_ERROR);
        testRow = null;
        bulkUploadManager.createResourceFromRow(testRow);
    }

    /***
     * Test the happy path for
     * {@link BulkUploadManager#createResourceFromRow(Row)}
     */
    @Test
    public void testCreateResourceFromRow_Success() throws ExcelException {
        testRow = wbRows.getSheetAt(0).getRow(1);
        Resource actual = bulkUploadManager.createResourceFromRow(testRow);
        assertEquals(VALID_RESOURCE_NAME, actual.getResourceName());
        assertEquals(VALID_RESOURCE_DESCRIPTION, actual.getDescription());
        assertEquals(validLink, actual.getResourceLink());
        assertEquals(testType, actual.getResourceType());
        assertEquals(testCategory, actual.getCategories().get(0));
        assertEquals(testDifficulties, actual.getResourceDifficultyForCategory());
    }

    /***
     * Tests the private extractCellString function through
     * {@link BulkUploadManager#createResourceFromRow(Row)} with the test
     * resource name that is a numeric value. Should throw an
     * {@link ExcelException}.
     */
    @Test
    public void testCreateResourceFromRow_StringExtraction() throws ExcelException {
        expectedException.expect(ExcelException.class);
        expectedException.expect(new ExcelExceptionMatcher(ExcelExceptionTypes.NOT_A_STRING));
        testRow = wbRows.getSheetAt(0).getRow(2);
        bulkUploadManager.createResourceFromRow(testRow);
    }

    /***
     * Test creating a {@link URL} from an invalid link throws an
     * {@link ExcelException}
     */
    @Test
    public void testCreateResourceFromRow_InvalidLink() throws ExcelException {
        expectedException.expect(ExcelException.class);
        expectedException.expect(new ExcelExceptionMatcher(ExcelExceptionTypes.LINK));
        testRow = wbRows.getSheetAt(0).getRow(3);
        bulkUploadManager.createResourceFromRow(testRow);
    }

    /***
     * Tests catching a {@link DAOException} when given an invalid Resource Type
     */
    @Test
    public void testCreateResourceFromRow_InvalidResourceType() throws DAOException, ExcelException {
        expectedException.expect(ExcelException.class);
        expectedException.expect(new ExcelExceptionMatcher(ExcelExceptionTypes.RESOURCE_TYPE));
        when(resourceTypeDAO.getByName(VALID_TYPE_NAME)).thenThrow(new DAOException());
        testRow = wbRows.getSheetAt(0).getRow(1);
        bulkUploadManager.createResourceFromRow(testRow);
    }

    /***
     * Tests catching a {@link DAOException} when given an invalid Category
     */
    @Test
    public void testCreateResourceFromRow_InvalidCategory() throws ExcelException, DAOException {
        expectedException.expect(ExcelException.class);
        expectedException.expect(new ExcelExceptionMatcher(ExcelExceptionTypes.CATEGORY_NAME));
        when(categoryDAO.getByName(VALID_CATEGORY_NAME)).thenThrow(new DAOException());
        testRow = wbRows.getSheetAt(0).getRow(1);
        bulkUploadManager.createResourceFromRow(testRow);
    }

    /***
     * Test throwing an {@link ExcelException} when required cells are skipped
     */
    @Test
    public void testCreateResourceFromRow_SkippedCell() throws ExcelException {
        expectedException.expect(ExcelException.class);
        expectedException.expect(new ExcelExceptionMatcher(ExcelExceptionTypes.SKIPPED_CELL));
        testRow = wbRows.getSheetAt(0).getRow(5);
        bulkUploadManager.createResourceFromRow(testRow);
    }

    /***
     * Test throwing an {@link ExcelException} when the category difficulty pair
     * is less than 2
     */
    @Test
    public void testCreateResourceFromRow_DifficultyPairOver2() throws ExcelException {
        expectedException.expect(ExcelException.class);
        expectedException.expect(new ExcelExceptionMatcher(ExcelExceptionTypes.CATEGORY_DIFFICULTY_PAIR));
        testRow = wbRows.getSheetAt(0).getRow(7);
        bulkUploadManager.createResourceFromRow(testRow);
    }

    /***
     * Test throwing an {@link ExcelException} when the category difficulty pair
     * is more than 2
     */
    @Test
    public void testCreateResourceFromRow_DifficultyPairUnder2() throws ExcelException {
        expectedException.expect(ExcelException.class);
        expectedException.expect(new ExcelExceptionMatcher(ExcelExceptionTypes.CATEGORY_DIFFICULTY_PAIR));
        testRow = wbRows.getSheetAt(0).getRow(7);
        bulkUploadManager.createResourceFromRow(testRow);
    }

    /***
     * Test throwing an {@link ExcelException} when the category difficulty is
     * not a number
     */
    @Test
    public void testCreateResourceFromRow_CategoryDifficultyNotNumber() throws ExcelException {
        expectedException.expect(ExcelException.class);
        expectedException.expect(new ExcelExceptionMatcher(ExcelExceptionTypes.CATEGORY_DIFFICULTY_NOT_AN_INTEGER));
        testRow = wbRows.getSheetAt(0).getRow(8);
        bulkUploadManager.createResourceFromRow(testRow);
    }
}
