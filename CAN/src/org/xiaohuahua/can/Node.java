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
   * Remove current zone with the given zone
   * 
   * @param zone
   *          zone to merge
   * @throws RemoteException
   */
  public void mergeZone(Zone zone) throws RemoteException;

  /**
   * Add a temp zone
   * 
   * @param zone
   *          temp zone to add
   * @throws RemoteException
   */
  public void addTempZone(Zone zone) throws RemoteException;

  /**
   * Get neighbor representation of current node
   * 
   * @return
   */
  public Neighbor asNeighbor() throws RemoteException;

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
  public JoinResult joinCAN(String peerId, String ip, Point point)
      throws RemoteException;

  /**
   * Search for a key word
   * 
   * @param key
   * @return
   * @throws RemoteException
   */
  public SearchResult searchCAN(String key) throws RemoteException;

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
  public InsertResult insertCAN(String key, String content)
      throws RemoteException;

  /**
   * Add or update a neighbor
   * 
   * @param neighbor
   *          neighbor to add or update
   * @throws RemoteException
   */
  public void addOrUpdateNeighbor(Neighbor neighbor) throws RemoteException;

  /**
   * Remove a given neighbor
   * 
   * @param neighbor
   *          neighbor to remove
   * @throws RemoteException
   */
  public void removeNeighbor(Neighbor neighbor) throws RemoteException;
}
