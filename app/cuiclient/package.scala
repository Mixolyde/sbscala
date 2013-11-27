
import akka.actor._
import jcurses.system.CharColor
import jcurses.widgets.BorderLayoutManager
import jcurses.widgets.BorderPanel
import jcurses.widgets.Panel
import jcurses.widgets.Widget
import game._

package object cuiclient {

  def singleWidgetPanel (widget:Widget, border:Boolean = false) = {
    val singleWidgetPanel = border match {case true => new BorderPanel(); case false => new Panel()}
    val singleWidgetManager = new BorderLayoutManager()
    import jcurses.widgets.BorderLayoutManager._
    import jcurses.widgets.WidgetsConstants._
    singleWidgetPanel.setLayoutManager(singleWidgetManager)
    singleWidgetManager.addWidget(widget, CENTER, ALIGNMENT_CENTER, ALIGNMENT_CENTER)
    singleWidgetPanel
  }
  
  def getCardStringRow(card:Card, orientation:CardOrientation, row:Int):String = {
    (card, orientation, row) match {
      case (SAB, any, 1) => "\\|/"
      case (SAB, any, 2) => "-*-"
      case (SAB, any, 3) => "/|\\"
      case (POW, any, 1) => " | "
      case (POW, any, 2) => "-O-"
      case (POW, any, 3) => " | "
      case (anyCard, anyOrient, 1) => " " + exitChar(anyCard, anyOrient, UP) + " "
      case (anyCard, anyOrient, 2) => exitChar(anyCard, anyOrient, LEFT) + "O" + exitChar(anyCard, anyOrient, RIGHT)
      case (anyCard, anyOrient, 3) => " " + exitChar(anyCard, anyOrient, DOWN) + " "
    }
  }
  
  def exitChar(card:Card, orientation:CardOrientation, exit:CardOrientation) = {
    if(card.exits(orientation).contains(exit)) {
      exit match {
        case UP => "|"
        case DOWN => "|"
        case LEFT => "-"
        case RIGHT=> "-"
      }
    } else {
      " "
    }
  }
  
  val playerColors = List(
    new CharColor(CharColor.WHITE, CharColor.GREEN),
    new CharColor(CharColor.WHITE, CharColor.YELLOW),
    new CharColor(CharColor.WHITE, CharColor.MAGENTA),
    new CharColor(CharColor.WHITE, CharColor.CYAN)
    
  )
  
  
  /**
   * Test data generating methods for testing the GUI elements
   */
  def testPlayerData(num:Int) = {
    val (hand, deck) = Card.newHandAndDeck()
    new PlayerData("Player" + num, hand, deck, List[Card](), 123)
  }
  
  def testBoardEmpty = new Board()
  
  def testBoardSinglePlayer = {
    val board = new Board()
    val system = ActorSystem("SBScalaBoardSuite")
    val actor = system.actorOf(Props(new Player("BoardSuitePlayer1")))

    // board for testing in loc X:
    //       |
    // O  X -O-
    // |     |
    // |  |  |
    //-O--O--O--O-
    // |  |  
    board.addCardToStation(new BoardLoc(0, 0), POW, UP, actor)
    board.addCardToStation(new BoardLoc(1, 0), HAB, DOWN, actor)
    board.addCardToStation(new BoardLoc(1, 1), LAB, DOWN, actor)
    board.addCardToStation(new BoardLoc(-1, 0), POW, UP, actor)
    board.addCardToStation(new BoardLoc(-1, 1), REC, DOWN, actor)
    board.addCardToStation(new BoardLoc(2, 0), FAC, LEFT, actor)
    board.addCardToStation(new BoardLoc(0, -1), COM, UP, actor)
    
    board
  }
  
}