package code.vi;

import java.util.Arrays;
import java.util.stream.IntStream;

import mki.math.MathHelp;
import code.mdp.MDP;

public record SymmetricVI(MDP[] mdps, MDP[] mdpToOthers, int numIterations, ValueIterator.Storage[] stores) {

  /**
   * Runs the {@code ValueIterator} for its desired number of iterations under its {@code MDP}
   * and calculates the percieved 'value' of each state within the scenario.
   * 
   * @return a set of percieved 'values' for each possible state within an {@code MDP}
   */
  public double[][] doValueIteration() {
    return doXValueIterationSteps(new double[mdps.length][mdps[0].numStates()], numIterations);
  }

  /**
   * Performs a single value iteration step, 
   * ideally improving decision making for actors acting within an {@code MDP}.
   * 
   * @param V a baseline set of values for each state in the parent {@code MDP}
   * 
   * @return the - hopefully - improved set of values for each state in the parent {@code MDP}.
   */
  public double[][] doValueIterationStep(double[][] V) {
    double[][][] Qts = new double[mdps.length][][];
    double[][][] Qos = new double[mdps.length][][];
    for (int i = 0; i < mdps.length; i++) {
      Qts[i] = calculateQs(V[i], mdps[i]);
      Qos[i] = calculateQs(V[i], mdpToOthers[i]);
    }
    
    int[][] chosenActs = calculateBestActions(Qts);
    double[][] nextVs  = new double[mdps.length][];
    for (int i = 0; i < mdps.length; i++) {
      nextVs[i] = calculateNextVs(i, Qts[i], Qos[i], chosenActs);
      if (this.stores != null) {
        this.stores[i].Q = Qts[i];
        this.stores[i].V = nextVs[i];
      }
      else System.out.println(Arrays.deepToString(Qts));
    }
    return nextVs;
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
  public double[][] doXValueIterationSteps(double[][] V, int numIterations) {
    for (int i = 0; i < numIterations; i++) {
      V=doValueIterationStep(V);
    }

    return V;
  }

  private int[][] calculateBestActions(double[][][] Qs) {
    int[][] res = new int[Qs[0].length][Qs.length];
    for (int s = 0; s < Qs[0].length; s++) {
      for (int i = 0; i < mdps.length; i++) {
        res[s][i] = MathHelp.argMax(Qs[i][s]);
      }
    }
    return res;
  }

  /**
   * Finds the 'best' action to perform at each state and thus determines 
   * a single percieved value of each state in an {@code MDP}.
   * 
   * @param Q a table of percieved 'value' of performing each possible action at each possible state
   * 
   * @return an array of state 'values'
   */
  private double[] calculateNextVs(int actorNum, double[][] Qt, double[][] Qo, int[][] chosenActs) {
    double[] ans = new double[mdps[0].numStates()];

    for (int s = 0; s < mdps[0].numStates(); s++) {
      ans[s] = calculateNextV(actorNum, Qt[s], Qo[s], chosenActs[s]);
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
  private double calculateNextV(int actorNum, double[] sQt, double[] sQo, int[] chosenActs) {
    return (sQt[chosenActs[actorNum]]+sQo[chosenActs[(actorNum+1)%chosenActs.length]])/2.0;
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

  
  ///////////            \\\\\\\\\\\
  /////////                \\\\\\\\\
  /////////                \\\\\\\\\
  ///////   Symmetrization   \\\\\\\
  ///////                    \\\\\\\
  /////                        \\\\\

  
  // private double[][] flipQ(double[][] Q) {
  //   double[][] res = new double[Q.length][];
  //   for (int s = 0; s < res.length; s++) {
  //     res[s] = Q[flipS(s)];
  //   }
  //   return res;
  // }

  // private double[] flipV(double[] V) {
  //   double[] res = new double[V.length];
  //   for (int s = 0; s < res.length; s++) {
  //     res[s] = V[flipS(s)];
  //   }
  //   return res;
  // }

  // private int flipS(int s) {
  //   int left = (s>>3)&7;
  //   int right = s&7;
  //   return (right<<3) | left;
  // }
}