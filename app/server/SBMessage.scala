package server

import akka.actor._
import game._

/**
 * All messages passed in the system are defined here
 */
sealed trait SBMessage
case class NewGame(players: List[ActorRef]) extends SBMessage
case class GameCreated(game: ActorRef) extends SBMessage
case class NewPlayer(name: String) extends SBMessage
case class StartGame() extends SBMessage
case class GameStarted() extends SBMessage
case class AddPlayer(player: Player) extends SBMessage
case class FinishedGame(game: ActorRef) extends SBMessage
case class CardSelected(player: Player, card: Card) extends SBMessage
case class CardPlayed(player: Player, card: Card, orientation: CardOrientation, loc: BoardLoc) extends SBMessage
case class InfoMessage(message: String) extends SBMessage
case class StatusRequest() extends SBMessage
case class GameDataUpdate(game: ActorRef, gameData: GameData) extends SBMessage
