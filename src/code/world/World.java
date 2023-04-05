package code.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;

import code.math.ValueIterator;
import code.mdp.MDP;

public abstract class World {

  private static final Decal deer = new Decal("icon.png");
  private static final Color deerColour = new Color(27, 0, 15);

  private static double gamma = 0.9;
  private static int numActors = 1;
  private static State currentState = null;

  private static MDP mdp = null;
  private static int numMDPIterations = 3;

  public static void doVI() {
    ValueIterator vi = new ValueIterator(mdp, numMDPIterations);
    System.out.println(Arrays.toString(vi.doValueIteration()));
  }

  public static double getGamma() {
    return gamma;
  }

  public static void setGamma(double gamma) {
    World.gamma = gamma;
  }

  /**
   * Gets the total number of {@code Actor}s present in the current {@code World}
   * 
   * @return number of {@code Actor} objects each {@code Scene} will use
   */
  public static int getNumActors() {
    return numActors;
  }

  /**
   * Sets the total number of {@code Actor}s present in the current {@code World}
   * 
   * @param numActors number of {@code Actor} objects each {@code Scene} will use
   */
  public static void setNumActors(int numActors) {
    World.numActors = numActors;
  }

  public static MDP getMdp() {
    return mdp;
  }

  public static void setMdp(MDP mdp) {
    World.mdp = mdp;
  }

  public static int getNumMDPIterations() {
    return numMDPIterations;
  }

  public static void setNumMDPIterations(int numMDPIterations) {
    World.numMDPIterations = numMDPIterations;
  }

  public static State getState() {
    return currentState;
  }

  public static void setState(State state) {
    World.currentState = state;
  }

  public static void draw(Graphics2D g, int width, int height) {

    if (currentState == null) {
      g.setColor(deerColour);

      g.fillOval(  (width-(int)(height*0.61))/2, (height-(int)(height*0.61))/2, (int)(height*0.61), (int)(height*0.61));
      deer.draw(g, (width-(int)(height*0.6 ))/2, (height-(int)(height*0.6 ))/2, (int)(height*0.6 ), (int)(height*0.6 ));

      return;
    }

    g.setColor(Color.white);
    g.drawOval((width - height)/2, 0, height, height);

    currentState.draw(g, width, height);
  }
}
