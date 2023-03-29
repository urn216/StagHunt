package code.mdp;

public interface MDP {
  public double gamma(); 
  public int numStates(); 
  public int numActions();
  public double[][][] T();
  public double[][][] R();
}
