package org.xiaohuahua;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public enum TransactionType {
    PUT, DEL
  }

  private static long TransactionId = new Date().getTime();

  private static synchronized long getTransactionId() {
    return ++TransactionId;
  }

  private TransactionType type;
  private long Id;
  private String key;
  private String value;

  public Transaction(TransactionType type, String key, String value) {
    this.Id = getTransactionId();
    this.type = type;
    this.key = key;
    this.value = value;
  }

  public TransactionType getType() {
    return this.type;
  }

  public long getId() {
    return this.Id;
  }

  public String getKey() {
    return this.key;
  }

  public String getValue() {
    return this.value;
  }

  @Override
  public String toString() {
    return String.format("{Id = %d Type = %s Key = %s Value = %s}", this.Id,
        this.type, this.key, this.value);
  }

  public String toJSON() {
    return Util.toJSON(this);
  }

  public static Transaction fromJSON(String json) {
    return Util.fromJSON(json, Transaction.class);
  }
}
