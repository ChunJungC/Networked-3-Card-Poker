package cchiu27.project3.server;

import cchiu27.project3.server.controllers.ServerIntroController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainServer extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cchiu27/project3/server/server-intro-view.fxml"));
        Scene scene = new Scene(loader.load());

        ServerIntroController controller = loader.getController();
        controller.setStage(stage);

        stage.setTitle("Poker Server");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
