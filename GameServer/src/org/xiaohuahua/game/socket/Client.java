package org.xiaohuahua.game.socket;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

import org.xiaohuahua.game.common.Config;
import org.xiaohuahua.game.common.GameWorld;
import org.xiaohuahua.game.common.IClient;
import org.xiaohuahua.game.common.Player;
import org.xiaohuahua.game.common.Scene;

public class Client implements IClient {

  private BufferedReader in;
  private PrintWriter out;
  private Socket socket;

  private Scene scene;
  private Player player;
  private GameWorld world;
  private String name;

  public Client(String name, String host, int port)
      throws UnknownHostException, IOException {

    this.name = name;
    this.socket = new Socket(host, port);
    this.scene = new Scene(this);
  }

  @Override
  public GameWorld getWorld() {
    return this.world;
  }

  @Override
  public Player getMe() {
    return this.player;
  }

  @Override
  public void leave() throws RemoteException {
    Message.send(out, Message.REQ_LEAVE, "");
    System.out.println("[Client] Left game!");
    System.exit(0);
  }

  @Override
  public void move(int dx, int dy) {
    Message.send(out, Message.REQ_MOVE, dx + " " + dy);
  }

  @Override
  public void openChest() {
    Message.send(out, Message.REQ_OPEN, "");
  }

  public void run() throws IOException {
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new PrintWriter(socket.getOutputStream(), true);

    this.player = new Player(this.name, 0, 0);

    // Process all messages from server, according to the protocol.
    while (true) {
      String line = in.readLine();
      String tag = Message.getTag(line);
      System.out
          .println("[SocketClient] Recv:" + tag + " length = " + line.length());
      if (line.startsWith(Message.REQ_NAME)) {
        out.println(this.player.getName());
      }
      if (line.startsWith(Message.SET_MAP)) {
        this.world = (GameWorld) Message.parseMessage(line);
        this.scene.repaint();
      }
      if (line.startsWith(Message.SET_POS)) {
        Point pt = Message.parsePointMessage(line);
        this.player.setLocation(pt);
      }
      if (line.startsWith(Message.UPDATE_PLAYER)) {
        Player p = (Player) Message.parseMessage(line);
        if (p.getName().equals(this.player.getName())) {
          this.player = p;
        }

        this.world.updatePlayer(p);
      }
      if (line.startsWith(Message.SET_SCORE)) {
        Integer score = (Integer) Message.parseMessage(line);
        this.player.setValue(score);
      }

      if (line.startsWith(Message.REMOVE_CHEST)) {
        Point p = Message.parsePointMessage(line);
        this.world.removeChest(p);
      }

      if (line.startsWith(Message.ADD_PLAYER)) {
        Player p = (Player) Message.parseMessage(line);
        this.world.addPlayer(p);
      }

      if (line.startsWith(Message.REMOVE_PLAYER)) {
        Player p = (Player) Message.parseMessage(line);
        this.world.removePlayer(p.getName());
      }

      this.scene.render();
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
      Client client = new Client(name, host, port);

      client.run();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
