package org.xiaohuahua;

import java.rmi.RemoteException;

public interface RemoteCoordinator {
//Two-Phase commit methods
 public void registerReplica(String replicaId, RemoteReplica replica)
     throws RemoteException;
}
