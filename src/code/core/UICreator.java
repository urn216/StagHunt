package code.core;

import code.math.Vector2;
import code.mdp.OOMDP;
import code.ui.UIColours;
import code.ui.UIController;
import code.ui.UIHelp;
import code.ui.UIPane;
import code.ui.UIState;
import code.ui.components.UIComponent;
import code.ui.components.UIInteractable;
import code.ui.components.UIText;
import code.ui.components.interactables.*;
import code.ui.elements.*;
import code.world.Action;
import code.world.State;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

class UICreator {
  // private static final UIElement VIRTUAL_KEYBOARD = new ElemKeyboard();
  
  private static final double COMPON_HEIGHT = 0.075;
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
    new Vector2(0.24, 0.28+UIHelp.calculateListHeightDefault(2, BUFFER_HEIGHT, COMPON_HEIGHT)),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UIButton("Run VI"         , () -> UIController.setState(UIState.NEW_GAME)),
      new UIButton("Quit to Desktop", Core::quitToDesk                             ),
    },
    new boolean[]{false, false, true, false}
    );
    
    UIElement newGame = new ElemList(
    new Vector2(0   , 0.28),
    new Vector2(0.24, 0.28+UIHelp.calculateListHeightDefault(4, BUFFER_HEIGHT, COMPON_HEIGHT)),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UIButton("Begin", () -> {
        Core.mdp = new OOMDP(
          0.9,
          new Action[] {
            (a) -> {return a.toggleBool(0);},
            (a) -> {return a.getState();},
            (a) -> {return a.leave();}
          },
          0
        );
        Core.doVI();
      }),
      new UISlider("Num Actors: %d", State::getNumActors, (a) -> {State.setNumActors(a); Core.s = State.decode(0);}, 1, 10),
      new UIButton("Back", UIController::back),
    },
    new boolean[]{false, false, true, false}
    );
    
    mainMenu.addState(UIState.DEFAULT,  title   );
    mainMenu.addState(UIState.DEFAULT,  outPanel);
    mainMenu.addState(UIState.NEW_GAME, title    , UIState.DEFAULT);
    mainMenu.addState(UIState.NEW_GAME, newGame );
    
    mainMenu.clear();
    
    return mainMenu;
  }
  
  /**
  * Creates the HUD for use during gameplay.
  */
  public static UIPane createHUD() {
    UIPane HUD = new UIPane();
    
    UIElement greyed = new UIElement(
    new Vector2(0,0),
    new Vector2(1, 1),
    new boolean[]{false, false, false, false}
    ){
      protected void init() {this.backgroundColour = UIColours.SCREEN_TINT;}
      protected void draw(Graphics2D g, int screenSizeY, Vector2 tL, Vector2 bR, Color[] c, UIInteractable highlighted) {}
    };
    
    UIElement outPause = new ElemList(
    new Vector2(0.38, 0.5-UIHelp.calculateListHeightDefault(2, BUFFER_HEIGHT, COMPON_HEIGHT)/2),
    new Vector2(0.62, 0.5+UIHelp.calculateListHeightDefault(2, BUFFER_HEIGHT, COMPON_HEIGHT)/2),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UIButton("Resume"         , UIController::back                         ),
      // new UIButton("Quit to Title"  , Core::toMenu                               ),
      new UIButton("Quit to Desktop", Core::quitToDesk                           ),
    },
    new boolean[]{false, true, true, true}
    );
    
    HUD.setModeParent(UIState.DEFAULT, UIState.PAUSED);
    
    HUD.addState(UIState.PAUSED, greyed   , UIState.DEFAULT);
    HUD.addState(UIState.PAUSED, outPause);
    
    HUD.clear();
    
    return HUD;
  }
}
