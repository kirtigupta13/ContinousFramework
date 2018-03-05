$(document).ready(function() {
    $("tr").click(function() {
        var resourceId = $(this).closest("tr").find("#resourceID").val();
        $.ajax({
            type : "GET",
            url : "resource/description",
            dataType : "html",
            data : {
                "id" : resourceId
            },
            success : function(response) {
                $('#resourceDescription').html($('<b>' + response + '</b>'));
                $('#resourceDescriptionModal').modal('show');
            }
        });
    });
});
