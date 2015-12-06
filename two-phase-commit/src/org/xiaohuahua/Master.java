package org.xiaohuahua;

import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
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

  private Set<String> requests;
  private Logger logger;
  private Config config;
  private boolean recoveryMode = false;

  public Master(Config config) throws RemoteException {
    this.logger = new Logger("master.log");
    this.requests = new HashSet<>();
    this.config = config;
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

    if (!this.recoveryMode)
      this.logger.log(Event.START_2PC, t);

    if (!recoveryMode && t.getKey().equals("crash_on_start_2pc")) {
      System.err.println("panic!");
      System.exit(0);
    }

    Message voteRequest = new Message("Master", -1, MessageType.VOTE_REQUEST);
    voteRequest.setTransaction(t);

    List<Message> voteRsps = this.broadcast(voteRequest);

    int commitVotes = 0;
    for (Message m : voteRsps)
      if (m.getType() == MessageType.VOTE_COMMIT)
        commitVotes++;

    Message command = null;

    // All replicas vote commit
    if (commitVotes == Config.NUM_OF_REPLICAS && voteCommit) {
      this.logger.log(Event.GLOBAL_COMMIT, t);
      command = new Message("Master", -1, MessageType.GLOBAL_COMMIT);
    } else {
      // timeout or abort
      this.logger.log(Event.GLOBAL_ABORT, t);
      command = new Message("Master", -1, MessageType.GLOBAL_ABORT);
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

  // helper functions

  private RemoteReplica getRandomReplica() {
    int id = random.nextInt(Config.NUM_OF_REPLICAS);

    System.out.println("replica " + id + " selected!");

    return this.getReplica(id);
  }

  private RemoteReplica getReplica(int id) {
    try {
      return (RemoteReplica) Naming.lookup(this.config.getReplicaName(id));
    } catch (Exception e) {
      System.out.println("Failed to find replica service at "
          + this.config.getReplicaName(id));
      // e.printStackTrace();
    }

    return null;
  }

  // broadcast a message to all replicas and receive replies
  private List<Message> broadcast(Message request) {

    System.out.println("Broadcasting message: " + request);

    List<Message> replies = new ArrayList<>();

    for (int i = 0; i < Config.NUM_OF_REPLICAS; ++i) {
      RemoteReplica rep = this.getReplica(i);
      // TODO(zxi) handle unavailable replicas
      if (rep == null) {
        continue;
      }
      try {
        Message response = rep.handleMessage(request);
        replies.add(response);
        System.out.println("Response" + i + ": " + response);
      } catch (ConnectException e) {
        System.out.println("Failed to connect to replica " + i);
      } catch (RemoteException e) {
        // TODO(zxi) handle timeout,
        e.printStackTrace();
      }
    }

    return replies;
  }

  public synchronized void recovery() {

    this.recoveryMode = true;

    Map<Transaction, Set<String>> events = this.logger.parseLog();

    for (Transaction t : events.keySet()) {
      Set<String> states = events.get(t);

      // Transaction started
      if (states.contains(Event.START_2PC)) {
        // no global state
        if (!states.contains(Event.GLOBAL_ABORT)
            || !states.contains(Event.GLOBAL_COMMIT)) {
          System.out.println("re-try transaction: " + t);
          this.twoPhaseCommit(t);
        }
      }
    }

    this.recoveryMode = false;
  }

  public static void main(String[] args) {
    try {
      System.setSecurityManager(new SecurityManager());

      Config config = Config.parseFromArgs(args);

      Master master = new Master(config);

      Naming.rebind(config.getMasterName(), master);

      System.out.println("Master binds to " + config.getMasterName());

      // do a recovery from log
      System.out.println("Master recovering...");
      master.recovery();
      System.out.println("Master recovered!");

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
