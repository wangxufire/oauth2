package com.hd123.oauth2.util;

import static com.google.common.base.CharMatcher.JAVA_LETTER_OR_DIGIT;
import static com.google.common.base.Objects.equal;
import static com.hd123.oauth2.common.Constants.COMMA;
import static java.util.UUID.randomUUID;

import java.util.function.Predicate;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * String工具类
 *
 * @author liyue
 */
public final class StringUtil {

  public static final Joiner COMMA_JOINER = Joiner.on(COMMA).skipNulls();
  public static final Splitter COMMA_SPLITTER = Splitter.on(COMMA).omitEmptyStrings().trimResults();

  private StringUtil() {
  }

  /**
   * 生成uuid
   *
   * @return uuid
   */
  public static String generateUuid() {
    final String uuid = randomUUID().toString();

    return JAVA_LETTER_OR_DIGIT.retainFrom(uuid);
  }

  /**
   * a,b相等条件
   * 
   * @param a
   *          a
   * @param b
   *          b
   * @return predicate
   */
  public static Predicate<String> predicateEqual(String a, String b) {
    return p -> equal(a, b);
  }

}