package com.cerner.devcenter.education.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.cerner.devcenter.education.user.UserProfileDetails;

/**
 * @author AC034492 This is test class for LdapReader Class.
 */

public class LdapReaderTest {


    public LdapReader ldapReader;
    public Properties expected;
    public static final String SEARCH_BASE = "OU=Office Locations,DC=northamerica,DC=cerner,DC=net";

    @Before
    public void setUp() throws Exception {

        expected = new Properties();
        expected.setProperty("contextFactory", "com.sun.jndi.ldap.LdapCtxFactory");
        expected.setProperty("providerUrl", "ldap://ldap.northamerica.cerner.net:389");
        expected.setProperty("securityAuthentication", "simple");
        expected.setProperty("securityPrincipal", "WHQ_NT_DOMAIN");
        ldapReader = LdapReader.getInstance();

    }

    @After
    public void tearDown() {
        ldapReader = null;
        expected = null;
    }

    /**
     * Test for getDirContext method
     * 
     * @throws Exception
     */
    @Test
    public void getDirContextTest() throws Exception {

        InitialDirContext dir = new InitialDirContext();
        ldapReader.createDirContext(expected);
        assertFalse(dir.equals(ldapReader.getDirContext()));

    }

    /**
     * Test case when the input file is valid
     * 
     * @throws Exception
     */
    @Test
    public void readPropertiesTestWithValidFile() throws Exception {

        Properties prop = ldapReader.readProperties("connection");
        assertEquals("com.sun.jndi.ldap.LdapCtxFactory",
                        prop.getProperty(Context.INITIAL_CONTEXT_FACTORY));
    }

    /**
     * Test case when the input file is invalid, Expected to throw an Exception
     * 
     * @throws Exception
     */
    @Test(expected = IOException.class)
    public void readPropertiesTestWithInvalidFile() throws Exception {
        ldapReader.readProperties("abc");
    }

    /**
     * Tests for readProperties to check if the output contains proper connection parameters
     * 
     * @throws Exception
     */
    @Test
    public void readPropertiesTestforSecurityPrincipal() throws Exception {

        Properties output = ldapReader.readProperties("connection");
        assertEquals(expected.getProperty("securityPrincipal"),
                        output.getProperty(Context.SECURITY_PRINCIPAL));

    }

    @Test
    public void readPropertiesTestForContextFactory() throws Exception {

        Properties output = ldapReader.readProperties("connection");
        assertEquals(expected.getProperty("contextFactory"),
                        output.getProperty(Context.INITIAL_CONTEXT_FACTORY));

    }

    @Test
    public void readPropertiesTestForProviderUrl() throws Exception {

        Properties output = ldapReader.readProperties("connection");
        assertEquals(expected.getProperty("providerUrl"), output.getProperty(Context.PROVIDER_URL));
    }

    @Test
    public void readPropertiesTestForSecurityAuthentication() throws Exception {

        Properties output = ldapReader.readProperties("connection");
        assertEquals(expected.getProperty("securityAuthentication"),
                        output.getProperty(Context.SECURITY_AUTHENTICATION));
    }

    /**
     * Test cases for getSearchResults method
     * 
     * @throws NamingException
     */
    @Test
    public void getSearchResultsTest() throws NamingException {

        DirContext dir = Mockito.mock(DirContext.class);
        NamingEnumeration<SearchResult> results = Mockito.mock(NamingEnumeration.class);
        SearchResult searchResult = Mockito.mock(SearchResult.class);

        Mockito.when(dir.search(Mockito.any(String.class), Mockito.any(String.class),
                        Mockito.any(SearchControls.class))).thenReturn(results);
        Mockito.when(results.hasMoreElements()).thenReturn(true);
        Mockito.when(results.nextElement()).thenReturn(searchResult);

        assertEquals(searchResult, ldapReader.getSearchResults(dir, SEARCH_BASE, "testId"));

    }

    @Test(expected = NamingException.class)
    public void getSearchResultsTestWhenDirContextIsNull() throws NamingException {

        ldapReader.getSearchResults(null, SEARCH_BASE, "testId");

    }

    @Test(expected = NamingException.class)
    public void getSearchResultsTestWhenUserIdIsNull() throws NamingException {

        DirContext dir = new InitialDirContext();
        ldapReader.getSearchResults(dir, SEARCH_BASE, "");

    }

    @Test(expected = NamingException.class)
    public void getSearchResultsTestWhenSearchBaseIsNull() throws NamingException {

        DirContext dir = new InitialDirContext();
        ldapReader.getSearchResults(dir, "", "");

    }

    /**
     * Test for getUserDetails method
     * 
     * @throws NamingException
     */
    @Test
    public void getUserDetailsTest() throws NamingException {

        Attributes attributes = new BasicAttributes();
        attributes.put(new BasicAttribute("cn", "Amar,Doe"));
        attributes.put(new BasicAttribute("title", "SoftWare engineer"));
        attributes.put(new BasicAttribute("sAMAccountName", "AC034492"));
        attributes.put(new BasicAttribute("mail", "amar.cherukuri@cerner.com"));
        attributes.put(new BasicAttribute("department", "Dev Center"));
        attributes.put(new BasicAttribute("extensionAttribute6", "Manager"));
        attributes.put(new BasicAttribute("extensionAttribute11", "Project"));

        UserProfileDetails user = ldapReader.getUserDetails(attributes);

        assertEquals("Amar,Doe", user.getName());

    }

}
