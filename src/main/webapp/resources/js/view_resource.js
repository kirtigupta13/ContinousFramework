$(document).ready(function() {
    $("#manageResourcesTabs").tabs();
    $('#viewAllResourcesTable').dataTable();
    
    $(".delete").click(function() {
        var resourceId = $(this).closest("tr").find(".resourceId").val();
        $.ajax({
            type : "GET",
            url : "delete/resource",
            dataType : "html",
            data : {
                "id" : resourceId
            },
            success : function(response) {
                if (response == "true") {
                    $("#" + resourceId).remove();
                } else {
                    alert("Error Deleting Resource");
                }
            }
        });
	});

    $(".update").addClass("hide");
    $(".edit").click(showUpdateHideEdit);
    
    /**
     * Makes an AJAX POST call to edit the resources. 
     */
    $(".update").click(function() {
        hideUpdateShowEditFunction($('.update'));
        var resourceId = $(this).closest("tr").find(".resourceId").val();
        var resourceName = $(this).closest("tr").find(".editResourceName").val();
        var resourceLink = $(this).closest("tr").find(".editResourceLink").val();
        resourceLink = resourceLink.replace(/\+/g, "%2B");
        var resourceDifficultyLevel = $(this).closest("tr").find(".editResourceLevel").val();
        var resourceType = $(this).closest("tr").find(".editResourceType").val();
        var resourceOwner = $(this).closest("tr").find(".editOwner").val();
        var token = $('#csrfToken').val();
        var header = $('#csrfHeader').val();
        
       $.ajax({
            type : "POST",
            url : "edit/resource?id="+resourceId+"&resourceName="+resourceName+"&resourceLink="+resourceLink+"&resourceDifficultyLevel="+resourceDifficultyLevel+"&resourceType="+resourceType+"&resourceOwner="+resourceOwner,
            contentType : "text/json",
            dataType : "text",
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success : function(response) {
                response = Boolean(response.bool);
                if (response != true) {
                    alert("Error Editing Resource");
                }
                else {
                    alert("Updated Successfully!");
                }
            }
        });
    });
});

/**
 * Helper function that enables the inputs of the selected row that are to be edited, hides the edit button and shows the update button
 */
function showUpdateHideEdit(){
    $(this).closest("tr").find(".editResource").attr('disabled', false); 
    $(this).closest("tr").find(".update").removeClass('hide');
    $(this).closest("tr").find(".edit").addClass('hide');
}

/**
 * Helper function that disables the inputs of the selected row that have been edited, hides the update button and shows the edit button 
 */
var hideUpdateShowEditFunction = function hideUpdateShowEdit(updateButton){
    $(updateButton).closest("tr").find(".edit").removeClass('hide');
    $(updateButton).closest("tr").find(".update").addClass('hide');
    $(updateButton).closest("tr").find(".editResource").attr('disabled', true);   
}