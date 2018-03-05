<div class="widget-wrapper">
    <div class="widget-header">
        <h2><fmt:message key="homepage.completedResources.widget.title" /></h2>
    </div>
    <div class="widget-body well">
        <c:if test="${completedResources.isEmpty() }">
            <div class="widget-message">
                <span>${completedResourceWidgetMessage }</span>
            </div>
        </c:if>
        <c:if test="${!completedResources.isEmpty() }">
            <table id="completedResourcesTable" class="table table-striped table-hover">
                <thead class="table-header">
                    <tr>
                        <th scope="col"><fmt:message key="common.tableHeader.name" /></th>
                        <th scope="col"><fmt:message key="common.tableHeader.completionRating" /></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${completedResources}" var="completeResource" varStatus="status">
                        <tr data-resourceId="${completeResource.resourceId}">
                            <td>${completeResource.resourceName}</td>
                            <td>${completeResource.getCompletedRating().toString()}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>    
    </div>
    <div class="widget-footer">
        <c:if test="${numberOfCompletedResources >= numberOfCompletedResourcesRequired }">
            <span><a href="${pageContext.request.contextPath}/app/completedResource"><fmt:message key="homepage.completedResources.widget.link" /></a></span>
        </c:if>
    </div>
</div>