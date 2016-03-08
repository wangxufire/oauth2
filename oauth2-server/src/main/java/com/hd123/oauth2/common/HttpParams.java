package com.hd123.oauth2.common;

import static java.nio.charset.Charset.forName;
import static org.apache.commons.lang3.CharEncoding.ISO_8859_1;
import static org.apache.commons.lang3.CharEncoding.US_ASCII;

import java.nio.charset.Charset;

/**
 * http常量
 *
 * @author liyue
 * @since 0.0.1
 */
public final class HttpParams {

  public static final int CR = 13; // <US-ASCII CR, carriage return (13)>
  public static final int LF = 10; // <US-ASCII LF, linefeed (10)>
  public static final int SP = 32; // <US-ASCII SP, space (32)>
  public static final int HT = 9; // <US-ASCII HT, horizontal-tab (9)>

  /** HTTP header definitions */
  public static final String TRANSFER_ENCODING = "Transfer-Encoding";
  public static final String CONTENT_LEN = "Content-Length";
  public static final String CONTENT_TYPE = "Content-Type";
  public static final String ACCEPT_CHARSET = "Accept-Charset";
  public static final String CONTENT_ENCODING = "Content-Encoding";
  public static final String EXPECT_DIRECTIVE = "Expect";
  public static final String CONN_DIRECTIVE = "Connection";
  public static final String TARGET_HOST = "Host";
  public static final String USER_AGENT = "User-Agent";
  public static final String DATE_HEADER = "Date";
  public static final String SERVER_HEADER = "Server";
  public static final String XAUTH_TOKEN_HEADER_NAME = "x-auth-token";

  public static final String ENCODING = "encoding";
  public static final String HTTP_METHOD = "http-method";
  public static final String REMOTE_IP = "remote-ip";
  public static final String SESSION_ID = "session-id";
  public static final String TARGET_CONTROLLER = "target-controller";
  public static final String TARGET_METHOD = "target-method";
  public static final String METHOD_DESCRIPTION = "method-description";
  public static final String X_REAL_IP = "X-Real-IP";
  public static final String X_FORWARDED_FOR = "X-Forwarded-For";
  public static final String REQUEST_URI = "request-uri";
  public static final String STATUS_CODE = "javax.servlet.error.status_code";
  public static final String HTTP11_PROTOCOL = "http/1.1";
  public static final String HTTP2_PROTOCOL = "http/2";

  public static final String PRAGMA = "Pragma";
  public static final String NO_STORE = "no-store";
  public static final String NO_CACHE = "no-cache";
  public static final String NO_CACHE_STORE = "no-cache, no-store";
  public static final String CACHE_CONTROL = "Cache-Control";

  public static final String PROTOCOL_CONTENT_CHARSET = "http.protocol.content-charset";

  /** HTTP expectations */
  public static final String EXPECT_CONTINUE = "100-continue";

  /** HTTP connection control */
  public static final String CONN_CLOSE = "Close";
  public static final String CONN_KEEP_ALIVE = "Keep-Alive";

  /** Transfer encoding definitions */
  public static final String CHUNK_CODING = "chunked";
  public static final String IDENTITY_CODING = "identity";

  public static final Charset DEF_CONTENT_CHARSET = forName(ISO_8859_1);
  public static final Charset DEF_PROTOCOL_CHARSET = forName(US_ASCII);

  public static final String DEFAULT_CONTENT_CHARSET = ISO_8859_1;
  public static final String DEFAULT_PROTOCOL_CHARSET = US_ASCII;

  public final static String OCTET_STREAM_TYPE = "application/octet-stream";
  public final static String PLAIN_TEXT_TYPE = "text/plain";

  public final static String CHARSET_PARAM = "; charset=";
  public final static String CHARSET_UTF8 = "charset=UTF-8";

  public final static String DEFAULT_CONTENT_TYPE = OCTET_STREAM_TYPE;

  private HttpParams() {
  }

  public static boolean isWhitespace(final char ch) {
    return ch == SP || ch == HT || ch == CR || ch == LF;
  }

}