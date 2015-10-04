package org.xiaohuahua.can;

import java.io.Serializable;

public class InsertResult extends ResultBase implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String key;
  private String content;

  public InsertResult(String peerId, String ip, String key, String content) {
    super(peerId, ip);
    this.key = key;
    this.content = content;
  }

  public String getKey() {
    return this.key;
  }

  public String getContent() {
    return this.content;
  }

}
