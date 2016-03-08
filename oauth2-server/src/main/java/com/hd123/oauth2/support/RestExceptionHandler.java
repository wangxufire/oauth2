package com.hd123.oauth2.support;

import static com.alibaba.fastjson.JSON.toJSONString;
import static com.alibaba.fastjson.serializer.SerializerFeature.DisableCheckSpecialChar;
import static com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat;
import static com.google.common.base.Strings.repeat;
import static com.hd123.oauth2.support.ExceptionCode.failed;
import static com.hd123.oauth2.support.ExceptionCode.illegalArgument;
import static com.hd123.oauth2.support.ExceptionCode.unauthorizedCurrentUser;
import static com.hd123.oauth2.support.ExceptionCode.unauthorizedCurrentUserToResource;
import static java.lang.String.format;
import static org.apache.logging.log4j.LogManager.getLogger;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hd123.oauth2.exception.AuthServiceException;
import com.hd123.oauth2.exception.OptionalException;
import com.hd123.oauth2.exception.SpecAuthServiceException;
import com.hd123.oauth2.rest.RsResponse;
import com.hd123.oauth2.util.ProfileUtil;

/**
 * RestController全局异常处理
 *
 * @author liyue
 * @since 0.0.1
 */
@ControllerAdvice(annotations = {
    Controller.class, RestController.class })
public class RestExceptionHandler {

  private final Logger logger = getLogger(RestExceptionHandler.class);

  @Autowired
  private ProfileUtil profileUtil;

  @ResponseBody
  @ResponseStatus(INTERNAL_SERVER_ERROR)
  @ExceptionHandler(value = {
      DuplicateKeyException.class, AuthServiceException.class, OptionalException.class,
      IllegalArgumentException.class, AccessDeniedException.class, AuthenticationException.class,
      MethodArgumentNotValidException.class, MissingServletRequestParameterException.class,
      ConstraintViolationException.class, NoSuchElementException.class, RuntimeException.class,
      Exception.class })
  public RsResponse restExceptionHandler(HttpServletRequest req, HttpServletResponse res,
      Exception ex) throws Exception {
    // If the exception is annotated with @ResponseStatus rethrow it and let
    // the framework handle it
    if (findAnnotation(ex.getClass(), ResponseStatus.class) != null) {
      throw ex;
    }

    final RsResponse response = new RsResponse();
    response.setMessage(ex.getMessage());

    if (ex instanceof AuthServiceException) {
      response.setErrorCode(((AuthServiceException) ex).getErrorCode());
    } else if (ex instanceof SpecAuthServiceException) {
      response.setErrorCode(((SpecAuthServiceException) ex).getErrorCode());
    } else if (ex instanceof IllegalArgumentException || ex instanceof OptionalException
        || ex instanceof MethodArgumentNotValidException
        || ex instanceof ConstraintViolationException
        || ex instanceof MissingServletRequestParameterException) {
      response.setErrorCode(illegalArgument.getCode());
      response.setMessage(illegalArgument.getMessage());
    } else if (ex instanceof AccessDeniedException) {
      response.setErrorCode(unauthorizedCurrentUserToResource.getCode());
      response.setMessage(unauthorizedCurrentUserToResource.getMessage());
    } else if (ex instanceof AuthenticationException) {
      response.setErrorCode(unauthorizedCurrentUser.getCode());
      response.setMessage(unauthorizedCurrentUser.getMessage());
    } else {
      response.setErrorCode(failed.getCode());
      response.setMessage(failed.getMessage());
    }

    if (profileUtil.isDevOrLog()) {
      try (final StringWriter sw = new StringWriter(); final PrintWriter pw = new PrintWriter(sw)) {
        ex.printStackTrace(pw);
        response.setStackTrace(sw.toString());
      }

      logHttpReturn(response);
    }

    if (logger.isErrorEnabled()) {
      logger.error(format("请求%s失败", req.getRequestURI()), ex);
    }

    return response;
  }

  /**
   * 记录http请求返回
   *
   * @param result
   *          响应结果
   */
  private void logHttpReturn(Object result) {
    if (logger.isInfoEnabled()) {
      final StringBuilder msg = new StringBuilder("\n" + repeat("-", 100)
          + "\nThe contents of response body is: \n");

      msg.append(toJSONString(result, DisableCheckSpecialChar, PrettyFormat) + "\n");
      msg.append(repeat("-", 100));
      logger.info(msg.toString());
    }
  }

}