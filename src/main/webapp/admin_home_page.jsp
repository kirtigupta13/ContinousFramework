<!DOCTYPE html>
<html>
    <!-- This page is the home page of the admin and contains links to add, delete, and view resources, categories, and courses. -->
    <head>
        <%@include file="common_includes.jsp"%>
        <!-- Page specific Javascript/CSS -->      
        <title><fmt:message key="adminHomePage.title" /></title>
    </head>
    <body>
        <div id="container">
            <div id="banner">
                <%@include file="page_header.jsp"%>
            </div>
            <div id="body">
                <ul>
                    <!-- Link to the add_resource page. -->
                    <li>
                        <a href=${pageContext.request.contextPath}/app/add_resource>
                            <fmt:message key="common.link.addResource" />
                        </a>
                    </li>
                    <!-- Link to the remove_resource page. -->
                    <li>
                        <a href=${pageContext.request.contextPath}/app/remove_resource>
                            <fmt:message key="common.link.removeResource" />
                        </a>
                    </li>
                    <!-- Link to the resources page. -->
                    <li>
                        <a href=${pageContext.request.contextPath}/app/resources>
                            <fmt:message key="common.link.viewResource" />
                        </a>
                    </li>
                    <!-- Link to the admin_course page. -->
                    <li>
                        <a href=${pageContext.request.contextPath}/app/admin_course>
                            <fmt:message key="common.trainingCourse" />
                        </a>
                    </li>
                    <!-- Link to the remove_course page. -->
                    <li>
                        <a href=${pageContext.request.contextPath}/app/remove_course>
                            <fmt:message key="adminHomePage.link.removeTrainingCourse"/>
                        </a>
                    </li>
                    <!-- Link to the remove_category page. -->
                    <li>
                        <a href=${pageContext.request.contextPath}/admin/remove_category>
                            <fmt:message key="common.link.removeCategory" />
                        </a>
                    </li><!-- Link to the add_remove_user page. -->
                    <li>
                        <a href=${pageContext.request.contextPath}/user/add_remove_user>
                            <fmt:message key="common.link.addRemoveUser" />
                        </a>
                    </li>
                    <!-- Link to the add_remove_admin page. -->
                    <li>
                        <a href="${pageContext.request.contextPath}/app/add_remove_admin">
                            <fmt:message key="admin.add_remove_admin" />
                        </a>
                    </li>
                    <!-- Link to the assessment_form page. -->
                    <li>
                        <a href="${pageContext.request.contextPath}/form/assessment_form">
                            <fmt:message key="adminHomePage.link.trainee" />
                        </a>
                    </li>
                </ul>
            </div>
            <div id="footer">
                <%@include file="page_footer.jsp"%>
            </div>
        </div>
    </body>
</html>