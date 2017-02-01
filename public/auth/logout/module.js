'use strict';

angular.module('Dashboard.auth.logout', [])

  .config(['$routeProvider', $routeProvider => {
    $routeProvider
      .when('/logout', {
        templateUrl: 'auth/logout/template.html',
        controller: 'LogoutCtrl'
      });
  }])

  .controller('LogoutCtrl', ['$scope', '$routeParams', '$q', '$http', function ($scope, $routeParams, $q, $http) {
    $scope.status = ""

    $http.post("/api/logout")
      .then(function successCallback(response) {
        console.log("successfully logged out")
      }, function errorCallback(response) {
        $scope.status += "Failed to log out."
        console.log('company query server error ' + response.status);
      })
  }]);

