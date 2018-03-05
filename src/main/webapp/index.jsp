<!DOCTYPE html>
<html>
    <!-- This page is the first to be displayed to the user when they launch the application.
        Serves as a welcome screen that directs them to the login page. -->
    <head>
        <%@include file="common_includes.jsp"%>
        <!-- Page specific Javascript/CSS -->
        <!-- BLUE STEEL style sheet -->
        <link rel="stylesheet" media="all" href="https://assets.devhealtheintent.com/blue_steel_hax/1.0.0.SNAPSHOT/assets/application-2c63634012b0041d88aefcffa5ab997b9157b7c1812008fa43b43fe2f2ff56bd.css">
        <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/index.css" />">
        <title><fmt:message key="common.title" /></title>
    </head>
    <body>
        <div class="head">
            <h1>
                <fmt:message key="common.title" />
            </h1>
        </div>
        <div class="welcomeBox">
            <div class="boxHead">
                <h2>
                    <fmt:message key="index.header.greetingTitle" />
                </h2>
            </div>
            <div class="boxBody">
                <p>
                    <fmt:message key="index.message" />
                </p>
                <div id="loginButton">
                    <a class="link" href="login">
                        <img src="https://www.cerner.com/shared/img/CernerAssociateLoginBadge.png">
                    </a>
                </div>
            </div>
        </div>
        <div id="footer">
            <%@include file="page_footer.jsp" %>
        </div>
    </body>
</html>
