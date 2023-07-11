package code.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Stack;

import code.core.UICreator;
import code.mdp.MDP;
import code.mdp.OOMDP;
import code.vi.*;
import code.world.actors.*;

import mki.math.vector.Vector2;
import mki.ui.control.UIController;

public abstract class World {

  private static double gamma = 0.9;

  private static final Stack<State> stateHistory = new Stack<>();
  private static State currentState = null;

  private static int actorSize;
  private static Constructor<? extends Actor> actorConstructor;
  private static Action[] possibleActions;

  private static int numActors = 2;
  private static ValueIterator.Storage[] actorBrains = new ValueIterator.Storage[0];
  private static MDP[] actorMDPs = new MDP[0];

  private static int VIMode = 2;

  private static int numMDPIterations = 100;

  public static abstract class Setup {

    private static boolean ready = false;

    static {
      setActorType(ItemSwapper.class);
    }

    public static double getGamma() {
      return World.gamma;
    }
  
    public static void setGamma(double gamma) {
      World.gamma = gamma;
      World.Setup.ready = false;
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
      World.Setup.ready = false;
    }

    public static int getActorSize() {
      return actorSize;
    }

    public static void setActorType(Class<? extends Actor> c) {
      try {
        World.actorSize = (Integer)c.getDeclaredMethod("size").invoke(null);
        World.actorConstructor = c.getConstructor(State.class, int.class, int.class);
        setPossibleActions(World.actorSize);
      } catch (IllegalAccessException 
          |    IllegalArgumentException 
          |    InvocationTargetException 
          |    NoSuchMethodException
          |    SecurityException e) {
        e.printStackTrace();
      }
      World.Setup.ready = false;
    }

    public static void setPossibleActions(int numBools) {
      World.possibleActions = new Action[numBools+2];
      for (int i = 0; i < numBools; i++) {
        int b = i;
        World.possibleActions[i] = (a) -> a.toggleBool(b);
      }
      World.possibleActions[numBools  ] = Actor::getState;
      World.possibleActions[numBools+1] = Actor::leave;
      World.Setup.ready = false;
    }

    public static Constructor<? extends Actor> getActorConstructor() {
      return World.actorConstructor;
    }

    public static int getNumMDPIterations() {
      return World.numMDPIterations;
    }
  
    public static void setNumMDPIterations(int numMDPIterations) {
      World.numMDPIterations = numMDPIterations;
      World.Setup.ready = false;
    }

    public static int getVIMode() {
      return World.VIMode;
    }

    public static void setVIMode(int VIMode) {
      World.VIMode = VIMode;
      World.Setup.ready = false;
    }

    public static void initialiseMDPs() {
      actorMDPs = new MDP[numActors*(World.VIMode==2?2:1)];

      State.Encoder.resetStateTable();
      
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
      
      World.Setup.ready = false;
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

      World.Visualiser.reset();

      World.Setup.ready = true;
    }

    public static boolean isReady() {
      return World.Setup.ready;
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
      World.Visualiser.reset();
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
        brain.bestActionIndexAtState(State.Encoder.encode(World.currentState))
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

    private static boolean pressedIn = false;
    private static int pressedActor = -1;
    private static int selectedActor = -1;

    public static void reset() {
      UIController.clearTempElement();
      Visualiser.pressedIn = false;
      Visualiser.pressedActor = -1;
      Visualiser.selectedActor = -1;
    }

    public static void press(Vector2 p) {
      for (int i = 0; i < numActors; i++) {
        double ang = i/(numActors/2.0);
        if (p.subtract(0.4*Math.sin(Math.PI*ang), 0.4*Math.cos(Math.PI*ang)).magnitude() < 0.0625) pressedActor = i;
      }
      pressedIn = true;
    }

    public static void release(Vector2 p) {
      if (!pressedIn) return;

      double ang = pressedActor/(numActors/2.0);

      if (
        pressedActor < 0 || 
        pressedActor == selectedActor || 
        p.subtract(0.4*Math.sin(Math.PI*ang), 0.4*Math.cos(Math.PI*ang)).magnitude() >= 0.0625
      ) {
        reset();
        return;
      }

      UIController.displayTempElement(UICreator.generateCirclePanel(pressedActor));
      
      selectedActor = pressedActor;
      pressedIn = false;
      pressedActor = -1;
    }

    public static void draw(Graphics2D g, int width, int height) {
  
      if (currentState == null) {
        g.setColor(deerColour);
  
        g.fillOval(  (width-(int)(height*0.61))/2, (height-(int)(height*0.61))/2, (int)(height*0.61), (int)(height*0.61));
        deer.draw(g, (width-(int)(height*0.6 ))/2, (height-(int)(height*0.6 ))/2, (int)(height*0.6 ), (int)(height*0.6 ));
  
        return;
      }
  
      g.setColor(Setup.isReady() ? Color.white : Color.yellow);
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
