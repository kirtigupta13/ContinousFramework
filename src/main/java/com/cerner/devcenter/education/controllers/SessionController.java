package com.cerner.devcenter.education.controllers;

import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.cerner.devcenter.education.utils.AuthenticationStatusUtil;

/**
 * This controller is for the rendering the page view when /login or /logout is
 * accessed
 *
 * @author James Kellerman (JK042311)
 * @author Surbhi Singh (SS043472)
 * @author JZ022690
 */
@Controller
@RequestMapping("/")
public class SessionController {

    private static final String LOGIN = "login";
    private static final String HOME_REDIRECT = "redirect:/home";
    private static final String LOGIN_ERROR = "com.cerner.devcenter.education.controllers.SessionController.errorLogin";
    private AuthenticationStatusUtil loginState = AuthenticationStatusUtil.getInstance();
    private ResourceBundle i18nBundle;

    /**
     * Displays the login page for users to log in to the application using
     * their LDAP credentials.
     *
     * @param error
     *            This will be set when there is an authentication failure
     * @return a {@link ModelAndView} object with login view
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "error", required = false) String error) {

        /* Get the resource bundle associated with the default locale */
        i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

        /*
         * Check if user is already Logged in and redirect to main page if
         * already authenticated
         */
        if (loginState.isLoggedIn()) {
            return new ModelAndView(HOME_REDIRECT);
        }

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", i18nBundle.getString(LOGIN_ERROR));
        }

        model.setViewName(LOGIN);

        return model;

    }

    /**
     * Displays the logout page and allows for users to log back in to the
     * application using their LDAP credentials.
     *
     * @return This will return a {@link ModelAndView} object with login view
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logout() {

        // Get the resource bundle associated with the default locale
        i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

        loginState = AuthenticationStatusUtil.getInstance();

        /*
         * Check if user is already Logged in and redirect to main page if
         * already authenticated
         */
        if (loginState.isLoggedIn()) {
            return new ModelAndView(HOME_REDIRECT);
        }

        ModelAndView model = new ModelAndView();
        model.addObject("login_message",
                i18nBundle.getString("com.cerner.devcenter.education.controllers.SessionController.logoutMessage"));
        model.setViewName(LOGIN);

        return model;
    }

    /**
     * Displays the login page for users to log in to the application using
     * their LDAP credentials.
     *
     * @param error
     *            This will be set when there is an authentication failure. Can
     *            be null.
     * @return a {@link ModelAndView} object with login view
     */
    @RequestMapping(value = "/login_force", method = RequestMethod.GET)
    public ModelAndView forceLogin(@RequestParam(value = "error", required = false) String error) {

        // Get the resource bundle associated with the default locale
        i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", i18nBundle.getString(LOGIN_ERROR));
        }

        model.setViewName(LOGIN);

        return model;
    }
}
