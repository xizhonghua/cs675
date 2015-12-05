package org.xiaohuahua;

import java.io.Serializable;

public class Message implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private MessageType type;
  private Transaction transaction;
  private String senderType;
  private String decision;
  private int senderId;

  public Message(String senderType, int senderId, MessageType type) {
    this.senderType = senderType;
    this.senderId = senderId;
    this.type = type;
  }

  @Override
  public boolean equals(Object msg) {
    if (!(msg instanceof Message))
      return false;
    // Message message = (Message) msg;

    // TODO(zxi)

    return false;
  }

  public MessageType getType() {
    return this.type;
  }

  public void setType(MessageType type) {
    this.type = type;
  }

  public void setTransaction(Transaction t) {
    this.transaction = t;
  }

  public Transaction getTranscation() {
    return this.transaction;
  }

  public String getSenderType() {
    return this.senderType;
  }

  public int getSenderId() {
    return this.senderId;
  }

  public void setDecision(String decision) {
    this.decision = decision;
  }

  public String getDecision() {
    return this.decision;
  }

  @Override
  public String toString() {
    return Util.toJSON(this);
  }

}
