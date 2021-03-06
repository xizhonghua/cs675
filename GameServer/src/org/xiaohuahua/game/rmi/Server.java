package org.xiaohuahua.game.rmi;

import java.rmi.Naming;

import org.xiaohuahua.game.common.Config;

public class Server {

  public static void main(String[] args) {

    try {
      System.setSecurityManager(new SecurityManager());

      ServerImpl server = new ServerImpl();
      server.init();

      // bind game service
      System.out.println(
          "[RMIServer] Registering Game Service " + Config.GAME_SERVICE_NAME);
      Naming.rebind(Config.GAME_SERVICE_NAME, server);

      System.out.println("[RMIServer] Ready...");
    } catch (Exception e) {
      System.out.println("[RMIServer] Error: " + e);
      e.printStackTrace();
    }
  }
}
