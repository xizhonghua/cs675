package org.xiaohuahua;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class Client {

  static Map<String, String> usages = new HashMap<String, String>();

  static {
    usages.put("put", "key value");
    usages.put("del", "key");
    usages.put("help", "");
    usages.put("script", "filename");
  }

  private Config config;

  public Client(Config config) {
    this.config = config;
  }

  private RemoteMaster getMaster() {
    RemoteMaster master = null;

    try {
      master = (RemoteMaster) Naming.lookup(this.config.getMasterName());
    } catch (Exception e) {
      System.out.println(
          "Failed to find Master service at " + this.config.getMasterName());
      e.printStackTrace();
    }

    return master;
  }

  public void put(String key, String value) {
    try {
      if (this.getMaster().put(key, value)) {
        System.out.println("Committed!");
      } else {
        System.out.println("Failed to commit!");
      }
    } catch (ConnectException e) {
      System.out.println("Failed to connect to Master!");
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  public String get(String key) {
    try {
      return this.getMaster().get(key);
    } catch (ConnectException e) {
      System.out.println("Failed to connect to Master!");
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void del(String key) {
    try {
      if (this.getMaster().del(key)) {
        System.out.println("Committed!");
      } else {
        System.out.println("Failed to commit!");
      }
    } catch (ConnectException e) {
      System.out.println("Failed to connect to Master!");
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  private void printUsage(String key) {
    if (key == null) {
      for (String k : usages.keySet()) {
        System.out.println("Usage: " + k + " " + usages.get(k));
      }
    } else {
      System.out.println("Usage: " + key + " " + usages.get(key));
    }
  }

  private void runCmd(String cmd) {
    String[] ops = cmd.split(" ");

    int requiredArgs = 0;
    if (usages.containsKey(ops[0])) {
      String help = usages.get(ops[0]);
      requiredArgs = help.equals("") ? 0 : help.split(" ").length;

      if (ops.length != requiredArgs + 1) {
        printUsage(ops[0]);
        return;
      }
    }

    switch (ops[0]) {
    case "put":
      this.put(ops[1], ops[2]);
      break;
    case "del":
      this.del(ops[1]);
      break;
    case "get":
      System.out.println(this.get(ops[1]));
      break;
    case "script":
      this.runScript(ops[1]);
      break;
    case "help":
      this.printUsage(null);
      break;
    default:
      System.out.println("!Unkown command: " + ops[0]);
      break;
    }
  }

  private void runScript(String filename) {
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(new FileInputStream(filename)))) {
      while (true) {
        String line = br.readLine();
        if (line == null)
          break;
        // Run multiple commands without delay
        String[] cmds = line.split(",");
        for (String cmd : cmds) {
          System.out.println(">>> " + cmd);
          this.runCmd(line);
        }
        // delay for next command
        Thread.sleep(2000);
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void run() {
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(System.in));

    try {
      while (true) {
        System.out.print(">>> ");
        String cmd;

        cmd = reader.readLine();
        if (cmd == null) {
          System.out.print("Bye bye!");
          break;
        }
        runCmd(cmd);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    Config config = Config.parseFromArgs(args);
    Client client = new Client(config);
    client.run();
  }

}
