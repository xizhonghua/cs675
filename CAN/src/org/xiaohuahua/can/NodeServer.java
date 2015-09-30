package org.xiaohuahua.can;

import java.net.InetAddress;
import java.rmi.Naming;

public class NodeServer {

  public static void main(String[] args) {

    if (args.length < 1) {
      System.out.println("Usage: java " + NodeServer.class.getName()
          + "peerId [bootstrapAddress bootstrapId]");
      System.exit(-1);
    }

    try {
      System.setSecurityManager(new SecurityManager());

      String peerId = args[0];

      InetAddress localhost = InetAddress.getLocalHost();
      String ip = localhost.getHostAddress();
      String host = localhost.getCanonicalHostName();

      System.out.println("[INFO] hostname = " + host);
      System.out.println("[INFO] ip = " + ip);
      System.out.println("[INFO] peerId = " + peerId);

      String nodeServiceName = Config.NODE_SERVICE_NAME_PREFIX + peerId;

      Bootstrap bootstrap = null;

      String bootstrapAddress = null;
      String bootstrapName = null;
      if (args.length > 2) {
        bootstrapAddress = args[1];
        bootstrapName = args[2];
      }

      // boostrapAddress specified
      if (bootstrapAddress != null) {
        try {
          String bootstrapUri = "rmi://" + bootstrapAddress + "/"
              + Config.BOOTSTRAP_SERVICE_PREFIX + bootstrapName;

          bootstrap = (Bootstrap) Naming.lookup(bootstrapUri);
        } catch (Exception e) {
          System.out.println("[NodeServer] Can not find bootstrap serivce @ "
              + bootstrapAddress + " error: " + e);
        }
      }

      NodeImpl node = new NodeImpl(peerId, host, ip,
          bootstrap);

      System.out.println("[NodeServer] Registering Node Service "
          + nodeServiceName + " @ " + ip);
      Naming.rebind(nodeServiceName, node);

      // bind this node as bootstrap service

      String bootstrapServiceName = Config.BOOTSTRAP_SERVICE_PREFIX + peerId;
      System.out.println("[NodeServer] Registering Bootstrap Service "
          + bootstrapServiceName + " @ " + ip);
      Naming.rebind(bootstrapServiceName, node);

      System.out.println("[NodeServer] Ready...");

      node.run();
    } catch (

    Exception e)

    {
      System.out
          .println("[NodeServer] Failed to register Bootstrap Service: " + e);
    }
  }

}
