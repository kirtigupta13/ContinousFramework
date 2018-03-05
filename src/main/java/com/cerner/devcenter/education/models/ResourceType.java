package com.cerner.devcenter.education.models;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.commons.lang3.StringUtils;

/**
 * This class represents the Resource Type that contains a unique ID, and a
 * resource name. A resource type can be a youtube video, and ebook, a online
 * classroom training, a webinar, and many more.
 * 
 * @author Gunjan Kaphle (GK045931)
 * @author Navya Rangeneni (NR046827)
 */

public class ResourceType {

    private static final String RESOURCE_TYPE_ID_ERROR_MESSAGE = "Resource type ID should be greater than zero";
    private static final String RESOURCE_TYPE_NAME_ERROR_MESSAGE = "Resource Type name cannot be null/empty/blank";
    private int resourceId;
    private String resourceType;

    /**
     * Default constructor
     */
    public ResourceType() {
    }

    /**
     * Parameterized Constructor for this class
     * 
     * @param resourceId
     *            an integer, unique id of the resource type
     * @param resourceType
     *            a String, type of the resource (eg. video, website link,
     *            online classroom)
     */
    public ResourceType(int resourceId, String resourceType) {
        checkParametersValidity(resourceId, resourceType);
        this.resourceId = resourceId;
        this.setResourceType(resourceType);
    }

    public ResourceType(int id) {
        checkArgument(id > 0, RESOURCE_TYPE_ID_ERROR_MESSAGE);
        this.resourceId = id;
    }

    /**
     * Sets the id of the resource
     * 
     * @param resourceId
     *            an integer, that is the unique id of the resource type
     * 
     * @throws IllegalArgumentException
     *             if id is less than or equal to 0
     */
    public void setResourceTypeId(int resourceId) {
        checkArgument(resourceId > 0, RESOURCE_TYPE_ID_ERROR_MESSAGE);
        this.resourceId = resourceId;
    }

    /**
     * Returns the unique resource id of the resource type
     * 
     * @return an integer, which is the unique id of the resource type
     */
    public int getResourceTypeId() {
        return resourceId;
    }

    /**
     * Sets the name of the resource type
     * 
     * @param resourceType
     *            a String, that is the name of the resource type
     * @throws IllegalArgumentException
     *             if the resource name is empty or null
     */
    public void setResourceType(String resourceType) {
        checkArgument(StringUtils.isNotBlank(resourceType), RESOURCE_TYPE_NAME_ERROR_MESSAGE);
        this.resourceType = resourceType;
    }

    /**
     * Returns the name of the resource type
     * 
     * @return a String, which is the the resource type
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Checks if the parameters passed in are valid or not
     * 
     * @param resourceId
     *            an integer, that is the unique identifier of the resource type
     * @param resourceType
     *            a String, that is the name of the resource
     * @throws IllegalArgumentException
     *             if the values for id and name are invalid
     */
    public static void checkParametersValidity(int resourceId, String resourceType) {
        checkArgument(resourceId > 0, RESOURCE_TYPE_ID_ERROR_MESSAGE);
        checkArgument(StringUtils.isNotBlank(resourceType), RESOURCE_TYPE_NAME_ERROR_MESSAGE);
    }
}
