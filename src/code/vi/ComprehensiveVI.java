package code.vi;

import java.util.Arrays;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import mki.math.MathHelp;
import code.mdp.MDP;

public record ComprehensiveVI(MDP[][] mdps, int numIterations, ValueIterator.Storage[] stores) {

  /**
   * Runs the {@code CVIMaster} for its desired number of iterations under its {@code MDP}s
   * and calculates the percieved 'value' of each state within the scenario.
   * 
   * @return a set of percieved 'values' for each possible state within each present {@code MDP}
   */
  public double[][] doValueIteration() {
    return doXValueIterationSteps(new double[mdps.length][mdps[0][0].numStates()], numIterations);
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
    double[][][] Qs = new double[mdps.length*mdps.length][][];
    for (int actorRecieving = 0; actorRecieving < mdps.length; actorRecieving++) {
      for(int actorDoing = 0; actorDoing < mdps.length; actorDoing++) {
        Qs[actorRecieving*mdps.length + actorDoing] = calculateQs(V[actorRecieving], mdps[actorRecieving][actorDoing]);
      }
    }
    
    int[][] chosenActs = calculateBestActions(Qs);
    double[][] nextVs  = new double[mdps.length][];
    for (int i = 0; i < mdps.length; i++) {
      nextVs[i] = calculateNextVs(i, Qs, chosenActs);
      if (this.stores != null) {
        this.stores[i].Q = Qs[i*mdps.length+i];
        this.stores[i].V = nextVs[i];
      }
      else System.out.println(Arrays.deepToString(Qs[i*mdps.length+i]));
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

  private int[][] calculateBestActions(double[][][] Qs) {
    int[][] res = new int[Qs[0].length][mdps.length];
    for (int s = 0; s < Qs[0].length; s++) {
      for (int i = 0; i < mdps.length; i++) {
        res[s][i] = MathHelp.argMax(Qs[i*mdps.length + i][s]);
      }
    }
    return res;
  }

  /**
   * Finds the 'best' action to perform at each state and thus determines 
   * a single percieved value of each state in an {@code MDP}.
   * 
   * @param Q a table of percieved 'value' of performing each possible action at each possible state
   * @param chosenActs the indices of the actions chosen as 'best' by each of the actors within the {@code World} for each state
   * 
   * @return an array of state 'values'
   */
  private double[] calculateNextVs(int actorNum, double[][][] Qs, int[][] chosenActs) {
    double[] ans = new double[mdps[0][0].numStates()];

    for (int s = 0; s < mdps[0][0].numStates(); s++) {
      double[] values = new double[mdps.length];
      for(int i = 0; i < mdps.length; i++) {
        values[i] = Qs[actorNum*mdps.length + i][s][chosenActs[s][i]];
      }
      ans[s] = calculateNextV(values);
    }

    return ans;
  }

  /**
   * Finds the expected value at a given state to a VI
   * 
   * @param sQ the current percieved values of performing each action at a single state
   * @param chosenActs the indices of the actions chosen as 'best' by each of the actors within the {@code World}
   * 
   * @return the average of all the chosen actions' value to a single VI
   */
  private double calculateNextV(double[] sQ) {
    return DoubleStream.of(sQ).sum()/sQ.length;
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
