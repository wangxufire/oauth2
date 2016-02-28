package com.hd123.oauth2.controller;

import static com.hd123.oauth2.common.HttpParams.STATUS_CODE;
import static com.hd123.oauth2.support.ExceptionCode.failed;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.valueOf;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hd123.oauth2.config.AppProperties;
import com.hd123.oauth2.config.AppProperties.Info;
import com.hd123.oauth2.rest.RsResponse;

/**
 * 全局拦截转发器
 *
 * @author liyue
 */
@Controller
@RequestMapping(consumes = ALL_VALUE)
public class CommonController implements ErrorController {

  private static final String ERROR_PATH = "/error";

  @Autowired
  private AppProperties appProperties;

  @ResponseBody
  @RequestMapping(path = "/info", method = {
      GET, POST })
  public Info info() {
    return appProperties.getInfo();
  }

  @ResponseBody
  @RequestMapping(path = ERROR_PATH, method = {
      GET, POST })
  public ResponseEntity<RsResponse> error(HttpServletRequest request, HttpServletResponse response) {
    final Integer statusCode = (Integer) request.getAttribute(STATUS_CODE);
    final HttpStatus status = statusCode == null ? INTERNAL_SERVER_ERROR : valueOf(statusCode);
    return new ResponseEntity<RsResponse>(new RsResponse(failed), status);
  }

  @Override
  public String getErrorPath() {
    return ERROR_PATH;
  }

}