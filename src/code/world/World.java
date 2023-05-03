package code.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Stack;

import code.math.Vector2;
import code.mdp.MDP;
import code.mdp.OOMDP;
import code.vi.CVIMaster;
import code.vi.ValueIterator;

public abstract class World {

  private static double gamma = 0.9;

  private static final Stack<State> stateHistory = new Stack<>();
  private static State currentState = null;
  
  // private static Action[] possibleActions = {
  //   (a) -> {return a.toggleBool(0);},
  //   (a) -> {return a.getState();},
  //   (a) -> {return a.leave();}
  // };

  private static Action[] possibleActions = {
    (a) -> {return a.toggleBool(0);},
    (a) -> {return a.toggleBool(1);},
    (a) -> {return a.toggleBool(2);},
    (a) -> {return a.getState();},
    (a) -> {return a.leave();}
  };

  private static int numActors = 2;
  private static int ActorSize = 3;
  private static ValueIterator.Storage[] actorBrains = new ValueIterator.Storage[0];
  private static MDP[] actorMDPs = new MDP[0];

  private static boolean cooperativeVI = false;

  private static int numMDPIterations = 100;

  public static abstract class Setup {

    public static double getGamma() {
      return World.gamma;
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
      return World.numActors;
    }
  
    /**
     * Sets the total number of {@code Actor}s present in the current {@code World}
     * 
     * @param numActors number of {@code Actor} objects each {@code Scene} will use
     */
    public static void setNumActors(int numActors) {
      World.numActors = numActors;
    }

    public static int getActorSize() {
      return ActorSize;
    }

    public static int getNumMDPIterations() {
      return World.numMDPIterations;
    }
  
    public static void setNumMDPIterations(int numMDPIterations) {
      World.numMDPIterations = numMDPIterations;
    }

    public static boolean isCooperativeVI() {
      return cooperativeVI;
    }

    public static void setCooperativeVI(boolean cooperativeVI) {
      World.cooperativeVI = cooperativeVI;
    }

    public static void initialiseMDPs() {
      actorMDPs = new MDP[numActors];
      actorBrains = new ValueIterator.Storage[numActors];
      for (int i = 0; i < numActors; i++) {
        actorMDPs[i] = new OOMDP( gamma, possibleActions, i);
        actorBrains[i] = new ValueIterator.Storage();
      }
    }
  
    public static void doVI() {
      System.out.println("\nValue Iteration:");
      if (!World.cooperativeVI) for (int i = 0; i < actorMDPs.length; i++) {
        System.out.println(Arrays.toString(new ValueIterator(actorMDPs[i], numMDPIterations, actorBrains[i]).doValueIteration()));
      }
      else {
        double[][] Vs = new CVIMaster(actorMDPs, numMDPIterations, actorBrains).doValueIteration();
        System.out.println("        A   , B   ");
        for (int i = 0; i < Vs[0].length; i++) {
          System.out.printf("%6s: %.2f, %.2f\n", Integer.toBinaryString(i), Vs[0][i], Vs[1][i]);
        }
      }
    }

  }

  public static abstract class Player {

    public static MDP[] getActorMDPs() {
      return actorMDPs;
    }
  
    public static ValueIterator.Storage[] getActorBrains() {
      return actorBrains;
    }

    public static State getState() {
      return currentState;
    }
  
    public static void setState(State state) {
      World.currentState = state;
      World.stateHistory.clear();
    }
  
    public static void progressState(int actorNum) {
      if (World.currentState == null 
      ||  World.actorBrains.length != World.numActors
      ||  World.currentState.getActors().length <= actorNum) return;
  
      World.stateHistory.push(World.currentState);
      ValueIterator.Storage brain = World.actorBrains[actorNum];
      
      World.currentState = World.possibleActions[
        brain.bestIndexAtState(State.Encoder.encode(World.currentState))
      ].act(World.currentState.getActors()[actorNum]);
    }
  
    public static void regressState() {
      if (!World.stateHistory.empty()) World.currentState = World.stateHistory.pop();
    }

  }

  public static abstract class Visualiser {

    private static final Decal deer = new Decal("icon.png");
    private static final Color deerColour = new Color(27, 0, 15);

    public static void press(Vector2 p) {

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
}
