package code.world.actors;

import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;

import code.core.Core;
import code.math.MathHelp;
import code.world.State;

public class ActorSwap extends Actor {

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
  public ActorSwap(State state, int actorNum, int encoded) {
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
    return state.allActorsCond((a) -> !((ActorSwap)a).holdOther)
    &&     state.atMostOneActorCond((a) -> ((ActorSwap)a).holdLeft)
    &&     state.atMostOneActorCond((a) -> ((ActorSwap)a).holdRight);
  }

  @Override
  protected Actor replace(int encoded) {
    return new ActorSH(this.state, this.actorNum, encoded);
  }

  @Override
  public void draw(Graphics2D g, int x, int y, int size) {
    g.setStroke(new BasicStroke(Core.WINDOW.screenHeight()/64));
    g.setColor(colourText);

    int leftX = Core.WINDOW.screenWidth()/2-(Core.WINDOW.screenHeight()/4);
    int rightX = Core.WINDOW.screenWidth()/2+(Core.WINDOW.screenHeight()/4);
    int objY = Core.WINDOW.screenHeight()/2;

    if (holdLeft) g.drawLine(x, y, leftX, objY);
    if (holdRight) g.drawLine(x, y, rightX, objY);
    if (holdOther) g.drawLine(x, y, x, objY-Math.abs(y-objY));

    super.draw(g, x, y, size);
    super.draw(g, this.actorNum==0 ? leftX : rightX, objY, size/2);

    g.setFont(new Font(Font.MONOSPACED, Font.BOLD, size/2));
    FontMetrics met = g.getFontMetrics();

    g.drawString(character, x-met.stringWidth(character)/2, y+met.getDescent()+met.getLeading());
  }
  
}
