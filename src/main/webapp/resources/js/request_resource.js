/**
 * On document ready, create the datatable
 */
$(document).ready(function() {
    // Set up the datatable when document is loaded
    $('#requestedResourcesTable').dataTable({
        "drawCallback" : drawTableHidePaginationIfSinglePage,
        "order" : [ [ 1, "asc" ] ]
    });
});