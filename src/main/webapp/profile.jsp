<!DOCTYPE html>
<%@ page import="com.cerner.devcenter.education.models.UserInterestedCategory"%>
<html>
<!-- This page is the user's profile. Here the user can view their information that is stored by Cerner. -->
<head>
<%@include file="common_includes.jsp"%>
<!-- Page specific Javascript/CSS -->
<script type="text/javascript" src="/EvaluationFramework/resources/js/edit_user_interested_category.js"></script>
<script type="text/javascript" src="/EvaluationFramework/resources/js/interested_categories_datatable.js"></script>
<script type="text/javascript" src="/EvaluationFramework/resources/js/datatable_drawcallback.js"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/profile.css" />">
<title><fmt:message key="profile.title" /></title>
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
                    <div class="panel panel-info col-md-4 profile-details">
                        <div class="panel-heading">
                            <span class="panel-title profile_details_header">${user.name}</span>
                        </div>
                        <div class="panel-body">
                            <div class="row">
                                <table class="table table-user-information">
                                    <tbody>
                                        <tr>
                                            <td class="profile_details_header"><fmt:message key="profile.label.email" />:</td>
                                            <td>${user.email}</td>
                                        </tr>
                                        <tr>
                                            <td class="profile_details_header"><fmt:message key="common.label.userId" />:</td>
                                            <td>${user.userId}</td>
                                        </tr>
                                        <tr>
                                            <td class="profile_details_header"><fmt:message key="profile.label.role" />:</td>
                                            <td>${user.role}</td>
                                        </tr>
                                        <tr>
                                            <td class="profile_details_header"><fmt:message key="profile.label.department" />:</td>
                                            <td>${user.department}</td>
                                        </tr>
                                        <tr>
                                            <td class="profile_details_header"><fmt:message key="profile.label.project" />:</td>
                                            <td>${user.project}</td>
                                        </tr>
                                        <tr>
                                            <td class="profile_details_header"><fmt:message key="profile.label.manager" />:</td>
                                            <td>${user.manager}</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <div class="panel-footer"></div>
                    </div>
                    <div class="col-md-2"></div>
                    <div class="panel panel-info col-md-6 profile-details">
                        <div class="panel-heading">
                            <span class="panel-title profile_details_header">
                                <fmt:message key="profile.label.interestedCategories" />
                            </span>
                        </div>
                        <div id="messages">${message}</div>
                        <form:form modelAttribute="userInterestedCategory" action="addInterestedCategory" method="post">
                            <div class="form-group side_margin" id="addCategorySection">
                                <label>
                                    <fmt:message key="profile.addCategory.category" />
                                </label>
                                <fmt:message key="profile.addCategory.search" var="categorySearch" />
                                <form:select type="text" path="category.id" id="categorySearchDropDown" class="form-control"
                                    placeholder="${categorySearch}" value="">
                                    <option disabled selected value="-1">
                                        <fmt:message key="profile.interestedCategories.addCategory" />
                                    </option>
                                    <c:forEach items="${categories}" var="category">
                                        <option value="${category.id}">${category.name}</option>
                                    </c:forEach>
                                </form:select>
                            </div>
                            <div class="form-group side_margin">
                                <label for="all_skill_levels">
                                    <fmt:message key="profile.addCategory.proficiency" />
                                </label>
                                <form:select class="form-control" path="skillLevel" id="all_skill_levels">
                                    <option value="1">1</option>
                                    <option value="2">2</option>
                                    <option value="3">3</option>
                                    <option value="4">4</option>
                                    <option value="5">5</option>
                                </form:select>
                            </div>
                            <div class="form-group side_margin">
                                <label for="all_interest_levels">
                                    <fmt:message key="profile.addCategory.interestLevel" />
                                </label>
                                <form:select class="form-control" path="interestLevel" id="all_interest_levels">
                                    <option value="1">1</option>
                                    <option value="2">2</option>
                                    <option value="3">3</option>
                                    <option value="4">4</option>
                                    <option value="5">5</option>
                                </form:select>
                            </div>
                            <div>
                                <fmt:message key="profilePage.button.addInterestedCategory" var="addButton" />
                                <input type="submit" class="btn btn-primary center-block" id="addCategoryButton"
                                    value="${addButton}" />
                            </div>
                        </form:form>
                    </div>
                </div>
                <form:form action="removeUserInterestedCategoriesInBatch" method="post">
                    <table id="interestedCategoriesTable" class="table table-striped table-hover">
                        <thead class="table-header">
                            <tr>
                                <th data-orderable="false"></th>
                                <th><fmt:message key="profile.addCategory.category" /></th>
                                <th><fmt:message key="profile.interestedCategories.tableHeader.skillLevel" /></th>
                                <th><fmt:message key="profile.addCategory.interestLevel" /></th>
                                <th data-orderable="false"><fmt:message
                                        key="profile.interestedCategories.tableHeader.alterCategories" /></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${user.userInterestedCategories}" var="eachInterestedObject" varStatus="status">
                                <tr>
                                    <td><input type="checkbox" name="categoryIds"
                                            value="${eachInterestedObject.category.id}" /></td>
                                    <td>${eachInterestedObject.category.name}</td>
                                    <td>${eachInterestedObject.skillLevel}</td>
                                    <td>${eachInterestedObject.interestLevel}</td>
                                    <td>
                                        <button class='editButton' type="button" data-id="${eachInterestedObject.category.id }"
                                            data-name="${eachInterestedObject.category.name}" data-toggle="modal"
                                            data-target="#editModal">
                                            <fmt:message key="common.tableBody.edit" />
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <div>
                        <fmt:message key="common.tableBody.delete" var="deleteAllBtn"></fmt:message>
                        <input type="submit" class="btn btn-primary center-block" value="${deleteAllBtn}" />
                    </div>
                </form:form>
            </div>

            <!-- edit user interested category -->
            <div class="modal fade" id="editModal">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <span class="modal-label">
                                <fmt:message key="profile.interestedCategories.edit" />
                            </span>
                            <button type="button" class="glyphicon glyphicon-remove" data-dismiss="modal"></button>
                        </div>
                        <form class="form-horizontal" method="post"
                            action="updateUserInterestedCategories?${_csrf.parameterName}=${_csrf.token}">
                            <div class="modal-body">
                                <fieldset id="modal_form">
                                    <p>
                                        <label>
                                            <fmt:message key="profile.addCategory.category" />
                                            :
                                        </label>
                                        <label id="CategoryName"></label>
                                        <input type="hidden" name="categoryId" id="categoryId" value="">
                                    </p>
                                    <p>
                                        <label>
                                            <fmt:message key="profile.addCategory.proficiency" />
                                        </label>
                                        <select id="all_skill_levels" name="skillLevel">
                                            <option value="1">1</option>
                                            <option value="2">2</option>
                                            <option value="3">3</option>
                                            <option value="4">4</option>
                                            <option value="5">5</option>
                                        </select>
                                        <label>
                                            <fmt:message key="profile.addCategory.interestLevel" />
                                        </label>
                                        <select id="all_interest_levels" name="interestLevel">
                                            <option value="1">1</option>
                                            <option value="2">2</option>
                                            <option value="3">3</option>
                                            <option value="4">4</option>
                                            <option value="5">5</option>
                                        </select>
                                    </p>
                                </fieldset>
                            </div>
                            <div class="modal-footer">
                                <button id="editSubmit" type="submit" class="btn btn-primary">
                                    <fmt:message key="profile.interestedCategories.edit.save" />
                                </button>
                                <button type="button" class="btn btn-cancel" data-dismiss="modal">
                                    <fmt:message key="profile.interestedCategories.edit.cancel" />
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div id="footNote">
        <%@include file="page_footer.jsp"%>
    </div>
</body>
</html>