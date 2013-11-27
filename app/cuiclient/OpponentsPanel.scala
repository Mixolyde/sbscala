package cuiclient

import jcurses.widgets.DefaultLayoutManager
import jcurses.widgets.Label
import jcurses.widgets.Panel

class OpponentsPanel extends Panel {
  val opponent1InfoPanel = new OpponentInfoPanel
  opponent1InfoPanel.setPlayerData(testPlayerData(1))
  val opponent2InfoPanel = new OpponentInfoPanel
  opponent2InfoPanel.setPlayerData(testPlayerData(2))
  val opponent3InfoPanel = new OpponentInfoPanel
  opponent3InfoPanel.setPlayerData(testPlayerData(3))
  
  //create panel for displaying the other three player's data
  val opponentsManager = new DefaultLayoutManager();
  import jcurses.widgets.WidgetsConstants._
  this.setLayoutManager(opponentsManager)
  val playerInfoWidth = 39
  opponentsManager.addWidget(new Label("Opponents"), 0, 0, playerInfoWidth, 2, ALIGNMENT_TOP, ALIGNMENT_LEFT);
  opponentsManager.addWidget(opponent1InfoPanel, 0, 2, playerInfoWidth, 4, ALIGNMENT_TOP, ALIGNMENT_LEFT);
  opponentsManager.addWidget(opponent2InfoPanel, 0, 6, playerInfoWidth, 4, ALIGNMENT_TOP, ALIGNMENT_LEFT);
  opponentsManager.addWidget(opponent3InfoPanel, 0, 10, playerInfoWidth, 4, ALIGNMENT_TOP, ALIGNMENT_LEFT);
  
  val deckLabel = "Full Deck:\n3xRec, 2xDoc, 3xCom, 4xLab\n3xFac, 2xHab, 1xPow, 2xSab"
  opponentsManager.addWidget(new Label(deckLabel), 0, 30, playerInfoWidth, 3, ALIGNMENT_TOP, ALIGNMENT_LEFT);

}