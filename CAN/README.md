### Distributed Systems Course Project: P2P Architecture
Zhonghua Xi, Sep. 2015

#### Build
```bash
make
```

#### Start Peer on Medusa Cluster
Peer can be easliy started using the following command:
```bash
make run-peer[1-6]
```

Notice: 
* peer1 must start and join CAN first before other peers can join CAN since it will be used as the default Bootstrap server.
* peer[2-6] can be started from any machine in the cluster
