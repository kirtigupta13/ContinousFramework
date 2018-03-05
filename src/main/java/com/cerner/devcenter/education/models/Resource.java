package com.cerner.devcenter.education.models;

import static com.google.common.base.Preconditions.checkArgument;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.cerner.devcenter.education.helpers.ObjectAndInputValidator;
import com.google.common.base.Strings;

/**
 * The information that is relevant to a resource is wrapped in the resources
 * class. It contains a resource ID field that is unique for each resource
 * (which is generated at the time the data is inserted into the database), the
 * link where the resource can be accessed, a short description for the
 * resource, a {@link List} of category IDs that the resource belongs to (this
 * field is used in the JSP page to retrieve those categories that are checked
 * by the admin) and a list of categories the resource belongs to. Validations
 * on data attributes of this class are done using the ObjectAndInputValidator (
 * {@link com.cerner.devcenter.education.helpers.ObjectAndInputValidator}) and
 * the HTTPURLValidator (
 * {@link com.cerner.devcenter.education.helpers.HttpURLValidator})
 *
 * @author Anudeep Kuamr Gadam (AG045334)
 * @author Mayur Rajendran (MT049536)
 * @author Navya Rangeneni (NR046827)
 * @author Rishabh Bhojak (RB048032)
 * @author Sairam Vudatha (SV051339)
 */
public class Resource {

    private static final String ID_NEGATIVE_ERROR_MESSAGE = "ID cannot be a negative number";
    private static final String RESOURCE_LINK_ERROR_MESSAGE = "There is an error in resource link.";
    private static final String RESOURCE_DESCRIPTION_EMPTY_ERROR_MESSAGE = "Resource description is null or empty.";
    private static final String RESOURCE_NAME_ERROR_MESSAGE = "Resource name cannot be null/empty/blank";
    private static final String RESOURCE_OWNER_ERROR_MESSAGE = "Resource owner cannot be null/empty/blank";
    private static final int RESOURCE_OWNER_LENGTH_UPPER_BOUND = 8;
    private static final String INVALID_LENGTH_RESOURCE_OWNER_ERROR_MESSAGE = "Resource owner is greater than 8 characters.";
    private static final String RESOURCE_TYPE_OBJECT_NULL_ERROR_MESSAGE = "Resource Type object from the argument can't be null.";
    private static final String AVERAGE_RATING_NEGATIVE_ERROR_MESSAGE = "Average Rating cannot be a negative number.";
    private static final String NUMBER_OF_RATINGS_NEGATIVE_ERROR_MESSAGE = "Number of Ratings cannot be a negative number.";
    private static final String RESOURCE_STATUS_NULL_ERROR_MESSAGE = "Resource status can not be null";
    private static final String RESOURCE_STATUS_INVALID_MESSAGE = "Resource status must be Available/Pending/Deleted";
    private static final String RESOURCE_DIFFICULTY_MAP_INVALID = "Resource difficulty map cannot be null/empty.";
    private static final String RESOURCE_DIFFICULTY_NULL_KEY = "Resource Difficulty for category map cannot have a null key";
    private static final String RESOURCE_DIFFICULTY_NULL_VALUE = "Resource Difficulty for category map cannot have null value(s)";
    private static final String RESOURCE_DIFFICULTY_KEY_INVALID = "Resource Difficulty for category map key must be greater than 0";
    private static final String RESOURCE_DIFFICULTY_VALUE_INVALID = "Resource Difficulty for category map value must be between 1 to 5";

    private String resourceName;
    private URL resourceLink;
    private String description;
    private ResourceType resourceType;
    private List<Integer> checkedCategoriesIDs = new ArrayList<Integer>();
    private final List<Category> categories = new ArrayList<Category>();
    private int resourceId;
    private Map<Integer, Integer> resourceDifficultyForCategory = new HashMap<>();
    private int averageRating;
    private int numberOfRatings;
    private String resourceOwner;
    private String resourceStatus;

    /**
     * Returns the resource status of the {@link Resource}.
     *
     * @return resource status of the {@link Resource}.
     */
    public String getResourceStatus() {
        return resourceStatus;
    }

    /**
     * Sets the resource status of the {@link Resource}.
     *
     * @param resourceStatus
     *            a {@link String} that represents status of the
     *            {@link Resource} (cannot be <code>null</code> and should be Available/Pending/Deleted).
     * @throws IllegalArgumentException
     *             when resourceStatus is <code>null</code> or not Available/Pending/Deleted.
     */
    public void setResourceStatus(String resourceStatus) {
        checkArgument(resourceStatus != null, RESOURCE_STATUS_NULL_ERROR_MESSAGE);
        try {
            ResourceStatus.valueOf(resourceStatus);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(RESOURCE_STATUS_INVALID_MESSAGE);
        }
        this.resourceStatus = resourceStatus;
    }

