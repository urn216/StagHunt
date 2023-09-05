package code.world.actors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import code.world.State;
import code.world.World;

import mki.math.MathHelp;

/**
 * A 'character' within the world. {@code Actor}s have attributes 
 * and can perform actions which alter these attributes.
 * 
 * @author William Kilty
 */
public abstract class Actor {
  protected final int encoded;
  protected final State state;
  protected final int actorNum;

  protected final Color colourBody;
  protected final Color colourText;

  /**
   * Creates a new {@code Actor} out of an encoded {@code int}.
   * All necessary parameters are to be extracted out of this {@code int}.
   * 
   * @param encoded the encoded {@code int} from which to construct this {@code Actor}
   */
  protected Actor(State state, int actorNum, int encoded) {
    this.state = state;
    this.actorNum = actorNum;
    this.encoded = encoded;
    // this.character = huntStag ? "S" : "H";

    float percentAround = 1f*actorNum/World.Setup.getNumActors();

    this.colourBody = Color.getHSBColor(percentAround, 1, 1   );
    this.colourText = Color.getHSBColor(percentAround, 1, 0.4f);
  }

  /**
   * Encodes this {@code Actor} into an {@code int} to be understood by a {@code ValueIterator}.
   * 
   * @return a uniquely identifiable {@code int} representing the state of this {@code Actor}
   */
  public int encoded() {
    return this.encoded;
  }
  
  /**
   * Gets the size in bits of this {@code Actor}
   * 
   * @return the number of bits in this {@code Actor}
   */
  public static int size() {return 1;}

  public abstract double exitReward();

  public abstract boolean exitCondition();

  public State toggleBool(int bool) {
    if (bool >= World.Setup.getActorSize()) throw new IndexOutOfBoundsException(bool);
    try {
      return state.changeActor(this.actorNum, MathHelp.toggleBit(encoded, bool));
    } catch (Exception e) {return null;}
  }

  public State getState() {
    return state;
  }

  public Color getTextColour() {
    return colourText;
  }
  
  public Color getBodyColour() {
    return colourBody;
  }

  public State leave() {
    return exitCondition() ? State.Encoder.decode(State.Encoder.numberOfStates()) : this.state;
  }

  public void draw(Graphics2D g, int x, int y, int width, int height) {

    x += (int)(this.state.getActorPs()[actorNum].x*width+width/2);
    y += (int)(this.state.getActorPs()[actorNum].y*height+height/2);
    int size = (int)(height*World.Visualiser.ACTOR_CIRC_RADIUS*2);

    drawCircle(g, colourBody, colourText, x, y, size);
  }

  protected static void drawCircle(Graphics2D g, Color colourBody, Color colourOutL, int x, int y, int size) {
    g.setStroke(new BasicStroke(size/16));

    g.setColor(colourBody);
    g.fillOval(x-size/2, y-size/2, size, size);

    g.setColor(colourOutL);
    g.drawOval(x-size/2, y-size/2, size, size);

    g.setStroke(new BasicStroke(1));
  }
}
