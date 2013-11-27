package game

import akka.actor._
import scala.Collection._
import scala.collection.mutable.Map

/**
 * X, Y board coordinate for a card, can go negative
 * Board starts with 0, 0
 */
case class BoardLoc(val x: Int, val y: Int) {
  def neighborLoc(orientation: CardOrientation) = {
    orientation match {
      case UP => new BoardLoc(x, y + 1)
      case DOWN => new BoardLoc(x, y - 1)
      case RIGHT => new BoardLoc(x + 1, y)
      case LEFT => new BoardLoc(x - 1, y)
    }
  }
}

//card played, facing orientation, player who played it
//stored in the board Map
case class PlayedCard(val card: Card, val orientation: CardOrientation, val player: ActorRef) {
  def exits = card.exits(orientation)
}

/**
 * Represents the current state of the board and played cards
 * Currently implemented as a Map of (x, y) keys to PlayedCards
 */
class Board() {
  val boardMap = scala.collection.mutable.Map[BoardLoc, PlayedCard]()

  //represents all the open playable locations
  val fringe = scala.collection.mutable.Set[BoardLoc](new BoardLoc(0, 0))

  def addCardToStation(loc: BoardLoc,
    card: Card, orientation: CardOrientation,
    player: ActorRef): Boolean = {

    val playLoc = boardMap.size match {
      // initial board location is always 0, 0
      case 0 => new BoardLoc(0, 0)
      case _ => loc
    }

    isLegalMove(playLoc, card, orientation) match {
      case true => {
        boardMap += ((playLoc, new PlayedCard(card, orientation, player)))
        updateFringe(playLoc, card, orientation)
        true
      }
      case false =>
        false
    }
  }

  def updateFringe(loc: BoardLoc, card: Card, orientation: CardOrientation) = {
    //for each exit of card, if that loc isn't already on the board, add to fringe
    for (exit <- card.exits(orientation)) {
      val exitLoc = loc.neighborLoc(exit)
      if (!boardMap.contains(exitLoc)) {
        fringe += exitLoc
      }
    }
    //remove played location from fringe
    fringe -= loc
  }

  def sabotageStation(loc: BoardLoc): Boolean = {
    isLegalSabotage(loc) match {
      case true =>
        //remove the station piece from the board
        boardMap -= loc

        //TODO update fringe after sabotage
        true
      case false =>
        false
    }

  }

  def isLegalSabotage(loc: BoardLoc): Boolean = {
    //TODO check sabotage for station division
    boardMap.contains(loc)
  }

  def isClosed = { fringe.size == 0 }

  def legalBoardLocs(card: Card): Set[BoardLoc] = {
    //TODO list of legal x,y coords for card
    fringe.toSet
  }

  //TODO determine if move is playable
  def isLegalMove(loc: BoardLoc,
    card: Card, orientation: CardOrientation): Boolean = {
    boardMap.size match {
      case 0 => true //any location will be moved to 0, 0
      case size =>
        //in the fringe of open spots
        fringe.contains(loc) &&
          //for each direction, make sure the card's exit or lack of exit
          //matches its neighbor if it has one
          CardOrientation.all.forall { direction =>
            boardMap.get(loc.neighborLoc(direction)) match {
              case None => true //empty neighbor always valid
              case Some(playedCard) => {
                //return true if played card's exit direction matches
                //layed neighbor's opposite exit
                //println("Checking " + loc + " " + card + " " + orientation)
                //println("vs. " + playedCard.card + " " + playedCard.orientation)
                card.exits(orientation).contains(direction) ==
                  playedCard.exits.contains(direction.opposite)
              }
            }
          }
    }

  }

  def immutableBoard = boardMap.toMap

  def payChoicePaths(from: BoardLoc, to: BoardLoc) = Set[List[BoardLoc]](List(from, to))
  
  def getMinX = boardMap.keys.size match { case 0 => 0; case any => boardMap.keys.minBy(_.x).x }
  def getMaxX = boardMap.keys.size match { case 0 => 0; case any => boardMap.keys.maxBy(_.x).x }
  def getMinY = boardMap.keys.size match { case 0 => 0; case any => boardMap.keys.minBy(_.y).y }
  def getMaxY = boardMap.keys.size match { case 0 => 0; case any => boardMap.keys.maxBy(_.y).y }
  
}

