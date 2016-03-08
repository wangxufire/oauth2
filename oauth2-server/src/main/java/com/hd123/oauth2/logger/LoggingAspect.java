package com.hd123.oauth2.logger;

import static com.alibaba.fastjson.JSON.parseObject;
import static com.alibaba.fastjson.JSON.toJSONString;
import static com.alibaba.fastjson.serializer.SerializerFeature.DisableCheckSpecialChar;
import static com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat;
import static com.google.common.base.Strings.repeat;
import static com.google.common.collect.Maps.newHashMap;
import static com.hd123.oauth2.common.Constants.COLON;
import static com.hd123.oauth2.common.Constants.COMMA;
import static com.hd123.oauth2.common.Constants.LINE;
import static com.hd123.oauth2.common.Constants.SPACE;
import static com.hd123.oauth2.common.HttpParams.ENCODING;
import static com.hd123.oauth2.common.HttpParams.HTTP_METHOD;
import static com.hd123.oauth2.common.HttpParams.METHOD_DESCRIPTION;
import static com.hd123.oauth2.common.HttpParams.REMOTE_IP;
import static com.hd123.oauth2.common.HttpParams.REQUEST_URI;
import static com.hd123.oauth2.common.HttpParams.SESSION_ID;
import static com.hd123.oauth2.common.HttpParams.TARGET_CONTROLLER;
import static com.hd123.oauth2.common.HttpParams.TARGET_METHOD;
import static com.hd123.oauth2.common.HttpParams.X_FORWARDED_FOR;
import static com.hd123.oauth2.common.HttpParams.X_REAL_IP;
import static com.hd123.oauth2.common.ProfileConstants.DEVELOPMENT;
import static com.hd123.oauth2.common.ProfileConstants.LOGGING;
import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static java.util.stream.Stream.of;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.logging.log4j.LogManager.getLogger;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.util.Assert.notNull;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONException;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * AOP记录请求日志
 *
 * @author liyue
 * @since 0.0.1
 */
@Aspect
@Component
@Profile({
    DEVELOPMENT, LOGGING })
@SuppressWarnings("unchecked")
public class LoggingAspect {

  private final Logger logger = getLogger(LoggingAspect.class);

  /**
   * 环绕通知 用于拦截Controller层记录用户的操作
   *
   * @param proJoinPoint
   *          切点
   * @return 方法执行结果
   */
  @Around(value = "@annotation(com.hd123.oauth2.logger.ControllerLogger)")
  public Object around(ProceedingJoinPoint proJoinPoint) throws Throwable {
    notNull(proJoinPoint, "切点不能为空");

    final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
        .getRequestAttributes()).getRequest();
    notNull(request, "http request不能为空");

    final boolean isGet = logBefore(proJoinPoint, request);
    logBegin(isGet, proJoinPoint, request);
    final Object result = logEnd(proJoinPoint);

