package com.hd123.oauth2.disruptor;

import static com.lmax.disruptor.dsl.ProducerType.SINGLE;
import static java.util.concurrent.Executors.newCachedThreadPool;

import java.util.concurrent.ExecutorService;

import com.hd123.oauth2.disruptor.TestEventRef.TestEvent;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;

/**
 * @author liyue
 */
@SuppressWarnings("unchecked")
public class DisruptorTest {

  private static volatile Disruptor<TestEvent> disruptor;
  private static final ExecutorService EXECUTOR = newCachedThreadPool();

  static {
    disruptor = new Disruptor<>(TestEvent::new, 1024, EXECUTOR, SINGLE, new YieldingWaitStrategy());
    // disruptor.handleExceptionsWith(getExceptionHandler());
    disruptor.handleEventsWith(new TestEventRef());
    disruptor.start();
  }

  public static void main(String[] args) throws Exception {
    // Get the ring buffer from the Disruptor to be used for publishing.
    final RingBuffer<TestEvent> ringBuffer = disruptor.getRingBuffer();

    for (long l = 0; l < 200; l++) {
      ringBuffer.publishEvent(new TestEventRef(), l);
    }
  }

}