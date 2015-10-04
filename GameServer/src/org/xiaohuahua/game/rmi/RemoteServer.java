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
   * @return true a token that player can use to interact with server
   * @throws RemoteException
   */
  public String enterGame(String name) throws RemoteException;

  /**
   * Player wants to leave the game
   * 
   * @param token
   *          Player's token
   * @return true if successfully left. false otherwise
   * @throws RemoteException
   */
  public boolean leaveGame(String token) throws RemoteException;

  /**
   * Move on the board
   * 
   * @param dx
   *          diff in x
   * @param dy
   *          diff in y
   * @return true if moved, false other wise
   */
  public boolean move(String token, int dx, int dy) throws RemoteException;

  /**
   * Get current location
   * 
   * @return
   */
  public Point getLocation(String token) throws RemoteException;

  /**
   * Open the chest at current location
   * 
   * @param token
   *          player's token
   * @return
   * @throws RemoteException
   */
  public int open(String token) throws RemoteException;

  /**
   * Get all objects in the world
   * 
   * @return
   */
  public List<GameObject> getObjects() throws RemoteException;

  /**
   * Echo the input
   * 
   * @param input
   * @return
   * @throws RemoteException
   */
  public String echo(String input) throws RemoteException;
}
