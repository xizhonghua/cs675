package org.xiaohuahua.game.common;

import java.rmi.RemoteException;

public interface IClient {
  public void leave() throws RemoteException;

  public void move(int dx, int dy) throws RemoteException;

  public void openChest() throws RemoteException;

  public GameWorld getWorld();

  public Player getMe();
}
