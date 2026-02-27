package cchiu27.project3.server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class ThreeCardLogic implements Serializable {

    /**
     * Returns an int representing the hand type:
     * 0 = high card, 1 = pair, 2 = flush, 3 = straight,
     * 4 = three-of-a-kind, 5 = straight-flush
     */
    public static int evalHand(ArrayList<Card> hand) {
        hand.sort(Comparator.comparingInt(Card::getRank));
        int r1 = hand.get(0).getRank();
        int r2 = hand.get(1).getRank();
        int r3 = hand.get(2).getRank();

        boolean sameSuit = hand.get(0).getSuit() == hand.get(1).getSuit()
                && hand.get(1).getSuit() == hand.get(2).getSuit();
        boolean straight = (r2 == r1 + 1 && r3 == r2 + 1)
                || (r1 == 2 && r2 == 3 && r3 == 14); // A-2-3

        boolean threeKind = (r1 == r2 && r2 == r3);
        boolean pair = (r1 == r2 || r2 == r3 || r1 == r3);

        if (straight && sameSuit) return 5;
        if (threeKind) return 4;
        if (straight) return 3;
        if (sameSuit) return 2;
        if (pair) return 1;
        return 0;
    }

    /**
     * Pair-Plus payout for given hand and bet.
     * Returns amount WON (0 if no payout).
     */
    public static int evalPPWinnings(ArrayList<Card> hand, int bet) {
        int type = evalHand(hand);
        return switch (type) {
            case 5 -> bet * 40; // straight flush
            case 4 -> bet * 30; // three of a kind
            case 3 -> bet * 6;  // straight
            case 2 -> bet * 3;  // flush
            case 1 -> bet;      // pair
            default -> 0;
        };
    }

    /**
     * Compare hands: >0 player wins, <0 dealer wins, 0 tie.
     */
    public static int compareHands(ArrayList<Card> dealer, ArrayList<Card> player) {
        int dealerType = evalHand(dealer);
        int playerType = evalHand(player);
        if (playerType != dealerType) {
            return Integer.compare(playerType, dealerType);
        }

        // same hand type -> compare high to low
        dealer.sort(Comparator.comparingInt(Card::getRank));
        player.sort(Comparator.comparingInt(Card::getRank));
        for (int i = 2; i >= 0; i--) {
            int d = dealer.get(i).getRank();
            int p = player.get(i).getRank();
            if (p != d) {
                return Integer.compare(p, d);
            }
        }
        return 0;
    }
}
