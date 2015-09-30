package org.xiaohuahua.can;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface Bootstrap extends Remote {

  /**
   * List random node list
   * 
   * @param peerId
   *          requester peerId
   * @param ip
   *          requester ip
   * @return <peerId, ip>
   * @throws RemoteException
   */
  Map<String, String> getNodeList(String peerId, String ip)
      throws RemoteException;

}
