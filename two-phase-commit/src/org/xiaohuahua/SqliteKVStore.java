package org.xiaohuahua;

import java.sql.Connection;
import java.sql.DriverManager;
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
      String sql = "INSERT INTO [" + tableName + "]"
          + "(KEY, VALUE) VALUES (" + key + "," + value + ")";
      stmt.executeUpdate(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void del(String key) {
    // TODO Auto-generated method stub

  }

  @Override
  public String get(String key) {
    // TODO Auto-generated method stub
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
          + "KEY TEXT PRIMARY KEY NOT NULL, VALUE TEXT NOT NULL)";
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
