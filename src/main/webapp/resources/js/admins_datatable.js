$(document).ready(function() {
    // create the jQuery DataTable
    $("#adminsTable").dataTable({
        "fnDrawCallback" : customizeAdminsTable,
        "lengthChange" : false
    });
});