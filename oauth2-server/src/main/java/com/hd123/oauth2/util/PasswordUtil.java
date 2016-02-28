package com.hd123.oauth2.util;

import static com.hd123.oauth2.common.Constants.ALGORITHM;
import static com.hd123.oauth2.common.Constants.ALGORITHM_PROVIDER;
import static java.security.SecureRandom.getInstance;
import static org.springframework.util.Assert.hasLength;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 加密工具
 *
 * @author liyue
 */
public final class PasswordUtil {

  private PasswordUtil() {
  }

  /**
   * 获取加密器
   *
   * @return PasswordEncoder
   */
  public static PasswordEncoder getPasswordEncoder() {
    SecureRandom random = null;
    try {
      random = getInstance(ALGORITHM, ALGORITHM_PROVIDER);
      random.setSeed(random.generateSeed(44));
    } catch (NoSuchAlgorithmException | NoSuchProviderException ignored) {
    }
    return new BCryptPasswordEncoder(13, random);
  }

  /**
   * 加密密码
   *
   * @param rawPwd
   *          rawPwd
   * @return encryptPwd
   */
  public static String encryptPassword(String rawPwd) {
    hasLength(rawPwd, "rawPwd");

    return getPasswordEncoder().encode(rawPwd);
  }

  /**
   * 校验密码
   *
   * @param rawPwd
   *          rawPwd
   * @param encryptPwd
   *          encryptPwd
   * @return isValid
   */
  public static boolean checkPassword(String rawPwd, String encryptPwd) {
    hasLength(rawPwd, "rawPwd");
    hasLength(encryptPwd, "encryptPwd");
    return getPasswordEncoder().matches(rawPwd, encryptPwd);
  }

}