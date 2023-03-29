package code.mdp;

import code.world.Action;
import code.world.State;

public class OOMDP implements MDP {
  private final MDP underlay;

  private static final double COST_OF_LIVING  = -0.1;
  // private static final double COST_OF_FAILING = -10;

  public OOMDP(double gamma, Action[] actions, int actor) {
    int sTot = State.numberOfStates();
    int aTot = actions.length;
    double[][][] T = new double[sTot][aTot][sTot];
    double[][][] R = new double[sTot][aTot][sTot];

    for (int s = 0; s < sTot; s++) {
      State state = State.decode(s);

      // Find all the valid actions this actor can perform from this state
      for (int a = 0; a < actions.length-1; a++) {
        State nextState = null;
        try {
          nextState = actions[a].act(state.getActors()[actor]);
        } catch (Exception e) {continue;}

        if (nextState == null) continue;

        int sPrime = State.encode(nextState);

        T[s][a][sPrime] = 1;
        R[s][a][sPrime] = COST_OF_LIVING;
      }

      // Exit if possible, and collect rewards
      try {
        State nextState = actions[actions.length-1].act(state.getActors()[actor]);
        int sPrime = State.encode(nextState);
        T[s][actions.length-1][sPrime] = 1;
        // If we exited, claim reward
        if (sPrime == State.numberOfStates()-1) R[s][actions.length-1][sPrime] = state.getActors()[actor].exitReward();
        // Otherwise we keep chugging
        else R[s][actions.length-1][sPrime] = COST_OF_LIVING;
      } catch (Exception e) {continue;}
    }

    underlay = new ClassicMDP(gamma, sTot, aTot, T, R);
  }

  public int numActions() {
    return underlay.numActions();
  }

  public double gamma() {
    return underlay.gamma();
  }

  public int numStates() {
    return underlay.numStates();
  }

  public double[][][] T() {
    return underlay.T();
  }

  public double[][][] R() {
    return underlay.R();
  }
}
