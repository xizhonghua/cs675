package org.xiaohuahua;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

  public static final String INIT = "INIT";
  public static final String READY = "READY";
  public static final String START_2PC = "START_2PC";
  public static final String VOTE_COMMIT = "VOTE_COMMIT";
  public static final String GLOBAL_ABORT = "GLOBAL_ABORT";
  public static final String GLOBAL_COMMIT = "GLOBAL_COMMIT";

  private String path;

  public Logger(String path) {
    this.path = path;
  }

  // log the state for a given transaction
  public synchronized void log(String state, Transaction t) {

    try (FileWriter fw = new FileWriter(path, true)) {
      fw.write(state + "," + t.toJSON() + "\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // get the latest state for a given transaction
  public synchronized String getLatestState(Transaction t) {
    String state = Logger.GLOBAL_ABORT;

    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      while (true) {
        String[] items = br.readLine().split(",");
        String curState = items[0];
        Transaction curT = Transaction.fromJSON(items[1]);
        if (curT.getId() != t.getId())
          continue;
        switch (curState) {
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
