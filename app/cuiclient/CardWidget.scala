package cuiclient

import java.awt.Paint
import jcurses.util.Protocol
import jcurses.event.ItemEvent
import jcurses.event.ItemListenerManager
import jcurses.system.CharColor
import jcurses.system.InputChar
import jcurses.system.Toolkit
import jcurses.util.Rectangle
import jcurses.widgets.DefaultLayoutManager
import jcurses.widgets.Label
import jcurses.widgets.Panel
import jcurses.widgets.Widget
import scala.collection.mutable.MutableList
import game._
/**
 * Displays a single card, either in the player's hand or deferred view
 * Requires a reference to the parent card list so it can check for focus
 */
class CardWidget(val parent: CardList, val card: Card) extends SBWidget {

  //a selected card has been chosen by the player to be played
  val selectedCardColors = new CharColor(CharColor.RED, CharColor.WHITE, CharColor.REVERSE)

  //a trackeded card is the focused card that could be selected
  val trackedCardColors = new CharColor(CharColor.BLUE, CharColor.WHITE, CharColor.REVERSE);

  //asks the parent list if this widget is being tracked
  def tracked = {parent.cardWidgets(parent.tracked) == this}
  
  //if no selection, return false
  def selected = { parent.selected match {
    case None => false
    case Some(any) => parent.cardWidgets(any) == this
  }}

  protected def getPreferredSize(): Rectangle = new Rectangle(5, 7)

  //rec  doc  com  lab  fac  hab  pow  sab
  //      |    |    |    |         |   \|/
  // O-   O    O    +-   +   -+-  -+-  -*-
  //                     |    |    |   /|\
  //              
  //  1    1    1    3    4    5    6    7
  // -1   -1   -1    1    1    2    3    1

  protected def doPaint(): Unit = {
    val rect = getSize().clone()
    val absX = getAbsoluteX()
    val absY = getAbsoluteY()

    //all text paints are in 3x1 blocks
    rect.setWidth(3)
    rect.setHeight(1)

    rect.setLocation(absX, absY)
    Toolkit.printString(card.name, rect, getColors());

    //card top
    rect.setLocation(absX, absY + 1)
    Toolkit.printString(getCardStringRow(card, UP, 1), rect, getColors());
    //card mid
    rect.setLocation(absX, absY + 2)
    Toolkit.printString(getCardStringRow(card, UP, 2), rect, getColors());
    //card bot
    rect.setLocation(absX, absY + 3)
    Toolkit.printString(getCardStringRow(card, UP, 3), rect, getColors());

    //draw blank line for coloring
    rect.setLocation(absX, absY + 4)
    Toolkit.printString("   ", rect, getColors());

    //card rank
    rect.setLocation(absX, absY + 5)
    Toolkit.printString(" " + card.rank + " ", rect, getColors());

    //card cost
    rect.setLocation(absX, absY + 6)
    card.cost match {
      case x if x < 0 => Toolkit.printString("" + x + " ", rect, getColors());
      case x => Toolkit.printString(" " + x + " ", rect, getColors());
    }

  }

  def doRepaint(): Unit = {
    doPaint
  }

  /**
   *  @return colors of the widget
   */
  override def getColors(): CharColor = {
    (parent.hasFocus(), selected, tracked) match {
      case (focus, true, tracked) => selectedCardColors    //if selected, always color as selected
      case (true, selected, true) => trackedCardColors     //if focused and tracked, show tracked
      case (focus, selected, tracked) => getDefaultColors  //else, show default colors
    }
  }

}

object CardWidget {
  val width = 5
  val height = 8 //cards are 7 rows + 1 for the title label
}

/**
 * Panel for displaying a horizontal list of cards and allows single selection/deselection
 */
class CardList(val title: String) extends Panel() {

  //selected card has been chosen for play, -1 for no selection
  //TODO set no selection value to None
  var selected: Option[Int] = None
  //tracked card is the one the cursor is on, first card always tracked
  var tracked: Int = 0

  val listenerManager = new ItemListenerManager()

  val cardListManager = new DefaultLayoutManager();
  val preferredSize = new Rectangle(0, CardWidget.height)
  this.setLayoutManager(cardListManager)
  import jcurses.widgets.WidgetsConstants._

  val titleLabel = new Label(title)
  cardListManager.addWidget(titleLabel,
    0,
    0,
    title.length(),
    1,
    ALIGNMENT_TOP, ALIGNMENT_LEFT);

  //the list of card/cardwidget pairs to display/select
  var cardWidgets = MutableList[CardWidget]()

  //end of initialization

  override def isFocusable() = {cardWidgets.size > 0}

  override def getPreferredSize(): Rectangle = preferredSize

