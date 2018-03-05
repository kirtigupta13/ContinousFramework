<!DOCTYPE html>
<html>
    <!-- This page displays information about the resources user has completed such as resource name,
        resource link and completion rating. -->
    <head>
        <%@include file="common_includes.jsp"%>
        <!-- Page specific Javascript/CSS -->
        <script type="text/javascript" src="/EvaluationFramework/resources/js/completed_resource.js"></script>
        <script type="text/javascript" src="/EvaluationFramework/resources/js/datatable_drawcallback.js"></script>
        <title><fmt:message key="common.link.completedResource" /></title>
    </head>
    <body>
        <div id="banner">
            <%@include file="page_header.jsp"%>
        </div>
        <div class="content">        
            <!-- Displays a list of completed resources by the user -->
            <table id="completedResourcesTable" class="table table-striped table-hover">
                <thead class="table-header">
                    <tr>
                        <th scope="col"><fmt:message key="common.tableHeader.name" /></th>
                        <th scope="col"><fmt:message key="common.tableHeader.link" /></th>
                        <th scope="col"><fmt:message key="common.tableHeader.completionRating" /></th>
                        <th scope="col"><fmt:message key="common.tableHeader.completionDate" /></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${completedResources}" var="completeResource" varStatus="status">
                        <tr data-resourceId="${completeResource.getResourceId()}">
                            <td>${completeResource.getResourceName()}</td>
                            <td><a href='${completeResource.getResourceLink().toString()}' target="_blank">${completeResource.getResourceLink().toString()}</a></td>
                            <td>${completeResource.getCompletedRating().toString()}</td>
                            <td>${completeResource.getFormattedDate()}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <!--  Displays a warning message if no records -->
            <div class="success-messages" id="messages">${message}</div>
        </div>
        <div id="footNote">
            <%@include file="page_footer.jsp"%>
        </div>
    </body>
</html>
