'use strict';

// Declare app level module which depends on views, and components
angular.module('Dashboard', [
  'ngRoute',
  'ngCookies',
  'ngStorage',
  'Dashboard.auth.login',
  'Dashboard.auth.notLoggedIn',
  'Dashboard.auth.forbidden',
  'Dashboard.auth.logout',
  'Dashboard.menu',
  'Dashboard.languageDetection',
  'Dashboard.languageDetectionAnalytics',
  'Dashboard.dataDependencies',
  'Dashboard.sparkLogging'
])
  .config(['$locationProvider', '$routeProvider', '$httpProvider', ($locationProvider, $routeProvider, $httpProvider) => {
    $routeProvider
      .when('/', {redirectTo: '/menu'})
      .otherwise({redirectTo: '/'});

    $httpProvider.interceptors.push($q => {
      return {
        'responseError': response => {
          if (response.status == 401) {
            window.location = "#/not-logged-in";
          }
          if (response.status == 403) {
            window.location = "#/forbidden";
          }
          return $q.reject(response);
        }
      };
    });
  }])


Array.prototype.flatMap = function (fn, ctx) {
  return this.reduce((k, v) => k.concat(fn.call(ctx, v)), []);
};
Array.prototype.flatten = function () {
  return this.reduce((k, v) => k.concat(v), []);
};
Array.prototype.groupBy = function (keyFunction) {
  var groups = {};
  this.forEach(el => {
    var key = keyFunction(el);
    if (key in groups == false) {
      groups[key] = [];
    }
    groups[key].push(el);
  });
  return Object.keys(groups).map(key => {
    return {
      key: key,
      values: groups[key]
    };
  });
};
Array.prototype.distinct = function () {
  return this.filter((item, index, self) => self.indexOf(item) === index)
}
