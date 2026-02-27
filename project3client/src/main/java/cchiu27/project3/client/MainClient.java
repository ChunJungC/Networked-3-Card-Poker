package cchiu27.project3.client;

import cchiu27.project3.client.controllers.ClientGameController;
import cchiu27.project3.client.controllers.ClientResultController;
import cchiu27.project3.client.controllers.ClientWelcomeController;
import cchiu27.project3.client.network.ClientConnection;
import cchiu27.project3.server.model.PokerInfo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainClient extends Application {

    private Stage primaryStage;
    private ClientConnection connection;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        primaryStage.setTitle("Three Card Poker Client");
        showWelcomeScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // ----------------- Screen navigation -----------------

    public void showWelcomeScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/cchiu27/project3/client/client-welcome-view.fxml"));
        Scene scene = new Scene(loader.load());
        ClientWelcomeController controller = loader.getController();
        controller.init(this);

        primaryStage.setScene(scene);
    }

    public void showGameScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/cchiu27/project3/client/client-game-view.fxml"));
        Scene scene = new Scene(loader.load());
        ClientGameController controller = loader.getController();
        controller.init(this, connection);

        primaryStage.setScene(scene);
    }

    public void showResultScreen(PokerInfo result) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/cchiu27/project3/client/client-result-view.fxml"));
        Scene scene = new Scene(loader.load());
        ClientResultController controller = loader.getController();
        controller.init(this, connection, result);

        primaryStage.setScene(scene);
    }

    // ----------------- Connection management -----------------

    public void connectToServer(String host, int port, String playerName) throws Exception {
        connection = new ClientConnection(host, port);
        connection.connect();
        connection.sendConnect(playerName);
        // After connect, we go to the game screen
        showGameScreen();
    }

    public void shutdown() {
        if (connection != null) {
            connection.close();
        }
    }
}
