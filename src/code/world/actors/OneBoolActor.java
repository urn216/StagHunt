package code.world.actors;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import code.world.State;
import code.world.World;

public class OneBoolActor extends Actor {

  private static final int SIZE = 1;

  public static final int size() {return SIZE;}

  public static int[] ACTOR_0_VALS = {1, 2, 3, 4};
  public static int[] ACTOR_1_VALS = {1, 2, 3, 4};

  protected final int value;

  protected final String character;

  public OneBoolActor(State state, int actorNum, int encoded) {
    super(state, actorNum, encoded);
    this.value = actorNum == 0 ? ACTOR_0_VALS[state.getDebugEncoding()] : ACTOR_1_VALS[state.getDebugEncoding()];
    this.character = ""+this.value;
  }

  @Override
  public double exitReward() {
    return this.value;
  }

  @Override
  public boolean exitCondition() {
    return true;
  }

  @Override
  public void draw(Graphics2D g, int x, int y, int width, int height) {

    x += (int)(this.state.getActorPs()[actorNum].x*width+width/2);
    y += (int)(this.state.getActorPs()[actorNum].y*height+height/2);
    int size = (int)(height*World.Visualiser.ACTOR_CIRC_RADIUS*2);

    Actor.drawCircle(g, colourBody, colourText, x, y, size);
    
    g.setFont(new Font(Font.MONOSPACED, Font.BOLD, size/2));
    FontMetrics met = g.getFontMetrics();

    g.drawString(character, x-met.stringWidth(character)/2, y+met.getDescent()+met.getLeading());
  }
}
