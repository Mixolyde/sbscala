package server

import akka.actor._
import akka.event.Logging
import game._

/**
 * The Game Sup is a supervisor Actor which maintains a list of current Games
 * and allows the creation of new games
 * When a game finishes, it should notify the GameServer, and it will be removed
 *   from its list
 * If a game crashes, the GameServer should receive the notifcation and remove it
 *   from the list
 * @author bwilliams
 *
 */
class GameSup extends Actor {
  val log = Logging(context.system, GameSup.this)
  var listeners :Set[ActorRef] = Set()
  
  log.info("Game Server actor created")
  
  /**
   * Handle incoming message requests
   */
  def receive = {
    case NewGame(players) => {
      log.info ("Creating new Game with " + players.length + " players."); 
      newGame(players)
    }
    case FinishedGame(gameRef) => log.info("Game Supervisor received FinishedGame message")
    case StatusRequest() => { 
      val statusMessage = "Game Supervisor Status. Games: " + context.children.size
      log.info("Game Supervisor received StatusRequest. Responding: " + statusMessage)
      sender ! InfoMessage(statusMessage)
    }    
    case listener: ActorRef =>{
      //add actor ref to listener list
      //TODO mechanism for removing listeners
      listeners += listener
    }
    case any =>
      log.info ("Game Supervisor received message: " + any.toString)
      sender ! new InfoMessage("Game Supervisor received message: " + any.toString)
  }
  
  /**
   * Creates a new Game actor and supervises it
   */
  private def newGame(players: List[ActorRef]) = {
    val newGame = context.actorOf(
      Props(Game(players)))
    log.info("Game created, notifying players.")
    for (player <- players) player ! GameCreated(newGame)
    
    fireGameDataChanged(newGame, new GameData(players.size, 4, false))
    
  }
  
  private def fireGameDataChanged(game:ActorRef, 
      gamedata: GameData): Unit = {
    listeners foreach ( listener => listener ! new GameDataUpdate(game, gamedata))
  }
}