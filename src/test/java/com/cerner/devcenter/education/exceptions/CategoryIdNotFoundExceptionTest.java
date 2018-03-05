package com.cerner.devcenter.education.exceptions;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests the {@link CategoryIdNotFoundException} class.
 * 
 * @author Abhi Purella (AP045635)
 * @author Santosh Kumar (SK051343)
 */
public class CategoryIdNotFoundExceptionTest {

    /**
     * This method tests
     * {@link CategoryIdNotFoundException#CategoryIdNotFoundException(String)}
     * and checks if the message is set properly.
     */
    @Test
    public void testCategoryIdNotFoundExceptionMessage() {
        CategoryIdNotFoundException categoryIdNotFoundException = new CategoryIdNotFoundException("Exception message");
        assertEquals("Exception message", categoryIdNotFoundException.getMessage());
    }
}
