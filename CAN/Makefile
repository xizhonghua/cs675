SRC = ./src
SRCS = $(shell find $(SRC) -name *.java)
CLASSES = $(SRCS:.java=.class)
NODECLASS = org.xiaohuahua.can.Node

JC = javac
JFLAGS = -g -classpath $(SRC)

RUNCOMD = java -Djava.security.policy=java.policy \
		 -Djava.rmi.server.codebase=file:$(SRC) \
		 -classpath $(SRC) \
		 $(NODECLASS)

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
	$(RUNCOMD) node1

run-node2: classes
	$(RUNCOMD) node2