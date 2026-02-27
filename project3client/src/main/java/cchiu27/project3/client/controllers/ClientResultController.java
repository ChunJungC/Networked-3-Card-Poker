package cchiu27.project3.client.controllers;

import cchiu27.project3.client.MainClient;
import cchiu27.project3.client.network.ClientConnection;
import cchiu27.project3.server.model.PokerInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class ClientResultController {

    @FXML
    private Label resultMessageLabel;

    @FXML
    private Label netChangeLabel;

    @FXML
    private Label totalWinningsLabel;

    private MainClient mainApp;
    private ClientConnection connection;

    public void init(MainClient mainApp, ClientConnection connection, PokerInfo result) {
        this.mainApp = mainApp;
        this.connection = connection;

        int net = result.getRoundWinnings();
        String msg = result.getInfoMessage();

        resultMessageLabel.setText(msg != null ? msg : (net >= 0 ? "You won!" : "You lost."));
        netChangeLabel.setText(String.valueOf(net));
        totalWinningsLabel.setText(String.valueOf(result.getTotalWinnings()));
    }

    @FXML
    private void handlePlayAgain() throws Exception{
        try {
            connection.sendNewGame();
            mainApp.showGameScreen();
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExit() {
        mainApp.shutdown();
        System.exit(0);
    }
}
