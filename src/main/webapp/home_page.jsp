<!DOCTYPE html>
<%@ page import="com.cerner.devcenter.education.models.UserRecommendedResource"%>
<%@ page import="com.cerner.devcenter.education.models.Resource"%>
<html>
<!-- This page is the common home page for admin and users contains links to view resources, courses, user statistics. -->
<head>
    <%@include file="common_includes.jsp"%>
    <!-- Page specific Javascript/CSS -->
    <script type="text/javascript" src="/EvaluationFramework/resources/js/homepage.js"></script>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/home_page.css" />" />
    <title><fmt:message key="homepage.title" /></title>
</head>
<body>
    <div id="banner">
        <%@include file="page_header.jsp"%>
    </div>
    <div class="content container-fluid">
        <div class="col-xs-4">
            <div class="row">
                <%@include file="general_details_widget.jsp"%>
            </div>
            <div class="row">
                <%@include file="completed_resources_widget.jsp"%>
            </div>
        </div>
        <div class="col-xs-8">
            <div class="row">
                <!-- Displays a list of recommended resources  -->
                <h1>
                    <fmt:message key="common.recommended.resource" />
                </h1>
                <div class="panel-group">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h4 class="panel-title">
                                <span class='glyphicon glyphicon-chevron-down'></span>
                                <a data-toggle="collapse" href="#categoryFilterPanel">
                                    <fmt:message key="homepage.recommendedResources.filterByCategory" />
                                </a>
                            </h4>
                        </div>
                        <div id="categoryFilterPanel" class="panel-collapse collapse">
                            <div class="panel-body">
                                <div class="form-group">
                                    <label for="filterCategoryDropdown">
                                        <fmt:message key="common.link.category" />
                                    </label>
                                    <select id="filterCategoryDropdown" class="form-control">
                                        <option value="-1" disabled><fmt:message
                                                key="homepage.recommendedResources.selectACategory" /></option>
                                        <c:forEach items="${allCategories}" var="eachCategory">
                                            <option value="${eachCategory.id}">${eachCategory.name}</option>
                                        </c:forEach>
                                    </select>
                                    <button class="btn" id="addCategoryFilterButton">
                                        <fmt:message key="homepage.recommendedResources.addFilter" />
                                    </button>
                                </div>
                                <div class="form-group">
                                    <label>
                                        <fmt:message key="homepage.recommendedResources.selectedCategories" />
                                    </label>
                                    <div id="categoryFilterList">
                                        <span id="noFilterSelected">
                                            <fmt:message key="homepage.recommendedResources.noFiltersSelected" />
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <table id="recommendedResourcesTable" class="table table-striped table-hover">
                    <thead class="table-header">
                        <tr>
                            <th scope="col"><fmt:message key="common.tableHeader.name" /></th>
                            <th scope="col"><fmt:message key="common.tableHeader.link" /></th>
                            <th scope="col"><fmt:message key="common.tableHeader.difficultyLevel" /></th>
                            <th scope="col"><fmt:message key="common.tableHeader.categoryName" /></th>
                            <th data-orderable="false"></th>
                        </tr>
                    </thead>
                    <tbody id="recommendedResourcesTableBody">
                        <c:forEach items="${recommendedResources}" var="eachResource" varStatus="status">
                            <tr id="${eachResource.resource.resourceId}">
                                <td>${eachResource.resource.resourceName}</td>
                                <td><a href='${eachResource.resource.resourceLink.toString()}' target="_blank">${eachResource.resource.resourceLink.toString()}</a></td>
                                <td>${eachResource.difficultyLevel}</td>
                                <td>${eachResource.category.name}</td>
                                <td>
                                    <button id='completeButton' type="button"
                                        data-resourceid="${eachResource.resource.resourceId}"
                                        data-resource="${eachResource.resource.resourceName}"
                                        data-category="${eachResource.category.name}" data-toggle="modal"
                                        data-target="#completeModal">
                                        <fmt:message key="homepage.button.complete" />
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <!--  Displays a warning message if no records -->
                <div class="success-messages" id="messages">${message}</div>
            </div>
        </div>
    </div>
    <div class="modal fade" id="completeModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <label>
                        <span class="modal-label">
                            <fmt:message key="homepage.rating" />
                        </span>
                    </label>
                    <button type="button" class="glyphicon glyphicon-remove" data-dismiss="modal"></button>
                </div>
                <form class="form-horizontal" method="post" action="completeResource?${_csrf.parameterName}=${_csrf.token}">
                    <div class="modal-body">
                        <fieldset id="modal_form">
                            <span class="hidden-error">
                                <fmt:message key="homepage.rating.unchecked" />
                            </span>
                            <label>
                                <fmt:message key="homepage.category" />
                            </label>
                            <span class="category-name" id="category-name"></span>
                            <label>
                                <fmt:message key="homepage.resource" />
                            </label>
                            <span id="resource-name"></span>
                            <input type="hidden" name="resource-id" id="resource-id" value="" />
                            <ul class="rating-list">
                                <li>
                                    <input class="added-margin" type="radio" name="rating" value="0">
                                    <fmt:message key="homepage.rating.extremely_dissatisfied" />
                                </li>
                                <li>
                                    <input class="added-margin" type="radio" name="rating" value="1">
                                    <fmt:message key="homepage.rating.dissatisfied" />
                                </li>
                                <li>
                                    <input class="added-margin" type="radio" name="rating" value="2">
                                    <fmt:message key="homepage.rating.neutral" />
                                </li>
                                <li>
                                    <input class="added-margin" type="radio" name="rating" value="3">
                                    <fmt:message key="homepage.rating.satisfied" />
                                </li>
                                <li>
                                    <input class="added-margin" type="radio" name="rating" value="4">
                                    <fmt:message key="homepage.rating.extremely_satisfied" />
                                </li>
                            </ul>
                        </fieldset>
                    </div>
                    <div class="modal-footer">
                        <div id="feedBackButtons">
                            <button id="saveSubmit" type="submit" class="btn btn-primary">
                                <fmt:message key="common.button.submit" />
                            </button>
                            <button id="cancelButton" type="button" class="btn btn-cancel" data-dismiss="modal">
                                <fmt:message key="homepage.cancel" />
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <div id="footNote">
        <%@include file="page_footer.jsp"%>
    </div>
</body>
</html>