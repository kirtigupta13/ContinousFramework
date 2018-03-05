<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html ng-app="EducationEvaluation">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script src="../resources/js/lib/angular/angular.js"></script>
<script src="../resources/js/lib/angular/angular-resource.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/angular_material/0.9.4/angular-material.js"></script>
<script src="../resources/js/app.js"></script>
<script src="../resources/js/services.js"></script>
<script src="../resources/js/controllers/categoryControllers.js"></script>
</head>
<body ng-controller="CategoryController">

	Search: <input ng-model="query">

	<ul>
  		<li ng-repeat="category in categories | filter:query">
    		<span>{{category.id}}</span>
    		<p>{{category.name}}</p>
    		<p>{{category.description}}</p>
 		</li>
	</ul>
</body>
</html>