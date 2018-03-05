package com.cerner.devcenter.education.utils;

/**
 * Class to define the constants and queries used in the application
 *
 * @author Surbhi Singh (SS043472)
 * @author Manoj Raj Devalla(MD042936)
 * @author Jacob Zimmermann (JZ022690)
 * @author Vincent Dasari (VD049645)
 */
public final class Constants {
    // Common Constants
    public static final String ADMIN_NOT_LOGGED_IN = "Admin is not logged in";
    public static final String USER_NOT_LOGGED_IN = "User is not logged in";
    public static final String SESSION_NULL_ERROR_MESSAGE = "Session cannot be null";
    public static final String DATABASE_ERROR_MESSAGE = "Error while accessing the database";

    // Constant for Course Manager
    public static final String COURSE_MANAGER_EXCEPTION_MESSAGE = "Error: Manager Exception when performing database operation";

    // Resources
    public static final String RESOURCE_URL_INVALID = "Resource URL is invalid";
    public static final String RESOURCE_DESCRIPTION_INVALID = "Resource description is invalid";
    public static final String RESOURCE_NAME_INVALID = "Resource name is invalid";
    public static final String RESOURCE_REQUIREDSKILLLEVEL_INVALID = "Required skill level of Resource is Invalid";
    public static final int AUTOFILL_SIZE = 10;
    // Constants for UserController
    public static final String MESSAGE = "message";
    public static final String INVALID_USER_ID = "User ID is invalid. Please re-enter";
    public static final String ADMINHOME = "admin_home_page";
    public static final String REDIRECT_LOGIN = "redirect:/login";
    public static final String MANAGE_ADMINS = "manage_admins";
    public static final String ADMIN_ROLE = "admin";
    public static final String CHECK_NULL_ERROR_USER = "Error: User must not be null";
    public static final String LOG_ERROR_BINDING = "There was a BindingResult error. Please enter valid input.";
    public static final String LOG_ERROR_ADD_USER = "Error while adding user to user table";
    public static final String LOG_ERROR_DATABASE_ACCESS = "Error in accessing the database";
    public static final String USER_ADD_SUCCESSFUL_MESSAGE = "User added successfully";
    public static final String USER_REMOVED_SUCCESSFUL_MESSAGE = "User has been deleted";
    public static final String USER_LOG_ERROR = "Admin not logged in";
    public static final String USER_ADD_EXCEPTION_HANDLER = "exceptionHandler";
    public static final String USER_ADD_EXCEPTION_NAME = "exceptionName";
    public static final String USER_ADD_EXCEPTION_MESSAGE = "Unable to Add user";
    public static final String USER_MANAGER_USER_NULL = "Error: User Object is null";
    public static final String USER_MANAGER_USERDETAILS_NULL = "Error: UserDetail Object is null";
    public static final String USER_MANAGER_ERROR = "Error: Unable to retrieve user detail";
    public static final String USER_MANAGER_USERID_ERROR = "Error: userID not found";
    public static final String AVAILABLE_USERS = "available_users";
    public static final String LOG_ERROR_DELETE_USER = "Error while deleting user from user table";
    public static final String USER_DELETE_EXCEPTION_HANDLER = "exceptionHandler";
    public static final String USER_DELETE_EXCEPTION_NAME = "exceptionName";
    public static final String USER_DELETE_EXCEPTION_MESSAGE = "Error when deleting the specified user";
    public static final String USER = "user";
    public static final String ERROR_MESSAGE = "exception while adding user";
    public static final String ERROR_MESSAGE_ROLE = "Error no such role exists";
    public static final String RETRIEVE_USERS_LIST_ERROR = "Error: Unable to retrieve list of available users";
    // course controller
    public final static String CATEGORY = "category";
    public final static String CATEGORIES = "categories";
    public static final String COURSE_VIEW = "courses";
    public static final String COURSE_INFO_MESSAGE = "There are no courses that are assigned that category.";

    // constant for Invalid Search i18N
    public static final String SEARCH_INVALID_I18N = "com.cerner.devcenter.education.search.invalid";

