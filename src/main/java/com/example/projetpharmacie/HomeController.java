package com.example.projetpharmacie;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class HomeController {

    @FXML
    private void goToLogin(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projetpharmacie/login.fxml"));
            Scene scene = new Scene(loader.load(), stage.getWidth(), stage.getHeight());

            stage.setScene(scene);
            stage.setTitle("SwiftCare/Login");
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
