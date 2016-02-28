package com.hd123.oauth2.main;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.boot.ansi.AnsiColor.BLUE;
import static org.springframework.boot.ansi.AnsiColor.BRIGHT_CYAN;
import static org.springframework.boot.ansi.AnsiColor.CYAN;
import static org.springframework.boot.ansi.AnsiColor.DEFAULT;
import static org.springframework.boot.ansi.AnsiColor.GREEN;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.core.env.Environment;

/**
 * Heading OAuth2 Banner implementation.
 *
 * @author liyue
 * @since 0.0.1
 */
public class HeadingOAuth2Banner implements Banner {

  @SuppressWarnings("unchecked")
  private static final String[] BANNER = {
      "                                                                                            ",
      " _   _ _____    _    ____ ___ _    _  _____       ___    _   _   _ _____ _   _ ____         ",
      "| | | | ____|  / \\  |  _ \\_ _| \\  | |/  ___|     / _ \\  / \\ | | | |_   _| | | |___ \\  ",
      "| |_| |  _|   / _ \\ | | | | ||  \\ | |  |  _     | | | |/ _ \\| | | | | | | |_| | __) |    ",
      "|  _  | |___ / ___ \\| |_| | || | \\  |  |_| |    | |_| / ___ \\ |_| | | | |  _  |/ __/     ",
      "|_| |_|_____/_/   \\_\\____/___|_|  \\_|\\_____|     \\___/_/   \\_\\___/  |_| |_| |_|_____|" };

  private static final int STRAP_LINE_SIZE = 50;

  private static String APP_VERSION = EMPTY;

  static {
    try (final InputStream pomPropertiesStream = HeadingOAuth2Banner.class
        .getResourceAsStream("/META-INF/maven/com.hd123.oauth2/oauth2-server/pom.properties")) {
      if (pomPropertiesStream != null) {
        final Properties pomProperties = new Properties();
        pomProperties.load(pomPropertiesStream);
        // APP_NAME = pomProperties.getProperty("artifactId");
        APP_VERSION = pomProperties.getProperty("version");
      }
    } catch (IOException ignored) {
      // ignore
    }
  }

  @Override
  public void printBanner(Environment environment, Class<?> sourceClass, PrintStream printStream) {
    for (final String line : BANNER) {
      printStream.println(AnsiOutput.toString(BRIGHT_CYAN, line));
    }

    String appName = EMPTY;
    String appVersion = EMPTY;
    String info = EMPTY;
    if (isNotBlank(APP_VERSION)) {
      final String APP_NAME = "Heading OAuth2 Server";
      appName = " :: " + APP_NAME + " :: ";
      appVersion = "(v" + APP_VERSION + ")";
      info = "[developer : liyue]";
    }

    String padding = EMPTY;
    while (padding.length() < STRAP_LINE_SIZE - (appVersion.length() + appName.length())) {
      padding += " ";
    }

    printStream.println(AnsiOutput.toString(GREEN, appName, DEFAULT, padding, BLUE, appVersion,
        DEFAULT, padding, DEFAULT, padding, CYAN, info) + "\n");
  }

}