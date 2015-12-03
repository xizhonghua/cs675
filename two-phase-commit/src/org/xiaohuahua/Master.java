package org.xiaohuahua;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class Master extends UnicastRemoteObject implements RemoteMaster {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Map<String, RemoteReplica> replicas;

  public Master() throws RemoteException {
    this.replicas = new HashMap<>();
  }

  @Override
  public String get(String key) throws RemoteException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void put(String key, String value) throws RemoteException {
    // TODO Auto-generated method stub

  }

  @Override
  public void del(String key) throws RemoteException {
    // TODO Auto-generated method stub

  }

  @Override
  public void registerReplica(String replicaId, RemoteReplica replica)
      throws RemoteException {
    this.replicas.put(replicaId, replica);
  }

  public static void main(String[] args) {
    try {
      System.setSecurityManager(new SecurityManager());

      RemoteMaster master = new Master();

      Naming.bind(Config.MASTER_SERVICE_NAME, master);

      System.out.println("Master binds to " + Config.MASTER_SERVICE_NAME);

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
