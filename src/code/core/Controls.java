package code.core;

import java.awt.MouseInfo;

import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import code.math.Vector2I;

import code.ui.UIController;

/**
 * Handles all user input within the game
 */
abstract class Controls {
  
  public static final boolean[] KEY_DOWN = new boolean[65536];
  public static final boolean[] MOUSE_DOWN = new boolean[Math.max(MouseInfo.getNumberOfButtons(), 3)];
  
  public static Vector2I mousePos = new Vector2I();
  public static Vector2I mousePre = new Vector2I();
  
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
        
        if (UIController.getHighlightedInteractable() == null) MOUSE_DOWN[e.getButton()] = true;
        mousePre = mousePos;
        
        //left click
        if (e.getButton() == 1) {
          if (UIController.press()) return;
        }
        
        //right click
        if (e.getButton() == 3) {
          return;
        }
      }
      
      @Override
      public void mouseReleased(MouseEvent e) {
        updateMousePos(e);
        
        MOUSE_DOWN[e.getButton()] = false;
        
        //left click
        if (e.getButton() == 1) {
          UIController.release();
          return;
        }
        
        //right click
        if (e.getButton() == 3) {
          return;
        }
      }
      
      @Override
      public void mouseExited(MouseEvent e) {
        mousePos = new Vector2I(Core.WINDOW.screenWidth()/2, Core.WINDOW.screenHeight()/2);
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
        if (keyCode == KeyEvent.VK_F11) {
          Core.WINDOW.toggleFullscreen();
          return;
        }
        if (keyCode == KeyEvent.VK_F2) {
          Core.printWorld(1080);;
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
}
