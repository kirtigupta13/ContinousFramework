<!DOCTYPE html>
<html>
    <!-- This page is admin console page and contains links resources, categories, and courses. -->
    <head>
        <%@include file="common_includes.jsp"%>
        <!-- Page specific Javascript/CSS -->       
        <title><fmt:message key="adminConsole.title" /></title>
    </head>
    <body>
        <div id="container">
            <div id="banner">
                <%@include file="page_header.jsp"%>
            </div>
            <div id="content">
                <ul>
                    <!-- Link to the category page. -->
                    <li>
                        <a href=${pageContext.request.contextPath}/app/categories>
                            <fmt:message key="common.link.category" />
                        </a>
                    </li>
                    <!-- Link to the course page. -->
                    <li>
                        <a href=${pageContext.request.contextPath}/app/remove_course>
                            <fmt:message key="common.link.course" />
                        </a>
                    </li>
                    <!-- Links to the add/remove resource page. -->
                    <li>
                        <a href=${pageContext.request.contextPath}/app/Show_add_resource>
                            <fmt:message key="common.link.addResource" />
                        </a>
                        <a href=${pageContext.request.contextPath}/app/Remove_resource>
                            <fmt:message key="common.link.removeResource" />
                        </a>
                    </li>
                    <!-- Link to the add/remove user page. -->
                    <li>
                        <a href="${pageContext.request.contextPath}/user/add_remove_user">
                            <fmt:message key="adminConsole.addRemoveUser" />
                        </a>
                    </li>
                    <!-- Link to the assessment_form page. -->
                    <li>
                        <a href="${pageContext.request.contextPath}/form/assessment_form">
                            <fmt:message key="adminConsole.link.userStatistics" />
                        </a>
                    </li>
                </ul>
            </div>
            <div id="footNote">
                <%@include file="page_footer.jsp"%>
            </div>
        </div>
    </body>
</html>