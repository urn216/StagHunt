package code.mdp;

public record ClassicMDP(double gamma, int numStates, int numActions, double[][][] T, double[][][] R) implements MDP {}
