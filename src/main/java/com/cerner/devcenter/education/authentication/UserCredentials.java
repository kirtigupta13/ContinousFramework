package com.cerner.devcenter.education.authentication;

/**
 * @author AC034492
 * User credentials holds the User Name and Password of the user
 */
public class UserCredentials {
	
	private String Username;
	private String Password;
	
	public UserCredentials(String Username, String Password){
		this.Username = Username;
		this.Password = Password;
	}

	/**
	 * @return this will return the User Name of the user
	 */
	public String getUsername() {
		return this.Username;
	}
	
	/**
	 * @return this will return the password of the user
	 */
	public String getPassword() {
		return this.Password;
	}	

}
