package com.hd123.oauth2.support.exceptionevent;

import static com.lmax.disruptor.dsl.ProducerType.SINGLE;
import static java.util.concurrent.Executors.newCachedThreadPool;

import java.util.concurrent.ExecutorService;

import com.hd123.oauth2.support.ExceptionCode;
import com.hd123.oauth2.support.exceptionevent.ExceptionEventRef.ExceptionEvent;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;

/**
 * 异常事件分发器
 *
 * @author liyue
 */
public class ExceptionEventDisruptor {

  private static volatile Disruptor<ExceptionEvent> disruptor;
  private static final ExecutorService EXECUTOR = newCachedThreadPool();

  static {
    disruptor = new Disruptor<>(ExceptionEvent::new, 1024, EXECUTOR, SINGLE,
        new YieldingWaitStrategy());
    disruptor.handleEventsWith(new ExceptionEventRef());
    disruptor.start();
  }

  /**
   * 推送异常事件
   *
   * @param exc
   *          exc
   */
  public static void pushExceptionEvent(ExceptionCode exc) {
    final RingBuffer<ExceptionEvent> ringBuffer = disruptor.getRingBuffer();
    ringBuffer.publishEvent(new ExceptionEventRef(), exc);
  }

}