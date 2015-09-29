package org.xiaohuahua.can;

import java.awt.Point;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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

  public NodeImpl(String peerId, String ip, Bootstrap bootstrap)
      throws RemoteException {
    super();
    this.peerId = peerId;
    this.ip = ip;
    this.zone = new Zone(0, 0, Config.ZONE_SIZE, Config.ZONE_SIZE);
    this.bootstrap = bootstrap;
    this.random = new Random(new Date().getTime());
    this.joined = false;
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
      Map<String, String> nodes = this.bootstrap.getNodeList();

      if (nodes.size() == 0) {
        System.out.println("[NodeServer] 1st node in CAN!");
        this.bootstrap.join(this.peerId, this.ip);

        this.joined = true;
      }

      else {
        Point pt = new Point(random.nextInt(Config.ZONE_SIZE),
            random.nextInt(Config.ZONE_SIZE));

        // TODO(zxi) join CAN using other nodes
        for (String peerId : nodes.keySet()) {
          String ip = nodes.get(peerId);

          Node node = this.getNode(ip, peerId);
          if (node == null)
            continue;

          // node.

        }
      }

    } catch (Exception e) {
      System.out.println("failed to join. Error: " + e);
    }

    System.out.println("[NodeServer] joined CAN!");

    return joined;
  }

  public boolean leave() {

    // Step 1: notify bootstrap node
    try {
      bootstrap.leave(this.peerId);
    } catch (Exception e) {
      System.out.println("Failed to leave CAN. Error: " + e);
    }

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
          break;
        case "search":
          break;
        case "vide":
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
  public JoinResult canJoin(String peerId, String ip, Point point)
      throws RemoteException {

    if (this.containsPoint(point)) {

      if (this.zone.contains(point)) {

        // TODO(zxi) update neighbor info...

        Zone newZone = this.zone.split();

        JoinResult result = new JoinResult(this.ip, newZone);

        return result;
      } else {
        // TODO(zxi) migrate zone
        return null;
      }
    } else {
      Node n = this.getNearestNeighbor(point);
      JoinResult result = n.canJoin(peerId, ip, point);
      result.prependRoute(this.ip);

      return result;
    }
  }

  @Override
  public Neighbor asNeighbor() {
    return new Neighbor(this.peerId, this.ip, this.zone);
  }

  @Override
  public SearchResult canSearch(String key) throws RemoteException {
    Point point = HashUtil.getCoordinate(key);
    if (this.containsPoint(point)) {
      List<String> files = this.getFiles(key);
      SearchResult reslt = new SearchResult(this.ip, key, files);
      return reslt;
    } else {
      Node n = this.getNearestNeighbor(point);
      SearchResult result = n.canSearch(key);
      result.prependRoute(this.ip);
      return result;
    }
  }

  @Override
  public ResultBase canInsert(String key, String content)
      throws RemoteException {
    Point point = HashUtil.getCoordinate(key);
    if (this.containsPoint(point)) {
      this.insertFile(key, content);
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

  private void insertFile(String key, String content) {
    Point p = HashUtil.getCoordinate(key);

    if (this.zone.contains(p)) {
      this.zone.insertFile(key, content);
    }

    // TODO(zxi) check temp zones...

  }

  private static void printHelp() {
    // TODO(zxi) print help
    System.err.println("Commands:");
    System.err.println("insert keyword peer");
    System.err.println("\tInsert keyword to CAN from peer");
  }

}