    /**
     * Default constructor, required by Spring.
     */
    public Resource() {
    }

    /**
     * Constructor for {@link Resource} object that ignores a Resource's
     * relationships with Categories ({@link Category}). Resource objects are
     * constructed with Resource ID -1 before they are assigned one from the
     * database.
     *
     * @param resourceLink
     *            a {@link URL} object representing the URL link for the
     *            {@link Resource}. Cannot be <code>null</code>, or an invalid
     *            URL.
     * @param description
     *            a {@link String} describing the {@link Resource}. Cannot be
     *            <code>null</code> or empty.
     * @throws IllegalArgumentException
     *             when:
     *             <ul>
     *             <li>When URL is not valid</li>
     *             <li>When description is <code>null</code> or empty</li>
     *             </ul>
     */
    public Resource(final URL resourceLink, final String description) {
        this.resourceId = -1;
        this.setResourceLink(resourceLink);
        this.setDescription(description);
    }

    /**
     * Constructor for {@link Resource} object that includes the resourceId
     * assigned from the database.
     *
     * @param resourceId
     *            an integer representing the unique id of the {@link Resource}.
     *            Cannot be 0 or negative.
     * @param resourceLink
     *            a {@link URL} object representing the URL link for the
     *            {@link Resource}. Cannot be <code>null</code>, or an invalid
     *            URL.
     * @param description
     *            a {@link String} describing the {@link Resource}. Cannot be
     *            <code>null</code> or empty.
     * @throws IllegalArgumentException
     *             when:
     *             <ul>
     *             <li>Resource Id is negative</li>
     *             <li>When URL is not valid</li>
     *             <li>When description is <code>null</code> or empty</li>
     *             </ul>
     */
    public Resource(final int resourceId, final URL resourceLink, final String description) {
        this.setResourceId(resourceId);
        this.setResourceLink(resourceLink);
        this.setDescription(description);
    }

    /**
     * Constructor for Resource Object.
     *
     * @param resourceId
     *            an integer representing the unique id of the {@link Resource}.
     *            Cannot be 0 or negative.
     * @param resourceLink
     *            a {@link URL} object representing the URL link for the
     *            {@link Resource}. Cannot be <code>null</code>, or an invalid
     *            URL.
     * @param description
     *            a {@link String} describing the {@link Resource}. Cannot be
     *            <code>null</code> or empty.
     * @param resourceName
     *            a String representing the name of the {@link Resource}. Cannot
     *            be <code>null</code> or empty.
     * @throws IllegalArgumentException
     *             when:
     *             <ul>
     *             <li>Resource Id is negative</li>
     *             <li>When URL is not valid</li>
     *             <li>When resource name is <code>null</code> or empty</li>
     *             <li>When description is <code>null</code> or empty</li>
     *             </ul>
     */
    public Resource(final int resourceId, final URL resourceLink, final String description, final String resourceName) {

        this.setResourceId(resourceId);
        this.setResourceLink(resourceLink);
        this.setDescription(description);
        this.setResourceName(resourceName);
    }

    /**
     * Constructor for {@link Resource} object that includes the resourceId
     * assigned from the database and has the resourceType.
     *
     * @param resourceId
     *            an integer representing the unique id of the {@link Resource}.
     *            Cannot be 0 or negative.
     * @param resourceLink
     *            a {@link URL} object representing the URL link for the
     *            {@link Resource}. Cannot be <code>null</code>, or an invalid
     *            URL.
     * @param description
     *            a {@link String} describing the {@link Resource}. Cannot be
     *            <code>null</code> or empty.
     * @param resourceType
     *            a {@link ResourceType} representing the type of the
     *            {@link Resource}. Cannot be <code>null</code>.
     * @throws IllegalArgumentException
     *             when:
     *             <ul>
     *             <li>Resource Id is negative</li>
     *             <li>When URL is not valid</li>
     *             <li>When description is <code>null</code> or empty</li>
     *             <li>When resource type is <code>null</code></li>
     *             </ul>
     */
    public Resource(final int resourceId, final URL resourceLink, final String description,
            final ResourceType resourceType) {

        this.setResourceId(resourceId);
        this.setResourceLink(resourceLink);
        this.setDescription(description);
        this.setResourceType(resourceType);
    }

