'use strict';

(function () {
  var app = angular.module('oauth2.filters', []);

  app.filter('range', function () {
    return function (input, total) {
      total = parseInt(total);
      for (var i = 0; i < total; i++) {
        input.push(i);
      }

      return input;
    };
  });

}());


