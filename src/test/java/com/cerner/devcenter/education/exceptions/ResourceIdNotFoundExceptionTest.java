package com.cerner.devcenter.education.exceptions;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests the {@link ResourceIdNotFoundException} class.
 * 
 * @author Abhi Purella (AP045635)
 */
public class ResourceIdNotFoundExceptionTest {

    /**
     * This method tests
     * {@link ResourceIdNotFoundException#ResourceIdNotFoundException(String)}
     * and checks if the message is set properly.
     */
    @Test
    public void testResourceIdNotFoundExceptionMessage() {
        ResourceIdNotFoundException resourceIdNotFoundException = new ResourceIdNotFoundException("Exception message");
        assertEquals("Exception message", resourceIdNotFoundException.getMessage());
    }
}
