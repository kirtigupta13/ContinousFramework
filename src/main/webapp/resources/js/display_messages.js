/**
 * Display or hide messages when page loads.
 */
$(document).ready(function() {
    displayMessages("#successMessages");
    displayMessages("#warningMessages");
    displayMessages("#errorMessages");
});

/**
 * Display the messages if they contain content. Hide them otherwise.
 * @param elementID
 *          jquery selector for the element to show/hide
 */
function displayMessages(elementSelector){
    if ($(elementSelector).html()) {
        $(elementSelector).show();
    } else {
        $(elementSelector).hide();
    }
}