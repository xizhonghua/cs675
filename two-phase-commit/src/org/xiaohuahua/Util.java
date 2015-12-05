package org.xiaohuahua;

import com.google.gson.Gson;

public class Util {

  public static String toJSON(Object o) {
    Gson g = new Gson();
    String json = g.toJson(o);
    return json;
  }
  
  public static <T> T fromJSON(String json, Class<T> clazz) {
    return new Gson().fromJson(json, clazz);
  }
}
