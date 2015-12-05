# CS 675 Course Project #4: Two-Phase Commit: A simple transaction system
#### Author: Zhonghua Xi 
#### Date: Dec 2015


### Build
`$ make`

### Run at Local

* Start RMI registry
```
$ ./stop_registry.sh
$ ./start_registry.sh
```

* Start Master
```
$ make start_master_local
```

* Start Replicas
```
make start_replica0_local
make start_replica1_local
```

* Start client
```
make start_client_local
```

### Run at Medusa Cluster

#### Default setting: (can be configured in Makefile) 
`MEDUSA_ARGS = --master medusa-node1.vsnet.gmu.edu --replicas medusa-node1.vsnet.gmu.edu,medusa-node2.vsnet.gmu.edu`

* Master: node1
* Replica0: node1
* Replica1: node2
* Client: any node

#### Commands

* Start RMI registry 
`$ ./stop_registry.sh`
`$ ./start_registry.sh`

* Start Master
On node1 `$ make start_master`

* Start Replicas 
On node1 `$ make start_replica0`
On node2 `$ make start_replica1`

* Start client
On any node `$ make start_client`
