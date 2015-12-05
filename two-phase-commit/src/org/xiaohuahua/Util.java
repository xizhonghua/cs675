package org.xiaohuahua;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Base64;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

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
