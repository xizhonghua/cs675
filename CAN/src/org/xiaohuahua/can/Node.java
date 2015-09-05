package org.xiaohuahua.can;

import java.awt.Point;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.xiaohuahua.can.util.HashUtil;

public class Node implements RemoteNode {

  public Node(String id) {
    this.id = id;
    this.neighbors = new ArrayList<String>();
    this.zone = new Zone(0, 0, Config.LENGTH, Config.LENGTH);
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
          break;
        case "leave":
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
      this.files.add(keyword);
    } else {
      List<Node> nodes = this.getRoute(p);
      Node targetNode = nodes.get(nodes.size() - 1);
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
    return this.files.contains(keyword);
  }

  /**
   * Check whether the zone of current node contains a given point
   * 
   * @param target
   * @return
   */
  @Override
  public Boolean containsPoint(Point target) {
    // TODO reimplement
    return this.zone.contains(target);
  }

  public String getId() {
    return this.id;
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
  private List<Node> getRoute(Point target) {
    List<Node> nodes = new ArrayList<>();

    return nodes;
  }

  private String id;

  private String ip;

  /**
   * Zone owned by current Node
   */
  private Zone zone;

  /**
   * Cached neighbor objects
   */
  private List<String> neighbors;

  /**
   * Files(keywords) managed by the node
   */
  private Set<String> files;

  private static void printHelp() {
    // TODO(zxi) print help
    System.err.println("Commands:");
    System.err.println("insert keyword peer");
    System.err.println("\tInsert keyword to CAN from peer");
  }

  public static void main(String[] args) {
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new SecurityManager());
    }
    try {
      String name = "mynode";
      Node node = new Node(name);
      RemoteNode stub = (RemoteNode) UnicastRemoteObject.exportObject(node, 0);
      Registry registry = LocateRegistry.getRegistry();
      registry.rebind(name, stub);
      System.out.println("Node bound");
    } catch (Exception e) {
      System.err.println("Node exception:");
      e.printStackTrace();
    }
  }

}
