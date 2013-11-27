package game

import akka.actor._
import org.scalatest.FunSuite

class BoardSuite extends FunSuite {
  test("empty board tests") {
    val emptyBoard = new Board()

    expect(0) { emptyBoard.immutableBoard.keys.size }
    expect(0) { emptyBoard.getMinX }
    expect(0) { emptyBoard.getMaxX }
    expect(0) { emptyBoard.getMinY }
    expect(0) { emptyBoard.getMaxY }
    expect(false) { emptyBoard.isClosed }
    expect(1) { emptyBoard.legalBoardLocs(REC).size }
    expect(true) { emptyBoard.isLegalMove(new BoardLoc(0, 0), REC, UP) }
    expect(true) { emptyBoard.isLegalMove(new BoardLoc(3, 5), REC, UP) }
    expect(false) { emptyBoard.isLegalSabotage(new BoardLoc(0, 0)) }
    expect(false) { emptyBoard.sabotageStation(new BoardLoc(0, 0)) }
  }

  test("single card station") {
    val board = new Board()
    val system = ActorSystem("SBScalaBoardSuite")
    val actor = system.actorOf(Props(new Player("BoardSuitePlayer1")))
    board.addCardToStation(new BoardLoc(3, 5), HAB, UP, actor) //t-shaped piece

    expect(1, "1 played card") { board.immutableBoard.keys.size }
    expect(Set(new BoardLoc(0, 0)), "1 played card location:0,0") { board.immutableBoard.keys }
    expect(0) { board.getMinX }
    expect(0) { board.getMaxX }
    expect(0) { board.getMinY }
    expect(0) { board.getMaxY }
    expect(false, "station is not closed") { board.isClosed }
    expect(3, "fringe size update") { board.fringe.size }
    expect(3, "3 possible exit locations") { board.legalBoardLocs(REC).size }
    expect(true, "possible exit location") { board.legalBoardLocs(REC).contains(new BoardLoc(0, -1)) }
    expect(true, "possible exit location") { board.legalBoardLocs(REC).contains(new BoardLoc(1, 0)) }
    expect(true, "possible exit location") { board.legalBoardLocs(REC).contains(new BoardLoc(-1, 0)) }
    expect(true, "0,0 is sabotageable") { board.isLegalSabotage(new BoardLoc(0, 0)) }
    expect(true, "0,0 is sabotaged") { board.sabotageStation(new BoardLoc(0, 0)) }
  }

  test("exit locations") {
    val loc = BoardLoc(0, 0)
    expect(BoardLoc(0, 1)) { loc.neighborLoc(UP) }
    expect(BoardLoc(0, -1)) { loc.neighborLoc(DOWN) }
    expect(BoardLoc(-1, 0)) { loc.neighborLoc(LEFT) }
    expect(BoardLoc(1, 0)) { loc.neighborLoc(RIGHT) }

  }

  test("capped station size 2") {
    val board = new Board()
    val system = ActorSystem("SBScalaBoardSuite")
    val actor = system.actorOf(Props(new Player("BoardSuitePlayer1")))

    expect(false, "station is not closed") { board.isClosed }
    board.addCardToStation(new BoardLoc(0, 0), REC, UP, actor)
    expect(false, "station is not closed") { board.isClosed }
    board.addCardToStation(new BoardLoc(0, 1), COM, DOWN, actor)
    expect(true, "station is closed") { board.isClosed }
  }

  test("capped station size 5") {
    val board = new Board()
    val system = ActorSystem("SBScalaBoardSuite")
    val actor = system.actorOf(Props(new Player("BoardSuitePlayer1")))

    expect(false, "station is not closed") { board.isClosed }
    board.addCardToStation(new BoardLoc(0, 0), POW, UP, actor) // cross piece in the middle
    expect(false, "station is not closed") { board.isClosed }
    board.addCardToStation(new BoardLoc(0, 1), COM, DOWN, actor)
    expect(false, "station is not closed") { board.isClosed }
    board.addCardToStation(new BoardLoc(0, -1), COM, UP, actor)
    expect(false, "station is not closed") { board.isClosed }
    board.addCardToStation(new BoardLoc(1, 0), DOC, LEFT, actor)
    expect(false, "station is not closed") { board.isClosed }
    board.addCardToStation(new BoardLoc(-1, 0), REC, RIGHT, actor)

    expect(true, "station is closed") { board.isClosed }
  }

  test("neighbor already in fringe") {
    val board = new Board()
    val system = ActorSystem("SBScalaBoardSuite")
    val actor = system.actorOf(Props(new Player("BoardSuitePlayer1")))

    board.addCardToStation(new BoardLoc(0, 0), POW, UP, actor) // cross piece in the middle
    board.addCardToStation(new BoardLoc(0, 1), POW, UP, actor) // cross neighbor

    expect(6, "fringe size update") { board.fringe.size }

    board.addCardToStation(new BoardLoc(1, 0), POW, UP, actor) // L-shape of cross pieces

    expect(7, "fringe size update") { board.fringe.size }
  }

