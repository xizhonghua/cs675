package org.xiaohuahua.can;

import java.net.InetAddress;
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

      String bootstrapUri = "rmi://" + bootstrapAddress + "/"
          + Config.BOOTSTRAP_SERVICE_NAME;

      String nodeServiceName = Config.NODE_SERVICE_NAME_PREFIX + peerId;

      Bootstrap bootstrap = null;

      try {
        bootstrap = (Bootstrap) Naming.lookup(bootstrapUri);
      } catch (Exception e) {
        System.out.println("Server: Can not find bootstrap serivce @ "
            + bootstrapAddress + " error: " + e);
      }

      InetAddress localhost = InetAddress.getLocalHost();
      String ip = localhost.getHostAddress();

      System.out.println(
          "Server: Registering Node Service " + nodeServiceName + " @ " + ip);
      NodeImpl node = new NodeImpl(peerId, ip, bootstrap);

      Naming.rebind(nodeServiceName, node);

      System.out.println("Server: Ready...");
      node.run();
    } catch (Exception e) {
      System.out.println("Server: Failed to register Bootstrap Service: " + e);
    }
  }

}
