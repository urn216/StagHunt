package code.world;

import code.core.Core;
import code.math.IOHelp;

// import code.core.Scene;

import code.math.Vector2;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
* Write a description of class Decal here.
*
* @author (your name)
* @version (a version number or a date)
*/
public class Decal
{
  private BufferedImage img;
  private Vector2 centre;
  private double scale;

  /**
  * Constructor for {@code Decal}s, fixed images to be drawn to screen at a given location
  */
  public Decal(String file, double x, double y, double scalePercent) {
    this.img = IOHelp.readImage(file);
    this.centre = new Vector2(x, y);
    this.scale = scalePercent;
  }

  public void draw(Graphics2D g) {
    Image img = this.img.getScaledInstance((int)(scale*Core.WINDOW.screenHeight()), (int)(scale*Core.WINDOW.screenHeight()), BufferedImage.SCALE_FAST);
    g.drawImage(
      img, 
      (int)(centre.x*Core.WINDOW.screenWidth ()-img.getWidth (null)/2.0), 
      (int)(centre.y*Core.WINDOW.screenHeight()-img.getHeight(null)/2.0), 
      null
    );
  }
}
