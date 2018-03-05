$(document).ready(function() {
    // on click of the add button
    $('#add_admin').click(function() {
        return (validateUserID('#addAdminId', '#errorMessageForID'));
    });

    // on select change of the authorization level dropdown.
    $('.select').change(function() {
        var userId = $(this).closest("tr").find("#userID").val();
        var optionSelected = $('#select' + userId + ' option:selected').val();
        changeAuthorizationLevelBasedOnUserId(optionSelected, userId);
    });

    // on click of remove button.
    $(".remove-admin-button").click(function() {
        var userId = $(this).data("userid");
        removeAdmin(userId);
    });
});

/**
 * remove admin status based on userId.
 * 
 * @param userId
 *            userId of the admin who needs to be demoted
 */
function removeAdmin(userId) {
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");

    $.ajax({
        type : "POST",
        url : "removeAdmin",
        dataType : "html",
        data : {
            userId : userId
        },
        beforeSend : function(xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success : function(response) {
            $("#" + userId).remove();
            $("#messages").html("Successfully removed admin.")
        },
        error : function(data) {
            $("#messages").html("Error removing the admin, please try again later.")
        }
    });
}

/**
 * Function that validates the UserID entered by the user.
 * 
 * @param elementId
 *            id of the input UserID
 * @param errorLabelId
 *            id of the displayed error message
 * @returns boolean true if UserID is not empty otherwise false.
 * 
 */
function validateUserID(elementId, errorLabelId) {
    if ($(elementId).val().trim() == '') {
        $(errorLabelId).show();
        return false
    } else {
        $(errorLabelId).hide();
        return true
    }
}

/**
 * Function that changes the authorization level based on the userId.
 * 
 * @param optionSelected
 *            selected option in the dropdown which represents the authorization
 *            Level that needs to be set.
 * @param userId
 *            unique userId of the user for whom the authorization needs to be
 *            changes.
 */
function changeAuthorizationLevelBasedOnUserId(optionSelected, userId) {
    if (optionSelected != 3) {
        $.ajax({
            type : "GET",
            url : "changeUserRole",
            dataType : "html",
            data : {
                "userId" : userId,
                "authLevel" : optionSelected
            },
            success : function(response) {
                $('#role' + userId).text($('#select' + userId + ' option:selected').text().toUpperCase());
            },
            error : function(data) {
                $("#messages").html("Error changing the user role, please try again later.");
            }
        });
    }
}