package org.xiaohuahua.game.common;

import java.util.UUID;

import org.xiaohuahua.game.socket.Message;

public class StrUtil {
  public static String getRandomStr(int minLen) {
    StringBuffer sb = new StringBuffer(minLen);
    sb.append(Message.ECHO + " ");
    while (sb.length() < minLen) {
      String uuid = UUID.randomUUID().toString();
      sb.append(uuid);
    }

    return sb.toString();
  }
}
