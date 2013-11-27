package cuiclient

import akka.actor._
import jcurses.system._
import jcurses.widgets._
import jcurses.util._
import jcurses.event._

import game._


/**
 * The singleton main Object which starts the CUI client
 * @author bwilliams
 *
 */
object CUIApp extends App {
  
  
  override def main(args: Array[String]) = {
    
	System.setProperty("jcurses.protocol.filename", "sb_curses.log");
    Protocol.activateChannel(Protocol.DEBUG);
    Protocol.debug("SBScala Starting up");
    
    Toolkit.beep();
    val appWidth = 150
    val appHeight = 45

    Protocol.debug("Width: " + appWidth + " Height: " + appHeight);
    val appWindow = new CUIApp(appWidth, appHeight)
    appWindow.show();
    
  }
  
   def shutdownCuiClient = {
     
     //TODO shutdown actor system
     //system.shutdown
     
     Protocol.debug("CUI client shutting down");
     
     System.exit(0)
  
   }
}

/**
 * The gui instance class that is the window
 */
class CUIApp(val widthParam:Int, val heightParam:Int) extends Window(0, 0, widthParam, heightParam, true, "Starbase Scala")
  with WindowListener {
  
  /* GUI elements */
  val manager = new GridLayoutManager(5, 9);
  import jcurses.widgets.WidgetsConstants._
  getRootPanel().setLayoutManager(manager);
  
  val boardViewer = new BoardWidget(testBoardSinglePlayer)
  
  val opponentsPanel = new OpponentsPanel
  
  val playerInfoPanel = new PlayerInfoPanel
  
  manager.addWidget(singleWidgetPanel(boardViewer), 0, 0, 4, 7, ALIGNMENT_CENTER, ALIGNMENT_CENTER);
  manager.addWidget(opponentsPanel,     4, 0, 1, 7, ALIGNMENT_CENTER, ALIGNMENT_CENTER);
  manager.addWidget(playerInfoPanel,    0, 7, 5, 2, ALIGNMENT_CENTER, ALIGNMENT_CENTER);

  this.addListener(this)
  
  /* Game Elements */
  var gameActor: ActorRef = null
  
  //TODO pop up a welcome message, help, instructions etc
  
  override def windowChanged(event: WindowEvent) = {
    event.getType match {
      case WindowEvent.CLOSING => {
        Protocol.debug("CLOSING window event")

        // capture the window closing event from hitting escape
        // show the game menu
        val menu = new PopUpMenu(50, 15, "Main Menu")
        menu.add("New Game")
        //TODO add a Help/Instructions option
        menu.add("Quit")
        
        menu.show
        
        menu.getSelectedIndex() match {
          case 0 => { 
            //TODO prompt user for no. of players/AI, etc
            //TODO create new game
            Protocol.debug("Creating new Game")
          }
          case 1 => event.getSourceWindow().close();
        }
      }
      case WindowEvent.ACTIVATED => { 
        //Protocol.debug("ACTIVATED window event")        
      }
      case WindowEvent.CLOSED => { 
        //Protocol.debug("CLOSED window event")
        CUIApp.shutdownCuiClient
      }
      case WindowEvent.DEACTIVATED => {
        //Protocol.debug("DEACTIVATED window event")
      }
    }
  }
  
}