package com.hd123.oauth2.disruptor;

import com.hd123.oauth2.disruptor.TestEventRef.TestEvent;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslatorOneArg;

/**
 * @author liyue
 */
public class TestEventRef implements EventTranslatorOneArg<TestEvent, Long>,
    EventHandler<TestEvent> {

  @Override
  public void translateTo(TestEvent event, long sequence, Long args) {
    event.setValue(args);
  }

  @Override
  public void onEvent(TestEvent event, long sequence, boolean endOfBatch) throws Exception {
    System.out.println(event.getValue());
  }

  public static class TestEvent {

    private long value;

    public TestEvent() {
    }

    public long getValue() {
      return value;
    }

    public void setValue(long value) {
      this.value = value;
    }

  }

}