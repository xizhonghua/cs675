package org.xiaohuahua;

public class Config {
  public static final String MASTER_SERVICE_NAME = "MasterService";
  public static final String REPLICA_SERVICE_NAME = "ReplicaService";

  public static final int NUM_OF_REPLICAS = 2;

  private String masterAddress;
  private String[] replicaAddresses;
  private int replicaId;

  public Config() {
    this.masterAddress = "localhost";
    this.replicaAddresses = new String[NUM_OF_REPLICAS];
    this.replicaId = -1;
  }

  public String getMasterAddress() {
    return this.masterAddress;
  }
  
  public int getReplicaId() {
    return this.replicaId;
  }

  public String getMasterName() {
    return "rmi://" + this.masterAddress + "/" + MASTER_SERVICE_NAME;
  }

  public String getReplicaAddress(int id) {
    return this.replicaAddresses[id];
  }

  public String getReplicaName(int id) {
    return "rmi://" + this.replicaAddresses[id] + "/" + REPLICA_SERVICE_NAME
        + id;
  }

  public static Config parseFromArgs(String[] args) {
    Config config = new Config();
    for (int i = 0; i < args.length; ++i) {
      switch (args[i]) {
      case "--master":
        config.masterAddress = args[++i];
        break;
      case "--replicas":
        config.replicaAddresses = args[++i].split(",");
        break;
      case "--repId":
        config.replicaId = Integer.parseInt(args[++i]);
      }
    }

    return config;
  }
}
