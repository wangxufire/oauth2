package com.hd123.oauth2.config;

import static com.alibaba.fastjson.JSON.toJSONString;
import static com.alibaba.fastjson.serializer.SerializerFeature.DisableCheckSpecialChar;
import static java.text.MessageFormat.format;
import static org.apache.logging.log4j.LogManager.getLogger;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.apache.logging.log4j.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 异步处理配置
 *
 * <pre>
 * spring 默认使用
 * {@link org.springframework.core.task.SimpleAsyncTaskExecutor}
 * 可通过配置文件做一些配置，此处只是自定义，该类非必须
 * </pre>
 *
 * @author liyue
 * @since 0.0.1
 */
@Configuration
public class AsyncTaskConfig implements AsyncConfigurer {

  private static final Logger logger = getLogger(WrapperAsyncExecutor.class);

  @Autowired
  private AppProperties appProperties;

  // ~ Common Settings
  // ========================================================================================================

  @Override
  @Role(ROLE_INFRASTRUCTURE)
  @Bean(name = "taskExecutor")
  public Executor getAsyncExecutor() {
    if (logger.isDebugEnabled()) {
      logger.debug("Creating Async Task Executor");
    }

    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(appProperties.getAsync().getCorePoolSize());
    executor.setMaxPoolSize(appProperties.getAsync().getMaxPoolSize());
    executor.setQueueCapacity(appProperties.getAsync().getQueueCapacity());
    executor.setThreadNamePrefix(appProperties.getAsync().getNamePrefix());
    executor.initialize();

    return new WrapperAsyncExecutor(executor);
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return new AsyncExceptionHandler();
  }

  /**
   * 异步任务异常捕获
   *
   * @author liyue
   */
  public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... args) {
      if (logger.isErrorEnabled()) {
        logger.error(
            format("\n调用异步任务{0}出错,\nargs : {1},\nmessage : ", method.getName(),
                toJSONString(args, DisableCheckSpecialChar)), throwable);
      }
    }

  }

  /**
   * 异步任务处理器
   *
   * @author liyue
   */
  public class WrapperAsyncExecutor implements AsyncTaskExecutor, InitializingBean, DisposableBean {
    private final AsyncTaskExecutor executor;

    public WrapperAsyncExecutor(AsyncTaskExecutor executor) {
      this.executor = executor;
    }

    @Override
    public void execute(Runnable task) {
      executor.execute(task);
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
      executor.execute(createWrappedRunnable(task), startTimeout);
    }

    @Override
    public Future<?> submit(Runnable task) {
      return executor.submit(createWrappedRunnable(task));
    }

    @Override
    public <T> Future<T> submit(final Callable<T> task) {
      return executor.submit(createCallable(task));
    }

    private <T> Callable<T> createCallable(final Callable<T> task) {
      return () -> {
        try {
          return task.call();
        } catch (Exception e) {
          handle(e);
          throw e;
        }
      };
    }

    private Runnable createWrappedRunnable(final Runnable task) {
      return () -> {
        try {
          task.run();
        } catch (Exception e) {
          handle(e);
        }
      };
    }

    /**
     * 捕获异常
     *
     * @param e
     */
    private void handle(Exception e) {
      if (logger.isErrorEnabled()) {
        logger.error("CAUGHT!", e);
      }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
      if (executor instanceof InitializingBean) {
        final InitializingBean bean = (InitializingBean) executor;
        bean.afterPropertiesSet();
      }
    }

    @Override
    public void destroy() throws Exception {
      if (executor instanceof DisposableBean) {
        final DisposableBean bean = (DisposableBean) executor;
        bean.destroy();
      }
    }

  }

}