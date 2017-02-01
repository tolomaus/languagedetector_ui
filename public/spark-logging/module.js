'use strict';

angular.module('Dashboard.sparkLogging', [])

  .config(['$routeProvider', $routeProvider => {
    $routeProvider
      .when('/spark-logging', {
        templateUrl: 'spark-logging/template.html',
        controller: 'sparkLoggingCtrl'
      })
  }])

  .controller('sparkLoggingCtrl', ['$scope', '$routeParams', '$q', '$http', '$location', '$cookies', function ($scope, $routeParams, $q, $http, $location, $cookies) {
    $scope.status = ""

    $scope.role = $cookies.get("role")

    $scope.loading = true;
    $scope.status = ""

    $http.get("/api/spark-logging")
      .then(response => {
        $scope.businessLogs = response.data
      }, response => {
        $scope.status += "Failed to retrieve spark logging."
        console.log('server error ' + response.status)
      })
      .finally(() => $scope.loading = false)

    $scope.filterMessages = (category) => {
      $(".messages li").each(function () {
        $(this).toggle(true);
      });
      if (category) {
        $(".messages li").not(".li-" + category).each(function () {
          $(this).toggle(false);
        });
      }
    }
  }])

