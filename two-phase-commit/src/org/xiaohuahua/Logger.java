package org.xiaohuahua;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Logger {  

  private String path;

  public Logger(String path) {
    this.path = path;
  }

  // log the state for a given transaction
  public synchronized void log(String state, Transaction t) {

    try (FileWriter fw = new FileWriter(path, true)) {
      fw.write(state + " " + t.toJSON() + "\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // get the latest state for a given transaction
  public synchronized String getLatestState(Transaction t) {
    String state = Event.GLOBAL_ABORT;

    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      while (true) {
        String[] items = br.readLine().split(" ");
        String curState = items[0];
        Transaction curT = Transaction.fromJSON(items[1]);
        if (curT.getId() != t.getId())
          continue;
        switch (curState) {
        case Event.GLOBAL_ABORT:
          state = Event.GLOBAL_ABORT;
          break;
        case Event.GLOBAL_COMMIT:
          state = Event.GLOBAL_COMMIT;
          break;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return state;
  }

  public synchronized Map<Transaction, Set<String>> parseLog() {
    Map<Transaction, Set<String>> events = new HashMap<>();

    // log file does not exist
    File f = new File(path);
    if (!f.exists() || f.isDirectory())
      return events;

    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      while (true) {
        String line = br.readLine();
        if (line == null)
          break;
        String[] items = line.split(" ");

        System.out.println(items[0] + "|" + items[1]);

        String curState = items[0];
        Transaction curT = Transaction.fromJSON(items[1]);

        Set<String> curEvents;
        if (!events.containsKey(curT)) {
          curEvents = new HashSet<>();
          events.put(curT, curEvents);
        } else {
          curEvents = events.get(curT);
        }

        curEvents.add(curState);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return events;

  }
}
