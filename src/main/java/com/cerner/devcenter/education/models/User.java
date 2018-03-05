package com.cerner.devcenter.education.models;

import static com.google.common.base.Preconditions.checkArgument;

import java.text.MessageFormat;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cerner.devcenter.education.helpers.ObjectAndInputValidator;
import com.cerner.devcenter.education.utils.AuthorizationLevel;

/**
 * Represents the User which contains userID along with authorizationLevel. It
 * uses {@link ObjectAndInputValidator} to validate the inputs.
 * 
 * @author Surbhi Singh (SS043472)
 * @author Manoj Raj Devalla (MD042936)
 * @author Navya Rangeneni (NR046827)
 */
public final class User {

    private static final String USERID_ERROR_MESSAGE = "Cannot initialze a User with null/empty/blank User ID";
    private static final String ERROR_MESSAGE = "Error: {0} should not be null/empty/blank";
    private static final String ERROR_MESSAGE_SPECIAL_CHARACTERS = "Error: Invalid {0} parameter: {1}";
    private static final String ERROR_MESSAGE_NUMBERS = "Error: Invalid {0} parameter: {1} ";
    private static final ObjectAndInputValidator validator = new ObjectAndInputValidator();
    private String userID;
    private AuthorizationLevel authorizationLevel;
    private boolean isValidUserID;
    private String firstName;
    private String lastName;
    private String role;
    private String email;
    private int authLevel;

    public User() {
    }

    /**
     * Constructs a User instance with the given userID and authorizationLevel
     * 
     * @param userID
     *            The userID of the user. Must be alphanumeric, cannot be null
     *            or empty.
     * @param authorizationLevel
     *            The authorization level assigned to user, should be numeric
     *            value.
     */
    public User(String userID, AuthorizationLevel authorizationLevel) {
        validateAlphaNum(userID, "userID");
        validateNumeric(authorizationLevel.getLevel(), "authorizationLevel");
        this.userID = userID;
        this.authorizationLevel = authorizationLevel;
    }

    /**
     * Constructs a User instance with the given user object of type Map<String,
     * Object>
     * 
     * @param user
     */

    public User(Map<String, Object> user) {
        validateAlphaNum(userID, "userID");
        this.userID = (String) user.get("user_id");
        this.firstName = (String) user.get("first_name");
        this.lastName = (String) user.get("last_name");
        this.role = (String) user.get("role");
        this.email = (String) user.get("email_id");
        Integer temp = (Integer) user.get("auth_level");
        if (temp >= 0) {
            this.authLevel = temp;
        }
    }

    /**
     * Constructs a User instance with the given userID and authorizationLevel
     * 
     * @param userID
     *            The userID of the user. Must be alphanumeric, cannot be null
     *            or empty.
     * @param authorizationLevel
     *            The authorization level assigned to user, should be numeric
     *            value.
     * @param firstName
     *            First Name of the user.
     * @param lastName
     *            Last Name of the user.
     * @param email
     *            User email.
     * @param role
     *            User role.
     */
    public User(String userID, AuthorizationLevel authorizationLevel, String firstName, String lastName, String email,
            String role) {
        checkArgument(StringUtils.isNotBlank(userID), USERID_ERROR_MESSAGE);
        this.isValidUserID = validateAlphaNum(userID, "userID");

        if (this.isValidUserID) {
            this.userID = userID;
        }

        if (authorizationLevel != null) {
            validateNumeric(authorizationLevel.getLevel(), "authorizationLevel");
        }

        this.authorizationLevel = authorizationLevel;
        validateAlpha(firstName, "firstName");
        this.firstName = firstName;
        validateAlpha(lastName, "lastName");
        this.lastName = lastName;
        this.email = email;
        validateAlpha(role, "role");
        this.role = role;
    }

    /**
     * @return Boolean: true if User ID is a valid one else false
     */
    public boolean isUserIDValid() {
        return isValidUserID;
    }

    /**
     * @return {@link String} with userID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * @return {@link AuthorizationLevel} with authorizationLevel
     */
    public AuthorizationLevel getAuthorizationLevel() {
        return authorizationLevel;
    }

    /**
     * @return {@link String} with role
     */
    public String getRole() {
        return role;
    }

    /**
     * @return {@link String} with firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return {@link String} with lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @return {@link String} with email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Validates field for AlphaNumeric characters.
     * 
     * @param field
     * @param fieldName
     *            The name of the field under test
     * 
     * @throws IllegalArgumentException
     */
    private boolean validateAlphaNum(String field, String fieldName) {
        if (field == null) {
            return false;
        }
        return validator.isAlphaNum(field);
    }

    /**
     * Validates field for Number.
     * 
     * @param authorizationLevel
     * @param string
     *            The name of the field under test
     * 
     * @throws IllegalArgumentException
     */
    private void validateNumeric(int field, String fieldValue) {
        checkArgument(validator.isValidLevel(field), MessageFormat.format(ERROR_MESSAGE_NUMBERS, fieldValue, field));
    }

    /**
     * @return {@link String} with authorization level
     */
    public int getAuthLevel() {
        return authLevel;
    }

    /**
     * Validates field for Alphabetic characters.
     * 
     * @param field
     * @param fieldName
     *            The name of the field under test
     * 
     * @throws IllegalArumentException
     */
    private void validateAlpha(String field, String fieldName) {
        checkArgument(StringUtils.isNotBlank(field), MessageFormat.format(ERROR_MESSAGE, fieldName));
        checkArgument(validator.isAlpha(field),
                MessageFormat.format(ERROR_MESSAGE_SPECIAL_CHARACTERS, fieldName, field));
    }
}
