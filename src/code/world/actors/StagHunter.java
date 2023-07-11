package code.world.actors;

import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;

import code.world.State;
import code.world.World;
import mki.math.MathHelp;

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
  public void draw(Graphics2D g, int width, int height) {

    int x = (int)(this.state.getActorPs()[actorNum].x*height+width/2);
    int y = (int)(this.state.getActorPs()[actorNum].y*height+height/2);
    int size = height/8;

    g.setStroke(new BasicStroke(height/64));
    g.setColor(colourText);
    
    if (holdOther) g.drawLine(
      x, 
      y, 
      (int)(this.state.getActorPs()[(actorNum+1)%World.Setup.getNumActors()].x*height+width/2), 
      (int)(this.state.getActorPs()[(actorNum+1)%World.Setup.getNumActors()].y*height+height/2)
    );

    Actor.drawCircle(g, colourBody, colourText, x, y, size);
    
    g.setFont(new Font(Font.MONOSPACED, Font.BOLD, size/2));
    FontMetrics met = g.getFontMetrics();

    g.drawString(character, x-met.stringWidth(character)/2, y+met.getDescent()+met.getLeading());
  }
  
}
