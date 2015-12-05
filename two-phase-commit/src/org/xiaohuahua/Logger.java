package org.xiaohuahua;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

  public static final String INIT = "INIT";
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
    String state = Logger.GLOBAL_ABORT;

    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      while (true) {
        String line = br.readLine();
        switch (line) {
        case GLOBAL_ABORT:
          state = GLOBAL_ABORT;
          break;
        case GLOBAL_COMMIT:
          state = GLOBAL_COMMIT;
          break;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return state;
  }
}
