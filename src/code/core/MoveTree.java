package code.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import code.world.State;
import code.world.World;

public abstract class MoveTree {
  public static void drawMoveTree() {
    List<TreeNode> visitedStates = new ArrayList<>();
    Map<Integer, List<TreeNode>> layers = new HashMap<>();
    visitedStates.add(new TreeNode(World.Player.getState(), 0));
    layers.put(0, List.of(visitedStates.get(0)));
    
    for (int i = 0; i < visitedStates.size(); i++) {
      TreeNode node = visitedStates.get(i);
      for (int j = 0; j < World.Setup.getNumActors(); j++) {
        State next = World.Player.getBestNextStateFromActor(node.state, j);
        
        if (next != null && next.getActors().length > 0) {
          TreeNode child = new TreeNode(next, node.layer+1);
          if (!visitedStates.contains(child)) {
            visitedStates.add(child);
            layers.putIfAbsent(child.layer, new ArrayList<>());
            layers.get(child.layer).add(child);
          }
          else child = visitedStates.get(visitedStates.indexOf(child));
          
          node.children.add(child);
        }
      }
    }

    for (int i = 0; i < visitedStates.size(); i++) {
      System.out.println(visitedStates.get(i));
    }
  }

  private static class TreeNode {
    public final State state;
    public final int encoded;
    public final int layer;
    public final List<TreeNode> children = new ArrayList<>();

    public TreeNode(State state, int layer) {
      this.state = state;
      this.encoded = State.Encoder.encode(state);
      this.layer = layer;
    }

    public boolean equals(Object o) {
      if (!(o instanceof TreeNode n)) return false;

      return this.encoded==n.encoded;
    }

    public String toString() {
      String buffer = "%"+(State.Encoder.size()-1)+"s";
      return layer + ": " + String.format(buffer, Integer.toBinaryString(encoded)).replace(' ', '0');
    }
  }
}
