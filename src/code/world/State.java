package code.world;

import java.awt.Graphics2D;

import code.math.Vector2;

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
   * Decodes an {@code int} into its underlying {@code State}.
   * 
   * @param state the {@code int} to decode.
   * 
   * @return a {@code State} represented by the given {@code int}
   */
  public static State decode(int state) {
    if (state >= exitStateEncoded()) return new State();

    Actor[] actors = new Actor[World.getNumActors()];

    State result = new State(actors);

    for (int i = 0; i < actors.length; i++) {
      actors[i] = new Actor(result, i, state >> (i * Actor.size()));
    }

    return result;
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

    for (int i = 0; i < World.getNumActors(); i++) {
      res |= state.actors[i].encode() << (i * Actor.size());
    }

    return res;
  }

  /**
   * Gets the size in bits of a {@code State}.
   * 
   * @return the number of bits required to represent a {@code State}
   */
  public static int size() {
    return World.getNumActors()*Actor.size() + 1;
  }

  /**
   * The total number of {@code State}s possible with the current global settings.
   * 
   * @return the total number of unique {@code State} objects.
   */
  public static int numberOfStates() {
    return (1 << (World.getNumActors() * Actor.size())) + 1;
  }

  public static int exitStateEncoded() {
    return numberOfStates()-1;
  }

  ///////////////////////////////////////////////////////////////////////////////
  //NON STATIC/////////////////////NON STATIC////////////////////////NON STATIC//
  ///////////////////////////////////////////////////////////////////////////////

  private final Actor[] actors;
  private final Vector2[] actorPs;

  /**
   * Hidden constructor for {@code State} objects.
   * 
   * @param actors {@code array} of {@code Actor}s. Must be of size 
   * {@code numActors} or empty to represent the 'exit' {@code State}
   */
  private State(Actor... actors) {
    if (actors.length != World.getNumActors() && actors.length != 0) {
      throw new RuntimeException(
        "Invalid number of Actors! Expected " + World.getNumActors() + ", but recieved " + actors.length
      );
    }

    this.actors = actors;
    this.actorPs = new Vector2[actors.length];

    double r = 0.4;

    for (int i = 0; i < actors.length; i++) {
      double ang = i/(actors.length/2.0);
      actorPs[i] = new Vector2(r*Math.sin(Math.PI*ang), r*Math.cos(Math.PI*ang));
    }
  }

  /**
   * Gets the {@code Actor}s in their current condition at this {@code State}.
   * 
   * @return this {@code State}'s {@code Actor}s
   */
  public Actor[] getActors() {
    return actors;
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

  public State changeActor(int i, Actor newActor) {
    State res = clone();
    res.actors[i] = newActor;
    return res;
  }

  public State clone() {
    return State.decode(State.encode(this));
  }

  public void draw(Graphics2D g, int width, int height) {
    for (int i = 0; i < actors.length; i++) {
      actors[i].draw(g, (int)(actorPs[i].x*height+width/2), (int)(actorPs[i].y*height+height/2), height/8);
    }
  }
}
