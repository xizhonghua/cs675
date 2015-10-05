### Distributed Systems Course Project: Game Server
Zhonghua Xi, Oct. 2015

#### Demo
Video https://youtu.be/WiXmO5ZJrIA

#### Build
```bash
$ make
```

#### Start Socket Game Server

Socket Game Server be can started using the following command:
```bash
$ make run_socket_server
```

Note: this command should be run on Medusa-node1 (default server) if you want to use a remote game server.
You can modify the makefile if you want to use a different remote game server

The full command to start the socket game server is is:
```bash
$ java -Djava.security.policy=java.policy \
       -Djava.rmi.server.codebase=file:./src \
       -classpath ./src \
       org.xiaohuahua.game.socket.Server [port]
```

#### Start Socket Game Client
Socket Game Client be can started using the following command, the default game server is Medusa-node1
```bash
$ make run_socket_client[1-3]
```

Note: if you started a local game client, you can use the following command to start the client:
```bash
$ make run_local_socket_client[1-3]
```

#### Start RMI Registry
On the machine you want to start RMI Game server, run the following script first to start registry 
```bash
$ ./stop_registry.sh
$ ./start_registry.sh
```

#### Start RMI Game Server

RMI Game Server be can started using the following command:
```bash
$ make run_rmi_server
```

Note: this command should be run on Medusa-node1 (default server) if you want to use a remote game server

#### Start RMI Game Client
Socket Game Client be can started using the following command, the default game server is Medusa-node1
```bash
$ make run_rmi_client[1-3]
```

Note: if you started local rmi game clients, you can use the following command to start the client:
```bash
$ make run_local_rmi_client[1-3]
```


#### Game Control
Once a game client started, you can use the following key to control the role:

* Arrows: move
* Space: open chest
* ESC: leave game