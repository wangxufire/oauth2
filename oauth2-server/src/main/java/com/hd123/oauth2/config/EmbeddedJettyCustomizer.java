package com.hd123.oauth2.config;

import static com.google.common.base.Strings.repeat;
import static com.hd123.oauth2.common.Constants.EQUAL;
import static org.apache.logging.log4j.LogManager.getLogger;
import static org.eclipse.jetty.http.HttpVersion.HTTP_2;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * 内嵌容器配置
 *
 * @author liyue
 * @since 0.1.0
 */
@Configuration
public class EmbeddedJettyCustomizer implements EmbeddedServletContainerCustomizer {

  private final Logger logger = getLogger(EmbeddedJettyCustomizer.class);

  @Autowired
  private AppProperties appProperties;
  @Autowired
  private ServerProperties serverProperties;

  @Override
  public void customize(ConfigurableEmbeddedServletContainer container) {
    if (container instanceof JettyEmbeddedServletContainerFactory) {
      customizeJetty((JettyEmbeddedServletContainerFactory) container);
    }
  }

  /**
   * Config Jetty Connector
   *
   * @param factory
   *          factory
   */
  private void customizeJetty(JettyEmbeddedServletContainerFactory factory) {
    factory.addServerCustomizers(server -> {
      /* HTTP */
      final ServerConnector connector = new ServerConnector(server);
      connector.setPort(serverProperties.getPort());

      /* HTTPS */
      final SslContextFactory sslContextFactory = new SslContextFactory();
      sslContextFactory.setKeyStorePassword(appProperties.getTls().getKeyStorePassword());
      try {
        sslContextFactory.setKeyStorePath(new ClassPathResource(appProperties.getTls()
            .getKeyStorePath()).getURL().toString());
      } catch (IOException ignored) {
        if (logger.isErrorEnabled()) {
          logger.error("\n" + repeat(EQUAL, 100)
              + "\nNo KeyStore Found in ClassPath, HTTPS Will Not be Available\n"
              + repeat(EQUAL, 100));
        }
      }

      final HttpConfiguration https = new HttpConfiguration();
      https.addCustomizer(new SecureRequestCustomizer());

      final ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(
          sslContextFactory, HTTP_2.asString()), new HttpConnectionFactory(https));
      sslConnector.setPort(appProperties.getTls().getPort());

      server.setConnectors(new Connector[] {
          connector, sslConnector });
    });
  }
}