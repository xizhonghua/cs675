package org.xiaohuahua;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;

public class Client {

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

  private void runCmd(String cmd) {
    String[] ops = cmd.split(" ");
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
    RemoteMaster master = new Master(); // TODO(zxi) get master

    Client client = new Client(master);
    client.run();
  }

}
