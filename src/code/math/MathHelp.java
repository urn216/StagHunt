package code.math;

/**
 * A class to perform helpful math-related tasks
 * 
 * @author William Kilty
 */
public abstract class MathHelp {

  /**
   * Converts a {@code boolean} into a single bit.
   * 
   * @param b the {@code boolean} to represent as a bit
   * 
   * @return {@code 1} if input is {@code true}; {@code 0} if input is {@code false}
   */
  public static int booleanToInt(boolean b) {return b ? 1 : 0;}

  /**
   * Converts an {@code int} into a {@code boolean}.
   * 
   * @param i the {@code int} to represent as a {@code boolean}
   * 
   * @return {@code false} if input is {@code 0}; {@code true} if input is anything else
   */
  public static boolean intToBoolean(int i) {return i == 0 ? false : true;}
}
