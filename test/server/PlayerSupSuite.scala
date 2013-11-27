package server

import akka.actor._
import org.scalatest.FunSuite

class PlayerSupSuite extends FunSuite {
  test("player sup start") {
    val system = ActorSystem("SBScalaPlayserSupSuite")
   
    val playerSup = system.actorOf(Props[PlayerSup], name = "PlayerSup")
    
    playerSup ! NewPlayer("TestPlayer1")
    
    playerSup ! NewPlayer("TestPlayer2")
    
    playerSup ! StatusRequest()
    
    
  }
  
  test("look up player actor"){
    val system = ActorSystem("SBScalaPlayserSupSuite")
   
    val playerSup = system.actorOf(Props[PlayerSup], name = "PlayerSup")
    
    playerSup ! NewPlayer("TestPlayer1")
        
  }

}