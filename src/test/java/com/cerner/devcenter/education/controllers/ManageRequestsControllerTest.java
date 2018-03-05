package com.cerner.devcenter.education.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * The class tests the functionalities of the {@link ManageRequestsController}
 * class.
 * 
 * @author NR046827
 */
@RunWith(MockitoJUnitRunner.class)
public class ManageRequestsControllerTest {

    private static final String MANAGE_REQUESTS_PAGE = "manage_requests";

    private ManageRequestsController manageRequestsController = new ManageRequestsController();

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = standaloneSetup(manageRequestsController).build();
    }

    /**
     * This tests {@link ResourcesController#showRequestResourcePage(String, HttpSession)}
     */
    @Test
    public void testShowManageResourcePage() throws Exception {
        mockMvc.perform(get("/app/show_manage_requests")).andExpect(view().name(MANAGE_REQUESTS_PAGE));
    }
}
