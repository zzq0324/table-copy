package top.letscode.tool.execute;

import java.util.concurrent.atomic.AtomicInteger;

public class ExecuteCalculator {

  private static AtomicInteger CALCULATOR = new AtomicInteger();

  public static int calc(int num) {
    return CALCULATOR.addAndGet(num);
  }

  public static int get() {
    return CALCULATOR.get();
  }
}
