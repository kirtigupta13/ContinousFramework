/**
 * This function passes the category id and name from the profile page to the
 * edit user interested category modal based on the category that the user wants
 * to edit.
 */
$(document).on("click", ".editButton", function() {
    var receivedCategoryId = $(this).data('id');
    var receivedCategoryName = $(this).data('name');
    $("#categoryId").val(receivedCategoryId);
    $("#CategoryName").html(receivedCategoryName);
});

$(document).ready(function() {
    disableAddCategory();
    $('#categorySearchDropDown').click(disableAddCategory);
});

var disableAddCategory = function() {
    if ($('#categorySearchDropDown option:selected').val() == -1) {
        $('#addCategoryButton').prop('disabled', true);
    } else {
        $('#addCategoryButton').prop('disabled', false);
    }
}