    /**
     * Constructor for Resource Object.
     *
     * @param resourceId
     *            an integer representing the unique id of the {@link Resource}.
     *            Cannot be 0 or negative.
     * @param resourceLink
     *            a {@link URL} object representing the URL link for the
     *            {@link Resource}. Cannot be <code>null</code>, or an invalid
     *            URL.
     * @param description
     *            a {@link String} describing the {@link Resource}. Cannot be
     *            <code>null</code> or empty.
     * @param name
     *            a String representing the name of the {@link Resource}. Cannot
     *            be <code>null</code> or empty.
     * @param resourceType
     *            a {@link ResourceType} representing the type of the
     *            {@link Resource}. Cannot be <code>null</code>.
     * @throws IllegalArgumentException
     *             when:
     *             <ul>
     *             <li>Resource Id is negative</li>
     *             <li>When URL is not valid</li>
     *             <li>When resource name is <code>null</code> or empty</li>
     *             <li>When description is <code>null</code> or empty</li>
     *             <li>When resource type is <code>null</code></li>
     *             </ul>
     */
    public Resource(final int resourceId, final URL resourceLink, final String description, final String name,
            final ResourceType resourceType) {

        this.setResourceId(resourceId);
        this.setResourceLink(resourceLink);
        this.setDescription(description);
        this.setResourceName(name);
        this.setResourceType(resourceType);

    }

    /**
     * Constructor for {@link Resource} Object.
     *
     * @param resourceId
     *            an integer representing the unique id of the {@link Resource}.
     *            Cannot be 0 or negative.
     * @param resourceLink
     *            a {@link URL} object representing the URL link for the
     *            {@link Resource}. Cannot be <code>null</code>, or an invalid
     *            URL.
     * @param description
     *            a {@link String} describing the {@link Resource}. Cannot be
     *            <code>null</code> or empty.
     * @param name
     *            a String representing the name of the {@link Resource}. Cannot
     *            be <code>null</code> or empty.
     * @param resourceType
     *            a {@link ResourceType} representing the type of the
     *            {@link Resource}. Cannot be <code>null</code>.
     * @param averageRating
     *            an integer representing the average rating of the
     *            {@link Resource}. Cannot be negative.
     * @param numberOfRatings
     *            an integer representing the total number of ratings for the
     *            {@link Resource}. Cannot be negative.
     * @param resourceOwner
     *            a String representing the resourceOwner ID of the
     *            {@link Resource}. Cannot be <code>null</code>, blank or empty.
     * @throws IllegalArgumentException
     *             when:
     *             <ul>
     *             <li>Resource Id is negative</li>
     *             <li>When URL is not valid</li>
     *             <li>When resource name is <code>null</code> or empty</li>
     *             <li>When description is <code>null</code> or empty</li>
     *             <li>When resource type is <code>null</code></li>
     *             <li>When average rating is negative</li>
     *             <li>When number of ratings is negative</li>
     *             <li>resourceOwner is <code>null</code>, blank or empty</li>
     *             </ul>
     */
    public Resource(final int resourceId, final URL resourceLink, final String description, final String name,
            final ResourceType resourceType, final int averageRating, final int numberOfRatings,
            final String resourceOwner) {
        this(resourceId, resourceLink, description, name, resourceType);
        this.setAverageRating(averageRating);
        this.setNumberOfRatings(numberOfRatings);
        this.setResourceOwner(resourceOwner);
    }

    /**
     * Fetches the resource ID of the {@link Resource} object.
     *
     * @return resourceId, an integer that represents the unique ID of the
     *         {@link Resource}.
     */
    public int getResourceId() {
        return resourceId;
    }

    /**
     * Sets the value of the resource to an integer value that is unique to a
     * resource object.
     *
     * @param resourceId
     *            integer which is the unique identifier of the resource (cannot
     *            be duplicated and cannot be less that 0).
     * @throws IllegalArgumentException
     *             if id is less than zero
     */
    public void setResourceId(final int resourceId) {
        checkArgument(resourceId > 0, ID_NEGATIVE_ERROR_MESSAGE);
        this.resourceId = resourceId;
    }

    /**
     * Retrieves the resourceLink {@link URL} object of the {@link Resource}
     * object.
     *
     * @return the {@link URL} object of the {@link Resource} object.
     * @throws MalformedURLException
     *             when the {@link URL} object is <code>null</code>, empty or
     *             invalid.
     */
    public URL getResourceLink() {
        return this.resourceLink;
    }

    /**
     * Sets the value of the link where the resource is present to a {@link URL}
     * object resourceLink.
     *
     * @param resourceLink
     *            a {@link URL} object containing the URL at which the resource
     *            is present (not <code>null</code>, not empty, contains a valid
     *            URL and the only HTTP and HTTPS are allowed as URL protocols).
     * @throws IllegalArgumentException
     *             when the URL is not valid (i.e. could be <code>null</code>,
     *             empty or not properly formed).
     */
    public void setResourceLink(final URL resourceLink) {
        this.resourceLink = resourceLink;
    }

