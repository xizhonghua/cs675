package org.xiaohuahua.game.rmi;

import java.awt.Point;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import org.xiaohuahua.game.common.Chest;
import org.xiaohuahua.game.common.GameMap;
import org.xiaohuahua.game.common.GameObject;
import org.xiaohuahua.game.common.Player;

public class ServerImpl extends UnicastRemoteObject implements RemoteServer {

  private GameMap map;
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

    inited = true;
  }

  @Override
  public boolean move(String name, int dx, int dy) throws RemoteException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Point getLocation(String name) throws RemoteException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<GameObject> getObjects() throws RemoteException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean enterGame(String name) throws RemoteException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean leaveGame(String name) throws RemoteException {
    // TODO Auto-generated method stub
    return false;
  }

}
