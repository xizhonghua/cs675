package org.xiaohuahua.can;

import java.awt.Point;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.xiaohuahua.can.util.HashUtil;

public class NodeImpl extends UnicastRemoteObject implements Node {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String peerId;

  private String ip;

  /**
   * Zone owned by current Node
   */
  private Zone zone;

  private Bootstrap bootstrap;

  public NodeImpl(String peerId, String ip, Bootstrap bootstrap)
      throws RemoteException {
    super();
    this.peerId = peerId;
    this.ip = ip;
    this.zone = new Zone(0, 0, Config.LENGTH, Config.LENGTH);
    this.bootstrap = bootstrap;
  }

  public boolean join() {
    try {
      Map<String, String> nodes = this.bootstrap.getNodeList();

      if (nodes.size() == 0) {
        System.out.println("1st node in CAN!");
        this.bootstrap.join(this.peerId, this.ip);
      }

      else {
        // TODO(zxi) join CAN using other nodes
        for (String peerId : nodes.keySet()) {
          String ip = nodes.get(peerId);

        }
      }

    } catch (Exception e) {
      System.out.println("failed to join. Error: " + e);
    }

    return true;
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
   * Insert a file into CAN from current node
   * 
   * @param keyword
   *          keyword of the file to be inserted
   */
  public void insert(String keyword) {
    Point p = HashUtil.getCoordinate(keyword);

    if (this.containsPoint(p)) {
      // this.zone.addFile(keyword);
      // TODO(zxi)
    } else {
      List<NodeImpl> nodes = this.getRoute(p);
      NodeImpl targetNode = nodes.get(nodes.size() - 1);
      // call PRC
    }
  }

  /**
   * Search the file by keyword
   * 
   * @param keyword
   */
  public void search(String keyword) {
    if (this.containsFile(keyword)) {
      // return it self
    }
  }

  /**
   * Check whether current node stores the file with given keywords
   * 
   * @param keyword
   * @return
   */
  @Override
  public Boolean containsFile(String keyword) {
    // return this.zone.contains(keyword);
    return false;
  }

  /**
   * Check whether the zone of current node contains a given point
   * 
   * @param target
   * @return
   */
  @Override
  public Boolean containsPoint(Point target) {
    return (target.x >= this.zone.x && target.y >= this.zone.y
        && target.x < this.zone.x + this.zone.width
        && target.y < this.zone.y + this.zone.height);
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

  /**
   * Split the zone owned by the node. Update the zone.
   * 
   * @return the split zone
   */
  public Zone splitZone() {
    Zone newZone = this.zone.split();
    return newZone;
  }

  /**
   * Get the route from current node to the node that contains the target point
   * 
   * @param target
   * @return
   */
  private List<NodeImpl> getRoute(Point target) {
    List<NodeImpl> nodes = new ArrayList<>();

    return nodes;
  }

  private static void printHelp() {
    // TODO(zxi) print help
    System.err.println("Commands:");
    System.err.println("insert keyword peer");
    System.err.println("\tInsert keyword to CAN from peer");
  }

}
