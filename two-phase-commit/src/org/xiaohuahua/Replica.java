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
  private Logger logger;

  public Replica(String replicaId, RemoteMaster master) throws RemoteException {

    this.replicaId = replicaId;
    this.master = master;
    this.logger = new Logger(replicaId + ".log");

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

  @Override
  public String get(String key) throws RemoteException {
    return this.store.get(key);
  }

  private void del(String key) {
    this.store.del(key);
  }

  private void put(String key, String value) {
    this.store.put(key, value);
  }

  private void commitTranscation(Transaction t) {
    switch (t.getType()) {
    case DEL:
      this.del(t.getKey());
      break;
    case PUT:
      this.put(t.getKey(), t.getValue());
      break;
    }
  }

  @Override
  public Message handleMessage(Message request) throws RemoteException {

    Transaction t = request.getTranscation();

    Message response = new Message(this.replicaId);
    response.setTransaction(t);

    System.out.println("Message: " + request);

    switch (request.getType()) {
    case VOTE_REQUEST:
      // always vote commit

      response.setType(MessageType.VOTE_COMMIT);
      break;
    case GLOBAL_COMMIT:
      // do the commit
      this.commitTranscation(t);

      break;
    case GLOBAL_ABORT:
      // do nothing
      break;
    default:
      // do nothing
      break;
    }

    try {
      // delay response
      Thread.sleep(2000);
    } catch (InterruptedException e) {
    }

    return response;
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

      Naming.rebind(replicaServiceName, replica);

      System.out.println("Replica binds to " + replicaServiceName);

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
