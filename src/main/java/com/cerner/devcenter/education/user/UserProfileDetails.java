package com.cerner.devcenter.education.user;

import java.util.ArrayList;
import java.util.List;

import com.cerner.devcenter.education.models.UserInterestedCategory;

/**
 * @author AC034492 UserProfileDetails holds the details of the user read from
 *         the LDAP after authentication.
 *
 */
public class UserProfileDetails {

    private String fullName;
    private String firstName;
    private String lastName;
    private String role;
    private String userId;
    private String email;
    private String department;
    private String manager;
    private String project;
    private List<UserInterestedCategory> userInterestedCategory = new ArrayList<>();

    /**
     * @param name
     *            - name of the User
     * @param role
     *            - Role of the User
     * @param userId
     *            - UserId used for login
     * @param email
     *            - Email of the User
     * @param department
     *            - Department / team of the user
     * @param manager
     *            - Manager for the User
     * @param project
     *            - Project the user is on
     */
    public UserProfileDetails(String name, String role, String userId, String email, String department, String manager,
            String project) {
        this.fullName = name;
        this.role = role;
        this.userId = userId;
        this.email = email;
        this.department = department;
        this.manager = manager;
        this.project = project;
        setFirstAndLastName(name);
    }

    /**
     * @return - returns the name of the User (LastName, FirstName)
     */
    public String getName() {
        return this.fullName;
    }

    /**
     * @return - returns the name of the User
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * @return - returns the name of the User
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Splits full name into First and Last name
     */
    private void setFirstAndLastName(String name) {
        String nameSplitter[] = name.split(",");
        this.firstName = nameSplitter[1];
        this.lastName = nameSplitter[0];
    }

    /**
     * @return - Role of the User, Ex: Software Engineer, Software Architect
     */
    public String getRole() {
        return this.role;
    }

    /**
     * @return - UserId that is used to login
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * @return - email of the User
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * @return - Department / Team of the user
     */
    public String getDepartment() {
        return this.department;
    }

    /**
     * @return - Manager of the User
     */
    public String getManager() {
        return this.manager;
    }

    /**
     * @return returns project the user is on
     */
    public String getProject() {
        return this.project;
    }

    /**
     * Getter method that returns the list of user interested categories.
     * 
     * @return a {@link List} of {@link UserInterestedCategory}. Can be null or
     *         empty
     */
    public List<UserInterestedCategory> getUserInterestedCategories() {
        return userInterestedCategory;
    }

    /**
     * Setter method that sets the list of user interested categories.
     * 
     * @param userInterestedCategory
     *            a {@link List} of {@link UserInterestedCategory}. Can be null or
     *            empty
     */
    public void setUserInterestedCategories(List<UserInterestedCategory> userInterestedCategory) {
        this.userInterestedCategory = userInterestedCategory;
    }
}
