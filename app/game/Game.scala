package game

import akka.actor._
import akka.event.Logging
import server._
/**
 * The game class, which maintains the current round and player information for a game
 * Creating by a game server, and communicates with the server and players via actor messages
 * Always in one of three states:
 *   Waiting to start, which allows adding players, up to 4
 *   Waiting for all player selections, received asynchronously
 *   Waiting for current player to play a piece from their deferred list
 */
class Game (val players:List[ActorRef]) extends Actor {
  val log = Logging(context.system, this)
  
  val currentRound: Round = new Round(new Board(), 1, 0)
  
  var currentSelections :List[(Player, Card)] = List()
  
  var playerData: Map[Player, PlayerData] = Map()
  
  var isStarted = false
  
  log.info("Game actor created")
  
  def receive = {
    case StatusRequest => sender ! gameData()
    case any =>
      log.info ("Game actor received message: " + any.toString)
      sender ! new InfoMessage("Game actor received message: " + any.toString)
  }
  
  def gameData() = new GameData(players.size, 4, isStarted)
}

object Game {
  // create a new game with a variable number of Player arguments
  def apply(players: ActorRef*) { 
    this(players.toList)
  }
  def apply(players: List[ActorRef]): Game = {
    players.length match {
      case size if size > 0 && size < 5 => new Game(players)
      case size => sys.error("Number of players must be 1-4, received: " + size) 

    }
    
  }
}

case class GameData(val players:Int, val maxPlayers:Int, val started:Boolean) {
  override def toString: String = {
    val startedString = started match {
      case false => "not started"
      case true => "started"
    }
    "Game: " + players + " of " + maxPlayers + ": " + startedString 
  }
}

class Round(val board: Board, val turnCount: Int, 
    pot : Int)
    
class PlayerData(val name:String, val hand: List[Card], val deck: List[Card], 
    val deferred: List[Card], val cash: Int)

