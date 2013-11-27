package server

import akka.actor._
import akka.event.Logging
import game._
import java.util.UUID

class PlayerSup extends Actor {
  val log = Logging(context.system, this)
  
  log.info("Player Supervisor actor created")
  
  /**
   * Handle incoming message requests
   */
  def receive = {
    case NewPlayer(name) => sender ! createPlayer(name)
    case StatusRequest() => { 
      val statusMessage = "Player Sup Status. Players: " + context.children.size
      log.info("Player Sup received StatusRequest. Responding: " + statusMessage)
      sender ! InfoMessage(statusMessage)
    }
    case any =>
      log.info ("Player Sup received message: " + any.toString)
  }

  def createPlayer(name: String) : ActorRef = {
    //create a new player
    context.actorOf(
      Props(new Player(name)))
    
  }
}