package code.core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import mki.io.FileIO;

import code.world.State;
import code.world.World;

public abstract class MoveTree {

  private static final Color blue = new Color(50, 50, 130);
  private static final Color green = new Color(50, 130, 50);
  private static final Color yellow = new Color(130, 130, 50);
  private static final Color grey = new Color(130, 130, 130);

  /**
   * Draws a network of 'optimal' {@code State} instances branching out from the {@code World}'s 
   * current {@code State} to an output image under the name of "{@code tree.png}".
   * 
   * @param stateSize the size in pixels of each node in the tree
   */
  public static void drawMoveTree(int stateSize) {

    TreeNode[][] layers = initTree();

    int xStep = 2 * stateSize;
    int width  = xStep * (layers.length+1);
    int height = xStep * Stream.of(layers).mapToInt(l->{return l.length;}).max().getAsInt();

    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = img.createGraphics();

    doOnEachNode(layers, stateSize, xStep, height, (node, x)->{});
    doOnEachNode(layers, stateSize, xStep, height, (node, x)->{
      node.drawArrows(g, stateSize);
    });
    doOnEachNode(layers, stateSize, xStep, height, (node, x)->{
      node.drawState(g, node.exitState ? node.children.isEmpty() ? green : yellow : x==0 ? blue : grey, stateSize);
    });

    FileIO.writeImage("../tree.png", img);
  }

  /**
   * Creates a tree representing all the possible 'optimal' actions branching from a starting node
   * 
   * @return a 2D array containing the layers of unique nodes in a tree
   */
  private static TreeNode[][] initTree() {
    List<TreeNode> visitedStates = new ArrayList<>();
    List<List<TreeNode>> layers = new ArrayList<>();
    visitedStates.add(new TreeNode(World.Player.getState(), 0));
    layers.add(List.of(visitedStates.get(0)));
    
    for (int i = 0; i < visitedStates.size(); i++) {
      TreeNode node = visitedStates.get(i);
      for (int j = 0; j < World.Setup.getNumActors(); j++) {
        State next = World.Player.getBestNextStateFromActor(node.state, j);
        
        if (next != null && next.getActors().length > 0) {
          TreeNode child = new TreeNode(next, node.layer+1);
          if (!visitedStates.contains(child)) {
            visitedStates.add(child);
            if (layers.size()==child.layer) layers.add(new ArrayList<>());
            layers.get(child.layer).add(child);
          }
          else child = visitedStates.get(visitedStates.indexOf(child));
          
          node.children.add(child);
        } else node.exitState = true;
      }
    }

    // for (int i = 0; i < visitedStates.size(); i++) {
    //   System.out.println(visitedStates.get(i));
    // }

    return layers.stream().map(l->l.toArray(new TreeNode[0])).toArray(TreeNode[][]::new);
  }

  private static void doOnEachNode(TreeNode[][] layers, int stateSize, int xStep, int height, BiConsumer<TreeNode, Integer> consumer) {
    for (int x = 0; x < layers.length; x++) {
      TreeNode[] layer = layers[x];
      int yStep = height/(layer.length+1);
      for (int y = 0; y < layer.length; y++) {
        TreeNode node = layer[y];
        node.x = (x+1)*xStep;
        node.y = (y+1)*yStep;
        consumer.accept(node, x);
      }
    }
  }

  /**
   * A node in our Movement Tree. 
   * Mutable in all the wrong ways. Definitely not for external use.
   * 
   * @author William Kilty
   */
  private static class TreeNode {
    public final State state;
    public final int encoded;
    public final int layer;
    public final List<TreeNode> children = new ArrayList<>();

    public boolean exitState = false;

    public int x = 0;
    public int y = 0;

    public TreeNode(State state, int layer) {
      this.state = state;
      this.encoded = State.Encoder.encode(state);
      this.layer = layer;
    }

    /**
     * Draws the arrows connecting this {@code TreeNode} to others onto a {@code Graphics2D} object 
     * with a given {@code size}
     * 
     * @param g the {@code Graphics2D} object to draw to
     * @param size the stroke size to use for the arrow
     */
    private void drawArrows(Graphics2D g, int size) {
      g.setStroke(new BasicStroke(size/32));

      g.setColor(Color.black); //TODO not black
      for (int i = 0; i < children.size(); i++) {
        g.drawLine(x, y, children.get(i).x, children.get(i).y);
      }

      g.setStroke(new BasicStroke(1));
    }

    /**
     * Draws the {@code State} belonging to this {@code TreeNode} to a {@code Graphics2D} object 
     * with a given background colour and {@code size}
     * 
     * @param g the {@code Graphics2D} object to draw to
     * @param c the {@code Color} to paint the backround disk of {@code s}
     * @param size the diameter in pixels to draw {@code s} at
     */
    private void drawState(Graphics2D g, Color c, int size) {
      g.setStroke(new BasicStroke(size/32));

      g.setColor(c);
      g.fillOval(x-size/2, y-size/2, size, size);

      g.setColor(c.brighter());
      g.drawOval(x-size/2, y-size/2, size, size);

      g.setStroke(new BasicStroke(1));

      state.draw(g, x-size/2, y-size/2, size, size);
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof TreeNode n)) return false;

      return this.encoded==n.encoded;
    }

    @Override
    public String toString() {
      String buffer = "%"+(State.Encoder.size()-1)+"s";
      return layer + ": " + String.format(buffer, Integer.toBinaryString(encoded)).replace(' ', '0');
    }
  }
}
