package com.cerner.devcenter.education.controllers;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.cerner.devcenter.education.managers.BulkUploadManager;
import com.cerner.devcenter.education.managers.UserManager;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthenticationStatusUtil;
import com.cerner.devcenter.education.utils.Constants;
import com.cerner.devcenter.education.utils.MessageHandler;

/***
 * This controller handles requests related to uploading an excel file that
 * contains new resources to be added to the site
 * 
 * @author JZ022690
 *
 */
@Controller
@Qualifier("BulkUploadController")
@RequestMapping("/app")
public class BulkUploadController {

    @Autowired
    private AuthenticationStatusUtil status;
    @Autowired
    private UserManager userManager;
    @Autowired
    private BulkUploadManager bulkUploadManager;

    private static final String UPLOAD_EXCEL = "upload_excel_resources";
    private static final String USER_DETAILS = "userDetails";
    private static final String REDIRECT_LOGIN = "redirect:/login";
    private static final String SUCCESS_MESSAGES = "successMessages";
    private static final String WARNING_MESSAGES = "warningMessages";
    private static final String ERROR_MESSAGES = "errorMessages";
    private static final String MESSAGE = "message";
    private static final String ADMIN_NOT_LOGGED_IN = "common.admin.notLoggedIn";
    private static final Logger LOGGER = Logger.getLogger(BulkUploadController.class);

    private static ResourceBundle i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

    /**
     * 
     * @param session
     *            The current HTTP session of the user (Cannot be null)
     * 
     * @return {@link ModelAndView} object that redirects to one of the below
     *         pages:
     *         <ul>
     *         <li>bulk upload page if user is admin</li>
     *         <li>restricted error message page if user is not admin</li>
     *         <li>general error page if the user session no longer exists</li>
     *         </ul>
     */
    @RequestMapping(value = "/show_bulk_upload", method = RequestMethod.GET)
    public ModelAndView showUploadExcelResources(HttpSession session) {
        checkArgument(session != null, "session cannot be null");

        if (!status.isLoggedIn()) {
            return redirectsNotLoggedIn();
        }
        UserProfileDetails user = (UserProfileDetails) session.getAttribute(USER_DETAILS);
        if (!userManager.isAdminUser(user.getUserId())) {
            return status.redirectsAccessDenied();
        }

        return new ModelAndView(UPLOAD_EXCEL);
    }

    /***
     * 
     * @param file
     *            The {@link MultipartFile} to be uploaded to the site
     * @param request
     *            The {@link HttpServletRequest} containing the file data
     * @return {@link ModelAndView} which will redirect to the show_bulk_upload
     *         page with the appropriate success or error message
     * @throws IOException
     *             caused by an error with reading/writing to the server
     * @throws FileUploadException
     *             caused by an error with the upload process
     */
    @RequestMapping(value = "/upload_file", method = RequestMethod.POST)
    public ModelAndView uploadResources(@RequestParam("file") MultipartFile file, HttpServletRequest request)
            throws IOException {

        if (!status.isLoggedIn()) {
            return redirectsNotLoggedIn();
        }
        ModelAndView model = new ModelAndView(UPLOAD_EXCEL);

        if (!file.isEmpty()) {
            MessageHandler messageHandler = bulkUploadManager.addExcelDataSafely(file);
            model.addObject(SUCCESS_MESSAGES,
                    MessageHandler.buildMessageHTML(messageHandler.getSuccessMessages(), SUCCESS_MESSAGES));
            model.addObject(ERROR_MESSAGES,
                    MessageHandler.buildMessageHTML(messageHandler.getErrorMessages(), ERROR_MESSAGES));
            model.addObject(WARNING_MESSAGES,
                    MessageHandler.buildMessageHTML(messageHandler.getWarningMessages(), WARNING_MESSAGES));
        } else {
            model.addObject(ERROR_MESSAGES, i18nBundle.getString(Constants.UPLOAD_ERROR_I18N));
        }
        return model;

    }

    /**
     * Redirects to the login page when admin is not logged in.
     *
     * @return the model and view that redirects to login jsp page
     */
    private ModelAndView redirectsNotLoggedIn() {
        ModelAndView model = new ModelAndView(REDIRECT_LOGIN);
        LOGGER.error("Admin not logged in");
        model.addObject(MESSAGE, i18nBundle.getString(ADMIN_NOT_LOGGED_IN));
        return model;
    }
}
