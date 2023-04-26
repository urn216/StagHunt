package code.vi;

import java.util.Arrays;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import code.mdp.MDP;

public record ValueIterator(MDP mdp, int numIterations, Storage store) {

  /**
   * Runs the {@code ValueIterator} for its desired number of iterations under its {@code MDP}
   * and calculates the percieved 'value' of each state within the scenario.
   * 
   * @return a set of percieved 'values' for each possible state within an {@code MDP}
   */
  public double[] doValueIteration() {
    return doXValueIterationSteps(new double[mdp.numStates()], numIterations);
  }

  /**
   * Performs a single value iteration step, 
   * ideally improving decision making for actors acting within an {@code MDP}.
   * 
   * @param V a baseline set of values for each state in the parent {@code MDP}
   * 
   * @return the - hopefully - improved set of values for each state in the parent {@code MDP}.
   */
  public double[] doValueIterationStep(double[] V) {
    double[][]   Q = calculateQs(V);
    double[] nextV = calculateNextVs(Q);
    if (this.store != null) {
      this.store.Q = Q;
      this.store.V = nextV;
    }
    else System.out.println(Arrays.deepToString(Q));
    return nextV;
  }

  /**
   * Performs a number of value iteration steps, 
   * ideally improving decision making for actors acting within an {@code MDP}.
   * 
   * @param V a baseline set of values for each state in the parent {@code MDP}
   * @param numIterations the number of value iterations to perform
   * 
   * @return the - hopefully - improved set of values for each state in the parent {@code MDP}.
   */
  public double[] doXValueIterationSteps(double[] V, int numIterations) {
    for (int i = 0; i < numIterations; i++) {
      V=doValueIterationStep(V);
    }

    return V;
  }

  /**
   * Finds the 'best' action to perform at each state and thus determines 
   * a single percieved value of each state in an {@code MDP}.
   * 
   * @param Q a table of percieved 'value' of performing each possible action at each possible state
   * 
   * @return an array of state 'values'
   */
  private double[] calculateNextVs(double[][] Q) {
    double[] ans = new double[mdp.numStates()];

    for (int s = 0; s < mdp.numStates(); s++) {
      ans[s] = calculateNextV(Q[s]);
    }

    return ans;
  }

  /**
   * Finds the best possible action to perform at a given state, 
   * and returns the percieved value of performing that action from that state.
   * 
   * @param sQ the current percieved values of performing each action at a single state
   * 
   * @return the maximum value possible from an action at a given state.
   */
  private double calculateNextV(double[] sQ) {
    return DoubleStream.of(sQ).max().getAsDouble();
  }

  /**
   * Creates a Q-table based on a previous understanding of V-values.
   * 
   * @param V an array of the percieved 'value' of each possible state in the {@code MDP}
   * 
   * @return a table consisting of percieved 'value' of performing each action at each state
   */
  private double[][] calculateQs(double[] V) {
    double[][] ans = new double[mdp.numStates()][mdp.numActions()];

    for (int s = 0; s < mdp.numStates(); s++) {
      for (int a = 0; a < mdp.numActions(); a++) {
        ans[s][a] = calculateQ(s, a, V);
      }
    }

    return ans;
  }

  /**
   * Calculates a Q-value for a given starting state and action to perform.
   * 
   * @param s the starting state in {@code int} representation
   * @param a the action being performed here in {@code int} representation
   * @param V the current value of each state to the owner of this VI
   * 
   * @return a single {@code double} representing the percieved value of performing an action at a given state
   */
  private double calculateQ(int s, int a, double[] V) {
    return (IntStream.range(0, mdp.numStates()).mapToDouble(
      (sPrime) -> mdp.T()[s][a][sPrime]*(mdp.R()[s][a][sPrime] + mdp.gamma()*V[sPrime])
    ).sum());
  }


  //VI STORAGE


  public static class Storage {
    public double[][] Q;
    public double[]   V;

    /**
     * Finds the index of the action which, when performed at a given state, 
     * will provide the best outcome for this VI.
     * 
     * @param s the starting state in {@code int} representation
     * 
     * @return the index of the best action to perform from the given starting state.
     */
    public int bestIndexAtState(int s) {
      for (int i = 0; i < Q[s].length; i++) {
        if (V[s] == Q[s][i]) return i;
      }
      return -1;
    }
  }
}
