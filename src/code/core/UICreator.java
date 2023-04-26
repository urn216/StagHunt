package code.core;

import ui.math.Vector2;
import ui.control.UIColours;
import ui.control.UIController;
import ui.control.UIHelp;
import ui.control.UIPane;
import ui.control.UIState;
import ui.components.UIComponent;
import ui.components.UIInteractable;
import ui.components.UIText;
import ui.components.interactables.*;
import ui.elements.*;

import code.world.State;
import code.world.World;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

class UICreator {
  // private static final UIElement VIRTUAL_KEYBOARD = new ElemKeyboard();
  
  private static final double COMPON_HEIGHT = 0.065;
  private static final double BUFFER_HEIGHT = 0.015;
  
  /**
  * Creates the UI pane for the main menu.
  */
  public static UIPane createMain() {
    UIPane mainMenu = new UIPane();
    
    UIElement title = new UIElement(
    new Vector2(0   , 0),
    new Vector2(0.28, 0.14),
    new boolean[]{true, false, true, false}
    ){
      protected void init() {components = new UIComponent[]{new UIText("Stag Hunt", 0.6, Font.BOLD)};}
      protected void draw(Graphics2D g, int screenSizeY, Vector2 tL, Vector2 bR, Color[] c, UIInteractable highlighted) {
        components[0].draw(g, (float)tL.x, (float)tL.y, (float)(bR.x-tL.x), (float)(bR.y-tL.y), c[UIColours.TEXT]);
      }
    };
    
    UIElement outPanel = new ElemList(
    new Vector2(0   , 0.28),
    new Vector2(0.18, 0.28+UIHelp.calculateListHeightDefault(2, BUFFER_HEIGHT, COMPON_HEIGHT)),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UIButton("Value Iterator" , () -> {UIController.setState(UIState.NEW_GAME); World.Player.setState(State.Encoder.decode(0));}),
      new UIButton("Quit to Desktop", Core::quitToDesk),
    },
    new boolean[]{false, false, true, false}
    );
    
    UIElement newGame = new ElemList(
    new Vector2(0   , 0.28),
    new Vector2(0.18, 0.28+UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT, COMPON_HEIGHT*2, COMPON_HEIGHT*2, COMPON_HEIGHT, COMPON_HEIGHT)),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UIButton("Begin", () -> {World.Setup.initialiseMDPs(); World.Setup.doVI();}),
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
        (a) -> {World.Setup.setNumActors(a); World.Player.setState(State.Encoder.decode(0));}, 
        1, 
        10
      ),
      new UIToggle("Co-op VI", World.Setup::isCooperativeVI, World.Setup::setCooperativeVI),
      new UIButton("Back", UIController::back),
    },
    new boolean[]{false, false, true, false}
    );
    
    mainMenu.addState(UIState.DEFAULT,  title   );
    mainMenu.addState(UIState.DEFAULT,  outPanel);
    mainMenu.addState(UIState.NEW_GAME, title    , UIState.DEFAULT, () -> {World.Player.setState(null); UIController.retState();});
    mainMenu.addState(UIState.NEW_GAME, newGame );
    
    mainMenu.clear();
    
    return mainMenu;
  }
}
