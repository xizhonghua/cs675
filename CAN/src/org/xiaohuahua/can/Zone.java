package org.xiaohuahua.can;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xiaohuahua.can.util.HashUtil;

public class Zone extends Rectangle implements Serializable {

  private static final long serialVersionUID = 1L;

  private Map<String, ArrayList<String>> files;

  public Zone() {
    this(0, 0, 0, 0);
  }

  public Zone(int x, int y, int width, int height) {
    super(x, y, width, height);

    this.files = new HashMap<>();
  }

  public boolean insertFile(String key, String content) {
    if (!files.containsKey(key)) {
      files.put(key, new ArrayList<String>());
    }

    files.get(key).add(content);

    return true;
  }

  public List<String> getFiles(String key) {
    if (files.containsKey(key)) {
      return files.get(key);
    }

    return new ArrayList<String>();
  }

  /**
   * Check whether the zone contains the given point
   */
  public boolean contains(Point point) {
    return (point.x >= this.x && point.y >= this.y
        && point.x < this.x + this.width && point.y < this.y + this.height);
  }
  
  

  /**
   * Compute the distance from center of the zone to a given point
   * 
   * @param point
   * @return
   */
  public double distanceTo(Point point) {
    Point center = new Point(this.x + this.width / 2, this.y + this.height / 2);

    return center.distance(point);
  }

  // public boolean contains(String keyword) {
  // return this.files.contains(keyword);
  // }

  /**
   * Split the zone
   * Update self
   * @return the split zone
   */
  public Zone split() {

    // Split zone
    final int w = this.width;
    final int h = this.height;

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

    // Split files
    for (String key : this.files.keySet()) {
      Point coord = HashUtil.getCoordinate(key);
      if (newZone.contains(coord)) {
        newZone.files.put(key, this.files.get(key));
      }
    }

    for (String key : newZone.files.keySet()) {
      this.files.remove(key);
    }

    // Split neighbors

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

    // Merge zone
    this.setBounds(this.union(zone));

    // // Merge files
    // this.files.addAll(zone.files);

    // Merge neighbors
    // this.neighbors.addAll(zone.neighbors);
  }

  // public void addFile(String keyword) {
  // this.files.add(keyword);
  // }
}
