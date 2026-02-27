package cchiu27.project3.server.network;

import cchiu27.project3.server.model.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Handles one connected client.
 * Receives PokerInfo commands, runs game logic, sends PokerInfo responses.
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final int playerId;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private String playerName = "Player";
    private int totalWinnings = 0;

    // State for current hand
    private Deck deck;
    private ArrayList<Card> playerHand;
    private ArrayList<Card> dealerHand;
    private int anteBet;
    private int pairPlusBet;
    private int playBet;

    public ClientHandler(Socket socket, int playerId) {
        this.socket = socket;
        this.playerId = playerId;
    }

    @Override
    public void run() {
        System.out.println("Handler started for player " + playerId);
        try (socket) {
            // Important: create ObjectOutputStream FIRST, then ObjectInputStream
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            // Main loop
            while (!socket.isClosed()) {
                Object obj = in.readObject();
                if (!(obj instanceof PokerInfo info)) {
                    System.err.println("Received non-PokerInfo object, ignoring.");
                    continue;
                }
                handleCommand(info);
            }
        } catch (EOFException eof) {
            System.out.println("Client " + playerId + " disconnected.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Connection error with player " + playerId + ": " + e.getMessage());
        }
    }

    private void handleCommand(PokerInfo info) throws IOException {
        PokerInfo.Command cmd = info.getCommand();
        if (cmd == null) return;

        switch (cmd) {
            case CONNECT -> handleConnect(info);
            case PLACE_BETS -> handlePlaceBets(info);
            case PLAY -> handlePlay();
            case FOLD -> handleFold();
            case NEW_GAME -> handleNewGame();
            case DISCONNECT -> handleDisconnect();
            default -> System.err.println("Unknown command from player " + playerId + ": " + cmd);
        }
    }

    private void handleConnect(PokerInfo info) throws IOException {
        this.playerName = info.getPlayerName() != null ? info.getPlayerName() : ("Player" + playerId);

        PokerInfo reply = new PokerInfo();
        reply.setCommand(PokerInfo.Command.UPDATE_STATE);
        reply.setPlayerId(playerId);
        reply.setPlayerName(playerName);
        reply.setTotalWinnings(totalWinnings);
        reply.setInfoMessage("Welcome, " + playerName + "! Place your bets to start.");
        send(reply);

        System.out.println("Player " + playerId + " connected as '" + playerName + "'");
    }

    private void handlePlaceBets(PokerInfo info) throws IOException {
        this.anteBet = info.getAnteBet();
        this.pairPlusBet = info.getPairPlusBet();
        this.playBet = 0; // will be set if player chooses PLAY

        // Deal new hand
        deck = new Deck();
        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerHand.add(deck.dealCard());
            dealerHand.add(deck.dealCard());
        }

        PokerInfo reply = new PokerInfo();
        reply.setCommand(PokerInfo.Command.UPDATE_STATE);
        reply.setPlayerId(playerId);
        reply.setPlayerName(playerName);
        reply.setAnteBet(anteBet);
        reply.setPairPlusBet(pairPlusBet);
        reply.setPlayerHand(playerHand);
        reply.setTotalWinnings(totalWinnings);
        reply.setInfoMessage("Bets accepted. Choose PLAY or FOLD.");
        send(reply);

        System.out.println("Player " + playerId + " bets: ante=" + anteBet + " pairPlus=" + pairPlusBet);
    }

    private void handlePlay() throws IOException {
        if (playerHand == null || dealerHand == null) {
            return; // no active hand
        }

        // In standard 3-card poker play bet equals ante
        this.playBet = anteBet;

        int roundWin = 0;

        // Pair Plus winnings (gross payout, not net)
        roundWin += ThreeCardLogic.evalPPWinnings(playerHand, pairPlusBet);

        // Dealer qualification rule:
        // Dealer must have Queen-high or better to qualify.
        boolean dealerQualifies = dealerQualifies();

        // Compare hands for ante + play
        int cmp = ThreeCardLogic.compareHands(dealerHand, playerHand);

        if (!dealerQualifies) {
            // Dealer does NOT qualify:
            //  - Ante wins 1:1 (win A, get A back) -> +2A
            //  - Play pushes (bet returned) -> +P
            roundWin += (anteBet * 2);
            roundWin += playBet;
            System.out.println("Dealer does not qualify for player " + playerId + ".");
        } else {
            if (cmp > 0) {
                // Player wins: gets ante + play (1:1 each) plus original bets back
                // -> +2A + 2P
                roundWin += (anteBet * 2) + (playBet * 2);
                System.out.println("Player " + playerId + " beats dealer.");
            } else if (cmp < 0) {
                // Dealer wins: ante + play lost (we already subtract bets in net calc)
                System.out.println("Dealer beats player " + playerId + ".");
            } else {
                // Tie: ante + play pushed (returned)
                // -> +A + P
                roundWin += anteBet + playBet;
                System.out.println("Player " + playerId + " pushes (tie).");
            }
        }

        // Net change relative to total bets placed
        int net = roundWin - (anteBet + pairPlusBet + playBet);
        totalWinnings += net;

        PokerInfo reply = new PokerInfo();
        reply.setCommand(PokerInfo.Command.GAME_RESULT);
        reply.setPlayerId(playerId);
        reply.setPlayerName(playerName);
        reply.setAnteBet(anteBet);
        reply.setPairPlusBet(pairPlusBet);
        reply.setPlayBet(playBet);
        reply.setPlayerHand(playerHand);
        reply.setDealerHand(dealerHand);
        reply.setRoundWinnings(net);
        reply.setTotalWinnings(totalWinnings);
        reply.setGameOver(true);
        reply.setInfoMessage(buildResultMessage(cmp, net, dealerQualifies));
        send(reply);

        // clear current hand
        playerHand = null;
        dealerHand = null;
    }

    /**
     * Dealer qualifies if:
     *  - hand is pair or better (ThreeCardLogic hand type > 0), OR
     *  - high card is Queen (12) or higher.
     */
    private boolean dealerQualifies() {
        if (dealerHand == null || dealerHand.size() < 3) return false;

        int type = ThreeCardLogic.evalHand(dealerHand);
        if (type > 0) {
            // pair or better always qualifies
            return true;
        }

        int maxRank = dealerHand.get(0).getRank();
        for (Card c : dealerHand) {
            if (c.getRank() > maxRank) {
                maxRank = c.getRank();
            }
        }
        // Queen (12) or higher
        return maxRank >= 12;
    }

    private String buildResultMessage(int cmp, int net, boolean dealerQualifies) {
        if (!dealerQualifies) {
            return "Dealer did not qualify. You win ante; play pushes. Net change: " + net;
        }
        if (cmp > 0) {
            return "You beat the dealer! Net change: " + net;
        } else if (cmp < 0) {
            return "Dealer wins. Net change: " + net;
        } else {
            return "Push (tie). Net change: " + net;
        }
    }

    private void handleFold() throws IOException {
        if (playerHand == null || dealerHand == null) {
            return;
        }

        // Folding: lose ante + pairPlus; no play bet
        int net = -(anteBet + pairPlusBet);
        totalWinnings += net;

        PokerInfo reply = new PokerInfo();
        reply.setCommand(PokerInfo.Command.GAME_RESULT);
        reply.setPlayerId(playerId);
        reply.setPlayerName(playerName);
        reply.setAnteBet(anteBet);
        reply.setPairPlusBet(pairPlusBet);
        reply.setPlayBet(0);
        reply.setPlayerHand(playerHand);
        reply.setDealerHand(null); // dealer cards hidden on fold
        reply.setRoundWinnings(net);
        reply.setTotalWinnings(totalWinnings);
        reply.setGameOver(true);
        reply.setInfoMessage("You folded. Net change: " + net);
        send(reply);

        playerHand = null;
        dealerHand = null;
    }

    private void handleNewGame() throws IOException {
        // Just send a state reset message; client will place new bets.
        PokerInfo reply = new PokerInfo();
        reply.setCommand(PokerInfo.Command.UPDATE_STATE);
        reply.setPlayerId(playerId);
        reply.setPlayerName(playerName);
        reply.setTotalWinnings(totalWinnings);
        reply.setInfoMessage("New game started. Place your bets.");
        send(reply);
    }

    private void handleDisconnect() throws IOException {
        System.out.println("Player " + playerId + " requested disconnect.");
        socket.close();
    }

    private void send(PokerInfo info) throws IOException {
        out.writeObject(info);
        out.flush();
    }
}
