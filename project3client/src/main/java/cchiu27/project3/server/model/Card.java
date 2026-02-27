package cchiu27.project3.server.model;

import java.io.Serializable;

public class Card implements Serializable {

    public enum Suit { CLUBS, DIAMONDS, HEARTS, SPADES }

    private final int rank; // 2–14 (J=11, Q=12, K=13, A=14)
    private final Suit suit;

    public Card(int rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public int getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        String r = switch (rank) {
            case 11 -> "J";
            case 12 -> "Q";
            case 13 -> "K";
            case 14 -> "A";
            default -> String.valueOf(rank);
        };
        char s = switch (suit) {
            case CLUBS -> 'C';
            case DIAMONDS -> 'D';
            case HEARTS -> 'H';
            case SPADES -> 'S';
        };
        return r + s;
    }
}
