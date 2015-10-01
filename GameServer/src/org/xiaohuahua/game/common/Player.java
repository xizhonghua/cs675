package org.xiaohuahua.game.common;

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
  }

  public void setY(int y) {
    this.y = y;
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
