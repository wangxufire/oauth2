package com.hd123.oauth2.config;

import static org.apache.logging.log4j.LogManager.getLogger;

import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 系统初始化后的一些检查
 *
 * @author liyue
 */
@Component
public class InitCheckAfterStart implements CommandLineRunner {

  private final Logger logger = getLogger(InitCheckAfterStart.class);

  // @Autowired
  // private CacheManager cacheManager;

  @Override
  public void run(String... strings) throws Exception {
    if (logger.isDebugEnabled()) {
      // logger.info("\n" + repeat(EQUAL, 100) + "\nUsing Cache Manager: "
      // + cacheManager.getClass().getName() + "\n" + repeat(EQUAL, 100));
    }
  }

}