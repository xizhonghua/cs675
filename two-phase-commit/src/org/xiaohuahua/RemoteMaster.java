package org.xiaohuahua;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteMaster extends Remote {
  public String get(String key) throws RemoteException;

  public void put(String key, String value) throws RemoteException;

  public void del(String key) throws RemoteException;
}
