/**
 * On document ready, create the datatable
 */
$(document).ready(function() {
    // Set up the datatable when document is loaded
    $('#completedResourcesTable').dataTable({
        "drawCallback" : drawTableHidePaginationIfSinglePage
    });
});