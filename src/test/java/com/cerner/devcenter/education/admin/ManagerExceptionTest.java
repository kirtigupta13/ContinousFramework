package com.cerner.devcenter.education.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This class is to test the {@link ManagerException} class.
 *
 * @author Piyush Bandil(Pb042879)
 *
 */
public class ManagerExceptionTest {

    private final static String EXCEPTION_MESSAGE = "Exception Message";
    private final static String TEST_EXCEPTION = "Test exception";
    private Exception encapsulatedException;
    private ManagerException testException;

    @Before
    public void setUp() {
        encapsulatedException = new IllegalArgumentException(TEST_EXCEPTION);
    }
    
    @After
    public void tearDown(){
    	encapsulatedException = null;
    	testException= null;
    }

    /**
     * Testing for default {@link ManagerException}
     */
    @Test
    public void testManagerExceptionCreationDefault() {
        testException = new ManagerException();
        assertNotNull(testException);
        assertNull(testException.getMessage());
    }

    /**
     * Testing for default {@link ManagerException} with message and cause
     */
    @Test
    public void testManagerExceptionCreationMessageAndCause() {
        testException = new ManagerException(EXCEPTION_MESSAGE, encapsulatedException);
        assertNotNull(testException);
        assertEquals(EXCEPTION_MESSAGE, testException.getMessage());
        assertEquals(encapsulatedException, testException.getCause());
    }

    /**
     * Testing for default {@link ManagerException} when IsThrowableMessageAndCause
     */
    @Test(expected = ManagerException.class)
    public void testManagerExceptionIsThrowableMessageAndCause() {
        throw new ManagerException(TEST_EXCEPTION, encapsulatedException);
    }
}