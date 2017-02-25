'use strict';

angular.module('Dashboard.dataFlow', [])

  .config(['$routeProvider', $routeProvider => {
    $routeProvider
      .when('/data-flow', {
        templateUrl: 'data-flow/template.html',
        controller: 'dataFlowCtrl'
      })
  }])

  .controller('dataFlowCtrl', ['$scope', '$routeParams', '$q', '$http', '$location', '$cookies', function ($scope, $routeParams, $q, $http, $location, $cookies) {
    $scope.status = ""

    $scope.role = $cookies.get("role")

    $scope.loading = true;
    $scope.status = ""

    $http.get("/api/data-flow")
      .then(response => {
        showGraph(response.data)
      }, response => {
        $scope.status += "Failed to retrieve the data."
        console.log('server error ' + response.status)
      })
      .finally(() => $scope.loading = false)

    function showGraph(dataset) {
      var cy = cytoscape({
        container: $('#cy'),
        boxSelectionEnabled: false,
        autounselectify: true,
        layout: {
          name: 'dagre',
          rankDir: 'LR',
          fit: false
        },
        style: [
          {
            selector: 'node',
            style: {
              'width': '200',
              'height': '200',
              'content': 'data(label)',
              'font-size': 30,
              'text-wrap': 'wrap',
              'text-max-width': '180',
              'color': 'lightgrey',
              'text-valign': 'center',
              'text-halign': 'center',
              'background-color': '#11479e'
            }
          },
          {
            selector: 'edge',
            style: {
              'width': 12,
              'target-arrow-shape': 'triangle',
              'line-color': '#9dbaea',
              'target-arrow-color': '#9dbaea',
              'curve-style': 'bezier'
            }
          }
        ],
        elements: dataset
      });

      cy.center()
    }
  }])

