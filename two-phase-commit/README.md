# CS 675 Course Project #4: Two-Phase Commit: A simple transaction system
Zhonghua Xi, Dec 2015

### Dependencies
```
sqlite-jdbc-3.8.11.2
gson-2.5
```

### Build
```
$ make
```

### Run at Local

#### Start RMI registry
```
$ ./stop_registry.sh
$ ./start_registry.sh
```

#### Start Master
```
$ make start_master_local
```

#### Start Replicas
```
$ make start_replica0_local
$ make start_replica1_local
```

#### Start client
```
$ make start_client_local
```

### Run at Medusa Cluster

#### Default setting: (can be configured in Makefile) 
`MEDUSA_ARGS = --master medusa-node1.vsnet.gmu.edu --replicas medusa-node1.vsnet.gmu.edu,medusa-node2.vsnet.gmu.edu`

* Master: node1
* Replica0: node1
* Replica1: node2
* Client: any node


#### Start RMI registry 
```
$ ./stop_registry.sh
$ ./start_registry.sh
```

#### Start Master
```
node1 $ make start_master
```

#### Start Replicas 
```
node1 $ make start_replica0
node2 $ make start_replica1
```

#### Start client
```
$ make start_client
```

### Client Usage
#### Put
`>>> put key value`
#### Get
`>>> get key`
#### Del
`>>> del key`
#### Run Script
`>>> script filename`
