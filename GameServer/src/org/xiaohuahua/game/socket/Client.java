package org.xiaohuahua.game.socket;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigestSpi;
import java.util.Date;

import org.xiaohuahua.game.common.Config;
import org.xiaohuahua.game.common.GameMap;
import org.xiaohuahua.game.common.Player;
import org.xiaohuahua.game.common.Scene;

public class Client {

  private Scene scene = new Scene("Game");

  private BufferedReader in;
  private PrintWriter out;
  private Socket socket;
  private Player player;
  private GameMap map;

  public Client(String host, int port)
      throws UnknownHostException, IOException {

    this.socket = new Socket("localhost", port);

    Client self = this;

    scene.addKeyListener(new KeyListener() {

      @Override
      public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

      }

      @Override
      public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
      }

      @Override
      public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
          System.exit(0);
          break;
        case KeyEvent.VK_UP:
          self.move(0, -1);
          break;
        case KeyEvent.VK_DOWN:
          self.move(0, 1);
          break;
        case KeyEvent.VK_LEFT:
          self.move(-1, 0);
          break;
        case KeyEvent.VK_RIGHT:
          self.move(1, 0);
          break;
        case KeyEvent.VK_SPACE:
          self.pickup();
          break;
        }

        self.render();
      }
    });
  }

  private void render() {
    this.scene.repaint();
    this.scene.setTitle(this.player.toString());
  }

  private void updatePos(int x, int y) {
    this.player.setX(x);
    this.player.setY(y);
    System.out.print("[Update Pos]" + this.player);
  }

  private void move(int dx, int dy) {
    Message.send(out, Message.REQ_MOVE, dx + " " + dy);
  }

  private void pickup() {
    Message.send(out, Message.REQ_PICK_UP, "");
  }

  public void run() throws IOException {
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new PrintWriter(socket.getOutputStream(), true);

    this.player = new Player();
    this.player.setName("huahua_" + new Date().getTime());

    // Process all messages from server, according to the protocol.
    while (true) {
      String line = in.readLine();
      System.out.println("[From Server]" + line);
      if (line.startsWith(Message.REQ_NAME)) {
        out.println(this.player.getName());
      }
      if (line.startsWith(Message.SET_MAP)) {
        this.map = (GameMap) Message.parseMessage(line);
        this.scene.setMap(this.map);
        this.scene.setMe(this.player);
        this.scene.repaint();
      }
      if (line.startsWith(Message.SET_POS)) {
        Point pt = Message.parsePointMessage(line);
        this.updatePos(pt.x, pt.y);
      }
      if (line.startsWith(Message.UPDATE_PLAYER)) {
        Player p = (Player) Message.parseMessage(line);
        if (p.getName().equals(this.player.getName())) {
          this.player = p;
          this.scene.setMe(this.player);
        }

        this.map.updatePlayer(p);
      }
      if (line.startsWith(Message.SET_SCORE)) {
        Integer score = (Integer) Message.parseMessage(line);
        this.player.setScore(score);
      }

      if (line.startsWith(Message.CLEAR_SCORE)) {
        Point p = Message.parsePointMessage(line);
        this.map.setScore(p.x, p.y, 0);
      }

      if (line.startsWith(Message.ADD_PLAYER)) {
        Player p = (Player) Message.parseMessage(line);
        this.map.addPlayer(p);
      }

      if (line.startsWith(Message.REMOVE_PLAYER)) {
        Player p = (Player) Message.parseMessage(line);
        this.map.removePlayer(p.getName());
      }

      this.render();
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