    // Bulk upload exception messages
    public static final String DELIMITER = ";";
    public static final String UPLOAD_ERROR_I18N = "bulkUpload.errorMessage";
    public static final String UPLOAD_SUCCESS_I18N = "bulkUpload.successMessage";
    public static final String UPLOAD_TYPE_ERROR_I18N = "bulkUpload.typeErrorMessage";
    public static final String UPLOAD_ENCRYPTION_ERROR_I18N = "bulkUpload.encryptionErrorMessage";
    public static final String UPLOAD_FORMAT_ERROR = "bulkUpload.formatErrorMessage";
    public static final String UPLOAD_NO_CATEGORY_HEADERS_ERROR_I18N = "bulkUpload.noCategoryHeaders";
    /***
     * <pre>
     * "There was an issue with resource type in cell {0}. Check that the type exists in the site."
     * </pre>
     */
    public static final String UPLOAD_CELL_RESOURCE_TYPE_ERROR_I18N = "bulkUpload.cell.resourceTypeErrorMessage";
    /***
     * <pre>
     * "The link in cell {0} cannot be translated into a URL."
     * </pre>
     */
    public static final String UPLOAD_CELL_LINK_ERROR_I18N = "bulkUpload.cell.linkErrorMessage";
    /***
     * <pre>
     * "There was an issue with the category in cell {0}. Check that the category exists in the site."
     * </pre>
     */
    public static final String UPLOAD_CELL_CATEGORY_ERROR_I18N = "bulkUpload.cell.categoryErrorMessage";
    /***
     * <pre>
     * "There is an issue with the category difficulty in cell {0}. Check that the category it is matched to exists on the site."
     * </pre>
     */
    public static final String UPLOAD_CELL_CATEGORY_DIFFIULTY_ERROR_I18N = "bulkUpload.cell.categoryDifficultyErrorMessage";
    /***
     * <pre>
     * "The cell {0} is not formatted as a number."
     * </pre>
     */
    public static final String UPLOAD_CELL_NUMBER_ERROR_I18N = "bulkUpload.cell.numberErrorMessage";
    /***
     * <pre>
     * "The cell {0} is not formatted as text."
     * </pre>
     */
    public static final String UPLOAD_CELL_TEXT_ERROR_I18N = "bulkUpload.cell.textErrorMessage";
    /***
     * <pre>
     * "One or more columns before cell {0} were skipped. This is likely due to empty cells."
     * </pre>
     */
    public static final String UPLOAD_CELL_SKIPPED_ERROR_I18N = "bulkUpload.cell.skippedErrorMessage";
    /***
     * <pre>
     * "No categories were found for the row starting in cell {0}. At least one category is required."
     * </pre>
     */
    public static final String UPLOAD_NO_CATEGORY_ERROR_I18N = "bulkUpload.noCategoryErrorMessage";
    /***
     * <pre>
     * "One of the category:difficulty pairs in {0} is missing a value."
     * </pre>
     */
    public static final String UPLOAD_CELL_CATEGORY_PAIR_ERROR_I18N = "bulkUpload.cell.categoryPairErrorMessage";
    /***
     * <pre>
     * "One of the category names in cell {0} could not be found in the database. Check that the category names in that cell exist on the site."
     * </pre>
     */
    public static final String UPLOAD_CELL_CATEGORY_NAME_ERROR_I18N = "bulkUpload.cell.categoryNameErrorMessage";
    /***
     * <pre>
     * "One of the category difficulties in cell {0} is not a number."
     * </pre>
     */
    public static final String UPLOAD_CELL_DIFFICULTY_NOT_AN_INTEGER_ERROR_I18N = "bulkUpload.cell.categoryDifficultyNotAnIntegerErrorMessage";
    /***
     * <pre>
     * "The resource name in cell {0} is already in use by another resource."
     * </pre>
     */
    public static final String UPLOAD_RESOURCE_EXISTS_ERROR_I18N = "bulkUpload.cell.resourceAlreadyExistsMessage";

