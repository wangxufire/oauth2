'use strict';

//Define a function scope, variables used inside it will NOT be globally visible.
(function () {
  var
  //the HTTP headers to be used by all requests
    httpHeaders,
  //the message to be shown to the user
    messages = [],
  //Define the main module.
    as = angular.module('oauth2', ['ngMessages', 'ngAria',
      'ngCookies', 'ui.router', 'ui.bootstrap', 'oauth2.services', 'oauth2.controllers',
      'oauth2.filters', 'oauth2.directives']);

  as.config(function ($stateProvider, $urlRouterProvider, $httpProvider) {//$cookiesProvider
    //configure the rounting of ui-view
    $stateProvider
      .state('home',
        {
          url: '/',
          templateUrl: 'scripts/view/home.html'
        }
      )
      .state('login',
        {
          url: '/login',
          controller: 'loginController',
          templateUrl: 'scripts/view/login.html'
        }
      )
      .state('product',
        {
          url: '/product',
          controller: 'productController',
          templateUrl: 'scripts/view/product/list.html'
        }
      )
      .state('productAdd',
        {
          url: '/product/add',
          controller: 'productAddController',
          templateUrl: 'scripts/view/product/edit.html'
        }
      )
      .state('productView',
        {
          url: '/product/:id',
          controller: 'productEditController',
          templateUrl: 'scripts/view/product/scope.html'
        }
      )
      .state('app',
        {
          url: '/app',
          controller: 'appController',
          templateUrl: 'scripts/view/app/list.html'
        }
      )
      .state('appAdd',
        {
          url: '/app/add',
          controller: 'appAddController',
          templateUrl: 'scripts/view/app/edit.html'
        }
      )
      .state('appView',
        {
          url: '/app/:id',
          controller: 'appEditController',
          templateUrl: 'scripts/view/app/edit.html'
        }
      )
      .state('appManager',
        {
          url: '/app_manager',
          controller: 'appAdminController',
          templateUrl: 'scripts/view/app/list.html'
        }
      )
      .state('appManagerEdit',
        {
          url: '/app_manager/:id',
          controller: 'appAdminViewController',
          templateUrl: 'scripts/view/app/view.html'
        }
      )
      .state('user',
        {
          url: '/user',
          //controller: 'userController',
          templateUrl: 'scripts/view/user/center.html'
        }
      )
      .state('register',
        {
          url: '/register',
          controller: 'userRegisterController',
          templateUrl: 'scripts/view/user/register.html'
        }
      )
      .state('userManager',
        {
          url: '/user_manager',
          controller: 'userAdminController',
          templateUrl: 'scripts/view/user/list.html'
        }
      )
      .state('userManagerEdit',
        {
          url: '/user_manager/:id',
          controller: 'userEditController',
          templateUrl: 'scripts/view/user/edit.html'
        }
      )
      .state('authorize',
        {
          url: '/authorize',
          controller: 'authorizeController',
          templateUrl: 'scripts/view/auth/auth.html'
        }
      )
      .state('access',
        {
          url: '/access',
          controller: 'accessTokenController',
          templateUrl: 'scripts/view/auth/token.html'
        }
      )
      .state('docs',
        {
          url: '/docs',
          templateUrl: 'scripts/view/doc/docs.html'
        }
      );

    $urlRouterProvider.otherwise('user');

    $httpProvider.interceptors.push(function ($rootScope, $q) {
      return {
        request: function (config) {
          config.headers = config.headers || {};
          var token = $rootScope.getUser().token;
          if (token) {
            config.headers['X-Auth-Token'] = token;
          }
          return config || $q.state(config);
        },
        'requestError': function (rejection) {
          return rejection;
        },
        'response': function (response) {
          $rootScope.addMessage(response);
          return response || $q.state(response);
        },
        'responseError': function (response) {
          $rootScope.addMessage(response);
          return $q.reject(response);
        }
      };
    });

    httpHeaders = $httpProvider.defaults.headers;

    //$cookiesProvider.defaults.domain = 'hd123.com';
    //var expireDate = new Date();
    //expireDate.setHours(expireDate.getHours() + 1);
    //$cookiesProvider.defaults.expires = expireDate;
  });

  as.run(function ($rootScope, $state, $cookies) {
    $rootScope.$state = $state;

    $rootScope.getUser = function () {
      return !!$cookies.getObject('user') ? $cookies.getObject('user') : {};
    };

    $rootScope.removeUser = function () {
      $rootScope.user = {};
      $cookies.remove('user');
    };

    $rootScope.messages = function () {
      return messages;
    };

    $rootScope.addMessage = function (response) {
      var message = null;
      if (response.status === 401) {
        $rootScope.removeUser();
        message = {
          code: '401',
          text: '登陆已失效，请重新登录',
          type: 'danger'
        };
        $rootScope.$broadcast('loginRequired');
      } else if (response.data.errorCode === '403') {
        message = {
          code: '403',
          text: '当前用户未授权访问该资源',
          type: 'danger'
        };
      } else if (response.data.errorCode && response.data.message) {
        message = {
          code: response.data.errorCode,
          text: response.data.message,
          type: response.data.errorCode === '200' ? 'success' : 'danger'
        };
      }
      if (!!message) {
        messages.push(message);
        if (message.code === '403') {
          $state.go('user');
        }
      }
    };

    $rootScope.$on('loginRequired', function () {
      $state.go('login');
    });

    $rootScope.$on('$stateChangeStart', function (event, toState) {
      var isHome = toState.name === 'home';
      var isLogin = toState.name === 'login';
      var isUserRegister = toState.name === 'register';
      if (!!$rootScope.getUser().username) {
        if (isHome || isLogin || isUserRegister) {
          $state.go('user');
        }
      } else {
        if (!isHome && !isLogin && !isUserRegister) {
          $rootScope.$broadcast('loginRequired');
        }
      }
    });

  });

}());