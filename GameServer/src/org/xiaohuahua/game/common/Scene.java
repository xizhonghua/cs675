package org.xiaohuahua.game.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Scene extends JFrame {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private GameMap map;
  private Player player;

  public Scene(String title) {

    setSize(new Dimension(Config.MAP_BLOCK_SIZE * Config.MAP_WIDTH,
        Config.MAP_BLOCK_SIZE * Config.MAP_HEIGHT));

    setVisible(true);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  public void setMap(GameMap map) {
    this.map = map;
  }

  public void setMe(Player player) {
    this.player = player;
  }

  private void drawMap(Graphics g) {
    for (int i = 0; i < Config.MAP_HEIGHT; ++i)
      for (int j = 0; j < Config.MAP_WIDTH; ++j) {
        int x = j * Config.MAP_BLOCK_SIZE;
        int y = i * Config.MAP_BLOCK_SIZE;

        int score = this.map.getScore(j, i);

        if (score != 0) {
          g.setColor(Color.green);
        } else {
          g.setColor(Color.white);
        }

        g.fillRect(x, y, Config.MAP_BLOCK_SIZE, Config.MAP_BLOCK_SIZE);
      }
  }

  private void drawPlayer(Graphics g, Player p) {
    g.fillOval(p.getX() * Config.MAP_BLOCK_SIZE,
        p.getY() * Config.MAP_BLOCK_SIZE, Config.MAP_BLOCK_SIZE,
        Config.MAP_BLOCK_SIZE);
  }

  private void drawPlayers(Graphics g) {
    g.setColor(Color.blue);
    for (Player p : map.getPlayers()) {
      this.drawPlayer(g, p);
    }
    g.setColor(Color.red);
    this.drawPlayer(g, this.player);
  }

  public void paint(Graphics g) {

    if (map == null)
      return;

    this.drawMap(g);

    this.drawPlayers(g);
  }

  public static void main(String arg[]) {

    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        // TODO Auto-generated method stub
        new Scene("Test");
      }
    });
  }

}