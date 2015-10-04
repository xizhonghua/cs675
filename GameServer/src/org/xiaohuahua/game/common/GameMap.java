package org.xiaohuahua.game.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameMap implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private int[][] score;
  private List<Player> players;

  public GameMap() {
    this.score = new int[Config.MAP_HEIGHT][Config.MAP_WIDTH];
    this.players = new ArrayList<>();
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
}
