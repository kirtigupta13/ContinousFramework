<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.cerner.devcenter.education.models.Resource"%>
<%@ page import="com.cerner.devcenter.education.models.ResourceType"%>
<%@ page import="com.cerner.devcenter.education.models.Category"%>
<%@ page import="com.cerner.devcenter.education.models.Tag"%>
<html>
<!-- This is the page where the admin can add a resource. The details that are requested on this page
are the resource link, resource description and the categories the resource belongs to. -->
<head>
    <%@include file="common_includes.jsp"%>
    <script type="text/javascript" src="../resources/js/education_framework_autocomplete.js"></script>
    <script type="text/javascript" src="../resources/js/validate_resource.js"></script>
    <script type="text/javascript" src="../resources/js/add_resource_type.js"></script>
    <script type="text/javascript" src="../resources/js/tag_autocomplete.js"></script>
    <script type="text/javascript" src="../resources/js/category_select.js"></script>
    <script type="text/javascript" src="../resources/js/view_resource.js"></script>
    <script type="text/javascript" src="../resources/js/resource_pagination.js"></script>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/add_resource.css" />" />
    <title><fmt:message key="common.resource.title" /></title>
</head>
<body>
    <div id="container">
        <div id="banner">
            <%@include file="page_header.jsp"%>
        </div>
        <div id="content">
            <div class="panel panel-primary">
                <div id="manageResourcesTabs">
                    <ul>
                        <li>
                            <a href="#addResource">
                                <fmt:message key="common.link.addResource" />
                            </a>
                        </li>
                        <li>
                            <a href="#viewResources">
                                <fmt:message key="manageResource.manageResources.heading" />
                            </a>
                        </li>
                    </ul>
                    <div id="addResource" class="panel-body">
                        <div class="section-head">${message}</div>
                        <c:if test="${successMessage != null}">
                            <div id="successMessages" class="alert alert-success">${successMessage}</div>
                        </c:if>
                        <c:if test="${errorMessage != null}">
                            <div id="errorMessages" class="alert alert-danger">${errorMessage}</div>
                        </c:if>
                        <form:form name="myForm" modelAttribute="resource" action="Add_resource" method="post" role="form">
                            <div class="form-group">
                                <label for="resourceName">
                                    <fmt:message key="addResourcePage.label.resourceName" />
                                </label>
                                <form:input path="resourceName" id="resourceName" name="text-field" class="form-control"
                                    type="text" />
                                <span class="errorMessage" id="errorMessageForResourceName">
                                    <fmt:message key="addResourcePage.errorMessage.resourceNameInvalid" />
                                </span>
                            </div>
                            <div class="form-group">
                                <label for="resourceLink">
                                    <fmt:message key="addResourcePage.label.resourceLink" />
                                </label>
                                <form:input path="resourceLink" id="resourceLink" name="text-field" class="form-control"
                                    type="text" />
                                <span class="errorMessage" id="errorMessageForLink">
                                    <fmt:message key="addResourcePage.errorMessage.linkInvalid" />
                                </span>
                            </div>
                            <div class="form-group">
                                <label for="description">
                                    <fmt:message key="addResourcePage.label.description" />
                                </label>
                                <form:input type="text" path="description" id="description" name="text-field"
                                    class="form-control" />
                                <span class="errorMessage" id="errorMessageForDescription">
                                    <fmt:message key="common.errorMessage.descriptionInvalid" />
                                </span>
                            </div>
                            <div class="form-group">
                                <label for="tagAutocomplete">
                                    <fmt:message key="addResourcePage.label.tags" />
                                </label>
                                <input type="text" id="tagAutocomplete" class="form-control" />
                                <div id="currentTags"></div>
                                <span class="info-message hidden" id="errorMessageDuplicateTag">
                                    <span class="glyphicon glyphicon-remove remove-info-msg"></span>
                                    <fmt:message key="common.errorMessage.duplicateTag" />
                                </span>
                            </div>
                            <div class="dropdown">
                                <label for="resourceTypes">
                                    <fmt:message key="addResourcePage.label.resourceType" />
                                </label>
                                <form:select path="resourceType.resourceTypeId" id="resourceTypes" class="form-control">
                                    <span class="caret"></span>
                                    <c:forEach items="${resourceTypes}" var="resourceType">
                                        <option value="${resourceType.resourceTypeId}" />${resourceType.resourceType}
                                    </c:forEach>
                                </form:select>
                                <a href="#panels" data-toggle="collapse">
                                    <fmt:message key="addResourcePage.button.addType" />
                                </a>
                            </div>
                            <div class="panel panel-primary" id="panelSmall">
                                <div id="panels" class="collapse">
                                    <div class="panel-heading">
                                        <h6 class="panel-title">
                                            <fmt:message key="addResourcePage.panel.header.addType" />
                                        </h6>
                                    </div>
                                    <div class="panel-body">
                                        <div class="form-group">
                                            <label for="resourceTypeName">
                                                <fmt:message key="addResourceTypePage.label.resourceTypeName" />
                                            </label>
                                            <p>
                                                <input id="resourceTypeName" name="text-field" class="form-control" type="text" />
                                            </p>
                                            <input type="hidden" id="csrfToken" class="form-control" value="${_csrf.token}" />
                                            <input type="hidden" id="csrfHeader" class="form-control"
                                                value="${_csrf.headerName}" />
                                            <p>
                                                <button type="button" class="btn btn-regular btn-mini" id="addTypeButton">
                                                    <fmt:message key="addResourcePage.button.addResourceType" />
                                                </button>
                                            </p>
                                            <span class="errorMessage" id="errorMessageForType">
                                                <fmt:message key="common.errorMessage.typeInvalid" />
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="section-head">
                                <fmt:message key="addResourcePage.selectCategories.addCategoriesToResouce" />
                            </div>
                            <div class="dropdown" id="categoriesSection">
                                <label for="resourceCategories">
                                    <fmt:message key="addResourcePage.selectCategories.selectCategories" />
                                </label>
                                <form:select path="resourceType.resourceTypeId" id="resourceCategories" class="form-control">
                                    <span class="caret"></span>
                                    <option disabled selected value="-1">
                                        <fmt:message key="addResourcePage.selectCategories.selectCategoriesToAdd" />
                                    </option>
                                    <c:forEach items="${categories}" var="category">
                                        <option value="${category.id}">${category.name}</option>
                                    </c:forEach>
                                </form:select>
                                <div class="hidden" id="categoryDescriptions">
                                    <c:forEach items="${categories}" var="category">
                                        <span data-value="${category.id}" class="hidden category-description-text">${category.description}</span>
                                    </c:forEach>
                                </div>
                                <button type="button" class="btn btn-regular btn-mini" id="addCategoryButton">
                                    <fmt:message key="addResourcePage.selectCategories.addSelectedCategory" />
                                </button>
                            </div>
                            <div class="selectTopicTable">
                                <table class="table" id="topicTable">
                                    <tr>
                                        <th class='category-name-column'><fmt:message key="common.tableHeader.category" /></th>
                                        <th><fmt:message key="common.tableHeader.description" /></th>
                                        <th class='category-difficulty-column'><fmt:message
                                                key="common.tableHeader.difficulty" /></th>
                                        <th class='category-delete-column'><fmt:message key="common.button.delete" /></th>
                                    </tr>
                                </table>
                            </div>
                            <span class="info-message" id="infoMessageNoCategories">
                                <span class="glyphicon glyphicon-info-sign info-message"></span>
                                <fmt:message key="addResourcePage.selectCategories.selectAtLeastOne" />
                            </span>
                            <input type="submit" class="btn btn-secondary btn-lg btn-block"
                                value="<fmt:message key="addResourcePage.button.submit"/>" id="addButton" />
                        </form:form>
                    </div>
                    <div id="viewResources">
                        <table id="viewAllResourcesTable" class="table table-striped table-hover">
                            <thead class="table-header">
                                <tr>
                                    <th scope="col"><fmt:message key="common.tableHeader.name" /></th>
                                    <th scope="col"><fmt:message key="common.tableHeader.link" /></th>
                                    <th scope="col"><fmt:message key="common.tableHeader.difficultyLevel" /></th>
                                    <th scope="col"><fmt:message key="common.tableHeader.categoryName" /></th>
                                    <th scope="col"><fmt:message key="common.tableHeader.type" /></th>
                                    <th scope="col"><fmt:message key="common.tableHeader.averageRating" /></th>
                                    <th scope="col"><fmt:message key="common.tableHeader.resourceOwner" /></th>
                                    <fmt:message key="common.role.admin" var="roleAdmin" />
                                    <c:if test="${sessionScope.role == 'admin'}">
                                        <th scope="col"><fmt:message key="common.tableHeader.alterResource" /></th>
                                    </c:if>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="resourcePossible" items="${resourceWithDifficulty}">
                                    <tr id="${resourcePossible.resourceId}">
                                        <form:form action="view_resources" method="get" class="master_form">
                                            <form:form modelAttribute="resourceCategoryRelation" class="resource_form">
                                                <td><input class="editResource editResourceName" disabled="disabled"
                                                        value="${resourcePossible.resourceName}" /></td>
                                                <td><a href='${resourcePossible.resourceLink.toString()}' target="_blank">
                                                        <input class="editResource editResourceLink" disabled="disabled"
                                                            value="${resourcePossible.resourceLink.toString()}" />
                                                    </a></td>
                                                <td><input class="editResource editResourceLevel" disabled="disabled"
                                                        value="${resourcePossible.difficultyLevel}" /></td>
                                                <td><input id="trainingResourceCategorySearchAutoComplete"
                                                        disabled="disabled" value="${resourcePossible.categoryName}" /> <input
                                                        type="hidden" id="trainingResourceCategorySearchAutoCompleteId" /></td>
                                                <td><input class="editResource editResourceType" disabled="disabled"
                                                        value="${resourcePossible.resourceType.getResourceType()}" /> <input
                                                        type="hidden" id="csrfToken" class="form-control" value="${_csrf.token}" />
                                                    <input type="hidden" id="csrfHeader" class="form-control"
                                                        value="${_csrf.headerName}" /></td>
                                                <td><input id="editAverageRating" disabled="disabled"
                                                        value="${resourcePossible.averageRating}" /></td>
                                                <td><input class="editResource editOwner" disabled="disabled"
                                                        value="${resourcePossible.resourceOwner}" /></td>
                                                <c:if test="${sessionScope.role == 'admin'}">
                                                    <td>
                                                        <button id="editButton" class="edit" type="button">
                                                            <fmt:message key="common.tableBody.edit" />
                                                        </button>
                                                        <button id="updateButton" class="update" type="button">
                                                            <fmt:message key="common.tableBody.update" />
                                                        </button>
                                                        <button class="delete" type="button">
                                                            <fmt:message key="common.tableBody.delete" />
                                                        </button>
                                                    </td>
                                                    <td><input class="resourceId" type="hidden"
                                                            value="${resourcePossible.resourceId}" /></td>
                                                </c:if>
                                            </form:form>
                                        </form:form>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
        <div>
            <!-- This link redirects the page to the admin home to perform further operations -->
            <c:choose>
                <c:when test="${sessionScope.role == 'admin'}">
                    <div class="return">
                        <a href="${pageContext.request.contextPath}/app/home_page">
                            <fmt:message key="common.link.returnAdminHomePage" />
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/app/home_page">
                        <fmt:message key="common.link.returnHomePage" />
                    </a>
                    <br />
                </c:otherwise>
            </c:choose>
            <div id="footNote">
                <%@include file="page_footer.jsp"%>
            </div>
        </div>
    </div>
</body>
</html>