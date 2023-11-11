package code.core;

import mki.math.vector.Vector2;

import mki.ui.control.UIActionGetter;
import mki.ui.control.UIColours;
import mki.ui.control.UIController;
import mki.ui.control.UIHelp;
import mki.ui.control.UIPane;
import mki.ui.control.UIState;

import mki.ui.components.UIComponent;
import mki.ui.components.UIInteractable;
import mki.ui.components.UIText;
import mki.ui.components.interactables.*;

import mki.ui.elements.*;

import code.world.State;
import code.world.World;
import code.world.actors.Actor;
import code.world.actors.ItemSwapper;
import code.world.actors.StagHunter;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class UICreator {
  // private static final UIElement VIRTUAL_KEYBOARD = new ElemKeyboard();
  
  private static final double COMPON_HEIGHT = 0.04;
  private static final double BUFFER_HEIGHT = 0.008;
  
  /**
  * Creates the UI pane for the main menu.
  */
  public static UIPane createMain() {
    UIPane mainMenu = new UIPane();
    
    UIElement title = new UIElement(
    new Vector2(0   , 0),
    new Vector2(0.28, 0.14),
    UIElement.TRANSITION_SLIDE_UP_LEFT
    ){
      protected void init() {components = new UIComponent[]{new UIText("Stag Hunt", 0.6, Font.BOLD)};}
      protected void draw(Graphics2D g, int screenSizeY, Vector2 tL, Vector2 bR, UIColours.ColourSet c) {
        components[0].draw(g, (float)tL.x, (float)tL.y, (float)(bR.x-tL.x), (float)(bR.y-tL.y), c);
      }
    };
    
    UIElement outPanel = new ElemListVert(
    new Vector2(0   , 0.28),
    new Vector2(0.11, 0.28+UIHelp.calculateListHeightDefault(2, BUFFER_HEIGHT, COMPON_HEIGHT)),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UIButton("Value Iterator" , () -> {UIController.setState(UIState.NEW_GAME); World.Player.setState(State.Encoder.decode(0b0));}),
      new UIButton("Quit to Desktop", Core::quitToDesk),
    },
    UIElement.TRANSITION_SLIDE_LEFT
    );
    
    UIElement newGame = new ElemListVert(
    new Vector2(0   , 0.28),
    new Vector2(0.11, 0.28+UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT, COMPON_HEIGHT*2, COMPON_HEIGHT*2, COMPON_HEIGHT, COMPON_HEIGHT, COMPON_HEIGHT, COMPON_HEIGHT)),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UIButton("Begin", () -> {World.Setup.initialiseMDPs(); World.Setup.doVI(); World.Visualiser.drawMoveTree();}),
      new UISlider.Double(
        "Gamma: %.2f", 
        World.Setup::getGamma, 
        World.Setup::setGamma, 
        0, 
        1, 
        0.01
      ),
      new UISlider.Integer(
        "Num Actors: %.0f", 
        World.Setup::getNumActors, 
        (a) -> {World.Setup.setNumActors(a); World.Player.setState(State.Encoder.decode(0b0));}, 
        1, 
        10
      ),
      new UIDropDown<World.VI_MODE>("VI Mode: %s", 
        World.Setup.getVIMode()::toString,
        World.Setup::setVIMode,
        World.VI_MODE.Naive,
        World.VI_MODE.CVI,
        World.VI_MODE.SVI
      ),
      // new UIDropDown("MDP Mode: %s", 
      //   World.Setup
      // ),
      new UIDropDown<Class<? extends Actor>>("Game: %s",
        World.Setup.getActorType()::getSimpleName,
        World.Setup::setActorType,
        StagHunter.class,
        ItemSwapper.class
      ),
      new UIButton("Return To Menu", UIController::back),
    },
    UIElement.TRANSITION_SLIDE_LEFT
    );

    UIElement randMove = new ElemListVert(
      new Vector2(0.45, 1-UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT)), 
      new Vector2(0.55, 1),
      COMPON_HEIGHT, 
      BUFFER_HEIGHT, 
      new UIComponent[] {
        new UIButton(
          "Random Turn", 
          ()-> World.Player.progressState((int)(Math.random()*World.Setup.getNumActors()))
        ).setLockCheck(lockCheck)
      }, 
      UIElement.TRANSITION_SLIDE_DOWN
    );
    
    mainMenu.addState(UIState.DEFAULT,  title   );
    mainMenu.addState(UIState.DEFAULT,  outPanel);
    mainMenu.addState(UIState.NEW_GAME, title    , UIState.DEFAULT, () -> {World.Player.setState(null); UIController.retState();});
    mainMenu.addState(UIState.NEW_GAME, newGame );
    mainMenu.addState(UIState.NEW_GAME, randMove);
    // mainMenu.addState(UIState.NEW_GAME, generateCirclePanel());

    mainMenu.clear();
    
    return mainMenu;
  }

  private static final UIActionGetter<Boolean> lockCheck = () -> !World.Setup.isReady();

  public static UIElement generateCirclePanel(int actorNum) {
    Actor a = World.Player.getState().getActors()[actorNum];
    double xOff = 0.5-0.3*Core.WINDOW.screenHeight()/Core.WINDOW.screenWidth();
    return new UIElement(new Vector2(xOff, 0.2), new Vector2(1-xOff, 0.8), UIElement.TRANSITION_SHRINK) {

      @Override
      public void init() {
        background = new UIComponent() {
          @Override
          protected void draw(Graphics2D g, UIColours.ColourSet c) {
            Shape circ = new Ellipse2D.Double(x, y, width, height);
            g.setColor(c.background());
            g.fill(circ);
            g.setColor(a.getTextColour());
            g.setStroke(new BasicStroke(Core.WINDOW.screenHeight()/128));
            g.draw(circ);
            g.setStroke(new BasicStroke(1));
          }
        };

        components = new UIComponent[World.Setup.getActorSize()+4];

        components[0] = new UIText("Actor " + actorNum, 1, Font.BOLD);

        for (int i = 0; i < World.Setup.getActorSize(); i++) {
          int action = i;
          components[i+1] = new UIButton("Action " + (action+1), ()->World.Player.actInState(actorNum, action));
        }

        components[components.length-3] = new UIButton("Pass", ()->World.Player.actInState(actorNum, components.length-4))
          .setLockCheck(lockCheck);
        components[components.length-2] = new UIButton("Leave", ()->World.Player.actInState(actorNum, components.length-3))
          .setLockCheck(()->!World.Setup.isReady() || !World.Player.getState().getActors()[actorNum].exitCondition());
        components[components.length-1] = new UIButton("Best Move", ()->World.Player.progressState(actorNum))
          .setLockCheck(lockCheck);
      }

      @Override
      protected void draw(Graphics2D g, int screenSizeY, Vector2 tL, Vector2 bR, UIColours.ColourSet c) {
        tL = tL.add     (BUFFER_HEIGHT*screenSizeY);
        bR = bR.subtract(BUFFER_HEIGHT*screenSizeY);

        float buttonWidth = (float)((bR.x-tL.x)/3);
        float buttonHeight = buttonWidth/3.5f;

        double mid = (tL.y+bR.y)/2;
        double rad = (bR.y-tL.y)/2;

        double yInit = rad-Math.sqrt(rad*rad-(buttonWidth*buttonWidth)/4);

        components[0].draw(g, (float)(tL.x)+buttonWidth, (float)(tL.y+yInit), buttonWidth, buttonHeight/2, c);

        float yOff = ((float)(rad-yInit)*2-buttonHeight)/((components.length+1)/2);

        for (int i = 1; i < components.length-1; i++) {
          float y = (float)(tL.y+yInit)+yOff*((i+1)/2);
          double xInit = rad-Math.sqrt(rad*rad-Math.pow(mid-y - (mid-y > 0 ? 0 : buttonHeight), 2));
          float x = (float)(i%2==1 ? tL.x+xInit : bR.x-buttonWidth-xInit);
          components[i].draw(g, x, y, buttonWidth, buttonHeight, c);
        }

        components[components.length-1].draw(g, (float)(tL.x)+buttonWidth, (float)(bR.y-yInit)-buttonHeight, buttonWidth, buttonHeight, c);
      }
      
    };
  }
}
