package code.core;

import java.util.Arrays;

public abstract class Core {

  private static MDP mdp = new MDP(0, 0, 0, new double[0][0][0], new double[0][0]);

  private static int numIterations = 10;

  public static void main(String[] args) {
    ValueIterator vi = new ValueIterator(mdp, numIterations);

    System.out.println(Arrays.toString(vi.doValueIteration()));
  }
}