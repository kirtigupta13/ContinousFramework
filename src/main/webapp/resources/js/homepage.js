var CATEGORY_SEARCH = "category";
var RESOURCE_SEARCH = "resource";
/**
 * Gets the category ID from the selected category from the filter dropdown and
 * makes and AJAX GET call
 */
$(document).ready(function() {
    $("#addCategoryFilterButton").click(function() {
        var categoryId = $("#filterCategoryDropdown option:selected").val();
        categoryId = parseInt(categoryId);
        updateRecommendedResources(categoryId);
    });
});

/**
 * function to show the auto search results to the user.
 */
$(document).ready(function() {
    $("#searchAutoComplete").autocomplete({
        minLength : 2,
        source : function(request, response) {
            getSearchRequest($("#searchAutoComplete").val(), 'autocomplete', request, response, RESOURCE_SEARCH);
        }
    });
});

/**
 * Takes keyword entered by user for search and makes an ajax GET request to
 * specified URL.
 * 
 * @param searchVal
 *            string user enters to perform search(cannot be null or empty or
 *            blank or less than 2 characters)
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
function getSearchRequest(searchVal, searchUrl, request, response, type) {
    if (searchVal && searchVal.trim().length > 1) {
        $.ajax({
            url : searchUrl,
            type : "GET",
            data : {
                'search' : searchVal
            },
            success : function(data) {
                if (type == CATEGORY_SEARCH)
                    return response($.map(data, dataValueCategory));
                else
                    return response($.map(data, dataValueResource));
            }
        });
    }
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

/**
 * Makes the AJAX GET call by sending the selected category ID
 * 
 * @param category
 *            ID The categoryId of the Category that was selected from the
 *            filter dropdown
 */
function updateRecommendedResources(categoryId) {
    $.ajax({
        type : "GET",
        url : "/EvaluationFramework/app/filterRecommendedResources",
        contentType : "application/json;charset=utf-8",
        dataType : "json",
        data : {
            "id" : categoryId
        },
        success : function(response) {
            var newTableBody = $('#recommendedResourcesTable');
            clearTable();
            addRecommendedResources(response, newTableBody);
        }
    });
}

/**
 * Clears the table rows of the jQuery object recommendedResourcesTableBody
 */
function clearTable() {
    $('#recommendedResourcesTableBody tr').remove();
}

/**
 * Appends the updated list of resources to the table
 * 
 * @param response
 *            AJAX response object. The updated list of recommended resources
 *            for the selected category
 * @param newTableBody
 *            The jQuery object which contains the remaining table body after
 *            the table rows have been removed
 */
function addRecommendedResources(response, newTableBody) {
    for (var i = 0; i < response.length; i++) {
        newTableBody.append('<tr id="' + response[i].resource.resourceId + '"><td>' + response[i].resource.resourceName + '</td><td><a href="' + response[i].resource.resourceLink
                + '" target="_blank">' + response[i].resource.resourceLink + '</a></td><td>' + response[i].difficultyLevel + '</td><td>' + response[i].category.name
                + '</td><td><button id="completeButton" type="button" data-resourceid="' + response[i].resource.resourceId + '"' + 'data-resource="' + response[i].resource.resourceName + '"'
                + 'data-category="' + response[i].category.name + '"' + 'data-toggle="modal" data-target="#completeModal">'
                + '<fmt:message key="homepage.recommendedResources.filterByCategory" />Complete</button>' + '</td></tr>');
    }
}

/**
 * Function that retrieves resource name and category name to display on the
 * rating pop-up and resource-id to mark the completed resource as complete.
 */
$(document).on("click", "#completeButton", function() {
    var receivedCategoryName = $(this).data("category");
    var receivedResourceId = $(this).data("resourceid");
    var receivedResourceName = $(this).data("resource");
    $("#category-name").text(receivedCategoryName);
    $("#resource-name").text(receivedResourceName);
    $("#resource-id").val(receivedResourceId);
});

/**
 * Function that validates that user provides rating before submitting the page.
 */
$(document).on("click", "#saveSubmit", function(e) {
    e.preventDefault();
    if ($("input[type='radio'][name='rating']:checked").length > 0) {
        $('.form-horizontal').submit();
    } else {
        $(".hidden-error").show();
    }
});

