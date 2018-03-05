<!DOCTYPE html>
<%@ page import="com.cerner.devcenter.education.models.Category"%>
<html>
    <head>
        <%@include file="common_includes.jsp"%>
        <!-- Page specific Javascript/CSS -->
        <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/admin_course.css" />" />      
        <title><fmt:message key="adminCourse.title" /></title>        
    </head>
    <body>
        <div id="container">
            <div id="banner">
                <%@include file="page_header.jsp"%>
            </div>
            <div id="content">
                <form:form modelAttribute="category" action="admin_course" id="select_category_form">
                    <p class="field field-select">
                        <label for="select2-columned">Categories</label>
                        <form:select class="blue-steel-select2" path="id" id="all_categories">
                            <form:options items="${categories}" itemValue="id" itemLabel="name" />
                        </form:select>
                    </p>
                    <p class="form-actions">
                        <button class="btn btn-primary" type="submit" id="select_category_submit">
                            <fmt:message key="common.button.select" />
                        </button>
                    </p>
                </form:form>
                <div id="messages">${message}</div>
                <table class="table-base table-striped table-hover">
                    <thead>
                        <tr class="table-header">
                            <th scope="col"><fmt:message key="common.tableHeader.courseName" /></th>
                            <th scope="col"><fmt:message key="common.tableHeader.courseCampus" /></th>
                            <th scope="col"><fmt:message key="common.tableHeader.startDate" /></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="courseList" items="${course}">
                            <tr>
                                <form:form action="admin_course" method="post" class="master_form">
                                    <form:form modelAttribute="category" class="category_id_form">
                                        <form:hidden path="id" value="${category.getId()}" />
                                    </form:form>
                                    <form:form modelAttribute="course" class="course_form">
                                        <td><label>${courseList.getCourseName()}</label></td>
                                        <td><label>${courseList.getCourseCampus()}</label></td>
                                        <td><label>${courseList.getCourseStartDate().toString("yyyy/MM/dd")}</label></td>
                                    </form:form>
                                </form:form>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <form action="add_course">
                    <button class="btn btn-primary" type="submit" id="select_course_submit">
                        <fmt:message key="common.button.addCourse" />
                    </button>
                </form>
                <!-- Link to return to admin Console page -->
                <p id="return">
                    <a href="admin_home"><fmt:message key="common.link.returnAdminHomePage" /></a>
                </p>
            </div>
            <div id="footNote">
                <%@include file="page_footer.jsp"%>
            </div>
        </div>
    </body>
</html>
