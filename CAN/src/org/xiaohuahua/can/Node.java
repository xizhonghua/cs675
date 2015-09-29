package org.xiaohuahua.can;

import java.awt.Point;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Node extends Remote {

  /**
   * Check whether the remote node contains a given point
   * 
   * @param point
   * @return
   */
  public Boolean containsPoint(Point point) throws RemoteException;

  /**
   * Compute the distance to a given point
   * 
   * @param point
   * @return
   * @throws RemoteException
   */
  public double distanceTo(Point point) throws RemoteException;

  /**
   * Get neighbor representation of current node
   * 
   * @return
   */
  public Neighbor asNeighbor();

  /**
   * Help a new node to join CAN
   * 
   * @param peerId
   *          new node's id
   * @param ip
   *          new node's ip
   * @param point
   *          a random point picked by the new node
   * @return
   * @throws RemoteException
   */
  public JoinResult canJoin(String peerId, String ip, Point point)
      throws RemoteException;

  /**
   * Search for a key word
   * 
   * @param key
   * @return
   * @throws RemoteException
   */
  public SearchResult canSearch(String key) throws RemoteException;

  /**
   * Insert a file
   * 
   * @param key
   *          keyword of the file
   * @param content
   *          content of the file
   * @return
   * @throws RemoteException
   */
  public ResultBase canInsert(String key, String content)
      throws RemoteException;
}
