### Distributed Systems Course Project: P2P Architecture
Zhonghua Xi, Sep. 2015

#### Build
```bash
make
```

#### Start RMI Registry
On the machine you want to start the peer, run the following script first to start registry 
```bash
./stop_registry.sh
./start_registry.sh
```

#### Start Peer on Medusa Cluster

Pre-defined peers can be easliy started using the following shortcuts:
```bash
make run-peer[1-6]
```

The full command to start a peer is:
```bash
java -Djava.security.policy=java.policy \
     -Djava.rmi.server.codebase=file:./src \
     -classpath ./src \
     org.xiaohuahua.can.NodeServer \
     peerName [bootstrapServerAddress bootstrapServerName]
```

Notice: 
* peer1 must start and join CAN first before other peers can join CAN since it will be used as the default Bootstrap server.
* peer[2-6] can be started from any machine in the cluster

#### Commands
