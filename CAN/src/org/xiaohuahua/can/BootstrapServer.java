package org.xiaohuahua.can;

import java.rmi.Naming;

public class BootstrapServer {

  public static void main(String[] args) {
    try {
      System.setSecurityManager(new SecurityManager());
      System.out.println("Server: Registering Bootstrap Service");
      BootstrapImpl remote = new BootstrapImpl();
      Naming.rebind(Config.BOOTSTRAP_SERVICE_NAME, remote);
      System.out.println("Server: Ready...");
    } catch (Exception e) {
      System.out.println("Server: Failed to register Bootstrap Service: " + e);
    }
  }

}
