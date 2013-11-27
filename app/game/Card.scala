package game
import org.apache.commons.lang.NotImplementedException
import scala.util.Random

/**
 * Case object representing the facing of a card: Up, Right, Down, Left
 */
sealed abstract class CardOrientation {
  /**
   * Returns the orientation opposite of this one (up, down) (left, right)
   */
  def opposite = { CardOrientation.all( (CardOrientation.all.indexOf(this) + 2) % 4) }
  
  /**
   * Returns the orientation one position clockwise from this one
   */
  def cw = { CardOrientation.all( (CardOrientation.all.indexOf(this) + 1) % 4) }
  
  /**
   * Returns the orientation one position counter-clockwise from this one
   */
  def ccw = { CardOrientation.all( (CardOrientation.all.indexOf(this) + 3) % 4) }
}
case object UP extends CardOrientation
case object DOWN extends CardOrientation
case object LEFT extends CardOrientation
case object RIGHT extends CardOrientation

object CardOrientation {
  //static utility value
  val all = List(UP, RIGHT, DOWN, LEFT)
}

/**
 * Parent class for all cards in the game
 */
sealed abstract class Card(val name: String,
  val rank: Int, val cost: Int, val isCap: Boolean = false){
  
  /**
   * When a card is played, it will have an orientation for where the "top" is pointing
   * A cap's exit is its orientation, for simplicity
   * Power station has all exits  
   * Habitat is all exits except its orientation (the T-piece)
   */
  def exits(orientation: CardOrientation) :Set[CardOrientation] = {
    (this, orientation) match {
      case (POW, any) => Set(UP, DOWN, LEFT, RIGHT)     // the + piece has all exits
      case (card, any) if card.isCap => Set(any)          // a cap's exit is its orientation
      case (HAB, any) => Set(UP, DOWN, LEFT, RIGHT).filterNot(_ == any).toSet  //the T-piece is every exit except its orientation
      case (LAB, any) => Set(any, any.cw)               // lab is the L-shaped piece
      case (FAC, any) => Set(any, any.opposite)         // factory is the straight piece
      case _ => throw new IllegalArgumentException("Cannot find this card's exits.")
    }
  }
  
  override def toString = name

}

/**
 * Case objects for each type of card, with default values
 * Pattern matching: card match {
 *   case REC => { handle rec }
 * }
 */
case object REC extends Card("Recreation", 0, -1, true)
case object DOC extends Card("Docking Bay", 1, -1, true)
case object COM extends Card("Communication", 2,-1, true)
case object LAB extends Card("Laboratory", 3, 1)
case object FAC extends Card("Factory", 4, 1)
case object HAB extends Card("Habitat", 5, 2)
case object POW extends Card("Power Station", 6, 3)
case object SAB extends Card("Sabotage", 7, 1)

/**
 * Card object with static values and utility methods
 */
object Card {
  val HANDSIZE = 5
  val DECKSIZE = 20
  
  def sortedDeck: List[Card] = 
    List.fill(3) (REC).toList ++ 
    List.fill(2) (DOC).toList ++ 
    List.fill(3) (COM).toList ++ 
    List.fill(4) (LAB).toList ++ 
    List.fill(3) (FAC).toList ++ 
    List.fill(2) (HAB).toList ++ 
    List.fill(1) (POW).toList ++ 
    List.fill(2) (SAB).toList

  def shuffledDeck(): List[Card] = {
    // shuffle the deck twice
    val shuffledDeck = (1 to 2).foldLeft(sortedDeck) {
      
      (deck, int) => shuffleDeck(deck)
    }
    shuffledDeck
  }
  
  def shuffleDeck(deck:List[Card]) : List[Card] = shuffleDeck(deck, List())
  //shuffle the deck by recursively pulling a random card from the input deck and
  //appending onto the accumulator
  private def shuffleDeck(deck:List[Card], accum:List[Card]) : List[Card] = {
    deck match {
      case Nil => accum
      case cards => {
        val randIndex = Random.nextInt(deck.length)
        val randCard = deck(randIndex)
        //hacky way to remove an element at an index by returning a new list
        shuffleDeck( (deck take randIndex) ::: (deck drop (randIndex + 1)), randCard::accum)

      }
    }
  } 
  
  def newHandAndDeck() : (List[Card], List[Card]) = {
    val deck = Card.shuffledDeck
    (deck take Card.HANDSIZE, deck drop Card.HANDSIZE)
  }
}

