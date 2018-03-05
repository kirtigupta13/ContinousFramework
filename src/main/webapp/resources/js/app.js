'use strict';
/**
 * Configure the angularJS modules used in the entire client application scope.
 */

var app = angular.module('educationEvaluation', [ 'ngRoute',
        'categoryControllers', 'resourceControllers', 'categoryServices' ]);

// configure the routing to partial contents.
app.config([ '$routeProvider', function($routeProvider) {
    $routeProvider.when('/category_resources', {
        templateUrl : '../resources/partials/categories.jsp',
        controller : 'CategoryController'
    }).when('/category_resources/:categoryId', {
        templateUrl : '../resources/partials/resources.jsp',
        controller : 'ResourceController'
    }).otherwise({
        redirectTo : '/category_resources'
    });
} ]);
