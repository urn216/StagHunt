package code.world.actors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import code.core.Core;
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

  public abstract double exitReward();

  public abstract boolean exitCondition();

  protected abstract Actor replace(int encoded);

  public State toggleBool(int bool) {
    if (bool >= World.Setup.getActorSize()) throw new IndexOutOfBoundsException(bool);
    return state.changeActor(this.actorNum, replace(MathHelp.toggleBit(encoded, bool)));
  }

  public State getState() {
    return state;
  }

  public Color getTextColour() {
    return colourText;
  }

  public State leave() {
    return State.Encoder.decode(State.Encoder.numberOfStates());
  }

  public void draw(Graphics2D g, int x, int y, int size) {
    g.setStroke(new BasicStroke(Core.WINDOW.screenHeight()/128));

    g.setColor(colourBody);
    g.fillOval(x-size/2, y-size/2, size, size);

    g.setColor(colourText);
    g.drawOval(x-size/2, y-size/2, size, size);

    g.setStroke(new BasicStroke(1));
  }
}
