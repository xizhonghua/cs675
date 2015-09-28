package org.xiaohuahua.can;

import java.awt.Point;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Node extends Remote {

  /**
   * Check whether the remote node contains the file given by the keyword
   * @param keyword
   * @return
   */
  public Boolean containsFile(String keyword) throws RemoteException;

  /**
   * Check whether the remote node 
   * @param point
   * @return
   */
  public Boolean containsPoint(Point point) throws RemoteException;
}