    // 需要返回方法返回值，否则调用方拿不到返回值
    return result;
  }

  /**
   * 异常通知 用于拦截service层记录异常日志
   *
   * @param joinPoint
   *          切点
   * @param e
   *          异常
   */
  @AfterThrowing(pointcut = "@annotation(com.hd123.oauth2.logger.ServiceLogger)", throwing = "e")
  public void afterThrowing(JoinPoint joinPoint, Throwable e) {
    notNull(joinPoint, "切点不能为空");

    try {
      // 获取用户请求方法的参数并序列化为JSON格式字符串
      StringBuilder params = new StringBuilder();
      if (joinPoint.getArgs() != null && joinPoint.getArgs().length > 0) {
        of(joinPoint.getArgs()).parallel().forEach(arg -> {
          try {
            params.append(toJSONString(arg, DisableCheckSpecialChar)).append(";");
          } catch (JSONException ignored) {
          }
        });
      }

      final String description = getServiceMthodDescription(joinPoint);
      final String method = joinPoint.getSignature().getName();
      final String service = joinPoint.getTarget().getClass().getName();

      if (logger.isErrorEnabled()) {
        logger.error(format("\n异常方法:{0},\n方法描述:{1},\n参数:{2},\n异常类:{3},\n异常信息:{4}", method,
            description, params.toString(), service, e));
      }
    } catch (Exception ex) {
      if (logger.isErrorEnabled()) {
        logger.error("异常通知失败:", ex);
      }
    }
  }

  /**
   * 前置记录
   *
   * @param joinPoint
   *          切点
   * @param request
   *          请求
   * @return 是否为get请求
   */
  private boolean logBefore(final JoinPoint joinPoint, final HttpServletRequest request) {
    boolean isGet = false;
    try {
      final HttpSession session = request.getSession(true);
      final String sessionId = session.getId();
      String ip = request.getRemoteAddr();
      String rip = request.getHeader(X_REAL_IP);
      if (isBlank(rip)) {
        rip = request.getHeader(X_FORWARDED_FOR);
      }
      ip = isNotBlank(rip) ? rip.contains(COMMA) ? rip.split(COMMA)[0] : rip : ip;
      final String uri = request.getRequestURI();
      final String method = request.getMethod();
      final String encoding = request.getCharacterEncoding();
      final Enumeration enums = request.getHeaderNames();

      isGet = GET.toString().equalsIgnoreCase(method);

      final MultiValueMap<String, String> httpinfos = new LinkedMultiValueMap();
      while (enums.hasMoreElements()) {
        final String key = enums.nextElement().toString();
        final List<String> value = Splitter.on(COMMA).trimResults().omitEmptyStrings()
            .splitToList(request.getHeader(key));

        httpinfos.put(key, value);
      }

      httpinfos.put(ENCODING, asList(encoding));
      httpinfos.put(HTTP_METHOD, asList(method));
      httpinfos.put(REMOTE_IP, asList(ip));
      httpinfos.put(SESSION_ID, asList(sessionId));
      httpinfos.put(TARGET_CONTROLLER, asList(joinPoint.getTarget().getClass().getName()));
      httpinfos.put(TARGET_METHOD, asList(joinPoint.getSignature().getName()));
      final String description = getControllerMethodDescription(joinPoint);
      if (isNotBlank(description)) {
        httpinfos.put(METHOD_DESCRIPTION, asList(description));
      }

      logHttpInfos(uri, httpinfos);
    } catch (Throwable e) {
      if (logger.isErrorEnabled()) {
        logger.error("环绕通知(logBefore)失败:", e);
      }
    }

    return isGet;
  }

  /**
   * 参数记录
   *
   * @param isGet
   *          是为否get方法
   * @param joinPoint
   *          切点
   * @param request
   *          请求
   */
  private void logBegin(final boolean isGet, final JoinPoint joinPoint,
      final HttpServletRequest request) {
    try {
      if (isGet) {
        final Map<String, String> httpparams = newHashMap();
        final Map<String, String[]> params = request.getParameterMap();
        params.entrySet().parallelStream().forEach(entry -> {
          final String paramName = entry.getKey();
          final String[] paramValues = entry.getValue();

          httpparams.put(paramName, Joiner.on(",").skipNulls().join(paramValues));
        });

        if (!httpparams.isEmpty()) {
          logHttpParams(httpparams);
        }
      } else {
        if (joinPoint.getArgs() != null && joinPoint.getArgs().length > 0) {
          final Object params = joinPoint.getArgs()[0];

          if (!(params instanceof HttpServletRequest)) {
            Map<String, String> httpparams = newHashMap();
            try {
              httpparams = parseObject(toJSONString(params), HashMap.class);
            } catch (JSONException e) {
              httpparams = parseObject(params.toString(), HashMap.class);
            }

            logHttpParams(httpparams);
          } else {
            final HttpServletRequest req = (HttpServletRequest) params;
            final Map<String, String> httpparams2 = newHashMap();
            req.getParameterMap().entrySet().parallelStream().forEach(entry -> {
              final String paramName = entry.getKey();
              final String[] paramValues = entry.getValue();

              httpparams2.put(paramName, Joiner.on(",").skipNulls().join(paramValues));
            });
            if (!httpparams2.isEmpty()) {
              logHttpParams(httpparams2);
            }
          }
        }
      }
    } catch (Throwable e) {
      if (logger.isErrorEnabled()) {
        logger.error("环绕通知(logBegin)失败:", e);
      }
    }
  }

  /**
   * 后置记录
   *
   * @param proJoinPoint
   *          切点
   * @return 方法执行结果
   */
  private Object logEnd(final ProceedingJoinPoint proJoinPoint) throws Throwable {
    final Object result;
    result = proJoinPoint.proceed();
    if (result != null) {
      logHttpReturn(result);
    }

    return result;
  }

  /**
   * 记录http请求信息
   *
   * @param uri
   *          请求url
   * @param httpinfos
   *          请求信息
   */
  private void logHttpInfos(String uri, MultiValueMap<String, String> httpinfos) {
    if (logger.isInfoEnabled()) {
      final StringBuilder msg = new StringBuilder("\n").append(repeat(LINE, 100)).append(
          "\nThe HTTP Request is: \n");

      httpinfos.entrySet().forEach(entry -> {
        msg.append(entry.getKey()).append(COLON).append(SPACE);
        entry.getValue().forEach(value -> {
          msg.append(value).append(COMMA).append(SPACE);
        });

        final int length = msg.length();
        msg.replace(length - 2, length, "\n");
      });

      msg.append(REQUEST_URI).append(COLON).append(SPACE).append(uri).append("\n")
          .append(repeat(LINE, 100));
      logger.info(msg.toString());
    }
  }

  /**
   * 记录http请求参数
   *
   * @param httpparams
   *          请求参数
   */
  private void logHttpParams(Map<String, String> httpparams) {
    if (logger.isInfoEnabled()) {
      final StringBuilder msg = new StringBuilder("\n").append(repeat(LINE, 100))
          .append("\nThe contents of request body is: \n")
          .append(toJSONString(httpparams, DisableCheckSpecialChar, PrettyFormat)).append("\n")
          .append(repeat(LINE, 100));
      logger.info(msg.toString());
    }
  }

  /**
   * 记录http请求返回
   *
   * @param result
   *          响应结果
   */
  private void logHttpReturn(Object result) {
    if (logger.isInfoEnabled()) {
      final StringBuilder msg = new StringBuilder("\n").append(repeat(LINE, 100))
          .append("\nThe contents of response body is: \n")
          .append(toJSONString(result, DisableCheckSpecialChar, PrettyFormat)).append("\n")
          .append(repeat(LINE, 100));
      logger.info(msg.toString());
    }
  }

  /**
   * 获取切点方法
   *
   * @param joinPoint
   *          切点
   * @return 切点方法
   */
  private static Method getJoinPointMethod(final JoinPoint joinPoint) {
    final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    return signature.getMethod();
  }

  /**
   * 获取注解中对方法的描述信息 用于service层注解
   *
   * @param joinPoint
   *          切点
   * @return 方法描述
   */
  private static String getServiceMthodDescription(final JoinPoint joinPoint) {
    String description = EMPTY;

    final Method method = getJoinPointMethod(joinPoint);
    final ServiceLogger serviceLogger = method.getAnnotation(ServiceLogger.class);
    if (serviceLogger != null) {
      description = serviceLogger.value();
    }

    return description;
  }

  /**
   * 获取注解中对方法的描述信息 用于Controller层注解
   *
   * @param joinPoint
   *          切点
   * @return 方法描述
   */
  private static String getControllerMethodDescription(final JoinPoint joinPoint) {
    String description = EMPTY;

    final Method method = getJoinPointMethod(joinPoint);
    final ControllerLogger controllerLogger = method.getAnnotation(ControllerLogger.class);
    if (controllerLogger != null) {
      description = controllerLogger.value();
    }

    return description;
  }

}