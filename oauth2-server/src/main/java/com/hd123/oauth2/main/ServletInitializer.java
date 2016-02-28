package com.hd123.oauth2.main;

import static com.google.common.base.Strings.repeat;
import static com.hd123.oauth2.common.ProfileConstants.DEVELOPMENT;
import static com.hd123.oauth2.common.ProfileConstants.PRODUCTION;
import static com.hd123.oauth2.main.ServletInitializer.AUTH_PACKAGE;
import static com.hd123.oauth2.main.ServletInitializer.MONGO_REPOSITORY;
import static java.lang.System.getenv;
import static java.net.InetAddress.getLocalHost;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.logging.log4j.LogManager.getLogger;
import static org.springframework.boot.Banner.Mode.LOG;
import static org.springframework.context.annotation.AdviceMode.ASPECTJ;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.CommandLinePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import com.hd123.oauth2.util.ProfileUtil;

/**
 * 程序入口
 *
 * @author liyue
 */
@Configuration
@EnableWebSecurity
@EnableResourceServer
@EnableConfigurationProperties
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync(proxyTargetClass = true, mode = ASPECTJ)
@EnableCaching(proxyTargetClass = true, mode = ASPECTJ)
@EnableMongoRepositories(basePackages = MONGO_REPOSITORY)
@ComponentScan(basePackages = AUTH_PACKAGE, scopedProxy = TARGET_CLASS)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableAutoConfiguration(exclude = {
    EmbeddedMongoAutoConfiguration.class, DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class })
public class ServletInitializer extends SpringBootServletInitializer {

  protected static final String AUTH_PACKAGE = "com.hd123.oauth2";
  protected static final String MONGO_REPOSITORY = "com.hd123.oauth2.repository";

  private static final Logger logger = getLogger(ServletInitializer.class);

  @Autowired
  private ProfileUtil profileUtil;

  /**
   * Configure the application for deploy.
   *
   * @param application
   *          Builder for {@link SpringApplication} and
   *          {@link ApplicationContext}
   */
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    final SpringApplicationBuilder springAppBuilder = application.bannerMode(LOG)
        .banner(new HeadingOAuth2Banner()).addCommandLineProperties(true)
        .sources(ServletInitializer.class);

    return springAppBuilder;
  }

  /**
   * Main method, used to run the application.
   *
   * <p>
   * Not Support JSP
   * </p>
   *
   * @param args
   *          启动参数
   * @throws Exception
   *           Exception
   */
  public static void main(String[] args) throws Exception {
    final SpringApplication application = new SpringApplication(ServletInitializer.class);
    final CommandLinePropertySource source = new SimpleCommandLinePropertySource(args);

    application.setBannerMode(LOG);
    application.setBanner(new HeadingOAuth2Banner());
    application.setAddCommandLineProperties(true);
    final ApplicationContext appCtx = application.run(args);
    final Environment env = appCtx.getEnvironment();
    final boolean isProdProfile = addDefaultProfile(application, source, env);

    if (logger.isInfoEnabled()) {
      final String port = env.getProperty("server.port");
      final String securePort = env.getProperty("app.tls.port");
      final String externalIp = getLocalHost().getHostAddress();
      final String contextPathNew = env.getProperty("server.context-path");
      final String contextPathOld = env.getProperty("server.contextPath");
      final String contextPath = isBlank(contextPathNew) ? isBlank(contextPathOld) ? EMPTY
          : contextPathOld : contextPathNew;

      final StringBuilder builder = new StringBuilder("Access URLs:\n");
      builder.append(repeat("-", 60)).append("\n\tLocal: \thttp://127.0.0.1:{}")
          .append(contextPath).append("\n\tExternal: \thttp://{}:{}").append(contextPath)
          .append("\n\tLocal Secure: \thttps://127.0.0.1:{}").append(contextPath)
          .append("\n\tExternal Secure: \thttps://{}:{}").append(contextPath).append("\n");
      if (isProdProfile) {
        builder.append(repeat("-", 60));
        logger.info(builder.toString(), port, externalIp, port, securePort, externalIp, securePort);
      } else {
        builder.append("\tAPI Documentation: \thttp://127.0.0.1:{}/doc/index.html\n").append(
            repeat("-", 60));
        logger.info(builder.toString(), port, externalIp, port, securePort, externalIp, securePort,
            port);
      }
    }
  }

  /**
   * If no profile has been configured, set by default the "dev" profile.
   *
   * @param application
   *          application
   * @param source
   *          source
   * @param env
   *          env
   * @return isDevProfile
   */
  private static boolean addDefaultProfile(SpringApplication application,
      CommandLinePropertySource source, Environment env) {
    if ((source == null || !source.containsProperty("spring.profiles.active"))
        && !getenv().containsKey("SPRING_PROFILES_ACTIVE")) {
      if (env.getActiveProfiles().length <= 0) {
        application.setAdditionalProfiles(DEVELOPMENT);
      }
    }

    return asList(env.getActiveProfiles()).contains(PRODUCTION);
  }

  /**
   * <p>
   * Spring profiles can be configured with a program arguments
   * --spring.profiles.active=your-active-profile
   * <p/>
   *
   * @throws IOException
   *           IOException
   */
  @PostConstruct
  public void checkApplicationProfiles() throws IOException {
    final boolean warnAble = logger.isWarnEnabled();
    final boolean infoAble = logger.isInfoEnabled();
    final boolean errorAble = logger.isErrorEnabled();

    final Collection<String> profiles = profileUtil.getProfiles();
    if (profiles.size() <= 0) {
      if (warnAble) {
        logger.warn("No Spring profile configured, running with default configuration");
      }
    } else {
      if (infoAble) {
        logger.info("Running with Spring profile(s) : {}", profiles);
      }

      if (errorAble) {
        if (profileUtil.isDevAndProd()) {
          logger
              .error("You have misconfigured your application! It should not run with both the 'dev' and 'prod' profiles at the same time.");
        }
      }
    }
  }

}