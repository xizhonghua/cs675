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

      System.out.println("Opened database successfully");
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return false;
  }

  @Override
  public void put(String key, String value) {
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
    try (Connection conn = this.openConnection();
        Statement stmt = conn.createStatement()) {
      String sql = "SELECT V FROM [" + tableName + "] WHERE K='" + key + "'";
      ResultSet rs = stmt.executeQuery(sql);
      if (!rs.next())
        return null;
      String value = rs.getString(1);
      return value;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
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
    System.out.println(store.get("hello"));
    store.put("hello", "huahua");
    System.out.println(store.get("hello"));
    store.del("hello");
    System.out.println(store.get("hello"));
  }

}