  test("power station isplayable with one card on board") {
    val board = new Board()
    val system = ActorSystem("SBScalaBoardSuite")
    val actor = system.actorOf(Props(new Player("BoardSuitePlayer1")))

    board.addCardToStation(new BoardLoc(0, 0), FAC, UP, actor) // factory is straight piece, exits are UP, DOWN
    expect(1, "1 played card") { board.immutableBoard.keys.size }

    expect(true, "cross any direction 1 north") {
      val loc = BoardLoc(0, 1)
      CardOrientation.all.forall { orientation =>
        board.isLegalMove(loc, POW, orientation)
      }
    }
    expect(true, "cross any direction 1 south") {
      val loc = BoardLoc(0, -1)
      CardOrientation.all.forall { orientation =>
        board.isLegalMove(loc, POW, orientation)
      }
    }
    expect(false, "cross any direction 1 east") {
      val loc = BoardLoc(1, 0)
      CardOrientation.all.forall { orientation =>
        board.isLegalMove(loc, POW, orientation)
      }
    }
    expect(false, "cross any direction 1 west") {
      val loc = BoardLoc(-1, 0)
      CardOrientation.all.forall { orientation =>
        board.isLegalMove(loc, POW, orientation)
      }
    }

    expect(true, "not connected") {
      val loc = BoardLoc(-1, 1)
      CardOrientation.all.forall { orientation =>
        !board.isLegalMove(loc, POW, orientation)
      }
    }

    expect(true, "not connected") {
      val loc = BoardLoc(1, 1)
      CardOrientation.all.forall { orientation =>
        !board.isLegalMove(loc, POW, orientation)
      }
    }

    expect(true, "already played") {
      val loc = BoardLoc(0, 0)
      CardOrientation.all.forall { orientation =>
        !board.isLegalMove(loc, POW, orientation)
      }
    }
  }

  test("cap isplayable with one card on board") {
    val board = new Board()
    val system = ActorSystem("SBScalaBoardSuite")
    val actor = system.actorOf(Props(new Player("BoardSuitePlayer1")))

    board.addCardToStation(new BoardLoc(0, 0), FAC, UP, actor) // factory is straight piece, exits are UP, DOWN
    expect(1, "1 played card") { board.immutableBoard.keys.size }

    expect(true, "north cap facing down") { board.isLegalMove(BoardLoc(0, 1), REC, DOWN) }
    expect(true, "south cap facing up") { board.isLegalMove(BoardLoc(0, -1), REC, UP) }

    expect(true, "north cap other facings") {
      val loc = BoardLoc(0, 1)
      CardOrientation.all.filterNot(_ == DOWN).toSet.forall { orientation =>
        !board.isLegalMove(loc, REC, orientation)
      }
    }

    expect(true, "south cap other facings") {
      val loc = BoardLoc(0, -1)
      CardOrientation.all.filterNot(_ == UP).toSet.forall { orientation =>
        !board.isLegalMove(loc, REC, orientation)
      }
    }

    expect(true, "east cap any direction") {
      val loc = BoardLoc(1, 0)
      CardOrientation.all.forall { orientation =>
        !board.isLegalMove(loc, REC, orientation)
      }
    }

    expect(true, "west cap any direction") {
      val loc = BoardLoc(-1, 0)
      CardOrientation.all.forall { orientation =>
        !board.isLegalMove(loc, REC, orientation)
      }
    }

  }

  test("isplayable into corner") {
    val board = new Board()
    val system = ActorSystem("SBScalaBoardSuite")
    val actor = system.actorOf(Props(new Player("BoardSuitePlayer1")))

    // board for testing in loc X:
    //       |
    // O  X -O
    // |     |
    // |  |  |
    //-O--O--O
    // |  |  
    board.addCardToStation(new BoardLoc(0, 0), POW, UP, actor)
    board.addCardToStation(new BoardLoc(1, 0), LAB, LEFT, actor)
    board.addCardToStation(new BoardLoc(1, 1), HAB, RIGHT, actor)
    board.addCardToStation(new BoardLoc(-1, 0), POW, UP, actor)
    board.addCardToStation(new BoardLoc(-1, 1), REC, DOWN, actor)

    expect(5, "5 played cards") { board.immutableBoard.keys.size }
    expect(5, "5 possible locs") { board.legalBoardLocs(LAB).size }

    val loc = BoardLoc(0, 1) //In the X

    expect(true) { board.isLegalMove(loc, HAB, LEFT) }
    expect(true) { board.isLegalMove(loc, LAB, RIGHT) }

    //any other move is illegal
    expect(true, "hab other facings") {
      CardOrientation.all.filterNot(_ == LEFT).toSet.forall {
        !board.isLegalMove(loc, HAB, _)
      }
    }
    expect(true, "lab other facings") {
      CardOrientation.all.filterNot(_ == RIGHT).toSet.forall {
        !board.isLegalMove(loc, LAB, _)
      }
    }
    expect(true, "cap any direction") {
      CardOrientation.all.forall { !board.isLegalMove(loc, REC, _) }
    }
    expect(true, "fac any direction") {
      CardOrientation.all.forall { !board.isLegalMove(loc, FAC, _) }
    }
    expect(true, "pow any direction") {
      CardOrientation.all.forall { !board.isLegalMove(loc, POW, _) }
    }

  }

  test("station min/max") {
    val board = new Board()
    val system = ActorSystem("SBScalaBoardSuite")
    val actor = system.actorOf(Props(new Player("BoardSuitePlayer1")))

    // board for testing in loc X:
    //       |
    // O  X -O
    // |     |
    // |  |  |
    //-O--O--O
    // |  |  
    board.addCardToStation(new BoardLoc(0, 0), POW, UP, actor)
    board.addCardToStation(new BoardLoc(1, 0), LAB, LEFT, actor)
    board.addCardToStation(new BoardLoc(1, 1), HAB, RIGHT, actor)
    board.addCardToStation(new BoardLoc(-1, 0), POW, UP, actor)
    board.addCardToStation(new BoardLoc(-1, 1), REC, DOWN, actor)

    expect(-1) { board.getMinX }
    expect(1) { board.getMaxX }
    expect(0) { board.getMinY }
    expect(1) { board.getMaxY }

  }

}