package code.world;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import code.core.Core;
import code.math.MathHelp;

/**
 * A 'character' within the world. {@code Actor}s have attributes 
 * and can perform actions which alter these attributes.
 * 
 * @author William Kilty
 */
public class Actor {
  private final boolean huntStag;
  private final State state;
  private final int actorNum;

  private final Color colourBody;
  private final Color colourText;

  /**
   * Creates a new {@code Actor} with given parameters.
   * 
   * @param huntStag whether the actor is considering hunting stags at the moment
   */
  Actor(State state, int actorNum, boolean huntStag) {
    this.state = state;
    this.actorNum = actorNum;
    this.huntStag = huntStag;

    float percentAround = 1f*actorNum/World.Setup.getNumActors();

    this.colourBody = Color.getHSBColor(percentAround, 1, 1   );
    this.colourText = Color.getHSBColor(percentAround, 1, 0.4f);
  }

  /**
   * Creates a new {@code Actor} out of an encoded {@code int}.
   * All necessary parameters are to be extracted out of this {@code int}.
   * 
   * @param encoded the encoded {@code int} from which to construct this {@code Actor}
   */
  Actor(State state, int actorNum, int encoded) {
    this(state, actorNum, MathHelp.intToBoolean(encoded & 1));
  }

  /**
   * Encodes this {@code Actor} into an {@code int} to be understood by a {@code ValueIterator}.
   * 
   * @return a uniquely identifiable {@code int} representing the state of this {@code Actor}
   */
  public int encode() {
    return MathHelp.booleanToInt(huntStag);
  }

  public double exitReward() {
    return huntStag ? state.allActorsCond((a) -> a.huntStag) ? 4 : 0 : 3;
  }

  public State toggleBool(int bool) {
    return state.changeActor(actorNum, new Actor(state, actorNum, !huntStag));
  }

  public State getState() {
    return state;
  }

  public State leave() {
    return State.Encoder.decode(State.Encoder.numberOfStates());
  }

  /**
   * Gets the size in bits of an {@code Actor}.
   * 
   * @return the number of bits required to represent an {@code Actor}
   */
  public static int size() {return 1;}

  public void draw(Graphics2D g, int x, int y, int size) {
    g.setStroke(new BasicStroke(Core.WINDOW.screenHeight()/128));
    g.setFont(new Font(Font.MONOSPACED, Font.BOLD, size/2));

    g.setColor(colourBody);
    g.fillOval(x-size/2, y-size/2, size, size);

    g.setColor(colourText);
    g.drawOval(x-size/2, y-size/2, size, size);

    FontMetrics met = g.getFontMetrics();
    char c = huntStag ? 'S' : 'H';

    g.drawString(c+"", x-met.charWidth(c)/2, y+met.getDescent()+met.getLeading());
    g.setStroke(new BasicStroke(1));
  }
}
