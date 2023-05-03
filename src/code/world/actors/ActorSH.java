package code.world.actors;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;

import code.math.MathHelp;
import code.world.State;

public class ActorSH extends Actor {

  private static final int HUNT_STAG_MASK = 1;
  
  protected final String character;

  protected final boolean huntStag;

  /**
   * Creates a new {@code Actor} with given parameters.
   */
  public ActorSH(State state, int actorNum, int encoded) {
    super(state, actorNum, encoded);
    this.huntStag = MathHelp.intToBoolean(encoded&HUNT_STAG_MASK);
    this.character = huntStag ? "S" : "H";
  }

  @Override
  public double exitReward() {
    return huntStag ? state.allActorsCond((a) -> (a.encoded&HUNT_STAG_MASK)==1) ? 4 : 0 : 3;
  }

  @Override
  public boolean exitCondition() {
    return true;
  }

  @Override
  protected Actor replace(int encoded) {
    return new ActorSH(this.state, this.actorNum, encoded);
  }

  @Override
  public void draw(Graphics2D g, int x, int y, int size) {
    super.draw(g, x, y, size);
    
    g.setFont(new Font(Font.MONOSPACED, Font.BOLD, size/2));
    FontMetrics met = g.getFontMetrics();

    g.drawString(character, x-met.stringWidth(character)/2, y+met.getDescent()+met.getLeading());
  }
  
}
