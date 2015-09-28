package org.xiaohuahua.can;

import java.net.InetSocketAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface Bootstrap extends Remote {

  /**
   * 
   * @return <peerId, ip>
   * @throws RemoteException
   */
  Map<String, String> getNodeList() throws RemoteException;

  /**
   * A node wants to join CAN
   * 
   * @param peerId
   *          peer id of the new node
   * @param ip
   *          ip of the new node
   * @return true if successfully joined CAN false otherwise
   */
  boolean join(String peerId, String ip) throws RemoteException;

  /**
   * A node wants to leave CAN
   * 
   * @param peerId
   *          peer id of the leaving node
   * @return true if successfully leaved CAN false otherwise
   * @throws RemoteException
   */
  boolean leave(String peerId) throws RemoteException;

}
