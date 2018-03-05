/**
 * Function for showing the auto-complete results of the tag field.
 */
$(document).ready(
        function() {
            var autocomplete = createAutocomplete($("#tagAutocomplete"), "tagAutocomplete", dataValueTag).onSelect(
                    tagAutocompleteSelect).customItemRender(tagAutocompleteItemRender).accessData(tagRetrievalAJAXSuccess)
                    .render();

            // create listener for delete event on tag
            $("#currentTags").on("click", ".delete-tag", function() {
                $(this).parent().remove();
            });

            // create listener for delete event on duplicate tag error message
            $("#errorMessageDuplicateTag").find(".remove-info-msg").click(function() {
                $(this).parent().hide();
            });
        });

/**
 * Sees if the specified tag has already been added to this resource.
 * 
 * @param tagObject,
 *            with field tagObject.label
 * @returns true or false
 */
function isTagAlreadyAddedToResource(tagObject) {
    var currentTags = $("#currentTags .tag-text");
    var tagObjectText = tagObject.label.toLowerCase();
    for (var i = 0; i < currentTags.length; i++) {
        if ($(currentTags[i]).text().toLowerCase().trim() === tagObjectText) {
            return true;
        }
    }
    return false;
}

/**
 * Adds a Tag object to the UI.
 * 
 * @param tag -
 *            An object with properties tag.value (the ID), tag.label, and
 *            tag.isNew
 */
function addTagToUI(tag) {
    var tagDiv = $("<div>").addClass("display-tag");
    var innerHTML = "<span class='glyphicon glyphicon-remove delete-tag'></span>";
    innerHTML += "<span class='tag-text'>";
    innerHTML += encodeSpecialHTMLChars(tag.label);
    innerHTML += "</span>";
    tagDiv.html(innerHTML).appendTo("#currentTags");
}

/**
 * Encodes HTML special characters for proper display on the UI
 * 
 * @param str
 *            A string to be encoded
 * @returns an encoded String
 */
function encodeSpecialHTMLChars(str) {
    return $("<textarea/>").html(str).html();
}

/**
 * Used to map the tag JSON retrieved from the database to an object that can be
 * used by jQuery UI's autocomplete.
 * 
 * @param item
 *            Object passed with 'tagName' attribute.
 * @returns Object with {label} attribute so the data can be used by jQuery
 *          Autocomplete.
 */
function dataValueTag(item) {
    return {
        label : item.tagName
    }
}

/**
 * Function executed when an autocomplete entry is selected.
 * 
 * @param event
 *            The Javascript event object which caused the entry to become
 *            selected.
 * @param ui
 *            An object containing the data displayed on the UI.
 */
function tagAutocompleteSelect(event, ui) {
    if (ui && ui.item) {
        event.preventDefault();
        var tag = ui.item;
        // The label may include the HTML for the plus sign.
        // Remove that.
        if ($(tag.label).length) {
            tag.label = $(tag.label).text();
        }
        if (!isTagAlreadyAddedToResource(tag)) {
            addTagToUI(tag);
            $("#errorMessageDuplicateTag").hide();
        } else {
            $("#errorMessageDuplicateTag").show();
        }
        $("#tagAutocomplete").val("");
    }
}

/**
 * Function for rendering the HTML of each list item in the autocomplete.
 * 
 * @param jQueryUL
 *            The JQuery object representing the UL element that the list items
 *            are stored in.
 * @param item
 *            An object with attribute 'label'. The label is to be displayed in
 *            the UI.
 * @returns The jQuery object for the list item which was appended
 */
function tagAutocompleteItemRender(jQueryUL, item) {
    return $("<li></li>").data("item.autocomplete", item).append("<a>" + item.label + "</a>").appendTo(jQueryUL);
}

/**
 * Function for accessing the data retrieved from the AJAX call before it is
 * displayed in the autocomplete.
 * 
 * @param data
 *            the JSON tag data retrieved from the backend
 * @param searchVal
 *            the string which was used to retrive the autocomplete results
 * @return The modified data
 */
function tagRetrievalAJAXSuccess(data, searchVal) {
    // see if data has a tag whose name exactly matches search.
    var exactMatch = false;
    for (var i = 0; i < data.length; i++) {
        if (data[i].tagName.toLowerCase().trim() === searchVal.toLowerCase().trim()) {
            exactMatch = true;
        }
        data[i].tagName = encodeSpecialHTMLChars(data[i].tagName);
    }

    // If there isn't a match, allow them to add a new tag with the currently
    // typed name
    if (!exactMatch) {
        var labelHTML = "<span class='glyphicon glyphicon-plus new-autocomplete-icon'></span>";
        labelHTML += "<span class='new-item-autocomplete'>" + encodeSpecialHTMLChars(searchVal) + "</span>";
        data.push({
            tagName : labelHTML
        });
    }
    return data;
}