    /**
     * Returns the description of the {@link Resource} object.
     *
     * @return description, a {@link String} describing the {@link Resource}
     *         object. Cannot be <code>null</code> or empty.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the {@link Resource}.
     *
     * @param description
     *            {@link String} that contains a description of the
     *            {@link Resource} (not <code>null</code>, not empty).
     */
    public void setDescription(final String description) {
        checkArgument(!Strings.isNullOrEmpty(description), RESOURCE_DESCRIPTION_EMPTY_ERROR_MESSAGE);
        this.description = description;
    }

    /**
     * Returns the resource name of the {@link Resource}.
     *
     * @return resource name of the {@link Resource}.
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Sets the resource name of the {@link Resource}.
     *
     * @param resourceName
     *            that contains resource name of the {@link Resource} (cannot be
     *            <code>null</code> or empty).
     * @throws IllegalArgumentException
     *             when resource name is <code>null</code>/empty.
     */
    public void setResourceName(final String resourceName) {
        checkArgument(StringUtils.isNotBlank(resourceName), RESOURCE_NAME_ERROR_MESSAGE);
        this.resourceName = resourceName;
    }

    /**
     * Returns the list of categories IDs the {@link Resource} belongs to.
     * CheckedCategoriesIDs is returned to the user as a modifiable list as it
     * is required by the JSP page.
     *
     * @return a {@link List} of {@link String} with the IDs of the categories
     *         the {@link Resource} belongs to.
     */
    public List<Integer> getCheckedCategoriesIDs() {
        return checkedCategoriesIDs;
    }

    /**
     * Sets the checkedCategories to the category IDs that were checked by the
     * admin.
     *
     * @param checkedCategoriesIDs
     *            a {@link List} containing the category IDs checked by the
     *            admin (the checkedCategories cannot be <code>null</code> or
     *            empty).
     */
    public void setCheckedCategoriesIDs(final List<Integer> checkedCategoriesIDs) {
        ObjectAndInputValidator.checkListValidity(checkedCategoriesIDs);
        this.checkedCategoriesIDs = checkedCategoriesIDs;
    }

    /**
     * Returns a {@link List} of categories that the {@link Resource} belongs
     * to.
     *
     * @return categories, a {@link List} of Category class objects (
     *         {@link Category}), that contains the list of categories that the
     *         {@link Resource} belongs to. An unmodifiable list is returned so
     *         that the user can see a read only and unmodifiable copy of the
     *         categories list.
     */
    public List<Category> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    /**
     * Adds one {@link Category} object at a time to the {@link List} of
     * {@link Category} objects that the {@link Resource} belongs to. There is
     * no setter for the List<Category> as that could be potentially used to
     * reset the entire list.
     *
     * @param category
     *            an object of the Category class ({@link Category}) that
     *            represents a category that the resource belongs to (cannot be
     *            <code>null</code> and cannot have invalid values for the
     *            category ID, name and description).
     */
    public void addCategory(final Category category) {
        // Validations on the category object are done before insertion.
        Category.checkCategoryParametersValidity(category.getId(), category.getName(), category.getDescription());
        this.categories.add(category);
    }

    /**
     * Returns the category for a given category ID.
     *
     * @param categoryID
     *            ({@link Category #categoryID}), an integer containing the
     *            unique identifier of the category object (greater than 0 and
     *            not <code>null</code>).
     * @return the Category ({@link Category}) Object corresponding to the
     *         categoryID. Returns <code>null</code> if the categoryID is
     *         absent.
     * @throws IllegalArgumentException
     *             if id is less than zero
     */
    public Category getCategoryByID(final int categoryID) {
        checkArgument(categoryID >= 0, ID_NEGATIVE_ERROR_MESSAGE);

        // The categories list containing the categories that the resource
        // belong to are checked one
        // by one and when a match is found the category object that has that ID
        // is
        // returned to the user.
        for (final Category category : categories) {
            if (category.getId() == categoryID) {
                return category;
            }
        }
        return null;
    }

    /**
     * @return the resourceDifficultyForCategory {@link Map} containing the
     *         resource and difficultyLevel pairs.
     */
    public Map<Integer, Integer> getResourceDifficultyForCategory() {
        return resourceDifficultyForCategory;
    }

