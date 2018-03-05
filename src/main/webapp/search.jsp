<!DOCTYPE html>
<%@ page import="com.cerner.devcenter.education.models.Resource"%>
<%@ page import="com.cerner.devcenter.education.models.Category"%>
<%@ page import="com.cerner.devcenter.education.models.UserInterestedCategory"%>
<html>
<!-- This page displays resources based on user search and his profile -->

<head>
<%@include file="common_includes.jsp"%>
<!-- Page specific JavaScript/CSS -->
<script type="text/javascript" src="/EvaluationFramework/resources/js/homepage.js"></script>
<script type="text/javascript" src="/EvaluationFramework/resources/js/search.js"></script>
<script type="text/javascript" src="/EvaluationFramework/resources/js/search_datatable.js"></script>
<script type="text/javascript" src="/EvaluationFramework/resources/js/datatable_drawcallback.js"></script>
<title>Search Page</title>
</head>

<body>
    <section>
        <div id="banner">
            <%@include file="page_header.jsp"%>
        </div>
        <div class="content">
            <hr class="divider">
            <article id="page">
                <div id="messages">${message}</div>
                <c:if test="${not empty noResource}">
                    <p>
                        <span class="search-errorMessage">${noResource}</span>
                    </p>
                </c:if>
                <c:if test="${not empty noCategory}">
                    <span class="search-errorMessage">${noCategory}</span>
                    <p>
                        <span>
                            <fmt:message key="common.user.requestCategory" />
                            <a href="../app/resourceRequest">
                                <fmt:message key="common.user.clickHere" />
                            </a>
                        </span>
                    </p>
                </c:if>
                <c:if test="${not empty noRetrievedResources}">
                    <p>
                        <span class="search-errorMessage">${noRetrievedResources}</span>
                    </p>
                </c:if>
                <c:if test="${not empty noRetrievedCategory}">
                    <p>
                        <span class="search-errorMessage">${noRetrievedCategory}</span>
                    </p>
                </c:if>
            </article>
            <c:if test="${not empty resourceList}">
                <c:if test="${resourceList != null}">
                    <div>
                        <h2 class="table-heading">
                            <fmt:message key="common.search.resourcesFound" />
                        </h2>
                        <table id="" class="table table-striped table-hover datatable">
                            <thead class="table-header">
                                <tr>
                                    <th scope="col"><fmt:message key="common.tableHeader.name" /></th>
                                    <th scope="col"><fmt:message key="common.tableHeader.link" /></th>
                                    <th scope="col"><fmt:message key="common.tableHeader.description" /></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${resourceList}" var="resource" varStatus="status">
                                    <tr data-resourceId="${resource.getResourceId()}">
                                        <td>${resource.getResourceName()}</td>
                                        <td><a href='${resource.getResourceLink().toString()}' target="_blank">${resource.getResourceLink().toString()}</a></td>
                                        <td>${resource.getDescription().toString()}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>
            </c:if>
            <c:if test="${nonchosenCategoryList != null || not empty nonchosenCategoryList}">
                <c:if test="${not empty chosenCategoryList ||  chosenCategoryList != null}">
                    <div>
                        <input type="hidden" id="nonchosenCategoryList" value="${nonchosenCategoryList}">
                        <input type="hidden" id="chosenCategoryList" value="${chosenCategoryList}">
                        <h2 class="table-heading">
                            <fmt:message key="common.search.categoryFound" />
                        </h2>
                        <table id="" class="table table-striped table-hover datatable">
                            <thead class="table-header">
                                <tr>
                                    <th scope="col"><fmt:message key="common.tableHeader.name" /></th>
                                    <th scope="col"><fmt:message key="common.tableHeader.description" /></th>
                                    <th scope="col"><fmt:message key="common.tableHeader.action" /></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${nonchosenCategoryList}" var="nonchosenCategory" varStatus="status">
                                    <tr>
                                        <td>${nonchosenCategory.getName()}</td>
                                        <td>${nonchosenCategory.getDescription().toString()}</td>
                                        <td id="${nonchosenCategory.getId()}" class="success-messages">
                                            <button class="btn btn-primary center-block" id='nonchosenCategoryButton'
                                                type="button" data-categoryname="${nonchosenCategory.getName()}"
                                                data-categoryid="${nonchosenCategory.getId()}"
                                                data-categorydescriptoin="${nonchosenCategory.getDescription().toString()}"
                                                data-backdrop="true" data-keyboard="true"
                                                data-target="#interestedCategoryModal">
                                                <fmt:message key="searchpage.label.addInterestedCategory" />
                                            </button>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:forEach items="${chosenCategoryList}" var="chosenCategory" varStatus="status">
                                    <tr>
                                        <td>${chosenCategory.getName()}</td>
                                        <td>${chosenCategory.getDescription().toString()}</td>
                                        <td class="success-messages">${categoryAddedMessage}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>
            </c:if>
        </div>
        <div class="modal fade" id="interestedCategoryModal">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <label>
                            <span class="modal-label">
                                <fmt:message key="searchpage.label.interestedCategories" />
                            </span>
                        </label>
                        <button type="button" class="glyphicon glyphicon-remove" data-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <fieldset id="modal_form">
                            <input id="_csrf_Token" value="${_csrf.token}" type="hidden" />
                            <input id="_csrf_header" value="${_csrf.headerName}" type="hidden" />
                            <label>
                                <fmt:message key="searchpage.label.category" />
                                :
                            </label>
                            <span class="category-name" id="category-name"></span>
                            <input type="hidden" class="category-id" name="categoryId" id="categoryId" value="">
                            <p>
                                <label>
                                    <fmt:message key="searchpage.label.proficiency" />
                                </label>
                                <select id="all_skill_levels" name="skillLevel">
                                    <option value="1">1</option>
                                    <option value="2">2</option>
                                    <option value="3">3</option>
                                    <option value="4">4</option>
                                    <option value="5">5</option>
                                </select>
                            </p>
                            <p>
                                <label>
                                    <fmt:message key="searchpage.label.interestLevel" />
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
                        <button class="btn btn-primary" id='submitCategory' type="button">
                            <fmt:message key="searchpage.label.addInterestedCategory" />
                        </button>

                        <button type="button" class="btn btn-cancel" data-dismiss="modal">
                            <fmt:message key="searchpage.button.cancel" />
                        </button>
                    </div>
                </div>
            </div>
        </div>
        <footer id="footNote">
            <%@include file="page_footer.jsp"%>
        </footer>
    </section>
</body>
</html>