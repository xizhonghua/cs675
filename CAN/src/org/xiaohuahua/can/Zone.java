package org.xiaohuahua.can;

import java.awt.Rectangle;

public class Zone extends Rectangle {

  private static final long serialVersionUID = 1L;

  public Zone() {
    this(0, 0, 0, 0);
  }

  public Zone(int x, int y, int width, int height) {
    super(x, y, width, height);
  }

  /**
   * Split the zone
   * @return the split zone
   */
  public Zone split() {

    int w = this.width;
    int h = this.height;

    Zone newZone = new Zone();

    if (w == h) {
      // The zone is square, split it vertically
      int sw = w / 2;
      newZone.setBounds(this.x + w - sw, this.y, sw, h);
      this.setSize(w - sw, h);
    } else {
      // The zone is rectangle, split it horizontally
      int sh = h / 2;
      newZone.setBounds(this.x, this.y + h - sh, w, sh);
      this.setSize(w, h - sh);
    }

    return newZone;
  }

  /**
   * Merge the given zone to this zone.
   * 
   * @param zone
   *          the zone to be merged
   * @throws IllegalArgumentException
   *           if given zone is not mergeable
   */
  public void merge(Zone zone) {

    if (!this.getSize().equals(zone.getSize()))
      throw new IllegalArgumentException(
          "Can not merge zones with different sizes.");

    if ((this.x != zone.x && this.y != zone.y)
        || (this.x == zone.x && this.y + this.height != zone.y
            && zone.y + zone.height != this.y)
        || (this.y == zone.y && this.x + this.width != zone.x
            && zone.x + zone.width != this.x))
      throw new IllegalArgumentException("Can not merge nonadjacent zones.");

    this.setBounds(this.union(zone));
  }
}
