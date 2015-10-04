package org.xiaohuahua.game.common;

import java.io.Serializable;

public class Chest extends GameObject implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public Chest() {
  }

  public Chest(int x, int y) {
    super(x, y);
  }

  public Chest(int x, int y, int value) {
    super(x, y);
    this.value = value;
  }

}
