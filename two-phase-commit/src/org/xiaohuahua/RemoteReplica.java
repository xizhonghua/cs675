package org.xiaohuahua;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteReplica extends Remote {
  public String get(String key) throws RemoteException;

  // handle message
  public Message handleMessage(Message request) throws RemoteException;

}
