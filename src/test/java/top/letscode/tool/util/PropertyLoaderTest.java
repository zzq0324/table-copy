package top.letscode.tool.util;

import org.junit.Assert;
import org.junit.Test;

import top.letscode.tool.exception.PropertyNotFoundException;
import top.letscode.tool.util.PropertyLoader;

public class PropertyLoaderTest {

  @Test(expected = PropertyNotFoundException.class)
  public void test_config_file_not_exist() {
    PropertyLoader.getConfig("notexist", "test");
  }

  @Test
  public void test_config_file_exist() {
    String timeout = PropertyLoader.getConfig("test", "timeout");
    Assert.assertNotNull(timeout);
    Assert.assertEquals("3", timeout);
  }

  @Test
  public void test_key_not_exist() {
    String value = PropertyLoader.getConfig("test", "abc");
    Assert.assertNull(value);
  }

  @Test
  public void test_getconfig_with_type() {
    int intValue = PropertyLoader.getConfig("test", "timeout", Integer.class);
    Assert.assertTrue(3 == intValue);
    String strValue = PropertyLoader.getConfig("test", "timeout", String.class);
    Assert.assertEquals("3", strValue);
  }
}
