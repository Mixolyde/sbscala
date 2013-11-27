package game

import server._
import akka.actor._
import akka.event.Logging
/**
 * Player class to hold a player's name, hand of cards, deck and current cash
 */
class Player (val name: String)
  extends Actor {
  val log = Logging(context.system, this)
  var currentGame : ActorRef = null
  var gameStarted = false
    
  def receive = {
    case GameCreated(game) => currentGame match {
      case null => log.info("Player received game created notification"); currentGame = game
      case any => log.error("Player received game created notification while still in a game")
    }
    case GameStarted() => log.info("Player received game started notification"); gameStarted = true
    case any =>
      log.info ("Player actor received message: " + any.toString)
      
  }
  
  
  def createGame() = {
    //create message
    val createGameMsg = NewGame(List(self))
    //lookup game server and send message
    //context.actorFor("/system/GameServer") ! createGameMsg
    SBApp.gameSup ! createGameMsg
    
  }
}

object Player{
 
}