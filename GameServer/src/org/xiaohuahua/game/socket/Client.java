package org.xiaohuahua.game.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.xiaohuahua.game.common.Config;
import org.xiaohuahua.game.common.Player;

public class Client {

  private BufferedReader in;
  private PrintWriter out;
  private Socket socket;
  private Player player;

  public Client(String host, int port)
      throws UnknownHostException, IOException {
    this.socket = new Socket("localhost", port);
  }

  public void run() throws IOException {
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new PrintWriter(socket.getOutputStream(), true);

    // Process all messages from server, according to the protocol.
    while (true) {
      String line = in.readLine();
      System.out.println("[From Server]" + line);
      if (line.startsWith(Message.REQUEST_NAME)) {
        out.println("huahua");
      }
    }
  }

  public static void main(String args[]) {
    int port = Config.DEFAULT_PORT;
    String host = "localhost";
    if (args.length > 1) {
      host = args[0];
    }
    if (args.length > 2) {
      port = Integer.parseInt(args[1]);
    }

    try {
      Client client = new Client(host, port);
      client.run();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
