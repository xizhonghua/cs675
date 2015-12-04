package org.xiaohuahua;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

  public static final String START_2PC = "START_2PC";
  public static final String GLOBAL_ABORT = "GLOBAL_ABORT";
  public static final String GLOBAL_COMMIT = "GLOBAL_COMMIT";

  private String path;

  public Logger(String path) {
    this.path = path;
  }

  public void log(String log) {
    try (FileWriter fw = new FileWriter(path, true)) {
      fw.write(log + "\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public String getLatestState() {
    try {
      BufferedReader br = new BufferedReader(new FileReader(path));
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }
}
