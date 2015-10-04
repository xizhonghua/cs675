package org.xiaohuahua.game.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;

import org.xiaohuahua.game.common.Config;
import org.xiaohuahua.game.common.StrUtil;

public class TestClient {

  private String name;
  private RemoteServer server;

  public TestClient(String name, RemoteServer server) {
    this.name = name;
    this.server = server;
  }

  public void runTest(int minLen, int count) throws RemoteException {

    long startTime = System.nanoTime();

    String msg = StrUtil.getRandomStr(minLen);
    for (int i = 0; i < count; ++i) {
      String rsp = server.echo(msg);
      if (rsp.length() != msg.length()) {
        System.out.println("Error! Length not match");
      }
    }

    long endTime = System.nanoTime();
    double opTime = (endTime - startTime) / 1e6 / count;

    System.out.println(
        "Length = " + msg.length() + " op time = " + opTime + "ms");
  }

  public static void main(String[] args) throws RemoteException {

    if (args.length < 1) {
      System.out.print(
          "Usage: java " + Client.class.getName() + " playerName [server]");
    }

    String name = args[0];
    String serverName = args.length > 1 ? args[1] : "localhost";
    String fullServiceName = "rmi://" + serverName + "/"
        + Config.GAME_SERVICE_NAME;
    RemoteServer server = null;

    try {
      server = (RemoteServer) Naming.lookup(fullServiceName);
    } catch (Exception e) {
      System.out.print("Failed to find game service at " + fullServiceName);
      e.printStackTrace();
    }

    for (int ml = 1; ml <= 100000; ml *= 10) {
      TestClient client = new TestClient(name, server);
      client.runTest(ml, 1000);
    }
  }
}
