package code.world.actors;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;

import code.math.MathHelp;
import code.world.State;

public class PrisonerDilemmee extends Actor {

  private static final int SIZE = 1;

  public static final int size() {return SIZE;}

  private static final int TAKE_CASH_MASK = 0b01;

  private static final double cashValue = 100;
  
  protected final String character;

  protected final boolean takeCash;

  /**
   * Creates a new {@code Actor} with given parameters.
   */
  public PrisonerDilemmee(State state, int actorNum, int encoded) {
    super(state, actorNum, encoded);
    this.takeCash = MathHelp.intToBoolean(encoded&TAKE_CASH_MASK);
    this.character = takeCash ? "T" : "L";
  }

  @Override
  public double exitReward() {
    return takeCash ? 
      state.allActorsCond((a) -> ((PrisonerDilemmee)a).takeCash) ? 0 : cashValue: 
      state.allActorsCond((a) -> !((PrisonerDilemmee)a).takeCash) ? cashValue/2 : 0;
  }

  @Override
  public boolean exitCondition() {
    return true;
  }

  @Override
  protected Actor replace(int encoded) {
    return new PrisonerDilemmee(this.state, this.actorNum, encoded);
  }

  @Override
  public void draw(Graphics2D g, int x, int y, int size) {
    super.draw(g, x, y, size);
    
    g.setFont(new Font(Font.MONOSPACED, Font.BOLD, size/2));
    FontMetrics met = g.getFontMetrics();

    g.drawString(character, x-met.stringWidth(character)/2, y+met.getDescent()+met.getLeading());
  }
  
}
