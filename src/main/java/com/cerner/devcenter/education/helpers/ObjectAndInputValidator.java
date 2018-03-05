package com.cerner.devcenter.education.helpers;

import static com.google.common.base.Preconditions.checkArgument;

import com.cerner.devcenter.education.utils.AuthorizationLevel;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

/**
 * Class responsible for validating all the input entered by the admin. The inputs that are
 * validated include the object, name, description, ID, list, date, and campus.
 *
 * @author Justin Kerber
 * @author James Kellerman
 * @author Piyush Bandil (PB042879)
 * @author Surbhi Singh (SS043472)
 */
@Component
public class ObjectAndInputValidator {

    /**
     * Validates the {@link List} object is not <code>null</code> and is not empty.
     * 
     * @param list the {@link List} object to be validated (should not be <code>null</code> or
     *        empty).
     * @throws IllegalArgumentException if the size of the list is 0.
     */
    public static void checkListValidity(List<?> list) {
        checkArgument(list.size() > 0, "List cannot be empty");
    }

    /**
     * Validates that a {@link DateTime} object is within an acceptable range. The date should not
     * be <code>null</code>, should not be in the past or more than 2 years in the future.
     * 
     * @param date the {@link DateTime} to be validated
     * @return true if the date is valid otherwise return false
     */
    public static boolean checkDateValidity(DateTime date) {

        DateTime local = new DateTime();

        // is date more than one day in past?
        if (date.isBefore(local.minusDays(1))) {
            return false;
        }

        // is date more than two years in future?
        if (date.getYear() >= local.getYear() + 2) {
            return false;
        }
        return true;
    }

    /**
     * This function uses the RegEx expression to check whether the name contains only letters.
     *
     * @param withOutNumbers should only contain alphabets
     * @return true when it matches the regex otherwise return false
     */
    public boolean isAlpha(String withOutNumbers) {
        return withOutNumbers.matches("^[a-zA-Z ]+$");
    }

    /**
     * This function uses the RegEx expression to check whether the description contain any alpha
     * numeric characters, just letters and numbers.
     *
     * @param withNumbers withNumbers should contain only alphabets and numbers
     * @return true when it matches the regex otherwise return false
     */
    public boolean isAlphaNum(String withNumbers) {
        return withNumbers.matches("^[a-zA-Z0-9 ]+$");
    }

    /**
     * To check whether the authorization level is valid.
     *
     * @param authorizationLevel
     * @return true if value matches to enum value otherwise false
     */
    public boolean isValidLevel(int authorizationLevel) {
        for (AuthorizationLevel authLevel : AuthorizationLevel.values()) {
            if (authLevel.getLevel() == authorizationLevel) {
                return true;
            }
        }
        return false;
    }

}
