package code.vi;

import java.util.Arrays;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import code.mdp.MDP;

public record CVIMaster(MDP[] mdps, int numIterations, ValueIterator.Storage[] stores) {

  /**
   * Runs the {@code CVIMaster} for its desired number of iterations under its {@code MDP}s
   * and calculates the percieved 'value' of each state within the scenario.
   * 
   * @return a set of percieved 'values' for each possible state within each present {@code MDP}
   */
  public double[][] doValueIteration() {
    return doXValueIterationSteps(new double[mdps.length][mdps[0].numStates()], numIterations);
  }

  /**
   * Performs a single value iteration step, 
   * ideally improving decision making for actors acting within a set of {@code MDP}s.
   * 
   * @param V a baseline set of values for each state in the parent {@code MDP}s
   * 
   * @return the - hopefully - improved set of values for each state in the parent {@code MDP}s.
   */
  public double[][] doValueIterationStep(double[][] V) {
    double[][] nextVs = new double[mdps.length][];
    for (int i = 0; i < mdps.length; i++) {
      double[][] Q = calculateQs(V[i], mdps[i]);
      nextVs[i] = calculateNextVs(Q, mdps[i]);
      if (this.stores != null) {
        this.stores[i].Q = Q;
        this.stores[i].V = nextVs[i];
      }
      else System.out.println(Arrays.deepToString(Q));
    }
    return nextVs;
  }

  /**
   * Performs a number of value iteration steps, 
   * ideally improving decision making for actors acting within a set of {@code MDP}s.
   * 
   * @param V a baseline set of values for each state in the parent {@code MDP}s
   * @param numIterations the number of value iterations to perform
   * 
   * @return the - hopefully - improved set of values for each state in the parent {@code MDP}s.
   */
  public double[][] doXValueIterationSteps(double[][] V, int numIterations) {
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
  private double[] calculateNextVs(double[][] Q, MDP mdp) {
    double[] ans = new double[mdp.numStates()];

    for (int s = 0; s < mdp.numStates(); s++) {
      ans[s] = calculateNextV(Q[s], mdp);
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
  private double calculateNextV(double[] sQ, MDP mdp) {
    return DoubleStream.of(sQ).max().getAsDouble();
  }

  /**
   * Creates a Q-table based on a previous understanding of V-values.
   * 
   * @param V an array of the percieved 'value' of each possible state in the {@code MDP}
   * 
   * @return a table consisting of percieved 'value' of performing each action at each state
   */
  private double[][] calculateQs(double[] V, MDP mdp) {
    double[][] ans = new double[mdp.numStates()][mdp.numActions()];

    for (int s = 0; s < mdp.numStates(); s++) {
      for (int a = 0; a < mdp.numActions(); a++) {
        ans[s][a] = calculateQ(s, a, V, mdp);
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
  private double calculateQ(int s, int a, double[] V, MDP mdp) {
    return (IntStream.range(0, mdp.numStates()).mapToDouble(
      (sPrime) -> mdp.T()[s][a][sPrime]*(mdp.R()[s][a][sPrime] + mdp.gamma()*V[sPrime])
    ).sum());
  }
}
