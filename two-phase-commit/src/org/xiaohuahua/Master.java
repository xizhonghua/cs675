package org.xiaohuahua;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xiaohuahua.Transaction.TransactionType;

public class Master extends Server implements RemoteMaster {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Set<String> requests;

  public Master(Config config) throws RemoteException {
    super(config);

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

  private boolean twoPhaseCommit(Transaction t) {

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

    if (!recoveryMode && t.getKey().equals("master_crash_after_start_2pc")) {
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

    // committed
    return command.getType() == MessageType.GLOBAL_COMMIT;
  }

  @Override
  public boolean put(String key, String value) throws RemoteException {

    System.out.println("put(" + key + "," + value + ")");

    Transaction t = new Transaction(TransactionType.PUT, key, value);

    return this.twoPhaseCommit(t);
  }

  @Override
  public boolean del(String key) throws RemoteException {

    System.out.println("del(" + key + ")");

    Transaction t = new Transaction(TransactionType.DEL, key, null);

    return this.twoPhaseCommit(t);
  }

  // helper functions

  private RemoteReplica getRandomReplica() {
    int id = random.nextInt(Config.NUM_OF_REPLICAS);

    System.out.println("replica " + id + " selected!");

    return this.getReplica(id);
  }

  protected void recoveryImpl() {

    Map<Transaction, Set<String>> events = this.logger.parseLog();

    for (Transaction t : events.keySet()) {
      Set<String> states = events.get(t);

      if (states.contains(Event.START_2PC)) {
        // 2pc started but no global state
        if (!states.contains(Event.GLOBAL_ABORT)
            && !states.contains(Event.GLOBAL_COMMIT)) {
          System.out.println("no global state, re-try: " + t);
          this.twoPhaseCommit(t);
        }

        else if ((states.contains(Event.GLOBAL_ABORT)
            || states.contains(Event.GLOBAL_COMMIT))
            && !states.contains(Event.ACK)) {
          // has global decision but no ACK
          System.out.println("no ACK: " + t);

          // get decision
          MessageType decision = MessageType
              .valueOf(this.logger.getLatestState(t));

          Message command = new Message("Master", -1, decision);

          command.setTransaction(t);
          this.broadcast(command);
        }
      }
    }
  }

  public static void main(String[] args) {
    try {
      System.setSecurityManager(new SecurityManager());

      Config config = Config.parseFromArgs(args);

      Master master = new Master(config);

      Naming.rebind(config.getMasterName(), master);

      System.out.println("Master bound to " + config.getMasterName());

      master.recovery();

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
