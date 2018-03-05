package com.cerner.devcenter.education.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * This class is to test the {@link DAOException} class.
 *
 * @author NO032013
 *
 */
public class DAOExceptionTest {

    @Test
    public void testDAOExceptionCreationDefault() {
        DAOException testException = new DAOException();
        assertNotNull(testException);
        assertNull(testException.getMessage());
    }

    @Test
    public void testDAOExceptionCreationMessage() {
        DAOException testException = new DAOException("Exception message");
        assertNotNull(testException);
        assertEquals("Exception message", testException.getMessage());
    }

    @Test
    public void testDAOExceptionCreationMessageAndCause() {
        Exception encapsulatedException = new IllegalArgumentException("Test exception");
        DAOException testException = new DAOException("Exception message", encapsulatedException);
        assertNotNull(testException);
        assertEquals("Exception message", testException.getMessage());
        assertEquals(encapsulatedException, testException.getCause());
    }

    @Test
    public void testDAOExceptionCreationCause() {
        Exception encapsulatedException = new IllegalArgumentException("Test exception");
        DAOException testException = new DAOException(encapsulatedException);
        assertNotNull(testException);
        assertEquals(encapsulatedException, testException.getCause());
    }

    @Test
    public void testDAOExceptionCreationAllOptions() {
        Exception encapsulatedException = new IllegalArgumentException("Test exception");
        DAOException testException = new DAOException("Exception message", encapsulatedException, false, false);
        assertNotNull(testException);
        assertEquals("Exception message", testException.getMessage());
        assertEquals(encapsulatedException, testException.getCause());
    }

    @Test(expected = DAOException.class)
    public void testDAOExceptionIsThrowable() throws DAOException {
        throw new DAOException("Test exception");
    }

    @Test(expected = DAOException.class)
    public void testDAOExceptionIsThrowableMessageAndCause() throws DAOException {
        Exception encapsulatedException = new IllegalArgumentException("Test exception");
        throw new DAOException("Test exception", encapsulatedException);
    }
}
