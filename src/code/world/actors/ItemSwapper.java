package code.world.actors;

import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;

import code.core.Core;
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
    this.holdLeft = MathHelp.intToBoolean(encoded&HOLD_LEFT_MASK);
    this.holdOther = MathHelp.intToBoolean(encoded&HOLD_OTHER_MASK);
    this.holdRight = MathHelp.intToBoolean(encoded&HOLD_RIGHT_MASK);
    this.character = "" + (char)(actorNum + 65);
  }

  @Override
  public double exitReward() {
    double reward = 0;
    if (holdRight) {
      reward += actorNum == 1 ? ownValue : otherValue;
    }
    if (holdLeft) {
      reward += actorNum == 0 ? ownValue : otherValue;
    }
    return reward;
  }

  @Override
  public boolean exitCondition() {
    return state.allActorsCond((a) -> !((ItemSwapper)a).holdOther)
    &&     state.atMostOneActorCond((a) -> ((ItemSwapper)a).holdLeft)
    &&     state.atMostOneActorCond((a) -> ((ItemSwapper)a).holdRight);
  }

  @Override
  protected Actor replace(int encoded) {
    return new StagHunter(this.state, this.actorNum, encoded);
  }

  // @Override
  // public State toggleBool(int bool) {
  //   if (bool == 1 || this.state.allButMeCond((a) -> (a.encoded&(1<<bool))==0, this.actorNum)) return super.toggleBool(bool);
  //   return null;
  // }

  @Override
  public void draw(Graphics2D g, int width, int height) {

    int x = (int)(this.state.getActorPs()[actorNum].x*height+width/2);
    int y = (int)(this.state.getActorPs()[actorNum].y*height+height/2);
    int size = height/8;

    int objX = (int)(this.state.getActorObjPs()[actorNum].x*height+width/2);
    int objY = (int)(this.state.getActorObjPs()[actorNum].y*height+height/2);

    g.setStroke(new BasicStroke(Core.WINDOW.screenHeight()/64));
    g.setColor(colourText);

    if (actorNum == 0 ? holdLeft : holdRight) g.drawLine(x, y, objX, objY);
    if (actorNum != 0 ? holdLeft : holdRight) g.drawLine(
      x, 
      y, 
      (int)(this.state.getActorObjPs()[(actorNum-1+World.Setup.getNumActors())%World.Setup.getNumActors()].x*height+width/2), 
      (int)(this.state.getActorObjPs()[(actorNum-1+World.Setup.getNumActors())%World.Setup.getNumActors()].y*height+height/2)
    );
    if (holdOther) g.drawLine(
      x, 
      y, 
      (int)(this.state.getActorPs()[(actorNum+1)%World.Setup.getNumActors()].x*height+width/2), 
      (int)(this.state.getActorPs()[(actorNum+1)%World.Setup.getNumActors()].y*height+height/2)
    );

    Actor.drawCircle(g, colourBody, colourText, x, y, size);
    Actor.drawCircle(g, colourBody, colourText, objX, objY, size/2);

    g.setFont(new Font(Font.MONOSPACED, Font.BOLD, size/2));
    FontMetrics met = g.getFontMetrics();

    g.drawString(character, x-met.stringWidth(character)/2, y+met.getDescent()+met.getLeading());
  }
  
}
