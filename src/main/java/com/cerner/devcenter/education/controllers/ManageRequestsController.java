package com.cerner.devcenter.education.controllers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * This class defines the controller that handles requests related to managing
 * the requests by administrator.
 * 
 * @author NR046827
 */
@Controller
@Qualifier("ManageRequestsController")
@RequestMapping("/app")
public class ManageRequestsController {

    private static final String MANAGE_REQUESTS_PAGE = "manage_requests";

    /**
     * Handles the request for retrieving the manage request page.
     * 
     * @return Returns the Spring {@link ModelAndView} object with the view name
     *         of the JSP page that is to be loaded
     */
    @RequestMapping(value = "/show_manage_requests", method = RequestMethod.GET)
    public ModelAndView requestForResource() {
        return new ModelAndView(MANAGE_REQUESTS_PAGE);
    }
}
