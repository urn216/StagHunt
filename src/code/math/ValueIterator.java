package code.math;

import java.util.Arrays;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import code.mdp.MDP;

public record ValueIterator(MDP mdp, int numIterations, Storage store) {

  public double[] doValueIteration() {
    return doXValueIterationSteps(new double[mdp.numStates()], numIterations);
  }

  public double[] doValueIterationStep(double[] V) {
    double[][]   Q = calculateQs(V);
    double[] nextV = calculateNextVs(Q);
    if (store != null) {
      store.Q = Q;
      store.V = nextV;
    }
    else System.out.println(Arrays.deepToString(Q));
    return nextV;
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

  public static class Storage {
    public double[][] Q;
    public double[]   V;

    public int bestIndexAtState(int s) {
      for (int i = 0; i < Q[s].length; i++) {
        if (V[s] == Q[s][i]) return i;
      }
      return -1;
    }
  }
}
