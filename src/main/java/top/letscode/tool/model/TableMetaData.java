package top.letscode.tool.model;

import java.util.List;

public class TableMetaData {

  private String tableName;
  private int primaryColumnIndex;
  private int columnCount;
  private List<ColumnMetadata> columnList;

  public TableMetaData() {

  }

  public TableMetaData(String tableName) {
    this.tableName = tableName;
  }

  /**
   * @return the tableName
   */
  public String getTableName() {
    return tableName;
  }

  /**
   * @return the columnList
   */
  public List<ColumnMetadata> getColumnList() {
    return columnList;
  }

  /**
   * @param tableName the tableName to set
   */
  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  /**
   * @param columnList the columnList to set
   */
  public void setColumnList(List<ColumnMetadata> columnList) {
    this.columnList = columnList;
  }

  /**
   * @return the columnCount
   */
  public int getColumnCount() {
    return columnCount;
  }

  /**
   * @param columnCount the columnCount to set
   */
  public void setColumnCount(int columnCount) {
    this.columnCount = columnCount;
  }

  /**
   * @return the primaryColumnIndex
   */
  public int getPrimaryColumnIndex() {
    return primaryColumnIndex;
  }

  /**
   * @param primaryColumnIndex the primaryColumnIndex to set
   */
  public void setPrimaryColumnIndex(int primaryColumnIndex) {
    this.primaryColumnIndex = primaryColumnIndex;
  }
}
