package code.mdp;

import code.world.Action;
import code.world.State;

public class OOMDP implements MDP {
  private final MDP underlay;

  private static final double COST_OF_LIVING  = -0;
  private static final double COST_OF_FAILING = Double.NEGATIVE_INFINITY;

  public OOMDP(double gamma, Action[] actions, int actorRecieving, int actorDoing) {
    int sTot = State.Encoder.numberOfStates();
    int aTot = actions.length;
    double[][][] T = new double[sTot][aTot][sTot];
    double[][][] R = new double[sTot][aTot][sTot];

    for (int s = 0; s < sTot; s++) {
      State state = State.Encoder.decode(s);

      // Find all the valid actions this actor can perform from this state
      for (int a = 0; a < actions.length; a++) {
        //get the destination State
        State nextState = null;

        try {
          nextState = actions[a].act(state.getActors()[actorDoing]);
        } catch (Exception e) {}

        if (nextState == null) continue;
        //

        int sPrime = State.Encoder.encode(nextState);

        //exiting
        if (sPrime == State.Encoder.exitStateEncoded()) {
          //claim reward if possible
          T[s][a][sPrime] =  1;
          R[s][a][sPrime] = state.getActors()[actorDoing].exitCondition() ?
          state.getActors()[actorRecieving].exitReward():
          COST_OF_FAILING;
          continue;
        }

        // Probability of success constant for now
        T[s][a][sPrime] = 1;
        // If we exited, claim reward, otherwise keep chugging
        R[s][a][sPrime] = COST_OF_LIVING;
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
