package org.xiaohuahua;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteReplica extends Remote {
  public String get(String key) throws RemoteException;  
  
  public Message handleMessage(Message request) throws RemoteException;

}
