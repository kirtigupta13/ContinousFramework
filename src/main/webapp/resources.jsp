<!DOCTYPE html>
<%@ page import="com.cerner.devcenter.education.models.Resource"%>
<%@ page import="com.cerner.devcenter.education.models.ResourceType"%>
<%@ page import="com.cerner.devcenter.education.models.CategoryResourceForm"%>
<%@ page import="com.cerner.devcenter.education.models.Category"%>
<html>
    <!-- This page displays resources for a selected category -->
    <head>
        <%@include file="common_includes.jsp"%>
        <!-- Page specific Javascript/CSS -->
        <script type="text/javascript" src="../resources/js/resource_pagination.js"></script>
        <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/resources.css" />" />     
        <title><fmt:message key="showResources.title" /></title>
    </head>
    <body>
        <section id="container">
            <div id="banner">
                <%@include file="page_header.jsp"%>
            </div>
            <article id="content">
                <hr class="divider">
                    <article class="page">
                        <!-- Allows user to select a category to narrow down resources for viewing -->
                        <form:form modelAttribute="categoryResourceForm" action="Resources" method="get" id="select_category_form">
                            <div class="panel col-md-6 col-md-offset-3 panel-info search_resource_panel">
                                <div class="panel-heading">
                                    <span class="panel-title profile_details_header">
                                        <fmt:message key="searchResource.heading" />
                                    </span>
                                </div>
                                <div id="messages">${message}</div>
                                <div class="form-group side_margin">
                                    <label for="trainingResourceCategorySearchAutoComplete">
                                        <fmt:message key="addCategoryPage.label.categoryName"/>
                                    </label>
                                    <fmt:message key="common.resource.search" var="search"/>
                                    <form:input type="text" path="" id="trainingResourceCategorySearchAutoComplete"
                                    class="form-control col-xs-4" placeholder="${search}" value="" />
                                    <form:input type="hidden" path="selectedCategoryID"
                                    id="trainingResourceCategorySearchAutoCompleteId" class="form-control" />
                                </div>
                                <div class="form-group side_margin">
                                    <label for="resourcesperpage"><fmt:message key="common.resource.perpage"/></label>
                                    <form:select class="form-control" path="" id="resourcesperpage" name="resourcesperpage">
                                        <option value="5" <c:if test="${resourcesperpage==5}">selected="selected"</c:if>>5</option>
                                        <option value="10" <c:if test="${resourcesperpage==10}">selected="selected"</c:if>>10</option>
                                        <option value="15" <c:if test="${resourcesperpage==15}">selected="selected"</c:if>>15</option>
                                    </form:select>
                                </div>
                                <div class="form-group side_margin">
                                    <label for="all_types"><fmt:message key="addResourcePage.label.resourceType"/></label>
                                    <form:select class="form-control" path="selectedResourceTypeID" id="all_types">
                                        <option value="0"><fmt:message key="common.resourcetype.any"/></option>
                                        <form:options items="${resourceTypes}" itemValue="resourceTypeId" itemLabel="resourceType" />
                                    </form:select>
                                </div>
                                <div>
                                    <fmt:message key="common.link.search" var="buttonText" />
                                    <button type="submit" class="btn btn-primary center-block" id="addButton" name="pagenumber">${buttonText}</button>
                                </div>
                            </div>
                            <!-- Displays a list of resources  -->
                            <table class="table table-striped table-hover">
                                <thead class="table-header">
                                    <tr>
                                        <th scope="col"><fmt:message key="common.tableHeader.name" /></th>
                                        <th scope="col"><fmt:message key="common.tableHeader.link" /></th>
                                        <th scope="col"><fmt:message key="common.tableHeader.difficultyLevel" /></th>
                                        <th scope="col"><fmt:message key="common.tableHeader.type" /></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="resourcePossible" items="${resourceWithDifficulty}">
                                        <tr id="${resourcePossible.resourceId}">
                                            <td>${resourcePossible.resourceName}</td>
                                            <td><a href='${resourcePossible.resourceLink.toString()}' target="_blank">${resourcePossible.resourceLink.toString()}></a></td>
                                            <td>${resourcePossible.difficultyLevel}</td>
                                            <td>${resourcePossible.resourceType.getResourceType()}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                            <c:if test="${pagecount >1}">
                                <input id="pagecount" type="hidden" value="${pagecount}">
                                <table class="paginationtable">
                                    <tr>
                                        <td><input type="button" id="previouspage" value="&larr;"
                                            <c:if test="${pagenumber==1}">disabled="disabled"</c:if> onclick="changeToPreviousPage()"></td>
                                        <td><form:input type="submit" id="currentpage" name="pagenumber" path="" value="${pagenumber}"/></td>
                                        <td><input type="button" id="nextpage" value="&rarr;"
                                            <c:if test="${pagenumber==pagecount}">disabled="disabled"</c:if> onclick="changeToNextPage()"></td>
                                        <td>
                                            <select id="page_select" onchange="changePage(this.value)">
                                                <c:forEach begin="1" end="${pagecount}" varStatus="loop">
                                                    <option onclick="changePage(${loop.index})" value="${loop.index}">${loop.index}</option>
                                                </c:forEach>
                                            </select>
                                        </td>
                                    </tr>
                                </table>
                            </c:if>
                        </form:form>
                        <!--  Displays a warning message if no records -->
                        <div id="errorMessages">${errorMessage}</div>
                        <!-- Link to return to admin page -->
                    <div class="return">
                        <a href="${pageContext.request.contextPath}/app/home_page"> </a>
                    </div>
                </article>
            </article>
            <footer id="footNote">
                <%@include file="page_footer.jsp"%>
            </footer>
        </section>
    </body>
</html>