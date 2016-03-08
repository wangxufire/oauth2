package com.hd123.oauth2.config;

import static com.google.common.base.Strings.repeat;
import static com.hd123.oauth2.common.Constants.EQUAL;
import static org.apache.logging.log4j.LogManager.getLogger;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * 系统初始化后的一些检查
 *
 * @author liyue
 */
@Component
public class InitCheckAfterStart implements ApplicationRunner {

  private final Logger logger = getLogger(InitCheckAfterStart.class);

  @Autowired
  private CacheManager cacheManager;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    if (logger.isDebugEnabled()) {
      logger.info("\n" + repeat(EQUAL, 100) + "\nUsing Cache Manager: "
          + cacheManager.getClass().getName() + "\n" + repeat(EQUAL, 100));
    }
  }

}