$(document).ready(function() {
    var selectMenu = $("#filterCategoryDropdown");

    sortDropDownMenu(selectMenu);

    selectMenu.siblings("#addCategoryFilterButton").click(function() {
        var categoryID = selectMenu.val();
        if (categoryID && categoryID !== "-1") {
            addCategoryFilterToUI(categoryID, selectMenu.find("option:selected").text());
            removeCategoryFromDropDown(selectMenu, categoryID);
        }
    });

    $("#categoryFilterList").on("click", ".delete-filter", deleteFilterFromUIEvent);

    /**
     * On document ready, creates the jQuery datatable.
     * 
     * This table does not need paging, as only a maximum of 10 rows will be
     * visible to the user at any point of time.
     * 
     * Only filter(search) is used, so only that and the table are included in
     * the dom initialization.
     * 
     * Prevents the table from being sorted initially
     */
    $('#recommendedResourcesTable').dataTable({
        "paging" : false,
        "dom" : 'ft',
        "ordering" : false
    });
});

/**
 * Event handler for removing a category filter from the UI.
 */
function deleteFilterFromUIEvent() {
    var filterDiv = $(this).parent();
    var selectMenu = $("#filterCategoryDropdown");
    updateRecommendedResources(filterDiv.attr("data-categoryid"));
    addCategoryToDropDown(selectMenu, filterDiv.attr("data-categoryid"), filterDiv.text());
    filterDiv.remove();
    sortDropDownMenu(selectMenu);
    if (!$("#categoryFilterList .display-category-filter").length) {
        $("#noFilterSelected").show();
    }
}

/**
 * Displays the newly-selected filter on the UI.
 * 
 * @param categoryID
 *            The ID of the category to filter by. Must be a positive whole
 *            number (or a string representing the aforementioned)
 * @param categoryName
 *            The name of the category to filter by. Must be a string.
 */
function addCategoryFilterToUI(categoryID, categoryName) {
    var filterDiv = $("<div>").addClass("display-category-filter").attr("data-categoryid", categoryID);
    var innerHTML = "<span class='glyphicon glyphicon-remove delete-filter'></span>";
    innerHTML += "<span class='category-filter-text'>";
    innerHTML += categoryName + "</span>";
    $("#noFilterSelected").hide();
    filterDiv.html(innerHTML).appendTo("#categoryFilterList");
}

/**
 * Removes the category option with the specified ID from the specified drop
 * down list.
 * 
 * @param selectMenu
 *            The jQuery object for the select menu from which the category
 *            option will be removed.
 * @param categoryID
 *            The ID of the category object to remove. Must be a positive whole
 *            number (or a string representing the aforementioned)
 */
function removeCategoryFromDropDown(selectMenu, categoryID) {
    selectMenu.find("option[value='" + categoryID + "']").remove();
    selectMenu.val(-1);
}

/**
 * Adds a category to the drop down menu with the specified name and ID.
 * 
 * @param selectMenu
 *            The jQuery object for the 'select' item to which the new category
 *            option will be added.
 * @param categoryID
 *            The ID of the category to add to the list. Must be a positive
 *            whole number (or a string representing the aforementioned)
 * @param categoryName
 *            The name of the category, as a string.
 */
function addCategoryToDropDown(selectMenu, categoryID, categoryName) {
    $("<option>").val(categoryID).html(categoryName).appendTo(selectMenu);
}

/**
 * Sorts the dropdown menu alphabetically and sets it to the default selection.
 * 
 * @param selectMenu
 *            A jQuery object representing the HTML select element containing
 *            the option elements to be sorted.
 */
function sortDropDownMenu(selectMenu) {
    var defaultOption = selectMenu.find("option:first");
    selectMenu.html(selectMenu.find("option:not(:first)").sort(function(a, b) {
        var aText = a.text.trim().toLowerCase();
        var bText = b.text.trim().toLowerCase();
        return aText === bText ? 0 : aText < bText ? -1 : 1;
    }));
    selectMenu.prepend(defaultOption);
    selectMenu.val(-1);
}