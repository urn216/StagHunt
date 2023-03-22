package code.core;

public record MDP(double gamma, int numStates, int numActions, double[][][] T, double[][] R) {}
