/**
 * Function to validate the input by the user.
 */
$(document).ready(function() {
	$('#sendRequest').click(function() {
	    return (validateTextField('#categoryName','#errorMessageForCategoryName')
				&& validateTextField('#resourceName','#errorMessageForResourceName'));
	});		
});

/**
 * Function that validates the Category name and Resource Name entered by the user.
 * @param elementId
 * 			id of the category name/resource name
 * @param errorLabelId
 * 			id of the displayed error message
 * @returns boolean
 * 			true if category name/resource name is characters and spaces else false.
 */
function validateTextField(elementId, errorLabelId) {
	var inputText = $(elementId).val();  
	if (inputText===undefined||inputText===null||inputText.trim()===''|| !isTextCharactersAndSpacesOnly(inputText)) 
	{
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
 * 			true if text entered by user is characters and spaces else false.
 */
function isTextCharactersAndSpacesOnly(text){
	return text.match("^[a-zA-Z ]+$");
}