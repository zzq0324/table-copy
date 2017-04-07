package top.letscode.tool.exception;

public class PropertyNotFoundException extends RuntimeException {

  private static final long serialVersionUID = -2585941972301572568L;

  public PropertyNotFoundException() {
    super();
  }

  public PropertyNotFoundException(String message) {
    super(message);
  }
}
