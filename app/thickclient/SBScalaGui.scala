package thickclient

import akka.actor._
import akka.dispatch.Await
import akka.pattern.ask
import akka.util.Timeout
import akka.util.duration._
import scala.swing._

import server._

object SBScalaGui { //extends SimpleSwingApplication {
  SBApp.main(Array[String]())
  
  def top = new MainFrame {
    title = "Scala Swing Demo"

    menuBar = new MenuBar {
      contents += new Menu("File") {
        contents += new MenuItem(Action("New Player") {
          println("Action '" + title + "' invoked")
        })
        contents += new Separator
        contents += new MenuItem(Action("Quit") {
          println("Action '" + title + "' invoked")
        })
      }
    }

    val duration = 5 seconds
    implicit val timeout = Timeout(duration)
    
    val future = SBApp.playerSup ask new NewPlayer("FirstPlayer")
    val firstPlayer = Await.result(future, duration).asInstanceOf[ActorRef]

    contents = new GamePanel(firstPlayer)
    size = new java.awt.Dimension(600, 400)

  }
}