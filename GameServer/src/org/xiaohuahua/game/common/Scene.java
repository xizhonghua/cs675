package org.xiaohuahua.game.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Scene extends JFrame {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private final IClient client;

  public Scene(IClient client) {

    this.client = client;

    setSize(new Dimension(Config.MAP_BLOCK_SIZE * Config.MAP_WIDTH,
        Config.MAP_BLOCK_SIZE * Config.MAP_HEIGHT));

    setVisible(true);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    Scene self = this;

    this.addKeyListener(new KeyListener() {

      @Override
      public void keyTyped(KeyEvent e) {
      }

      @Override
      public void keyReleased(KeyEvent e) {
      }

      @Override
      public void keyPressed(KeyEvent e) {

        try {

          switch (e.getKeyCode()) {
          case KeyEvent.VK_ESCAPE:
            if (client != null)
              client.leave();
            break;
          case KeyEvent.VK_UP:
            if (client != null)
              client.move(0, -1);
            break;
          case KeyEvent.VK_DOWN:
            if (client != null)
              client.move(0, 1);
            break;
          case KeyEvent.VK_LEFT:
            if (client != null)
              client.move(-1, 0);
            break;
          case KeyEvent.VK_RIGHT:
            if (client != null)
              client.move(1, 0);
            break;
          case KeyEvent.VK_SPACE:
            if (client != null)
              client.openChest();
            break;
          }

          self.render();
        } catch (Exception exeption) {
          System.out.println("Error: " + exeption);
          exeption.printStackTrace();
        }
      }
    });
  }

  public void render() {
    this.repaint();
    this.setTitle(client.getMe().toString());
  }

  private void drawMap(Graphics g) {
    for (int i = 0; i < Config.MAP_HEIGHT; ++i)
      for (int j = 0; j < Config.MAP_WIDTH; ++j) {
        int x = j * Config.MAP_BLOCK_SIZE;
        int y = i * Config.MAP_BLOCK_SIZE;

        int score = this.client.getWorld().getScore(j, i);

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

    GameWorld world = this.client.getWorld();

    g.setColor(Color.blue);
    for (Player p : world.getPlayers()) {
      this.drawPlayer(g, p);
    }

    g.setColor(Color.red);
    this.drawPlayer(g, this.client.getMe());
  }

  public void paint(Graphics g) {

    if (this.client.getWorld() == null)
      return;

    this.drawMap(g);

    this.drawPlayers(g);
  }

  public static void main(String arg[]) {

    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        // TODO Auto-generated method stub
        new Scene(null);
      }
    });
  }

}