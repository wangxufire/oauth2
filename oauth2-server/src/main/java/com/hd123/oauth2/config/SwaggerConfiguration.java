package com.hd123.oauth2.config;

import static com.google.common.collect.Lists.newArrayList;
import static com.hd123.oauth2.common.ProfileConstants.UN_PRODUCTION;
import static org.apache.logging.log4j.LogManager.getLogger;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_SUPPORT;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static springfox.documentation.builders.PathSelectors.regex;
import static springfox.documentation.builders.RequestHandlerSelectors.any;
import static springfox.documentation.schema.AlternateTypeRules.newRule;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

import java.util.Date;

import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.async.DeferredResult;

import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.fasterxml.classmate.TypeResolver;

import com.hd123.oauth2.config.AppProperties.Swagger;
import com.hd123.oauth2.util.ProfileUtil;

/**
 * Springfox Swagger configuration.
 *
 * Warning! When having a lot of REST endpoints, Springfox can become a
 * performance issue. In that case, you can use a specific Spring profile for
 * this class, so that only front-end developers have access to the Swagger
 * view.
 * 
 * @author liyue
 */
@Configuration
@EnableSwagger2
@Profile(UN_PRODUCTION)
public class SwaggerConfiguration {

  private final Logger logger = getLogger(SwaggerConfiguration.class);

  @Inject
  private TypeResolver typeResolver;
  @Autowired
  private ProfileUtil profileUtil;
  @Autowired
  private AppProperties appProperties;

  /**
   * Swagger Springfox configuration.
   */
  @Bean
  @Role(ROLE_SUPPORT)
  @Profile(UN_PRODUCTION)
  @Description("Heading OAuth2 API Documentation")
  public Docket swaggerSpringfoxDocket() {
    final boolean debugAble = logger.isDebugEnabled();
    if (debugAble) {
      logger.debug("Starting Swagger");
    }
    final StopWatch watch = new StopWatch();
    watch.start();
    final ApiInfo apiInfo = apiInfo();
    final Docket docket = new Docket(SWAGGER_2)
        .apiInfo(apiInfo)
        .enable(!profileUtil.isProd())
        .enableUrlTemplating(false)
        .forCodeGeneration(true)
        .genericModelSubstitutes(ResponseEntity.class)
        .ignoredParameterTypes(Pageable.class)
        .directModelSubstitute(java.time.LocalDate.class, String.class)
        .directModelSubstitute(java.time.ZonedDateTime.class, Date.class)
        .directModelSubstitute(java.time.LocalDateTime.class, Date.class)
        .useDefaultResponseMessages(false)
        .alternateTypeRules(
            newRule(
                typeResolver.resolve(DeferredResult.class,
                    typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                typeResolver.resolve(WildcardType.class)))
        .globalResponseMessage(
            GET,
            newArrayList(new ResponseMessageBuilder().code(500).message("Internal Server Error")
                .responseModel(new ModelRef("Error")).build())).select().apis(any())
        .paths(regex(appProperties.getSwagger().getApiPattern())).build();
    watch.stop();

    if (debugAble) {
      logger.debug("Started Swagger in {} ms", watch.getTotalTimeMillis());
    }

    return docket;
  }

  /**
   * 构造应用信息
   *
   * @return 应用信息
   */
  private ApiInfo apiInfo() {
    final Swagger swagger = appProperties.getSwagger();
    return new ApiInfo(swagger.getTitle(), swagger.getDescription(), swagger.getVersion(),
        swagger.getTermsOfServiceUrl(), swagger.getContact(), swagger.getLicense(),
        swagger.getLicenseUrl());
  }

}