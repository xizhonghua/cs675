package org.xiaohuahua.game.common;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.xiaohuahua.game.socket.Server;

public class GameMap implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private int[][] score;
  private List<Player> players;
  private List<Chest> chests;

  public GameMap() {
    this.score = new int[Config.MAP_HEIGHT][Config.MAP_WIDTH];
    this.players = new ArrayList<>();
    this.chests = new ArrayList<>();
  }

  public List<GameObject> getObjects() {
    List<GameObject> list = new ArrayList<>();
    list.addAll(this.players);

    return list;
  }

  public int getScore(int x, int y) {
    return this.score[y][x];
  }

  public int setScore(int x, int y, int score) {
    return this.score[y][x] = score;
  }

  public Player getPlayer(int x, int y) {
    for (Player p : this.players)
      if (p.getX() == x && p.getY() == y)
        return p;
    return null;
  }

  public boolean hasPlayer(int x, int y) {
    return getPlayer(x, y) == null;
  }

  public List<Player> getPlayers() {
    return this.players;
  }

  public List<Chest> getChests() {
    return this.chests;
  }

  public void addPlayer(Player p) {
    this.players.add(p);
  }

  public void updatePlayer(Player p) {
    for (int i = 0; i < this.players.size(); ++i) {
      if (players.get(i).getName().equals(p.getName())) {
        players.set(i, p);
        return;
      }
    }
  }

  public void removePlayer(String name) {
    for (Player p : this.players) {
      if (p.getName().equals(name)) {
        this.players.remove(p);
        return;
      }
    }
  }

  public void removeChest(Point location) {
    Chest c = this.getChestByLocation(location);
    if (c == null)
      return;
    this.chests.remove(c);
  }

  /**
   * Try to open chest at given location
   * 
   * @param location
   * @return
   */

  public int openChest(Point location) {
    synchronized (this.chests) {
      Chest c = this.getChestByLocation(location);
      if (c == null)
        return 0;

      this.chests.remove(c);
      return c.getValue();
    }
  }

  private Chest getChestByLocation(Point location) {
    for (Chest c : this.chests) {
      if (c.getLocation().equals(location))
        return c;
    }
    return null;
  }

  public static GameMap generateRandomMap() {
    GameMap map = new GameMap();
    Random r = new Random(new Date().getTime());

    for (int i = 0; i < Config.MAP_HEIGHT; ++i)
      for (int j = 0; j < Config.MAP_WIDTH; ++j) {
        if (r.nextDouble() < Config.GEM_PROB) {
          int value = Config.SCORE_BASE
              * (r.nextInt(Config.MAX_SCORE - Config.MIN_SCORE)
                  + Config.MIN_SCORE);
          Chest c = new Chest(j, i, value);
          map.chests.add(c);
        }
      }

    return map;
  }
}
