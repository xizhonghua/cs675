package org.xiaohuahua.can;

import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class BootstrapImpl extends UnicastRemoteObject
    implements Bootstrap {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Max number of nodes in the returned list
   */
  private static final int MAX_NODES = 3;

  private Map<String, String> nodes;

  protected BootstrapImpl() throws RemoteException {
    super();
    nodes = new HashMap<>();
  }

  @Override
  public Map<String, String> getNodeList() throws RemoteException {

    System.out.println("BootstrapImpl.getNodeList");

    int nodesToReturn = Math.min(nodes.size(), MAX_NODES);

    Map<String, String> output = new HashMap<>();
    Random r = new Random(new Date().getTime());

    Object[] keys = nodes.keySet().toArray();

    while (output.size() < nodesToReturn) {
      int index = r.nextInt(keys.length);
      String key = (String) keys[index];
      if (output.containsKey(key))
        continue;
      output.put(key, nodes.get(key));
    }

    return output;
  }

  @Override
  public boolean join(String peerId, String ip)
      throws RemoteException {

    if (nodes.containsKey(peerId)) {
      if (!nodes.get(peerId).equals(ip)) {
        throw new RemoteException("peer \"" + peerId + "\" alredy exists.");
      } else {
        throw new RemoteException("peer \"" + peerId + "\" alredy joined.");
      }
    } else {
      nodes.put(peerId, ip);
      System.out.println("node " + peerId + "@" + ip + " joined CAN!");
      return true;
    }
  }

  @Override
  public boolean leave(String peerId) throws RemoteException {
    if (!nodes.containsKey(peerId)) {
      throw new RemoteException("peer \"" + peerId + "\" does not exists.");
    } else {
      String ip = nodes.remove(peerId);
      System.out.println("node " + peerId + "@ " + ip + " left CAN!");
      return true;
    }
  }

}
