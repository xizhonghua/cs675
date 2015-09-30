SRC = ./src
SRCS = $(shell find $(SRC) -name *.java)
CLASSES = $(SRCS:.java=.class)
NODE_CLASS = org.xiaohuahua.can.NodeServer
BOOTSTRAP_CLASS = org.xiaohuahua.can.BootstrapServer
BOOTSTRAP_SERVER = localhost
BOOTSTRAP_NODE1 = node1
BOOTSTRAP_NODE2 = node2
BOOTSTRAP_NODE3 = node3

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
	$(RUN_NODE) node1

run-node2: classes
	$(RUN_NODE) node2 ${BOOTSTRAP_SERVER} ${BOOTSTRAP_NODE1}

run-node3: classes
	$(RUN_NODE) node3 ${BOOTSTRAP_SERVER} ${BOOTSTRAP_NODE1}

run-node4: classes
	$(RUN_NODE) node4 ${BOOTSTRAP_SERVER} ${BOOTSTRAP_NODE1}
