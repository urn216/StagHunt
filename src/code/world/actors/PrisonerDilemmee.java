package code.world.actors;

import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;

import code.world.State;
import code.world.World;
import mki.math.MathHelp;

public class PrisonerDilemmee extends Actor {

  private static final int SIZE = 2;

  public static final int size() {return SIZE;}

  private static final int     DEFECT_MASK = 0b01;
  private static final int HOLD_OTHER_MASK = 0b10;

  private static final double fullSentence = 0;
  private static final double partSentence = 2;
  private static final double reduSentence = 4;
  private static final double zeroSentence = 6;
  
  protected final String character;

  protected final boolean defect;
  protected final boolean holdOther;

  /**
   * Creates a new {@code Actor} with given parameters.
   */
  public PrisonerDilemmee(State state, int actorNum, int encoded) {
    super(state, actorNum, encoded);
    this.defect = !MathHelp.intToBoolean(encoded&DEFECT_MASK);
    this.holdOther = MathHelp.intToBoolean(encoded&HOLD_OTHER_MASK);
    this.character = defect ? "D" : "C";
  }

  @Override
  public double exitReward() {
    return state.rightOfMeCond((a) -> ((PrisonerDilemmee)a).defect, actorNum) ? 
      defect ? partSentence : fullSentence:
      defect ? zeroSentence : reduSentence;
  }

  @Override
  public boolean exitCondition() {
    return state.allActorsCond((a) -> !((PrisonerDilemmee)a).holdOther);
    // return true;
  }

  @Override
  public void draw(Graphics2D g, int x, int y, int width, int height) {

    int thisX = x + (int)(this.state.getActorPs()[actorNum].x*width+width/2);
    int thisY = y + (int)(this.state.getActorPs()[actorNum].y*height+height/2);
    int size = (int)(height*World.Visualiser.getActorCircRadius()*2);

    g.setStroke(new BasicStroke(height/64));
    g.setColor(colourText);

    if (holdOther) g.drawLine(
      thisX, 
      thisY, 
      x + (int)(this.state.getActorPs()[(actorNum+1)%World.Setup.getNumActors()].x*width+width/2), 
      y + (int)(this.state.getActorPs()[(actorNum+1)%World.Setup.getNumActors()].y*height+height/2)
    );

    Actor.drawCircle(g, colourBody, colourText, thisX, thisY, size);
    
    g.setFont(new Font(Font.MONOSPACED, Font.BOLD, size/2));
    FontMetrics met = g.getFontMetrics();

    g.drawString(character, thisX-met.stringWidth(character)/2, thisY+met.getDescent()+met.getLeading());
  }
  
}
