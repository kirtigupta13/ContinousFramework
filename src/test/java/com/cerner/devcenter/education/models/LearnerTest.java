package com.cerner.devcenter.education.models;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link Learner} class.
 *
 * @author Mani Teja Kurapati (MK051340)
 */
public class LearnerTest {

    private static final String VALID_USER_ID = "MK051340";
    private static final String VALID_EMAIL_ID = "firstname.lastname@company.com";

    private Learner learner;
    private Learner testLearner;

    @Before
    public void setUp() {
        learner = new Learner();
    }

    /***
     * Test {@link Learner#Learner(String)} will throw an
     * {@link IllegalArgumentException} when given a null user id.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testOneArgLearnerConstructor_NullUserId() {
        testLearner = new Learner(null);
    }

    /***
     * Test {@link Learner#Learner(String)} will throw an
     * {@link IllegalArgumentException} when given a blank user id.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testOneArgLearnerConstructor_BlankUserId() {
        testLearner = new Learner("");
    }

    /***
     * Test {@link Learner#Learner(String)} will throw an
     * {@link IllegalArgumentException} when given a empty user id.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testOneArgLearnerConstructor_EmptyUserId() {
        testLearner = new Learner(" ");
    }

    /***
     * Test {@link Learner#Learner(String)} when valid parameters are passed.
     */
    @Test
    public void testOneArgLearnerConstructor_ValidParameters() {
        testLearner = new Learner(VALID_USER_ID);
        assertEquals(VALID_USER_ID, testLearner.getUserId());
    }

    /***
     * Test {@link Learner#Learner(String, String)} will throw an
     * {@link IllegalArgumentException} when given a null user id.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testTwoArgLearnerConstructor_NullUserId() {
        testLearner = new Learner(null, VALID_EMAIL_ID);
    }

    /***
     * Test {@link Learner#Learner(String, String)} will throw an
     * {@link IllegalArgumentException} when given a blank user id.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testTwoArgLearnerConstructor_BlankUserId() {
        testLearner = new Learner("", VALID_EMAIL_ID);
    }

    /***
     * Test {@link Learner#Learner(String, String)} will throw an
     * {@link IllegalArgumentException} when given a empty user id.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testTwoArgLearnerConstructor_EmptyUserId() {
        testLearner = new Learner(" ", VALID_EMAIL_ID);
    }

    /***
     * Test {@link Learner#Learner(String, String)} will throw an
     * {@link IllegalArgumentException} when given a null email id.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testTwoArgLearnerConstructor_NullEmailId() {
        testLearner = new Learner(VALID_USER_ID, null);
    }

    /***
     * Test {@link Learner#Learner(String, String)} will throw an
     * {@link IllegalArgumentException} when given a blank email id.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testTwoArgLearnerConstructor_BlankEmailId() {
        testLearner = new Learner(VALID_USER_ID, " ");
    }

    /***
     * Test {@link Learner#Learner(String, String)} will throw an
     * {@link IllegalArgumentException} when given a empty email id.
     *
     * @throws IllegalArgumentException
     *             to pass the test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testTwoArgLearnerConstructor_EmptyEmailId() {
        testLearner = new Learner(VALID_USER_ID, "");
    }

    /***
     * Test {@link Learner#Learner(String, String)} when valid parameters are
     * passed.
     */
    @Test
    public void testTwoArgLearnerConstructor_ValidParameters() {
        testLearner = new Learner(VALID_USER_ID, VALID_EMAIL_ID);
        assertEquals(VALID_USER_ID, testLearner.getUserId());
        assertEquals(VALID_EMAIL_ID, testLearner.getEmailId());
    }

    /**
     * Expects the userId of the learner when {@link Learner#getUserId()} is
     * called.
     */
    @Test
    public void testGetUserId() {
        assertEquals(null, learner.getUserId());
    }

    /**
     * Expects {@link Learner#setUserId(String)} to throw
     * {@link IllegalArgumentException} when userId is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetUserIdWithNullUserId() {
        learner.setUserId(null);
    }

    /**
     * Expects {@link Learner#setUserId(String)} to throw
     * {@link IllegalArgumentException} when userId is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetUserId_EmptyUserId() {
        learner.setUserId("");
    }

    /**
     * Expects {@link Learner#setUserId(String)} to throw
     * {@link IllegalArgumentException} when userId is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetUserId_BlankUserId() {
        learner.setUserId(" ");
    }

    /**
     * Expects the given valid userId to be set for {@link Learner} when passed
     * to {@link Learner#setUserId(String)}.
     */
    @Test
    public void testSetUserId() {
        learner.setUserId(VALID_USER_ID);
        assertEquals(VALID_USER_ID, learner.getUserId());
    }

    /**
     * Expects the email Id of the learner when {@link Learner#getEmailId()} is
     * called.
     */
    @Test
    public void testGetEmailId() {
        assertEquals(null, learner.getEmailId());
    }

    /**
     * Expects {@link Learner#setEmailId(String)} to throw
     * {@link IllegalArgumentException} when emailId is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetEmailIdWithNullUserId() {
        learner.setEmailId(null);
    }

    /**
     * Expects {@link Learner#setEmailId(String)} to throw
     * {@link IllegalArgumentException} when emailId is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetEmailIdWithEmptyUserId() {
        learner.setEmailId("");
    }

    /**
     * Expects {@link Learner#setEmailId(String)} to throw
     * {@link IllegalArgumentException} when emailId is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetEmailIdWithBlankUserId() {
        learner.setEmailId(" ");
    }

    /**
     * Expects the given valid emailId to be set for {@link Learner} when passed
     * to {@link Learner#setEmailId(String)}.
     */
    @Test
    public void testSetEmailId() {
        learner.setEmailId(VALID_EMAIL_ID);
        assertEquals(VALID_EMAIL_ID, learner.getEmailId());
    }
}