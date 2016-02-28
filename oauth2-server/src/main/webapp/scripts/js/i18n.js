//(function () {
//  var as = angular.module('oauth2.i18n', []);
//
//  as.service('i18n', function () {
//    var self = this;
//    this.setLanguage = function (language) {
//      $.i18n.properties({
//        name: 'messages',
//        path: 'i18n/',
//        mode: 'map',
//        language: language,
//        callback: function () {
//          self.language = language;
//        }
//      });
//    };
//    this.setLanguage('zh_CN');
//  });
//
//  as.directive('txt', function () {
//    return {
//      restrict: 'EA',
//      link: function (scope, element, attrs) {
//        var key = attrs.key;
//        if (attrs['key-expr']) {
//          scope.$watch(attrs['key-expr'], function (value) {
//            key = value;
//            element.text($.i18n.prop(value));
//          });
//        }
//        scope.$watch('language()', function (value) {
//          element.text($.i18n.prop(key));
//        });
//      }
//    };
//  });
//
//}());