package code.world;

import java.awt.Graphics2D;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.IntStream;

import code.world.actors.Actor;

import mki.math.vector.Vector2;

/**
 * A single state of the world.
 * <p>
 * A {@code State} represents a number of {@code Actors}, each frozen at a point in time; 
 * their variables making up the current representation of the world.
 * 
 * @author William Kilty
 */
public class State {

  /**
   * A collection of static functions useful for encoding and decoding {@code State}s
   * to and from binary for easy storage, transmission, and modification
   */
  public static abstract class Encoder {
    private static State[] stateTable = new State[numberOfStates()];

    public static void resetStateTable() {
      stateTable = new State[numberOfStates()];
    }
  
    /**
     * Decodes an {@code int} into its underlying {@code State}.
     * 
     * @param state the {@code int} to decode.
     * 
     * @return a {@code State} represented by the given {@code int}
     */
    public static State decode(int state) {
      if (stateTable.length!=numberOfStates()) stateTable = new State[numberOfStates()];
      state = Math.min(state, stateTable.length-1);
  
      if (stateTable[state] == null) {
        Actor[] actors = new Actor[state == stateTable.length - 1 ? 0 : World.Setup.getNumActors()];
        
        stateTable[state] = new State(state, actors);
        
        for (int i = 0; i < actors.length; i++) {
          try {
            actors[i] = World.Setup.getActorConstructor().newInstance(stateTable[state], i, state >> (i * World.Setup.getActorSize()));
          } catch (InstantiationException
              |    IllegalAccessException 
              |    IllegalArgumentException
              |    InvocationTargetException e) {
            e.printStackTrace();
          }
        }
      }
  
      return stateTable[state];
    }
  
    /**
     * Encodes a {@code State} into a single {@code int} to be understood by a {@code ValueIterator}.
     * 
     * @param state the {@code State} to encode
     * 
     * @return a uniquely identifiable {@code int} representation of the given {@code State}
     */
    public static int encode(State state) {
      if (state.actors.length == 0) return exitStateEncoded();
  
      int res = 0;
  
      for (int i = 0; i < state.actors.length; i++) {
        res |= state.actors[i].encoded() << (i * World.Setup.getActorSize());
      }
  
      return res;
    }
  
    /**
     * Gets the size in bits of a {@code State}.
     * 
     * @return the number of bits required to represent a {@code State}
     */
    public static int size() {
      return World.Setup.getNumActors()*World.Setup.getActorSize() + 1;
    }
  
    /**
     * The total number of {@code State}s possible with the current global settings.
     * 
     * @return the total number of unique {@code State} objects.
     */
    public static int numberOfStates() {
      return (1 << (World.Setup.getNumActors() * World.Setup.getActorSize())) + 1;
    }
  
    public static int exitStateEncoded() {
      return numberOfStates()-1;
    }
  }

  private final Actor[] actors;
  private final Vector2[] actorPs;
  private final Vector2[] actorObjPs;
  private final int encoded;

  /**
   * Hidden constructor for {@code State} objects.
   * 
   * @param actors {@code array} of {@code Actor}s. Must be of size 
   * {@code numActors} or empty to represent the 'exit' {@code State}
   */
  private State(int state, Actor... actors) {
    if (actors.length != World.Setup.getNumActors() && actors.length != 0) {
      throw new RuntimeException(
        "Invalid number of Actors! Expected " + World.Setup.getNumActors() + ", but recieved " + actors.length
      );
    }

    this.encoded = state;

    this.actors = actors;
    this.actorPs = new Vector2[actors.length];
    this.actorObjPs = new Vector2[actors.length];

    double actR = World.Visualiser.getActorRingRadius();
    double innR = World.Visualiser.getInnerRingRadius();

    double innOff = 1.0/actors.length;

    double offset = World.Visualiser.isOffset45() ? 0.25 : 0;

    for (int i = 0; i < actors.length; i++) {
      double ang = i/(actors.length/2.0)+offset;
      actorPs[i] = new Vector2(actR*Math.sin(Math.PI*ang), actR*Math.cos(Math.PI*ang));
      ang -= innOff;
      actorObjPs[i] = new Vector2(innR*Math.sin(Math.PI*ang), innR*Math.cos(Math.PI*ang));
    }
  }

  public int getDebugEncoding() {return encoded;}

  /**
   * Gets the {@code Actor}s in their current condition at this {@code State}.
   * 
   * @return this {@code State}'s {@code Actor}s
   */
  public Actor[] getActors() {
    return actors;
  }

