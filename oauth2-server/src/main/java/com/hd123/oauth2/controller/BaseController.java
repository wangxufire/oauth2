package com.hd123.oauth2.controller;

import static org.apache.logging.log4j.LogManager.getLogger;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.oauth2.service.AppService;
import com.hd123.oauth2.service.OAuthService;
import com.hd123.oauth2.service.ProductService;
import com.hd123.oauth2.service.UserService;

/**
 * 转发器基类
 *
 * @author liyue
 */
@Component
public class BaseController {

  protected final Logger logger = getLogger(getClass());
  protected static final String PATH = "/api/";

  @Autowired
  protected OAuthService oAuthService;
  @Autowired
  protected AppService appService;
  @Autowired
  protected UserService userService;
  @Autowired
  protected ProductService productService;

}