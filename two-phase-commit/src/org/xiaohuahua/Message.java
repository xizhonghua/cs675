package org.xiaohuahua;

import java.io.Serializable;

public class Message implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private MessageType messageType;
  private long transcationId;
  private String key;
  private String value;

  public Message(MessageType messageType, long transcationId, String key,
      String value) {
    this.messageType = messageType;
    this.transcationId = transcationId;
    this.key = key;
    this.value = value;
  }

  @Override
  public boolean equals(Object msg) {
    if (!(msg instanceof Message))
      return false;
    Message message = (Message) msg;

    return message.transcationId == this.transcationId
        && message.messageType == this.messageType
        && message.key.equals(this.key) && message.value.equals(this.value);
  }

  public void copyKVFrom(Message msg) {
    this.key = msg.key;
    this.value = msg.value;
  }

  public MessageType getMessageType() {
    return this.messageType;
  }

  public long getTranscationId() {
    return this.transcationId;
  }

  public String getKey() {
    return this.key;
  }

  public String getValue() {
    return this.value;
  }

}
