<!DOCTYPE html>
<%@ page import="com.cerner.devcenter.education.models.UserSubscription"%>
<html>
<!-- This page is the user's subscription's page. Here the user can view their subscribed categories and manage them. -->
<head>
<%@include file="common_includes.jsp"%>
<!-- Page specific Javascript/CSS -->
<script type="text/javascript" src="/EvaluationFramework/resources/js/unsubscribe_category.js"></script>
<script type="text/javascript" src="/EvaluationFramework/resources/js/user_subscriptions_datatable.js"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/user_subscription.css" />">
<title><fmt:message key="subscriptionPage.title" /></title>
</head>
<body>
    <div id="banner">
        <%@include file="page_header.jsp"%>
    </div>
    <div class="container-fluid">
        <div id="content">
            <div id="page">
                <div id="errorMessages">${errorMessage}</div>
                <div id="successMessages">${successMessage}</div>
                <div class="row">
                    <div class="panel panel-info col-md-6 col-md-offset-3 subscription-box">
                        <div class="panel-heading">
                            <span class="panel-title subscribe_category_header"> <fmt:message
                                    key="subscriptionPage.label.add.subscription" />
                            </span>
                        </div>
                        <div class="panel-body">
                            <form:form modelAttribute="userSubscription" action="addSubscription" method="post">
                                <div class="form-group side_margin" id="addSubscriptionSection">
                                    <label><fmt:message key="userSubsciption.addSubscription.category" /></label>
                                    <form:select type="text" path="categoryId" id="categoryDropDown" class="form-control">
                                        <option disabled selected value="-1">
                                            <fmt:message key="subscriptionPage.subscriptionMenu.addCategory" />
                                        </option>
                                        <c:forEach items="${categories}" var="category">
                                            <option value="${category.id}">${category.name}</option>
                                        </c:forEach>
                                    </form:select>
                                </div>
                                <div>
                                    <fmt:message key="subscriptionPage.addCategory.button" var="addButton" />
                                    <input type="submit" class="btn btn-primary center-block" id="addCategoryButton"
                                        value="${addButton}" />
                                </div>
                                <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden" />
                            </form:form>
                        </div>
                    </div>
                </div>
                <div>
                    <table id="subscriptionsTable" class="table table-striped table-hover">
                        <thead>
                            <tr>
                                <th scope="col"><fmt:message key="subscriptionTable.category" /></th>
                                <th scope="col"><fmt:message key="subscriptionTable.description" /></th>
                                <th data-orderable="false"><fmt:message key="subscriptionTable.removeHeader" /></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="eachSubscribedCategory" items="${subscribedCategories}">
                                <tr id="${eachSubscribedCategory.name}">
                                    <td>${eachSubscribedCategory.name}</td>
                                    <td>${eachSubscribedCategory.description}</td>
                                    <td><fmt:message key="subscription.remove.button" var="removeButton" /> <input
                                        type="button" class="submit remove-subscription-button" value="${removeButton}"
                                        data-categoryId="${eachSubscribedCategory.id}"
                                        data-categoryName="${eachSubscribedCategory.name}"></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
    <div id="footNote">
        <%@include file="page_footer.jsp"%>
    </div>
</body>
<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
</html>