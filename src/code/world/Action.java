package code.world;

import code.world.actors.Actor;

public interface Action {
  public State act(Actor a);
}
