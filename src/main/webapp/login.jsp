<!DOCTYPE html>
<html>
    <!-- This page allows the user to login with their Cerner credentials -->
    <head>
        <%@include file="common_includes.jsp"%>
        <!-- Page specific Javascript/CSS -->    
        <link rel="stylesheet" href="http://yui.yahooapis.com/pure/0.5.0/pure-min.css">
        <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/login.css" />">        
        <title><fmt:message key="common.login" /></title>
    </head>
    <body>
        <div id="login-box">
            <h3>
                <fmt:message key="login.header.title" />
            </h3>
            <!-- This displays an error message to the user if the credentials given are not valid -->
            <c:if test="${not empty error}">
                <div class="banner banner-fail">${error}</div>
            </c:if>
            <!-- This displays a message to the user informing them that they have been logged out successfully -->
            <c:if test="${not empty login_message}">
                <div class="banner banner-success">${login_message}</div>
            </c:if>
                <form action="<c:url value='/j_spring_security_check' />" method='POST' class="pure-form pure-form-stacked">
                <input type='text' name='username' placeholder="<fmt:message key="common.label.userId" />" />
                <input type='password' name='password' placeholder="<fmt:message key="login.label.password" />" />
                <input name="submit" type="submit" value="<fmt:message key="common.login" />" class="pure-button pure-button-primary" />
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            </form>
        </div>
    </body>
</html>