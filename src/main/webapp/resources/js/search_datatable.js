/**
 * On document ready, create the datatable
 */
$(document).ready(function() {
    // Set up the datatable when document is loaded
    $('table.datatable').dataTable({
        "drawCallback" : drawTableHidePaginationIfSinglePage
    });

});

