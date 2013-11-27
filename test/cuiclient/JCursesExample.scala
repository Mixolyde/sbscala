package cuiclient

import jcurses.system._
import jcurses.widgets._
import jcurses.util._

/**
 * Example J Curses window, based on test code from the JCurses source
 * @author bwilliams
 *
 */
object JCursesExampleTest extends App {
  override def main(args: Array[String]) = {
    println("Unit Test before main");
  
    JCursesExample.main(args)
    
    Protocol.debug("Unit Test after main");

  }
}

