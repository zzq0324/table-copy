package top.letscode.tool.execute;

import java.util.Map;

import top.letscode.tool.db.mysql.JdbcHelper;
import top.letscode.tool.log.LogHelper;
import top.letscode.tool.model.TableMetaData;
import top.letscode.tool.util.ConstantValue;
import top.letscode.tool.util.PropertyLoader;
import top.letscode.tool.util.Utils;

public class ExecuteTask {

  public void execute() {
    long startTime = System.currentTimeMillis();
    LogHelper.info("start execute task, start time: " + startTime);

    String migrationTables =
        PropertyLoader.getConfig(ConstantValue.CONFIG_FILE_NAME, "migration.tables");

    String[] tableArr = migrationTables.split(",");
    for (int i = 0; i < tableArr.length; i++) {
      try {
        startCopyThread(tableArr[i]);
      } catch (Exception e) {
        LogHelper.error(" execute exeption ", e);
      }
    }

    long endTime = System.currentTimeMillis();
    LogHelper.info("finish execute task, end time: " + endTime + ". cost time: "
        + (endTime - startTime) + "ms");
  }

  private void startCopyThread(String tableInfo) throws Exception {
    String srcTable = tableInfo.split(":")[0];
    String destTable = tableInfo.split(":")[1];

    int srcTableCount = JdbcHelper.getCount(srcTable);
    int destTableCount = JdbcHelper.getCount(destTable);
    LogHelper.info("src table " + srcTable + " count: " + srcTableCount + " ,dest table "
        + destTable + " count:" + destTableCount);

    int threadCount =
        PropertyLoader.getConfig(ConstantValue.CONFIG_FILE_NAME, "thread.count", Integer.class);
    int avgPerThread = srcTableCount / threadCount;
    LogHelper.info("execute thread count: " + threadCount + ", every thread will execute "
        + avgPerThread + " rows!");

    // get table structure
    TableMetaData tableMetadata = JdbcHelper.getTableMetadata(srcTable);
    ExecuteContext.setTableMetadata(tableMetadata);

    // format execute insert sql
    // for example: insert into dest(f1,f2,f3...) select f1,f2,f3... from src where id between
    // ? and ? order by id limit ?
    StringBuilder columnsBuilder = new StringBuilder();
    for (int i = 0; i < tableMetadata.getColumnCount(); i++) {
      if (i > 0) {
        columnsBuilder.append(",");
      }
      columnsBuilder.append(tableMetadata.getColumnList().get(i).getColumnName());
    }
    String columns = columnsBuilder.toString();

    String primaryColumnName =
        PropertyLoader.getConfig(ConstantValue.CONFIG_FILE_NAME, "primary.key.name");
    int fetchDataNum = PropertyLoader.getConfig(ConstantValue.CONFIG_FILE_NAME,
        "fetch.data.everytime", Integer.class);

    StringBuilder sqlBuilder =
        new StringBuilder("INSERT INTO ").append(destTable).append("(").append(columns).append(") ")
            .append(" SELECT ").append(columns).append(" FROM ").append(srcTable).append(" WHERE ")
            .append(primaryColumnName).append(" BETWEEN ? AND ? ORDER BY ")
            .append(primaryColumnName).append(" LIMIT ").append(fetchDataNum);
    ExecuteContext.setExecuteInsertSql(sqlBuilder.toString());

    for (int i = 0; i < threadCount; i++) {
      int limit = avgPerThread;
      if (i == threadCount - 1) {
        // last thread maybe should execute more than avg rows.for example,rows:201,thread 5, last
        // thread should execute 41 rows
        limit = avgPerThread + threadCount + 1;
      }
      Map<String, Object> maxMinMap = JdbcHelper.getMaxMinId(srcTable, i * avgPerThread, limit);
      new Thread(new SegmentExecutor(maxMinMap.get("minId"), maxMinMap.get("maxId"), destTable,
          primaryColumnName), "segment-thread-" + i).start();

      // 每个线程间隔500毫秒再启动
      Utils.sleepQuietly(500);
    }

    // 开始监视拷贝情况
    startMonitor(srcTableCount, destTableCount, destTable);
    // 拷贝完毕后，从db中查询验证下count数
    srcTableCount = JdbcHelper.getCount(srcTable);
    destTableCount = JdbcHelper.getCount(destTable);
    LogHelper.info("validate from db ,src table " + srcTable + " count: " + srcTableCount
        + " ,dest table " + destTable + " count:" + destTableCount);
  }

  private static void startMonitor(int srcTableCount, int destTableCount, String destTableName)
      throws Exception {
    int lastDestTableCount = destTableCount;
    while (true) {
      destTableCount = ExecuteCalculator.get();
      LogHelper.info("src table count: " + srcTableCount + ", dest table count: " + destTableCount);
      if (destTableCount == srcTableCount) {
        break;
      }
      // count does not change in recent 10 seconds,may be finished
      if (lastDestTableCount == destTableCount) {
        break;
      }
      lastDestTableCount = destTableCount;

      Utils.sleepQuietly(10000);
    }

    LogHelper.info("finish monitor data copy !");
  }
}
