package top.letscode.tool.model;

public class ColumnMetadata {

  // column index in select * result set
  private int columnIndex;
  private String columnName;

  public ColumnMetadata() {

  }

  public ColumnMetadata(int columnIndex, String columnName) {
    this.columnIndex = columnIndex;
    this.columnName = columnName;
  }

  /**
   * @return the columnIndex
   */
  public int getColumnIndex() {
    return columnIndex;
  }

  /**
   * @return the columnName
   */
  public String getColumnName() {
    return columnName;
  }

  /**
   * @param columnIndex the columnIndex to set
   */
  public void setColumnIndex(int columnIndex) {
    this.columnIndex = columnIndex;
  }

  /**
   * @param columnName the columnName to set
   */
  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }
}
