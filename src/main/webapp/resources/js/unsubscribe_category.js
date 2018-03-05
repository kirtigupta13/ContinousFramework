$(document).on("click", ".remove-subscription-button", function() {
    var categoryId = $(this).attr("data-categoryId");
    var categoryName = $(this).attr("data-categoryName");
    unsubscribeCategory(categoryId, categoryName);
});

/**
 * remove category from subscribed categories based on categoryId.
 * 
 * @param categoryId
 *            categoryId of the category which has to be removed from
 *            subscriptions
 */
function unsubscribeCategory(categoryId, categoryName) {
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");
    var reqdata = {
        'categoryId' : categoryId
    };

    $.ajax({
        type : "POST",
        url : "deleteSubscription",
        dataType : "html",
        data : {
            categoryId : categoryId
        },
        beforeSend : function(xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success : function(response) {
            $("#" + categoryName).remove();
            $("#successMessages").html("Successfully unsubscribed from category.")
        },
        error : function(data) {
            $("#errorMessages").html("Error unsubscribing from this category, please try again later.")
        }
    });
}