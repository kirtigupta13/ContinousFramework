'use strict';

/**
 * Configure the categoryControllers used to retrieve category data from server
 * and assign to scope variables.
 */

var categoryControllers = angular.module('categoryControllers', []);

categoryControllers.controller('CategoryController', [ '$scope', 'Category',
	function($scope, Category) {
	    $scope.categories = Category.query();
	} ]);
