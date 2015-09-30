### Distributed Systems Course Project: P2P Architecture
Zhonghua Xi, Sep. 2015

#### Build
```bash
$ make
```

#### Start RMI Registry
On the machine you want to start peers, run the following script first to start registry 
```bash
$ ./stop_registry.sh
$ ./start_registry.sh
```

#### Start Peers on Medusa Cluster

Pre-defined peers for Medusa Cluster can be easliy started using the following command:
```bash
$ make run-peer[1-6]
```

Notice: 
* peer1 must be started on medusa-node1 (default bootstrap server) and join CAN first before other peers can join CAN.
* peer[2-6] can be started from any machine in the cluster

The full command to start a peer is:
```bash
$ java -Djava.security.policy=java.policy \
       -Djava.rmi.server.codebase=file:./src \
       -classpath ./src \
       org.xiaohuahua.can.NodeServer \
       peerId [bootstrapServerAddress bootstrapServerName]
```

#### CLI
Once a node is started, the following commands can be used to interactive with the node.

| Command | Arguments    | Comment                     |
|:-------:|:------------:|:---------------------------:|
| join    |              | join CAN                    |
| view    |              | view current peer's info    |
| insert  | key, content | insert the content into CAN |
| search  | key          | search content by key       |
| script  | filename     | run a script                |
| leave   |              | leave CAN                   |
| exit    |              | leave CAN and exit          |

#### Scripting
Scripting is implementated for dome purpose. Once a node is started, you can use the following command to run a script.
```bash
>>> script filename
```


#### Start Peers on Single Machine
Pre-defined peers can be started on a single machine using the following command:
```bash
$ make run-local-peer[1-6]
```
