package org.xiaohuahua.can;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class ResultBase implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  // private String reqPeerId;
  // private String reqIp;
  private List<String> routes;

  protected ResultBase(String resIp) {
    // String reqPeerId, String reqIp,
    // this.reqPeerId = reqPeerId;
    // this.reqIp = reqIp;
    this.routes = new ArrayList<>();
    routes.add(resIp);
  }

  // public String getReqPeerId() {
  // return this.reqPeerId;
  // }
  //
  // public String getReqIp() {
  // return this.reqIp;
  // }

  public List<String> getRoutes() {
    return this.routes;
  }

  public void prependRoute(String ip) {
    this.routes.add(0, ip);
  }
}
