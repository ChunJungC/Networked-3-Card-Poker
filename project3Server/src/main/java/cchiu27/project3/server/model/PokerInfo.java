package cchiu27.project3.server.model;

import java.io.Serializable;
import java.util.ArrayList;

public class PokerInfo implements Serializable {

    public enum Command {
        CONNECT,        // client -> server: initial connection, name, etc.
        PLACE_BETS,     // client -> server: ante + pair plus
        PLAY,           // client -> server: player chooses play
        FOLD,           // client -> server: player folds
        UPDATE_STATE,   // server -> client: general update (hands, totals, message)
        GAME_RESULT,    // server -> client: result for win/lose screen
        NEW_GAME,       // client -> server: play another hand
        DISCONNECT      // client -> server: quit
    }

    private Command command;

    // Optional player identity (for logs / future GUI)
    private int playerId;
    private String playerName;

    // Bets for a single hand
    private int anteBet;
    private int pairPlusBet;
    private int playBet;

    // Cards
    private ArrayList<Card> playerHand;
    private ArrayList<Card> dealerHand;

    // Money / winnings
    private int roundWinnings; // net change for this hand
    private int totalWinnings; // running total for this client

    // Helper info for GUI
    private String infoMessage;
    private boolean gameOver;  // true when hand is finished

    public PokerInfo() {}

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getAnteBet() {
        return anteBet;
    }

    public void setAnteBet(int anteBet) {
        this.anteBet = anteBet;
    }

    public int getPairPlusBet() {
        return pairPlusBet;
    }

    public void setPairPlusBet(int pairPlusBet) {
        this.pairPlusBet = pairPlusBet;
    }

    public int getPlayBet() {
        return playBet;
    }

    public void setPlayBet(int playBet) {
        this.playBet = playBet;
    }

    public ArrayList<Card> getPlayerHand() {
        return playerHand;
    }

    public void setPlayerHand(ArrayList<Card> playerHand) {
        this.playerHand = playerHand;
    }

    public ArrayList<Card> getDealerHand() {
        return dealerHand;
    }

    public void setDealerHand(ArrayList<Card> dealerHand) {
        this.dealerHand = dealerHand;
    }

    public int getRoundWinnings() {
        return roundWinnings;
    }

    public void setRoundWinnings(int roundWinnings) {
        this.roundWinnings = roundWinnings;
    }

    public int getTotalWinnings() {
        return totalWinnings;
    }

    public void setTotalWinnings(int totalWinnings) {
        this.totalWinnings = totalWinnings;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public void setInfoMessage(String infoMessage) {
        this.infoMessage = infoMessage;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}
