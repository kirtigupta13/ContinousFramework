package com.cerner.devcenter.education.authentication;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author AC034492
 * This is the test class for UserCredentials class
 *
 */
public class UserCredentialsTest {
	
	private UserCredentials userCredentials = new UserCredentials("myid", "mypassword");

	/**
	 * Test the getUsername method
	 */
	@Test
	public void getUsernameTest() {
		assertEquals("myid", userCredentials.getUsername());
	}
	
	/**
	 * Test the getPassword method
	 */
	@Test
	public void getPasswordTest() {
		assertEquals("mypassword", userCredentials.getPassword());
	}

}
