package org.xiaohuahua;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

  private RemoteMaster master;

  public Client(RemoteMaster master) {
    this.master = master;
  }

  public void put(String key, String value) {
    try {
      master.put(key, value);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  public String get(String key) {
    try {
      return master.get(key);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void del(String key) {
    try {
      master.del(key);
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

  public void runScript(String filename) {
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(new FileInputStream(filename)))) {
      while (true) {
        String line = br.readLine();
        if (line == null)
          break;
        System.out.println(">>> " + line);
        this.runCmd(line);
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

    if (args.length < 1) {
      System.out
          .println("Usage: java " + Client.class.getName() + "[master_server]");
    }

    String serverAddress = args.length > 1 ? args[1] : "localhost";
    String fullServiceName = "rmi://" + serverAddress + "/"
        + Config.MASTER_SERVICE_NAME;
    RemoteMaster master = null;

    try {
      master = (RemoteMaster) Naming.lookup(fullServiceName);
    } catch (Exception e) {
      System.out.println("Failed to find Master service at " + fullServiceName);
      e.printStackTrace();
    }

    Client client = new Client(master);
    client.run();
  }

}
