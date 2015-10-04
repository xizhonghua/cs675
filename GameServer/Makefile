SRC = ./src
SRCS = $(shell find $(SRC) -name *.java)
CLASSES = $(SRCS:.java=.class)
SOCKET_SERVER_CLASS = org.xiaohuahua.game.socket.Server
SOCKET_CLIENT_CLASS = org.xiaohuahua.game.socket.Client
RMI_SERVER_CLASS = org.xiaohuahua.game.rmi.Server
RMI_CLIENT_CLASS = org.xiaohuahua.game.rmi.Client
# local server address
LOCAL_SERVER = localhost
# server address
SERVER = medusa-node1.vsnet.gmu.edu

JC = javac
JFLAGS = -classpath $(SRC)

RUN_CMD = java -Djava.security.policy=java.policy \
		 -Djava.rmi.server.codebase=file:$(SRC) \
		 -classpath $(SRC)

RUN_SOCKET_SERVER = ${RUN_CMD} $(SOCKET_SERVER_CLASS)
RUN_SOCKET_CLIENT = ${RUN_CMD} ${SOCKET_CLIENT_CLASS}

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

classes: $(CLASSES)

default: classes

clean:
	rm -f $(CLASSES)

backup:
	tar -cvf \
			cs675_p2_zhonghua.tar \
			--exclude='*.class' \
			Makefile \
			java.policy \
			src/* \
			README.* \
			report/*.pdf

run_socket_server: classes
	${RUN_SOCKET_SERVER}

run_local_socket_client: classes
	${RUN_SOCKET_CLIENT} ${LOCAL_SERVER}

run_socket_client: classes
	${RUN_SOCKET_CLIENT} ${SERVER}