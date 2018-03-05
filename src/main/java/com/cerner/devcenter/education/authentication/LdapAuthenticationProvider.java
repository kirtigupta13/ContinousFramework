package com.cerner.devcenter.education.authentication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * @author AC034492
 * This class is the customAuthentication Provider for the Spring Security Authentication Manager
 *
 */
@Component
public class LdapAuthenticationProvider implements AuthenticationProvider {

    public static final int LOGIN_SUCCESS = 1;

    /**
     * This method returns a Auth Token when the user is successfully authenticated else a Exception will be thrown
     * {@link org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)}
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationCredentialsNotFoundException {

        UserCredentials user = new UserCredentials(authentication.getName(), authentication.getCredentials().toString());
        return authenticate(authentication, user, LdapReader.getInstance(), new LdapAuthenticator());

    }

    /**
     * Overloaded method for facilitating the testing. This will return an {@link UsernamePasswordAuthenticationToken}
     * @param auth - initial Auth in the Session that holds the UserName
     * @param user - Instance of {@link UserCredentials} holding the user credentials
     * @param ldapReader - Instance of  {@link LdapReader}
     * @param ldapAuth - Instance of {@link LdapAuthenticator}
     * @return {@link Authentication} object
     */
    public Authentication authenticate(Authentication auth, UserCredentials user, LdapReader ldapReader, LdapAuthenticator ldapAuth){
        Properties connectionProperties;
        try {
            connectionProperties = ldapReader.readProperties("connection");
            int status = ldapAuth.authenticateUser(user, connectionProperties);

            if (status == LOGIN_SUCCESS) {
                List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
                grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
                return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), grantedAuths);
            }
            else
                throw new AuthenticationCredentialsNotFoundException("Invalid credentials");

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return auth;
    }

    /**
     * Returns true if this AuthenticationProvider supports the indicated Authentication object.
     * Here the Auth object is {@link UsernamePasswordAuthenticationToken}
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
