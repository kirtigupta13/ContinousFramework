package com.cerner.devcenter.education.helpers;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.validator.routines.UrlValidator;

/**
 * This class is responsible for checking that a URL or String representation of
 * a URL is valid and has a valid protocol.
 *
 * @author NO032013
 * @author Justin Kerber
 * 
 */
public class HttpURLValidator {
	private static final String[] PROTOCOLS = { "http", "https" };
	private static final UrlValidator URL_VALIDATOR = new UrlValidator(PROTOCOLS);

	/**
	 * Ensures that a <code>string</code> representation of a URL is valid and
	 * has a valid protocol.
	 * 
	 * @param urlAsString
	 *            <code>string</code> representation of a URL that is to be
	 *            validated, a null value is considered invalid
	 * @return <code>Boolean.true</code> if the URL is valid and has protocol
	 *         http or https
	 * @throws MalformedURLException
	 *             when the URL passed in is not a valid URL
	 * @throws NullPointerException
	 *             when the URL passed in is null
	 */
	public static boolean verifyURL(String urlAsString) throws MalformedURLException {
		checkNotNull(urlAsString);
		return (verifyURL(new URL(urlAsString)));
	}

	/**
	 * Ensures that a {@link URL} object representation of a url is valid and
	 * has a valid protocol.
	 * 
	 * @param url
	 *            a {@link URL} object that is to be validated, a null value is
	 *            considered invalid
	 * @return <code>Boolean.true</code> if the URL is valid and has protocol
	 *         http or https
	 * @throws NullPointerException
	 *             when the URL passed in is null
	 */
	public static boolean verifyURL(URL url) {
		checkNotNull(url);
		return (URL_VALIDATOR.isValid(url.toString()));
	}

	/**
	 * 
	 * Private constructor for the {@link HttpURLValidator} util class.
	 */
	private HttpURLValidator() {
		throw new UnsupportedOperationException("HttpURLValidator cannot be instantiated");
	}
}