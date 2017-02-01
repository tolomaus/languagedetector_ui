'use strict';

angular.module('Dashboard.languageDetection', [])

  .config(['$routeProvider', $routeProvider => {
    $routeProvider
      .when('/language-detection', {
        templateUrl: 'language-detection/template.html',
        controller: 'languageDetectionCtrl'
      })
  }])

  .controller('languageDetectionCtrl', ['$scope', '$routeParams', '$q', '$http', '$location', '$cookies', function ($scope, $routeParams, $q, $http, $location, $cookies) {
    $scope.status = ""

    $scope.role = $cookies.get("role")

    $scope.translate = () => {
      $scope.loading = true;
      $scope.status = ""

      $http.get("/api/language-detection", {params: {text: $scope.$storage.text}})
        .then(response => {
          $scope.sentences = response.data
        }, response => {
          $scope.status += "Failed to log in."
          console.log('login server error ' + response.status)
        })
        .finally(() => $scope.loading = false)
    }
  }])

