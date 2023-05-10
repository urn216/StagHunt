package code.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Stack;

import code.math.Vector2;
import code.mdp.MDP;
import code.mdp.OOMDP;
import code.vi.ComprehensiveVI;
import code.vi.SymmetricVI;
import code.vi.ValueIterator;
import code.world.actors.Actor;

public abstract class World {

  private static double gamma = 0.9;

  private static final Stack<State> stateHistory = new Stack<>();
  private static State currentState = null;

  private static Action[] possibleActions = {
    (a) -> a.toggleBool(0),
    (a) -> a.toggleBool(1),
    (a) -> a.toggleBool(2),
    Actor::getState,
    Actor::leave,
  };

  private static int numActors = 2;
  private static int ActorSize = 3;
  private static ValueIterator.Storage[] actorBrains = new ValueIterator.Storage[0];
  private static MDP[] actorMDPs = new MDP[0];

  private static int VIMode = 2;

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

    public static int getVIMode() {
      return World.VIMode;
    }

    public static void setVIMode(int VIMode) {
      World.VIMode = VIMode;
    }

    public static void initialiseMDPs() {
      actorMDPs = new MDP[numActors*(World.VIMode==2?2:1)];
      
      actorBrains = new ValueIterator.Storage[numActors];
      for (int i = 0; i < numActors; i++) {
        if (World.VIMode!=2) {
          actorMDPs[i] = new OOMDP( gamma, possibleActions, i, i);
        }
        else {
          actorMDPs[i*2] = new OOMDP( gamma, possibleActions, i, i);
          actorMDPs[i*2+1] = new OOMDP( gamma, possibleActions, (i+1)%numActors, i);
        }
        actorBrains[i] = new ValueIterator.Storage();
      }
    }
  
    public static void doVI() {
      System.out.println("\nValue Iteration:");
      double[][] Vs = new double[numActors][];
      switch(World.VIMode){
        case 0:
        for (int i = 0; i < actorMDPs.length; i++) {
          Vs[i]=(new ValueIterator(actorMDPs[i], numMDPIterations, actorBrains[i]).doValueIteration());
        }
        break;
        case 1:
        Vs = new ComprehensiveVI(actorMDPs, numMDPIterations, actorBrains).doValueIteration();
        break;
        case 2:
        Vs = new SymmetricVI(new MDP[] {actorMDPs[0], actorMDPs[2]}, new MDP[] {actorMDPs[1], actorMDPs[3]}, numMDPIterations, actorBrains).doValueIteration();
      }
      printVs(Vs);
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

    private static ValueIterator.Storage getBrain(int actorNum) {
      if (World.currentState == null 
      ||  World.actorBrains.length != World.numActors
      ||  World.currentState.getActors().length <= actorNum) return null;
  
      World.stateHistory.push(World.currentState);
      return World.actorBrains[actorNum];
    }
  
    public static void progressState(int actorNum) {
      ValueIterator.Storage brain = getBrain(actorNum);
      if (brain == null) return;
      
      World.currentState = World.possibleActions[
        brain.bestIndexAtState(State.Encoder.encode(World.currentState))
      ].act(World.currentState.getActors()[actorNum]);
    }

    public static void actInState(int actorNum, int action) {
      if (getBrain(actorNum) == null) return;
      
      World.currentState = World.possibleActions[
        action
      ].act(World.currentState.getActors()[actorNum]);
    }
  
    public static void regressState() {
      if (!World.stateHistory.empty()) World.currentState = World.stateHistory.pop();
    }

  }

  public static abstract class Visualiser {

    private static final Decal deer = new Decal("deer.png");
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

  public static void printVs(double[][] Vs) {
    String buffer = "%"+(State.Encoder.size()-1)+"s";
    System.out.printf(buffer, "");
    for (int i = 0; i < Vs.length; i++) System.out.print("|  " + (char)(i+65) + "  ");
    System.out.println();
    for (int l = 0; l < Vs[0].length-1; l++) {
      System.out.print(String.format(buffer, Integer.toBinaryString(l)).replace(' ', '0'));
      for (int i = 0; i < Vs.length; i++) System.out.printf("|"+((Vs[i][l]<0)?"":" ")+"%.2f", Vs[i][l]);
      System.out.println();
    }
  }
}