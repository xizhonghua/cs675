package org.xiaohuahua;

import java.io.Serializable;

public class Message implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private MessageType messageType;
  private Transaction transaction;

  public Message(MessageType messageType) {
    this.messageType = messageType;
  }

  @Override
  public boolean equals(Object msg) {
    if (!(msg instanceof Message))
      return false;
    // Message message = (Message) msg;

    // TODO(zxi)

    return false;
  }

  public MessageType getMessageType() {
    return this.messageType;
  }

  public void setTransaction(Transaction t) {
    this.transaction = t;
  }

  public Transaction getTranscation() {
    return this.transaction;
  }

  @Override
  public String toString() {
    return String.format("{Type = %s, Transaction = %s}", this.messageType,
        this.transaction);
  }

}
