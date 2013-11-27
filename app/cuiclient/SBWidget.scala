package cuiclient

import jcurses.system.CharColor
import jcurses.widgets.Widget

abstract class SBWidget extends Widget {
    val __labelDefaultColors = new CharColor(CharColor.WHITE, CharColor.BLACK);
	
	override def getDefaultColors() = {
      __labelDefaultColors
	}


}