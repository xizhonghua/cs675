package org.xiaohuahua.game.common;

import java.io.Serializable;

public abstract class GameObject implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  protected int x;
  protected int y;
  protected int value;

  protected GameObject() {
    this(0, 0);
  }

  protected GameObject(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getValue() {
    return this.value;
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

  public void setValue(int value) {
    this.value = value;
  }
}
