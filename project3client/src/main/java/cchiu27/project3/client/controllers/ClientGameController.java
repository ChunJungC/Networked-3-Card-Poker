package cchiu27.project3.client.controllers;

import cchiu27.project3.client.MainClient;
import cchiu27.project3.client.network.ClientConnection;
import cchiu27.project3.client.network.PokerInfoListener;
import cchiu27.project3.server.model.Card;
import cchiu27.project3.server.model.PokerInfo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.List;

public class ClientGameController implements PokerInfoListener {

    @FXML
    private Label playerCard1;
    @FXML
    private Label playerCard2;
    @FXML
    private Label playerCard3;

    @FXML
    private Label dealerCard1;
    @FXML
    private Label dealerCard2;
    @FXML
    private Label dealerCard3;

    @FXML
    private TextField anteField;
    @FXML
    private TextField pairPlusField;

    @FXML
    private Label totalWinningsLabel;
    @FXML
    private Label lastResultLabel;
    @FXML
    private Label infoLabel;

    private MainClient mainApp;
    private ClientConnection connection;

    public void init(MainClient mainApp, ClientConnection connection) {
        this.mainApp = mainApp;
        this.connection = connection;
        this.connection.setListener(this);

        hideCards();
        infoLabel.setText("Place your bets to start.");
    }

    private void hideCards() {
        playerCard1.setText("?");
        playerCard2.setText("?");
        playerCard3.setText("?");
        dealerCard1.setText("?");
        dealerCard2.setText("?");
        dealerCard3.setText("?");
    }

    @FXML
    private void handlePlaceBets() {
        int ante, pairPlus;
        try {
            ante = Integer.parseInt(anteField.getText().trim());
            pairPlus = Integer.parseInt(pairPlusField.getText().trim());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Enter numeric values for bets.").showAndWait();
            return;
        }

        try {
            connection.sendPlaceBets(ante, pairPlus);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to send bets: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handlePlay() {
        try {
            connection.sendPlay();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to send PLAY: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handleFold() {
        try {
            connection.sendFold();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to send FOLD: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handleExit() {
        mainApp.shutdown();
        Platform.exit();
    }

    @FXML
    private void handleFreshStart() {
        // Reset totals only on client side; server will continue with same totals.
        // If you want server to reset totals, you'd need an extra command.
        totalWinningsLabel.setText("0");
        lastResultLabel.setText("0");
        hideCards();
        infoLabel.setText("Fresh start. Place bets for a new hand.");
    }

    @FXML
    private void handleNewLook() {
        // Simple "new look": toggle a CSS style class on the root label
        infoLabel.getStyleClass().add("highlight");
    }

    // ----------- PokerInfoListener callbacks -----------

    @Override
    public void onPokerInfo(PokerInfo info) {
        Platform.runLater(() -> {
            switch (info.getCommand()) {
                case UPDATE_STATE -> handleUpdateState(info);
                case GAME_RESULT -> handleGameResult(info);
                default -> { /* ignore others here */ }
            }
        });
    }

    private void handleUpdateState(PokerInfo info) {
        showPlayerHand(info.getPlayerHand());
        // Dealer cards hidden until result
        dealerCard1.setText("?");
        dealerCard2.setText("?");
        dealerCard3.setText("?");
        totalWinningsLabel.setText(String.valueOf(info.getTotalWinnings()));
        infoLabel.setText(info.getInfoMessage());
    }

    private void handleGameResult(PokerInfo info) {
        showPlayerHand(info.getPlayerHand());
        showDealerHand(info.getDealerHand());
        totalWinningsLabel.setText(String.valueOf(info.getTotalWinnings()));
        lastResultLabel.setText(String.valueOf(info.getRoundWinnings()));
        infoLabel.setText(info.getInfoMessage());

        // Move to result screen
        try {
            mainApp.showResultScreen(info);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to show result screen: " + e.getMessage()).showAndWait();
        }
    }

    private void showPlayerHand(List<Card> hand) {
        if (hand == null || hand.size() < 3) return;
        playerCard1.setText(hand.get(0).toString());
        playerCard2.setText(hand.get(1).toString());
        playerCard3.setText(hand.get(2).toString());
    }

    private void showDealerHand(List<Card> hand) {
        if (hand == null || hand.size() < 3) {
            dealerCard1.setText("?");
            dealerCard2.setText("?");
            dealerCard3.setText("?");
            return;
        }
        dealerCard1.setText(hand.get(0).toString());
        dealerCard2.setText(hand.get(1).toString());
        dealerCard3.setText(hand.get(2).toString());
    }

    @Override
    public void onConnectionClosed(String reason) {
        Platform.runLater(() ->
                new Alert(Alert.AlertType.INFORMATION, "Connection closed: " + reason).showAndWait());
    }
}
