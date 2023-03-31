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
      for (int a = 0; a < actions.length; a++) {
        //get the destination State
        State nextState = null;

        try {
          nextState = actions[a].act(state.getActors()[actor]);
        } catch (Exception e) {}

        if (nextState == null) continue;
        //

        int sPrime = State.encode(nextState);

        // Probability of success constant for now
        T[s][a][sPrime] = 1;
        // If we exited, claim reward, otherwise keep chugging
        R[s][a][sPrime] = sPrime == State.exitStateEncoded() ? 
          state.getActors()[actor].exitReward() :
          COST_OF_LIVING;
      }
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
