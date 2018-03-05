$(document).ready(function() {
    $("tr").click(function() {
        var courseId = $(this).closest("tr").find("#courseId").val();
        $.ajax({
            type : "GET",
            url : "course/description",
            dataType : "html",
            data : {
                "id" : courseId
            },
            success : function(response) {
                $('#courseDescription').html($('<b>' + response + '</b>'));
                $('#courseDescriptionModal').modal('show');
            }
        });
    });
})