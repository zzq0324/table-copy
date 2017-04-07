package top.letscode.tool.db.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.letscode.tool.execute.ExecuteContext;
import top.letscode.tool.log.LogHelper;
import top.letscode.tool.model.ColumnMetadata;
import top.letscode.tool.model.TableMetaData;
import top.letscode.tool.util.ConstantValue;
import top.letscode.tool.util.PropertyLoader;

public class JdbcHelper {

  /**
   * 通过sql: insert into xxx select的方式进行数据库内部的数据拷贝
   * 
   * @param startId
   * @param endId
   * @return
   * @throws Exception
   */
  public static int batchCopy(Object startId, Object endId) throws Exception {
    Connection conn = ExecuteContext.getConnection();
    PreparedStatement statement = null;

    try {
      statement = conn.prepareStatement(ExecuteContext.getExecuteInsertSql());
      statement.setObject(1, startId);
      statement.setObject(2, endId);
      return statement.executeUpdate();
    } finally {
      closeQuietly(statement, null);
    }
  }

  /**
   * 获取某个id段内存在的最大id
   * 
   * @param startId
   * @param endId
   * @param tableName
   * @param primaryColumnName
   * @return
   * @throws Exception
   */
  public static Object getMaxId(Object startId, Object endId, String tableName,
      String primaryColumnName) throws Exception {
    StringBuilder sqlBuilder = new StringBuilder("SELECT MAX(").append(primaryColumnName)
        .append(") AS startId FROM ").append(tableName).append(" WHERE ").append(primaryColumnName)
        .append(" BETWEEN ? AND ?");
    Connection conn = ExecuteContext.getConnection();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try {
      statement = conn.prepareStatement(sqlBuilder.toString());
      statement.setObject(1, startId);
      statement.setObject(2, endId);
      resultSet = statement.executeQuery();

      if (resultSet.next()) {
        Object dbStartId = resultSet.getObject("startId");
        if (dbStartId != null) {
          startId = dbStartId;
        }
      }

      return startId;
    } finally {
      closeQuietly(statement, resultSet);
    }
  }

  /**
   * 获取某个段内的最大和最小id
   * 
   * @param tableName
   * @param start
   * @param limit
   * @return
   * @throws Exception
   */
  public static Map<String, Object> getMaxMinId(String tableName, int start, int limit)
      throws Exception {
    String primaryColumnName =
        PropertyLoader.getConfig(ConstantValue.CONFIG_FILE_NAME, "primary.key.name");

    StringBuilder sqlBuilder = new StringBuilder("SELECT MAX(").append(primaryColumnName)
        .append(") AS maxId,MIN(").append(primaryColumnName).append(") AS minId FROM (SELECT ")
        .append(primaryColumnName).append(" FROM ").append(tableName).append(" ORDER BY ")
        .append(primaryColumnName).append(" LIMIT ?,?) t");
    Connection conn = ExecuteContext.getConnection();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try {
      statement = conn.prepareStatement(sqlBuilder.toString());
      statement.setInt(1, start);
      statement.setInt(2, limit);
      resultSet = statement.executeQuery();

      resultSet.next();

      Map<String, Object> resultMap = new HashMap<String, Object>();
      resultMap.put("maxId", resultSet.getObject("maxId"));
      resultMap.put("minId", resultSet.getObject("minId"));
      return resultMap;
    } finally {
      closeQuietly(statement, resultSet);
    }
  }

  /**
   * 获取某个表的数据总量
   * 
   * @param tableName
   * @return
   * @throws Exception
   */
  public static int getCount(String tableName) throws Exception {
    String sql = "SELECT COUNT(1) AS cn FROM " + tableName;
    Connection conn = ExecuteContext.getConnection();
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    try {
      statement = conn.prepareStatement(sql);
      resultSet = statement.executeQuery();
      resultSet.next();

      return resultSet.getInt("cn");
    } finally {
      closeQuietly(statement, resultSet);
    }
  }

  /**
   * 获取表结构，主要包括字段顺序、字段名称以及字段总数
   * 
   * @param tableName
   * @return
   * @throws Exception
   */
  public static TableMetaData getTableMetadata(String tableName) throws Exception {
    LogHelper.info("*************** start get table: " + tableName + "'s metadata *************");
    Connection conn = ExecuteContext.getConnection();
    String sql = "SELECT * FROM " + tableName + " WHERE 1 != 1";
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    TableMetaData tableMetadata;
    try {
      statement = conn.prepareStatement(sql);
      resultSet = statement.executeQuery();
      ResultSetMetaData metadata = resultSet.getMetaData();

      tableMetadata = new TableMetaData(tableName);
      List<ColumnMetadata> columnMetadataList = new ArrayList<ColumnMetadata>();
      String primaryColumnName =
          PropertyLoader.getConfig(ConstantValue.CONFIG_FILE_NAME, "primary.key.name");

      for (int i = 1; i <= metadata.getColumnCount(); i++) {
        String columnName = metadata.getColumnName(i);
        columnMetadataList.add(new ColumnMetadata(i, columnName));
        if (primaryColumnName.equals(columnName)) {
          tableMetadata.setPrimaryColumnIndex(i);
        }
        LogHelper.info("columnIndex: " + i + ", columnName: " + columnName);
      }
      tableMetadata.setColumnList(columnMetadataList);
      tableMetadata.setColumnCount(metadata.getColumnCount());
      LogHelper.info("*************** end get table: " + tableName + "'s metadata *************");
    } finally {
      closeQuietly(statement, resultSet);
    }

    return tableMetadata;
  }

  public static void closeQuietly(Connection connection) {
    if (connection != null) {
      try {
        connection.close();
      } catch (Exception e) {
        LogHelper.error("close connection error: ", e);
      }
    }
  }

  public static void closeQuietly(Statement statement, ResultSet resultSet) {
    if (resultSet != null) {
      try {
        resultSet.close();
      } catch (Exception e) {
        LogHelper.error("close resultset error: ", e);
      }
    }

    if (statement != null) {
      try {
        statement.close();
      } catch (Exception e) {
        LogHelper.error("close statement error: ", e);
      }
    }
  }
}
