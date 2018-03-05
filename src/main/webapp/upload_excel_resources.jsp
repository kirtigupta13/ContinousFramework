<!DOCTYPE html>
<%@ page import="com.cerner.devcenter.education.models.Resource"%>
<%@ page import="com.cerner.devcenter.education.models.ResourceType"%>
<%@ page import="com.cerner.devcenter.education.models.Category"%>
<%@ page import="com.cerner.devcenter.education.models.Tag"%>
<html>
    <!-- This is the page where the admin can add an excel file containing many resources. -->
    <head>
        <%@include file="common_includes.jsp"%>
        <!-- Page specific Javascript/CSS -->
        <script type="text/javascript" src="../resources/js/display_messages.js"></script>
        <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/home_page.css" />" />           
        <link rel="stylesheet" href="http://davidstutz.github.io/bootstrap-multiselect/dist/css/bootstrap-multiselect.css" type="text/css" />
        <title><fmt:message key="common.resource.title" /></title>
    </head>
    <body>
        <div id="container">
            <div id="banner">
                <%@include file="page_header.jsp"%>
            </div>
            <div class="content">
                <div class="panel panel-primary">
                    <div class="panel-body">
                        <div id="successMessages" class="alert alert-success">${successMessages}</div>
                        <div id="errorMessages" class="alert alert-danger">${errorMessages}</div>
                        <div id="warningMessages" class="alert alert-warning">${warningMessages}</div>
                        <form:form name="bulkUploadForm" action="upload_file" method="post" role="form" enctype="multipart/form-data">
                            <div class="form-group">
                                <label for="file"><fmt:message key="bulkupload.filename" /></label>
                                <input type="file" name="file" class="form-control" accept="application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"/>
                            </div>
                            <div class="form-group">
                                <button class="btn btn-primary center-block" type="submit" id="upload">
                                    <fmt:message key="bulkUpload.button.upload" />
                                </button>
                            </div>
                        <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden" />
                    </form:form>
                </div>
            </div>
        </div>
        <div id="footNote">
            <%@include file="page_footer.jsp"%>
        </div>
    </div>
</body>
</html>