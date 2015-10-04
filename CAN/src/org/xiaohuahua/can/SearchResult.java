package org.xiaohuahua.can;

import java.io.Serializable;
import java.util.List;

public class SearchResult extends ResultBase implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String key;
  private List<String> files;

  public SearchResult(String peerId, String ip, String key,
      List<String> files) {
    super(peerId, ip);
    this.key = key;
    this.files = files;
  }

  public String getKey() {
    return this.key;
  }

  public List<String> getFiles() {
    return this.files;
  }

}
