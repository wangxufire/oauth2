package com.hd123.oauth2.controller;

import static com.hd123.oauth2.controller.Router.Route.route;
import static com.hd123.oauth2.controller.Router.Route.v1Route;
import static com.hd123.oauth2.controller.Router.Route.v2Route;

/**
 * @author liyue
 */
@Deprecated
public final class Router {

  public static final Route API_PATH = route("**", "应用api基础路径");
  public static final Route API_V1_PATH = v1Route("**", "v1版本api基础路径");
  public static final Route API_V2_PATH = v2Route("**", "v2版本api基础路径");

  // app
  public static final Route APP_LIST = route("list", "获取应用列表");

  public static final class Route {

    private static final String API_PATH = "/api/";
    private static final String API_V1_PATH = "/api/v1/";
    private static final String API_V2_PATH = "/api/v2/";

    private String path;
    private String name;

    private Route() {
    }

    private Route(String name, String path) {
      this.name = name;
      this.path = path;
    }

    public String path() {
      return path;
    }

    public String name() {
      return name;
    }

    public static Route route(String basePath, String apiName) {
      return new Route(api(basePath), apiName);
    }

    public static Route v1Route(String basePath, String apiName) {
      return new Route(apiV1(basePath), apiName);
    }

    public static Route v2Route(String basePath, String apiName) {
      return new Route(apiV2(basePath), apiName);
    }

    private static String api(String basePath) {
      return API_PATH + basePath;
    }

    private static String apiV1(String basePath) {
      return API_V1_PATH + basePath;
    }

    private static String apiV2(String basePath) {
      return API_V2_PATH + basePath;
    }

  }

}