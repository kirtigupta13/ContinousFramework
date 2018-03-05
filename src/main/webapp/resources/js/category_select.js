$(document).ready(
        function() {
            var categoriesSection = $("#categoriesSection");
            var categoryDescriptions = categoriesSection.find("#categoryDescriptions");
            var selectMenu = categoriesSection.find("#resourceCategories");

            sortDropDownMenu(selectMenu);
            selectMenu.val(-1);

            $.widget("custom.categorySelectMenu", $.ui.selectmenu, {
                _renderItem : function(ul, item) {
                    var li = $("<li>");
                    var wrapper = $("<div>");
                    if (item && item.label) {
                        $("<span>").addClass("category-name-dropdown").text(item.label.trim()).appendTo(wrapper);
                        $("<span>").addClass("category-description-dropdown").text(
                                getCategoryDescription(categoryDescriptions, item.value)).appendTo(wrapper);
                        if (item.disabled) {
                            li.addClass("ui-state-disabled");
                        }
                    }
                    return li.append(wrapper).appendTo(ul);
                }
            });

            selectMenu.categorySelectMenu();

            categoriesSection.find("#addCategoryButton").click(
                    function() {
                        var selectedCategory = $(this).parent().find("option:selected");
                        if (selectedCategory.val().trim() === "-1") {
                            return;
                        } else {
                            var categoryID = parseInt(selectedCategory.val(), 10)
                            addCategoryTableEntry(categoryID, selectedCategory.text().trim(), getCategoryDescription(
                                    categoryDescriptions, categoryID));
                            removeDropDownOption(selectMenu, categoryID);
                        }
                    });

            $("#topicTable").on("click", ".remove-category", function() {
                var parentTR = $(this).parent().parent();
                var categoryID = parentTR.find('span').attr("data-categoryid");
                var categoryName = parentTR.find("label").text().trim();
                // re-add the drop down option which was earlier removed.
                addDropDownOption(selectMenu, categoryID, categoryName);
                parentTR.remove();
                manageNoCategoriesMessage();
            });
        });

/**
 * This function decides whether or not the info message stating that at least
 * one category must be added should be showing or hidden. Based upon this
 * decision, the message is shown/hidden.
 */
function manageNoCategoriesMessage() {
    var topicTable = $("#topicTable");
    if (topicTable.find("tr").length > 1) {
        $("#infoMessageNoCategories").hide();
    } else {
        $("#infoMessageNoCategories").show();
    }
}

/**
 * Returns the category description for the given category ID. If the category
 * ID is invalid, the empty string is returned.
 * 
 * @param categoryDescriptions
 *            The jQuery object representing the container for the category
 *            descriptions.
 * @param categoryID
 *            The ID of the category. Must be an integer, or the string
 *            representation of an integer.
 * @returns The description for the category, as a string. If the description
 *          cannot be found, the empty string is returned.
 */
function getCategoryDescription(categoryDescriptions, categoryID) {
    return categoryDescriptions.find(".category-description-text[data-value='" + categoryID + "']").text().trim();
}

/**
 * Adds an item to the category drop down list.
 * 
 * @param selectMenu
 *            The jQuery object for the drop down menu.
 * @param categoryID
 *            The ID of the category to add to the list. Should be an integer or
 *            a string representation of an integer.
 * @param categoryName
 *            The name of the category to add to the drop-down. Should be a
 *            string.
 */
function addDropDownOption(selectMenu, categoryID, categoryName) {
    $("<option>").val(categoryID).text(categoryName).appendTo(selectMenu);
    sortDropDownMenu(selectMenu);
    selectMenu.categorySelectMenu("refresh");
}

/**
 * Removes the drop down option in the category drop-down with the specified
 * category ID.
 * 
 * @param selectMenu
 *            The jQuery object for the drop-down menu.
 * @param categoryID
 *            The category ID of the option to remove. Should be an integer or a
 *            string representation of an integer.
 */
function removeDropDownOption(selectMenu, categoryID) {
    selectMenu.find("option[value='" + categoryID + "']").remove();
    selectMenu.val(-1);
    selectMenu.categorySelectMenu("refresh");
}

/**
 * Sorts the specified drop down menu alphabetically.
 * 
 * @param selectMenu
 *            The jQuery object for the drop down container.
 */
function sortDropDownMenu(selectMenu) {
    var defaultOption = selectMenu.find("option:first");
    selectMenu.html(selectMenu.find("option:not(:first)").sort(function(a, b) {
        return a.text.toLowerCase() === b.text.toLowerCase() ? 0 : a.text.toLowerCase() < b.text.toLowerCase() ? -1 : 1;
    }));
    selectMenu.prepend(defaultOption);
}

/**
 * Adds an entry to the category table to note that the specified category has
 * been added to the resource.
 * 
 * @param categoryID
 *            The ID of the category which is added to the table (and therefore
 *            added to the resource). Should be an integer or a string
 *            representing an integer.
 * @param categoryName
 *            The name of the category. Should be a string.
 * @param categoryDescription
 *            A description for the category. Should be a string.
 */
function addCategoryTableEntry(categoryID, categoryName, categoryDescription) {
    var tableRow = $("<tr>").addClass("added-category");
    var tableCells = $();
    var CategoryIDKey = "resourceDifficultyForCategory['" + categoryID + "']";

    var categoryNameCell = $("<td>");
    $("<label>").attr("for", CategoryIDKey).html(categoryName).appendTo(categoryNameCell);
    $("<input />").addClass("form-control difficulty-level-for-category").attr("type", "hidden").attr("id", CategoryIDKey).attr("name", CategoryIDKey).appendTo(categoryNameCell);
    $("<span>").addClass("hidden category-id-holder").attr("data-categoryid", categoryID).appendTo(categoryNameCell);
    tableCells = tableCells.add(categoryNameCell);

    var categoryDescriptionCell = $("<td>");
    $("<div>").addClass("description").html(categoryDescription).appendTo(categoryDescriptionCell);
    tableCells = tableCells.add(categoryDescriptionCell);

    var categoryDifficultyCell = $("<td>");
    var selectDiv = $("<div>").addClass("dropdown text");
    // create a drop-down with values 1 to 5 so the user can select a difficulty
    var selectHTML = "<select class='form-control difficulty-selection'><span class='caret'></span>";
    for (var i = 1; i <= 5; i++) {
        selectHTML += "<option value='" + i + "'>" + i + "</option>";
    }
    selectHTML += "</select>";
    categoryDifficultyCell.append(selectDiv.html(selectHTML));
    tableCells = tableCells.add(categoryDifficultyCell);
    tableCells = tableCells.add($("<td><span class='glyphicon glyphicon-remove remove-category'></span></td>"));

    $("#topicTable").append(tableRow.append(tableCells));

    manageNoCategoriesMessage();
}
