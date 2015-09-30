package org.xiaohuahua.can;

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

public abstract class ResultBase implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private List<SimpleEntry<String, String>> routes;

  // Last peer that handle the request
  private String peerId;

  protected ResultBase(String peerId, String ip) {
    this.peerId = peerId;
    this.routes = new ArrayList<>();
    routes.add(new SimpleEntry<String, String>(peerId, ip));
  }

  public String getPeerId() {
    return this.peerId;
  }

  public List<SimpleEntry<String, String>> getRoutes() {
    return this.routes;
  }

  public void prependRoute(String peerId, String ip) {
    this.routes.add(new SimpleEntry<String, String>(peerId, ip));
  }
}
