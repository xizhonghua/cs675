package org.xiaohuahua.game.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.xiaohuahua.game.common.Config;
import org.xiaohuahua.game.common.Player;
import org.xiaohuahua.game.common.StrUtil;

public class TestClient {
  private BufferedReader in;
  private PrintWriter out;
  private String name;
  private Socket socket;
  private Player player;

  public TestClient(String name, String host, int port)
      throws UnknownHostException, IOException {

    this.name = name;
    this.socket = new Socket(host, port);
  }

  public void run(int minLen, int msgToSend) throws IOException {

    System.out.println("Test client started!");

    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new PrintWriter(socket.getOutputStream(), true);

    this.player = new Player(this.name, 0, 0);

    String echoMsg = StrUtil.getRandomStr(minLen);

    long startTime = 0;
    long count = 0;
    // enter game, and run test
    while (true) {
      String line = in.readLine();
      // System.out.println(line.length());
      if (line.startsWith(Message.REQ_NAME)) {
        out.println(this.player.getName());
        out.println(echoMsg);
      } else if (line.startsWith(Message.ECHO)) {
        if (startTime == 0) {
          startTime = System.nanoTime();
        }
        count++;
        if (count < msgToSend) {
          out.println(echoMsg);
        } else {
          long endTime = System.nanoTime();
          double opTime = (endTime - startTime) / 1e6;
          System.out.println(
              "Length = " + echoMsg.length() + " op time = " + opTime + "ms");
          socket.close();
          break;
        }
      }
    }

  }

  public static void main(String args[]) {
    int port = Config.DEFAULT_PORT;
    String host = "localhost";

    if (args.length < 2) {
      System.out.println("Usage: java " + Client.class.getName()
          + " playerName [server] [port]");
      System.exit(-1);
    }

    String name = args[0];

    if (args.length > 1) {
      host = args[1];
    }
    if (args.length > 2) {
      port = Integer.parseInt(args[2]);
    }

    try {   

      for (int ml = 1; ml <= 100000; ml *= 10) {
        TestClient client2 = new TestClient(name, host, port);
        client2.run(ml, 1000);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
