package org.xiaohuahua.game.rmi;

import java.awt.Point;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.xiaohuahua.game.common.GameObject;

public interface RemoteServer extends Remote {

  /**
   * Player wants to enter the game
   * 
   * @param name
   *          Player's name (id)
   * @return true if successfully joined. false otherwise
   * @throws RemoteException
   */
  public boolean enterGame(String name) throws RemoteException;

  /**
   * Player wants to leave the game
   * 
   * @param name
   *          Player's name (id)
   * @return true if successfully left. false otherwise
   * @throws RemoteException
   */
  public boolean leaveGame(String name) throws RemoteException;

  /**
   * Move on the board
   * 
   * @param dx
   *          diff in x
   * @param dy
   *          diff in y
   * @return true if moved, false other wise
   */
  public boolean move(String name, int dx, int dy) throws RemoteException;

  /**
   * Get current location
   * 
   * @return
   */
  public Point getLocation(String name) throws RemoteException;

  /**
   * Get all objects in the world
   * 
   * @return
   */
  public List<GameObject> getObjects() throws RemoteException;
}
