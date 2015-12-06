package org.xiaohuahua;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

public class Replica extends Server implements RemoteReplica {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private KVStore store;

  public Replica(Config config) throws RemoteException {

    super(config);

    this.logger = new Logger(this.getName() + ".log");

    String db_path = this.getName() + ".db";

    this.store = new SqliteKVStore();
    if (!this.store.open(db_path)) {
      System.err.println("Failed to open database " + db_path);
      System.exit(-1);
    }
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

  private String getName() {
    return "rep" + this.config.getReplicaId();
  }

  @Override
  public Message handleMessage(Message request) throws RemoteException {

    // ignore message from itself
    if (request.getSenderType().equals("Replica")
        && request.getSenderId() == this.config.getReplicaId()) {
      return null;
    }

    Transaction t = request.getTranscation();

    Message response = new Message("Replica", this.config.getReplicaId(),
        MessageType.ACK);
    response.setTransaction(t);

    System.out.println("Request: " + request);

    switch (request.getType()) {

    case DECISION_REQUEST:
      response.setType(MessageType.DECISION_RESPONSE);

      String state = this.logger.getLatestState(t);
      if (state.equals(Event.GLOBAL_COMMIT))
        response.setDecision(Event.GLOBAL_COMMIT);
      else
        response.setDecision(Event.GLOBAL_ABORT);
      break;

    case VOTE_REQUEST:
      // always vote commit

      this.logger.log(Event.VOTE_COMMIT, t);
      // send to master
      response.setType(MessageType.VOTE_COMMIT);
      break;
    case GLOBAL_COMMIT:
      this.logger.log(Event.GLOBAL_COMMIT, t);
      // do the commit
      this.commitTranscation(t);

      break;
    case GLOBAL_ABORT:
      // do nothing
      this.logger.log(Event.GLOBAL_ABORT, t);
      break;
    default:
      // do nothing
      break;
    }

    try {
      // delay response
      Thread.sleep(random.nextInt(500) + 100);
    } catch (InterruptedException e) {
    }

    System.out.println("Respone: " + response);

    return response;
  }

  protected void recoveryImpl() {

    Map<Transaction, Set<String>> events = this.logger.parseLog();

    for (Transaction t : events.keySet()) {
      Set<String> states = events.get(t);

      if (states.contains(Event.VOTE_COMMIT)) {
        // vote commit, no decision
        if (!states.contains(Event.GLOBAL_ABORT)
            || !states.contains(Event.GLOBAL_COMMIT)) {

          // send decision request to other replicas
          Message decisionRequest = new Message("Replica",
              this.config.getReplicaId(), MessageType.DECISION_REQUEST);

          this.broadcast(decisionRequest);

        }
      }
    }
  }

  public static void main(String[] args) {
    if (args.length < 2) {
      System.out
          .println("Usage: java " + Replica.class.getName() + " --repId id");
      return;
    }

    try {
      System.setSecurityManager(new SecurityManager());

      Config config = Config.parseFromArgs(args);

      Replica replica = new Replica(config);

      Naming.rebind(config.getReplicaName(config.getReplicaId()), replica);

      replica.recovery();

      System.out.println(
          "Replica bound to " + config.getReplicaName(config.getReplicaId()));

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
