package thickclient

import akka.actor._
import akka.event.Logging
import java.awt.BorderLayout
import scala.swing._
import scala.swing.event._
import game._
import server._
import scala.collection.mutable.ListBuffer

class GamePanel(val player: ActorRef) extends BorderPanel {
  import BorderPanel.Position._

  //list of current games and statuses
  layout(new GameListPanel(player)) = West

  //canvas for drawing board
  layout(new BoardPanel()) = Center

  //panel for displaying current hand
  layout(new PlayerInfoPanel()) = South

  //panel for showing other player info and deferred cards
  layout(new GameInfoPanel()) = East
}

class GameListPanel(val player: ActorRef) extends BoxPanel(Orientation.Vertical) {
  val gameList = new ListView(List[(ActorRef, GameData)]()) {
    import ListView._
    selection.intervalMode = ListView.IntervalMode.Single
    // renders a game data object as a text string in the list view
    renderer = Renderer(_._2.toString())
    
    preferredSize = new java.awt.Dimension
  }
  val createButton = new Button("Create")
  val joinButton = new Button("Join")
  val refreshButton = new Button("Refresh")

  class GamePanelActor extends Actor {
    val log = Logging(context.system, this)
    
    val buffer = ListBuffer[(ActorRef, GameData)]()
    gameList.listData = buffer
    
    def receive = {
      case GameDataUpdate(game, gameData) => {
        log.info("received game data")
        buffer += ((game, gameData))
        gameList.listData = buffer
      }
      case _ => log.info("Game Panel Actor received unknown message")
    }
  }
  
  val gamePanelActorRef = SBApp.system.actorOf(Props(new GamePanelActor()))
  SBApp.gameSup ! gamePanelActorRef

  contents += new Label("Game List")
  contents += gameList
  contents += createButton
  contents += joinButton
  contents += refreshButton

  listenTo(gameList, createButton, joinButton, refreshButton)

  reactions += {
    case ButtonClicked(`createButton`) =>{
      println("Create Button Clicked")
      SBApp.gameSup ! NewGame(List(player))
      createButton.enabled = false
      joinButton.enabled = false
    }
      
    case ButtonClicked(`joinButton`) =>
      println("Join Button Clicked")
    case ButtonClicked(`refreshButton`) => {
      //ask game server for fresh game data
      println("Refresh Button Clicked")
    }
  }

}

class BoardPanel extends BoxPanel(Orientation.Vertical) {
  contents += new Label("Board")
}

class PlayerInfoPanel extends BoxPanel(Orientation.Horizontal) {
  contents += new CardDisplayPanel("Hand")
  contents += new CardDisplayPanel("Deferred")
}

class CardDisplayPanel(val title: String) extends BoxPanel(Orientation.Vertical) {
  val cardDisplayPanel = new BoxPanel(Orientation.Horizontal) {
    contents += new Label("Card 1")
    contents += new Label("Card 2")
  }
  contents += new Label(title)
  contents += cardDisplayPanel

}

class GameInfoPanel extends BoxPanel(Orientation.Vertical) {
  contents += new Label("Game Info")
  contents += new Label("Other Player Info 1")
  contents += new Label("Other Player Info 2")
  contents += new Label("Other Player Info 3")
}
