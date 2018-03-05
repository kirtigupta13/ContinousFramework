package com.cerner.devcenter.education.authentication;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;

import java.util.Properties;

import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;


/**
 * @author AC034492
 * Test class for LdapAuthenticator class
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(LdapAuthenticator.class)
public class LdapAuthenticatorTest {

    AuthenticatorService _authenticator;
    public static Properties connectionProperties;

    /**
     * create an object for LdapAuthenticator
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        _authenticator = new LdapAuthenticator();

        connectionProperties = new Properties();
        connectionProperties.setProperty("contextFactory", "com.sun.jndi.ldap.LdapCtxFactory");
        connectionProperties.setProperty("providerUrl", "ldap://ldap.northamerica.cerner.net:389");
        connectionProperties.setProperty("securityAuthentication", "simple");
        connectionProperties.setProperty("securityPrincipal", "WHQ_NT_DOMAIN");
    }

    /**
     * Test cases for authenticateUser method
     * Mocked the {@link LdapReader} to test the return for valid and invalid scenarios
     * @throws Exception
     */
    @Test
    public void authenticateUserTestForInvalidCredentials() throws Exception {

        LdapReader ldapreader = createMock(LdapReader.class);
        Whitebox.setInternalState(LdapReader.class, "instance", ldapreader);
        expect(ldapreader.getDirContext()).andThrow(new NamingException());
        expect(ldapreader.createDirContext(connectionProperties)).andThrow(new NamingException());
        replayAll(ldapreader);
        assertEquals(2, _authenticator.authenticateUser(new UserCredentials("name", "********"), connectionProperties));

    }

    @Test
    public void authenticateUserTestForValidCredentials() throws Exception {

        LdapReader ldapreader = createMock(LdapReader.class);
        Whitebox.setInternalState(LdapReader.class, "instance", ldapreader);
        expect(ldapreader.createDirContext(connectionProperties)).andReturn(new InitialDirContext());
        replayAll(ldapreader);
        assertEquals(1, _authenticator.authenticateUser(new UserCredentials("AC034492", "********"), connectionProperties));

    }

    /**
     * @throws Exception
     * Test cases for authenticateUser with Empty Strings
     */
    @Test
    public void authenticateUserTestForEmptyArgs() throws Exception {

        assertEquals(3, _authenticator.authenticateUser(new UserCredentials("", ""), connectionProperties));
    }

    @Test
    public void authenticateUserTestForEmptyUserName() throws Exception {

        assertEquals(3, _authenticator.authenticateUser(new UserCredentials("", "******"), connectionProperties));
    }

    @Test
    public void authenticateUserTestForEmptyPassword() throws Exception {

        assertEquals(3, _authenticator.authenticateUser(new UserCredentials("AC034492", ""), connectionProperties));
    }

}
