package code.mdp;

import code.world.Action;

public record OOMDP(double gamma, int numStates, Action[] actions, double[][][] T, double[][][] R) implements MDP {

  public int numActions() {
    return actions.length;
  }
}
