package com.cerner.devcenter.education.authentication;

import java.io.IOException;
import java.util.Properties;

/**
 * @author AC034492
 * Implement this interface for authenticating the user.
 */

public interface AuthenticatorService {

    /**
     * Return values form the service
     */

    public static final int LOGIN_SUCCESS = 1;
    public static final int LOGIN_BAD_CREDENTIALS = 2;
    public static final int LOGIN_INVALID_ARGS = 3;


    /**
     * This method validates the user credentials against the server and returns a result status
     * @param credentials
     * Holds the user name and password of the user who is requesting the authentication
     * @return
     * The return type is a integer validation code , each code representing particular authentication status
     * 1 - success
     * 2 - Invalid credentials
     * 3 - any other validation error
     * @throws IOException
     *
     */
    public int authenticateUser(UserCredentials credentials, Properties connectionProperties) throws IOException;

}
