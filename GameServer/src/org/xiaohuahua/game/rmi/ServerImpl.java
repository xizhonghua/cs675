package org.xiaohuahua.game.rmi;

import java.awt.Point;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xiaohuahua.game.common.Chest;
import org.xiaohuahua.game.common.GameMap;
import org.xiaohuahua.game.common.GameObject;
import org.xiaohuahua.game.common.Player;

public class ServerImpl extends UnicastRemoteObject implements RemoteServer {

  private GameMap map;
  // token to player mapping
  private Map<String, Player> tokens;
  private boolean inited = false;

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  protected ServerImpl() throws RemoteException {
    super();
  }

  public void init() {
    if (inited)
      return;

    this.map = new GameMap();
    this.tokens = new HashMap<>();

    inited = true;
  }

  private Player getPlayerByToken(String token) throws RemoteException {
    if (tokens.containsKey(token)) {
      return this.tokens.get(token);
    }

    throw new RemoteException("Invalid token.");

  }

  @Override
  public boolean move(String token, int dx, int dy) throws RemoteException {
    Player p = this.getPlayerByToken(token);
    return p.move(new Point(dx, dy));
  }

  @Override
  public Point getLocation(String token) throws RemoteException {
    Player p = this.getPlayerByToken(token);
    return p.getLocation();
  }

  @Override
  public List<GameObject> getObjects() throws RemoteException {
    return this.map.getObjects();
  }

  @Override
  public String enterGame(String name) throws RemoteException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean leaveGame(String token) throws RemoteException {
    Player p = this.getPlayerByToken(token);
    this.map.removePlayer(p.getName());
    this.tokens.remove(token);
    return true;
  }

  @Override
  public int open(String token) throws RemoteException {
    Player p = this.getPlayerByToken(token);
    return this.map.openChest(p.getLocation());
  }

}
