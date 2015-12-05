package org.xiaohuahua;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.xiaohuahua.Transaction.TransactionType;

public class Master extends UnicastRemoteObject implements RemoteMaster {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static final Random random = new Random(new Date().getTime());

  private Map<String, RemoteReplica> replicas;
  private Set<String> requests;
  private Logger logger;

  public Master() throws RemoteException {
    this.replicas = new HashMap<>();
    this.logger = new Logger("master.log");
    this.requests = new HashSet<>();
  }

  @Override
  public String get(String key) throws RemoteException {
    String value = this.getRandomReplica().get(key);

    System.out.println("get(" + key + ") = " + value);

    return value;
  }

  private void twoPhaseCommit(Transaction t) {

    boolean voteCommit = false;

    synchronized (this.requests) {
      // vote commit if there is no concurrent writes on the same key
      if (!requests.contains(t.getKey())) {
        requests.add(t.getKey());
        voteCommit = true;
      }
    }

    this.logger.log(Logger.START_2PC);

    Message voteRequest = new Message(MessageType.VOTE_REQUEST);
    voteRequest.setTransaction(t);

    List<Message> voteRsps = this.broadcast(voteRequest);

    int commitVotes = (int) voteRsps.stream()
        .filter(m -> m.getMessageType() == MessageType.VOTE_COMMIT).count();

    Message command = null;

    // All replicas vote commit
    if (commitVotes == this.replicas.values().size() && voteCommit) {
      this.logger.log(Logger.GLOBAL_COMMIT);
      command = new Message(MessageType.GLOBAL_COMMIT);
    } else {
      // timeout or abort
      this.logger.log(Logger.GLOBAL_ABORT);
      command = new Message(MessageType.GLOBAL_ABORT);
    }

    command.setTransaction(t);
    this.broadcast(command);

    if (voteCommit) {
      synchronized (this.requests) {
        this.requests.remove(t.getKey());
      }
    }
  }

  @Override
  public void put(String key, String value) throws RemoteException {

    System.out.println("put(" + key + "," + value + ")");

    Transaction t = new Transaction(TransactionType.PUT, key, value);

    this.twoPhaseCommit(t);
  }

  @Override
  public void del(String key) throws RemoteException {

    System.out.println("del(" + key + ")");

    Transaction t = new Transaction(TransactionType.DEL, key, null);

    this.twoPhaseCommit(t);
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

  // broadcast a message to all replicas and receive replies
  private List<Message> broadcast(Message request) {

    System.out.println("Broadcasting message: " + request);

    List<Message> replies = new ArrayList<>();
    for (RemoteReplica rep : this.replicas.values()) {
      try {
        Message response = rep.handleMessage(request);
        replies.add(response);
      } catch (RemoteException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    return replies;
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
