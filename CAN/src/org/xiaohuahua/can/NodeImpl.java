package org.xiaohuahua.can;

import java.awt.Point;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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

  private String id;

  private String ip;

  /**
   * Zone owned by current Node
   */
  private Zone zone;

  private Bootstrap bootstrap;

  public NodeImpl(String id, Bootstrap bootstrap) throws RemoteException {
    super();
    this.id = id;
    this.zone = new Zone(0, 0, Config.LENGTH, Config.LENGTH);
    this.bootstrap = bootstrap;
  }  

  public boolean join() {
    try {
      Map<String, InetSocketAddress> nodes = this.bootstrap.getNodeList();
      
      if(nodes.size() == 0) {
        System.out.println("1st node in CAN!");
        
        return true;
      }
      
      else
      {
        //TODO(zxi) join CAN using other nodes
        for(String peerId : nodes.keySet()) {        
          InetSocketAddress address = nodes.get(peerId);
          
        }
      }
      
    } catch (Exception e) {
      System.out.println("failed to join. Error: " + e);
    }
    
    return false;
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

  // public static void main(String[] args) {
  // if (System.getSecurityManager() == null) {
  // System.setSecurityManager(new SecurityManager());
  // }
  // try {
  // String name = "mynode";
  // NodeImpl node = new NodeImpl(name);
  // Node stub = (Node) UnicastRemoteObject.exportObject(node, 0);
  // Registry registry = LocateRegistry.getRegistry();
  // registry.rebind(name, stub);
  // System.out.println("Node bound");
  // node.run();
  // } catch (Exception e) {
  // System.err.println("Node exception:");
  // e.printStackTrace();
  // }
  // }

}
