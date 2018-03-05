<!DOCTYPE html>
<html>
    <!-- This page provides the user with an assessment form where he/she can enter his/her skills in select areas and their relevance to the work -->
    <head>
        <%@include file="common_includes.jsp"%>
        <!-- Page specific Javascript/CSS -->
        <link href="<c:url value="/resources/css/main.css" />" rel="stylesheet" type="text/css">
        <title><fmt:message key="assessmentForm.title" /></title>
    </head>
    <body>
        <div id="container">
            <div id="banner">
                <%@include file="page_header.jsp"%>
            </div>
            <div id="content">
                <div class="content-wrapper">
                    <!-- The form redirects to resources page after input is given. All data that is entered is stored in the form of a modelAttribute 'surveyForm' -->
                    <form:form method="post" action="resources" modelAttribute="surveyForm">
                        <!-- Obtain category names and descriptions from controller and create menus for those. -->
                        <c:forEach begin="0" end="${fn:length(categories)-1}" var="categoryNumber">
                            <fieldset class="field-group">
                                <div id="option-box">
                                    <h2 title="${categories[categoryNumber].description}">${categories[categoryNumber].name}</h2>
                                    <p>
                                        <!-- Obtain options from controller and display those as options to user. -->
                                        <form:hidden path="userRatings[${categoryNumber}].categoryId" value="${categories[categoryNumber].id}" />
                                    </p>
                                    <div id="form-options">
                                        <div class="inner">
                                            <div class="inner">
                                                <label for="skill"><fmt:message key="common.label.skillLevel" /></label>
                                            </div>
                                            <div class="inner">
                                                <form:select path="userRatings[${categoryNumber}].skill">
                                                    <c:set var="skillOptionIndex" value="${skillOptionIndex+1}" />
                                                    <c:set var="skillType" value="category_${skillOptionIndex}_skill" />
                                                    <c:forEach var="item" items="${skillOptions}">
                                                        <option value="${item.key}" ${item.key == userResults[skillType] ? 'selected="selected"' : ''}>${item.value}</option>
                                                    </c:forEach>
                                                </form:select>
                                            </div>
                                        </div>
                                        <div class="inner">
                                            <div class="inner">
                                                <label for="relevance"><fmt:message key="assessmentForm.label.relevanceSkill" /></label>
                                            </div>
                                            <div class="inner">
                                                <form:select path="userRatings[${categoryNumber}].relevance">
                                                    <c:set var="relevanceOptionIndex" value="${relevanceOptionIndex+1}" />
                                                    <c:set var="relevanceType" value="category_${relevanceOptionIndex}_relevance" />
                                                    <c:forEach var="relevance" items="${relevanceOptions}">
                                                        <option value="${relevance.key}" ${relevance.key == userResults[relevanceType] ? 'selected="selected"' : ''}>${relevance.value}</option>
                                                    </c:forEach>
                                                </form:select>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </fieldset>
                        </c:forEach>
                        <p class="form-actions">
                            <button id="button" class="btn btn-primary btn-block" type="submit">
                                <fmt:message key="common.button.submit" />
                            </button>
                        </p>
                    </form:form>
                </div>
            </div>
            <div id="footNote">
                <%@include file="page_footer.jsp"%>
            </div>
        </div>
    </body>
</html>