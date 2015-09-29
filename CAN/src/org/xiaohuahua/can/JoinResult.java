package org.xiaohuahua.can;

import java.io.Serializable;
import java.util.List;

public class JoinResult extends ResultBase implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Zone newZone;
  private List<Neighbor> newNeighbors;

  protected JoinResult(String peerId, String ip, Zone newZone,
      List<Neighbor> newNeighbors) {
    super(peerId, ip);

    this.newZone = newZone;
    this.newNeighbors = newNeighbors;
  }

  public Zone getNewZone() {
    return this.newZone;
  }

  public List<Neighbor> getNewNeighbors() {
    return this.newNeighbors;
  }

}