  /**
   * Gets the positions of the centre of this {@code State}'s {@code Actor}s.
   * 
   * @return this {@code State}'s {@code Actor} positions in {@code Vector2} format
   */
  public Vector2[] getActorPs() {
    return actorPs;
  }

  /**
   * Gets the positions of the centre of any objects possessed by this {@code State}'s {@code Actor}s.
   * 
   * @return this {@code State}'s {@code Actor}-belongings positions in {@code Vector2} format
   */
  public Vector2[] getActorObjPs() {
    return actorObjPs;
  }

  /**
   * Retrieves the {@code Actor} at a given {@code Vector2} position.
   * <p>
   * Assumes given position is in a square between {@code -1} and {@code 1} in both dimensions
   * 
   * @param p a {@code Vector2} with {@code x} and {@code y} coordinates between {@code -1} and {@code 1}
   * @return the {@code Actor} at the given location, or {@code null} if none was found
   */
  public Actor getActor(Vector2 p) {
    int closest = getActorNum(p);
    return closest == -1 ? null : actors[closest];
  }

  /**
   * Retrieves the index of the {@code Actor} at a given {@code Vector2} position.
   * <p>
   * Assumes given position is in a square between {@code -1} and {@code 1} in both dimensions
   * 
   * @param p a {@code Vector2} with {@code x} and {@code y} coordinates between {@code -1} and {@code 1}
   * @return the @{@code int} index of the {@code Actor} at the given location, or {@code -1} if none was found
   */
  public int getActorNum(Vector2 p) {
    return IntStream.range(0, actors.length).filter((i) -> p.subtract(actorPs[i]).magsquare() < 0.00390625).reduce(
      (a, b) -> actorPs[a].subtract(p).magsquare() < actorPs[a].subtract(p).magsquare() ? a : b
    ).orElse(-1);
  }

  /**
   * Checks to see if all the {@code Actor}s in this {@code State} meet a certain condition.
   * 
   * @param ac the condition to check for on all {@code Actor}s
   * 
   * @return {@code true} if all {@code Actor}s meet the condition
   */
  public boolean allActorsCond(ActorCond ac) {
    for (int i = 0; i < actors.length; i++) {
      if (!ac.find(actors[i])) return false;
    }
    return true;
  }

  public boolean atMostOneActorCond(ActorCond ac) {
    boolean found = false;
    for (int i = 0; i < actors.length; i++) {
      if (ac.find(actors[i])) {
        if (found) {return false;}
        found = true;
      }
    }
    return true;
  }

  public boolean allButMeCond(ActorCond ac, int actorNum) {
    for (int i = 0; i < actors.length; i++) {
      if (i==actorNum) continue;
      if (!ac.find(actors[i])) return false;
    }
    return true;
  }

  public boolean leftOfMeCond(ActorCond ac, int actorNum) {
    return ac.find(actors[(actorNum-1+actors.length)%actors.length]);
  }

  public boolean rightOfMeCond(ActorCond ac, int actorNum) {
    return ac.find(actors[(actorNum+1)%actors.length]);
  }

  public State changeActor(int i, int newActorEncoding) {
    int encoded = (
      State.Encoder.encode(this) & ~(((1 << World.Setup.getActorSize()) - 1) << (i * World.Setup.getActorSize()))
    ) | (
      newActorEncoding << (i * World.Setup.getActorSize())
    );
    // System.out.println("Actor " + i + ": " + encode(this) + " -> " + encoded);
    return State.Encoder.decode(encoded);
  }

  public String toString() {
    return ""+Encoder.encode(this);
  }

  public boolean equals(Object o) {
    if (!(o instanceof State s)) return false;

    return Encoder.encode(this) == Encoder.encode(s);
  }

  /**
   * Draws this {@code State} to a {@code Graphics2D} object at a given size and offset.
   * 
   * @param g the {@code Graphics2D} object to draw to
   * @param x the left-most x-coordinate of this {@code State} within {@code g}
   * @param y the top-most y-coordinate of this {@code State} within {@code g}
   * @param width the width in pixels to draw this {@code State} at
   * @param height the height in pixels to draw this {@code State} at
   */
  public void draw(Graphics2D g, int x, int y, int width, int height) {
    for (int i = 0; i < actors.length; i++) {
      actors[i].draw(g, x, y, width, height);
    }
  }
}
