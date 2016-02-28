'use strict';

(function () {
  var app = angular.module('oauth2.directives', []);

  //app.directive('usernameValidator', function ($http, $q) {
  //  return {
  //    require: 'ngModel',
  //    link: function (scope, element, attrs, ngModel) {
  //      ngModel.$asyncValidators.username = function (modelValue, viewValue) {
  //        if (ngModel.$isEmpty(modelValue)) {
  //          // consider empty model valid
  //          return $q.when();
  //        }
  //
  //        var def = $q.defer();
  //
  //        $http.get('api/users/username-check?username=' + viewValue)
  //          .then(
  //            function () {
  //              def.reject();
  //            },
  //            function () {
  //              def.resolve();
  //            }
  //          );
  //
  //        return def.promise;
  //      };
  //    }
  //  };
  //});

}());