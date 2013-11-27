package cuiclient

import jcurses.util.Protocol
import jcurses.widgets.GridLayoutManager
import jcurses.widgets.Label
import jcurses.widgets.Panel
import jcurses.widgets.TextArea

import game._

class OpponentInfoPanel() extends Panel {
  var nameLabel:Label = new Label("Name")
  var cashLabel:Label = new Label("Cash: 0")
  val deferredLabel:Label = new Label("Deferred")
  var deferredContentLabel:Label = new Label("")
  
  val manager = new GridLayoutManager(2, 4)
  import jcurses.widgets.WidgetsConstants._
  OpponentInfoPanel.this.setLayoutManager(manager)
  
  manager.addWidget(nameLabel,            0, 0, 1, 1, ALIGNMENT_CENTER, ALIGNMENT_LEFT)
  manager.addWidget(cashLabel,            1, 0, 1, 1, ALIGNMENT_CENTER, ALIGNMENT_RIGHT)
  manager.addWidget(deferredLabel,        0, 1, 2, 1, ALIGNMENT_CENTER, ALIGNMENT_LEFT)
  manager.addWidget(deferredContentLabel, 0, 2, 2, 2, ALIGNMENT_CENTER, ALIGNMENT_LEFT)

  def setPlayerData(newData:PlayerData) = {
    //remove old widgets
    manager.removeWidget(nameLabel)
    manager.removeWidget(cashLabel)
    
    //create new ones
    nameLabel = new Label(newData.name)
    cashLabel = new Label("Cash: " + newData.cash.toString)

    //re-add widgets
    manager.addWidget(nameLabel, 0, 0, 1, 1, ALIGNMENT_CENTER, ALIGNMENT_LEFT)
    manager.addWidget(cashLabel, 1, 0, 1, 1, ALIGNMENT_CENTER, ALIGNMENT_LEFT)
    
    repaint()
  }
}