package com.cerner.devcenter.education.authentication;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.cerner.devcenter.education.user.UserProfileDetails;

/**
 * @author AC034492
 * This class contains methods for creating, processing the LDAP directoryContext and getting the
 * UserDetails from the LDAP directory.
 *
 */
public class LdapReader {

    private static LdapReader instance = null;
    private DirContext dirContext;
    private LdapReader() { }

    /**
     * Singleton Implementation
     * @return {@link LdapReader} instance.
     */
    public static LdapReader getInstance() {
        if (instance == null) {
            instance = new LdapReader();
        }
        return instance;
    }


    /**
     * Takes the connectionProperties from {@link LdapAuthenticator#authenticateUser} as input and
     * creates a LDAP directoryContext if the credentials are valid
     * @param connectionProperties
     * @return - directory context created
     * @throws NamingException
     */
    public DirContext createDirContext(Properties connectionProperties) throws NamingException{
        dirContext = new InitialDirContext(connectionProperties);
        return dirContext;
    }

    /**
     * Getter Method for the DirectoryContext
     * @return - DirContext
     * @throws NamingException
     */
    public DirContext getDirContext() throws NamingException {
        return dirContext;
    }

    /**
     * Reads the properties file and returns the connection properties.
     * {@link Properties}
     * @param fileName - Name of the properties file
     * @return this will return connection properties for the server
     * @throws IOException
     */
    public Properties readProperties(String fileName) throws IOException {

        Properties input = new Properties();
        Properties connectionProperties = new Properties();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName + ".properties");

        if(inputStream == null){
            throw new FileNotFoundException(fileName + ".properties file not found");
        }

        input.load(inputStream);

        // set the LDAP connection Properties
        connectionProperties.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                input.getProperty("contextFactory"));
        connectionProperties.setProperty(Context.PROVIDER_URL,
                input.getProperty("providerUrl"));
        connectionProperties.setProperty(Context.SECURITY_AUTHENTICATION,
                input.getProperty("securityAuthentication"));
        connectionProperties.setProperty(Context.SECURITY_PRINCIPAL,
                input.getProperty("securityPrincipal"));

        return connectionProperties;
    }


    /**
     * This method will search the LDAP directory with the UserID provided and returns a {@link SearchResult} which holds the
     * User information
     * @param dir LDAP directory context created after Authentication
     * @param ldapSearchBase SearchBase where the user need to be searched
     * @param UserId UserId of whose details need to be searched in LDAP
     * @return This will return an object of type {@link SearchResult} which holds the User information
     * @throws NamingException
     */
    public SearchResult getSearchResults(DirContext dir, String ldapSearchBase, String UserId) throws NamingException {

        SearchResult searchResult = null;

        if(dir == null || UserId == null || ldapSearchBase == null)
            throw new NamingException("Not Authenticated");

        String searchFilter = "(&(objectClass=user)(sAMAccountName=" + UserId + "))";

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        NamingEnumeration<SearchResult> results = dir.search(ldapSearchBase, searchFilter, searchControls);

        if(results.hasMoreElements()) {
            searchResult = results.nextElement();
        }

        return searchResult;
    }

    /**
     * Parse the {@link Attributes} object for User Profile Information
     * 
     * @param attributes - Holds the User information
     * @return {@link UserProfileDetails} object
     * @throws NamingException
     */
    public UserProfileDetails getUserDetails(Attributes attributes) throws NamingException{

        String Name = attributes.get("cn").get().toString();
        String Role = attributes.get("title").get().toString();
        String UserId = attributes.get("sAMAccountName").get().toString();
        String Email = attributes.get("mail").get().toString();
        String Department = attributes.get("department").get().toString();
        String Manager = attributes.get("extensionAttribute6").get().toString();
        String Project = attributes.get("extensionAttribute11").get().toString();

        return new UserProfileDetails(Name, Role, UserId, Email, Department, Manager, Project);

    }

}
