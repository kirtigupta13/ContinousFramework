/**
 * Function that retrieves category name and category description to display
 * as a pop-up in modal after clicking category name and do datatable initialization.
 */
$(document).on("click", "#categoryButton", function() {
    var receivedCategoryName = $(this).data("category");
    var receivedCategoryDescription = $(this).data("description");
    $("#category-name-more").text(receivedCategoryName);
    $("#category-description").text(receivedCategoryDescription);
});

/**
 * On document ready, create the datatable
 */
$(document).ready(function() {
    // Set up the datatable when document is loaded
    $('#manageCategoryTable').dataTable({
        "drawCallback" : drawTableHidePaginationIfSinglePage,
        "order" : [ [ 1, "asc" ] ]
    });
});