package org.xiaohuahua.can;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class ResultBase implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private List<String> routes;

  // Last peer that handle the request
  private String peerId;

  protected ResultBase(String peerId, String ip) {
    this.peerId = peerId;
    this.routes = new ArrayList<>();
    routes.add(ip);
  }

  public String getPeerId() {
    return this.peerId;
  }

  public List<String> getRoutes() {
    return this.routes;
  }

  public void prependRoute(String ip) {
    this.routes.add(0, ip);
  }
}
