package com.cerner.devcenter.education.models;

import com.google.common.base.Preconditions;

/**
 * A wrapper class to contain the user selected values (category and resource
 * type by their ids) from display resources page to filter the search results.
 * 
 * @author Gunjan Kaphle (GK045931)
 */
public class CategoryResourceForm {

    private static final String CATEGORY_ID_ERROR_MESSAGE = "Category ID should be greater than zero";
    private static final String RESOURCE_TYPE_ID_ERROR_MESSAGE = "Resource Type ID should be greater than zero";
    public int selectedCategoryID;
    public int selectedResourceTypeID;

    /**
     * Default constructor
     */
    public CategoryResourceForm() {
    }

    /**
     * Constructor for CategoryResourceForm that stores the user selected
     * category and resource type to filter results in show resources page.
     * Resource Type ID can be 0 if user selected "ANY" to see all types of
     * resources.
     * 
     * @param selectedCategoryID
     *            an {@link Integer} that stores the selected category id.
     * @param selectedResourceTypeID
     *            an {@link Integer} that stores the selected resource type id.
     * @throws {link
     *             {@link IllegalArgumentException} when the category id and
     *             resource type id is not greater than zero.
     */
    public CategoryResourceForm(final int selectedCategoryID, final int selectedResourceTypeID) {
        checkParametersValidity(selectedCategoryID, selectedResourceTypeID);
        this.selectedCategoryID = selectedCategoryID;
        this.selectedResourceTypeID = selectedResourceTypeID;
    }

    /**
     * Getter to retrieve the selected category id.
     * 
     * @return an {@link Integer} that is the selected category id
     */
    public int getSelectedCategoryID() {
        return selectedCategoryID;
    }

    /**
     * Setter to set the selected category type id by the user.
     * 
     * @param selectedCategoryID
     *            an {@link Integer} that is the category id
     * @throws {link
     *             {@link IllegalArgumentException} when the category id is less
     *             than or equal to zero.
     */
    public void setSelectedCategoryID(final int selectedCategoryID) {
        Preconditions.checkArgument(selectedCategoryID > 0, CATEGORY_ID_ERROR_MESSAGE);
        this.selectedCategoryID = selectedCategoryID;
    }

    /**
     * Getter to retrieve the selected resource type id.
     * 
     * @return an {@link Integer} that is the selected resource type id
     */
    public int getSelectedResourceTypeID() {
        return selectedResourceTypeID;
    }

    /**
     * Setter to set the selected resource type id by the user.
     * 
     * @param selectedResourceTypeID
     *            an {@link Integer} that is the resource type id. Cannot be
     *            less that zero. Zero means all resources.
     * @throws {link
     *             {@link IllegalArgumentException} when the resource type id is
     *             less than or equal to zero.
     */
    public void setSelectedResourceTypeID(final int selectedResourceTypeID) {
        Preconditions.checkArgument(selectedResourceTypeID >= 0, RESOURCE_TYPE_ID_ERROR_MESSAGE);
        this.selectedResourceTypeID = selectedResourceTypeID;
    }

    /**
     * Checks if the parameters passed in are valid or not.
     * 
     * @param selectedCategoryID
     *            an {@link Integer}, that is the user selected category id.
     *            Cannot be less than 1
     * @param selectedResourceTypeID
     *            an {@link Integer}, that is the user selected resource type id
     *            Cannot be less than 0
     * @throws IllegalArgumentException
     *             if the value for either category id or resource type id is
     *             invalid
     */
    public void checkParametersValidity(final int selectedCategoryID, final int selectedResourceTypeID) {
        Preconditions.checkArgument(selectedCategoryID > 0, CATEGORY_ID_ERROR_MESSAGE);
        Preconditions.checkArgument(selectedResourceTypeID >= 0, RESOURCE_TYPE_ID_ERROR_MESSAGE);
    }
}
