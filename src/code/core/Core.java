package code.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import mki.ui.control.UIController;

import mki.io.FileIO;
import code.mdp.ClassicMDP;
import code.vi.ValueIterator;
import code.world.World;

public abstract class Core {
  
  public static final Window WINDOW = new Window();
  
  private static final double TICKS_PER_SECOND = 60;
  private static final double MILLISECONDS_PER_TICK = 1000/TICKS_PER_SECOND;
  
  private static boolean quit = false;
  
  static {
    WINDOW.setFullscreen(false);
    
    UIController.putPane("Main Menu", UICreator.createMain());
    UIController.setCurrentPane("Main Menu");
    Controls.initialiseControls(WINDOW.FRAME);
  }
  
  public static void main(String[] args) {
    ValueIterator vi = new ValueIterator(ClassicMDP.oneManStagHunt, 3, null);
    System.out.println(Arrays.toString(vi.doValueIteration()));
    run();
  }
  
  /**
  * Sets a flag to close the program at the nearest convenience
  */
  public static void quitToDesk() {
    quit = true;
  }

  public static void printWorld(String filename, int size) {
    BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    World.Visualiser.draw(img.createGraphics(), size, size);
    FileIO.writeImage("../screenshots/"+filename + (filename.endsWith(".png") ? "" : ".png"), img);
  }
  
  /**
  * Main loop. Should always be running. Runs the rest of the game engine
  */
  private static void run() {
    while (true) {
      long tickTime = System.currentTimeMillis();
      
      if (quit) {
        System.exit(0);
      }
      
      WINDOW.PANEL.repaint();
      tickTime = System.currentTimeMillis() - tickTime;
      try {
        Thread.sleep(Math.max((long)(MILLISECONDS_PER_TICK - tickTime), 0));
      } catch(InterruptedException e){System.out.println(e); System.exit(0);}
    }
  }
  
  /**
  * Paints the contents of the program to the given {@code Graphics} object.
  * 
  * @param gra the supplied {@code Graphics} object
  */
  public static void paintComponent(Graphics gra) {
    Graphics2D g = (Graphics2D)gra;

    int sw = WINDOW.screenWidth(), sh = WINDOW.screenHeight();
    
    g.setColor(Color.black);
    
    g.fillRect(0, 0, sw, sh);

    World.Visualiser.draw(g, sw, sh);
    
    UIController.draw(g, WINDOW.screenWidth(), WINDOW.screenHeight());
  }
}