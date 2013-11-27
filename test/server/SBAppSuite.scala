package server

import org.scalatest.FunSuite
import scala.actors._
import scala.actors.Actor._
import akka.actor.UnhandledMessage

class SBAppSuite extends FunSuite {
  
  test("start and stop app") {
    SBApp.main(Array[String]())
    
    Thread.sleep(4000)
    
    SBApp.shutdownSBApp
    
  }
  
  test("get game sup") {
    SBApp.main(Array[String]())
    
    val gameSupRef = SBApp.gameSup
    
    gameSupRef ! new InfoMessage("Test message to game server")
  }
  
  test("get player sup") {
    SBApp.main(Array[String]())
    
    val playerSupRef = SBApp.playerSup
    
    playerSupRef ! new InfoMessage("Test message to player super")
  }

}