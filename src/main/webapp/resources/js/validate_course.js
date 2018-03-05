/**
 * Function to validate the input by the user.
 */
$(document).ready(function() {
	$('#courseName').on("blur",function() {
		return validateString('#courseName','#errorMessageForcourseName');
	});
	
	$('#courseCampus').on("blur",function() {
		return validateString('#courseCampus','#errorMessageForcourseCampus');
	});

	$('#courseURL').on("blur",function() {
		return validateCourseURL('#courseURL','#errorMessageForurl');
	});
			
	$('#instructor').on("blur",function() {
		return validateString('#instructor','#errorMessageForInstructor');
	});
	
	$('#difficultyLevel').on("blur",function() {
		return validateDifficultyLevel('#difficultyLevel','#errorMessageForDifficultyLevel');
	});
					
	// on click of the add button
	$('#addButton').click(function() {
	    if (!validateCheckedCategories('#errorMessageForCheckedId')
			|| !validateCourseURL('#courseURL','#errorMessageForurl')
			|| !validateString('#courseName','#errorMessageForcourseName')
			|| !validateString('#courseCampus','#errorMessageForcourseCampus')
			|| !validateString('#instructor','#errorMessageForInstructor')
			|| !validateDifficultyLevel('#difficultyLevel','#errorMessageForDifficultyLevel')){
				validateCheckedCategories('#errorMessageForCheckedId')
				validateCourseURL('#courseURL','#errorMessageForurl')
				validateString('#courseName','#errorMessageForcourseName')
				validateString('#courseCampus','#errorMessageForcourseCampus')
				validateString('#instructor','#errorMessageForInstructor')
				validateDifficultyLevel('#difficultyLevel','#errorMessageForDifficultyLevel')
				return false;
		} else
				return true;

	});
});

/**
 * Function that validates the checked category Id by the user.
 * @param errorLabelId
 * 			id of the displayed error message
 * @returns boolean
 * 			true if at least one of the category id is checked otherwise false
 * 
 */
function validateCheckedCategories(errorLabelId) {
	if ($(":checkbox:checked").length > 0) {
		$(errorLabelId).hide();
		return true;
	}
	$(errorLabelId).show();
	return false;

}

/**
 * Function that validates the Course URL entered by the user.
 * @param elementId
 * 			id of the input Course URL
 * @param errorLabelId
 * 			id of the displayed error message
 * @returns boolean
 * 			true if Course URL is valid otherwise false.
 * 
 */
function validateCourseURL(elementId, errorLabelId) {
	if ($(elementId).val().trim() == '' || !isValidURL($(elementId).val())) {
		$(errorLabelId).show();
		return false
	} else {
		$(errorLabelId).hide();
		return true
	}
}

/**
 * Function that validates the name description or instructor entered by the
 * user.
 * 
 * @param elementId
 *            id of the input
 * @param errorLabelId
 *            id of the displayed error message
 * @returns boolean true if description is valid otherwise false.
 * 
 */
function validateString(elementId, errorLabelId) {
	if ($(elementId).val().trim() == '' || !isValidString($(elementId).val())) {
		$(errorLabelId).show();
		return false
	} else {
		$(errorLabelId).hide();
		return true
	}
}

/**
 * Function that validates the skillLevel entered by the user.
 * @param elementId
 * 			id of the input skillLevel
 * @param errorLabelId
 * 			id of the displayed error message
 * @returns boolean
 * 			true if skillLevel is valid otherwise false.
 * 
 */
function validateDifficultyLevel(elementId, errorLabelId) {
	if ($(elementId).val().trim() == ''|| $(elementId).val()<1) {
		$(errorLabelId).show();
		return false
	} else {
		$(errorLabelId).hide();
		return true
	}
}

/**
 * Function that validates the url entered by the user.
 * @param url
 * 			url entered by the user
 * @returns boolean
 * 			true if url is valid otherwise false.
 * 
 */
function isValidURL(url){
	var urlregex = new RegExp("^(http|https|ftp)\://([a-zA-Z0-9\.\-]+(\:[a-zA-Z0-9\.&amp;%\$\-]+)*@)*((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0-9\-]+\.)*[a-zA-Z0-9\-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(\:[0-9]+)*(/($|[a-zA-Z0-9\.\,\?\'\\\+&amp;%\$#\=~_\-]+))*$");
	return urlregex.test(url);
}

/**
 * Function that validates the name description or instructor entered by the
 * user.
 * 
 * @param description
 *            String as a description entered by the user
 * @returns boolean true if description is valid otherwise false.
 * 
 */
function isValidString(nameDescriptionOrInstructor) {
	return nameDescriptionOrInstructor.match("^[a-zA-Z0-9 ]+$");
}