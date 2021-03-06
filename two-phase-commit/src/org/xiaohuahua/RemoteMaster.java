package org.xiaohuahua;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteMaster extends Remote {

  // Store Methods
  public String get(String key) throws RemoteException;

  public boolean put(String key, String value) throws RemoteException;

  public boolean del(String key) throws RemoteException;
}
