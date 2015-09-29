package org.xiaohuahua.can;

import java.awt.Point;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.xiaohuahua.can.util.HashUtil;

public class NodeImpl extends UnicastRemoteObject implements Node {

  public static final String NAME = "[Node] ";

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String peerId;

  private String ip;

  private boolean joined;

  /**
   * Zone owned by current Node
   */
  private Zone zone;

  private Bootstrap bootstrap;

  private Random random;

  private List<Neighbor> neighbors;

  public NodeImpl(String peerId, String ip, Bootstrap bootstrap)
      throws RemoteException {
    super();
    this.peerId = peerId;
    this.ip = ip;
    this.zone = new Zone(0, 0, Config.ZONE_SIZE, Config.ZONE_SIZE);
    this.bootstrap = bootstrap;
    this.random = new Random(new Date().getTime());
    this.joined = false;
    this.neighbors = new ArrayList<>();
  }

  public String getId() {
    return this.peerId;
  }

  public String getIP() {
    return this.ip;
  }

  public void setZone(Zone zone) {
    this.zone = (Zone) zone.clone();
  }

  public Zone getZone() {
    return this.zone;
  }

  public boolean join() {

    try {

      //
      Map<String, String> nodes = this.bootstrap.getNodeList(this.peerId,
          this.ip);

      if (nodes.size() == 0) {
        System.out.println(NAME + "1st node in CAN!");
        this.bootstrap.join(this.peerId, this.ip);

        this.joined = true;
      }

      else {
        Point point = new Point(random.nextInt(Config.ZONE_SIZE),
            random.nextInt(Config.ZONE_SIZE));

        // TODO(zxi) join CAN using other nodes
        for (String peerId : nodes.keySet()) {
          String ip = nodes.get(peerId);

          Node node = this.getNode(ip, peerId);
          if (node == null)
            continue;

          JoinResult result = node.joinCAN(this.peerId, this.ip, point);

          this.zone = result.getNewZone();

          this.joined = true;

          break;
        }
      }

    } catch (Exception e) {
      System.out.println(NAME + "Failed to join CAN. Error: " + e);
    }

    if (joined) {
      System.out.println(NAME + "CAN joined!");

      this.view();
    }

    return joined;
  }

  public boolean leave() {

    // Step 1: notify bootstrap node
    try {
      bootstrap.leave(this.peerId);
    } catch (Exception e) {
      System.out.println(NAME + "Failed to leave CAN. Error: " + e);
    }

    System.out.println(NAME + "Left CAN!");

    return true;
  }

  /**
   * Run the node
   */
  public void run() {
    try (Scanner s = new Scanner(System.in)) {
      while (s.hasNextLine()) {
        String[] parameters = s.nextLine().split(" ");
        switch (parameters[0]) {
        case "insert":
          this.hanldeInsert(parameters);
          break;
        case "search":
          this.handleSearch(parameters);
          break;
        case "view":
          this.view();
          break;
        case "join":
          this.join();
          break;
        case "leave":
          this.leave();
          break;
        case "help":
          printHelp();
          break;
        }
      }
    }
  }

  private void hanldeInsert(String[] parameters) {
    if (parameters.length != 3) {
      System.out.println("Usage: insert key content");
    } else {
      String key = parameters[1];
      String content = parameters[2];
      String kv = "{\"" + key + "\":\"" + content + "\"}";

      try {
        InsertResult result = this.insertCAN(key, content);
        System.out.println(NAME + kv + " inserted!");
        this.printPeerAndRoute(result);
      } catch (Exception e) {
        System.out.println(NAME + "Failed to insert" + kv + ". Error: " + e);
      }
    }
  }

  private void handleSearch(String[] parameters) {
    if (parameters.length != 2) {
      System.out.println("Usage: search key");
    } else {
      String key = parameters[1];

      try {
        SearchResult result = this.searchCAN(key);
        System.out.println(NAME + "Search results: matched files = "
            + result.getFiles().size());
        System.out.println("\tkey = \"" + result.getKey() + "\"");
        for (String content : result.getFiles())
          System.out.println("\t\tContent = \"" + content + "\"");
        this.printPeerAndRoute(result);
      } catch (Exception e) {
        System.out.println(NAME + "Failed to search" + key + ". Error: " + e);
      }
    }
  }

  /**
   * Check whether the zones of current node contain a given point
   * 
   * @param point
   * @return
   */
  @Override
  public Boolean containsPoint(Point point) {
    if (this.zone.contains(point))
      return true;
    // TODO(zxi) check temp zone;

    return false;
  }

