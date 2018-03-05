var CATEGORY_SEARCH = "category";
var RESOURCE_SEARCH = "resource";

/**
 * function to show the auto search results of category the user in training
 * resource page.
 */
$(document).ready(function() {

    var nonchosenCategoryList = $("#nonchosenCategoryList").val();
    var nonchosenCategoryList = $("#chosenCategoryList").val();

    $("#trainingResourceCategorySearchAutoComplete").autocomplete({
        minLength : 2,
        source : function(request, response) {
            getSearchRequest($("#trainingResourceCategorySearchAutoComplete").val(), 'category_autocomplete', request, response, CATEGORY_SEARCH);
        },
        select : function(event, ui) {
            event.preventDefault();
            $("#trainingResourceCategorySearchAutoComplete").val(ui.item.label);
            $("#trainingResourceCategorySearchAutoCompleteId").val(ui.item.value);
        }
    });

    /**
     * function to show the auto search results to the user.
     */
    $("#searchAutoComplete").autocomplete({
        minLength : 2,
        source : function(request, response) {
            getSearchRequest($("#searchAutoComplete").val(), 'autocomplete', request, response, RESOURCE_SEARCH);
        }
    });

    /**
     * Takes keyword entered by user for search and makes an ajax GET request to
     * specified URL.
     * 
     * @param searchVal
     *            string user enters to perform search(cannot be null or empty
     *            or blank or less than 2 characters)
     * @param searchUrl
     *            url for sending an ajax request.
     * @param request
     *            AJAX request object.
     * @param response
     *            AJAX response object.
     * @param type
     *            Type of the request for search(category or resource search).
     * @returns List of search results.
     */
    $("#categorySearchAutoComplete").autocomplete({
        minLength : 2,
        source : function(request, response) {
            getSearchRequest($("#categorySearchAutoComplete").val(), 'app/category_autocomplete', request, response, CATEGORY_SEARCH);
        },
        select : function(event, ui) {
            event.preventDefault();
            $("#categorySearchAutoComplete").val(ui.item.label);
            $("#categorySearchAutoCompleteId").val(ui.item.value);
        }
    });

});

$(document).on("click", "#nonchosenCategoryButton", function() {
    var receivedCategoryId = $(this).data("categoryid");
    var receivedCategoryName = $(this).data("categoryname");
    $("#interestedCategoryModal").modal('toggle');
    $("#category-name").text(receivedCategoryName);
    $("#categoryId").text(receivedCategoryId);
    $("#categoryId").val(receivedCategoryId);
});

$(document).on("click", "#submitCategory", function() {
    var categoryId = document.getElementById("categoryId").value;
    var skillLevel = $("#all_skill_levels").val();
    var interestLevel = $("#all_interest_levels").val();
    addCategorytoUserInterestedList(categoryId, skillLevel, interestLevel);
});

/**
 * Makes the AJAX GET call by sending the selected categoryID, skillLevel,
 * interestLevel to the user interested category modal based on the category
 * that the user wants to add.
 * 
 * @param categoryId
 *            The categoryId of the Category that appeared on the pop-up window,
 *            must be a positive integer
 * 
 * @param skillLevel
 *            the skill level that user possess for that particular category,
 *            must be a positive integer
 * 
 * @param interestLevel
 *            the level of interest a user possess for that particular category
 */
function addCategorytoUserInterestedList(categoryId, skillLevel, interestLevel) {
    var csrfToken = $('#_csrf_Token').attr("value");
    var csrfHeader = $('#_csrf_header').attr("value");
    var reqdata = {
        'categoryId' : categoryId,
        'skillLevel' : skillLevel,
        'interestLevel' : interestLevel
    };
    $.ajax({
        type : "GET",
        url : "/EvaluationFramework/app/addInterestedCategories",
        contentType : "application/json;charset=utf-8",
        dataType : "json",
        data : {
            categoryId : categoryId,
            skillLevel : skillLevel,
            interestLevel : interestLevel
        },
        beforeSend : function(xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success : function(response) {
            $('#interestedCategoryModal').modal('toggle');
            $('.modal-backdrop').remove();
            $('#' + categoryId).find("nonchosenCategoryButton").hide();
            $('#' + categoryId).text("Category Already Added");
        }
    });
}

/**
 * Assigns 'name' and 'id' values from object to label and value property of a
 * new object.
 * 
 * @param item
 *            Object passed to assign 'name' and 'id' to a new object.
 * @returns Object with {label, value} attributes.
 */
function dataValueCategory(item) {
    return {
        label : item.name,
        value : item.id
    }
}

/**
 * Assigns the String to label and value property of a new object.
 * 
 * @param item
 *            value assigned to a new object.
 * @returns Object with {label, value} attributes.
 */
function dataValueResource(item) {
    return {
        label : item.resourceName,
        value : item.resourceName
    }
}
