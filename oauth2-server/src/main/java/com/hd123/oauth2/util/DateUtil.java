package com.hd123.oauth2.util;

import static java.time.Instant.ofEpochSecond;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.util.Date.from;
import static org.springframework.util.Assert.notNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 通用日期工具类
 * 
 * @author liyue
 * @since 0.0.1
 */
public final class DateUtil {

  public static final String MONTH_DAY_FORMAT = "MM-dd";
  public static final String DATE_FORMAT = "yyyy-MM-dd";
  public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static final String I18N_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

  private DateUtil() {
  }

  /**
   * 将日期类型转为时间戳
   * 
   * @param date
   *          date
   * @return 秒级时间戳
   */
  public static long date2Timestamp(Date date) {
    notNull(date, "date");

    return date.toInstant().getEpochSecond();
  }

  /**
   * 将日期字符串转为时间戳
   *
   * @param time
   *          time
   * @param format
   *          format
   * @return 秒级时间戳
   * @throws Exception
   *           Exception
   */
  public static long date2Timestamp(String time, String format) throws Exception {
    notNull(time, "time");
    notNull(format, "format");

    long reTime;
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    try {
      reTime = sdf.parse(time).toInstant().getEpochSecond();
    } catch (ParseException e) {
      throw new Exception("转换unix时间戳失败");
    }

    return reTime;
  }

  /**
   * unix时间戳转换Date
   *
   * @param timeString
   *          秒级
   * @param format
   *          format
   * @return 日期字符串
   */
  public static String timeStamp2Date(long timeString, String format) {
    notNull(format, "format");

    final Date date = from(ofEpochSecond(timeString));

    return new SimpleDateFormat(format).format(date);
  }

  /**
   * 获取当前日期字符串
   *
   * @return now String
   */
  public static String now() {
    return ISO_DATE_TIME.format(LocalDateTime.now());
  }

  /**
   * 获取当前日期时间戳
   *
   * @return nowTimestamp
   */
  public static long nowTimestamp() {
    return date2Timestamp(new Date());
  }

}