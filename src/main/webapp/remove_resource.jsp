<!DOCTYPE html>
<%@ page import="com.cerner.devcenter.education.models.Resource"%>
<%@ page import="com.cerner.devcenter.education.models.Category"%>
<html>
    <!-- This page displays resources for a selected category and allows user to remove a resource-->
    <head>
        <%@include file="common_includes.jsp"%>
        <!-- Page specific Javascript/CSS -->     
        <title><fmt:message key="removeResource.title" /></title>
    </head>
    <body>
        <div id="container">
            <div id="banner">
                <%@include file="page_header.jsp"%>
            </div>
            <div id="content">
                <h3>
                    <fmt:message key="common.link.removeResource" />
                </h3>
                <div id="page">
                    <!-- Allows user to select a category to narrow down resources for removing -->
                    <form:form modelAttribute="category" action="Remove_resource" method="get" id="select_category_form">
                        <p class="field-select">
                            <label for="select2-columned">Categories</label>
                            <form:select class="blue-steel-select2" path="id" id="all_categories">
                                <form:options items="${categories}" itemValue="id" itemLabel="name" />  
                            </form:select>
                        </p>
                        <p class="form-actions">
                            <button class="btn-primary" type="submit" id="select_category_submit">
                                <fmt:message key="common.button.select" />
                            </button>
                        </p>
                    </form:form>
                    <!--  Displays a warning message if no records -->
                    <div id="messages">
                        ${message}
                    </div>
                    <!-- Displays list of resources along with a button for removing each resource -->
                    <table class="table-base table-striped table-hover">
                        <thead class="table-header">
                            <tr>
                                <th scope="col"><fmt:message key="common.tableHeader.name" /></th>
                                <th scope="col"><fmt:message key="common.tableHeader.link" /></th>
                                <th scope="col"><fmt:message key="common.tableHeader.skillLevel" /></th>
                                <th scope="col"><fmt:message key="common.tableHeader.remove" /></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="resourcePossible" items="${resourcesAvailableToDelete}">
                                <tr>
                                    <form:form action="remove_resource" method="post" class="master_form">
                                        <form:form modelAttribute="category" class="category_id_form">
                                            <form:hidden path="id" value="${category.getId()}" />
                                        </form:form>
                                        <form:form modelAttribute="resource" class="resource_form">
                                            <td><label>${resourcePossible.resourceName}</label></td>
                                            <td><label>${resourcePossible.resourceLink.toString()}</label></td>
                                            <td><label>${resourcePossible.requiredSkillLevel}</label></td>
                                            <td>
                                                <form:hidden path="resourceID" value="${resourcePossible.resourceID}" />
                                                <form:hidden path="resourceLink" value="${resourcePossible.resourceLink.toString()}" />
                                                <form:hidden path="description" value="${resourcePossible.description}" />
                                                <input id="submit_button" type="submit" value="Remove">
                                            </td>
                                        </form:form>
                                    </form:form>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <!-- Link to return to admin console page -->
                    <c:choose>
                        <c:when test="${sessionScope.role == 'admin'}">
                            <div id="return">
                                <a href="${pageContext.request.contextPath}/app/home_page">
                                    <fmt:message key="common.link.returnAdminHomePage" />
                                </a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/app/home_page"> 
                                <fmt:message key="common.link.returnHomePage" />
                            </a>
                            <br/>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div id="footNote">
                <%@include file="page_footer.jsp"%>
            </div>
        </div>
    </body>
</html>