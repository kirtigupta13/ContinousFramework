<!DOCTYPE html>
<html>
    <!-- This is the page where the admin can add a new category. The admin needs to fill out the 
    name and the description of the category. -->
    <head>
        <%@include file="common_includes.jsp"%>
        <!-- Page specific Javascript/CSS -->
        <script type="text/javascript" src="../resources/js/validate_category.js"></script>
        <script type="text/javascript" src="../resources/js/manage_category.js"></script>
        <script type="text/javascript" src="../resources/js/datatable_drawcallback.js"></script>
        <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/add_category.css" />" />
        <title><fmt:message key="common.category.title" /></title>
    </head>
    <body>
        <div id="container">
            <div id="banner">
                <%@include file="page_header.jsp"%>
            </div>
            <div id="content">
                <!-- This will display messages to help admin add a new category -->
                <div class="messages">
                    <c:if test="${successMessage != null}">
                        <div id="successMessages" class="alert alert-success">${successMessage}</div>
                    </c:if>
                    <c:if test="${errorMessage != null}">
                        <div id="errorMessages" class="alert alert-danger">${errorMessage}</div>
                    </c:if>
                </div>
                <div class="panel panel-info">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <fmt:message key="common.link.header.addCategory" />
                        </h4>
                    </div>
                    <div class="panel-body">
                        <form:form  name="newform" modelAttribute="category" action="add_category_page" method="post" role="form">
                            <div class="form-group col-sm-3">
                                <label for="name">
                                    <fmt:message key="addCategoryPage.label.categoryName" />
                                </label>
                                <form:input path="name" type="text" id="name" name="text-field" class="form-control" />
                                <span class="errorMessage" id="errorMessageForCategoryName">
                                    <fmt:message key="addCategoryPage.errorMessage.invalidCategoryName" />
                                </span>
                            </div>
                            <div class="form-group col-sm-9">
                                <label for="description">
                                    <fmt:message key="addCategoryPage.label.categoryDescription" />
                                </label>
                                <form:input path="description" type="text" id="description" name="text-field" class="form-control" />
                                <span class="errorMessage" id="errorMessageForDescription"> 
                                    <fmt:message key="addCategoryPage.errorMessage.invalidDescription" />
                                </span>
                            </div>
                            <div class = "submit-btn">
                                <fmt:message key="addCategoryPage.button.submit" var="submitButton"/>
                                <input type="submit" class="btn btn-mini" id="addButton" value="${submitButton}" />
                            </div>
                        </form:form>
                    </div>
                </div>
                    <!--  Display all available categories -->
                <div>
                    <table id="manageCategoryTable" class="table table-striped table-hover">
                        <thead class="table-header">
                            <tr>
                                <th scope="col"><fmt:message key="common.tableHeader.category.name" /></th>
                                <c:forEach var="resourcesPerSkillLevel" items ="${eachCategory.getResourceCountPerSkillLevel()}">
                                    <th>${resourcesPerSkillLevel.key}</th>
                                </c:forEach>
                                <th><fmt:message key="common.tableHeader.category.numberOfResourcesForSkillLevelOne" /></th>
                                <th><fmt:message key="common.tableHeader.category.numberOfResourcesForSkillLevelTwo" /></th>
                                <th><fmt:message key="common.tableHeader.category.numberOfResourcesForSkillLevelThree" /></th>
                                <th><fmt:message key="common.tableHeader.category.numberOfResourcesForSkillLevelFour" /></th>
                                <th><fmt:message key="common.tableHeader.category.numberOfResourcesForSkillLevelFive" /></th>
                                <th scope="col"><fmt:message key="common.tableHeader.category.numberOfResources" /></th> 
                                <th data-orderable="false" scope="col"></th>                               
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="eachCategory" items="${categories}">
                                <tr id="${eachCategory.id}">
                                    <form:form modelAttribute="category" class="category_id_form">
                                        <td>
                                            <button id='categoryButton' type="button" data-category="${eachCategory.getName()}"
                                            data-description="${eachCategory.getDescription()}"
                                            data-toggle="modal" data-target="#categoryModal">
                                            ${eachCategory.getName()}
                                            </button>
                                        </td>
                                        <c:forEach var="resourcesPerSkillLevel" items ="${eachCategory.getResourceCountPerSkillLevel()}">
                                            <td>${resourcesPerSkillLevel.value}</td>
                                        </c:forEach>
                                        <td>${eachCategory.getResourcesCount()}</td>                                        
                                        <td>
                                            <fmt:message key="common.button.remove" var="removeButton"/>
                                            <input id="removeButton${eachCategory.id}" data-categoryid="${eachCategory.id}" type=button value="${removeButton}"/>
                                        </td>
                                    </form:form>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <!--  End of display all available categories -->
            </div>
            <div class="modal fade" id="categoryModal">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <label><span id="modalLabel"><fmt:message key="homepage.category"/></span></label>
                            <button type="button" class="glyphicon glyphicon-remove" data-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                        	<form class="form-horizontal" method="post" action="categoryMore?${_csrf.parameterName}=${_csrf.token}">
                            	<fieldset id="modal_form">
                                	<p><label><fmt:message key="addCategoryPage.label.category"/></label> 
                                        <span id ="category-name-more"></span>
                                    </p>
                                    <p><label><fmt:message key="addCategoryPage.label.categoryDescription"/></label> 
                                        <span id="category-description"></span>
                                    </p>
                                </fieldset>
                            </form>
                        </div>
                     </div>
                   </div>
              </div>
              <div id="footNote">
                <%@include file="page_footer.jsp"%>
            </div>
        </div>
    </body>
</html>