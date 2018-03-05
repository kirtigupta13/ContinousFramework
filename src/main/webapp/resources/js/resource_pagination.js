function changePage(pageNumber) {
    document.getElementById("currentpage").value = pageNumber;
    document.getElementById("currentpage").click();
}

function changeToPreviousPage() {
    var currentPageNumber = parseInt(document.getElementById("currentpage").value);
    changePage(--currentPageNumber);
}

function changeToNextPage() {
    var currentPageNumber = parseInt(document.getElementById("currentpage").value);
    changePage(++currentPageNumber);
}