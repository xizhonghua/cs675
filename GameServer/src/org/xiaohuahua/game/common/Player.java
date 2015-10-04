package org.xiaohuahua.game.common;

import java.awt.Point;
import java.io.Serializable;

public class Player extends GameObject implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String name;

  public Player() {
    this("Unknown", 0, 0);
  }

  public Player(String name, int x, int y) {
    super(x, y);
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  /**
   * move player
   * 
   * @param dp
   *          vector to move
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

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "{ x:" + x + ", y:" + y + ", name:\"" + name + "\", score:" + value
        + " }";
  }

}
