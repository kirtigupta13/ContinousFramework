<!DOCTYPE html>
<html>
    <!-- Page to Manage the requests-->
    <head>
        <%@include file="common_includes.jsp"%>
        <!-- Page specific Javascript/CSS -->    
        <script type="text/javascript" src="../resources/js/validate_manage_requests.js"></script>
        <title><fmt:message key="common.link.manageRequests" /></title>
    </head>
    <body>
        <div id="container" class="content">
            <div id="banner">
                <%@include file="page_header.jsp"%>
            </div>
            <c:if test="${successMessage != null}">
                <div class="alert alert-success">
                    <a href="#" class="close" data-dismiss="alert">&times;</a>
                    ${successMessage}
                </div>
            </c:if>
            <form:form action="delete_requests" method="post">
                <table class="table table-striped table-hover">
                    <thead>
                        <tr>
                            <th></th>
                            <th scope="col"><fmt:message key="admin.manageRequests.userId" /></th>
                            <th scope="col"><fmt:message key="admin.manageRequests.category" /></th>
                            <th scope="col"><fmt:message key="admin.manageRequests.resource" /></th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${listOfRequests}" var="eachRequest" varStatus="status">
                            <tr>
                                <td><input type="checkbox" name="requestIds" value="${eachRequest.id}" /></td>
                                <td>${eachRequest.userId}</td>
                                <td>${eachRequest.categoryName}</td>
                                <td>${eachRequest.resourceName}</td>
                                <td>
                                    <button class='editButton' type="button" >
                                        <fmt:message key="common.tableBody.approve" />
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <div>
                    <fmt:message key="common.tableBody.delete" var="deleteAllBtn"></fmt:message>
                    <input type="submit" class="btn btn-primary center-block" id ="deleteAllBtn" value="${deleteAllBtn}" />
                </div>
            </form:form>
        </div>
        <div id="footNote">
            <%@include file="page_footer.jsp"%>
        </div>
    </body>
</html>