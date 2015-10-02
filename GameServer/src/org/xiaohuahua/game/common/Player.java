package org.xiaohuahua.game.common;

import java.awt.Point;
import java.io.IOException;
import java.io.Serializable;

public class Player implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String name;
  private int x;
  private int y;
  private int score;

  public Player() {
    this("Unknown", 0, 0);
  }

  public Player(String name, int x, int y) {
    this.name = name;
    this.x = x;
    this.y = y;
    this.score = 0;
  }

  public String getName() {
    return this.name;
  }

  public int getX() {
    return this.x;
  }

  public int getY() {
    return this.y;
  }

  public int getScore() {
    return this.score;
  }

  public void setX(int x) {
    this.x = x;
    if (this.x >= Config.MAP_WIDTH)
      this.x = Config.MAP_WIDTH - 1;
    if (this.x < 0)
      this.x = 0;
  }

  public void setY(int y) {
    this.y = y;

    if (this.y >= Config.MAP_HEIGHT)
      this.y = Config.MAP_HEIGHT - 1;
    if (this.y < 0)
      this.y = 0;
  }

  /**
   * move player
   * @param dp vector to move
   * @return true if moved
   */
  public boolean move(Point dp) {
    int tx = x;
    int ty = y;
    this.setX(this.x + dp.x);
    this.setY(this.y + dp.y);
    if (tx == x && ty == y)
      return false;
    return true;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "{ x:" + x + ", y:" + y + ", name:\"" + name + "\", score:" + score
        + " }";
  }

  public String toBase64String() {
    try {
      return Base64Util.toString(this);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static Player fromBase64String(String base64String) {
    try {
      Player p = (Player) Base64Util.fromString(base64String);
      return p;
    } catch (Exception e) {
      e.printStackTrace();
      return new Player();
    }
  }

}
