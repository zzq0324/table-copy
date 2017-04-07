package top.letscode.tool.execute;

import java.sql.Connection;

import top.letscode.tool.db.mysql.JdbcHelper;
import top.letscode.tool.db.mysql.MysqlConnectionFactory;
import top.letscode.tool.model.TableMetaData;

public class ExecuteContext {

  private static ThreadLocal<Connection> CONN_HOLDER = new ThreadLocal<Connection>();
  private static TableMetaData TABLE_METADATA = null;
  private static String EXECUTE_INSERT_SQL = null;

  public static void setTableMetadata(TableMetaData tableMetadata) {
    TABLE_METADATA = tableMetadata;
  }

  public static TableMetaData getTableMetadata() {
    return TABLE_METADATA;
  }

  public static Connection getConnection() throws Exception {
    Connection connection = CONN_HOLDER.get();
    if (connection == null) {
      CONN_HOLDER.set(MysqlConnectionFactory.openConnection());
      connection = CONN_HOLDER.get();
    }

    return connection;
  }

  public static void closeConnection() {
    Connection connection = CONN_HOLDER.get();
    if (connection != null) {
      JdbcHelper.closeQuietly(connection);
    }
  }

  public static void setExecuteInsertSql(String sql) {
    EXECUTE_INSERT_SQL = sql;
  }

  public static String getExecuteInsertSql() {
    return EXECUTE_INSERT_SQL;
  }
}
