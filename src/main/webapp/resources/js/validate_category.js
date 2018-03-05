/**
 * Function to validate the input by the user.
 */
$(document).ready(function() {

	$('#name').on("blur",function() {
		return validateCategoryName('#name','#errorMessageForCategoryName');
	});
			
	$('#description').on("blur",function() {
		return validateCategoryDescription('#description','#errorMessageForDescription');
	});
					
	// on click of the add button
	$('#addButton').click(function() {
	    return (validateCategoryName('#name','#errorMessageForCategoryName')
				&& validateCategoryDescription('#description','#errorMessageForDescription'));
	});

    // On click of delete button.
    $("[id^='removeButton']").click(function() {
        var categoryId = $(this).data('categoryid');
        deleteCategory(categoryId);
    });
});

/**
 * Deletes the category based on categoryId.
 * 
 * @param categoryId
 *            categoryId of the category which needs to be deleted
 */
function deleteCategory(categoryId) {
    $.ajax({
        type : "GET",
        url : "delete_category",
        dataType : "html",
        data : {
            categoryId : categoryId
        },
        success : function(response) {
                $("#" + categoryId).remove();
                $("#successMessages").html("Category deleted.")
        },
        error : function(data){
            $("#errorMessages").html("Error deleting the category, please try again later.")
        }
    });
}

/**
 * Function that validates the Category name entered by the user.
 * @param elementId
 * 			id of the Category name
 * @param errorLabelId
 * 			id of the displayed error message
 * @returns boolean
 * 			true if category name is valid else false.
 */
function validateCategoryName(elementId, errorLabelId) {
	if ($(elementId).val().trim() == '' || !isValidText($(elementId).val())) {
		$(errorLabelId).show();
		return false
	} else {
		$(errorLabelId).hide();
		return true
	}
}

/**
 * Function that validates the Category description entered by the user.
 * @param elementId
 * 			id of the Category description
 * @param errorLabelId
 * 			id of the displayed error message
 * @returns boolean
 * 			true if category description is valid else false.
 */
function validateCategoryDescription(elementId, errorLabelId) {
	if ($(elementId).val().trim() == '' || !isValidText($(elementId).val())) {
		$(errorLabelId).show();
		return false
	} else {
		$(errorLabelId).hide();
		return true
	}
}

/**
 * Function that validates the text given by the user using regular expression.
 * @param text
 * 			String entered by the user
 * @returns boolean
 * 			true if text entered by user is valid else false.
 */
function isValidText(text){
	return text.match("^[a-zA-Z0-9 ]+$");
}