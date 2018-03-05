/**
 *
 */
package com.cerner.devcenter.education.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * @author AC034492 Test class for Login Controller
 * @author JZ022690
 */

@RunWith(MockitoJUnitRunner.class)
public class SessionControllerTest {

    @InjectMocks
    private SessionController controller;
    private MockMvc mockMvc;
    private AnonymousAuthenticationToken token;
    private List<GrantedAuthority> grantedAuths;

    @Before
    public void setUp() {

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/");
        viewResolver.setSuffix(".jsp");
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setViewResolvers(viewResolver).build();

        grantedAuths = new ArrayList<GrantedAuthority>();
        grantedAuths.add(new SimpleGrantedAuthority("dummy"));
        token = new AnonymousAuthenticationToken("abc", "abc", grantedAuths);

    }

    /**
     * Test for when the user opens login page
     *
     * @throws Exception
     */
    @Test
    public void testLoginWhenLoggingIn() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(token);

        mockMvc.perform(get("/login").principal(token)).andExpect(view().name("login"))
                .andExpect(forwardedUrl("/login.jsp"));
    }

    /**
     * Test when user /login?error path is called
     *
     * @throws Exception
     */
    @Test
    public void testLoginWhenInvalidCredentials() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(token);

        mockMvc.perform(get("/login").principal(token).param("error", "error")).andExpect(view().name("login"))
                .andExpect(model().attributeExists("error")).andExpect(forwardedUrl("/login.jsp"));
    }

    /**
     * Test when /login is tried to access when user is already logged in.
     *
     * @throws Exception
     */
    @Test
    public void testLoginWhenAlreadyLoggedIn() throws Exception {

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("", "", grantedAuths));

        System.out.println("With in controller:"
                + (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken));

        mockMvc.perform(get("/login").principal(token)).andExpect(view().name("redirect:/home"));
    }

    /**
     * Test when user clicks log out in the application. /logout path will be
     * called
     *
     * @throws Exception
     */
    @Test
    public void testLogout() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(token);

        mockMvc.perform(get("/logout").principal(token)).andExpect(view().name("login"))
                .andExpect(model().attributeExists("login_message")).andExpect(forwardedUrl("/login.jsp"));
    }

    /**
     * Test when /logout is tried to be accessed directly when user is logged
     * in.
     *
     * @throws Exception
     */
    @Test
    public void testLogoutWhenAlreadyLoggedIn() throws Exception {

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("", "", grantedAuths));

        mockMvc.perform(get("/logout").principal(token)).andExpect(view().name("redirect:/home"));
    }

    /***
     * Test logging in through login_force
     *
     * @throws Exception
     *             when something goes wrong
     */
    @Test
    public void testForceLogin() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(token);

        mockMvc.perform(get("/login_force").principal(token)).andExpect(view().name("login"))
                .andExpect(forwardedUrl("/login.jsp"));
    }
}
