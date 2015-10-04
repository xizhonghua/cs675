package org.xiaohuahua.can;

import java.io.Serializable;

public class Neighbor implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String peerId;
  private String ip;
  /**
   * Does not contain any file info
   */
  private Zone zone;

  public Neighbor(String peerId, String ip, Zone zone) {
    this.peerId = peerId;
    this.ip = ip;
    // geometry only
    this.zone = new Zone(zone.x, zone.y, zone.width, zone.height);
  }

  public String getPeerId() {
    return this.peerId;
  }

  public String getIp() {
    return this.ip;
  }

  public String getName() {
    return this.peerId + "@" + this.ip;
  }

  public Zone getZone() {
    return (Zone) this.zone.clone();
  }  

  @Override
  public String toString() {
    return "{ " + this.getName() + ", " + this.zone.toString() + " }";
  }

  @Override
  public int hashCode() {
    return (this.ip + "_" + this.peerId).hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Neighbor) {
      Neighbor n = (Neighbor) obj;
      return n.ip.equals(this.ip) && n.peerId.equals(this.peerId);
    }

    return false;
  }
}
