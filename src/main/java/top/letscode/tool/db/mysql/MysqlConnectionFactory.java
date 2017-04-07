package top.letscode.tool.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;

import top.letscode.tool.log.LogHelper;
import top.letscode.tool.util.ConstantValue;
import top.letscode.tool.util.PropertyLoader;

public class MysqlConnectionFactory {

  // static block to register jdbc driver
  static {
    try {
      String jdbcDriver = PropertyLoader.getConfig(ConstantValue.CONFIG_FILE_NAME, "jdbc.driver");
      Class.forName(jdbcDriver);
    } catch (ClassNotFoundException e) {
      LogHelper.error("can't find driver class, please check again!", e);
    }
  }

  /**
   * create new jdbc connection every time.
   * 
   * @return
   * @throws Exception
   */
  public static Connection openConnection() throws Exception {
    String jdbcUrl = PropertyLoader.getConfig(ConstantValue.CONFIG_FILE_NAME, "jdbc.url");
    String jdbcUserName = PropertyLoader.getConfig(ConstantValue.CONFIG_FILE_NAME, "jdbc.username");
    String jdbcPassword = PropertyLoader.getConfig(ConstantValue.CONFIG_FILE_NAME, "jdbc.password");

    Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUserName, jdbcPassword);
    LogHelper.info("open new jdbc connection...");

    return connection;
  }
}
