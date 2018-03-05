package com.cerner.devcenter.education.helpers;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

/**
 * This class is to test the {@link HttpURLValidator} class.
 *
 * @author NO032013
 *
 */
public class HttpURLValidatorTest {

    /**
     * Tests HttpURLValidator against an expected return value in both the String and URL function
     */
    public void testAsStringAndUrl(boolean expected, String urlAsString) throws MalformedURLException {
        assertEquals(expected, HttpURLValidator.verifyURL(urlAsString));
        assertEquals(expected, HttpURLValidator.verifyURL(new URL(urlAsString)));
    }

    @Test
    public void testHttpURLValidatorIsCorrectHttp() throws MalformedURLException {
        testAsStringAndUrl(true, "http://www.test.com");
    }

    @Test
    public void testHttpURLValidatorIsCorrectHttps() throws MalformedURLException {
        testAsStringAndUrl(true, "https://www.test.com");
    }

    @Test
    public void testHttpURLValidatorInvalidURL() throws MalformedURLException {
        testAsStringAndUrl(false, "https://thiswillnotwork");
    }

    @Test
    public void testHttpURLValidatorInvalidProtocol() throws MalformedURLException {
        testAsStringAndUrl(false, "ftp://www.test.com");
    }

    @Test (expected = MalformedURLException.class)
    public void testHttpURLValidatorStringNotURL() throws MalformedURLException {
        HttpURLValidator.verifyURL("ThisIsNotAURL");
    }

    @Test (expected = NullPointerException.class)
    public void testHttpURLValidatorStringNull() throws MalformedURLException {
        String nullString = null;
        HttpURLValidator.verifyURL(nullString);
    }

    @Test (expected = NullPointerException.class)
    public void testHttpURLValidatorURLNull() {
        URL nullURL = null;
        HttpURLValidator.verifyURL(nullURL);
    }
}
