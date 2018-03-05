<!DOCTYPE html>
<%@ page import="com.cerner.devcenter.education.models.Resource"%>
<%@ page import="com.cerner.devcenter.education.models.Category"%>
<html ng-app="educationEvaluation">
    <!-- This page displays resources for a selected category -->
    <head>
        <%@include file="common_includes.jsp"%>
        <!-- Page specific Javascript/CSS -->
        <script src="../resources/js/lib/angular/angular.js"></script>
        <script src="../resources/js/lib/angular/angular-route.js"></script>
        <script src="../resources/js/lib/angular/angular-resource.js"></script>
        <script src="../resources/js/app.js"></script>
        <script src="../resources/js/services.js"></script>
        <script src="../resources/js/controllers/categoryControllers.js"></script>
        <script src="../resources/js/controllers/resourceControllers.js"></script>         
        <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/resources.css" />" />         
        <title><fmt:message key="showResources.title" /></title>
    </head>
    <body>
        <div id="container">
            <div id="banner">
                <%@include file="page_header.jsp"%>
            </div>
            <div id="content">
                <div id="page">
                    <!--Route to the partial content-->
                    <div ng-view></div>
                    <!--  Displays a warning message if no records -->
                    <div id="messages">${message}</div>
                    <!-- Link to return to admin page -->
                    <div id="return">
                        <a href="home_page"><fmt:message key="common.link.returnAdminHomePage" /></a>
                    </div>
                </div>
            </div>
            <div id="footNote">
                <%@include file="page_footer.jsp"%>
            </div>
        </div>
    </body>
</html>