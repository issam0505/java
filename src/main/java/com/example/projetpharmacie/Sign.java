package com.example.projetpharmacie;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Sign{

    private Scene scene;

    public Sign(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("Sign.fxml")
            );
            scene = new Scene(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Scene getScene() {
        return scene;
    }
}
