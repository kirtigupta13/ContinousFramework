/**
 * Customize Datatable by:
 * <ul>
 * <li>Hiding pagination if only one page is needed</li>
 * </ul>
 * 
 * @param oSettings
 *            DataTable settings object. Cannot be null, undefined, or falsy.
 */
function drawTableHidePaginationIfSinglePage(settings) {
    if (settings) {
        // Calculates the number of pages that the datatable has (total records/
        // number displayed)
        if (1 >= (settings.fnRecordsDisplay() / settings._iDisplayLength)) {
            $(settings.nTableWrapper).find(".dataTables_paginate").hide();
        } else {
            $(settings.nTableWrapper).find(".dataTables_paginate").show();
        }
    }
}