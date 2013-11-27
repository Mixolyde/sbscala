package cuiclient

import jcurses.util.Rectangle
import jcurses.widgets.DefaultLayoutManager
import jcurses.widgets.Label
import jcurses.widgets.Panel

/**
 * Displays the current player's info: hand, deferred cards, cash
 */
class PlayerInfoPanel extends Panel {
  val playerInfoManager = new DefaultLayoutManager
  import jcurses.widgets.WidgetsConstants._
  
  this.setLayoutManager(playerInfoManager)
  
  val labelPanelWidth = 9

  initCardLabelsPanel
  
  val handCardsPanel = new CardList("Hand")
  //TODO unset test data
  //TODO set real card data on update
  val testCards = testPlayerData(4).hand
  handCardsPanel.setCards(testCards)
  
  val deferredCardsPanel = new CardList("Deferred")
  //TODO unset test data
  //TODO set real card data on update
  deferredCardsPanel.setCards(testCards)

  
  playerInfoManager.addWidget(handCardsPanel, labelPanelWidth, 0, 
      handCardsPanel.getPreferredSize.getWidth(), 
      handCardsPanel.getPreferredSize.getHeight(),
      ALIGNMENT_TOP, ALIGNMENT_LEFT);

  playerInfoManager.addWidget(deferredCardsPanel, labelPanelWidth + handCardsPanel.getPreferredSize.getWidth() + 8, 0, 
      80, 
      deferredCardsPanel.getPreferredSize.getHeight(),
      ALIGNMENT_TOP, ALIGNMENT_LEFT);  
  //done initializing
  
  
  def initCardLabelsPanel:Unit = {
    val cardLabelsPanel = new Panel()
    val cardLabelsManager = new DefaultLayoutManager
    cardLabelsPanel.setLayoutManager(cardLabelsManager)
    
    //add two labels for cash on hand
    cardLabelsManager.addWidget(new Label("Cash:"), 0, CardWidget.height-5, 
      labelPanelWidth, 
      1,
      ALIGNMENT_TOP, ALIGNMENT_LEFT);
    
    //TODO set real cash total on update
    cardLabelsManager.addWidget(new Label("123"), 0, CardWidget.height-4, 
      labelPanelWidth, 
      1,
      ALIGNMENT_TOP, ALIGNMENT_LEFT);
    
    //row - 3 is blank
    
    //add the two labels for card stats
    cardLabelsManager.addWidget(new Label("Rank:"), 0, CardWidget.height-2, 
      labelPanelWidth, 
      1,
      ALIGNMENT_TOP, ALIGNMENT_LEFT);
    
    cardLabelsManager.addWidget(new Label("Cost:"), 0, CardWidget.height-1, 
      labelPanelWidth, 
      1,
      ALIGNMENT_TOP, ALIGNMENT_LEFT);
    
    //add this label panel to the overall info panel
    playerInfoManager.addWidget(cardLabelsPanel, 0, 0, 
      labelPanelWidth, 
      CardWidget.height,
      ALIGNMENT_TOP, ALIGNMENT_LEFT);
  }
}