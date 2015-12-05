package org.xiaohuahua;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteMaster extends Remote {

  // Store Methods
  public String get(String key) throws RemoteException;

  public void put(String key, String value) throws RemoteException;

  public void del(String key) throws RemoteException;
  
//Two-Phase commit methods
 public void registerReplica(String replicaId, RemoteReplica replica)
     throws RemoteException;
}
