package org.xiaohuahua.can;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

import org.xiaohuahua.can.util.HashUtil;

public class Zone extends Rectangle {

  private static final long serialVersionUID = 1L;

  public Zone() {
    this(0, 0, 0, 0);
  }

  public Zone(int x, int y, int width, int height) {
    super(x, y, width, height);
    
    // this.files = new HashSet<>();
    this.neighbors = new HashSet<>();
  }


  /**
   * Check whether the zone contains the given point
   */
  public boolean contains(Point point) {
    return (point.x >= this.x && point.y >= this.y
        && point.x < this.x + this.width && point.y < this.y + this.height);
  }
  
//  public boolean contains(String keyword) {
//    return this.files.contains(keyword);
//  }

  /**
   * Split the zone
   * 
   * @return the split zone
   */
  public Zone split() {
    
    // Split zone
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
    
//    // Split files        
//    for(String file : this.files)
//    {
//      Point coord = HashUtil.getCoordinate(file);
//      if(newZone.contains(coord)) {
//        newZone.files.add(file);        
//      }     
//    }
//    
//    for(String file : newZone.files) {
//      this.files.remove(file);
//    }
    
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
    
//    // Merge files
//    this.files.addAll(zone.files);
    
    // Merge neighbors
    this.neighbors.addAll(zone.neighbors);
  }
  
//  public void addFile(String keyword) {
//    this.files.add(keyword);
//  }
  
  // private Set<String> files;
  private Set<Rectangle> neighbors;
}
