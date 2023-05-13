package code.world.actors;

import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;

import code.core.Core;
import code.math.MathHelp;
import code.world.State;

public class StagHunter extends Actor {

  private static final int SIZE = 2;

  public static final int size() {return SIZE;}

  private static final int HUNT_STAG_MASK = 0b01;
  private static final int HOLD_OTHER_MASK = 0b10;

  private static final double hareValue = 3;
  private static final double stagValue = 4;
  
  protected final String character;

  protected final boolean huntStag;
  protected final boolean holdOther;

  /**
   * Creates a new {@code Actor} with given parameters.
   */
  public StagHunter(State state, int actorNum, int encoded) {
    super(state, actorNum, encoded);
    this.huntStag = MathHelp.intToBoolean(encoded&HUNT_STAG_MASK);
    this.holdOther = MathHelp.intToBoolean(encoded&HOLD_OTHER_MASK);
    this.character = huntStag ? "S" : "H";
  }

  @Override
  public double exitReward() {
    return huntStag ? state.allActorsCond((a) -> (a.encoded&HUNT_STAG_MASK)==1) ? stagValue : 0 : hareValue;
  }

  @Override
  public boolean exitCondition() {
    return state.allActorsCond((a) -> !((StagHunter)a).holdOther);
  }

  @Override
  protected Actor replace(int encoded) {
    return new StagHunter(this.state, this.actorNum, encoded);
  }

  @Override
  public void draw(Graphics2D g, int x, int y, int size) {
    g.setStroke(new BasicStroke(Core.WINDOW.screenHeight()/64));
    g.setColor(colourText);
    
    int objY = Core.WINDOW.screenHeight()/2;
    
    if (holdOther) g.drawLine(x, y, x, objY-(y-objY));

    super.draw(g, x, y, size);
    
    g.setFont(new Font(Font.MONOSPACED, Font.BOLD, size/2));
    FontMetrics met = g.getFontMetrics();

    g.drawString(character, x-met.stringWidth(character)/2, y+met.getDescent()+met.getLeading());
  }
  
}
