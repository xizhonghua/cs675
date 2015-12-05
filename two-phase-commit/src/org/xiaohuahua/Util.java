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
  /** Read the object from Base64 string. */
  @SuppressWarnings("unchecked")
  public static <T> T fromString(String s) {
    try {
      byte[] data = Base64.getDecoder().decode(s);
      ObjectInputStream ois = new ObjectInputStream(
          new ByteArrayInputStream(data));
      Object o = ois.readObject();
      ois.close();
      return (T) o;
    } catch (Exception e) {
      return null;
    }

  }

  /** Write the object to a Base64 string. */
  public static String toString(Serializable o) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(o);
      oos.close();
      return Base64.getEncoder().encodeToString(baos.toByteArray());
    } catch (Exception e) {
      return null;
    }
  }

  public static String toJSON(Object o) {
    Gson g = new Gson();
    String json = g.toJson(o);
    return json;
  }
  
  public static <T> T fromJSON(String json, Class<T> clazz) {
    return new Gson().fromJson(json, clazz);
  }
}
