package org.xiaohuahua.can;

import java.rmi.Naming;

public class NodeServer {

  public static void main(String[] args) {

    if (args.length < 2) {
      System.out.println("Usage: java " + NodeServer.class.getName()
          + " peerId bootstrapAddress");
      System.exit(-1);
    }

    try {

      String peerId = args[0];
      String bootstrapAddress = args[1];           

      System.setSecurityManager(new SecurityManager());

      String bootstrapUri = "rmi://" + bootstrapAddress + "/" + Config.BOOTSTRAP_SERVICE_NAME;

      Bootstrap bootstrap = null;

      try {
        bootstrap = (Bootstrap) Naming.lookup(bootstrapUri);
      } catch (Exception e) {
        System.out.println("Server: Can not find bootstrap serivce @ "
            + bootstrapAddress + " error: " + e);
      }

      System.out.println("Server: Registering Node Service " + peerId);
      NodeImpl node = new NodeImpl(peerId, bootstrap);      
      Naming.rebind(Config.NODE_SERVICE_NAME_PREFIX + peerId, node);      
      System.out.println("Server: Ready...");
      node.join();
    } catch (Exception e) {
      System.out.println("Server: Failed to register Bootstrap Service: " + e);
    }
  }

}
