/**
 * Function to validate and add the resource type entered by the user.
 */
$(document).ready(function() {
    $(".addType").click(function() {
        $("#panel").toggle();     
    });
    
    $("#addTypeButton").click(function() {
        var token = $('#csrfToken').val();
        var header = $('#csrfHeader').val();
        if ($('#resourceTypeName').val().trim() == '' || !isValidResourceType($('#resourceTypeName').val())) {
            $('#errorMessageForType').show();
        } else {
            $('#errorMessageForType').hide();
            addResourceType($("#resourceTypeName").val(), token, header);
        }
        return false
    });
});

/**
 * Function that takes the user entered resource type to add to the database.
 * Shows success message if input to db was successful else shows error message.
 * @param typeName 
 *              String as a resource type entered by the user
 */
function addResourceType(typeName, token, header){
    $.ajax({
        type : "POST",
        contentType : "text/json",
        url : "addResourceType",
        data : JSON.stringify(typeName),
        dataType : "json",
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success : function(response){
            var dataObject = response;
            if((dataObject.resourceType) != null){
                $("#resourceTypes").append($("<option></option>")
                        .attr("value",dataObject.resourceTypeId)
                        .text(dataObject.resourceType));
                $("#successMessages").show().html("New Resource Type successfully added");
                $("#errorMessageForType").hide();
                $("#errorMessages").hide();
            }else{
                $("#errorMessages").show().html("Resource Type already exists");
                $("#successMessages").hide();
            }
        }
    });
}

/**
 * Function that validates the resource type entered by the user.
 * @param resourceType
 *          String as a resource type entered by the user
 * @returns boolean
 *          true if resource type is valid otherwise false.
 */
function isValidResourceType(resourceType){
    return resourceType.match("^[a-zA-Z ]+$");
}
