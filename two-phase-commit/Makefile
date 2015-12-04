SRC = ./src
SRCS = $(shell find $(SRC) -name *.java)
CLASSES = $(SRCS:.java=.class)
SOCKET_SERVER_CLASS = org.xiaohuahua.game.socket.Server
SOCKET_CLIENT_CLASS = org.xiaohuahua.game.socket.Client
SOCKET_TEST_CLIENT_CLASS = org.xiaohuahua.game.socket.TestClient
RMI_SERVER_CLASS = org.xiaohuahua.game.rmi.Server
RMI_CLIENT_CLASS = org.xiaohuahua.game.rmi.Client
RMI_TEST_CLIENT_CLASS = org.xiaohuahua.game.rmi.TestClient
# local server address
LOCAL_SERVER = localhost
# server address
SERVER = medusa-node1.vsnet.gmu.edu

JC = javac
JFLAGS = -classpath $(SRC)

RUN_CMD = java -Djava.security.policy=java.policy \
		 -Djava.rmi.server.codebase=file:$(SRC) \
		 -classpath $(SRC):./sqlite-jdbc-3.8.11.2.jar

RUN_SOCKET_SERVER = ${RUN_CMD} $(SOCKET_SERVER_CLASS)
RUN_SOCKET_CLIENT = ${RUN_CMD} ${SOCKET_CLIENT_CLASS}
RUN_SOCKET_TEST_CLIENT = ${RUN_CMD} ${SOCKET_TEST_CLIENT_CLASS}


RUN_RMI_SERVER = ${RUN_CMD} $(RMI_SERVER_CLASS)
RUN_RMI_CLIENT = ${RUN_CMD} $(RMI_CLIENT_CLASS)
RUN_RMI_TEST_CLIENT = ${RUN_CMD} $(RMI_TEST_CLIENT_CLASS)

RUN_REPLICA = ${RUN_CMD} org.xiaohuahua.Replica

RUN_REPLICA_LOCAL_MASTER = ${RUN_REPLICA} localhost

RUN_CLIENT_BASE = ${RUN_CMD} org.xiaohuahua.Client
RUN_CLIENT_LOCAL = ${RUN_CLIENT_BASE} localhost

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

classes: $(CLASSES)

default: classes

clean:
	rm -f $(CLASSES)

backup:
	tar -zcvf \
			cs675_p2_zhonghua.tar.gz \
			--exclude='*.class' \
			--disable-copyfile \
			Makefile \
			java.policy \
			src/* \
			README.* \
			*.sh \
			report/*.pdf

run_db_test: ${CLASSES}
	${RUN_CMD} org.xiaohuahua.SqliteKVStore

run_client_test: ${CLASSES}
	${RUN_CMD} org.xiaohuahua.Client

start_master: ${CLASSES}
	${RUN_CMD} org.xiaohuahua.Master

start_client_local: ${CLASSES}
	${RUN_CLIENT_LOCAL}

start_replica1_local: ${CLASSES}
	${RUN_REPLICA_LOCAL_MASTER} rep1

start_replica2_local: ${CLASSES}
	${RUN_REPLICA_LOCAL_MASTER} rep2