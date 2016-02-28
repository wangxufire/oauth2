package com.hd123.oauth2.common;

import static com.hd123.oauth2.common.HttpParams.CHARSET_PARAM;
import static org.apache.commons.lang3.CharEncoding.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;
import static org.springframework.http.MediaType.parseMediaType;

import org.springframework.http.MediaType;

/**
 * HttpMediaType
 *
 * @author liyue
 * @since 0.0.1
 */
public interface HttpMediaType {

  String APPLICATION_URLENCODED_VALUE_UTF_8 = APPLICATION_FORM_URLENCODED_VALUE + CHARSET_PARAM
      + UTF_8;
  MediaType APPLICATION_URLENCODED_UTF_8 = parseMediaType(APPLICATION_URLENCODED_VALUE_UTF_8);

  String APPLICATION_JSON_VALUE_UTF_8 = APPLICATION_JSON_VALUE + CHARSET_PARAM + UTF_8;
  MediaType APPLICATION_JSON_UTF_8 = parseMediaType(APPLICATION_JSON_VALUE_UTF_8);

  String TEXT_XML_VALUE_UTF_8 = TEXT_XML_VALUE + CHARSET_PARAM + UTF_8;
  MediaType APPLICATION_XML_UTF_8 = parseMediaType(TEXT_XML_VALUE_UTF_8);

  String APPLICATION_XML_VALUE_UTF_8 = APPLICATION_XML_VALUE + CHARSET_PARAM + UTF_8;
  MediaType TEXT_XML_UTF_8 = parseMediaType(TEXT_XML_VALUE_UTF_8);

  String TEXT_PLAIN_VALUE_UTF_8 = TEXT_PLAIN_VALUE + CHARSET_PARAM + UTF_8;
  MediaType TEXT_PLAIN_UTF_8 = parseMediaType(TEXT_PLAIN_VALUE_UTF_8);

}