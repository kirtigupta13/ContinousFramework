/**
 * Function to validate the input by the user.
 */
$(document).ready(
        function() {
            $('#resourceLink').on("blur", function() {
                return validateResourceLink('#resourceLink', '#errorMessageForLink');
            });

            $('#description').on("blur", function() {
                return validateDescription('#description', '#errorMessageForDescription');
            });

            $('#resourceName').on("blur", function() {
                return validateResourceName('#resourceName', '#errorMessageForResourceName');
            });

            // on click of the add button
            $('#addButton').click(
                    function() {
                        var resourceValidation = validateCheckedCategories('#infoMessageNoCategories') && validateResourceLink('#resourceLink', '#errorMessageForLink')
                                && validateDescription('#description', '#errorMessageForDescription') && validateResourceName('#resourceName', 'errorMessageForResourceName');
                        addResourceCategoryRelations();
                        return resourceValidation;
                    });
        });

/**
 * Sets the difficulty level as value for hidden input tags in the category table
 * for form submit binding.
 */
function addResourceCategoryRelations() {
    var table = $("#topicTable");

    table.find('tr.added-category').each(function() {
        var difficultyLevel = $(this).find('.difficulty-selection').val();
        $(this).find('.difficulty-level-for-category').val(difficultyLevel);
    });
}

/**
 * Function that verifies the user has added at least one category. Highlights
 * the "select at least one category" info message in red if the user has not
 * yet added a category.
 * 
 * @param errorLabelId
 *            id of the displayed error message
 * @returns boolean true if at least one category has been added. Otherwise,
 *          returns false.
 * 
 */
function validateCheckedCategories(errorLabelId) {
    if ($("#topicTable tr").length > 1) {
        return true;
    }
    // make the info message appear red
    var errorLabel = $(errorLabelId).removeClass("info-message").addClass("errorMessage");
    errorLabel.find(".glyphicon-info-sign").removeClass("info-message").addClass("errorMessage");
    return false;
}

/**
 * Function that validates the Resource Link entered by the user.
 * 
 * @param elementId
 *            id of the input resource link
 * @param errorLabelId
 *            id of the displayed error message
 * @returns boolean true if resource link is valid otherwise false.
 * 
 */
function validateResourceLink(elementId, errorLabelId) {
    if ($(elementId).val().trim() == '' || !isValidURL($(elementId).val())) {
        $(errorLabelId).show();
        return false
    } else {
        $(errorLabelId).hide();
        return true
    }
}

/**
 * Function that validates the Description entered by the user.
 * 
 * @param elementId
 *            id of the input description
 * @param errorLabelId
 *            id of the displayed error message
 * @returns boolean true if description is valid otherwise false.
 * 
 */
function validateDescription(elementId, errorLabelId) {
    if ($(elementId).val().trim() == '' || !isValidDescription($(elementId).val())) {
        $(errorLabelId).show();
        return false
    } else {
        $(errorLabelId).hide();
        return true
    }
}

/**
 * Function that validates the ResourceName entered by the user.
 * 
 * @param elementId
 *            id of the input ResourceName
 * @param errorLabelId
 *            id of the displayed error message
 * @returns boolean true if ResourceName is valid otherwise false.
 * 
 */
function validateResourceName(elementId, errorLabelId) {
    if ($(elementId).val().trim() == '' || !isValidResourceName($(elementId).val())) {
        $(errorLabelId).show();
        return false
    } else {
        $(errorLabelId).hide();
        return true
    }
}

/**
 * Function that validates the url entered by the user.
 * 
 * @param url
 *            url entered by the user
 * @returns boolean true if url is valid otherwise false.
 * 
 */
function isValidURL(url) {
    var urlregex = new RegExp(
            "^(http|https|ftp)\://([a-zA-Z0-9\.\-]+(\:[a-zA-Z0-9\.&amp;%\$\-]+)*@)*((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0-9\-]+\.)*[a-zA-Z0-9\-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(\:[0-9]+)*(/($|[a-zA-Z0-9\.\,\?\'\\\+&amp;%\$#\=~_\-]+))*$");
    return urlregex.test(url);
}

/**
 * Function that validates the description entered by the user.
 * 
 * @param description
 *            String as a description entered by the user
 * @returns boolean true if description is valid otherwise false.
 * 
 */
function isValidDescription(description) {
    return description.match("^[a-zA-Z0-9 ]+$");
}

/**
 * Function that validates the resource name entered by the user.
 * 
 * @param resourceName
 *            String as a name entered by the user (cannot be null/empty)
 * @returns boolean true if name is valid otherwise false.
 * 
 */
function isValidResourceName(resourceName) {
    return resourceName.match("^[a-zA-Z ]+$");
}