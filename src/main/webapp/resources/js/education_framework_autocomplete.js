/**
 * Short-hand for creating and returning a new Autocomplete object for the
 * following parameters:
 * 
 * @param jQueryTextarea
 *            A jQuery object containing the DOM element to become an
 *            autocomplete. Cannot be undefined.
 * @param searchURL
 *            The URL where the data is retrieved from, as a string. Cannot be
 *            blank, empty, or undefined.
 * @param dataValueFunction
 *            A function with a parameter of a JSON object which was returned by
 *            the AJAX call to the specified URL. This function must transform
 *            the JSON object into a new object with attributes 'value' and
 *            'label' so that it can be displayed in the autocomplete.
 * @returns a newly constructed Autocomplete object with the specified
 *          parameters (allows function chaining).
 * @throws Error
 *             When the first parameter is not a jQuery object, when the second
 *             parameter is not a string or is empty/blank, or when the third
 *             parameter is not a function.
 */
function createAutocomplete(jQueryTextarea, searchURL, dataValueFunction) {
    return new Autocomplete(jQueryTextarea, searchURL, dataValueFunction);
}

/**
 * Constructor for Autocomplete
 * 
 * @param jQueryTextarea
 *            A jQuery object containing the DOM element to become an
 *            autocomplete. Cannot be undefined.
 * @param searchURL
 *            The URL where the data is retrieved from, as a string. Cannot be
 *            blank, empty, or undefined. Must accept GET requests.
 * @param dataValueFunction
 *            A function with a parameter of a JSON object which was returned by
 *            the AJAX call to the specified URL. This function must transform
 *            the JSON object into a new object with attributes 'value' and
 *            'label' so that it can be displayed in the autocomplete.
 * @throws Error
 *             When the first parameter is not a jQuery object, when the second
 *             parameter is not a string or is empty/blank, or when the third
 *             parameter is not a function.
 */
function Autocomplete(jQueryTextarea, searchURL, dataValueFunction) {
    if (!(jQueryTextarea instanceof jQuery)) {
        throw new Error("The first parameter of the constructor must be a jQuery object.");
    }
    if (typeof searchURL !== "string") {
        throw new Error("The second parameter to the constructor must be a string.");
    }
    if (!searchURL.trim()) {
        throw new Error("The URL string cannot be empty or blank.");
    }
    if (typeof dataValueFunction !== "function") {
        throw new Error("The third parameter to the constructor must be a function.");
    }

    this.jQueryTextarea = jQueryTextarea;
    this.searchURL = searchURL;

    /**
     * A user-defined function which transforms a data object retrieved by the
     * AJAX call into an object with attributes 'label' and 'value' so that it
     * can be displayed on the UI. The callback should have the following
     * parameter:
     * 
     * @param data
     *            A JSON object with attributes varying depending on the types
     *            of data retrieved.
     */
    this.dataValueFunction = dataValueFunction;

    /**
     * Callback function for when an autocomplete entry is selected. If set to
     * null, nothing happens. A defined callback should have the following
     * parameters, in this order:
     * 
     * @param event
     *            The JavaScript event object from which cause the entry to
     *            become selected.
     * @param displayElement
     *            The object containing the data displayed in the autocomplete
     *            entry.
     */
    this.onSelectCallback = null;

    /**
     * Callback function for customizing the rendering of the list items in the
     * autocomplete. A defined callback should have the following parameters, in
     * this order:
     * 
     * @param jQueryUL
     *            The jQuery object for the ul item which will contain the
     *            autocomplete entries.
     * @param item
     *            Contains the data to be displayed in a single list item.
     * 
     * @returns a jQuery object for a new list item to be displayed in the
     *          autocomplete.
     */
    this.customItemRenderCallback = null;

    /**
     * Callback function for accessing and modifying data from the AJAX call
     * before it is displayed in the autocomplete. A defined callback should
     * have the following parameters, in this order:
     * 
     * @param data
     *            A JSON object containing all the data returned by the AJAX
     *            call.
     * @param searchValue
     *            The string which was used to search for that data.
     */
    this.accessDataCallback = null;
}

