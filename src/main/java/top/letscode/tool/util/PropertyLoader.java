package top.letscode.tool.util;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import top.letscode.tool.exception.PropertyNotFoundException;
import top.letscode.tool.log.LogHelper;

public class PropertyLoader {

  // properties file suffix
  private static final String FILE_SUFFIX = ".properties";
  // config map: key is config file name(or call it namespace),value is it's config info
  private static Map<String, Properties> CONFIG_MAP = new HashMap<String, Properties>();

  /**
   * every config file must in classpath,such as classpath:common.properties
   * 
   * @param configFileName
   * @param key
   * @return
   */
  public static String getConfig(String configFileName, String key) {
    Properties prop = CONFIG_MAP.get(configFileName);
    if (prop == null) {
      prop = loadConfig(configFileName);
    }
    if (prop == null) {
      throw new PropertyNotFoundException();
    }

    return prop.getProperty(key);
  }

  @SuppressWarnings("unchecked")
  public static <T> T getConfig(String configFileName, String key, Class<T> classz) {
    String value = getConfig(configFileName, key);
    if (classz == Integer.class) {
      return (T) new Integer(value);
    }
    if (classz == Float.class) {
      return (T) new Float(value);
    }
    if (classz == Double.class) {
      return (T) new Double(value);
    }
    if (classz == Long.class) {
      return (T) new Long(value);
    }
    if (classz == Boolean.class) {
      return (T) new Boolean(value);
    }

    return (T) value;
  }

  private synchronized static Properties loadConfig(String configFileName) {
    // check again to avoid load multi times
    if (CONFIG_MAP.get(configFileName) != null) {
      return CONFIG_MAP.get(configFileName);
    }

    URL url = PropertyLoader.class.getClassLoader().getResource(configFileName + FILE_SUFFIX);
    if (url == null) {
      return null;
    }

    InputStream is = null;
    try {
      is = url.openStream();
      Properties properties = new Properties();
      properties.load(url.openStream());

      return properties;
    } catch (Exception e) {
      LogHelper.error("load config " + configFileName + " error", e);
    } finally {
      IOUtils.closeQuietly(is);
    }

    return null;
  }
}
