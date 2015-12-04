package org.xiaohuahua;

import java.io.FileWriter;
import java.io.IOException;

public class Logger {

  public static final String START_2PC = "START_2PC";
  public static final String GLOBAL_ABORT = "GLOBAL_ABORT";
  public static final String GLOBAL_COMMIT = "GLOBAL_COMMIT";

  private FileWriter fw;

  public Logger(String path) {
    try {
      this.fw = new FileWriter(path, true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void log(String log) {
    try {
      fw.write(log + "\n");
      fw.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
