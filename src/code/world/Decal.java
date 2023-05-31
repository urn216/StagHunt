package code.world;

import mki.io.FileIO;

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

  /**
  * Constructor for {@code Decal}s, fixed images to be drawn to screen at a given location and size
  */
  public Decal(String file) {
    this.img = FileIO.readImage(file);
  }

  public void draw(Graphics2D g, int x, int y, int width, int height) {
    Image img = this.img.getScaledInstance(width, height, BufferedImage.SCALE_FAST);
    g.drawImage(img, x, y, null);
  }
}
