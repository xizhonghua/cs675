package org.xiaohuahua;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteReplica extends Remote {
  public String get(String key) throws RemoteException;

  public void del(String key) throws RemoteException;

  public void put(String key, String value) throws RemoteException;

}
