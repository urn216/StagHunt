package code.core;

import java.util.Arrays;

public abstract class Core {

  private static final double COST_OF_LIVING = -0.1;

  private static MDP mdp = new MDP(
    0.9, 3, 3, 
    new double[][][] { // Transition Matrix
      { // hare hunting
        { // toggle mode
          0, 1, 0
        },
        { // pass
          1, 0, 0
        },
        { // exit
          0, 0, 1
        }
      },
      { // stag hunting
        { // toggle mode
          1, 0, 0
        },
        { // pass
          0, 1, 0
        },
        { // exit
          0, 0, 1
        }
      },
      { // exited
        { // toggle mode
          0, 0, 0
        },
        { // pass
          0, 0, 0
        },
        { // exit
          0, 0, 0
        }
      }
    }, 
    new double[][] { // Reward Matrix
      { // hare hunting
        COST_OF_LIVING, COST_OF_LIVING, 3
      },
      { // stag hunting
        COST_OF_LIVING, COST_OF_LIVING, 4
      },
      { // exited
        0, 0, 0
      }
    }
  );

  private static int numIterations = 3;

  public static void main(String[] args) {
    ValueIterator vi = new ValueIterator(mdp, numIterations);

    System.out.println(Arrays.toString(vi.doValueIteration()));
  }
}