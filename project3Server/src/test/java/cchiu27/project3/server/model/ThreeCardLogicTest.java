package cchiu27.project3.server.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ThreeCardLogicTest {

    private ArrayList<Card> hand(Card... cards) {
        ArrayList<Card> list = new ArrayList<>();
        for (Card c : cards) list.add(c);
        return list;
    }

    // -------------------------------------------------------------
    // evalHand() tests (using your ranking: 0–5)
    // -------------------------------------------------------------

    @Test
    public void testStraightFlush() {
        ArrayList<Card> h = hand(
                new Card(10, Card.Suit.HEARTS),
                new Card(11, Card.Suit.HEARTS),
                new Card(12, Card.Suit.HEARTS)
        );
        assertEquals(5, ThreeCardLogic.evalHand(h));   // straight flush
    }

    @Test
    public void testThreeOfAKind() {
        ArrayList<Card> h = hand(
                new Card(7, Card.Suit.CLUBS),
                new Card(7, Card.Suit.HEARTS),
                new Card(7, Card.Suit.SPADES)
        );
        assertEquals(4, ThreeCardLogic.evalHand(h));   // trips
    }

    @Test
    public void testStraight() {
        ArrayList<Card> h = hand(
                new Card(4, Card.Suit.SPADES),
                new Card(5, Card.Suit.DIAMONDS),
                new Card(6, Card.Suit.CLUBS)
        );
        assertEquals(3, ThreeCardLogic.evalHand(h));   // straight
    }

    @Test
    public void testFlush() {
        ArrayList<Card> h = hand(
                new Card(3, Card.Suit.HEARTS),
                new Card(7, Card.Suit.HEARTS),
                new Card(12, Card.Suit.HEARTS)
        );
        assertEquals(2, ThreeCardLogic.evalHand(h));   // flush
    }

    @Test
    public void testPair() {
        ArrayList<Card> h = hand(
                new Card(9, Card.Suit.CLUBS),
                new Card(9, Card.Suit.SPADES),
                new Card(4, Card.Suit.DIAMONDS)
        );
        assertEquals(1, ThreeCardLogic.evalHand(h));   // pair
    }

    @Test
    public void testHighCard() {
        ArrayList<Card> h = hand(
                new Card(2, Card.Suit.CLUBS),
                new Card(7, Card.Suit.SPADES),
                new Card(11, Card.Suit.DIAMONDS)
        );
        assertEquals(0, ThreeCardLogic.evalHand(h));   // high card
    }

    // -------------------------------------------------------------
    // Pair Plus Winnings — stays the same
    // -------------------------------------------------------------

    @Test
    public void testPairPlusWin_Trips() {
        ArrayList<Card> h = hand(
                new Card(5, Card.Suit.CLUBS),
                new Card(5, Card.Suit.HEARTS),
                new Card(5, Card.Suit.DIAMONDS)
        );
        assertEquals(150, ThreeCardLogic.evalPPWinnings(h, 5));
    }

    @Test
    public void testPairPlusWin_Flush() {
        ArrayList<Card> h = hand(
                new Card(3, Card.Suit.SPADES),
                new Card(8, Card.Suit.SPADES),
                new Card(12, Card.Suit.SPADES)
        );
        assertEquals(15, ThreeCardLogic.evalPPWinnings(h, 5));
    }

    @Test
    public void testPairPlusWin_None() {
        ArrayList<Card> h = hand(
                new Card(2, Card.Suit.CLUBS),
                new Card(7, Card.Suit.HEARTS),
                new Card(11, Card.Suit.SPADES)
        );
        assertEquals(0, ThreeCardLogic.evalPPWinnings(h, 5));
    }

    // -------------------------------------------------------------
    // compareHands() tests
    // -------------------------------------------------------------

    @Test
    public void testCompare_PlayerWins() {
        ArrayList<Card> dealer = hand(
                new Card(4, Card.Suit.CLUBS),
                new Card(4, Card.Suit.HEARTS),
                new Card(7, Card.Suit.CLUBS)
        );

        ArrayList<Card> player = hand(
                new Card(10, Card.Suit.SPADES),
                new Card(11, Card.Suit.HEARTS),
                new Card(12, Card.Suit.CLUBS)
        );

        assertTrue(ThreeCardLogic.compareHands(dealer, player) > 0);
    }

    @Test
    public void testCompare_DealerWins() {
        ArrayList<Card> dealer = hand(
                new Card(12, Card.Suit.CLUBS),
                new Card(11, Card.Suit.DIAMONDS),
                new Card(10, Card.Suit.CLUBS)
        );

        ArrayList<Card> player = hand(
                new Card(6, Card.Suit.SPADES),
                new Card(6, Card.Suit.HEARTS),
                new Card(2, Card.Suit.SPADES)
        );

        assertTrue(ThreeCardLogic.compareHands(dealer, player) < 0);
    }

    @Test
    public void testCompare_Tie() {
        ArrayList<Card> dealer = hand(
                new Card(8, Card.Suit.CLUBS),
                new Card(8, Card.Suit.HEARTS),
                new Card(3, Card.Suit.DIAMONDS)
        );

        ArrayList<Card> player = hand(
                new Card(8, Card.Suit.SPADES),
                new Card(8, Card.Suit.DIAMONDS),
                new Card(3, Card.Suit.CLUBS)
        );

        assertEquals(0, ThreeCardLogic.compareHands(dealer, player));
    }
}
