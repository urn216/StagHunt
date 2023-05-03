package code.math;

/**
 * A class to perform helpful math-related tasks
 * 
 * @author William Kilty
 */
public abstract class MathHelp {
  /**
  * Clamps a value to one between an upper and lower bound.
  *
  * @param val The value to clamp.
  * @param l The lower bound.
  * @param u The upper bound.
  *
  * @return The clamped value.
  */
  public static final double clamp(double val, double l, double u) {
    return Math.min(Math.max(val, l), u);
  }

  /**
  * Gives a value a certain percentage of the way from one point to another.
  *
  * @param start The starting value.
  * @param end The ending value.
  * @param p The percentage of the way through the transition.
  *
  * @return The lerped value.
  */
  public static final double lerp(double start, double end, double p) {
    return start + (end-start)*p;
  }

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

  public static int toggleBit(int num, int b) {
    return (num & (1 << b)) == 0 ? num | 1 << b : num & ~(1 << b);
  }

  public static int argMax(double... vals) {
    double max = Double.NEGATIVE_INFINITY;
    int argMax = -1;
    for (int i = 0; i < vals.length; i++) {
      if (vals[i] > max) {
        max = vals[i];
        argMax = i;
      }
    }
    return argMax;
  }
}
