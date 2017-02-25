'use strict';

angular.module('Dashboard.languageDetectionAnalytics', ['chart.js'])

  .config(['$routeProvider', $routeProvider => {
    $routeProvider
      .when('/language-detection-analytics', {
        templateUrl: 'language-detection-analytics/template.html',
        controller: 'languageDetectionAnalyticsCtrl'
      })
  }])

  .controller('languageDetectionAnalyticsCtrl', ['$scope', '$routeParams', '$q', '$http', '$location', '$cookies', function ($scope, $routeParams, $q, $http, $location, $cookies) {
    $scope.status = ""

    $scope.role = $cookies.get("role")

    $scope.loading = true;
    $scope.status = ""

    $http.get("/api/language-detection-analytics")
      .then(response => {
        showChart(response.data)
      }, response => {
        $scope.status += "Failed to retrieve the data."
        console.log('server error ' + response.status)
      })
      .finally(() => $scope.loading = false)

    function showChart(dataset) {
      $scope.labels = dataset.labels //['2006', '2007', '2008', '2009', '2010', '2011', '2012'];
      $scope.series = dataset.series // ['Series A', 'Series B'];
      $scope.data = dataset.data
      $scope.options = {legend: {display: true}}
      $scope.colors = [
        '#97BBCD', // blue
        '#F7464A', // red
        '#DCDCDC', // light grey
        '#46BFBD', // green
        '#FDB45C', // yellow
        '#949FB1', // grey
        '#4D5360'  // dark grey
      ]
    }
  }])

