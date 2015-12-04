package org.xiaohuahua;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Master extends UnicastRemoteObject implements RemoteMaster {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static final Random random = new Random(new Date().getTime());

  private Map<String, RemoteReplica> replicas;

  public Master() throws RemoteException {
    this.replicas = new HashMap<>();
  }

  @Override
  public String get(String key) throws RemoteException {
    String value = this.getRandomReplica().get(key);

    System.out.println("get(" + key + ") = " + value);

    return value;
  }

  @Override
  public void put(String key, String value) throws RemoteException {
    this.getRandomReplica().put(key, value);

    System.out.println("put(" + key + "," + value + ")");
  }

  @Override
  public void del(String key) throws RemoteException {
    this.getRandomReplica().del(key);

    System.out.println("del(" + key + ")");
  }

  @Override
  public void registerReplica(String replicaId, RemoteReplica replica)
      throws RemoteException {
    this.replicas.put(replicaId, replica);
    System.out.println("replica " + replicaId + " registered!");
  }

  // helper functions

  private RemoteReplica getRandomReplica() {
    List<String> keys = new ArrayList<String>(this.replicas.keySet());

    String replicaId = keys.get(random.nextInt(keys.size()));

    System.out.println("replica " + replicaId + "selected!");

    return this.replicas.get(replicaId);
  }

  public static void main(String[] args) {
    try {
      System.setSecurityManager(new SecurityManager());

      RemoteMaster master = new Master();

      Naming.rebind(Config.MASTER_SERVICE_NAME, master);

      System.out.println("Master binds to " + Config.MASTER_SERVICE_NAME);

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
