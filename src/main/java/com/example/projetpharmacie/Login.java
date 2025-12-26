package com.example.projetpharmacie;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Login {

    private Scene scene;

    public Login(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/projetpharmacie/login.fxml")
            );
            AnchorPane root = loader.load();
            scene = new Scene(root, stage.getWidth(), stage.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Scene getScene() {
        return scene;
    }
}
