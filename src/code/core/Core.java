package code.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import ui.control.UIController;

import code.math.IOHelp;
import code.mdp.ClassicMDP;
import code.mdp.MDP;
import code.vi.ValueIterator;
import code.world.World;

public abstract class Core {

  private static final double COST_OF_LIVING  = -0.1;
  private static final double COST_OF_FAILING = -10;
  
  public static final Window WINDOW = new Window();
  
  private static final double TICKS_PER_SECOND = 60;
  private static final double MILLISECONDS_PER_TICK = 1000/TICKS_PER_SECOND;
  
  private static boolean quit = false;
  
  private static MDP mdp = new ClassicMDP(
    0.9, 3, 3, 
    new double[][][] { // Transition Matrix
      { // hare hunting
        { // toggle mode
          0, 1, 0
        },
        { // pass
          1, 0, 0
        },
        { // exit
          0, 0, 1
        }
      },
      { // stag hunting
        { // toggle mode
          1, 0, 0
        },
        { // pass
          0, 1, 0
        },
        { // exit
          0, 0, 1
        }
      },
      { // exited
        { // toggle mode
          0, 0, 0
        },
        { // pass
          0, 0, 0
        },
        { // exit
          0, 0, 0
        }
      }
    }, 
    new double[][][] { // Reward Matrix
      { // hare hunting
        { // toggle mode
          COST_OF_FAILING, COST_OF_LIVING, COST_OF_FAILING
        },
        { // pass
          COST_OF_LIVING, COST_OF_FAILING, COST_OF_FAILING
        },
        { // exit
          COST_OF_FAILING, COST_OF_FAILING, 3
        }
      },
      { // stag hunting
        { // toggle mode
          COST_OF_LIVING, COST_OF_FAILING, COST_OF_FAILING
        },
        { // pass
          COST_OF_FAILING, COST_OF_LIVING, COST_OF_FAILING
        },
        { // exit
          COST_OF_FAILING, COST_OF_FAILING, 4
        }
      },
      { // exited
        { // toggle mode
          0, 0, 0
        },
        { // pass
          0, 0, 0
        },
        { // exit
          0, 0, 0
        }
      }
    }
  );
  
  static {
    WINDOW.setFullscreen(false);
    
    UIController.putPane("Main Menu", UICreator.createMain());
    UIController.setCurrentPane("Main Menu");
    Controls.initialiseControls(WINDOW.FRAME);
  }
  
  public static void main(String[] args) {
    ValueIterator vi = new ValueIterator(mdp, 3, null);
    System.out.println(Arrays.toString(vi.doValueIteration()));
    run();
  }
  
  /**
  * Sets a flag to close the program at the nearest convenience
  */
  public static void quitToDesk() {
    quit = true;
  }

  public static void printWorld(int size) {
    BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    World.Visualiser.draw(img.createGraphics(), size, size);
    IOHelp.writeImage("../output.png", img);
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

      if (Controls.KEY_DOWN[0x30]) {World.Player.progressState(0); Controls.KEY_DOWN[0x30] = false;}
      if (Controls.KEY_DOWN[0x31]) {World.Player.progressState(1); Controls.KEY_DOWN[0x31] = false;}
      if (Controls.KEY_DOWN[0x32]) {World.Player.progressState(2); Controls.KEY_DOWN[0x32] = false;}
      if (Controls.KEY_DOWN['\b']) {World.Player.regressState();   Controls.KEY_DOWN['\b'] = false;}
      
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