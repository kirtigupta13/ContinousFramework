/**
 * Customize the #adminsTable Datatable by:
 * <ul>
 * <li>Hiding pagination if only one page is needed (<=10 results)</li>
 * <li>Moving the search bar to the left side</li>
 * </ul>
 * 
 * @param oSettings
 *            DataTable settings object. Cannot be null, undefined, or falsy.
 */
function customizeAdminsTable(oSettings) {
    if (oSettings) {
        if (10 >= oSettings.fnRecordsDisplay()) {
            $(oSettings.nTableWrapper).find('.dataTables_paginate').hide();
        }
        $("#adminsTable_filter").parent().removeClass();
        $("#adminsTable_filter").parent().addClass("col-sm-12");
    }
}