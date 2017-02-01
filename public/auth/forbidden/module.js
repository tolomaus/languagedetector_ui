'use strict';

angular.module('Dashboard.auth.forbidden', [])

  .config(['$routeProvider', $routeProvider => {
    $routeProvider
      .when('/forbidden', {
        templateUrl: 'auth/forbidden/template.html',
        controller: 'ForbiddenCtrl'
      });
  }])

  .controller('ForbiddenCtrl', ['$scope', function ($scope) {
  }]);

