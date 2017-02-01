'use strict';

angular.module('Dashboard.auth.login', [])

  .config(['$routeProvider', $routeProvider => {
    $routeProvider
      .when('/login', {
        templateUrl: 'auth/login/template.html',
        controller: 'LoginCtrl'
      });
  }])

  .controller('LoginCtrl', ['$scope', '$routeParams', '$q', '$http', '$cookies', function ($scope, $routeParams, $q, $http, $cookies) {
    $scope.status = ""
    $scope.userName = "user"
    $scope.password = "user"

    $scope.login = () => {
      $scope.loading = true;
      $scope.status = ""

      $http.post("/api/login", {userName: $scope.userName, password: $scope.password})
        .then(response => {
          console.log("authentication succeeded, the user's role is " + $cookies.get("role"))
          window.location = "#/menu";
        }, response => {
          $scope.status += "Failed to log in."
          console.log('login server error ' + response.status)
        })
        .finally(() => $scope.loading = false)
    }
  }]);