  def setCards(newCards: scala.collection.immutable.List[Card]) = {
    cardWidgets = MutableList[CardWidget]()

    preferredSize.setWidth(newCards.length * CardWidget.width)
    for (index <- 0 until newCards.length) {
      val cardWidget = new CardWidget(this, newCards(index))
      cardListManager.addWidget(cardWidget,
        0 + index * CardWidget.width,
        1,
        CardWidget.width,
        CardWidget.height,
        ALIGNMENT_TOP, ALIGNMENT_LEFT);
      cardWidgets += ( cardWidget )
    }
  }

  def getItemsCount() = {
    cardWidgets.size
  }

  def dispatchEvent(index: Int, value: Boolean) {
    import jcurses.event.ItemEvent._
    val event: ItemEvent = new ItemEvent(this, index, cardWidgets(index),
      value match { case true => SELECTED; case false => DESELECTED })
    listenerManager.handleEvent(event)
  }

  def isSelected(pos: Int): Boolean = {
    selected match {
      case None => false
      case Some(any) => any == pos
    }
  }

  def isSelectable(i: Int) = {
    true
  }

  
  /**
   *  Selects an item at the specified position
   *
   * @param index position
   */
  def select(index: Int) {
    setSelection(index, true)
    dispatchEvent(index, true)
  }

  /**
   *  Deselects an item at the specified position
   *
   * @param index position
   */
  def deselect(index: Int) {
    Protocol.debug("Calling deselect from handle input: " + index)
    setSelection(index, false);
    dispatchEvent(index, false);
  }
  
  /**
   * Updates the selection and redraws appropriately
   */
  def setSelection(index: Int, value: Boolean) = {
    //match based on various boolean tests
    (isSelected(index), value, selected) match {
      case (true, true, any) => Unit //already selected, do nothing
      case (true, false, Some(any) ) =>
        // unsetting selection -1 is value for no selection
        Protocol.debug("Unselecting current selection")
        val oldSelected = any
        selected = None
        redrawItem(oldSelected)
      case (false, true, None) =>
        //setting a brand new selection
        selected = Some(index)
        if (isVisible()) {
          redrawItem(index)
        }
      case (false, true, Some(any)) =>
        //setting new selection, unsetting old one
        //Protocol.debug("Changing selection from " + selected + " to " + index)
        val oldSelected = any
        selected = Some(index)
        if (isVisible()) {
          //Protocol.debug("Redrawing " + oldSelected + " and " + selected)
          redrawItem(oldSelected)
          redrawItem(index)
        }
    }
  }

  def getSelectedCard(): Option[Card] = {
    selected match {
      case None => None
      case Some(index) => Some(cardWidgets(index).card)  //get the card from the (card, cardwidget) pair
    }
  }

  /**
   * On a focus change we have to change how the tracked item is drawn
   */
  override def focus() = {
    Protocol.debug("Card List: " + title + " received focus")
    redrawTrackedItem
  }

  override def unfocus() = {
    Protocol.debug("Card List: " + title + " lost focus")
    redrawTrackedItem
  }

  def redrawTrackedItem = {
    redrawItem(tracked)
  }

  //value of key character used to select the card that is currently tracked
  val changeStatusChar = new InputChar(' ')

  def getChangeStatusChar() = {
    changeStatusChar
  }

  def setTrack(value: Int): Boolean = {
    tracked = value
    true
  }

  def incrementTrack() = {
    if (tracked < (getItemsCount() - 1)) {
      tracked += 1
      true
    } else {
      false
    }
  }

  def decrementTrack() = {
    if (tracked > 0) {
      tracked -= 1
      true
    } else {
      false
    }
  }


  def redraw(trackedIndex: Int, backupTrackedIndex: Int) {
    redrawItem(trackedIndex);
    redrawItem(backupTrackedIndex);
  }

  def redrawItem(index: Int) = {
    cardWidgets(index).doRepaint
  }

  override def handleInput(ch: InputChar): Boolean = {
    import InputChar._
    val backupTrackedIndex = tracked
    (cardWidgets.length, ch.getCode) match {
      case (0, code) => false
      case (any, code) if code == KEY_LEFT || code == KEY_UP => {
        if (decrementTrack()) {
          redraw(tracked, backupTrackedIndex);
        }
        true

      }
      case (any, code) if code == KEY_RIGHT || code == KEY_DOWN => {
        if (incrementTrack()) {
          redraw(tracked, backupTrackedIndex);
        }
        true

      }
      case (any, code) if code == KEY_HOME || code == KEY_PPAGE => {
        if (setTrack(0)) {
          redraw(tracked, backupTrackedIndex);
        }
        true

      }
      case (any, code) if code == KEY_END || code == KEY_NPAGE => {
        if (setTrack(getItemsCount() - 1)) {
          redraw(tracked, backupTrackedIndex);
        }
        true

      }
      case (any, code) if code == getChangeStatusChar.getCode() => {
        if (isSelected(tracked)) {
          deselect(tracked);
        } else {
          select(tracked);
        }
        true

      }
      case (any, other) => { false }
    }
  }
}