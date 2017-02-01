'use strict';

angular.module('Dashboard.auth.notLoggedIn', [])

  .config(['$routeProvider', $routeProvider => {
    $routeProvider
      .when('/not-logged-in', {
        templateUrl: 'auth/not-logged-in/template.html',
        controller: 'NotLoggedInCtrl'
      });
  }])

  .controller('NotLoggedInCtrl', ['$scope', function ($scope) {
  }]);

