package com.hd123.oauth2.config;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.core.JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS;
import static com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS;
import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER;
import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES;
import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES;
import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.DeserializationFeature.READ_ENUMS_USING_TO_STRING;
import static com.fasterxml.jackson.databind.DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY;
import static com.fasterxml.jackson.databind.MapperFeature.AUTO_DETECT_FIELDS;
import static com.fasterxml.jackson.databind.MapperFeature.AUTO_DETECT_GETTERS;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_EMPTY_JSON_ARRAYS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_ENUMS_USING_TO_STRING;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_NULL_MAP_VALUES;
import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newArrayList;
import static com.hd123.oauth2.common.HttpMediaType.APPLICATION_JSON_UTF_8;
import static com.hd123.oauth2.common.HttpMediaType.TEXT_XML_VALUE_UTF_8;
import static com.hd123.oauth2.common.ProfileConstants.PRODUCTION;
import static com.hd123.oauth2.util.DateUtil.I18N_DATE_FORMAT;
import static java.util.EnumSet.of;
import static java.util.TimeZone.getDefault;
import static javax.servlet.DispatcherType.ASYNC;
import static javax.servlet.DispatcherType.FORWARD;
import static javax.servlet.DispatcherType.REQUEST;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_SUPPORT;
import static org.springframework.boot.context.embedded.MimeMappings.DEFAULT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.MimeMappings;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Role;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import com.hd123.oauth2.util.ProfileUtil;

/**
 * 自定义配置
 *
 * @author liyue
 * @since 0.0.1
 */
@Configuration
public class WebConfigurerAdapter {

  @Autowired
  private ProfileUtil profileUtil;

  @Bean
  @Role(ROLE_INFRASTRUCTURE)
  public WebMvcConfigurerAdapter webMvcConfigurerAdapter() {
    return new WebMvcConfigurerAdapter() {

      @Override
      public void addInterceptors(InterceptorRegistry registry) {
        // 可以通过此方法添加拦截器, 可以是spring提供的或者自己添加的
        registry.addInterceptor(new AccessKeyInterceptor()).addPathPatterns("/api/**");
      }

      @Override
      public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 在此处定制全局JsonMapper,有需要也可定制XmlMapper(MappingJackson2XmlHttpMessageConverter)
        converters
            .parallelStream()
            .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
            .forEach(
                converter -> {
                  final MappingJackson2HttpMessageConverter cvt = (MappingJackson2HttpMessageConverter) converter;
                  final ObjectMapper objectMapper = cvt.getObjectMapper();
                  // objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL,
                  // JsonTypeInfo.As.PROPERTY);
                  final SimpleDateFormat dateTimeFormat = new SimpleDateFormat(I18N_DATE_FORMAT);
                  objectMapper.setDateFormat(dateTimeFormat);
                  objectMapper.setTimeZone(getDefault());
                  // objectMapper
                  // .setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
                  objectMapper.registerModule(new SimpleModule("ObjectIdConverter").addSerializer(
                      ObjectId.class, objectIdSerializer()).addDeserializer(ObjectId.class,
                      objectIdDeserializer()));
                  objectMapper.setSerializationInclusion(NON_NULL);
                  objectMapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);
                  objectMapper.configure(WRITE_ENUMS_USING_TO_STRING, true);
                  objectMapper.configure(WRITE_NULL_MAP_VALUES, false);
                  objectMapper.configure(WRITE_EMPTY_JSON_ARRAYS, true);
                  objectMapper.configure(FAIL_ON_EMPTY_BEANS, false);
                  objectMapper.configure(INDENT_OUTPUT, true);
                  objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
                  objectMapper.configure(USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
                  objectMapper.configure(FAIL_ON_READING_DUP_TREE_KEY, true);
                  objectMapper.configure(FAIL_ON_NUMBERS_FOR_ENUMS, true);
                  objectMapper.configure(READ_ENUMS_USING_TO_STRING, true);
                  objectMapper.configure(ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

                  objectMapper.configure(AUTO_DETECT_GETTERS, true);
                  objectMapper.configure(AUTO_DETECT_FIELDS, true);
                  objectMapper.configure(ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
                  // 引号
                  objectMapper.configure(ALLOW_SINGLE_QUOTES, true);
                  objectMapper.configure(ALLOW_UNQUOTED_FIELD_NAMES, true);
                  objectMapper.configure(WRITE_NUMBERS_AS_STRINGS, true);
                  objectMapper.configure(QUOTE_NON_NUMERIC_NUMBERS, true);
                });

        super.extendMessageConverters(converters);
      }

      @Role(ROLE_SUPPORT)
      @Description("jackson转换器")
      @Bean(name = "mappingJackson2HttpMessageConverter")
      public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        final MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        final List<MediaType> supportedMediaTypes = newArrayList(APPLICATION_JSON,
            APPLICATION_JSON_UTF_8);
        jsonConverter.setSupportedMediaTypes(supportedMediaTypes);

        return jsonConverter;
      }

      // ObjectId序列化
      private JsonSerializer<Object> objectIdSerializer() {
        return new JsonSerializer<Object>() {
          @Override
          public void serialize(Object obj, JsonGenerator jsonGenerator, SerializerProvider provider)
              throws IOException {
            jsonGenerator.writeString(obj == null ? null : obj.toString());
          }
        };
      }

      // ObjectId反序列化
      private JsonDeserializer<ObjectId> objectIdDeserializer() {
        return new JsonDeserializer<ObjectId>() {
          @Override
          public ObjectId deserialize(JsonParser jp, DeserializationContext ctxt)
              throws IOException {
            return new ObjectId(jp.readValueAs(String.class));
          }
        };
      }

      @Override
      public void configureViewResolvers(ViewResolverRegistry registry) {
      }

      @Override
      public void addResourceHandlers(ResourceHandlerRegistry registry) {
      }

      @Override
      public void addViewControllers(ViewControllerRegistry registry) {
      }

      @Override
      public void configurePathMatch(PathMatchConfigurer configurer) {
        // 定制uri
        // configurer.setUseSuffixPatternMatch(false).setUseTrailingSlashMatch(true);
      }

      /**
       * 访问拦截器
       *
       * @author liyue
       */
      final class AccessKeyInterceptor extends HandlerInterceptorAdapter {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
          // ~ 允许跨域请求, 部分IE无效

          // 表明它允许所有("http://foo.org")发起跨域请求
          response.addHeader("Access-Control-Allow-Origin", "*");
          // 表明它允许跨域请求包含content-type头
          response.addHeader("Access-Control-Allow-Headers", "Content-Type");
          // 表明它允许GET、PUT、DELETE的外域请求
          response.addHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
          // 表明在30秒内，不需要再发送预检验请求，可以缓存该结果
          response.addHeader("Access-Control-Max-Age", "3600");

          return super.preHandle(request, response, handler);
        }

        /**
         * This implementation is empty.
         */
        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler, ModelAndView modelAndView) throws Exception {

          super.postHandle(request, response, handler, modelAndView);
        }

        /**
         * This implementation is empty.
         */
        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) throws Exception {

          super.afterCompletion(request, response, handler, ex);
        }

