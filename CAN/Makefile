SRC = ./src
SRCS = $(shell find $(SRC) -name *.java)
CLASSES = $(SRCS:.java=.class)
NODE_CLASS = org.xiaohuahua.can.NodeServer
BOOTSTRAP_SERVER = localhost
NODE1_NAME = node1
NODE2_NAME = node2
NODE3_NAME = node3
NODE4_NAME = node4

JC = javac
JFLAGS = -g -classpath $(SRC)

RUN_CMD = java -Djava.security.policy=java.policy \
		 -Djava.rmi.server.codebase=file:$(SRC) \
		 -classpath $(SRC)

RUN_NODE = ${RUN_CMD} $(NODE_CLASS)
RUN_BOOTSTRAP = ${RUN_CMD} ${BOOTSTRAP_CLASS}

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

classes: $(CLASSES)

default: classes

clean:
	rm -f $(CLASSES)

backup:
	tar -c -f src.tar src/*

run-node1: classes
	$(RUN_NODE) ${NODE1_NAME}

run-node2: classes
	$(RUN_NODE) ${NODE2_NAME} ${BOOTSTRAP_SERVER} ${NODE1_NAME}

run-node3: classes
	$(RUN_NODE) ${NODE3_NAME} ${BOOTSTRAP_SERVER} ${NODE1_NAME}

run-node4: classes
	$(RUN_NODE) ${NODE4_NAME} ${BOOTSTRAP_SERVER} ${NODE1_NAME}
