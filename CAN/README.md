### Distributed Systems Course Project: P2P Architecture
Zhonghua Xi, Sep. 2015

#### Build
```bash
make
```

#### Start RMI Registry
On the machine you want to start peers, run the following script first to start registry 
```bash
./stop_registry.sh
./start_registry.sh
```

#### Start Peers on Medusa Cluster

Pre-defined peers for Medusa Cluster can be easliy started using the following command:
```bash
make run-peer[1-6]
```

Notice: 
* peer1 must be started on medusa-node1 (default bootstrap server) and join CAN first before other peers can join CAN.
* peer[2-6] can be started from any machine in the cluster

The full command to start a peer is:
```bash
java -Djava.security.policy=java.policy \
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
| leave   |              | leave CAN                   |
| exit    |              | leave CAN and exit          |


#### Start Peers on Single Machine
Pre-defined peers can be started on a single machine using the following command:
```bash
make run-local-peer[1-6]
```

### Example
Terminal 1
```bash
make run-local-peer1
java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:./src -classpath ./src org.xiaohuahua.can.NodeServer peer1
[INFO] hostname = zhonghuas-mbp.byod.gmu.edu
[INFO] ip = 10.159.213.86
[INFO] peerId = peer1
[NodeServer] Registering Node Service XIAOHUAHUA_NodeService_peer1 @ 10.159.213.86
[NodeServer] Registering Bootstrap Service XIAOHUAHUA_BootstrapService_peer1 @ 10.159.213.86
[NodeServer] Ready...
>>>
```

Terminal 2
```bash
make run-local-peer2
java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:./src -classpath ./src org.xiaohuahua.can.NodeServer peer2 localhost peer1
[INFO] hostname = zhonghuas-mbp.byod.gmu.edu
[INFO] ip = 10.159.213.86
[INFO] peerId = peer2
[NodeServer] Registering Node Service XIAOHUAHUA_NodeService_peer2 @ 10.159.213.86
[NodeServer] Registering Bootstrap Service XIAOHUAHUA_BootstrapService_peer2 @ 10.159.213.86
[NodeServer] Ready...
>>>
```

Terminal 1
```bash
>>> join
[Node] 1st node in CAN!
[Node] CAN joined!
--------------------------------
| View
| peerId    = peer1
| host      = zhonghuas-mbp.byod.gmu.edu
| ip        = 10.159.213.86
| Zone      = { (0,0), (10,10) }
| Neighbors =
| Files     =
--------------------------------
```

Terminal 2
```bash
>>> join
[Node] Peer = peer1@10.159.213.86
[Node] Route = [peer1@10.159.213.86]
[Node] CAN joined!
--------------------------------
| View
| peerId    = peer2
| host      = zhonghuas-mbp.byod.gmu.edu
| ip        = 10.159.213.86
| Zone      = { (5,0), (5,10) }
| Neighbors =
|  { peer1@10.159.213.86, { (0,0), (5,10) } }
| Files     =
--------------------------------
```

Terminal 1
```
[Bootstrap]peer2@10.159.213.86 requested node list!
[Node] New neighbor added!
[Node] New neighbor = { peer2@10.159.213.86, { (5,0), (5,10) } }
[Node] Neighborpeer2@10.159.213.86's zone updated from { (5,0), (5,10) } to { (5,0), (5,10) }
[Node] peer1@10.159.213.86 notifyed!
--------------------------------
| View
| peerId    = peer1
| host      = zhonghuas-mbp.byod.gmu.edu
| ip        = 10.159.213.86
| Zone      = { (0,0), (5,10) }
| Neighbors =
|  { peer2@10.159.213.86, { (5,0), (5,10) } }
| Files     =
--------------------------------
>>> leave
[Node] Leaving CAN...
[Node] Left CAN!
```

Terminal 2
```
[Node] Zone merged!
--------------------------------
| View
| peerId    = peer2
| host      = zhonghuas-mbp.byod.gmu.edu
| ip        = 10.159.213.86
| Zone      = { (0,0), (10,10) }
| Neighbors =
|  { peer1@10.159.213.86, { (0,0), (5,10) } }
| Files     =
--------------------------------
[Node] Neighbor removed
[Node] Removed neighbor = { peer1@10.159.213.86, { (0,0), (5,10) } }

>>> view
--------------------------------
| View
| peerId    = peer2
| host      = zhonghuas-mbp.byod.gmu.edu
| ip        = 10.159.213.86
| Zone      = { (0,0), (10,10) }
| Neighbors =
| Files     =
--------------------------------
```
