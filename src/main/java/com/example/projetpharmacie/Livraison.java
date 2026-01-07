package com.example.projetpharmacie;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Livraison {

    @FXML
    private Label totalLabel;

    @FXML
    private Label thankLabel;

    @FXML
    private ImageView logoImage;

    private final CartManager cartManager = CartManager.getInstance();

    @FXML
    public void initialize() {
        // Logo
        try {
            Image logo = new Image(getClass().getResourceAsStream("logo.png"));
            logoImage.setImage(logo);
        } catch (Exception e) {
            System.out.println("Logo not found");
        }

        // Total + livraison 30 DH
        double totalWithDelivery = cartManager.getTotal() + 30;
        totalLabel.setText("Total avec livraison : " + totalWithDelivery + " DH");

        // Merci text
        thankLabel.setText("Merci d'avoir choisi notre store !");
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("store.fxml"));
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            // Garde l’ancien taille
            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(loader.load(), width, height);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
