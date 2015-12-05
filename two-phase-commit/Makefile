SRC = ./src
SRCS = $(shell find $(SRC) -name *.java)
CLASSES = $(SRCS:.java=.class)

CP = $(SRC):./sqlite-jdbc-3.8.11.2.jar:./gson-2.5.jar

JC = javac
JFLAGS = -classpath ${CP}

RUN_CMD = java -Djava.security.policy=java.policy \
		 -Djava.rmi.server.codebase=file:$(SRC) \
		 -classpath ${CP}

LOCAL_ARGS  = --master localhost --replicas localhost,localhost
MEDUSA_ARGS = --master medusa-node1.vsnet.gmu.edu --replicas medusa-node1.vsnet.gmu.edu,medusa-node2.vsnet.gmu.edu


RUN_MASTER_BASE = ${RUN_CMD} org.xiaohuahua.Master
RUN_MASTER_LOCAL = ${RUN_MASTER_BASE} ${LOCAL_ARGS}

RUN_REPLICA_BASE = ${RUN_CMD} org.xiaohuahua.Replica
RUN_REPLICA_LOCAL = ${RUN_REPLICA_BASE} ${LOCAL_ARGS}

RUN_CLIENT_BASE = ${RUN_CMD} org.xiaohuahua.Client
RUN_CLIENT_LOCAL = ${RUN_CLIENT_BASE} ${LOCAL_ARGS}


.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

classes: $(CLASSES)

default: classes

clean:
	rm -f $(CLASSES)

backup:
	tar -zcvf \
			cs675_p4_zhonghua.tar.gz \
			--exclude='*.class' \
			--disable-copyfile \
			Makefile \
			java.policy \
			*.jar \
			src/* \
			README.* \
			*.sh \
			report/*.pdf

run_db_test: ${CLASSES}
	${RUN_CMD} org.xiaohuahua.SqliteKVStore

start_master_local: ${CLASSES}
	${RUN_MASTER_LOCAL}

start_master: ${CLASSES}
	${RUN_MASTER_BASE} ${MEDUSA_ARGS}

start_client_local: ${CLASSES}
	${RUN_CLIENT_LOCAL}

start_client: ${CLASSES}
	${RUN_CLIENT_BASE} ${MEDUSA_ARGS}


start_replica0_local: ${CLASSES}
	${RUN_REPLICA_LOCAL} --repId 0

start_replica0: ${CLASSES}
	${RUN_REPLICA_BASE} ${MEDUSA_ARGS} --repId 0

start_replica1_local: ${CLASSES}
	${RUN_REPLICA_LOCAL} --repId 1

start_replica1: ${CLASSES}
	${RUN_REPLICA_BASE} ${MEDUSA_ARGS} --repId 1


clear_log:
	rm *.log

clear_all:
	rm *.log *.db