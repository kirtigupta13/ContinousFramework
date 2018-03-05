/**
 * Configure the services used to make XmlHttp Request to the server.
 */

var categoryServices = angular.module('categoryServices', [ 'ngResource' ]);

// defines the service for retrieving a list of categories.
categoryServices.factory('Category', [ '$resource', function($resource) {
    return $resource('categories', {}, {
	query : {
	    method : 'GET',
	    isArray : true
	}
    });
} ]);

// defines the service for retrieving resources identified by a category.
categoryServices.factory('CategoryResource', [ '$resource',
	function($resource) {
	    return $resource('categories/:categoryId', {}, {
		query : {
		    method : 'GET',
		    isArray : true
		}
	    });
	} ]);