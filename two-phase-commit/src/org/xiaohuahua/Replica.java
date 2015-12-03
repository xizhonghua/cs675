package org.xiaohuahua;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Replica extends UnicastRemoteObject implements RemoteReplica {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private RemoteMaster master;
  private String replicaId;
  private KVStore store;

  public Replica(String replicaId, RemoteMaster master) throws RemoteException {
    this.replicaId = replicaId;
    this.master = master;

    String db_path = "store_" + this.replicaId + ".db";

    this.store = new SqliteKVStore();
    if (!this.store.open(db_path)) {
      System.err.println("Failed to open database " + db_path);
      System.exit(-1);
    }

    this.register();
  }

  public void register() throws RemoteException {
    this.master.registerReplica(replicaId, this);
  }

  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("Usage: java " + Replica.class.getName()
          + " master_address replica_id");
    }

    String masterAddress = args[0];
    String replicaId = args[1];
    

    try {
      System.setSecurityManager(new SecurityManager());

      String masterServiceName = "rmi://" + masterAddress + "/"
          + Config.MASTER_SERVICE_NAME;
      String replicaServiceName = Config.REPLICA_SERVICE_NAME + "_" + replicaId;

      RemoteMaster master = (RemoteMaster) Naming.lookup(masterServiceName);

      System.out.println("Master found at " + masterServiceName);

      RemoteReplica replica = new Replica(replicaId, master);

      Naming.bind(replicaServiceName, replica);

      System.out.println("Replica binds to " + replicaServiceName);

    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
