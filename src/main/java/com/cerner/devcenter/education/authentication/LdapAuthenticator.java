/**
 *
 */
package com.cerner.devcenter.education.authentication;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * @author AC034492 This class implements AuthenticatorService for
 *         authenticating the user credentials with Cerner LDAP server.
 *
 */
public class LdapAuthenticator implements AuthenticatorService {

    @Override
    public int authenticateUser(UserCredentials credentials, Properties connectionProperties) throws IOException {

        if (credentials.getUsername().equals("") || credentials.getPassword().equals("")) {
            return LOGIN_INVALID_ARGS;
        }

        connectionProperties.setProperty(Context.SECURITY_CREDENTIALS, credentials.getPassword());
        connectionProperties.setProperty(Context.SECURITY_PRINCIPAL, connectionProperties.getProperty(Context.SECURITY_PRINCIPAL)
                + "\\" + credentials.getUsername());

        LdapReader ldapReader = LdapReader.getInstance();
        System.out.println(ldapReader);

        try {
            ldapReader.createDirContext(connectionProperties);
            return LOGIN_SUCCESS;
        } catch (NamingException e) {
            return LOGIN_BAD_CREDENTIALS;
        }
    }

}
