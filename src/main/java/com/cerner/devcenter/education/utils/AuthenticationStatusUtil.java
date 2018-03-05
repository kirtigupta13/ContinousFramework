package com.cerner.devcenter.education.utils;

import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

/**
 * AuthenticationStatusUtil will get the current login status
 * 
 * @author AC034492
 * @author Anudeep Kumar Gadam (AG045334)
 */
@Component
public class AuthenticationStatusUtil {

    private static AuthenticationStatusUtil instance = null;
    private static final String PAGERESTRICTEDMESSAGE = "restricted_message";
    private static final String MESSAGE = "message";

    private static ResourceBundle i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

    private AuthenticationStatusUtil() {
    }

    /**
     * Singleton Implementation
     * 
     * @return instance
     */
    public static AuthenticationStatusUtil getInstance() {
        if (instance == null) {
            instance = new AuthenticationStatusUtil();
        }
        return instance;
    }

    /**
     * This function gets the login status of the application which indicates if
     * user is already logged in.
     */
    public boolean isLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth instanceof AnonymousAuthenticationToken ? false : true;
    }

    /**
     * Redirects to access denied page when restricted page is accessed.
     * 
     * @return a {@link ModelAndView} objects that shows access denied page.
     */
    public ModelAndView redirectsAccessDenied() {
        ModelAndView model = new ModelAndView(PAGERESTRICTEDMESSAGE);
        model.addObject(MESSAGE, i18nBundle.getString("common.accessDenied.message"));

        return model;
    }

}
