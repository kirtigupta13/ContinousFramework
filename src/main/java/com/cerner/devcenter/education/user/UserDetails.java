package com.cerner.devcenter.education.user;

import javax.naming.NamingException;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.authentication.LdapReader;

/**
 * Extracts the userDetails from LDAP when a valid userID is provided.
 * 
 * @author Nikhil Agrawal (na044293)
 */
public class UserDetails {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserDetails.class);
    private final String BASE = "OU=Office Locations,DC=northamerica,DC=cerner,DC=net";
    private LdapReader ldapReader;

    public UserDetails() {}

    /**
     * Extracts the user details from the LDAP
     * 
     * @param userID a {@link String} object which will be used to extract user details
     * 
     * @return {@link UserProfileDetails} with details of user with given userID
     * @throws NamingException when LDAP search fails
     * @throws DAOException when userID not found in LDAP
     */
    public UserProfileDetails getUserDetails(String userID) throws NamingException {
        ldapReader = LdapReader.getInstance();
        String error = "Error: User Not Found in database";
        SearchResult searchResult =
                        ldapReader.getSearchResults(ldapReader.getDirContext(), BASE, userID);
        if (searchResult == null) {
            LOGGER.error(error);
            return null;
        }
        UserProfileDetails user = ldapReader.getUserDetails(searchResult.getAttributes());
        return user;
    }

}
