package code.world.actors;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.function.BiFunction;

import code.world.State;
import code.world.World;
import mki.math.MathHelp;

public class TwoBoolActor extends Actor {

  private static final int SIZE = 2;

  public static final int size() {return SIZE;}

  public static int[] ACTOR_0_VALS = {1, 2, 3, 4};
  public static int[] ACTOR_1_VALS = {1, 2, 3, 4};

  public static BiFunction<State, Integer, Boolean> exitCond = (state, actorNum) -> state.allActorsCond((a) -> !((TwoBoolActor)a).holding);

  protected final int value;

  protected final boolean holding;

  protected final String character;

  public TwoBoolActor(State state, int actorNum, int encoded) {
    super(state, actorNum, encoded);
    this.holding = MathHelp.intToBoolean(encoded&0b10);
    int index = (state.getDebugEncoding()&1)+2*((state.getDebugEncoding()>>2)&1);
    this.value = actorNum == 0 ? ACTOR_0_VALS[index] : ACTOR_1_VALS[index];
    this.character = ""+this.value;
  }

  public boolean isHolding() {
    return holding;
  }

  @Override
  public double exitReward() {
    return this.value;
  }

  @Override
  public boolean exitCondition() {
    return exitCond.apply(state, actorNum);
  }

  @Override
  public void draw(Graphics2D g, int x, int y, int width, int height) {

    x += (int)(this.state.getActorPs()[actorNum].x*width+width/2);
    y += (int)(this.state.getActorPs()[actorNum].y*height+height/2);
    int size = (int)(height*World.Visualiser.getActorCircRadius()*2);

    Actor.drawCircle(g, colourBody, colourText, x, y, size);
    
    g.setFont(new Font(Font.MONOSPACED, Font.BOLD, size/2));
    FontMetrics met = g.getFontMetrics();

    g.drawString(character, x-met.stringWidth(character)/2, y+met.getDescent()+met.getLeading());
  }
}
