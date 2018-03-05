<!DOCTYPE html>
<html>
    <!-- This page is the request_resource page. Here the user can request for resources that are not available from Administrators -->
    <head>
        <%@include file="common_includes.jsp"%>
        <!-- Page specific Javascript/CSS -->
        <script type="text/javascript" src="../resources/js/request_resource.js"></script> 
        <script type="text/javascript" src="../resources/js/datatable_drawcallback.js"></script>
        <script type="text/javascript" src="../resources/js/validate_request_resource.js"></script>          
        <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/profile.css" />">       
        <title><fmt:message key="resources.request" /></title>
    </head>
    <body>
        <div id="banner">
            <%@include file="page_header.jsp"%>
        </div>
        <div class="content">
            <c:if test="${successMessage != null}">
                <div class="alert alert-success">
                    <a href="#" class="close" data-dismiss="alert">&times;</a>
                    ${successMessage}
                </div>
            </c:if>
            <div id="errorMessageForCategoryName" class="alert alert-danger fade in" role="alert" hidden="true">
                <span >
                    <fmt:message key="resources.request.errorMessage.invalidCategoryName" />
                </span> 
            </div>  
            <div id="errorMessageForResourceName" class="alert alert-danger fade in" role="alert" hidden="true">
                <span >
                    <fmt:message key="resources.request.errorMessage.invalidResourceName" />
                </span> 
            </div>                
            <div id="content">
                <div id="page">
                    <table id="requestedResourcesTable" class="table table-striped table-hover">
                        <thead>
                            <tr>
                                <th data-orderable="false"></th>
                                <th scope="col"><fmt:message key="admin.manageRequests.category" /></th>
                                <th scope="col"><fmt:message key="admin.manageRequests.resource" /></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${listOfRequests}" var="eachRequest" varStatus="status">
                                <tr>
                                    <td><input type="checkbox" name="requestIds"/></td>
                                    <td>${eachRequest.categoryName}</td>
                                    <td>${eachRequest.resourceName}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <div class="center-block">
                        <fmt:message key="resources.request.submit" var="sendRequest" />
                        <input type="button" style="margin-right: 10px" class="btn btn-primary" value="New Request" data-toggle="modal" data-target="#myModal"/>
                        <fmt:message key="common.tableBody.delete" var="deleteAllBtn"></fmt:message>
                        <input type="submit" class="btn btn-primary" value="${deleteAllBtn}" />
                    </div>
                    <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <label><span class="modal-label"><fmt:message key="category.request.heading"/></span></label>
                                    <button type="button" class="glyphicon glyphicon-remove" data-dismiss="modal"></button>
                                </div>
                                <form:form name="requestResourceForm" modelAttribute="resourceRequest" action="resourceRequest" method="post" role="form">
                                    <div class="modal-body">
                                        <fieldset id="modal_form">
                                                <div class="form-group side_margin">
                                                    <label><fmt:message key="common.tableHeader.category" /></label>
                                                    <fmt:message key="resources.categoryMessage" var="categoryTabMessage" />
                                                    <input type="text" id="categoryName" name ="categoryName" class="form-control" placeholder="${categoryTabMessage}">
                                                </div>
                                                <div class="form-group side_margin">
                                                    <label><fmt:message key="homepage.resource" /></label>
                                                    <fmt:message key="resources.resourceMessage" var="resourceTabMessage" />
                                                    <input type="text" id="resourceName" name="resourceName" class="form-control" placeholder="${resourceTabMessage}">                      
                                                </div>
                                        </fieldset>
                                    </div>
                                    <div class="modal-footer">
                                        <fmt:message key="resources.request.submit" var="sendRequest" />
                                        <input type="submit" class="btn btn-primary" id="sendRequest" value="${sendRequest}" />
                                        <button type="button" class="btn btn-cancel" data-dismiss="modal"><fmt:message key="homepage.cancel"/></button>
                                    </div>
                                </form:form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div id="footNote">
            <%@include file="page_footer.jsp"%>
        </div>
    </body>
</html>