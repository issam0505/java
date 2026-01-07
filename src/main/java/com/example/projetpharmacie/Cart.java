package com.example.projetpharmacie;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;

public class Cart {
    private final CartManager cartManager = CartManager.getInstance();
    private final UserSession userSession = UserSession.getInstance();
    private final DAO dao = new DAO();
    @FXML
    private TextField cihInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private Label messageLabel;

    @FXML
    private Button submitBtn;

    @FXML
    private Button backBtn;

    @FXML
    private VBox vbox;

    @FXML
    public void initialize() {
        messageLabel.setText("");
        playAnimation();
    }

    // ===== Animation formulaire =====
    private void playAnimation() {
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.6), vbox);
        slide.setFromY(-50);
        slide.setToY(0);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.6), vbox);
        fade.setFromValue(0);
        fade.setToValue(1);

        slide.play();
        fade.play();
    }

    @FXML
    private void submitCart(ActionEvent event) throws InterruptedException {
        String cih = cihInput.getText();
        String password = passwordInput.getText();

        if(cih.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Merci de remplir tous les champs !");
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 16;");
        } else {
            messageLabel.setText("Merci d'avoir choisi notre store !");
            messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 16;");
            double total = cartManager.getTotal();
            LocalDate today = LocalDate.now();
            int idCommande = dao.insertcommande(userSession,today, "cart", total);
            System.out.println("Commande: " + cartManager.getCartItems().size() + " produits, total: " + cartManager.getTotal() + " DH");
            for(CartManager.CartItem item : cartManager.getCartItems()) {
                int idproduit = item.getIdproduit();
                int quantity = item.getQuantity();
                double price = item.getPrice();
                addtocardthred n1 = new addtocardthred(idproduit, quantity,"rupture");
                addtocardthred n2 = new addtocardthred(idCommande,idproduit, quantity,"ajoute");
                n1.start();
                n1.join();
                n2.start();
                n2.join();
            }
            cartManager.getCartItems().clear();
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("store.fxml"));
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(loader.load(), width, height);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
