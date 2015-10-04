package org.xiaohuahua.game.socket;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

import org.xiaohuahua.game.common.Config;
import org.xiaohuahua.game.common.GameWorld;
import org.xiaohuahua.game.common.Player;

public class Server {

  /**
   * The set of all names of clients in the chat room. Maintained so that we can
   * check that new clients are not registering name already in use.
   */
  private static HashSet<String> names = new HashSet<String>();

  /**
   * The set of all the print writers for all the clients. This set is kept so
   * we can easily broadcast messages.
   */
  private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

  private static Random r = new Random(new Date().getTime());

  private static GameWorld map;

  /**
   * A handler thread class. Handlers are spawned from the listening loop and
   * are responsible for a dealing with a single client and broadcasting its
   * messages.
   */
  private static class Handler extends Thread {
    private String name;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private Player player;

    /**
     * Constructs a handler thread, squirreling away the socket. All the
     * interesting work is done in the run method.
     */
    public Handler(Socket socket) {
      this.socket = socket;
    }

    // Send the map to player
    private void sendMap() {
      Message.send(out, Message.SET_MAP, Server.map);
    }

    private void sendPos() {
      Message.send(out, Message.SET_POS,
          this.player.getX() + " " + this.player.getY());
    }

    private boolean handlePlayerEnter() throws IOException {
      player = new Player();

      // Request a name from this client. Keep requesting until
      // a name is submitted that is not already used. Note that
      // checking for the existence of a name and adding the name
      // must be done while locking the set of names.
      while (true) {
        out.println(Message.REQ_NAME);
        name = in.readLine();
        if (name == null) {
          return false;
        }
        synchronized (names) {
          if (!names.contains(name)) {
            names.add(name);
            player.setName(name);
            System.out.print("Player " + name + " entered scene!");
            break;
          }
        }
      }

      player.setX(r.nextInt(Config.MAP_WIDTH));
      player.setY(r.nextInt(Config.MAP_HEIGHT));

      // Add player to the list
      Server.map.addPlayer(player);

      this.broadCast(Message.ADD_PLAYER, player);

      // Add player's out to list
      writers.add(out);

      return true;
    }

    /**
     * Send messages to all clinets
     * 
     * @param tag
     * @param body
     */
    private void broadCast(String tag, Serializable body) {

      for (PrintWriter writer : writers) {
        Message.send(writer, tag, body);
      }
    }

    /**
     * Services this thread's client by repeatedly requesting a screen name
     * until a unique one has been submitted, then acknowledges the name and
     * registers the output stream for the client in a global set, then
     * repeatedly gets inputs and broadcasts them.
     */
    public void run() {
      try {

        // Create character streams for the socket.
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        if (!this.handlePlayerEnter())
          return;

        // send map info to player
        this.sendMap();

        // send player's init position
        this.sendPos();

        // Accept messages from this client and broadcast them.
        // Ignore other clients that cannot be broadcasted to.
        while (true) {
          String input = in.readLine();
          if (input == null) {
            break;
          }

          if (input.startsWith(Message.REQ_MOVE)) {
            Point dp = Message.parsePointMessage(input);
            if (player.move(dp)) {
              this.broadCast(Message.UPDATE_PLAYER, player);
            }
          }

          if (input.startsWith(Message.REQ_PICK_UP)) {
            int x = this.player.getX();
            int y = this.player.getY();
            int value = 0;
            Point loc = new Point(x, y);
            value = Server.map.openChest(loc);
            player.setValue(player.getValue() + value);
            Message.send(out, Message.SET_SCORE, player.getValue());
            this.broadCast(Message.CLEAR_SCORE, x + " " + y);
          }
        }
      } catch (IOException e) {
        System.out.println(e);
      } finally {
        // This client is going down! Remove its name and its print
        // writer from the sets, and close its socket.
        if (name != null) {
          names.remove(name);
        }
        if (out != null) {
          writers.remove(out);
        }

        this.broadCast(Message.REMOVE_PLAYER, this.player);

        try {
          socket.close();
        } catch (IOException e) {
        }
      }
    }
  }

  public static void init() {
    Server.map = GameWorld.generateRandomMap();
  }

  public static void main(String args[]) {
    int port = Config.DEFAULT_PORT;
    if (args.length > 0) {
      port = Integer.parseInt(args[0]);
    }

    try {
      ServerSocket server = new ServerSocket(port);
      System.out.println("Server lintening on port: " + port);
      Server.init();

      try {
        while (true) {
          new Handler(server.accept()).start();
        }
      } finally {
        server.close();
      }

    } catch (IOException e) {
      System.out.println("Failed to listen on port: " + port);
      System.exit(-1);
    }
  }

}
