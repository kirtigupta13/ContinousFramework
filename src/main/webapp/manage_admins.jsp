<!DOCTYPE html>
<html>
    <!-- Page to add admin or remove any existing administrator -->
    <head>
        <%@include file="common_includes.jsp"%>
        <!-- Page specific Javascript/CSS -->
        <script type="text/javascript" src="../resources/js/admins_datatable.js"></script> 
        <script type="text/javascript" src="../resources/js/admins_datatable_drawcallback.js"></script> 
        <script type="text/javascript" src="../resources/js/validate_add_admin.js"></script>        
        <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/add_remove_admin.css" />" />        
        <title><fmt:message key="addRemoveUser.title" /></title>
    </head>
    <body>
        <div id="container"> 
            <div id="banner">
                <%@include file="page_header.jsp"%>
            </div>
            <div class="content">
                <div class="success-messages">${message}</div>
                <div class="panel panel-info col-md-4">
                    <div class="panel-heading">
                        <fmt:message key="admin.addAdmin.header" />
                    </div>
                    <div class="panel-body">
                        <form action="addAdmin" method="POST" class="form">
                            <div class="form-group">
                                <label for="userID"><fmt:message key="user.userId" /></label> 
                                <input type="text" name="userId" class="form-control" id="addAdminId"/>
                            </div>
                            <div class="form-group">
                                <button class="btn btn-primary center-block" type="submit" id="add_admin">
                                    <fmt:message key="common.link.adminPage.button" />
                                </button>
                            </div>
                            <span class="errorMessage" id="errorMessageForID"> 
                                <fmt:message key="addRemoveUser.validCheckId" />
                            </span>
                            <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden" />
                        </form>
                    </div>
                </div>
                <div>
                    <table id="adminsTable" class="table table-striped table-hover">
                        <thead>
                            <tr>
                                <th scope="col"><fmt:message key="user.userId" /></th>
                                <th scope="col"><fmt:message key="user.firstName" /></th>
                                <th scope="col"><fmt:message key="user.lastName" /></th>
                                <th data-orderable="false"></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="availableuser" items="${available_users}">
                                <tr id="${availableuser.userID}">
                                    <td>${availableuser.userID}</td>
                                    <td>${availableuser.firstName}</td>
                                    <td>${availableuser.lastName}</td>
                                    <td>
                                        <fmt:message key="admin.remove.button" var="removeButton"/>
                                        <input type="button" class="submit remove-admin-button"
                                            value="${removeButton}" data-userid="${availableuser.userID}">
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
            <div id="footNote">
                <%@include file="page_footer.jsp"%>
            </div>
        </div>
    </body>
    <meta name="${_csrf.parameterName}" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
</html>
