SRC = ./src
SRCS = $(shell find $(SRC) -name *.java)
CLASSES = $(SRCS:.java=.class)
NODE_CLASS = org.xiaohuahua.can.NodeServer
PEER1_NAME = peer1
PEER2_NAME = peer2
PEER3_NAME = peer3
PEER4_NAME = peer4
PEER5_NAME = peer5
PEER6_NAME = peer6
LOCAL_BOOTSTRAP_SERVER = localhost
LOCAL_BOOTSTRAP_SERVER_NAME = ${LOCAL_BOOTSTRAP_SERVER} ${PEER1_NAME}
BOOTSTRAP_SERVER = medusa-node1.vsnet.gmu.edu
BOOTSTRAP_SERVER_NAME = ${BOOTSTRAP_SERVER} ${PEER1_NAME}

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
	tar -cvf \
			cs675_p1_zhonghua.tar \
			--exclude='*.class' \
			Makefile \
			java.policy \
			src/* \
			README.* \
			report/*.pdf


################################
# medusa cluster mode
################################
run-peer1: classes
	${RUN_NODE} ${PEER1_NAME}

run-peer2: classes
	${RUN_NODE} ${PEER2_NAME}	${BOOTSTRAP_SERVER_NAME}

run-peer3: classes
	${RUN_NODE} ${PEER3_NAME}	${BOOTSTRAP_SERVER_NAME}

run-peer4: classes
	${RUN_NODE} ${PEER4_NAME}	${BOOTSTRAP_SERVER_NAME}

run-peer5: classes
	${RUN_NODE} ${PEER5_NAME}	${BOOTSTRAP_SERVER_NAME}

run-peer6: classes
	${RUN_NODE} ${PEER6_NAME}	${BOOTSTRAP_SERVER_NAME}

################################
# local mode
################################

run-local-peer1: classes
	$(RUN_NODE) ${PEER1_NAME}

run-local-peer2: classes
	$(RUN_NODE) ${PEER2_NAME} ${LOCAL_BOOTSTRAP_SERVER_NAME}

run-local-peer3: classes
	$(RUN_NODE) ${PEER3_NAME} ${LOCAL_BOOTSTRAP_SERVER_NAME}

run-local-peer4: classes
	$(RUN_NODE) ${PEER4_NAME} ${LOCAL_BOOTSTRAP_SERVER_NAME}
