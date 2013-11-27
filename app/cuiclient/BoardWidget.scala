package cuiclient

import jcurses.system.CharColor
import jcurses.system.Toolkit
import jcurses.util.Protocol
import jcurses.util.Rectangle
import jcurses.widgets.BorderPanel
import jcurses.widgets.IScrollable
import jcurses.widgets.Widget
import jcurses.widgets.ScrollbarPainter
import game._

/**
 * Displays the station by drawing all the played cards
 * Allows a player to navigate around the board
 * Allows a player to place a tile (similar to Carcassonne on XBLA)
 * Allows a player to choose a tile for sabotage
 */
class BoardWidget(val board: Board) extends SBWidget with IScrollable {
  //init
  val scrollbars = new ScrollbarPainter(this)

  val drawnCardWidth = 5
  val drawnCardHeight = 5

  sealed abstract class SelectionMode
  case object NORMAL extends SelectionMode
  case class PLACINGCARD(val card:Card) extends SelectionMode
  case object SELECTINGCARD extends SelectionMode
  case object SELECTINGPATH extends SelectionMode

  var selectionMode:SelectionMode = NORMAL

  override protected def getPreferredSize(): Rectangle = new Rectangle(118, 33)

  override protected def doPaint(): Unit = {
    val rect = getSize().clone()
    rect.setLocation(getAbsoluteX(), getAbsoluteY());
    scrollbars.paint()
    drawBoard(rect, new BoardLoc(0, 0))

  }

  override protected def doRepaint(): Unit = {
    doPaint
  }

  override def isFocusable() = selectionMode match {
    case NORMAL => hasHorizontalScrollbar || hasVerticalScrollbar //if the viewport is scrollable, focus is used to scroll
    case PLACINGCARD(card) => true
    case SELECTINGCARD => true
    case SELECTINGPATH => true
  }

  /**
   * Draws the board pieces inside the rectangle, with centerViewLoc as the
   * BoardLoc to draw in the middle of the viewable rectangle
   */
  def drawBoard(rect: Rectangle, centerViewLoc: BoardLoc) = {

    Toolkit.printString("Board", rect, getColors);
    //the center of the viewable rectangle, it will be the center of the
    //5x5 box used to draw the playedcard in the centerViewLoc
    val centerX = (scala.math.floor(rect.getWidth / 2 - rect.getX)).toInt
    val centerY = (scala.math.floor(rect.getHeight() / 2 - rect.getY)).toInt

    val drawBoard = board.immutableBoard
    //for each loc/card pair in the board map
    //TODO draw each card in the color of its player
    drawBoard.foreach {
      case (loc, pcard) =>
        //calculate the draw rectangle for the border
        val borderX = centerX - 2 + drawnCardWidth * loc.x
        // Ys go Up in the board keys, but down when drawing things
        val borderY = centerY - 2 - drawnCardHeight * loc.y

        Protocol.debug("Drawing " + pcard.card + ":" + pcard.orientation + " in position " + loc)

        val cardBorder = new Rectangle(borderX, borderY, drawnCardWidth, drawnCardHeight)
        Toolkit.drawBorder(cardBorder, getColors)

        //draw the card itself, inside the card border, one line at a time
        val cardX = borderX + 1
        val cardY = borderY + 1
        val cardTextRect = new Rectangle(borderX + 1, borderY + 1, drawnCardWidth - 1, 1)

        //card top
        cardTextRect.setLocation(cardX, cardY)
        Toolkit.printString(getCardStringRow(pcard.card, pcard.orientation, 1), cardTextRect, getColors());
        //card mid
        cardTextRect.setLocation(cardX, cardY + 1)
        Toolkit.printString(getCardStringRow(pcard.card, pcard.orientation, 2), cardTextRect, getColors());
        //card bot
        cardTextRect.setLocation(cardX, cardY + 2)
        Toolkit.printString(getCardStringRow(pcard.card, pcard.orientation, 3), cardTextRect, getColors());
    }

  }

  val scrollbarDefaultColors = new CharColor(CharColor.BLACK, CharColor.WHITE, CharColor.REVERSE);

  def scrollbarColors = getScrollbarDefaultColors()

  def getScrollbarDefaultColors() {
    scrollbarDefaultColors
  }

  /* IScrollable Methods */
  //TODO show scrollbars only if drawn map is bigger than the window
  /**
   * This method returns true, if the using widget has a horizontal scrollbar,
   * ( independent of the size of it, this can be also empty)
   *
   * @return true, if the scrollbar is to be paint, false otherwise
   */

  override def hasHorizontalScrollbar() = true

  /**
   * This method returns true, if the using widget has a vertical scrollbar,
   * ( independent of the size of it, this can be also empty)
   *
   * @return true, if the scrollbar is to be paint, false otherwise
   */
  override def hasVerticalScrollbar() = true

  /**
   *  The method returns the rectangle of the border, on which scrollbars are to be painted
   *
   * @return rectangle of the border
   */
  override def getBorderRectangle() = {
    val rect = getSize().clone()
    rect.setLocation(getAbsoluteX(), getAbsoluteY())
    rect
  }

  /**
   * The method returns colors, with which the border is to be paint
   *
   * @return border colors
   */

  override def getBorderColors() = getDefaultColors

  /**
   * The method returns colors, with which scrollbars are to be paint
   *
   * @return scrollbar colors
   */

  override def getScrollbarColors() = getDefaultColors

  //TODO set scrollbar offset and length based on size/location of board view

  /**
   *  The method returns the offset of the horizontal scrollbar as part of the length of the
   *  side of the border rectangle ( 0 <=value < 1.0 )
   *
   * @return horizontal scrollbar offset
   */
  override def getHorizontalScrollbarOffset() = 0

  /**
   *  The method returns the length of the horizontal scrollbar as part of the length of the
   *  side of the border rectangle ( 0 < value <= 1.0 )
   *
   * @return vertical scrollbar o
   */
  override def getHorizontalScrollbarLength() = .25f

  /**
   *  The method returns the offset of the vertical scrollbar as part of the length of the
   *  side of the border rectangle ( 0 < =value < 1.0 )
   */
  override def getVerticalScrollbarOffset() = 0

  /**
   *  The method returns the length of the vertical scrollbar as part of the length of the
   *  side of the border rectangle ( 0 <value <= 1.0 )
   */
  override def getVerticalScrollbarLength() = .25f

}