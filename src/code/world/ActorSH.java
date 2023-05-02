package code.world;

public class ActorSH extends Actor {

  /**
   * Creates a new {@code Actor} with given parameters.
   * 
   * @param huntStag whether the actor is considering hunting stags at the moment
   */
  public ActorSH(State state, int actorNum, boolean huntStag) { //TODO this?
    super(state, actorNum, huntStag);
    // this.state = state;
    // this.actorNum = actorNum;
    // this.huntStag = huntStag;
    // this.character = huntStag ? "S" : "H";

    // float percentAround = 1f*actorNum/World.Setup.getNumActors();

    // this.colourBody = Color.getHSBColor(percentAround, 1, 1   );
    // this.colourText = Color.getHSBColor(percentAround, 1, 0.4f);
  }
  
}
