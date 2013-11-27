package server

import akka.actor._
import akka.testkit.TestActorRef
import org.scalatest.FunSuite

import game._

class GameServerSuite extends FunSuite{
  test("Game Server Start") {
    val system = ActorSystem("SBScalaGameServerSuite")
   
    val gameServer = system.actorOf(Props[GameSup], name = "GameServer")
    
    gameServer ! StatusRequest()

  }
  
  test("Game create and start") {
    implicit val system = ActorSystem("SBScalaGameServerSuite")
   
    //create 2 players
    val playerRef1 = TestActorRef(new Player("Player1"))
    val playerRef2 = TestActorRef(new Player("Player2"))
    //val actor = actorRef.underlyingActor

    val gameServer = system.actorOf(Props[GameSup], name = "GameServer")
    
    gameServer ! NewGame(List(playerRef1, playerRef2))
    
    // playerRefs should receive a game created message with the ref

  }
  
  test("Multiple game starts") {
    implicit val system = ActorSystem("SBScalaGameServerSuite")
   
    //create 2 players
    val playerRef1 = TestActorRef(new Player("Player1"))
    val playerRef2 = TestActorRef(new Player("Player2"))

    val gameServer = system.actorOf(Props[GameSup], name = "GameServer")
    
    gameServer ! NewGame(List(playerRef1))
    
    gameServer ! NewGame(List(playerRef2))
    
  }

}