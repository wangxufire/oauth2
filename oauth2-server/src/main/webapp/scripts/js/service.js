'use strict';

(function () {
  var as = angular.module('oauth2.services', []);

  as.service('productService', function ($http, $q) {
    this.list = function (keyword, page) {
      return $http.get('api/product/list' + '?keyword=' + keyword + '&page=' + page).then(function (res) {
        return $q.when(res);
      }, function (res) {
        return $q.reject(res);
      });
    };

    this.listAll = function () {
      return $http.get('api/product/listAll').then(function (res) {
        return $q.when(res);
      }, function (res) {
        return $q.reject(res);
      });
    };

    this.add = function (product) {
      return $http.post('api/product/create', product);
    };

    this.get = function (id) {
      return $http.get('api/product/' + id).then(function (res) {
        return $q.when(res);
      }, function (res) {
        return $q.reject(res);
      });
    };

    this.getByName = function (name) {
      return $http.get('api/product/getByName/' + name).then(function (res) {
        return $q.when(res);
      }, function (res) {
        return $q.reject(res);
      });
    };

    this.modify = function (product) {
      return $http.post('api/product/update', product);
    };

    this.delete = function (id) {
      return $http.delete('api/product/delete/' + id);
    };
  });

  as.service('appService', function ($http, $q) {
    this.list = function (keyword, page) {
      return $http.get('api/app/list' + '?keyword=' + keyword + '&page=' + page).then(function (res) {
        return $q.when(res);
      }, function (res) {
        return $q.reject(res);
      });
    };

    this.listByUser = function (keyword, user, page) {
      return $http.get('api/app/listUserApps' + '?keyword=' + keyword + '&user=' + user
        + '&page=' + page).then(function (res) {
        return $q.when(res);
      }, function (res) {
        return $q.reject(res);
      });
    };

    this.add = function (app) {
      return $http.post('api/app/create', app);
    };

    this.audit = function (id) {
      return $http.post('api/app/audit/' + id);
    };

    this.get = function (id) {
      return $http.get('api/app/' + id).then(function (res) {
        return $q.when(res);
      }, function (res) {
        return $q.reject(res);
      });
    };

    this.modify = function (app) {
      return $http.post('api/app/update', app);
    };

    this.delete = function (id) {
      return $http.delete('api/app/delete/' + id);
    };
  });

  as.service('userService', function ($http, $q) {
    this.list = function (keyword, page) {
      return $http.get('api/user/list' + '?keyword=' + keyword + '&page=' + page).then(function (res) {
        return $q.when(res);
      }, function (res) {
        return $q.reject(res);
      });
    };

    this.login = function (username, password) {
      var user = {
        username: username,
        password: password
      };
      return $http.post('api/user/login', user).then(function (res) {
        return $q.when(res);
      }, function (res) {
        return $q.reject(res);
      });
    };

    this.logout = function () {
      return $http.get('api/user/logout').then(function (res) {
        return $q.when(res);
      }, function (res) {
        return $q.reject(res);
      });
    };

    this.add = function (user) {
      return $http.post('api/user/create', user);
    };

    this.get = function (id) {
      return $http.get('api/user/' + id).then(function (res) {
        return $q.when(res);
      }, function (res) {
        return $q.reject(res);
      });
    };

    this.modify = function (app) {
      return $http.post('api/user/update', app);
    };

    this.delete = function (id) {
      return $http.delete('api/user/delete/' + id);
    };
  });

  as.service('authorizeService', function ($http, $q) {
    this.get = function (appid) {
      return $http.get('api/app/query/' + appid).then(function (res) {
        return $q.when(res);
      }, function (res) {
        return $q.reject(res);
      });
    };

    this.authorize = function (appid, redirectUrl, user) {
      return $http.post('authorize?client_id=' + appid +
        '&response_type=code&redirect_uri=' + redirectUrl, user).then(function (res) {
        return $q.when(res);
      }, function (res) {
        return $q.reject(res);
      });
    };
  });

  as.service('accessTokenService', function ($http, $q) {
    this.fetchToken = function (appid, appSecret, code, redirectUrl) {
      var postData = 'client_id=' + appid + '&client_secret=' + appSecret + '&code=' + code
        + '&response_type=code&grant_type=authorization_code&redirect_uri=' + redirectUrl;
      return $http({
        method: 'POST',
        url: 'accessToken',
        data: postData,
        headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
      }).then(function (res) {
        return $q.when(res);
      }, function (res) {
        return $q.reject(res);
      });
    };
  });

  as.service('geetestService', function ($http, $window) {
    var check = false;

    this.initGeetest = function () {
      check = false;
      $http.get('startCaptchaServlet').success(function (res) {
        loadGeetest(res);
      });
    };

    this.getCheckResult = function () {
      return check;
    };

    function geetestAjaxCheckResults() {
      $http.post('verifyServlet', gt_login_captcha.getValidate()).success(function (res) {
        check = res === 'success';
      });
    }

    var loadGeetest = function (config) {
      $window.gt_login_captcha = new $window.Geetest({
        gt: config.gt,
        challenge: config.challenge,
        product: 'float',
        lang: 'zh-cn',
        sandbox: false
      });
      gt_login_captcha.appendTo("#captcha-box");
      gt_login_captcha.onFail(function () {
        document.getElementById("captcha-help-block").style.visibility = "visible";
      });
      gt_login_captcha.onSuccess(function () {
        document.getElementById("captcha-help-block").style.visibility = "hidden";
        geetestAjaxCheckResults()
      });
    };
  });

  as.factory('arrayFactory', function () {
    var factory = {};

    var indexOf = function (element, array) {
      for (var i = 0, j; j = array[i]; i++) {
        if (angular.equals(element, j)) {
          return i;
        }
      }
      return -1;
    };

    factory.indexOf = function (element, array) {
      indexOf(element, array);
    };

    factory.removeFrom = function (element, array) {
      var index = indexOf(element, array);
      return index < 0 ? array : array.splice(index, 1);
    };

    return factory;
  });

})();