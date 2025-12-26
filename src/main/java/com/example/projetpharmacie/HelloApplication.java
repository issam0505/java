package com.example.projetpharmacie;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projetpharmacie/home-view.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        stage.setTitle("SwiftCare/Home");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logof2.png")));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
