package org.xiaohuahua;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
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

  public abstract void recovery();

}
