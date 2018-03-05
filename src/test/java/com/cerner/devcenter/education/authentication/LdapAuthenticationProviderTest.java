package com.cerner.devcenter.education.authentication;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * @author AC034492
 * This is the test class for LdapAuthenticationProvider which will return a UsernamePasswordAuthenticationToken when successfully Authenticated
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(LdapAuthenticationProvider.class)
public class LdapAuthenticationProviderTest {

    LdapAuthenticationProvider authProvider = new LdapAuthenticationProvider();
    Properties connectionProperties;

    @Before
    public void setUp() throws Exception {

        connectionProperties = new Properties();
        connectionProperties.setProperty("contextFactory", "com.sun.jndi.ldap.LdapCtxFactory");
        connectionProperties.setProperty("providerUrl", "ldap://ldap.northamerica.cerner.net:389");
        connectionProperties.setProperty("securityAuthentication", "simple");
        connectionProperties.setProperty("securityPrincipal", "WHQ_NT_DOMAIN");
    }
    
    @After
    public void tearDown(){
    	authProvider = null;
    	connectionProperties = null;
    }

    /**
     * Test when invalid credentials are used for login
     * @throws Exception
     */
    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void testAuthenticateWithFailedLogin() throws Exception {

        LdapReader ldapReader = Mockito.mock(LdapReader.class);
        LdapAuthenticator ldapAuth = Mockito.mock(LdapAuthenticator.class);
        UserCredentials user = new UserCredentials("myid", "pwd");

        List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication token = new UsernamePasswordAuthenticationToken("myid", "pwd", grantedAuths);

        Mockito.when(ldapReader.readProperties("connection")).thenReturn(connectionProperties);
        Mockito.when(ldapAuth.authenticateUser(user, connectionProperties)).thenReturn(2);

        System.out.println(authProvider.authenticate(token, user, ldapReader, ldapAuth));

    }

    /**
     * Test when valid credentials are used. It will return a UsernamePasswordAuthenticationToken
     * @throws Exception
     */
    @Test
    public void testAuthenticateWithSuccessfulLogin() throws Exception {

        LdapReader ldapReader = Mockito.mock(LdapReader.class);
        LdapAuthenticator ldapAuth = Mockito.mock(LdapAuthenticator.class);
        UserCredentials user = new UserCredentials("myid", "pwd");

        List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication token = new UsernamePasswordAuthenticationToken("myid", "pwd", grantedAuths);

        Mockito.when(ldapReader.readProperties("connection")).thenReturn(connectionProperties);
        Mockito.when(ldapAuth.authenticateUser(user, connectionProperties)).thenReturn(1);

        assertTrue(authProvider.authenticate(token, user, ldapReader, ldapAuth) instanceof UsernamePasswordAuthenticationToken);

    }


}