    // Constants for UserRecommendedResourceDAO
    public static final String USER_ID_NULL = "The specified User ID is null.";
    public static final String USER_ID_BLANK_OR_EMPTY = "The specified User ID is blank.";
    public static final String CATEGORY_LIST_EMPTY = "The list of categories to filter by is empty.";
    public static final String CATEGORY_LIST_NULL = "The list of categories to filter by is null";
    public static final String CATEGORY_ID_MUST_BE_POSITIVE = "The ID for the category must be positive.";
    public static final String CATEGORY_NAME_NULL = "The category name cannot be null.";
    public static final String CATEGORY_NAME_EMPTY_OR_BLANK = "The category name cannot be empty or blank.";
    public static final String CATEGORY_LIST_HAS_NULL_ITEM = "The category list contains a null item.";
    public static final String ERROR_RETRIEVING_RECOMMENDED_RESOURCES = "There was an error retrieving recommended resources for the user.";

    // constants for TagDAOImpl
    public static final String TAG_ID_MUST_BE_POSITIVE = "The Tag ID must be positive.";
    public static final String TAG_ID_HAS_NULL_NAME = "The queried tag has a null tag name.";
    public static final String TAG_ID_HAS_EMPTY_NAME = "The queried tag has an empty tag name.";
    public static final String ERROR_ADDING_TAG_TO_DB = "There was an error adding the tag to the database.";
    public static final String TAG_NAME_CANNOT_BE_NULL = "The tag name cannot be null.";
    public static final String TAG_NAME_CANNOT_BE_BLANK_OR_EMPTY = "The tag name cannot be a blank or empty string.";
    public static final String ERROR_FINDING_TAGS_BY_NAME = "There was an error when finding tags whose name matches the specified name.";
    public static final String TAG_LIST_HAS_NULL_TAG = "The specified tag list contains a null tag.";
    /**
     * Error while extracting tag with id (integer).
     */
    public static final String ERROR_GETTING_TAG_BY_ID = "Error while extracting tag with id %d.";

    // constants for ResourceTagRelationDAO
    public static final String RESOURCE_CANNOT_BE_NULL = "The resource cannot be null.";
    public static final String TAG_CANNOT_BE_NULL = "The tag cannot be null.";
    public static final String RESOURCE_ID_MUST_BE_POSITIVE = "The Resource ID must be positive.";
    public static final String RESOURCE_WITH_ID_NOT_FOUND = "The resource with the specified ID was not found.";
    public static final String TAG_WITH_ID_NOT_FOUND = "The tag with the specified ID was not found.";
    public static final String ERROR_ADDING_TAG_RESOURCE_RELTN = "There was an error while attempting to update the database with the tag-resource relation.";

    // constants for TagManager
    public static final String TAG_LIST_EMPTY = "The list of tag names is empty.";
    public static final String TAG_LIST_NULL = "The list of tag names is null.";
    public static final String TAG_LIST_HAS_BLANK_OR_EMPTY_STRING = "The list of tag names contains a blank or empty tag name.";
    public static final String TAG_LIST_HAS_NULL_STRING = "The list of tag names contains a null string.";
    public static final String TAG_NAME_ALREADY_EXISTS = "A tag with that name already exists.";

    // constants for ResourceController
    public static final String ERROR_ADDING_TAGS = "There was an error when attempting to add the tags to the resource.";

    // constants for TagController
    public static final String ADD_TAGS_TO_RESOURCE_REQUEST_NULL = "The object representing the request for adding tags to the resource is null.";

    // constants for QueryExpanderUtils
    public static final String NUMBER_OF_PLACEHOLDERS_MUST_BE_POSITIVE = "The number of placeholders to expand the query for must be positive.";

    // Constants for CategoryController
    public static final String ID_INVALID_ERROR_MESSAGE = "The ID is invalid.";
    public static final String SEARCH_INVALID_ERROR_MESSAGE = "Search string cannot be null or empty.";

    // constants for SurveyController
    public static final String CATEGORY_OBJECT_NULL_ERROR_MESSAGE = "Category object cannot be null";
    public static final String MODEL_OBJECT_NULL_ERROR_MESSAGE = "Model object cannot be null";
    public static final String RESULT_OBJECT_NULL_ERROR_MESSAGE = "Result object passed can't be null";
    public static final String BINDING_RESULT_ERROR = "There was a BindingResult error. Please enter valid input.";
    public static final String CATEGORY_DELETE_ERROR_MESSAGE = "Error Deleting Category";
    public static final String QUERY_FAIL_ERROR = "Query failed: ";

}
