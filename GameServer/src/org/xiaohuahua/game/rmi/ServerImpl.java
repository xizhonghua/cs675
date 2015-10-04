package org.xiaohuahua.game.rmi;

import java.awt.Point;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.xiaohuahua.game.common.Config;
import org.xiaohuahua.game.common.GameWorld;
import org.xiaohuahua.game.common.GameObject;
import org.xiaohuahua.game.common.Player;

public class ServerImpl extends UnicastRemoteObject implements RemoteServer {

  private Random r;
  private GameWorld world;
  // token to player mapping
  private Map<String, Player> tokens;
  private boolean inited = false;

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  protected ServerImpl() throws RemoteException {
    super();
  }

  public void init() {
    if (inited)
      return;
    this.r = new Random(new Date().getTime());
    this.world = GameWorld.generateRandomMap();
    this.tokens = new HashMap<>();

    inited = true;
  }

  private Player getPlayerByToken(String token) throws RemoteException {
    if (tokens.containsKey(token)) {
      return this.tokens.get(token);
    }

    throw new RemoteException("Invalid token.");
  }

  @Override
  public boolean move(String token, int dx, int dy) throws RemoteException {
    Player p = this.getPlayerByToken(token);
    return p.move(new Point(dx, dy));
  }

  @Override
  public Point getLocation(String token) throws RemoteException {
    Player p = this.getPlayerByToken(token);
    return p.getLocation();
  }

  @Override
  public List<GameObject> getObjects() throws RemoteException {
    return this.world.getObjects();
  }

  @Override
  public String enterGame(String name) throws RemoteException {
    if (this.world.getPlayerByName(name) != null) {
      throw new RemoteException("Player name " + name + " already exists.");
    }

    // Generate a token
    String token = UUID.randomUUID().toString();

    // Create player
    Player player = new Player(name, this.r.nextInt(Config.MAP_WIDTH),
        this.r.nextInt(Config.MAP_HEIGHT));

    // Add player to world
    this.world.addPlayer(player);

    // Create mapping
    this.tokens.put(token, player);

    System.out.println("[RMIServer] Player " + name + " entered!");

    return token;
  }

  @Override
  public boolean leaveGame(String token) throws RemoteException {
    Player p = this.getPlayerByToken(token);
    this.world.removePlayer(p.getName());
    this.tokens.remove(token);

    System.out.println("[RMIServer] Player " + p.getName() + " left!");

    return true;
  }

  @Override
  public int open(String token) throws RemoteException {
    Player p = this.getPlayerByToken(token);
    int value = this.world.openChest(p.getLocation());
    p.setValue(p.getValue() + value);
    return value;
  }

  @Override
  public String echo(String input) throws RemoteException {
    return input;
  }

}
