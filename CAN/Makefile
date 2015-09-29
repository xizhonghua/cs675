SRC = ./src
SRCS = $(shell find $(SRC) -name *.java)
CLASSES = $(SRCS:.java=.class)
NODE_CLASS = org.xiaohuahua.can.NodeServer
BOOTSTRAP_CLASS = org.xiaohuahua.can.BootstrapServer
BOOTSTRAP_SERVER = localhost

JC = javac
JFLAGS = -g -classpath $(SRC)

RUN_CMD = java -Djava.security.policy=java.policy \
		 -Djava.rmi.server.codebase=file:$(SRC) \
		 -classpath $(SRC)

RUN_NODE = ${RUN_CMD} $(NODE_CLASS) ${BOOTSTRAP_SERVER}
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
	$(RUN_NODE) node2

run-bootstrap: classes
	${RUN_BOOTSTRAP}