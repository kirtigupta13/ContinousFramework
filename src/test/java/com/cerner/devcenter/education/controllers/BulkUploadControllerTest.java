package com.cerner.devcenter.education.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

import com.cerner.devcenter.education.managers.BulkUploadManager;
import com.cerner.devcenter.education.managers.UserManager;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthenticationStatusUtil;
import com.cerner.devcenter.education.utils.MessageHandler;

/***
 * Tests the functionality of {@link BulkUploadController}
 * 
 * @author JZ022690
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class BulkUploadControllerTest {

    @InjectMocks
    private BulkUploadController bulkUploadController = new BulkUploadController();
    @Mock
    private UserProfileDetails userInfo;
    @Mock
    private AuthenticationStatusUtil status;
    @Mock
    private UserManager userManager;
    @Mock
    private HttpSession session;
    @Mock
    private MultipartFile mpFile;
    @Mock
    private BulkUploadManager bulkUploadManager;
    @Mock
    private HttpServletRequest request;

    private static final String USER_DETAILS = "userDetails";
    private static final String LOGIN_REDIRECT = "redirect:/login";
    private static final String UPLOAD_EXCEL = "upload_excel_resources";
    private static final String SUCCESS_MESSAGES = "successMessages";
    private static final String WARNING_MESSAGES = "warningMessages";
    private static final String ERROR_MESSAGES = "errorMessages";
    private static final String ADMIN_REDIRECT = "restricted_message";
    private static final String UPLOAD_ERROR_MESSAGE = "bulkUpload.errorMessage";
    private static final String UPLOAD_SUCCESS_MESSAGE = "bulkUpload.successMessage";
    private static final String TEST_MESSAGE = "test message";
    private MessageHandler messageHandler;

    private static ResourceBundle i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

    @Before
    public void setUp() {
        messageHandler = new MessageHandler();
        when(status.isLoggedIn()).thenReturn(true);
        userInfo = new UserProfileDetails("test first, test last", "ADMIN", "test", "test@cerner.com", "Dev Academy",
                "test manager", "Education Evaluation");
        when(session.getAttribute(USER_DETAILS)).thenReturn(userInfo);
        when(userManager.isAdminUser(userInfo.getUserId())).thenReturn(true);
    }

    /**
     * This test
     * {@link BulkUploadController#showUploadExcelResources(HttpSession)},
     * validates bulk upload page when admin is not logged in.
     */
    @Test
    public void testUploadFileWhenNotLoggedIn() {
        when(status.isLoggedIn()).thenReturn(false);
        assertEquals(LOGIN_REDIRECT, bulkUploadController.showUploadExcelResources(session).getViewName());
    }

    /**
     * This test
     * {@link BulkUploadController#showUploadExcelResources(HttpSession)},
     * validates bulk upload page when admin is logged in.
     */
    @Test
    public void testShowUploadExcelWhenIsAdmin() {
        when(status.redirectsAccessDenied()).thenCallRealMethod();
        assertEquals(UPLOAD_EXCEL, bulkUploadController.showUploadExcelResources(session).getViewName());
    }

    /**
     * This test
     * {@link BulkUploadController#showUploadExcelResources(HttpSession)},
     * validates bulk upload page when non-admin user is logged in.
     */
    @Test
    public void testShowUploadExcelWhenNotAdmin() {
        when(userManager.isAdminUser(userInfo.getUserId())).thenReturn(false);
        when(status.redirectsAccessDenied()).thenCallRealMethod();
        assertEquals(ADMIN_REDIRECT, bulkUploadController.showUploadExcelResources(session).getViewName());
    }

    /**
     * This test
     * {@link BulkUploadController#uploadResources(MultipartFile, HttpServletRequest)},
     * validates that an error message appears when the file is empty
     */
    @Test
    public void testUploadResourcesWithEmptyFile() throws IOException {
        when(mpFile.isEmpty()).thenReturn(true);
        assertEquals(i18nBundle.getString(UPLOAD_ERROR_MESSAGE),
                bulkUploadController.uploadResources(mpFile, request).getModel().get(ERROR_MESSAGES));
    }

    /***
     * Test that
     * {@link BulkUploadController#uploadResources(MultipartFile, HttpServletRequest)}
     * displays the correct error messages.
     */
    @Test
    public void testUploadResourcesWithErrors() throws IOException {
        messageHandler.addError(TEST_MESSAGE);
        when(bulkUploadManager.addExcelDataSafely(mpFile)).thenReturn(messageHandler);
        assertEquals(MessageHandler.buildMessageHTML(messageHandler.getErrorMessages(), ERROR_MESSAGES),
                bulkUploadController.uploadResources(mpFile, request).getModel().get(ERROR_MESSAGES));
    }

    /***
     * Test that
     * {@link BulkUploadController#uploadResources(MultipartFile, HttpServletRequest)}
     * displays the correct warning messages.
     */
    @Test
    public void testUploadResourcesWithWarnings() throws IOException {
        messageHandler.addWarning(TEST_MESSAGE);
        when(bulkUploadManager.addExcelDataSafely(mpFile)).thenReturn(messageHandler);
        assertEquals(MessageHandler.buildMessageHTML(messageHandler.getWarningMessages(), WARNING_MESSAGES),
                bulkUploadController.uploadResources(mpFile, request).getModel().get(WARNING_MESSAGES));
    }

    /***
     * Test that
     * {@link BulkUploadController#uploadResources(MultipartFile, HttpServletRequest)}
     * displays the correct error, warning, and success messages.
     */
    @Test
    public void testUploadResourcesWithMixedMessages() throws IOException {
        messageHandler.addWarning(TEST_MESSAGE);
        messageHandler.addError(i18nBundle.getString(UPLOAD_ERROR_MESSAGE));
        messageHandler.addSuccess(i18nBundle.getString(UPLOAD_SUCCESS_MESSAGE));
        when(bulkUploadManager.addExcelDataSafely(mpFile)).thenReturn(messageHandler);
        assertEquals(MessageHandler.buildMessageHTML(messageHandler.getWarningMessages(), WARNING_MESSAGES),
                bulkUploadController.uploadResources(mpFile, request).getModel().get(WARNING_MESSAGES));
        assertEquals(MessageHandler.buildMessageHTML(messageHandler.getErrorMessages(), ERROR_MESSAGES),
                bulkUploadController.uploadResources(mpFile, request).getModel().get(ERROR_MESSAGES));
        assertEquals(MessageHandler.buildMessageHTML(messageHandler.getSuccessMessages(), SUCCESS_MESSAGES),
                bulkUploadController.uploadResources(mpFile, request).getModel().get(SUCCESS_MESSAGES));
    }

    /**
     * This test
     * {@link BulkUploadController#uploadResources(MultipartFile, HttpServletRequest)},
     * validates that a success message appears when the file is not empty
     */
    @Test
    public void testUploadResourcesSuccess() throws IOException {
        when(mpFile.isEmpty()).thenReturn(false);
        messageHandler.addSuccess(i18nBundle.getString(UPLOAD_SUCCESS_MESSAGE));
        when(bulkUploadManager.addExcelDataSafely(mpFile)).thenReturn(messageHandler);
        assertEquals(MessageHandler.buildMessageHTML(messageHandler.getSuccessMessages(), SUCCESS_MESSAGES),
                bulkUploadController.uploadResources(mpFile, request).getModel().get(SUCCESS_MESSAGES));
    }

    /**
     * This test
     * {@link BulkUploadController#uploadResources(MultipartFile, HttpServletRequest)},
     * validates that user redirects to login page if not logged in
     */
    @Test
    public void testUploadResourcesNotLoggedIn() throws IOException {
        when(status.isLoggedIn()).thenReturn(false);
        assertEquals(LOGIN_REDIRECT, bulkUploadController.uploadResources(mpFile, request).getViewName());
    }
}
