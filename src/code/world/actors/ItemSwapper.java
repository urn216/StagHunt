package code.world.actors;

import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;

import code.world.State;
import code.world.World;
import mki.math.MathHelp;

public class ItemSwapper extends Actor {

  private static final int SIZE = 3;

  public static final int size() {return SIZE;}

  private static final int HOLD_LEFT_MASK  = 0b001;
  private static final int HOLD_OTHER_MASK = 0b010;
  private static final int HOLD_RIGHT_MASK = 0b100;

  private static final double ownValue = 2;
  private static final double otherValue = 4;
  
  protected final String character;

  protected final boolean holdLeft;
  protected final boolean holdOther;
  protected final boolean holdRight;

  /**
   * Creates a new {@code Actor} with given parameters.
   */
  public ItemSwapper(State state, int actorNum, int encoded) {
    super(state, actorNum, encoded);
    this.holdLeft  = MathHelp.intToBoolean(encoded&HOLD_LEFT_MASK );
    this.holdOther = MathHelp.intToBoolean(encoded&HOLD_OTHER_MASK);
    this.holdRight = MathHelp.intToBoolean(encoded&HOLD_RIGHT_MASK);
    this.character = "" + (char)(actorNum + 'A');
  }

  @Override
  public double exitReward() {
    double reward = 0;
    if (holdRight) {
      reward += state.rightOfMeCond((a) -> ((ItemSwapper)a).holdLeft, actorNum) ? 0 : otherValue;
    }
    if (holdLeft) {
      reward += state.leftOfMeCond((a) -> ((ItemSwapper)a).holdRight, actorNum) ? 0 : ownValue;
    }
    return reward;
  }

  @Override
  public boolean exitCondition() {
    return !holdOther 
    &&  state.rightOfMeCond((a) -> !((ItemSwapper)a).holdOther, actorNum)
    && (state.leftOfMeCond ((a) -> !((ItemSwapper)a).holdRight, actorNum) || !holdLeft )
    && (state.rightOfMeCond((a) -> !((ItemSwapper)a).holdLeft , actorNum) || !holdRight);
  }

  // @Override
  // public State toggleBool(int bool) {
  //   if (bool == 1 || this.state.allButMeCond((a) -> (a.encoded&(1<<bool))==0, this.actorNum)) return super.toggleBool(bool);
  //   return null;
  // }

  @Override
  public void draw(Graphics2D g, int x, int y, int width, int height) {

    int thisX = x + (int)(this.state.getActorPs()[actorNum].x*width+width/2);
    int thisY = y + (int)(this.state.getActorPs()[actorNum].y*height+height/2);
    int size = (int)(height*World.Visualiser.getActorCircRadius()*2);

    int objX = x + (int)(this.state.getActorObjPs()[actorNum].x*width+width/2);
    int objY = y + (int)(this.state.getActorObjPs()[actorNum].y*height+height/2);

    g.setStroke(new BasicStroke(height/64));
    g.setColor(colourText);

    if (holdLeft)  g.drawLine(thisX, thisY, objX, objY);
    if (holdRight) g.drawLine(
      thisX, 
      thisY, 
      x + (int)(this.state.getActorObjPs()[(actorNum+1)%World.Setup.getNumActors()].x*width+width/2), 
      y + (int)(this.state.getActorObjPs()[(actorNum+1)%World.Setup.getNumActors()].y*height+height/2)
    );
    if (holdOther) g.drawLine(
      thisX, 
      thisY, 
      x + (int)(this.state.getActorPs()[(actorNum-1+World.Setup.getNumActors())%World.Setup.getNumActors()].x*width+width/2), 
      y + (int)(this.state.getActorPs()[(actorNum-1+World.Setup.getNumActors())%World.Setup.getNumActors()].y*height+height/2)
    );

    Actor.drawCircle(g, colourBody, colourText, thisX, thisY, size);
    Actor.drawCircle(g, colourBody, colourText, objX, objY, size/2);

    g.setFont(new Font(Font.MONOSPACED, Font.BOLD, size/2));
    FontMetrics met = g.getFontMetrics();

    g.drawString(character, thisX-met.stringWidth(character)/2, thisY+met.getDescent()+met.getLeading());
  }
  
}
