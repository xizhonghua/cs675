package org.xiaohuahua.can;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

  public Set<String> getKeySet() {
    return this.files.keySet();
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
  @Override
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
   * Split the zone Update self
   * 
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
   * Check whether a given zone can be merged to this zone
   * 
   * @param zone
   *          zone to merge
   * @return
   */
  public boolean canMerge(Zone zone) {

    if (!this.getSize().equals(zone.getSize()))
      return false;

    if ((this.x != zone.x && this.y != zone.y)
        || (this.x == zone.x && this.y + this.height != zone.y
            && zone.y + zone.height != this.y)
        || (this.y == zone.y && this.x + this.width != zone.x
            && zone.x + zone.width != this.x))
      return false;

    return true;
  }

  /**
   * Merge the given zone to this zone. Files managed by the zone will also be
   * merged
   * 
   * @param zone
   *          the zone to be merged
   * @throws IllegalArgumentException
   *           if given zone is not mergeable
   */
  public void merge(Zone zone) {

    if (!this.canMerge(zone))
      throw new IllegalArgumentException("Can not merge nonadjacent zones.");

    // Merge zone
    this.setBounds(this.union(zone));

    // Merge files
    for (String key : zone.files.keySet()) {
      this.files.put(key, zone.files.get(key));
    }
  }

  /**
   * Check whether this is a neighbor of a given zone
   * 
   * @param other
   * @return
   */
  public boolean isNeighbor(Zone other) {

    boolean x_inter = (this.x >= other.x && this.x < other.x + other.width)
        || (other.x >= this.x && other.x < this.x + this.width);

    boolean y_inter = (this.y >= other.y && this.y < other.y + other.height)
        || (other.y >= this.y && other.y < this.y + this.height);

    // this.top == other.bottom
    if (this.y == other.y + other.height && x_inter)
      return true;

    // this.left == other.right
    if (this.x == other.x + other.width && y_inter)
      return true;

    // this.bottom == other.top
    if (this.y + this.height == other.y && x_inter)
      return true;

    // this.right == other.left
    if (this.x + this.width == other.x && y_inter)
      return true;

    return false;
  }

  @Override
  public String toString() {
    return "{ (" + this.x + "," + this.y + "), (" + this.width + ","
        + this.height + ") }";
  }
}
