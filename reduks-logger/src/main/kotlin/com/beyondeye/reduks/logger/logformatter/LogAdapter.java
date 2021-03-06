package com.beyondeye.reduks.logger.logformatter;

public interface LogAdapter {
  void d(String tag, String message);

  void e(String tag, String message);

  void w(String tag, String message);

  void i(String tag, String message);

  void v(String tag, String message);

  void wtf(String tag, String message);

  int max_message_size(); //cannot log messages bigger than this
}