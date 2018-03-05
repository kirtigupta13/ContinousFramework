<!-- common includes.jsp must have been included in the page for the header to display properly -->
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@include file="common_includes.jsp"%>
<nav class="navbar logout_nav_header">
    <div class="container-fluid inherit_parent_height">
        <div class="navbar-header">
            <span class="navbar-brand">
                <a id="title" href="${pageContext.request.contextPath}/home">
                    <fmt:message key="common.title" />
                </a>
            </span>
        </div>
        <ul class="logout_items nav navbar-nav inherit_parent_height">
            <li class="inherit_parent_height vertical_align_parent">
                <div class="inherit_parent_height vertical_align_child">
                    <form:form action="searchResources" method="post">
                        <fmt:message key="common.resource.search" var="resourceSearch" />
                        <input type="text" id="searchAutoComplete" name="searchAutoComplete" class="form-control"
                            placeholder="${resourceSearch}" value="" />
                    </form:form>
                </div>
            </li>
            <li class="inherit_parent_height">
                <div class="col-lg-12 dropdown inherit_parent_height vertical_align_parent">
                    <span class="dropdown-toggle vertical_align_child">
                        ${pageContext.request.userPrincipal.name}
                        <span class="caret"></span>
                    </span>
                    <div class="dropdown-menu list-group">
                        <a href="${pageContext.request.contextPath}/app/categoriesForResources" class="list-group-item">
                            <fmt:message key="common.link.userResource" />
                        </a>
                        <a href="${pageContext.request.contextPath}/app/myProfile" class="list-group-item">
                            <fmt:message key="profile.myTitle" />
                        </a>
                        <a href="${pageContext.request.contextPath}/app/mySubscriptions" class="list-group-item">
                            <fmt:message key="subscriptionPage.dropDown.title" />
                        </a>
                        <a href="${pageContext.request.contextPath}/app/Show_add_resource" class="list-group-item">
                            <fmt:message key="common.link.manageResources" />
                        </a>
                        <a href="${pageContext.request.contextPath}/app/resourceRequest" class="list-group-item">
                            <fmt:message key="resources.request" />
                        </a>
                        <a href="${pageContext.request.contextPath}/app/completedResource" class="list-group-item">
                            <fmt:message key="common.link.completedResource" />
                        </a>
                    </div>
                </div>
            </li>
            <li class="inherit_parent_height">
                <c:if test="${sessionScope.role == 'admin'}">
                    <div class="col-lg-12 dropdown inherit_parent_height vertical_align_parent">
                        <span class="dropdown-toggle vertical_align_child">
                            <fmt:message key="label.admin" />
                            <span class="caret"></span>
                        </span>
                        <div class="dropdown-menu list-group">
                            <a href="${pageContext.request.contextPath}/app/show_add_category" class="list-group-item">
                                <fmt:message key="common.link.manageCategories" />
                            </a>
                            <a href="${pageContext.request.contextPath}/app/Show_add_resource" class="list-group-item">
                                <fmt:message key="common.link.manageResources" />
                            </a>
                            <a href="${pageContext.request.contextPath}/app/manage_admins" class="list-group-item">
                                <fmt:message key="common.link.manageAdmins" />
                            </a>
                            <a href="${pageContext.request.contextPath}/app/show_requests" class="list-group-item">
                                <fmt:message key="common.link.manageRequests" />
                            </a>
                            <a href="${pageContext.request.contextPath}/app/show_bulk_upload" class="list-group-item">
                                <fmt:message key="common.link.bulkUploadResources" />
                            </a>
                        </div>
                    </div>
                </c:if>
            </li>
            <li>
                <c:url value="/j_spring_security_logout" var="logoutUrl" />
                <form action="${logoutUrl}" method="post" id="logoutForm">
                    <fmt:message key="common.button.logout" var="logoutButtonText" />
                    <input class="btn" type="submit" value='${logoutButtonText}' />
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                </form>
            </li>
        </ul>
    </div>
</nav>