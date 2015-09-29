package org.xiaohuahua.can;

import java.io.Serializable;

public class JoinResult extends ResultBase implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Zone newZone;

  protected JoinResult(String resIp, Zone newZone) {
    super(resIp);

    this.newZone = newZone;
  }

  public Zone getNewZone() {
    return newZone;
  }

}
