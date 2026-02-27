package cchiu27.project3.server.controllers;

import cchiu27.project3.server.network.Server;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class ServerMainController {

    @FXML
    private ListView<String> logList;

    private Server server;

    public void setServer(Server server) {
        this.server = server;
        log("Server started successfully.");
    }

    public void log(String message) {
        Platform.runLater(() -> logList.getItems().add(message));
    }

    @FXML
    private void handleStopServer() {
        if (server != null) {
            server.stop();
            log("Server stopped.");
        }
    }
}
