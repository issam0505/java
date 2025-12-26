package com.example.projetpharmacie;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SignController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField telephoneField;

    @FXML
    private TextField emailField;

    @FXML
    private Text errorLabel2;

    @FXML
    private VBox signCard;

    // variables
    private String user;
    private String email;
    private String password;
    private String number;

    // ===== ANIMATION (نفس Login 100%) =====
    @FXML
    public void initialize() {

        if (signCard == null) return;

        signCard.setOpacity(0);
        signCard.setTranslateY(90);
        signCard.setScaleX(0.9);
        signCard.setScaleY(0.9);

        FadeTransition fade =
                new FadeTransition(Duration.millis(1300), signCard);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide =
                new TranslateTransition(Duration.millis(1300), signCard);
        slide.setFromY(90);
        slide.setToY(0);

        ScaleTransition scale =
                new ScaleTransition(Duration.millis(1300), signCard);
        scale.setFromX(0.9);
        scale.setFromY(0.9);
        scale.setToX(1);
        scale.setToY(1);

        fade.play();
        slide.play();
        scale.play();
    }


    @FXML
    private void goToStore(ActionEvent event) {
        try {
            user = usernameField.getText();
            email = emailField.getText();
            password = passwordField.getText();
            number = telephoneField.getText();

            errorLabel2.setText("");
            if (email.isEmpty() || password.isEmpty()) {
                errorLabel2.setText("Veuillez remplir tous les champs ❌");
                return;
            }
            else if ( number.length() != 10){
                errorLabel2.setText("votre refuser numero ❌");
                return;
            }
            else if (!email.contains("@gmail.com") ){
                errorLabel2.setText("votre email refuser ❌");
                return;
            }
            else if (email.contains("@gmail.com") && number.length() == 10) {
                try (Connection conn = Database.getConnection()) {
                    if (conn != null) {
                        String query1 = "SELECT * FROM client WHERE email=?";
                        PreparedStatement pst1 = conn.prepareStatement(query1);
                        pst1.setString(1, email);
                        ResultSet rs1 = pst1.executeQuery();
                        if(rs1.next()) {
                            errorLabel2.setText("email: " + email + "existe deja ❌");
                        }
                        else {
                            String query = "INSERT INTO client (nom, email, motpasse, telephone) VALUES (?, ?, ?, ?)";
                            PreparedStatement ps = conn.prepareStatement(query);
                            ps.setString(1, user);
                            ps.setString(2, email);
                            ps.setString(3, password);
                            ps.setString(4, number);
                            ps.executeUpdate();

                            Stage stage = (Stage) ((Node) event.getSource())
                                    .getScene().getWindow();

                            FXMLLoader loader = new FXMLLoader(
                                    getClass().getResource("/com/example/projetpharmacie/store.fxml")
                            );

                            Scene scene = new Scene(loader.load(),
                                    stage.getWidth(),
                                    stage.getHeight());

                            stage.setScene(scene);
                            stage.setMaximized(true);
                            stage.setTitle("SwiftCare/Store");


                        }}else {
                        System.out.println("rien recu ily a un prblm ❌<<<<");
                    }
                }
            } else {
                errorLabel2.setText("Erreur connexion DB");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/projetpharmacie/login.fxml")
            );

            Scene scene = new Scene(loader.load(),
                    stage.getWidth(),
                    stage.getHeight());

            stage.setScene(scene);
            stage.setTitle("SwiftCare/Login");
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
