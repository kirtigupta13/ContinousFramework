<!DOCTYPE html>
<html>
    <head>
        <%@include file="common_includes.jsp"%>
        <%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
        <!-- Page specific Javascript/CSS -->
        <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/restricted_message.css" />">      
        <title><fmt:message key="common.accessDeniedPage.title" /></title>
    </head>
    <body>
        <div class="panel panel-info col-md-5 profile-details">
            <div class="panel-heading">
                <span class="panel-title profile_details_header">${message}</span>
            </div>
            <div class="panel-body">
                <div class="row">
                    <div class="glyphicon glyphicon-lock col-xs-2">
                    </div>
                    <div class="col-xs-8">
                        <div>
                            <span class="message_title"><fmt:message key="common.accessDenied.message.section.one"></fmt:message></span>
                        </div>
                        <div>
                            <span class="message_desc"><fmt:message key="common.accessDenied.message.section.two"></fmt:message></span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel-footer">
            </div>
        </div>
    </body>
</html>