    /**
     * sets the resourceDifficultyForCategory after validation.
     *
     * @param resourceDifficultyForCategory
     *            the resourceDifficultyForCategory to set (cannot be empty).
     * @throws IllegalArgumentException
     *             <ul>
     *             <li>when resourceDifficultyForCategory map is null/empty</li>
     *             <li>when resourceDifficultyForCategory map has a null
     *             key</li>
     *             <li>when resourceDifficultyForCategory map has null
     *             value(s)</li>
     *             <li>when resourceDifficultyForCategory has a key less than
     *             1</li>
     *             <li>when resourceDifficultyForCategory has a value other than
     *             from 1 to 5</li>
     *             </ul>
     */
    public void setResourceDifficultyForCategory(final Map<Integer, Integer> resourceDifficultyForCategory) {
        checkArgument(!MapUtils.isEmpty(resourceDifficultyForCategory), RESOURCE_DIFFICULTY_MAP_INVALID);
        checkArgument(!resourceDifficultyForCategory.containsKey(null), RESOURCE_DIFFICULTY_NULL_KEY);
        checkArgument(!resourceDifficultyForCategory.containsValue(null), RESOURCE_DIFFICULTY_NULL_VALUE);
        for (Map.Entry<Integer, Integer> entry : resourceDifficultyForCategory.entrySet()) {
            checkArgument(entry.getKey() > 0, RESOURCE_DIFFICULTY_KEY_INVALID);
            checkArgument(entry.getValue() >= 1 && entry.getValue() <= 5, RESOURCE_DIFFICULTY_VALUE_INVALID);
        }
        this.resourceDifficultyForCategory = resourceDifficultyForCategory;
    }

    /**
     * Returns the type of the resource (can be <code>null</code>)
     *
     * @return {@link ResourceType}, an instance of ResourceType class object
     *         that represent the type of the resource (eg. website link,
     *         videos, ebook..).
     */
    public ResourceType getResourceType() {
        return resourceType;
    }

    /**
     * Sets the type of the resource
     *
     * @param resourceType
     *            that represents the type of the resource
     */
    public void setResourceType(final ResourceType resourceType) {
        checkArgument(resourceType != null, RESOURCE_TYPE_OBJECT_NULL_ERROR_MESSAGE);
        this.resourceType = resourceType;
    }

    /**
     * Fetches the average rating of the {@link Resource} object.
     *
     * @return averageRating, an integer that represents the average of all the
     *         ratings of a particular {@link Resource}.
     */
    public int getAverageRating() {
        return averageRating;
    }

    /**
     * Sets the average rating of {@link Resource} to an integer value.
     *
     * @param averageRating
     *            integer which represents the average of all ratings for a
     *            particular resource. Cannot be negative.
     * @throws IllegalArgumentException
     *             if averageRating is less than zero
     */
    public void setAverageRating(final int averageRating) {
        checkArgument(averageRating >= 0, AVERAGE_RATING_NEGATIVE_ERROR_MESSAGE);
        this.averageRating = averageRating;
    }

    /**
     * Fetches the number of ratings of the {@link Resource} object.
     *
     * @return numberOfRatings, an integer that represents the total number of
     *         ratings of the particular {@link Resource}. Guaranteed to be
     *         non-negative.
     */
    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    /**
     * Sets the number of ratings for the {@link Resource} to an integer value.
     *
     * @param numberOfRatings
     *            an integer that represents the total number of ratings of the
     *            particular {@link Resource}. Cannot be negative.
     * @throws IllegalArgumentException
     *             if rating is less than zero
     */
    public void setNumberOfRatings(final int numberOfRatings) {
        checkArgument(numberOfRatings >= 0, NUMBER_OF_RATINGS_NEGATIVE_ERROR_MESSAGE);
        this.numberOfRatings = numberOfRatings;
    }

    /**
     * Returns the resourceOwner string ID of the {@link Resource}.
     *
     * @return resourceOwner of the {@link Resource}.
     */
    public String getResourceOwner() {
        return resourceOwner;
    }

    /**
     * Sets the resourceOwner of the {@link Resource}.
     *
     * @param resourceOwner
     *            that contains resourceOwner ID of the {@link Resource}. Cannot
     *            be <code>null</code>, blank or empty).
     * @throws IllegalArgumentException
     *             when resourceOwner is <code>null</code>, blank or empty.
     */
    public void setResourceOwner(String resourceOwner) {
        checkArgument(StringUtils.isNotBlank(resourceOwner), RESOURCE_OWNER_ERROR_MESSAGE);
        checkArgument(StringUtils.length(resourceOwner) <= RESOURCE_OWNER_LENGTH_UPPER_BOUND,
                INVALID_LENGTH_RESOURCE_OWNER_ERROR_MESSAGE);
        this.resourceOwner = resourceOwner;
    }
}
