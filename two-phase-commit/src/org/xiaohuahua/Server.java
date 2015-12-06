package org.xiaohuahua;

import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public abstract class Server extends UnicastRemoteObject {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  protected static final Random random = new Random(new Date().getTime());

  protected Logger logger;
  protected Config config;
  protected boolean recoveryMode = false;

  protected Server(Config config) throws RemoteException {
    super();

    this.config = config;
  }

  protected RemoteReplica getReplica(int id) {
    try {
      return (RemoteReplica) Naming.lookup(this.config.getReplicaName(id));
    } catch (Exception e) {
      System.out.println("Failed to find replica service at "
          + this.config.getReplicaName(id));
    }

    return null;
  }

  protected RemoteMaster getMaster() {

    try {
      return (RemoteMaster) Naming.lookup(this.config.getMasterName());
    } catch (Exception e) {
      System.out.println(
          "Failed to find Master service at " + this.config.getMasterName());
      e.printStackTrace();
    }

    return null;
  }

  // broadcast a message to all replicas and receive replies
  protected List<Message> broadcast(Message request) {

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
        if (response != null) {
          replies.add(response);
          // log ACK events
          if (response.getType() == MessageType.ACK) {
            this.logger.log(Event.ACK, response.getTranscation());
          }
        }
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

  public final synchronized void recovery() {
    this.recoveryMode = true;
    System.out.println("recovering...");

    this.recoveryImpl();

    this.recoveryMode = false;
    System.out.println("recovered!");

  }

  protected abstract void recoveryImpl();

}
