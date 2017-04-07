package top.letscode.tool.util;

import java.io.InputStream;

import top.letscode.tool.log.LogHelper;

public class IOUtils {

  public static void closeQuietly(InputStream stream) {
    if (stream != null) {
      try {
        stream.close();
      } catch (Exception e) {
        LogHelper.error("close stream error: ", e);
      }
    }
  }
}
