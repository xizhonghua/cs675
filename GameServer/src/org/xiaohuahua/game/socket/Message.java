package org.xiaohuahua.game.socket;

import java.awt.Point;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import org.xiaohuahua.game.common.SerializationUtil;

public class Message {

  public static final String ECHO = "ECHO";

  public static final String REQ_NAME = "REQ_NAME";

  public static final String SET_NAME = "SET_NAME";
  public static final String SET_POS = "SET_POS";

  /**
   * Set player's score
   */
  public static final String SET_SCORE = "SET_SCORE";
  public static final String SET_MAP = "SET_MAP";

  public static final String REQ_MOVE = "REQ_MOVE";
  public static final String REQ_OPEN = "REQ_OPEN";

  public static final String REQ_LEAVE = "REQ_LEAVE";

  public static final String UPDATE_PLAYER = "UPDATE_PLAYER";
  public static final String ADD_PLAYER = "ADD_PLAYER";
  public static final String REMOVE_PLAYER = "REMOVE_PLAYER";

  /**
   * parameter x,y remove chest on x,y
   */
  public static final String REMOVE_CHEST = "REMOVE_CHEST";

  /**
   * Send the message out
   * 
   * @param out
   * @param tag
   * @param body
   */
  public static void send(PrintWriter out, String tag, Serializable body) {
    try {
      out.println(tag + " " + SerializationUtil.toString(body));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Object parseMessage(String line) {
    int index = line.indexOf(" ");
    if (index >= 0 && index < line.length()) {
      String body = line.substring(index + 1);
      Object obj = null;
      try {
        obj = SerializationUtil.fromString(body);
      } catch (ClassNotFoundException | IOException e) {
        e.printStackTrace();
      }
      return obj;
    }
    return null;
  }

  public static String getTag(String line) {
    int index = line.indexOf(" ");
    if (index >= 0 && index < line.length()) {
      return line.substring(0, index);
    }

    return null;
  }

  public static Point parsePointMessage(String line) {
    String[] pos = ((String) parseMessage(line)).split(" ");
    int x = Integer.parseInt(pos[0]);
    int y = Integer.parseInt(pos[1]);
    return new Point(x, y);
  }
}
