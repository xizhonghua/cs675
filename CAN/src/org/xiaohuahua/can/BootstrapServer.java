package org.xiaohuahua.can;

import java.rmi.Naming;

public class BootstrapServer {

  public static void main(String[] args) {
    try {
      System.setSecurityManager(new SecurityManager());
      System.out.println(BootstrapImpl.NAME + "Registering Bootstrap Service");
      BootstrapImpl remote = new BootstrapImpl();
      Naming.rebind(Config.BOOTSTRAP_SERVICE_PREFIX, remote);
      System.out.println(BootstrapImpl.NAME + "Ready...");
    } catch (Exception e) {
      System.out.println(
          BootstrapImpl.NAME + "Failed to register Bootstrap Service: " + e);
    }
  }

}
