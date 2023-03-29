package code.world;

import code.math.MathHelp;

/**
 * A 'character' within the world. {@code Actor}s have attributes 
 * and can perform actions which alter these attributes.
 * 
 * @author William Kilty
 */
public class Actor {
  private final boolean huntHare;

  /**
   * Creates a new {@code Actor} with given parameters.
   * 
   * @param huntHare whether the actor is considering hunting hares at the moment
   */
  public Actor(boolean huntHare) {
    this.huntHare = huntHare;
  }

  /**
   * Creates a new {@code Actor} out of an encoded {@code int}.
   * All necessary parameters are to be extracted out of this {@code int}.
   * 
   * @param encoded the encoded {@code int} from which to construct this {@code Actor}
   */
  public Actor(int encoded) {
    this(MathHelp.intToBoolean(encoded & 1));
  }

  /**
   * Encodes this {@code Actor} into an {@code int} to be understood by a {@code ValueIterator}.
   * 
   * @return a uniquely identifiable {@code int} representing the state of this {@code Actor}
   */
  public int encode() {
    return MathHelp.booleanToInt(huntHare);
  }

  /**
   * Gets the size in bits of an {@code Actor}.
   * 
   * @return the number of bits required to represent an {@code Actor}
   */
  public static int size() {return 1;}
}
