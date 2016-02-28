'use strict';

(function () {
  var as = angular.module('oauth2.controllers', []);

  as.controller('mainController', function ($scope, $rootScope, $state, $cookies, userService) {
    $rootScope.user = $rootScope.getUser();

    $scope.isAdmin = function () {
      return !!$rootScope.getUser().isAdmin;
    };

    $scope.isAdminUser = function (roles) {
      return roles.indexOf('ROLE_ADMIN') >= 0;
    };

    $scope.activeWhen = function (name) {
      return $state.current.name.indexOf(name) >= 0 ? 'active' : '';
    };

    $scope.closeAlert = function (index) {
      $rootScope.messages().splice(index, 1);
    };

    $scope.home = function () {
      if (!!$rootScope.getUser().usename) {
        $state.go('user');
      } else {
        $state.go('home');
      }
    };

    $scope.signin = function () {
      $state.go('login');
    };

    $scope.signup = function () {
      $state.go('register');
    };

    $scope.signout = function () {
      userService.logout().then(function () {
        $rootScope.removeUser();
        $state.go('home');
      });
    };
  });

  as.controller('loginController', function ($scope, $rootScope, $state, $cookies, userService, geetestService) {
    geetestService.initGeetest();

    var login = function (username, password) {
      if (geetestService.getCheckResult()) {
        userService.login(username, password).then(function (res) {
          var accessToken = res.headers('token');
          var adminUser = false;
          var auth = {authority: 'ROLE_ADMIN'};
          res.data.authorities.forEach(function (authority) {
            if (angular.equals(authority, auth)) {
              adminUser = true;
            }
          });
          var user = {
            username: res.data.username,
            isActive: res.data.enabled,
            isAdmin: adminUser,
            token: accessToken
          };
          $rootScope.user = user;
          var expireDate = new Date();
          expireDate.setHours(expireDate.getHours() + 1);
          $cookies.putObject('user', user, {expires: expireDate});
          $state.go('user');
        });
      } else {
        document.getElementById("captcha-help-block").style.visibility = "visible";
      }
    };

    $scope.login = function (username, password) {
      login(username, password);
    };
  });

  as.controller('productController', function ($scope, $state, productService) {
    $scope.page = 1;
    $scope.keyword = '';
    $scope.orderName = '+name';

    var load = function () {
      productService.list($scope.keyword, $scope.page - 1).then(function (res) {
        $scope.products = res.data.content;
        $scope.totalItems = res.data.totalElements;
      });
    };

    load();

    $scope.search = function () {
      load();
    };

    $scope.orderByName = function (property) {
      $scope.orderName = ($scope.orderName[0] === '+' ? '-' : '+') + property;
    };
    $scope.orderIconName = function (property) {
      return property === $scope.orderName.substring(1) ? $scope.orderName[0] === '+' ?
        'glyphicon glyphicon-chevron-up' : 'glyphicon glyphicon-chevron-down' : '';
    };

    $scope.add = function () {
      $state.go('productAdd');
    };

    $scope.delete = function (product) {
      if (confirm('确认删除产品[' + product.name + ']吗?')) {
        productService.delete(product.id).success(function () {
          load();
        });
      }
    };
  });

  as.controller('productAddController', function ($scope, $state, productService) {
    $scope.editPage = false;

    $scope.save = function () {
      productService.add($scope.product).success(function () {
        $state.go('product');
      });
    };

    $scope.cancel = function () {
      $state.go('product');
    };
  });

  as.controller('productEditController', function ($scope, $stateParams, productService, arrayFactory) {
    $scope.isList = true;
    $scope.order = '+left';

    var load = function () {
      productService.get($stateParams.id).then(function (res) {
        $scope.product = res.data;
        $scope.scopes = res.data.scopes;
      });
    };

    load();

    $scope.search = function () {
      productService.list($scope.keyword, $scope.page - 1).then(function (res) {
        $scope.products = res.data.content;
        $scope.totalItems = res.data.totalElements;
      });
    };

    $scope.cancel = function () {
      load();
      $scope.isList = true;
    };

    $scope.add = function () {
      $scope.isList = false;
    };

    $scope.orderBy = function (property) {
      $scope.order = ($scope.order[0] === '+' ? '-' : '+') + property;
    };
    $scope.orderIcon = function (property) {
      return property === $scope.order.substring(1) ? $scope.order[0] === '+' ?
        'glyphicon glyphicon-chevron-up' : 'glyphicon glyphicon-chevron-down' : '';
    };


    $scope.addScope = function () {
      $scope.product.scopes.push($scope.scope);
      productService.modify($scope.product).success(function () {
        load();
        $scope.isList = true;
      });
    };

    $scope.delete = function (scope) {
      if (confirm('确认删除 ' + scope.right + ' 接口吗?')) {
        arrayFactory.removeFrom(scope, $scope.product.scopes);
        productService.modify($scope.product).success(function () {
          load();
          $scope.isList = true;
        });
      }
    };
  });

  as.controller('appController', function ($scope, $rootScope, $state, appService) {
    $scope.page = 1;
    $scope.keyword = '';
    $scope.order = '+appName';

    var load = function () {
      appService.listByUser($scope.keyword, $rootScope.getUser().username, $scope.page - 1).then(function (res) {
        $scope.apps = res.data.content;
        $scope.totalItems = res.data.totalElements;
      });
    };

    load();

    $scope.search = function () {
      load();
    };

    $scope.orderBy = function (property) {
      $scope.order = ($scope.order[0] === '+' ? '-' : '+') + property;
    };
    $scope.orderIcon = function (property) {
      return property === $scope.order.substring(1) ? $scope.order[0] === '+' ?
        'glyphicon glyphicon-chevron-up' : 'glyphicon glyphicon-chevron-down' : '';
    };

    $scope.add = function () {
      $state.go('appAdd');
    };

    $scope.delete = function (app) {
      if (confirm('确认删除应用[' + app.appName + ']吗')) {
        appService.delete(app.id).success(function () {
          load();
        });
      }
    };
  });

  as.controller('appAdminController', function ($scope, appService) {
    $scope.page = 1;
    $scope.keyword = '';
    $scope.order = '+appName';

    var load = function () {
      appService.list($scope.keyword, $scope.page - 1).then(function (res) {
        $scope.apps = res.data.content;
        $scope.totalItems = res.data.totalElements;
      });
    };

    load();

    $scope.search = function () {
      load();
    };

    $scope.orderBy = function (property) {
      $scope.order = ($scope.order[0] === '+' ? '-' : '+') + property;
    };
    $scope.orderIcon = function (property) {
      return property === $scope.order.substring(1) ? $scope.order[0] === '+' ?
        'glyphicon glyphicon-chevron-up' : 'glyphicon glyphicon-chevron-down' : '';
    };

    $scope.delete = function (app) {
      if (confirm('确认删除应用[' + app.appName + ']吗')) {
        appService.delete(app.id).success(function () {
          load();
        });
      }
    };
  });

  as.controller('appAdminViewController', function ($scope, $state, $stateParams, appService, productService) {
    var appScopes = [];

    var load = function () {
      appService.get($stateParams.id).then(function (res) {
        $scope.app = res.data;
        appScopes = $scope.app.scopes;
        productService.getByName($scope.app.bindProduct).then(function (res) {
          $scope.productScopes = res.data.scopes;
        });
      });
    };

    load();

    $scope.check = function (productScope) {
      return appScopes.indexOf(productScope) >= 0;
    };

    $scope.audit = function () {
      if (confirm('确认审核应用[' + $scope.app.appName + ']吗?')) {
        appService.audit($scope.app.id).success(function () {
          $state.go('appManager');
        });
      }
    };

    $scope.cancel = function () {
      $state.go('appManager');
    };
  });

  as.controller('appAddController', function ($scope, $rootScope, $state, appService, productService, arrayFactory) {
    $scope.editPage = false;
    var selectScopes = [];

    var load = function () {
      productService.listAll().then(function (res) {
        $scope.products = res.data;
        $scope.selectedProduct = $scope.products[0];
        changeAppScopes($scope.selectedProduct);
      });
    };

    load();

    var changeAppScopes = function (product) {
      $scope.productScopes = product.scopes;
    };

    $scope.changeScopes = function (product) {
      changeAppScopes(product);
    };

    $scope.check = function (appScope, checked) {
      if (checked == true) {
        selectScopes.push(appScope);
      } else {
        arrayFactory.removeFrom(appScope, selectScopes);
      }
    };

    $scope.save = function () {
      $scope.app.user = $rootScope.getUser().username;
      $scope.app.bindProduct = $scope.selectedProduct.name;
      $scope.app.scopes = selectScopes;
      appService.add($scope.app).success(function () {
        $state.go('app');
      });
    };

    $scope.cancel = function () {
      $state.go('app');
    };
  });

  as.controller('appEditController', function ($scope, $stateParams, $state, appService, productService, arrayFactory) {
    $scope.editPage = true;
    var selectScopes = [];

    var load = function () {
      appService.get($stateParams.id).then(function (res) {
        $scope.app = res.data;
        $scope.app.scopes.forEach(function (scope) {
          selectScopes.push(scope);
        });
        productService.getByName($scope.app.bindProduct).then(function (res) {
          var product = res.data;
          productService.listAll().then(function (res) {
            $scope.products = res.data;
            arrayFactory.removeFrom(product, $scope.products);
            $scope.products.unshift(product);
            $scope.selectedProduct = $scope.products[0];
            changeAppScopes($scope.selectedProduct);
          });
        });
      });
    };

    load();

    var changeAppScopes = function (product) {
      $scope.productScopes = product.scopes;
    };

    $scope.changeScopes = function (product) {
      changeAppScopes(product);
    };

    $scope.check = function (appScope, checked) {
      if (checked == true) {
        selectScopes.push(appScope);
      } else {
        arrayFactory.removeFrom(appScope, selectScopes);
      }
    };

    $scope.checkPush = function (productScope) {
      return $scope.app.scopes.indexOf(productScope) >= 0;
    };

    $scope.cancel = function () {
      $state.go('app');
    };

    $scope.save = function () {
      $scope.app.bindProduct = $scope.selectedProduct.name;
      $scope.app.scopes = selectScopes;
      appService.modify($scope.app).success(function () {
        $state.go('app');
      });
    };
  });

  as.controller('userController', function ($scope, $rootScope, $state, userService, arrayFactory) {

  });

  as.controller('userAdminController', function ($scope, userService) {
    $scope.page = 1;
    $scope.keyword = '';
    $scope.order = '+username';

    var load = function () {
      userService.list($scope.keyword, $scope.page - 1).then(function (res) {
        $scope.users = res.data.content;
        //arrayFactory.removeFrom($rootScope.getUser(), $scope.users);
        $scope.totalItems = res.data.totalElements;
      });
    };

    load();

    $scope.search = function () {
      load();
    };

    $scope.isAdminUser = function (roles) {
      return roles.indexOf('ROLE_ADMIN') >= 0;
    };

    $scope.orderBy = function (property) {
      $scope.order = ($scope.order[0] === '+' ? '-' : '+') + property;
    };
    $scope.orderIcon = function (property) {
      return property === $scope.order.substring(1) ? $scope.order[0] === '+' ? 'glyphicon glyphicon-chevron-up' : 'glyphicon glyphicon-chevron-down' : '';
    };

    $scope.delete = function (user) {
      if (confirm('确认删除用户[' + user.username + ']吗?')) {
        userService.delete(user.id).success(function () {
          load();
        });
      }
    };
  });

  as.controller('userRegisterController', function ($scope, $state, userService, geetestService) {
    geetestService.initGeetest();

    $scope.save = function () {
      if (geetestService.getCheckResult()) {
        userService.add($scope.user).success(function () {
          $state.go('login');
        });
      } else {
        document.getElementById("captcha-help-block").style.visibility = "visible";
      }
    };

    $scope.cancel = function () {
      $state.go('home');
    };
  });

  as.controller('userEditController', function ($scope, $stateParams, $state, userService) {
    $scope.editPage = true;

    //var load = function () {
    //  userService.get($stateParams.id).then(function (res) {
    //    $scope.user = res.data;
    //  });
    //};
    //
    //load();

    $scope.cancel = function () {
      $state.go('user');
    };

    $scope.save = function () {
      userService.modify($scope.user).success(function () {
        $state.go('user');
      });
    };
  });

  as.controller('authorizeController', function ($scope, $state, $window, authorizeService, geetestService) {
    $scope.login = false;

    geetestService.initGeetest();

    var load = function () {
      authorizeService.get($scope.appid).then(function (res) {
        $scope.app = res.data;
      });
    };

    $scope.auth = function () {
      load();
      $scope.login = true;
    };

    $scope.submit = function () {
      if (geetestService.getCheckResult()) {
        authorizeService.authorize($scope.appid, $scope.redirectUrl, $scope.user).then(function (res) {
          $window.location = res.data.locationUri;
        });
      } else {
        $scope.incorrect = true;
      }
    };

    $scope.cancel = function () {
      $state.path('home');
    };
  });

  as.controller('accessTokenController', function ($scope, $state, accessTokenService) {
    $scope.auth = true;

    $scope.submit = function () {
      accessTokenService.fetchToken($scope.appid, $scope.appSecret, $scope.code, $scope.redirectUrl).then(function (res) {
        $scope.data = res.data;
        $scope.auth = false;
      });
    };

    $scope.cancel = function () {
      $state.go('home');
    };
  });

}());