  /**
   * Split the zone owned by the node. Update the zone.
   * 
   * @return the split zone
   */
  public Zone splitZone() {
    Zone newZone = this.zone.split();
    return newZone;
  }

  /////////////////////////////////////
  // Remote Node's Methods
  ////////////////////////////////////
  @Override
  public double distanceTo(Point point) throws RemoteException {
    double minDist = this.zone.distanceTo(point);

    // TODO (check temp zone)

    return minDist;

  }

  @Override
  public JoinResult joinCAN(String peerId, String ip, Point point)
      throws RemoteException {

    if (this.containsPoint(point)) {

      if (this.zone.contains(point)) {

        // TODO(zxi) update neighbor info...

        Zone newZone = this.zone.split();

        JoinResult result = new JoinResult(this.peerId, this.ip, newZone, null);

        return result;
      } else {
        // TODO(zxi) migrate zone
        return null;
      }
    } else {
      Node n = this.getNearestNeighbor(point);
      JoinResult result = n.joinCAN(peerId, ip, point);
      result.prependRoute(this.ip);

      return result;
    }
  }

  @Override
  public Neighbor asNeighbor() throws RemoteException {
    return new Neighbor(this.peerId, this.ip, this.zone);
  }

  @Override
  public SearchResult searchCAN(String key) throws RemoteException {
    Point point = HashUtil.getCoordinate(key);
    if (this.containsPoint(point)) {
      List<String> files = this.getFiles(key);
      SearchResult reslt = new SearchResult(this.peerId, this.ip, key, files);
      return reslt;
    } else {
      Node n = this.getNearestNeighbor(point);
      SearchResult result = n.searchCAN(key);
      result.prependRoute(this.ip);
      return result;
    }
  }

  @Override
  public InsertResult insertCAN(String key, String content)
      throws RemoteException {
    Point point = HashUtil.getCoordinate(key);
    if (this.containsPoint(point)) {
      this.insertFile(key, content);
      InsertResult result = new InsertResult(this.peerId, this.ip, key,
          content);
      return result;
    } else {

    }
    return null;
  }

  // End of Node's method
  //////////////////////////////////////////////////////////////////////////////

  /**
   * Get the neighbor which is the closest to a given point
   * 
   * @param point
   * @return
   */
  private Node getNearestNeighbor(Point point) {
    return null;
  }

  private Node getNode(String ip, String peerId) {
    String nodeServiceName = Config.NODE_SERVICE_NAME_PREFIX + peerId;
    String uri = "rmi://" + ip + "/" + nodeServiceName;
    try {
      Node node = (Node) Naming.lookup(uri);
      return node;
    } catch (Exception e) {
      System.out.println("[NodeServer] Failed to get remote node " + peerId
          + " @ " + ip + ". Error: " + e);
      return null;
    }
  }

  private List<String> getFiles(String key) {

    Point p = HashUtil.getCoordinate(key);

    if (this.zone.contains(p)) {
      return this.zone.getFiles(key);
    }
    // TODO(zxi) Check temp zones...

    return new ArrayList<>();
  }

  /**
   * Insert the given file to zone/tmp zone
   * 
   * @param key
   * @param content
   */
  private void insertFile(String key, String content) {
    Point p = HashUtil.getCoordinate(key);

    if (this.zone.contains(p)) {
      this.zone.insertFile(key, content);
    }

    // TODO(zxi) check temp zones...
  }

  private void printPeerAndRoute(ResultBase result) {

    System.out.println(NAME + "Peer = " + result.getPeerId() + "@"
        + result.getRoutes().get(0));

    System.out.println(NAME + "Route = " + result.getRoutes());
  }

  // view self
  private void view() {
    System.out.println("--------------------------------");
    System.out.println("View");
    System.out.println("peerId = " + this.peerId);
    System.out.println("ip = " + this.ip);
    System.out.println("Zone = " + this.zone.toString());
    System.out.println("Files = ");
    for (String key : this.zone.getKeySet()) {
      List<String> contents = this.zone.getFiles(key);
      System.out.println("\tKey = \"" + key + "\"");
      for (String content : contents) {
        System.out.println("\t\tContent = \"" + content + "\"");
      }
    }
    System.out.println("--------------------------------");
  }

  private static void printHelp() {
    // TODO(zxi) print help
    System.err.println("Commands:");
    System.err.println("join                   | join CAN");
    System.err
        .println("insert keyword content | insert keyword/content into CAN");
    System.err.println("search keyword         | search by keyword");
    System.err.println("leave                  | leave CAN");
    System.err.println("help                   | print this message");
  }

}