        /**
         * This implementation is empty.
         */
        @Override
        public void afterConcurrentHandlingStarted(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {

          super.afterConcurrentHandlingStarted(request, response, handler);
        }

      }
    };

  }

  @Bean
  @Profile(PRODUCTION)
  @Role(ROLE_INFRASTRUCTURE)
  public ServletContextInitializer servlet() {
    return servletContext -> {
      final EnumSet<DispatcherType> disps = of(REQUEST, FORWARD, ASYNC);
      initStaticResourcesProductionFilter(servletContext, disps);
    };
  }

  /**
   * Initializes the static resources production Filter.
   */
  private void initStaticResourcesProductionFilter(ServletContext servletContext,
      EnumSet<DispatcherType> disps) {
    final Dynamic staticResourcesProductionFilter = servletContext.addFilter(
        "staticResourcesProductionFilter", new StaticResourcesProductionFilter());

    staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/");
    staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/index.html");
    staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/assets/*");
    staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/scripts/*");
    staticResourcesProductionFilter.setAsyncSupported(true);
  }

  @Bean
  @Role(ROLE_INFRASTRUCTURE)
  public EmbeddedServletContainerCustomizer dispatcherServletCustomizer() {
    return container -> {
      final MimeMappings mappings = new MimeMappings(DEFAULT);
      mappings.add("html", TEXT_XML_VALUE_UTF_8);
      container.setMimeMappings(mappings);
    };
  }

  /**
   * This filter is used in production, to serve static resources generated by
   * "grunt build".
   * <p/>
   * <p>
   * It is configured to serve resources from the "dist" directory, which is the
   * Grunt destination directory.
   * </p>
   */
  final class StaticResourcesProductionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
      // Nothing to initialize
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
      final HttpServletRequest httpRequest = (HttpServletRequest) request;
      final String contextPath = ((HttpServletRequest) request).getContextPath();
      String requestURI = httpRequest.getRequestURI();
      requestURI = substringAfter(requestURI, contextPath);
      if (equal("/", requestURI)) {
        requestURI = "/index.html";
      }
      request.getRequestDispatcher("/dist" + requestURI).forward(request, response);
    }

    @Override
    public void destroy() {
      // Nothing to destroy
    }

  }

}