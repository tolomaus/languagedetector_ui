'use strict';

angular.module('Dashboard.menu', [])

  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider
      .when('/menu', {
        templateUrl: 'menu/template.html',
        controller: 'MenuCtrl'
      })
  }])

  .controller('MenuCtrl', ['$scope', '$routeParams', '$http', '$cookies', function ($scope, $routeParams, $http, $cookies) {
    $scope.status = ""
    $scope.role = $cookies.get("role")
  }])

