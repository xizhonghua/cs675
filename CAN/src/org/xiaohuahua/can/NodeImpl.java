package org.xiaohuahua.can;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.xiaohuahua.can.util.HashUtil;

public class NodeImpl extends UnicastRemoteObject
    implements Node, Bootstrap, Serializable {

  public static final String NAME_NODE = "[Node] ";
  public static final String NAME_BOOTSTRAP = "[Bootstrap] ";

  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_BLACK = "\u001B[30m";
  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_YELLOW = "\u001B[33m";
  public static final String ANSI_BLUE = "\u001B[34m";
  public static final String ANSI_PURPLE = "\u001B[35m";
  public static final String ANSI_CYAN = "\u001B[36m";
  public static final String ANSI_WHITE = "\u001B[37m";

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String host;

  private String peerId;

  private String ip;

  private boolean joined;

  /**
   * Zone owned by current node
   */
  private Zone zone;

  /**
   * Temporary zones owned by current node
   */
  private List<Zone> tempZones;

  private Bootstrap bootstrap;

  private Random random;

  private List<Neighbor> neighbors;

  public NodeImpl(String peerId, String host, String ip, Bootstrap bootstrap)
      throws RemoteException {
    super();

    this.random = new Random(new Date().getTime());

    this.peerId = peerId;
    this.host = host;
    this.ip = ip;

    this.zone = new Zone(0, 0, Config.ZONE_SIZE, Config.ZONE_SIZE);
    this.tempZones = new ArrayList<>();

    this.bootstrap = bootstrap == null ? (Bootstrap) this : bootstrap;

    this.joined = false;
    this.neighbors = new ArrayList<>();
  }

  public String getPeerId() {
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

    if (joined) {
      System.out.println("Error: already joined CAN.");
      return false;
    }

    try {

      if (this.bootstrap == this) {
        System.out.println(NAME_NODE + "1st node in CAN!");
        this.joined = true;
      } else {

        Map<String, String> nodes = this.bootstrap.getNodeList(this.peerId,
            this.ip);

        System.out.println(NAME_NODE + "Got node list " + nodes);

        Point point = new Point(random.nextInt(Config.ZONE_SIZE),
            random.nextInt(Config.ZONE_SIZE));

        for (String peerId : nodes.keySet()) {
          String ip = nodes.get(peerId);

          Node node = this.getNode(ip, peerId);
          if (node == null)
            continue;

          JoinResult result = node.joinCAN(this.peerId, this.ip, point);

          // update zone info and neighbors
          this.zone = result.getNewZone();
          this.neighbors = result.getNewNeighbors();

          this.joined = true;

          this.printPeerAndRoute(result);

          break;
        }
      }

    } catch (Exception e) {
      System.out.println(NAME_NODE + "Failed to join CAN. Error: " + e);
    }

    if (joined) {
      System.out.println(NAME_NODE + "CAN joined!");

      this.view(true);
    }

    return joined;
  }

  /**
   * Migrate a given zone to its neighbor
   * 
   * @param zone
   *          zone to migrate
   * @return true, if migrated, false otherwise
   * @throws RemoteException
   */
  private boolean migrateZone(Zone zone) throws RemoteException {
    // Step 2: update zones/neighbors
    for (Neighbor nb : this.neighbors) {
      boolean canMerge = false;

      // can be directly merged
      if (nb.getZone().canMerge(zone)) {
        canMerge = true;
      }

      // can be merged to a temp zone
      // could this happen?

      // TODO(zxi) check temp zones...

      if (canMerge) {
        // Step 2.1 Merge current zone to a new node
        Node node = this.getNode(nb);
        node.mergeZone(zone);

        // Step 2.2 Notify current neighbors
        for (Neighbor enb : this.neighbors) {

          Node enode = this.getNode(enb);

          // Step 2.2.1 Notify existing neighbors to remove myself
          enode.removeNeighbor(this.asNeighbor());
          // Step 2.2.2 Notify existing neighbors to add new neighbor
          if (!enb.equals(nb))
            enode.addOrUpdateNeighbor(nb);
        }
      }

      break;
    }

    return false;
  }

  public boolean leave() {

    if (!joined) {
      System.out.println("Error: node is not in CAN.");
      return false;
    }

    try {

      System.out.println(NAME_NODE + "Leaving CAN...");

      boolean selfZoneMerged = this.migrateZone(this.zone);

    } catch (Exception e) {
      System.out.println(NAME_NODE + "Failed to leave CAN. Error: " + e);
      e.printStackTrace();
    }

    this.joined = false;
    this.neighbors.clear();

    System.out.println(NAME_NODE + "Left CAN!");

    return true;
  }

  /**
   * Run the shell
   */
  public void run() {
    System.out.print(">>> ");
    try (Scanner s = new Scanner(System.in)) {
      while (s.hasNextLine()) {
        this.handleCommand(s.nextLine());
        System.out.print(">>> ");
      }
    }
  }

  private void handleCommand(String line) {
    String[] parameters = line.split(" ");
    switch (parameters[0]) {
    case "insert":
      this.hanldeInsert(parameters);
      break;
    case "search":
      this.handleSearch(parameters);
      break;
    case "view":
      this.view(true);
      break;
    case "join":
      this.join();
      break;
    case "leave":
      this.leave();
      break;
    case "script":
      this.handleScript(parameters);
      break;
    case "exit":
      if (this.joined)
        this.leave();
      System.exit(0);
      break;
    case "help":
      printHelp();
      break;
    default:
      System.out.println(">>> Unknown command:" + parameters[0]);
      break;
    }
  }

  private void handleScript(String[] parameters) {
    if (parameters.length != 2) {
      System.out.println(">>> Usage: script filename");
      return;
    }

    String filename = parameters[1];

    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String line;
      System.out.println(ANSI_CYAN + "[Scripting] Started!" + ANSI_RESET);
      while ((line = br.readLine()) != null) {
        Thread.sleep(2000);
        System.out.println(ANSI_CYAN + "[Scripting] >>> " + line + ANSI_RESET);
        this.handleCommand(line);
      }
      System.out.println(ANSI_CYAN + "[Scripting] Done!" + ANSI_RESET);

    } catch (FileNotFoundException e) {
      System.out.println("Fil not found for: " + filename + ". Error: " + e);
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("Unknown IO error. Error: " + e);
      e.printStackTrace();
    } catch (InterruptedException e) {
    }

  }

  private void hanldeInsert(String[] parameters) {
    if (parameters.length != 3) {
      System.out.println(">>> Usage: insert key content");
      return;
    }

    String key = parameters[1];
    String content = parameters[2];
    String kv = "{\"" + key + "\":\"" + content + "\"}";

    try {
      InsertResult result = this.insertCAN(key, content);
      System.out.println(NAME_NODE + kv + " inserted!");
      this.printPeerAndRoute(result);
    } catch (Exception e) {
      System.out
          .println(NAME_NODE + "Failed to insert " + kv + ". Error: " + e);
      e.printStackTrace();
    }

  }

  private void handleSearch(String[] parameters) {
    if (parameters.length != 2) {
      System.out.println("Usage: search key");
    } else {
      String key = parameters[1];

      try {
        SearchResult result = this.searchCAN(key);
        System.out.println(NAME_NODE + "Search results: matched files = "
            + result.getFiles().size());
        System.out.println("\tkey = \"" + result.getKey() + "\"");
        for (String content : result.getFiles())
          System.out.println("\t\tContent = \"" + content + "\"");
        this.printPeerAndRoute(result);
      } catch (Exception e) {
        System.out
            .println(NAME_NODE + "Failed to search" + key + ". Error: " + e);
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
  public void mergeZone(Zone zone) throws RemoteException {
    if (this.zone.canMerge(zone)) {
      this.zone.merge(zone);
    } else {
      // check temp zones...
    }

    System.out.println(NAME_NODE + "Zone merged!");
    this.view(false);
  }

  /**
   * update zones/neighbors for a given new zone
   * 
   * @param newZone
   * @return new neighbors
   * @throws RemoteException
   */
  private List<Neighbor> updateZone(String peerId, String ip, Zone newZone)
      throws RemoteException {

    // new neighbor for the new zone
    List<Neighbor> newNeighbors = new ArrayList<>();

    // neighbor to remove for current (main zone U temp zone)
    List<Neighbor> neighborsToRemove = new ArrayList<>();

    for (Neighbor nb : this.neighbors) {
      if (nb.getZone().isNeighbor(newZone))
        newNeighbors.add(nb);
      if (!nb.getZone().isNeighbor(this.zone))
        neighborsToRemove.add(nb);
    }

    // Remove neighbor
    for (Neighbor nb : neighborsToRemove) {
      Node node = this.getNode(nb);
      node.removeNeighbor(this.asNeighbor());
      System.out.println(NAME_NODE + nb.getName() + " notifyed!");
    }

    // Update zone info for neighbors
    for (Neighbor nb : this.neighbors) {
      Node node = this.getNode(nb);
      node.addOrUpdateNeighbor(this.asNeighbor());
      System.out.println(NAME_NODE + nb.getName() + " notifyed!");
    }

    Neighbor neighbor = new Neighbor(peerId, ip, newZone);

    // add each other as neighbor
    this.addOrUpdateNeighbor(neighbor);
    newNeighbors.add(this.asNeighbor());

    // Notify new node neighbor entered
    for (Neighbor nb : newNeighbors) {
      Node node = this.getNode(nb);
      node.addOrUpdateNeighbor(neighbor);
      System.out.println(NAME_NODE + nb.getName() + " notifyed!");
    }

    return newNeighbors;
  }

  @Override
  public JoinResult joinCAN(String peerId, String ip, Point point)
      throws RemoteException {

    if (this.containsPoint(point)) {

      Zone newZone = null;

      // if the target point is in main zone
      if (this.zone.contains(point)) {

        // slip the main zone
        newZone = this.zone.split();

      } else {

        // if the target point is in a temp zone
        for (Zone tempZone : this.tempZones) {

          if (!tempZone.contains(point))
            continue;

          // remove from this
          this.tempZones.remove(tempZone);

          // migrate the entire temp zone
          newZone = tempZone;

          break;
        }
      }

      // update zone info
      List<Neighbor> newNeighbors = this.updateZone(peerId, ip, newZone);

      this.view(false);

      // generate the result
      JoinResult result = new JoinResult(this.peerId, this.ip, newZone,
          newNeighbors);

      return result;

    } else {
      Node n = this.getNearestNeighbor(point);
      JoinResult result = n.joinCAN(peerId, ip, point);
      result.prependRoute(this.peerId, this.ip);

      return result;
    }

  }

  @Override
  public void addOrUpdateNeighbor(Neighbor neighbor) throws RemoteException {
    for (int i = 0; i < this.neighbors.size(); ++i) {
      if (this.neighbors.get(i).equals(neighbor)) {
        // Update
        Neighbor oldNeighbor = this.neighbors.get(i);
        this.neighbors.set(i, neighbor);
        System.out.println(NAME_NODE + "Neighbor" + neighbor.getName()
            + "'s zone updated from " + oldNeighbor.getZone() + " to "
            + neighbor.getZone());
        return;
      }
    }

    this.neighbors.add(neighbor);
    System.out.println(NAME_NODE + "New neighbor added!");
    System.out.println(NAME_NODE + "New neighbor = " + neighbor);
  }

  @Override
  public void removeNeighbor(Neighbor neighbor) throws RemoteException {
    for (int i = 0; i < this.neighbors.size(); ++i) {
      if (this.neighbors.get(i).equals(neighbor)) {
        // Update
        Neighbor oldNeighbor = this.neighbors.get(i);
        this.neighbors.remove(oldNeighbor);
        System.out.println(NAME_NODE + "Neighbor removed");
        System.out.println(NAME_NODE + "Removed neighbor = " + oldNeighbor);
        return;
      }
    }

    throw new RemoteException("Failed to remove uknown neighbor: " + neighbor);

  }

  @Override
  public Neighbor asNeighbor() throws RemoteException {
    return new Neighbor(this.peerId, this.ip, this.zone);
  }

  @Override
  public SearchResult searchCAN(String key) throws RemoteException {
    Point point = HashUtil.getCoordinate(key);
    SearchResult result = null;
    if (this.containsPoint(point)) {
      List<String> files = this.getFiles(key);
      result = new SearchResult(this.peerId, this.ip, key, files);
    } else {
      Node n = this.getNearestNeighbor(point);
      result = n.searchCAN(key);
      result.prependRoute(this.peerId, this.ip);
    }
    return result;
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
      Node n = this.getNearestNeighbor(point);
      InsertResult result = n.insertCAN(key, content);
      result.prependRoute(this.peerId, this.ip);
      return result;
    }
  }

  // End of Node's method
  //////////////////////////////////////////////////////////////////////////////

  /**
   * Get the neighbor which is the closest to a given point
   * 
   * @param point
   * @return
   */
  private Node getNearestNeighbor(Point point) throws RemoteException {
    double bestDist = Double.MAX_VALUE;
    Node bestNode = null;

    for (Neighbor nb : this.neighbors) {
      Node node = this.getNode(nb);
      double dist = node.distanceTo(point);
      if (dist < bestDist) {
        bestNode = node;
        bestDist = dist;
      }
    }

    return bestNode;
  }

  private Node getNode(Neighbor nb) {
    return this.getNode(nb.getIp(), nb.getPeerId());
  }

  private Node getNode(String ip, String peerId) {
    String nodeServiceName = Config.NODE_SERVICE_NAME_PREFIX + peerId;
    String uri = "rmi://" + ip + "/" + nodeServiceName;
    try {
      Node node = (Node) Naming.lookup(uri);
      return node;
    } catch (Exception e) {
      System.out.println(NAME_NODE + "Failed to get remote node " + peerId
          + " @ " + ip + ". Error: " + e);
      e.printStackTrace();
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

    System.out.println(
        NAME_NODE + "new file {\"" + key + "\":\"" + content + "\"} inserted!");
  }

  private void printPeerAndRoute(ResultBase result) {

    System.out.println(NAME_NODE + "Peer = " + result.getPeerId() + "@"
        + result.getRoutes().get(0).getValue());

    System.out.print(NAME_NODE + "Route = [");
    boolean first = true;
    for (SimpleEntry<String, String> kv : result.getRoutes()) {
      if (!first)
        System.out.print(", ");
      System.out.print(kv.getKey() + "@" + kv.getValue());
      first = false;
    }
    System.out.println("]");
  }

  // view self
  private void view(boolean fromShell) {
    System.out.print(ANSI_GREEN);
    System.out.println("--------------------------------------------------");
    System.out.println("| View");
    System.out.println("| peerId    = " + this.peerId);
    System.out.println("| host      = " + this.host);
    System.out.println("| ip        = " + this.ip);
    System.out.println("| Zone      = " + this.zone.toString());
    System.out.println("| Neighbors = ");
    for (Neighbor neighbor : this.neighbors)
      System.out.println("|  " + neighbor);
    System.out.println("| Files     = ");
    for (String key : this.zone.getKeySet()) {
      List<String> contents = this.zone.getFiles(key);
      System.out.println("|  Key = \"" + key + "\"");
      for (String content : contents) {
        System.out.println("|    Content = \"" + content + "\"");
      }
    }
    System.out.println("--------------------------------------------------");
    System.out.print(ANSI_RESET);

    if (!fromShell)
      System.out.print(">>> ");
  }

  private static void printHelp() {
    // TODO(zxi) print help
    System.err.println("Commands:");
    System.err.println("join                   | join CAN");
    System.err.println("view                   | view node info");
    System.err
        .println("insert keyword content | insert keyword/content into CAN");
    System.err.println("search keyword         | search by keyword");
    System.err.println("leave                  | leave CAN");
    System.err.println("script filename        | run a script");
    System.err.println("help                   | print this message");
  }

  /**
   * Max number of nodes in the returned list
   */
  private static final int MAX_NODES = 3;

  @Override
  public Map<String, String> getNodeList(String peerId, String ip)
      throws RemoteException {

    System.out
        .println(NAME_BOOTSTRAP + peerId + "@" + ip + " requested node list!");

    int nodesToReturn = Math.min(this.neighbors.size(), MAX_NODES - 1);

    System.out.println(NAME_BOOTSTRAP + "Current neighbor size = "
        + this.neighbors.size() + " nodes to return = " + nodesToReturn);

    Map<String, String> nodeList = new HashMap<>();
    Random r = new Random(new Date().getTime());

    while (nodeList.keySet().size() < nodesToReturn) {
      int index = r.nextInt(this.neighbors.size());
      Neighbor nb = this.neighbors.get(index);
      if (nodeList.containsKey(nb.getPeerId()))
        continue;
      nodeList.put(nb.getPeerId(), nb.getIp());
    }

    // Add it self into list

    nodeList.put(this.getPeerId(), this.getIP());

    System.out.println(NAME_BOOTSTRAP + "response node list = " + nodeList);

    return nodeList;
  }
}
