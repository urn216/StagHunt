package code.core;

import java.util.Arrays;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public record ValueIterator(MDP mdp, int numIterations) {

  public double[] doValueIteration() {
    return doXValueIterationSteps(new double[mdp.numStates()], numIterations);
  }

  public double[] doValueIterationStep(double[] V) {
    double[][] Q = calculateQs(V);
    System.out.println(Arrays.deepToString(Q));
    return calculateNextVs(Q);
  }

  public double[] doXValueIterationSteps(double[] V, int numIterations) {
    for (int i = 0; i < numIterations; i++) {
      V=doValueIterationStep(V);
    }

    return V;
  }

  private double[] calculateNextVs(double[][] Q) {
    double[] ans = new double[mdp.numStates()];

    for (int s = 0; s < mdp.numStates(); s++) {
      ans[s] = calculateNextV(Q[s]);
    }

    return ans;
  }

  private double calculateNextV(double[] sQ) {
    return DoubleStream.of(sQ).max().getAsDouble();
  }

  private double[][] calculateQs(double[] V) {
    double[][] ans = new double[mdp.numStates()][mdp.numActions()];

    for (int s = 0; s < mdp.numStates(); s++) {
      for (int a = 0; a < mdp.numActions(); a++) {
        ans[s][a] = calculateQ(s, a, V);
      }
    }

    return ans;
  }

  private double calculateQ(int s, int a, double[] V) {
    return (IntStream.range(0, mdp.numStates()).mapToDouble((sPrime) -> {
      return mdp.T()[s][a][sPrime]*(mdp.R()[s][a][sPrime] + mdp.gamma()*V[sPrime]);
    }).sum());
  }
}
