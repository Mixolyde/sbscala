package server

import akka.actor._

object SBApp extends App {
   val system = ActorSystem("SBScalaSystem")
   
   val gameSup = system.actorOf(Props[GameSup], name = "GameSup")
   
   val playerSup = system.actorOf(Props[PlayerSup], name = "PlayerSup")
   
   println("Started SBApp")
   
   def shutdownSBApp = {
     println("Shutting down")
     system.shutdown
   }
   
}