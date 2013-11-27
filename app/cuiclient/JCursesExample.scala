package cuiclient

import jcurses.system._
import jcurses.widgets._
import jcurses.util._
import jcurses.event._
import java.io._
import jcurses.widgets.WidgetsConstants.ALIGNMENT_CENTER
import jcurses.widgets.WidgetsConstants.ALIGNMENT_TOP

/**
 * Example J Curses window, based on test code from the JCurses source
 * @author bwilliams
 *
 */
object JCursesExample extends App {
  override def main(args: Array[String]) = {
    
    System.setProperty("jcurses.protocol.filename", "jcurses.log");
    Protocol.activateChannel(Protocol.DEBUG);
    Protocol.debug("Program begin");
    //val url = ClassLoader.getSystemClassLoader().getResource("jcurses/system/Toolkit.class")
    //Protocol.debug(url.toString);
    
    Toolkit.beep();

    val test = new JCursesExample(150, 45)
    test.show();
    
    //clear screen test
    //Toolkit.clearScreen(new CharColor(CharColor.BLUE, CharColor.BLUE, CharColor.REVERSE));

  }
}

class JCursesExample(width: Int, height: Int) extends Window(0, 0, width, height, true, "Test Window")
  with ItemListener with ActionListener with ValueChangedListener with WindowListener{

  val bp = new BorderPanel()

  val check1 = new CheckBox()
  check1.addListener(this);
  val check2 = new CheckBox(true)
  check2.addListener(this);

  val label1 = new Label("checkbox1");
  val label2 = new Label("checkbox2");

  val okBtn = new Button("OK");
  okBtn.setShortCut('o');
  okBtn.addListener(this);

  val cancelBtn = new Button("Cancel");
  cancelBtn.setShortCut('p');
  cancelBtn.addListener(this);

  val itemList = new List();
  itemList.add("item1");
  itemList.add("item201234567890123456789");
  itemList.add("item3");
  itemList.add("item4");
  itemList.add("item5");
  itemList.addListener(this);
  itemList.getSelectedItemColors().setColorAttribute(CharColor.BOLD);

  val textArea = new TextArea(-1, -1, "1111\n2222\n3333\n4444\n\n66666\n77777\n888888\n99999999999999999\n1010100101");

  val passField = new PasswordField();

  val manager1 = new GridLayoutManager(1, 1);
  import jcurses.widgets.WidgetsConstants._
  getRootPanel().setLayoutManager(manager1);
  manager1.addWidget(bp, 0, 0, 1, 1, ALIGNMENT_CENTER, ALIGNMENT_CENTER);

  val manager = new GridLayoutManager(2, 7);
  bp.setLayoutManager(manager);
  manager.addWidget(label1,    0, 0, 1, 1,ALIGNMENT_CENTER, ALIGNMENT_CENTER);
  manager.addWidget(label2,    0, 1, 1, 1,ALIGNMENT_CENTER, ALIGNMENT_CENTER);
  manager.addWidget(check1,    1, 0, 1, 1,ALIGNMENT_CENTER, ALIGNMENT_CENTER);
  manager.addWidget(check2,    1, 1, 1, 1,ALIGNMENT_CENTER, ALIGNMENT_CENTER);
  manager.addWidget(itemList,  0, 2, 1, 4, ALIGNMENT_TOP, ALIGNMENT_CENTER);
  manager.addWidget(textArea,  1, 2, 1, 2, ALIGNMENT_CENTER, ALIGNMENT_CENTER);
  manager.addWidget(passField, 1, 3, 1, 2, ALIGNMENT_CENTER, ALIGNMENT_CENTER);
  manager.addWidget(okBtn,     0, 6, 1, 1, ALIGNMENT_CENTER, ALIGNMENT_CENTER);
  manager.addWidget(cancelBtn, 1, 6, 1, 1, ALIGNMENT_CENTER, ALIGNMENT_CENTER);

  addListener(this)
  //end of initialization

  override def actionPerformed(event: ActionEvent) = {
    val w = event.getSource();
    if (w == okBtn) {
      Protocol.debug("point1");
      val dial = new FileDialog("File open, source is okBtn");
      Protocol.debug("point2");
      dial.show();
      Protocol.debug("point3");

      if (dial.getChoosedFile() != null) {
        new Message("Message Dialog", dial.getChoosedFile().getAbsolutePath() + "", "OK").show();
      }

      Protocol.debug("point4");
      passField.setVisible(!passField.isVisible());
      pack();
      paint();

    } else {
      new Message("Other Action Event Message", "01234567890\nassssssss\naaaaaaa\naaaaaa", "CANCEL").show();
      val menu = new PopUpMenu(53, 5, "test");
      for (i <- 1 to 100) {
        if ((i == 35) || (i == 4)) {
          menu.addSeparator();
        } else {
          menu.add("item" + i);
        }
      }

      menu.show();

      new Message("Menu Selection Message", menu.getSelectedItem() + ":" + menu.getSelectedIndex(), "OK").show();

    }

    //close();

  }

  override def stateChanged(e: ItemEvent) = {
    Protocol.debug("Item Event: " + e.getItem() + ":" + e.getType());
    new Message("Item Event State Changed", e.getItem() + ":" + e.getType(), "OK").show();
  }

  override def valueChanged(e: ValueChangedEvent) = {
    Protocol.debug("ValueChangedEvent index: " + itemList.getSelectedIndex());
    new Message("Value Changed", "ValueChangedEvent: ", "" + itemList.getSelectedIndex()).show();
  }

  override def windowChanged(event: WindowEvent) = {
    Protocol.debug("window event: " + event.getType())
    event.getType match {
      case WindowEvent.CLOSING => {
        Protocol.debug("CLOSING window event");
        event.getSourceWindow().close();
        
      }
      case WindowEvent.ACTIVATED => Protocol.debug("ACTIVATED window event")
      case WindowEvent.CLOSED => { 
        Protocol.debug("CLOSED window event")
        System.exit(0)
      }
      case WindowEvent.DEACTIVATED => Protocol.debug("DEACTIVATED window event")
    }
  }

}