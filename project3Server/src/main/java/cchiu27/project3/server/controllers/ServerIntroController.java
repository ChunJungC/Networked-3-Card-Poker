package cchiu27.project3.server.controllers;

import cchiu27.project3.server.network.Server;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ServerIntroController {

    @FXML
    private TextField portField;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleStartServer(ActionEvent event) {
        try {
            int port = Integer.parseInt(portField.getText().trim());
            Server server = new Server(port);
            server.start();

            // Load main server window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cchiu27/project3/server/server-main-view.fxml"));
            Scene scene = new Scene(loader.load());
            ServerMainController controller = loader.getController();
            controller.setServer(server);

            stage.setScene(scene);
            stage.setTitle("Poker Server Running");

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid port number").showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to start server: " + e.getMessage()).showAndWait();
        }
    }
}
