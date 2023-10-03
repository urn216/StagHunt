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
import code.world.actors.Actor;
import code.world.actors.OneBoolActor;
import code.world.actors.TwoBoolActor;

public abstract class MoveTree {

  private static final Color red    = new Color(200, 50 , 50 );
  private static final Color yellow = new Color(200, 200, 50 );
  private static final Color green  = new Color(50 , 200, 50 );
  private static final Color blue   = new Color(50 , 50 , 200);
  private static final Color grey   = new Color(100, 100, 100);

  /**
   * Draws a network of 'optimal' {@code State} instances branching out from the {@code World}'s 
   * current {@code State} to an output image under the name of "{@code tree.png}".
   * 
   * @param stateSize the size in pixels of each node in the tree
   */
  public static void drawMoveTree(String filename, int stateSize) {

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

    FileIO.writeImage("../screenshots/"+filename + (filename.endsWith(".png") ? "" : ".png"), img);
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
          node.childInt.add(j);
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
    public final int layer;
    public final List<TreeNode> children = new ArrayList<>();
    public final List<Integer>  childInt = new ArrayList<>();

    public boolean exitState = false;

    public int x = 0;
    public int y = 0;

    public TreeNode(State state, int layer) {
      this.state = state;
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

      for (int i = 0; i < children.size(); i++) {
        TreeNode next = children.get(i);
        g.setColor(next.state.getActors()[childInt.get(i)].getBodyColour());
        g.drawLine(x, y, next.x, next.y);
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

      return this.state.getDebugEncoding()==n.state.getDebugEncoding();
    }

    @Override
    public String toString() {
      String buffer = "%"+(State.Encoder.size()-1)+"s";
      return layer + ": " + String.format(buffer, Integer.toBinaryString(state.getDebugEncoding())).replace(' ', '0');
    }
  }

  //           //
  // MOVE GRID //
  //           //

  public static void drawMoveGrid(String filename, double gamma, int stateSize, Class<? extends Actor> actorType) throws Exception {
    World.Setup.setActorType(actorType);
    World.Setup.setNumActors(2);
    World.Setup.setVIMode(World.VI_MODE_COMP);
    World.Setup.setGamma(gamma);

    World.Visualiser.setActorRingRadius(0.3);
    World.Visualiser.setActorCircRadius(0.23);
    World.Visualiser.setOffset45(true);

    int buffer = stateSize/8;

    BufferedImage img = new BufferedImage((stateSize*2+buffer)*12-buffer, (stateSize*2+buffer)*12-buffer, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = img.createGraphics();

    for (int i = 0; i < 6; i++) {
      int aa = 1;
      int ab = i/2 + 2;
      int ac = ((i+3)%6)/2 + 2;
      int ad = ((5-i)%3) + 2;

      actorType.getDeclaredField("ACTOR_0_VALS").set(null, new int[]{aa, ab, ac, ad});

      for (int j = 0; j < 24; j++) {
        int ba = j/6+1;
        int bb = (j%6)/2 + 2;
        if (bb==ba) bb = 1;
        int bc = (((j%6)+3)%6)/2 + 2;
        if (bc==ba) bc = 1;
        int bd = ((5-(j%6))%3) + 2;
        if (bd==ba) bd = 1;

        actorType.getDeclaredField("ACTOR_1_VALS").set(null, new int[]{ba, bb, bc, bd});

        World.Setup.initialiseMDPs(); 
        World.Setup.doVI();

        drawGridMDP(g, stateSize, (stateSize*2+buffer)*(j%12), (stateSize*2+buffer)*(j/12 + i*2));
      }
    }

    FileIO.writeImage(filename, img);
  }

  private static void drawGridMDP(Graphics2D g, int stateSize, int x, int y) {
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        State s = State.Encoder.decode(i + (j<<World.Setup.getActorSize()));
        World.Player.setState(s);

        Color c = World.Player.getBestNextStateFromActor(s, 0).getActors().length == 0 ? red : grey;
        c = World.Player.getBestNextStateFromActor(s, 1).getActors().length == 0 ? c == red ? green : blue : c;

        g.setStroke(new BasicStroke(stateSize/32));
    
        g.setColor(c);
        g.fillRect(x+i*stateSize, y+j*stateSize, stateSize, stateSize);
    
        g.setColor(c.brighter());
        g.drawRect(x+i*stateSize, y+j*stateSize, stateSize, stateSize);
    
        g.setStroke(new BasicStroke(1));
        s.draw(g,  x+i*stateSize, y+j*stateSize, stateSize, stateSize);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    drawMoveGrid("StagHunt/screenshots/AllGames_0_NoDiscount.png", 1, 360, OneBoolActor.class);
    drawMoveGrid("StagHunt/screenshots/AllGames_1_Discount.png", 0.99, 360, OneBoolActor.class);
    drawMoveGrid("StagHunt/screenshots/AllGames_2_Holds.png", 0.99, 360, TwoBoolActor.class);
    TwoBoolActor.exitCond = (state, actorNum) -> state.allButMeCond((a) -> !((TwoBoolActor)a).isHolding(), actorNum);
    drawMoveGrid("StagHunt/screenshots/AllGames_3_StrongHolds.png", 0.99, 360, TwoBoolActor.class);
    TwoBoolActor.exitCond = (state, actorNum) -> state.allButMeCond((a) -> !((TwoBoolActor)a).isHolding(), (actorNum+1)%2);
    drawMoveGrid("StagHunt/screenshots/AllGames_4_ReducedFreedom.png", 0.99, 360, TwoBoolActor.class);
  }
}
