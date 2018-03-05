$(document).ready(function() {
    /**
     * Set up the datatable when page is loaded.
     * 
     * Removes ability to change page length (default fixed at 10)
     * 
     * Orders table based on 2nd column (first is a checkbox)
     */
    $('#interestedCategoriesTable').dataTable({
        "drawCallback" : drawTableHidePaginationIfSinglePage,
        "dom" : "ftip",
        "order" : [ [ 1, "asc" ] ]
    });
});
