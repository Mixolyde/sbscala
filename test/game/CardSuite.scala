package game

import org.scalatest.FunSuite

class CardSuite extends FunSuite {
  test("deck size") {
    expect(Card.DECKSIZE) {Card.sortedDeck.length}
  }
  
  test("deck card counts") {
    expect(3) {Card.sortedDeck.count(card => card == REC)}
    expect(2) {Card.sortedDeck.count(card => card == SAB)}
  }
  
  test("card exits") {
    expect (Set(UP, DOWN, LEFT, RIGHT)) { POW.exits(UP)}
    expect (Set(UP, DOWN, LEFT, RIGHT)) { POW.exits(DOWN)}
    
    expect (Set(UP)) { REC.exits(UP)}
    expect (Set(DOWN)) { DOC.exits(DOWN)}
    expect (Set(LEFT)) { COM.exits(LEFT)}
    
    expect (Set(DOWN, LEFT, RIGHT)) { HAB.exits(UP)}
    expect (Set(UP, LEFT, RIGHT)) { HAB.exits(DOWN)}
    
    expect (Set(UP, DOWN)) { FAC.exits(UP)}
    expect (Set(LEFT, RIGHT)) { FAC.exits(LEFT)}
    expect (Set(RIGHT, LEFT)) { FAC.exits(RIGHT)}
    
    expect (Set(UP, RIGHT)) { LAB.exits(UP)}
    expect (Set(DOWN, LEFT)) { LAB.exits(DOWN)}
    
    // Sabotage card has no exits
    intercept[IllegalArgumentException] {
      SAB.exits(UP)
    }
  }
  
  test("shuffled deck") {
    expect(Card.DECKSIZE) {Card.shuffledDeck.length}
    expect(3) {Card.shuffledDeck.count(card => card == REC)}
    expect(2) {Card.shuffledDeck.count(card => card == SAB)}
    
  }

}