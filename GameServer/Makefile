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
		 -classpath $(SRC)

RUN_SOCKET_SERVER = ${RUN_CMD} $(SOCKET_SERVER_CLASS)
RUN_SOCKET_CLIENT = ${RUN_CMD} ${SOCKET_CLIENT_CLASS}
RUN_SOCKET_TEST_CLIENT = ${RUN_CMD} ${SOCKET_TEST_CLIENT_CLASS}


RUN_RMI_SERVER = ${RUN_CMD} $(RMI_SERVER_CLASS)
RUN_RMI_CLIENT = ${RUN_CMD} $(RMI_CLIENT_CLASS)
RUN_RMI_TEST_CLIENT = ${RUN_CMD} $(RMI_TEST_CLIENT_CLASS)

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

#############################################
# Socket 
#############################################
run_socket_server: classes
	${RUN_SOCKET_SERVER}

run_local_socket_client1: classes
	${RUN_SOCKET_CLIENT} Player1 ${LOCAL_SERVER}

run_local_socket_client2: classes
	${RUN_SOCKET_CLIENT} Player2 ${LOCAL_SERVER}

run_local_socket_client3: classes
	${RUN_SOCKET_CLIENT} Player3 ${LOCAL_SERVER}

run_local_socket_test_client: classes
	${RUN_SOCKET_TEST_CLIENT} test ${LOCAL_SERVER} 

run_socket_client1: classes
	${RUN_SOCKET_CLIENT} Player1 ${SERVER}

run_socket_client2: classes
	${RUN_SOCKET_CLIENT} Player2 ${SERVER}

run_socket_client3: classes
	${RUN_SOCKET_CLIENT} Player3 ${SERVER}

run_socket_test_client: classes
	${RUN_SOCKET_TEST_CLIENT} test ${SERVER} 

#############################################
# RMI
#############################################
run_rmi_server: classes
	${RUN_RMI_SERVER}

run_rmi_client1: classes
	${RUN_RMI_CLIENT} Player1 ${SERVER}

run_rmi_client2: classes
	${RUN_RMI_CLIENT} Player2 ${SERVER}

run_rmi_client3: classes
	${RUN_RMI_CLIENT} Player3 ${SERVER}

run_local_rmi_client: classes
	${RUN_RMI_TEST_CLIENT} test ${SERVER}

run_local_rmi_client1: classes
	${RUN_RMI_CLIENT} Player1 ${LOCAL_SERVER} 

run_local_rmi_client2: classes
	${RUN_RMI_CLIENT} Player2 ${LOCAL_SERVER} 

run_local_rmi_client3: classes
	${RUN_RMI_CLIENT} Player3 ${LOCAL_SERVER} 

run_local_rmi_test_client: classes
	${RUN_RMI_TEST_CLIENT} test ${LOCAL_SERVER} 