package org.xiaohuahua;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteKVStore implements KVStore {

  public static final String tableName = "STORE";

  private String dbPath;

  public SqliteKVStore() {

  }

  @Override
  public boolean open(String path) {
    this.dbPath = path;

    try (Connection conn = this.openConnection()) {
      if (conn == null)
        return false;

      this.createTable();

      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return false;
  }

  @Override
  public void put(String key, String value) {

    System.out.println("PUT( " + key + ", " + value + " )");

    try (Connection conn = this.openConnection();
        Statement stmt = conn.createStatement()) {
      String sql = "INSERT OR REPLACE INTO [" + tableName + "](K, V) VALUES('"
          + key + "','" + value + "')";
      stmt.executeUpdate(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void del(String key) {

    System.out.println("DELETE( " + key + " )");

    try (Connection conn = this.openConnection();
        Statement stmt = conn.createStatement()) {
      String sql = "DELETE FROM [" + tableName + "] WHERE K='" + key + "'";
      stmt.executeUpdate(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String get(String key) {
    String value = null;
    try (Connection conn = this.openConnection();
        Statement stmt = conn.createStatement()) {
      String sql = "SELECT V FROM [" + tableName + "] WHERE K='" + key + "'";
      ResultSet rs = stmt.executeQuery(sql);
      if (rs.next())
        value = rs.getString(1);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    System.out.println("GET( " + key + " ) = " + value);

    return value;
  }

  private Connection openConnection() {
    try {
      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager
          .getConnection("jdbc:sqlite:" + this.dbPath);
      return conn;
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return null;
    }
  }

  private void createTable() {
    try (Connection conn = this.openConnection();
        Statement stmt = conn.createStatement()) {
      String sql = "CREATE TABLE IF NOT EXISTS [" + tableName + "] ("
          + "K TEXT PRIMARY KEY NOT NULL, V TEXT NOT NULL)";
      stmt.executeUpdate(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void main(String args[]) {
    KVStore store = new SqliteKVStore();
    if (!store.open("store.db")) {
      System.exit(0);
    }

    store.put("hello", "world");
    store.get("hello");
    store.put("hello", "huahua");
    store.get("hello");
    store.del("hello");
    store.get("hello");
  }

}
