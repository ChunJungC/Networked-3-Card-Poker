package cchiu27.project3.client.controllers;

import cchiu27.project3.client.MainClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class ClientWelcomeController {

    @FXML
    private TextField ipField;

    @FXML
    private TextField portField;

    @FXML
    private TextField nameField;

    private MainClient mainApp;

    public void init(MainClient mainApp) {
        this.mainApp = mainApp;
        ipField.setText("127.0.0.1");
        portField.setText("5555");
    }

    @FXML
    private void handleConnect(ActionEvent event) {
        String host = ipField.getText().trim();
        String portText = portField.getText().trim();
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            name = "Player";
        }

        int port;
        try {
            port = Integer.parseInt(portText);
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid port number.").showAndWait();
            return;
        }

        try {
            mainApp.connectToServer(host, port, name);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to connect: " + e.getMessage()).showAndWait();
        }
    }
}
