package com.cerner.devcenter.education.exceptions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Class that tests the {@link DuplicateUserInterestedCategoryException} class.
 * 
 * @author Gunjan Kaphle (GK045931)
 * @author Santosh Kumar (SK051343)
 */
public class DuplicateUserInterestedCategoryExceptionTest {

    /**
     * Tests {@link DuplicateUserInterestedCategoryException} to find if the
     * exception throws the correct message or not.
     */
    @Test
    public void testDuplicateUserInterestedCategoryMessage() {
        DuplicateUserInterestedCategoryException duplicateUserInterestedCategoryException = new DuplicateUserInterestedCategoryException(
                "The category has already been added for the user.");
        assertEquals("The category has already been added for the user.", duplicateUserInterestedCategoryException.getMessage());
    }
}
