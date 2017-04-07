package top.letscode.tool.util;

import top.letscode.tool.log.LogHelper;

public class Utils {

  public static void sleepQuietly(long millis) {
    try {
      Thread.sleep(millis);
    } catch (Exception e) {
      LogHelper.error("thread sleep error: ", e);
    }
  }
}
