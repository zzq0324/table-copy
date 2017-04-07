package top.letscode.tool.log;

import org.apache.log4j.Logger;

public class LogHelper {

  private static Logger logger = Logger.getLogger("executeLog");

  public static void error(Object message, Throwable t) {
    logger.error(message, t);
  }

  public static void info(Object message) {
    logger.info(message);
  }
}
