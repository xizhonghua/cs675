package org.xiaohuahua.game.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;

import org.xiaohuahua.game.common.Config;
import org.xiaohuahua.game.common.GameObject;
import org.xiaohuahua.game.common.GameWorld;
import org.xiaohuahua.game.common.IClient;
import org.xiaohuahua.game.common.Player;
import org.xiaohuahua.game.common.Scene;

public class Client implements IClient {

  private Scene scene;
  private String name;
  private String token;
  private GameWorld world;
  private Player player;
  private RemoteServer server;

  public Client(String name, RemoteServer server) {
    this.name = name;
    this.server = server;
    this.scene = new Scene(this);
  }

  public void run() throws RemoteException {
    // step 1, enter the game
    this.token = this.server.enterGame(this.name);
    System.out.println("[Client] Entered game!");

    this.update();
    this.render();
  }

  public void render() {
    this.scene.repaint();
    this.scene.setTitle(this.player.toString());
  }

  public void move(int dx, int dy) throws RemoteException {
    boolean moved = this.server.move(token, dx, dy);
    this.update();

    if (moved) {
      System.out.println("[Client] Moved to " + this.player.getX() + " , "
          + this.player.getY());
    }
  }

  public void openChest() throws RemoteException {
    int value = this.server.open(token);
    this.update();

    if (value > 0) {
      System.out.println("[Client] Opened Chest! Awarded " + value + "!");
    }
  }

  public void leave() throws RemoteException {
    this.server.leaveGame(token);
    System.out.println("[Client] Left game!");
    System.exit(0);
  }

  private void update() throws RemoteException {
    List<GameObject> objects = this.server.getObjects();

    // init world
    this.world = new GameWorld();
    this.world.initWithObjects(objects);
    // get myself
    player = this.world.getPlayerByName(this.name);
  }

  @Override
  public GameWorld getWorld() {
    return this.world;
  }

  @Override
  public Player getMe() {
    return this.player;
  }

  public static void main(String[] args) throws RemoteException {

    if (args.length < 1) {
      System.out.print(
          "Usage: java " + Client.class.getName() + " playerName [server]");
    }

    String name = args[0];
    String serverName = args.length > 1 ? args[1] : "localhost";
    String fullServiceName = "rmi://" + serverName + "/"
        + Config.GAME_SERVICE_NAME;
    RemoteServer server = null;

    try {
      server = (RemoteServer) Naming.lookup(fullServiceName);
    } catch (Exception e) {
      System.out.print("Failed to find game service at " + fullServiceName);
      e.printStackTrace();
    }

    Client client = new Client(name, server);
    client.run();
  }

}
