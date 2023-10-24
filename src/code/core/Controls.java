package code.core;

import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import code.world.State;
import code.world.World;

import mki.math.vector.Vector2;
import mki.math.vector.Vector2I;
import mki.ui.control.UIController;

/**
 * Handles all user input within the game
 */
abstract class Controls {
  
  public static final boolean[] KEY_DOWN = new boolean[65536];
  
  public static Vector2I mousePos = new Vector2I();
  
  /**
  * Starts up all the listeners for the window. Only to be called once on startup.
  */
  public static void initialiseControls(JFrame FRAME) {
    
    //Mouse Controls
    FRAME.addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        updateMousePos(e);
      }
      
      @Override
      public void mouseDragged(MouseEvent e) {
        updateMousePos(e);
      }
    });
    FRAME.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        updateMousePos(e);
        
        //left click
        if (e.getButton() == 1) {
          if (!UIController.press())
            World.Visualiser.press(mouseToWorldSpace());
        }
      }
      
      @Override
      public void mouseReleased(MouseEvent e) {
        updateMousePos(e);
        
        //left click
        if (e.getButton() == 1) {
          UIController.release();
          World.Visualiser.release(mouseToWorldSpace());
        }
      }
    });
    
    //Keyboard Controls
    FRAME.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        if (UIController.getActiveTextfield() != null && !KEY_DOWN[KeyEvent.VK_CONTROL]) UIController.typeKey(e);
        
        if(KEY_DOWN[keyCode]) return; //Key already in
        KEY_DOWN[keyCode] = true;
        
        // System.out.print(keyCode);
        for (int i = 0; i < 10; i++)
          if (keyCode == '0'+i) {World.Player.progressState(i); return;}
        for (int i = 0; i < 5; i++)
          if (keyCode == 'A'+i) {World.Player.actInState(0, i); return;}
        
        if (keyCode == KeyEvent.VK_BACK_SPACE) {
          World.Player.regressState();
          return;
        }
        if (keyCode == KeyEvent.VK_F2) {
          Core.printWorld("screenshot_"+Integer.toBinaryString(State.Encoder.encode(World.Player.getState())), 1080);
          return;
        }
        if (keyCode == KeyEvent.VK_F1) {
          MoveTree.drawMoveTree("tree_from_"+Integer.toBinaryString(State.Encoder.encode(World.Player.getState())), 480);
          return;
        }
        if (keyCode == KeyEvent.VK_F3) {
          MoveTree.drawNormalForm("normal_form_"+World.Setup.getActorType().getSimpleName(), 480);
          return;
        }
        if (keyCode == KeyEvent.VK_F11) {
          Core.WINDOW.toggleFullscreen();
          return;
        }
        if (keyCode == KeyEvent.VK_ESCAPE) {
          UIController.release();
          UIController.back();
          return;
        }
        if (keyCode == KeyEvent.VK_ENTER) {
          UIController.press();
          return;
        }
      }
      
      @Override
      public void keyReleased(KeyEvent e){
        int keyCode = e.getKeyCode();
        KEY_DOWN[keyCode] = false;
        
        if (keyCode == KeyEvent.VK_ENTER) {
          UIController.release();
        }
      }
    });
  }
  
  /**
  * Updates the program's understanding of the location of the mouse cursor after a supplied {@code MouseEvent}.
  * 
  * @param e the {@code MouseEvent} to determine the cursor's current position from
  */
  public static void updateMousePos(MouseEvent e) {
    int x = e.getX() - Core.WINDOW.toolBarLeft;
    int y = e.getY() - Core.WINDOW.toolBarTop;
    mousePos = new Vector2I(x, y);
    
    UIController.cursorMove(mousePos);
  }

  public static Vector2 mouseToWorldSpace() {
    return new Vector2(
      mousePos.x - (0.5 * Core.WINDOW.screenWidth ()), 
      mousePos.y - (0.5 * Core.WINDOW.screenHeight())
    ).scale        (1.0 / Core.WINDOW.screenHeight());
  }
}
