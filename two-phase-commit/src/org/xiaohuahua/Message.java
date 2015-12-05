package org.xiaohuahua;

import java.io.Serializable;

public class Message implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private MessageType type;
  private Transaction transaction;
  private String sender;
  
  public Message(String sender) {
    this(sender, MessageType.ACK);
  }

  public Message(String sender, MessageType type) {
    this.sender = sender;
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
  
  public void setType(MessageType type){
    this.type = type;
  }

  public void setTransaction(Transaction t) {
    this.transaction = t;
  }

  public Transaction getTranscation() {
    return this.transaction;
  }
  
  public String getSender() {
    return this.sender;
  }

  @Override
  public String toString() {
    return String.format("{Sender = %s, Type = %s, Transaction = %s}", this.sender, this.type,
        this.transaction);
  }

}
