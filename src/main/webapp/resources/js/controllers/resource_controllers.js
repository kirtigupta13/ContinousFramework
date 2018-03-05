'use strict'
/**
 * Configure the resourceControllers used to retrieve resource data from server
 * and assign to scope variables.
 */

var resourceControllers = angular.module('resourceControllers', []);

resourceControllers.controller('ResourceController', [ '$scope',
        '$routeParams', 'CategoryResource',
        function($scope, $routeParams, CategoryResource) {
            $scope.resources = CategoryResource.query({
                categoryId : $routeParams.categoryId
            });
        } ]);