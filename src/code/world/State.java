package code.world;

/**
 * A single state of the world.
 * <p>
 * A {@code State} represents a number of {@code Actors}, each frozen at a point in time; 
 * their variables making up the current representation of the world.
 * 
 * @author William Kilty
 */
public class State {

  private static int numActors = 1;

  /**
   * Decodes an {@code int} into its underlying {@code State}.
   * 
   * @param state the {@code int} to decode.
   * 
   * @return a {@code State} represented by the given {@code int}
   */
  public static State decode(int state) {
    if (state >= numberOfStates()) return new State();

    Actor[] actors = new Actor[numActors];

    for (int i = 0; i < actors.length; i++) {
      actors[i] = new Actor(state >> (i * Actor.size()));
    }

    return new State(actors);
  }

  /**
   * Encodes a {@code State} into a single {@code int} to be understood by a {@code ValueIterator}.
   * 
   * @param state the {@code State} to encode
   * 
   * @return a uniquely identifiable {@code int} representation of the given {@code State}
   */
  public static int encode(State state) {
    if (state.actors.length == 0) return numberOfStates();

    int res = 0;

    for (int i = 0; i < numActors; i++) {
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
    return numActors*Actor.size() + 1;
  }

  /**
   * The total number of {@code State}s possible with the current global settings.
   * 
   * @return the total number of unique {@code State} objects.
   */
  public static int numberOfStates() {
    return 1 << (numActors * Actor.size());
  }

  private final Actor[] actors;

  /**
   * Hidden constructor for {@code State} objects.
   * 
   * @param actors {@code array} of {@code Actor}s. Must be of size 
   * {@code numActors} or empty to represent the 'exit' {@code State}
   */
  private State(Actor... actors) {
    if (actors.length != numActors || actors.length != 0) {
      throw new RuntimeException(
        "Invalid number of Actors! Expected " + numActors + ", but recieved " + actors.length
      );
    }

    this.actors = actors;
  }
}
