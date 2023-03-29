package code.core;

import java.util.Arrays;

import code.math.ValueIterator;
import code.mdp.MDP;
import code.mdp.OOMDP;

import code.world.State;

public abstract class Core {

  private static final double COST_OF_LIVING  = -0.1;
  private static final double COST_OF_FAILING = -10;

  private static MDP mdp = new OOMDP(
    0.9, State.numberOfStates(), 
    null, 
    null, 
    null
  );

  private static int numIterations = 10;

  public static void main(String[] args) {
    ValueIterator vi = new ValueIterator(mdp, numIterations);

    System.out.println(Arrays.toString(vi.doValueIteration()));
  }
}

//////////                              ////////
///////// Hard-Coded One-Man Stag Hunt /////////
////////                              //////////
//
// private static MDP mdp = new MDP(
//     0.9, 3, 3, 
//     new double[][][] { // Transition Matrix
//       { // hare hunting
//         { // toggle mode
//           0, 1, 0
//         },
//         { // pass
//           1, 0, 0
//         },
//         { // exit
//           0, 0, 1
//         }
//       },
//       { // stag hunting
//         { // toggle mode
//           1, 0, 0
//         },
//         { // pass
//           0, 1, 0
//         },
//         { // exit
//           0, 0.01, 0.99
//         }
//       },
//       { // exited
//         { // toggle mode
//           0, 0, 0
//         },
//         { // pass
//           0, 0, 0
//         },
//         { // exit
//           0, 0, 0
//         }
//       }
//     }, 
//     new double[][][] { // Reward Matrix
//       { // hare hunting
//         { // toggle mode
//           COST_OF_FAILING, COST_OF_LIVING, COST_OF_FAILING
//         },
//         { // pass
//           COST_OF_LIVING, COST_OF_FAILING, COST_OF_FAILING
//         },
//         { // exit
//           COST_OF_FAILING, COST_OF_FAILING, 3
//         }
//       },
//       { // stag hunting
//         { // toggle mode
//           COST_OF_LIVING, COST_OF_FAILING, COST_OF_FAILING
//         },
//         { // pass
//           COST_OF_FAILING, COST_OF_LIVING, COST_OF_FAILING
//         },
//         { // exit
//           COST_OF_FAILING, COST_OF_FAILING, 4
//         }
//       },
//       { // exited
//         { // toggle mode
//           0, 0, 0
//         },
//         { // pass
//           0, 0, 0
//         },
//         { // exit
//           0, 0, 0
//         }
//       }
//     }
//   );