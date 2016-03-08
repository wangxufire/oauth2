package com.hd123.oauth2.support.exceptionevent;

import java.lang.reflect.Method;

import com.hd123.oauth2.support.ExceptionCode;
import com.hd123.oauth2.support.exceptionevent.ExceptionEventRef.ExceptionEvent;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslatorOneArg;

/**
 * 异常事件传输及监听
 *
 * @author liyue
 */
public class ExceptionEventRef implements EventTranslatorOneArg<ExceptionEvent, ExceptionCode>,
    EventHandler<ExceptionEvent> {

  @Override
  public void translateTo(ExceptionEvent event, long sequence, ExceptionCode args) {
    event.setExc(args);
  }

  @Override
  public void onEvent(ExceptionEvent event, long sequence, boolean endOfBatch) throws Exception {
    final ExceptionCode exc = event.getExc();
    final Method rebuidMessage = exc.getClass().getDeclaredMethod("rebuidMessage",
        ExceptionCode.class);
    rebuidMessage.setAccessible(true);
    rebuidMessage.invoke(exc, exc);
  }

  /**
   * 异常事件
   *
   * @author liyue
   */
  static final class ExceptionEvent {

    private ExceptionCode exc;

    public ExceptionEvent() {
    }

    public ExceptionCode getExc() {
      return exc;
    }

    public void setExc(ExceptionCode exc) {
      this.exc = exc;
    }

  }

}