package org.xiaohuahua;

public interface KVStore {  
  public boolean open(String path);
  public void put(String key, String value);
  public void del(String key);
  public String get(String key);
}
