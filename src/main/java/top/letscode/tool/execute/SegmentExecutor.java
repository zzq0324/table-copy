package top.letscode.tool.execute;

import top.letscode.tool.db.mysql.JdbcHelper;
import top.letscode.tool.log.LogHelper;
import top.letscode.tool.util.ConstantValue;
import top.letscode.tool.util.PropertyLoader;
import top.letscode.tool.util.Utils;

public class SegmentExecutor implements Runnable {

  private Object startId;
  private Object endId;
  private String destTable;
  private String primaryColumnName;

  public SegmentExecutor(Object startId, Object endId, String destTable, String primaryColumnName) {
    this.startId = startId;
    this.endId = endId;
    this.destTable = destTable;
    this.primaryColumnName = primaryColumnName;
  }

  private Object idPlusOne(Object id) {
    if (id instanceof Integer) {
      return new Integer(id.toString()) + 1;
    } else if (id instanceof Long) {
      return new Long(id.toString()) + 1;
    }

    return id;
  }

  @Override
  public void run() {
    long sleepMills =
        PropertyLoader.getConfig(ConstantValue.CONFIG_FILE_NAME, "thread.sleep.time", Long.class);
    // check startId again, maybe have already move some data to dest table.
    try {
      Object newStartId = JdbcHelper.getMaxId(startId, endId, destTable, primaryColumnName);
      if (!newStartId.equals(startId)) {
        LogHelper.info("startId: " + startId + ", newStartId: " + newStartId
            + ", executor will continue at " + newStartId);
        startId = newStartId;
        startId = idPlusOne(startId);
      }

      while (true) {
        LogHelper.info("will execute at startId=" + startId + ", endId=" + endId);
        int effectRows = JdbcHelper.batchCopy(startId, endId);
        ExecuteCalculator.calc(effectRows);
        if (effectRows == 0) {
          break;
        }
        // update startId
        startId = JdbcHelper.getMaxId(startId, endId, destTable, primaryColumnName);
        startId = idPlusOne(startId);

        Utils.sleepQuietly(sleepMills);
      }

      ExecuteContext.closeConnection();
    } catch (Exception e) {
      LogHelper.error("execute error ", e);
    }
  }
}