/**
 * Sets the function to be executed upon the selecting of an item in the
 * autocomplete list.
 * 
 * @param callback
 *            A function with parameters 'event' and 'displayElement', where
 *            'event' is the JavaScript event object which caused the entry to
 *            be selected, and 'displayElement' is an object containing the data
 *            displayed in the autocomplete entry.
 * @returns The autocomplete object the function was called on.
 * @throws Error
 *             When the callback is not a function.
 */
Autocomplete.prototype.onSelect = function(callback) {
    verifyArgumentIsFunction(callback);
    this.onSelectCallback = callback;
    return this;
};

/**
 * Sets the function to be executed to generate the HTML of a list item in the
 * autocomplete.
 * 
 * @param callback
 *            A function with parameters 'jQueryUL' and 'item', where 'jQueryUL'
 *            is the jQuery object for the <code>ul</code> element which will
 *            contain the autocomplete entries, and item is data to render an
 *            <code>li</code> element with.
 * @throws Error
 *             When the callback is not a function.
 */
Autocomplete.prototype.customItemRender = function(callback) {
    verifyArgumentIsFunction(callback);
    this.customItemRenderCallback = callback;
    return this;
}

/**
 * Sets a callback function which allows the user to access and modify the data
 * retrieved from the AJAX call before displaying it in the autocomplete.
 * 
 * @param callback
 *            A function with parameters 'data' and 'searchValue', where data is
 *            a JSON object containing all the data retrieved by the AJAX call
 *            and 'searchValue' is the string which was used to search for that
 *            data.
 * @returns the Autocomplete object the function was called on.
 * @throws Error
 *             When the callback is not a function.
 */
Autocomplete.prototype.accessData = function(callback) {
    verifyArgumentIsFunction(callback);
    this.accessDataCallback = callback;
    return this;
}

/**
 * Starts the autocomplete event listeners.
 * 
 * @returns The autocomplete object the function was called on.
 */
Autocomplete.prototype.render = function() {
    var self = this;
    var autocompleteParam = {};
    autocompleteParam.source = function(request, response) {
        getAutocompleteSearchRequest(response, self);
    }
    if (typeof this.onSelectCallback === "function") {
        autocompleteParam.select = this.onSelectCallback;
    }
    var autocomplete = this.jQueryTextarea.autocomplete(autocompleteParam).data("ui-autocomplete");
    if (typeof this.customItemRenderCallback === "function" && autocomplete) {
        autocomplete._renderItem = this.customItemRenderCallback;
    }
    return this;
};

/**
 * Takes keyword entered by user for search and makes an AJAX GET request to the
 * specified URL.
 * 
 * @param response
 *            AJAX response object.
 * @param autocomplete
 *            The <code>Autocomplete</code> object for which the AJAX call is
 *            being done.
 * @returns List of search results.
 */
function getAutocompleteSearchRequest(response, autocomplete) {
    var searchValue = autocomplete.jQueryTextarea.val().trim();
    if (searchValue && searchValue.length) {
        $.ajax({
            url : autocomplete.searchURL,
            type : "GET",
            data : {
                'search' : searchValue
            },
            success : function(data) {
                if (typeof autocomplete.accessDataCallback === "function") {
                    autocomplete.accessDataCallback(data, searchValue);
                }
                return response($.map(data, autocomplete.dataValueFunction));
            }
        });
    }
};

/**
 * Function which verifies whether or not an argument is a function.
 * 
 * @param argument
 *            The argument to check.
 * @throws Error
 *             When the argument is not of type 'function'.
 */
function verifyArgumentIsFunction(argument) {
    if (typeof argument !== "function") {
        throw new Error("Parameter must be of type 'function'.");
    }
}
